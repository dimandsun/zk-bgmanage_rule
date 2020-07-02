package cn.lxt6.config.core.enums;

import cn.lxt6.config.enums.IEnum;

/**
 * @author chenzy
 *
 * @since 2020-04-03
 */
public enum AspectTypeEnum implements IEnum<Integer> {
    Before(1,"前置切面"),After(2,"后置切面"),Around(3,"环绕");

    AspectTypeEnum(Integer id, String msg) {
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
