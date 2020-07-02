package cn.lxt6.secret.enums;


import cn.lxt6.config.enums.IEnum;
import cn.lxt6.util.StringUtil;

/**
 * @author chenzy
 * @since 2020-05-13
 *
 */
public enum SecretTypeEnum implements IEnum<Integer> {
    Self(1, "内部加密规则"), License(2, "授权服务器加密规则")
    ,Normal(3,"通用加密规则")
    ,NB(4,"nb服务器加密规则"),Custom(5,"自定义加密规则")
    ;

    SecretTypeEnum(Integer id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public static SecretTypeEnum getEnum(String msg) {
        if (StringUtil.isBlank(msg)) {
            return null;
        }
        for (SecretTypeEnum typeEnum : SecretTypeEnum.values()) {
            if (typeEnum.msg.equals(msg)) {
                return typeEnum;
            }
        }
        return Custom;
    }

    private Integer id;
    private String msg;

    @Override
    public Integer getValue() {
        return id;
    }
}
