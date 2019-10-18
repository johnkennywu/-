package com.gf.intelligence.service;

import com.gf.intelligence.dao.LoginDao;
import com.gf.intelligence.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushubiao
 * @Title: LoginService
 * @ProjectName gf-intelligence
 * @Description:登录service
 * @date 2019/10/14
 */
@Component
public class LoginService {
    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private LoginDao loginDao;
    public List<UserDto> findByUser(String username){
        try {
            return loginDao.findByUsername(username);
        } catch (Exception var4) {
            return new ArrayList();
        }
    }

    public void save(UserDto dto){
        try{
            loginDao.save(dto);
        }catch (Exception e){

        }
    }
}
