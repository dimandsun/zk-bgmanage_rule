package cn.lxt6.model;

import cn.lxt6.config.consts.ConstContainer;
import cn.lxt6.config.enums.ClientSourceEnum;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.util.BufferPar;
import cn.lxt6.secret.SecretContainer;
import cn.lxt6.secret.enums.SecretTypeEnum;
import cn.lxt6.secret.model.SecretRule;
import cn.lxt6.secret.util.SecretUtil;
import cn.lxt6.util.json.JsonUtil;
import org.slf4j.Logger;

import java.util.Map;

/**
 * @author chenzy
 *  调用其他项目接口时发送quest的信息封装
 * @since 2020-04-11
 */
public class QuestModel<T> {
    private String url;
    /*明文参数:业务参数*/
    private BufferPar dataPar;
    private ReqParQO reqParQO;
    private Class<T> resultType;
    private Logger logger;
    private ResCodeEnum resNullEnum;
    private String fromMethodName;
    private SecretTypeEnum secretTypeEnum;
    private String random;
    private String token;
    private ResCodeEnum resCodeEnum;
    private String sid;

    public String getSid() {
        return sid;
    }

    public QuestModel setSid(String sid) {
        this.sid = sid;
        return this;
    }

    public String getToken() {
        return token;
    }

    public QuestModel setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public QuestModel setUrl(String url) {
        this.url = url;
        return this;
    }

    public QuestModel setUrl(Long schoolId, String route) {
        String serverAdress = ConstContainer.getInstance().getURL(schoolId);
        if (serverAdress != null) {
            this.url = serverAdress + route;// ConstContainer.getInstance().getURL(route);
        }
        return this;
    }

    public void encrypt() {
        if (reqParQO == null) {
            reqParQO = new ReqParQO();
        }
        String orgPar=this.dataPar.toString();

        ClientSourceEnum requestType = ClientSourceEnum.APP;
        SecretRule secretRule = SecretContainer.getInstance().getSecretRule(requestType, secretTypeEnum, sid);
        String data = SecretUtil.encryptByAES(secretRule, orgPar);
        /*aes加密后再md5加密*/
        String sign = SecretUtil.encryptByMD5(secretRule, data);
        String sid = secretRule.getSid();
        reqParQO.setData(data);
        reqParQO.setSign(sign);
        reqParQO.setSid(sid);
        reqParQO.setRequestType(requestType);
        reqParQO.setToken(token);
    }

    public String getRealPar() {
        encrypt();
        Map<String, Object> te = JsonUtil.model2Model(reqParQO, Map.class).get();
        StringBuilder re = new StringBuilder();
        for (Map.Entry<String, Object> entry : te.entrySet()) {
            re.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return re.toString();
    }

    public QuestModel addPar(String parName, Object parValue) {
        if (parName == "random" || parName == "rand") {
            random = parValue.toString();
        }
        if (dataPar == null) {
            dataPar = new BufferPar(parName, parValue);
        } else {
            dataPar.add(parName, parValue);
        }
        return this;
    }

    public ResCodeEnum getResCodeEnum() {
        return resCodeEnum;
    }

    public QuestModel setSuccessCode(ResCodeEnum resCodeEnum) {
        this.resCodeEnum = resCodeEnum;
        return this;
    }

    public Class<T> getResultType() {
        return resultType;
    }

    public BufferPar getDataPar() {
        return dataPar;
    }

    public String getFromMethodName() {
        return fromMethodName;
    }

    public SecretTypeEnum getSecretTypeEnum() {
        return secretTypeEnum;
    }

    public QuestModel setSecretTypeEnum(SecretTypeEnum secretTypeEnum) {
        this.secretTypeEnum = secretTypeEnum;
        return this;
    }

    public QuestModel setReqParQO(ReqParQO reqParQO) {
        this.reqParQO = reqParQO;
        return this;
    }

    public QuestModel setResultType(Class<T> resultType) {
        this.resultType = resultType;
        return this;
    }

    public QuestModel setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public QuestModel setResNullEnum(ResCodeEnum resNullEnum) {
        this.resNullEnum = resNullEnum;
        return this;
    }

    public ResCodeEnum getResNullEnum() {
        return resNullEnum;
    }

    public QuestModel setFromMethodName(String fromMethodName) {
        this.fromMethodName = fromMethodName;
        return this;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getRandom() {
        return random;
    }


}
