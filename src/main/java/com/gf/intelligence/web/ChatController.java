package com.gf.intelligence.web;

import com.gf.intelligence.dto.ClickRequest;
import com.gf.intelligence.service.ClickService;
import com.gf.intelligence.util.ElasticSearchClient;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;

/**
 * @author wushubiao
 * @Title: ChatController
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/8
 */
@Controller
@RequestMapping("/chat")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ClickService clickService;

    /**
     * 问题点击并更新点击次数
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/click",method = RequestMethod.POST)
    public String click(@RequestBody String request){
        try {
            ClickRequest req = JSON.parseObject(request, ClickRequest.class);
            clickService.updateClicks(req.getId());
            return "success";
        }catch (Exception e){
        }
        return "fail";
    }
}
