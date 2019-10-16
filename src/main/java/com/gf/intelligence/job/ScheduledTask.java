package com.gf.intelligence.job;

import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.ClickDto;
import com.gf.intelligence.service.ClickService;
import com.gf.intelligence.util.ElasticSearchClient;
import com.gf.intelligence.util.ExcelReadUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author wushubiao
 * @Title: ScheduledTask
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/16
 */
@Component
public class ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private ElasticSearchClient esClient;

    @Autowired
    private ClickService clickService;

    @Scheduled(cron = "0 0 0 * * ? ")
    public void updateclick() throws Exception{
        List<ClickDto> list = clickService.getAll();
        BulkRequestBuilder bulkRequest = esClient.client.prepareBulk();
        for(ClickDto dto:list){
            UpdateRequest request = esClient.client
                    .prepareUpdate(Constants.GF_INDEX, Constants.GF_TYPE, dto.getId())
                    .setDoc(jsonBuilder().startObject().field("clicks",dto.getClicks())).request();
            bulkRequest.add(request);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new Exception("更新点击量失败");
        }
    }
}
