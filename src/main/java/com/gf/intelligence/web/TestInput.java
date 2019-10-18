package com.gf.intelligence.web;

import com.gf.intelligence.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wushubiao
 * @Title: TestInput
 * @ProjectName gf-intelligence
 * @Description:压测测试搜索接口
 * @date 2019/10/18
 */
@Controller
@RequestMapping("/test")
public class TestInput {
    private static int num;
    @Autowired
    private ChatService chatService;
    @ResponseBody
    @RequestMapping(value="/test",method = RequestMethod.GET)
    private String test(@RequestParam(value="message")String message)
    {
        try {
            String rtnMsg = chatService.input(message);
            return rtnMsg;
        }catch (Exception e){
            System.out.println("出错"+(++num));
        }
        return "error";
    }
}
