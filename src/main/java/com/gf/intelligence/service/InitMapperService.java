package com.gf.intelligence.service;

import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.Question;
import com.gf.intelligence.util.ExcelReadUtil;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author wushubiao
 * @Title: InitMapperService
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/9
 */
@Component
public class InitMapperService {
    private static Logger logger = LoggerFactory.getLogger(InitMapperService.class);
    @Async
    public Future<String> createGFMapping(TransportClient client, String index, String type, String index_click,
                                          String type_click){
        logger.info("开始创建索引");
        try{
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
                    .startObject("id").field("type", "keyword").endObject()
                    .startObject("question").field("type", "keyword").endObject()
                    .startObject("answer").field("type", "keyword").endObject()
                    .startObject("keywords").field("type","text").field("analyzer","comma").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            XContentBuilder properties_click = jsonBuilder()
                    .startObject()
                    .startObject("click")
                    .startObject("_parent")
                    .field("type","data")
                    .endObject()
                    .startObject("properties")
                    .startObject("id").field("type", "keyword").endObject()
                    .startObject("clicks").field("type", "long").endObject()
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

            CreateIndexRequest request_click = new CreateIndexRequest(index_click);
            request_click.settings(settingsBuilder);
            client.admin().indices().create(request_click).get();
            //创建mapping
            PutMappingRequest mapping_click = Requests.putMappingRequest(index_click).type(type_click)
                    .source(properties_click);
            client.admin().indices().putMapping(mapping_click).get();
            importData(client, index, type, index_click, type_click);
        }catch (Exception e){
            logger.error("创建索引失败",e);
        }
        return new AsyncResult<String>("ok");
    }

    private void importData(TransportClient client, String index, String type, String index_click, String type_click)
            throws Exception{
        List<String[]> list = ExcelReadUtil.readExcel(Constants.GF_DATA_PATH);
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        BulkRequestBuilder bulkRequest_click = client.prepareBulk();
        long len = 0;
        for(String[] str:list) {
            String uuid = UUID.randomUUID().toString();
            IndexRequest request = client
                    .prepareIndex(index, type, uuid)
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("id", uuid)
                            .field("question", str[0])
                            .field("answer", str[1])
                            .field("keywords", str[2])
                            .endObject()
                    )
                    .request();
            bulkRequest.add(request);
            IndexRequest request_click = client
                    .prepareIndex(index_click, type_click, uuid)
                    .setRouting(uuid)
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("id", uuid)
                            .field("clicks", ++len)
                            .endObject()
                    )
                    .request();
            bulkRequest_click.add(request_click);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        BulkResponse bulkResponse_click = bulkRequest_click.execute().actionGet();
        if (bulkResponse.hasFailures()||bulkResponse_click.hasFailures()) {
            throw new Exception("导入数据失败");
        }
    }
}
