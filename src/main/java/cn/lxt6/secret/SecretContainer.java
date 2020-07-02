package cn.lxt6.secret;


import cn.lxt6.config.core.model.ProjectInfo;
import cn.lxt6.config.enums.ClientSourceEnum;
import cn.lxt6.model.util.MyMap;
import cn.lxt6.secret.enums.SecretTypeEnum;
import cn.lxt6.secret.model.SecretRule;
import cn.lxt6.util.FileUtil;
import cn.lxt6.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * @author chenzy
 *  加密规则容器，读取配置文件
 * @since 2020-05-13
 */
public class SecretContainer {

    private MyMap<ClientSourceEnum, MyMap<SecretTypeEnum, MyMap<String, SecretRule>>> secretMap = new MyMap<>();
    private static SecretContainer instance = new SecretContainer();

    public static SecretContainer getInstance() {
        return instance;
    }

    private SecretContainer() {
        reloadSecretConst();
    }

    public SecretRule getSecretRule(ClientSourceEnum clientSourceEnum, SecretTypeEnum secretTypeEnum, String sid) {
        if (secretMap.size() < 1) {
            reloadSecretConst();
        }
        if (secretTypeEnum == null) {
            secretTypeEnum = SecretTypeEnum.Normal;
        }
        Map<String, SecretRule> secretRuleMap = secretMap.get(clientSourceEnum).get(secretTypeEnum);
        if (secretRuleMap == null) {
            return null;
        }
        if (StringUtil.isBlank(sid)) {
            if (secretRuleMap == null || secretRuleMap.isEmpty()) {
                return null;
            }
            //有多个同样的加密规则，则默认取第一个

            for (SecretRule secretRule:secretRuleMap.values()) {
                return secretRule;
            }
            return null;//不会走这里
        } else {
            return secretRuleMap.get(sid);
        }
    }

    public void reloadSecretConst() {
        MyMap<String,ArrayList<Map<String, Object>>> secretListMap = FileUtil.readConfigFileByYML("config/secret-" + ProjectInfo.getInstance().getActive().getMsg() + ".yml");
        List<Map<String, Object>> secretList = secretListMap.get("secrets");

        for (Map<String, Object> temp : secretList) {
            String sid = StringUtil.getStr(temp.get("sid"), null);
            String name = StringUtil.getStr(temp.get("name"), null);
            if (StringUtil.isBlankOr(sid,name)){
                continue;
            }
            SecretRule appSecretRule = createSecretRule(ClientSourceEnum.APP, sid, name, StringUtil.getStr(temp.get("app_secret_aes"), null), StringUtil.getStr(temp.get("app_secret_md5"), null));
            SecretRule smSecretRule = createSecretRule(ClientSourceEnum.SmallProgram, sid, name, StringUtil.getStr(temp.get("sm_secret_aes"), null), StringUtil.getStr(temp.get("sm_secret_md5"), null));


            putMap(secretMap.get(ClientSourceEnum.APP,new MyMap<>()), appSecretRule);
            putMap(secretMap.get(ClientSourceEnum.SmallProgram,new MyMap<>()), smSecretRule);
        }
    }

    private void putMap(MyMap<SecretTypeEnum, MyMap<String, SecretRule>> clienteMap, SecretRule secretRule) {
        Map<String, SecretRule> secretTypeMap = clienteMap.get(secretRule.getSecretTypeEnum(),new MyMap<>());
        secretTypeMap.put(secretRule.getSid(), secretRule);
    }

    private SecretRule createSecretRule(ClientSourceEnum clientSourceEnum, String sid, String secretTypeMsg, String aes, String md5) {
        SecretRule secretRule = new SecretRule();
        secretRule.setSid(sid);
        secretRule.setAes(aes);
        secretRule.setMd5(md5);
        secretRule.setSecretTypeEnum(SecretTypeEnum.getEnum(secretTypeMsg));
        secretRule.setClientSourceEnum(clientSourceEnum);
        return secretRule;
    }


}
