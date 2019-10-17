package com.gf.intelligence.web;

import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.UserDto;
import com.gf.intelligence.service.LoginService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author wushubiao
 * @Title: LoginController
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/14
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    /**
     * 登录
     * @param username
     * @param password
     * @param model
     * @return
     */
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public String login(@RequestParam(value = "username") String username, @RequestParam(value = "password")
                        String password, Model model){
        try {
            UserDto dto = loginService.findByUser(username).get(0);
            model.addAttribute("username",username);
            return checkUser(password,dto)?"socket": "error";
        }catch (Exception e){
            logger.error("登录异常{}",e);
        }
        return "error";
    }

    /**
     * 验证密码
     * @param password
     * @param dto
     * @return
     */
    private Boolean checkUser(String password,UserDto dto){
        String pass = DigestUtils.sha1Hex(DigestUtils.sha1Hex(password+ Constants.GF_SALT));
        return pass.equals(dto.getHashpassword());
    }

    /**
     * 生成加密串main函数
     * @param args
     */
    public static void main(String[] args) {
        String hash_password = DigestUtils.sha1Hex(DigestUtils.sha1Hex("admin123"+ Constants.GF_SALT));
        System.out.println(hash_password);
    }
}
