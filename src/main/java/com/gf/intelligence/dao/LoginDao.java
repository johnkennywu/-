package com.gf.intelligence.dao;

import com.gf.intelligence.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushubiao
 * @Title: LoginDao
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/14
 */
@Repository
public interface LoginDao  extends JpaRepository<UserDto,String> {
    List<UserDto> findByUsername(String var);
}
