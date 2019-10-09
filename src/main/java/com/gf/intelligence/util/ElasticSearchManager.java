package com.gf.intelligence.util;

import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.service.InitMapperService;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wushubiao
 * @Title: ElasticSearchManager
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/9
 */
@Component
public class ElasticSearchManager implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchManager.class);

    @Autowired
    private ElasticSearchClient esClient;
    @Autowired
    private InitMapperService initMapperService;
    @Override
    public void afterPropertiesSet() throws Exception {
        if (!indexExists(Constants.GF_INDEX)) {
            logger.info("index for travel guide not fount,starting to create...");
            initMapperService.createGFMapping(esClient.client,);
        }
    }

    private boolean indexExists(String index) {
        try {
            IndicesExistsRequest request = Requests.indicesExistsRequest(index);
            boolean exists = esClient.client.admin().indices().exists(request).get().isExists();
            return exists;
        } catch (Exception e) {
            logger.error("查询索引是否存在出错", e);
        }
        return false;
    }
}
