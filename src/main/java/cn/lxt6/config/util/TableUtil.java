package cn.lxt6.config.util;


import cn.lxt6.config.core.annotation.db.Table;
import cn.lxt6.util.StringUtil;

/**
 * @author chenzy
 * @since 2020-04-28
 * 
 */
public class TableUtil {
    private TableUtil(){

    }

    public static<Bean> String getTableName(Class<Bean> beanClass){
        String tableName = null;
        if (beanClass.isAnnotationPresent(Table.class)){
            tableName=(beanClass.getAnnotation(Table.class)).value();
        }
        if (StringUtil.isBlank(tableName)){
            tableName=StringUtil.lowFirst(beanClass.getSimpleName().replace(".class", ""));
        }
        return tableName;
    }
}
