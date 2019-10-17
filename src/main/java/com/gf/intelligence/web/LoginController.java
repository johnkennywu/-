package com.gf.intelligence.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.UserDto;
import com.gf.intelligence.dto.UserRequest;
import com.gf.intelligence.service.LoginService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wushubiao
 * @Title: LoginController
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/14
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public String login(@RequestBody String request){
        JSONObject object = new JSONObject();
        try {
            UserRequest req = JSON.parseObject(request, UserRequest.class);
            UserDto dto = loginService.findByUser(req.getUsername()).get(0);
            object.put("success",checkUser(req,dto));
        }catch (Exception e){
            logger.error("登录异常{}",e);
        }
        return object.toJSONString();
    }
    private Boolean checkUser(UserRequest req,UserDto dto){
        String pass = DigestUtils.sha1Hex(DigestUtils.sha1Hex(req.getPassword()+ Constants.GF_SALT));
        return pass.equals(dto.getHashpassword());
    }

    public static void main(String[] args) {
        String hash_password
    }
}
