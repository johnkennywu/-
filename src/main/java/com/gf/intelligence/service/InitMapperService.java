package com.gf.intelligence.service;

import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.ClickDto;
import com.gf.intelligence.dto.Question;
import com.gf.intelligence.dto.UserDto;
import com.gf.intelligence.util.ExcelReadUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.assertj.core.internal.Bytes;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author wushubiao
 * @Title: InitMapperService
 * @ProjectName gf-intelligence
 * @Description: 创建索引，导入数据
 * @date 2019/10/9
 */
@Component
public class InitMapperService {
    private static Logger logger = LoggerFactory.getLogger(InitMapperService.class);

    @Autowired
    private ClickService clickService;

    @Autowired
    private LoginService loginService;

    @Async
    public Future<String> createGFMapping(TransportClient client, String index, String type){
        logger.info("开始创建索引");
        try{
            //keywords设置逗号分词，gf索引的分片数目设为1，备份0
            XContentBuilder settingsBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("analysis")
                    .startObject("analyzer")
                    .startObject("comma")
                    .field("type","pattern")
                    .field("pattern",",|，")
                    .endObject()
                    .endObject()
                    .endObject()
                    .field("index.number_of_shards", 1)
                    .field("index.number_of_replicas", 0)
                    .endObject();
            XContentBuilder properties = jsonBuilder()
                    .startObject()
                    .startObject("data")
                    .startObject("properties")
                    .startObject("question").field("type", "keyword").endObject()
                    .startObject("answer").field("type", "keyword").endObject()
                    .startObject("keywords").field("type","text").field("analyzer","comma")
                    .endObject()
                    .startObject("clicks").field("type","long").endObject()
//                    .startObject("joiner").field("type","join")
//                    .startObject("relations").field("parent","child").endObject().endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            //创建index,指定别名alias
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.settings(settingsBuilder);
            client.admin().indices().create(request).get();
            //创建mapping
            PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(properties);
            client.admin().indices().putMapping(mapping).get();

            importData(client, index, type);
        }catch (Exception e){
            logger.error("创建索引失败",e);
        }
        return new AsyncResult<String>("ok");
    }

    //首次导入文档数据到ES，并将文档点击数初始化到数据库中
    private void importData(TransportClient client, String index, String type)
            throws Exception{
        List<String[]> list = ExcelReadUtil.readExcel(Constants.GF_DATA_PATH);
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        List<ClickDto> dtoList = new ArrayList<ClickDto>();
        long len = 0;
        for(String[] str:list) {
            String uuid = UUID.randomUUID().toString();
            IndexRequest request = client
                    .prepareIndex(index, type, uuid)
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("question", str[0])
                            .field("answer", str[1])
                            .field("keywords", str[2])
                            .field("clicks",0)
                            .endObject()
                    )
                    .request();
            bulkRequest.add(request);
            ClickDto dto = new ClickDto();
            dto.setId(uuid);
            dto.setClicks(0);
            dtoList.add(dto);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new Exception("导入数据失败");
        }
        clickService.batchSave(dtoList);

        String hash_password = DigestUtils.sha1Hex(DigestUtils.sha1Hex("admin123"+ Constants.GF_SALT));
        UserDto dto1 =  new UserDto();
        dto1.setUsername("admin");
        dto1.setHashpassword(hash_password);
        UserDto dto2 = new UserDto();
        dto2.setUsername("admin123");
        dto2.setHashpassword(hash_password);
        loginService.save(dto1);
        loginService.save(dto2);
    }
}
