package cn.lxt6.secret.model;

import cn.lxt6.config.enums.ClientSourceEnum;
import cn.lxt6.secret.enums.SecretTypeEnum;

/**
 * @author chenzy
 * @since 2020-05-13
 *  加密规则
 */
public class SecretRule {
    private String sid;
   private String aes;
   private String md5;
   private ClientSourceEnum clientSourceEnum;
    private SecretTypeEnum secretTypeEnum;

    public SecretTypeEnum getSecretTypeEnum() {
        return secretTypeEnum;
    }

    public void setSecretTypeEnum(SecretTypeEnum secretTypeEnum) {
        this.secretTypeEnum = secretTypeEnum;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAes() {
        return aes;
    }

    public void setAes(String aes) {
        this.aes = aes;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public ClientSourceEnum getClientSourceEnum() {
        return clientSourceEnum;
    }

    public void setClientSourceEnum(ClientSourceEnum clientSourceEnum) {
        this.clientSourceEnum = clientSourceEnum;
    }
}
