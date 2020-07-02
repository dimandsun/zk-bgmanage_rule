package cn.lxt6.secret.util;


import cn.lxt6.config.enums.ClientSourceEnum;
import cn.lxt6.secret.SecretContainer;
import cn.lxt6.secret.enums.SecretTypeEnum;
import cn.lxt6.secret.model.SecretRule;
import cn.lxt6.util.StringUtil;

/**
 * 加密工具类。业务代码需要加密解密功能，统一调用此工具类。不用MD5和AESHelper
 *
 * @author chenzy
 * @date 2019.12.17
 */
public class SecretUtil {
    private SecretUtil() {

    }

    /**
     * md5加密
     * @param secretRule
     * @param data
     * @return
     */
    public static String encryptByMD5(SecretRule secretRule, String data){
        return MD5Secret.md5(secretRule.getMd5() + data);
    }

    /**
     * aes加密
     * @param secretRule
     * @param data
     * @return
     */
    public static String encryptByAES(SecretRule secretRule, String data){
        if (StringUtil.isBlank(secretRule.getAes())){
            return data;
        }
        return AESSecret.encrypt(data, secretRule.getAes());
    }

    /**
     * aes加密
     * @param data
     * @return
     */
    public static String encryptByAES(String data) {
        SecretRule secretRule = SecretContainer.getInstance().getSecretRule(ClientSourceEnum.APP, SecretTypeEnum.Normal,null);
        return AESSecret.encrypt(data, secretRule.getAes());
    }

    /**
     * aes解密
     * @param secretRule
     * @param data
     * @return
     */
    public static String decryptByAES(SecretRule secretRule, String data){
        if (StringUtil.isBlank(secretRule.getAes())){
            return data;
        }
        try {
            return AESSecret.decrypt(data, secretRule.getAes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * aes解密
     * @param data
     * @return
     */
    public static String decryptByAES(String data){
        SecretRule secretRule =SecretContainer.getInstance().getSecretRule(ClientSourceEnum.APP, SecretTypeEnum.Normal,null);
        return decryptByAES(secretRule,data);
    }

}
