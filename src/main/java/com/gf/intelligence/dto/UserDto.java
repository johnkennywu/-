package com.gf.intelligence.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wushubiao
 * @Title: UserDto
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/14
 */
@Entity
@Table(name="user")
public class UserDto {
    @Id
    @Column(name="user_name")
    private String username;

    @Column(name="hash_password")
    private String hashpassword;

    @Column(name="salt")
    private String salt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashpassword() {
        return hashpassword;
    }

    public void setHashpassword(String hashpassword) {
        this.hashpassword = hashpassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
