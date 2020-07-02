package cn.lxt6.bgmanage_rule.model.enums;

import cn.lxt6.config.enums.IEnum;

/**
 * @author chenzy
 * @since 2020-06-30
 * 1 创建信息，2为修改，3为删除s
 */
public enum OperationTypeEnum implements IEnum<Integer> {
    Insert(1,"新增"),Update(2,"修改"),Delete(3,"删除");
    private Integer id;
    private String msg;

    OperationTypeEnum(Integer id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
