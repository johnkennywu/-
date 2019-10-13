package com.gf.intelligence.web;

import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.ChatRequest;
import com.gf.intelligence.dto.ClickRequest;
import com.gf.intelligence.dto.Question;
import com.gf.intelligence.util.ElasticSearchClient;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wushubiao
 * @Title: ChatController
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/8
 */
@RestController
@RequestMapping("/chat")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private ElasticSearchClient esClient;

    private final static int SHOW_NUMBER = 5;

    @RequestMapping(value="/input",method = RequestMethod.GET)
    public String input(@RequestParam(value="text") String text){
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
            SearchResponse searchResponse = esClient.client.prepareSearch(Constants.GF_INDEX,Constants.GF_INDEX_CLICK).
                    setTypes( Constants.GF_TYPE, Constants.GF_TYPE_CLICK).setQuery(matchQuery).get();
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

    @RequestMapping(value="/click",method = RequestMethod.POST)
    public void click(@RequestBody String request){
        try {
            ClickRequest req = JSON.parseObject(request, ClickRequest.class);
            UpdateResponse rsp = esClient.client.prepareUpdate("gf", "data", req.getId()).setDoc(XContentFactory.
                    jsonBuilder().startObject().field("", "").endObject()).execute().get();
        }catch (Exception e){
        }
    }
}
