package com.gf.intelligence.dao;

import com.gf.intelligence.dto.ClickDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author wushubiao
 * @Title: ClickDao
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/16
 */
@Repository
public interface ClickDao  extends JpaRepository<ClickDto,String> {
    @Query(nativeQuery=true, value = "select * from clicks")
    List<ClickDto> getAll();

    @Transactional
    @Modifying
    @Query(nativeQuery=true, value = "update clicks set clicks = clicks+1 where id = ?1")
    void updateClicks(String id);
}
