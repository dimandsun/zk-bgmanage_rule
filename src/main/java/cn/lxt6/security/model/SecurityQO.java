package cn.lxt6.security.model;

import cn.lxt6.config.core.annotation.VarExplain;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author chenzy
 *
 * @since 2020-04-02
 */
public class SecurityQO {
    @VarExplain(value = "随机数")
    private String random;

    @VarExplain("维护分钟数")
    @JsonProperty("usetime")
    private String useTime;

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }
}
