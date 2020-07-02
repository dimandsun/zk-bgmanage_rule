package cn.lxt6.model.util;

/**
 *有些接口请求调用后，不返回具体业务信息。
 * ResponseBO ResBO
 */
public class ResBO implements IVerifyBO {
    private Integer serverId;
    private Boolean success;
    private String random;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }
    @Override
    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }
}
