package com.gf.intelligence.service;

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
                    .startObject("cmsid").field("type", "keyword").endObject()
                    .startObject("city").field("type", "keyword").endObject()
                    .startObject("continent").field("type", "keyword").endObject()
                    .startObject("country").field("type", "keyword").endObject()
                    .startObject("title").field("type", "text").field("analyzer", "index_ansj").field("search_analyzer", "query_ansj").endObject()
                    .startObject("summary").field("type", "text").field("analyzer", "index_ansj").field("search_analyzer", "query_ansj").endObject()
                    .startObject("subclassification").field("type", "text").field("analyzer", "index_ansj").field("search_analyzer", "query_ansj").endObject()
                    .startObject("tags").field("type", "text").field("analyzer", "index_ansj").field("search_analyzer", "query_ansj").endObject()
                    .startObject("classificationId").field("type", "byte").endObject()
                    .startObject("url").field("type", "keyword").endObject()
                    .startObject("@timestamp").field("type", "date").endObject() //logstash需要此字段
                    .startObject("@version").field("type", "keyword").endObject() //logstash需要此字段
                    .endObject()
                    .endObject();
            //创建index,指定别名alias
            CreateIndexRequest request = new CreateIndexRequest((tmp?indice.getTmpIndex():indice.getIndex())).alias(new Alias(indice.getAlias())).settings(settingsBuilder);
            client.admin().indices().create(request).get();
            //创建mapping
            PutMappingRequest mapping = Requests.putMappingRequest((tmp?indice.getTmpIndex():indice.getIndex())).type(indice.getType()).source(properties);
            client.admin().indices().putMapping(mapping).get();
        }catch (Exception e){
            logger.error("创建索引失败",e);
        }
        return new AsyncResult<>("ok");
    }
}
