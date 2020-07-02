package cn.lxt6.bgmanage_rule.dao;

import cn.lxt6.config.core.annotation.bean.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author chenzy
 * @since 2020-06-30
 */
@Dao
public interface IBaseManagerDao {

    Integer verifyUniqueUpdate(@Param("id") Long id, @Param("uniqueSystem")String uniqueSystem);
    Map<String,Object> selectById(@Param("id") Long id);
    Integer verifyUniqueInsert(@Param("uniqueSystem")String uniqueSystem);
}
