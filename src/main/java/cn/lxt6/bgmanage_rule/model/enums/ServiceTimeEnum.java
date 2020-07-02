package cn.lxt6.bgmanage_rule.model.enums;

import cn.lxt6.config.enums.IEnum;

/**
 * @author chenzy
 * @since 2020-06-30
 * 运营时间标志 0 为全天开放,1 为自定义时间
 */
public enum ServiceTimeEnum implements IEnum<Integer> {
    AllDay(0,"全天开放"),CustomTime(1,"自定义时间");

    ServiceTimeEnum(Integer id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    private Integer id;
    private String msg;
    @Override
    public Integer getValue() {
        return id;
    }
}
