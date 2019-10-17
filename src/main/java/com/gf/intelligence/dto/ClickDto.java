package com.gf.intelligence.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wushubiao
 * @Title: ClickDto
 * @ProjectName gf-intelligence
 * @Description:文档对应的点击类
 * @date 2019/10/16
 */
@Entity
@Table(name="clicks")
public class ClickDto {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="clicks")
    private long clicks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }
}
