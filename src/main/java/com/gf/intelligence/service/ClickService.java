package com.gf.intelligence.service;

import com.gf.intelligence.dao.ClickDao;
import com.gf.intelligence.dto.ClickDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushubiao
 * @Title: ClickService
 * @ProjectName gf-intelligence
 * @Description: 点击service
 * @date 2019/10/16
 */
@Component
public class ClickService {
    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private ClickDao clickDao;
    public void updateClicks(String id){
        try {
            clickDao.updateClicks(id);
        } catch (Exception e) {
            logger.error("更新点击量异常{}",e);
        }
    }

    public List<ClickDto> getAll(){
        try{
            return clickDao.getAll();
        }catch(Exception e){
            logger.error("获取点击全量异常{}",e);
            return new ArrayList<ClickDto>();
        }
    }

    /**
     * 批量保存
     * @param list
     */
    public void batchSave(List<ClickDto> list){
        try {
            //批量存储的集合
            List<ClickDto> data = new ArrayList<ClickDto>();

            //批量存储
            for (ClickDto dto : list) {
                if (data.size() == 300) {
                    clickDao.save(data);
                    data.clear();
                }
                data.add(dto);
            }
            if (!data.isEmpty()) {
                clickDao.save(data);
            }
        }catch (Exception e){
            logger.error("批量插入异常{}",e);
        }
    }
}
