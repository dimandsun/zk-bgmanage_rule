package cn.lxt6.config.db;

import cn.lxt6.config.enums.IEnum;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * 数据源枚举
 * @author chenzy
 * @date 2019.12.25
 */
public enum DataSourceEnum implements IEnum<String> {
    DEFAULT("default",null),;
    private String beanName;
    private DataSource dataSource;

    public static DataSourceEnum getEnum(String beanName) {
        for (DataSourceEnum enumObj : DataSourceEnum.values()) {
            if (enumObj.beanName.equals(beanName)) {
                return enumObj;
            }
        }
        return null;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public static DataSourceEnum setDataSource(String beanName, DataSource dataSource) {
        for (DataSourceEnum enumObj : DataSourceEnum.values()) {
            if (enumObj.beanName.equals(beanName)) {
                enumObj.dataSource=dataSource;
                return enumObj;
            }
        }
        return null;
    }
    public static void clear(){
        System.out.println("DataSource--close+++++++++++++++++++++++++++++++");
        for (DataSourceEnum enumObj : DataSourceEnum.values()) {
            if (enumObj.dataSource!=null&&enumObj.dataSource instanceof DruidDataSource){
                DruidDataSource dataSource= (DruidDataSource) enumObj.dataSource;
                dataSource.close();
            }
        }
    }
    DataSourceEnum(String beanName, DataSource dataSource) {
        this.beanName = beanName;
        this.dataSource = dataSource;
    }

    @Override
    public String getValue() {
        return beanName;
    }
}
