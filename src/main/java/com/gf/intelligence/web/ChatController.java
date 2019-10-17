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

    @RequestMapping("/websocket/{name}")
    public String webSocket(@PathVariable String name, Model model){
        try{
            logger.info("跳转到websocket的页面上");
                //通过Model进行对象数据的传递
            model.addAttribute("username",name);
            return "socket";
        }
        catch (Exception e){
            logger.info("跳转到websocket的页面上发生异常，异常信息是："+e.getMessage());
            return "error";
        }
    }

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
