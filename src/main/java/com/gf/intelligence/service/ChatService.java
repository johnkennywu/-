package com.gf.intelligence.service;

import com.alibaba.fastjson.JSON;
import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.Question;
import com.gf.intelligence.util.ElasticSearchClient;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
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
 * @Description:
 * @date 2019/10/14
 */
@Component
public class ChatService {
    private static Logger logger = LoggerFactory.getLogger(ChatService.class);
    @Autowired
    private ElasticSearchClient esClient;

    private final static int SHOW_NUMBER = 5;

    public String input(String text){
        List<Question> questions = new ArrayList<Question>();
        List<String> keywords = new ArrayList<String>();
        try {
//        ChatRequest req = JSON.parseObject(request,ChatRequest.class);
            List<Term> terms = ToAnalysis.parse(text).getTerms();
            for (Term t : terms) {
                keywords.add(t.getName());
            }
            String keys = StringUtils.join(keywords, ",");
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("keywords", keys);
            SearchResponse searchResponse = esClient.client.prepareSearch(Constants.GF_INDEX).
                    setTypes( Constants.GF_TYPE).setQuery(matchQuery).addSort("_score", SortOrder.DESC)
                    .addSort("clicks",SortOrder.DESC).get();
            SearchHits hit = searchResponse.getHits();
            long totalHits = hit.getTotalHits();
            if (totalHits < 1) {
                return null;
            }
            SearchHit[] hits = hit.getHits();

            for (int i = 0; i < Math.min(SHOW_NUMBER,hits.length); i++) {
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
