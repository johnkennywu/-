package com.gf.intelligence.service;

import com.gf.intelligence.dto.Question;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
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
import java.util.concurrent.Future;

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
    public Future<String> createGFMapping(TransportClient client, String index){
        logger.info("开始创建索引");
        try{
            XContentBuilder properties = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties")
//                    .startObject("id").field("type", "keyword").endObject()
                    .startObject("question").field("type", "keyword").endObject()
                    .startObject("answer").field("type", "keyword").endObject()
                    .startObject("clicks").field("type", "keyword").endObject()
                    .endObject()
                    .endObject();
            //创建index,指定别名alias
            CreateIndexRequest request = new CreateIndexRequest(index);
            client.admin().indices().create(request).get();
            //创建mapping
            PutMappingRequest mapping = Requests.putMappingRequest(index).source(properties);
            client.admin().indices().putMapping(mapping).get();
        }catch (Exception e){
            logger.error("创建索引失败",e);
        }
        return new AsyncResult<String>("ok");
    }
}
