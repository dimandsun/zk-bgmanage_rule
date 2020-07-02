package cn.lxt6.config.enums;



/**
 * 把请求类型封装成枚举
 * 从哪来请求。 1 小程序，2 App/net/java。
 * ClientTypeEnum
 * @author chenzy
 * @date 2019.12.16
 */
public enum ClientSourceEnum implements IEnum<String> {
    SmallProgram("1","小程序"),APP("2","App/net/java")
    ,Unknown("3","未知请求源")
    ;

    ClientSourceEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    private String code;
    private String msg;

    @Override
    public String toString() {
        return getValue();
    }

    public String getMsg() {
        return this.msg;
    }

    @Override
    public String getValue() {
        return this.code;
    }
}

