package cn.lxt6.bgmanage_rule.dao;

import cn.lxt6.config.core.annotation.bean.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author chenzy
 * @since 2020-06-30
 * 项目的通用sql放在这里
 */
@Dao("bgManageDao")
public interface IBGManageDao {
    Integer createTable(@Param("procedureName")String tableName,@Param("parMap") Map<String, Object> map);
    Integer insert(@Param("parMap") Map<String, Object> parMap, @Param("tableName") String tableName);
    Integer update(@Param("setPar") Map<String, Object> setPar, @Param("wherePar") Map<String, Object> wherePar, @Param("tableName") String tableName);
    Integer delete(@Param("wherePar") Map<String, Object> wherePar, @Param("tableName") String tableName);
}
