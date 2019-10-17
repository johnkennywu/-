package com.gf.intelligence.service;

import com.gf.intelligence.dto.Question;
import com.gf.intelligence.util.BeanMapper;
import com.gf.intelligence.util.ElasticSearchClient;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wushubiao
 * @Title: GFDataService
 * @ProjectName gf-intelligence
 * @Description:批量导入文档
 * @date 2019/10/10
 */
@Component
public class GFDataService {
    private static Logger logger = LoggerFactory.getLogger(GFDataService.class);

    public void bulkIndex(String index, String type, ElasticSearchClient esClient, List<Question> list) {
        BulkRequestBuilder bulkRequest = esClient.client.prepareBulk();
        try {
            for (Question t : list) {
                IndexRequestBuilder prepareIndex = esClient.client.prepareIndex(index, type, t.getId())
                        .setSource(BeanMapper.beansToEsMap(t));
                bulkRequest.add(prepareIndex);
            }

            BulkResponse bulkResponse = bulkRequest.execute().get();
            if (bulkResponse.hasFailures()) {
                logger.error("批量导入出错：{}", bulkResponse.buildFailureMessage());
                throw new Exception("批量导入出错");
            }else {
                logger.info("bulk index success!");
            }
        } catch (Exception e) {
            logger.error("批量导入出错：" + e.getMessage(), e);
        }
    }

}
