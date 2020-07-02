package cn.lxt6.config.core.enums;

import cn.lxt6.config.enums.IEnum;
import cn.lxt6.util.StringUtil;

/**
 * @author chenzy
 * @since 2020-05-11
 *  项目环境：开发、生产、测试、默认
 */
public enum ActiveEnum implements IEnum<Integer> {

    Dev(1,"dev"),Pro(2,"pro"),Test(3,"test"),Default(4,"dev")
    /*开发者本地测试，使用正式服务器的配置文件*/
    ,Local(5,"local")
    ;

    ActiveEnum(Integer id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    private Integer id;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public static ActiveEnum getEnum(String active) {
        if (StringUtil.isBlank(active)){
            return null;
        }
        for (ActiveEnum activeEnum : ActiveEnum.values()) {
            if (active.equals(activeEnum.msg)){
                return activeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return id;
    }
}
