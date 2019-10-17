package com.gf.intelligence.service;

import com.alibaba.fastjson.JSON;
import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.Question;
import com.gf.intelligence.util.ElasticSearchClient;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wushubiao
 * @Title: ChatService
 * @ProjectName gf-intelligence
 * @Description: 搜索类，根据输入分词并查询，返回结果
 * @date 2019/10/14
 */
@Component
public class ChatService {
    private static Logger logger = LoggerFactory.getLogger(ChatService.class);
    @Autowired
    private ElasticSearchClient esClient;

    /**
     * 根据输入分词并查询，返回结果
     * @param text
     * @return
     */
    public String input(String text){
        List<Question> questions = new ArrayList<Question>();
        List<String> keywords = new ArrayList<String>();
        try {
            //***分词，新建default.dic，配置关键词，使得再词典中配置词不会被分词
            List<Term> terms = ToAnalysis.parse(text).getTerms();
            for (Term t : terms) {
                keywords.add(t.getName());
            }
            //***should查询及Constant_score打分,忽略IDF/TF,使得结果只跟关键词命中次数相关
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            for(String key:keywords) {
                boolQuery = boolQuery.should(QueryBuilders.constantScoreQuery(QueryBuilders.matchQuery("keywords", key)));
            }
            //设置_score主排序，点击量次排序
            SearchResponse searchResponse = esClient.client.prepareSearch(Constants.GF_INDEX).
                    setTypes( Constants.GF_TYPE).setQuery(boolQuery).addSort("_score", SortOrder.DESC)
                    .addSort("clicks",SortOrder.DESC).get();
            SearchHits hit = searchResponse.getHits();
            long totalHits = hit.getTotalHits();
            if (totalHits < 1) {
                return null;
            }
            SearchHit[] hits = hit.getHits();

            for (int i = 0; i < Math.min(Constants.SHOW_NUMBER,hits.length); i++) {
                Map<String, Object> docMap = hits[i].getSourceAsMap();
                Question ques = new Question();
                ques.setId(hits[i].getId());
                ques.setQuestion((String) docMap.get("question"));
                ques.setAnswer((String) docMap.get("answer"));
                questions.add(ques);
            }
        }catch (Exception e){
            logger.error("搜索异常{}",e);
        }
        return JSON.toJSONString(questions);
    }
}
