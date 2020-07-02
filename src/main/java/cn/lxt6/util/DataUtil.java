package cn.lxt6.util;

import cn.lxt6.config.core.annotation.VarExplain;
import cn.lxt6.config.enums.ClientSourceEnum;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.ReqParQO;
import cn.lxt6.model.ResultVO;
import cn.lxt6.model.util.OutPar;
import cn.lxt6.secret.SecretContainer;
import cn.lxt6.secret.enums.SecretTypeEnum;
import cn.lxt6.secret.model.SecretRule;
import cn.lxt6.secret.util.SecretUtil;
import cn.lxt6.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Map;


/**
 * @author chenzy
 * @since 2020-04-13
 */
public class DataUtil {
    private DataUtil() {

    }

    private static Logger sysErrorLog = LoggerFactory.getLogger("sys_error");
    ;

    /**
     * 数据校验。 aspect调用，在controller调用具体业务service前进行数据校验
     * 校验通过返回null，未通过返回错误信息
     *
     * @param reqParQO
     * @return
     */
    public static ResultVO dataVerify(ReqParQO reqParQO) {
        ClientSourceEnum clientSourceEnum = reqParQO.getRequestType();
        String sign = reqParQO.getSign().trim();
        /*1、请求类型requestType 不能为空*/
        if (clientSourceEnum == null) {
            return new ResultVO(ResCodeEnum.ArgEmpty, "参数:requestType 为空");
        }
        /*2、参数:sign不能为空*/
        if (StringUtil.isBlank(sign)) {
            return new ResultVO(ResCodeEnum.ArgEmpty, "参数:sign为空");
        }
        switch (clientSourceEnum) {
            /*3、小程序校验*/
            case SmallProgram:
                ResultVO result = smallProgramVerify(reqParQO.getData(), reqParQO.getSid(), sign);
                if (result.isNotNormal()) {
                    return result;
                }
                break;
            /*4、app校验*/
            case APP:
                ResultVO appResult = appVerify(reqParQO.getData(), reqParQO.getSid(), sign);
                if (appResult.isNotNormal()) {
                    return appResult;
                }
                break;
            /*5、未知请求源*/
            default:
                return new ResultVO(ResCodeEnum.UnknownExce, "未知请求源！");
        }
        return new ResultVO(ResCodeEnum.Normal);
    }

    /**
     * 小程序校验
     *
     * @param wxCode
     * @param sId
     * @param sign
     * @return
     */
    private static ResultVO smallProgramVerify(String wxCode, String sId, String sign) {
        if (StringUtil.isBlank(wxCode)) {
            return new ResultVO(ResCodeEnum.ArgEmpty, "参数:wxcode为空");
        }
        try {
            wxCode = URLEncoder.encode(wxCode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //内部加密规则加密
        SecretRule secretRule = SecretContainer.getInstance().getSecretRule(ClientSourceEnum.SmallProgram, SecretTypeEnum.Self, sId);
        String checksign = SecretUtil.encryptByMD5(secretRule, wxCode);
        //校验
        if (!checksign.equals(sign)) {
            return new ResultVO(ResCodeEnum.SecretKeyError);
        }
        return new ResultVO(ResCodeEnum.Normal);
    }

    /**
     * @param code
     * @param sign
     * @return
     */
    private static ResultVO appVerify(String code, String sId, String sign) {
        if (StringUtil.isBlank(code)) {
            return new ResultVO(ResCodeEnum.ArgEmpty, "参数:code为空");
        }
        //内部加密规则加密
        SecretRule secretRule = SecretContainer.getInstance().getSecretRule(ClientSourceEnum.APP, SecretTypeEnum.Self, sId);
        String checksign = SecretUtil.encryptByMD5(secretRule, code);
        //校验
        if (!checksign.equals(sign)) {
            return new ResultVO(ResCodeEnum.SecretKeyError);
        }
        return new ResultVO(ResCodeEnum.Normal);
    }

    /**
     * 解析业务数据，封装进dataJson
     *
     * @param reqParQO
     * @return
     */
    public static ResultVO<Map> getBusDataJson(ReqParQO reqParQO) {
        //内部加密规则解密
        SecretRule secretRule = SecretContainer.getInstance().getSecretRule(reqParQO.getRequestType(), SecretTypeEnum.Self, reqParQO.getSid());
        String data = SecretUtil.decryptByAES(secretRule, reqParQO.getData());
        String dataJson = StringUtil.getStandardJsonString(data);
        return new ResultVO(JsonUtil.str2ModelSimple(dataJson, Map.class));
    }

    /**
     * 业务数据校验:通过VarExplain注解对指定字段进行验证
     *
     * @param o
     * @param fieldNames
     * @return
     */
    public static <T> ResultVO<String> validateField(T o, String... fieldNames) {
        if (fieldNames == null || fieldNames.length < 1) {
            return new ResultVO();
        }
        String validateMSG = "";
        for (String fieldName : fieldNames) {
            validateMSG += validateField(o, fieldName);
        }
        if ("".equals(validateMSG)) {
            return new ResultVO();
        } else {
            return new ResultVO(ResCodeEnum.ArgNoCorrect, validateMSG);
        }
    }

    private static String validateField(Object o, String fieldName) {
        final String methodName = "get" + StringUtil.upFirst(fieldName);
        try {
            StringBuilder sb = new StringBuilder();
            Class oClass = o.getClass();
            Field field = oClass.getDeclaredField(fieldName);
            if (field.isAnnotationPresent(VarExplain.class)) {
                VarExplain varExplain = field.getAnnotation(VarExplain.class);
                String explainValue = varExplain.value();
                Method method = oClass.getMethod(methodName);
                Object value = method.invoke(o);
                if (StringUtil.isBlank(value)) {
                    sb.append(explainValue).append("不能为空，");
                } else {

                    /*整形的大小判断*/
                    Integer max = varExplain.max();
                    Integer min = varExplain.min();
                    if (max != Integer.MAX_VALUE || min != Integer.MIN_VALUE) {
                        Integer valInt = Integer.valueOf(value.toString());
                        if (valInt < min) {
                            sb.append(explainValue).append("不能小于").append(min);
                        } else if (valInt > max) {
                            sb.append(explainValue).append("不能大于").append(max);
                        }
                    }
                    /*只能为数字*/
                    boolean justPositiveInt = varExplain.PositiveInt();
                    if (justPositiveInt && !StringUtil.isPositiveInt(value.toString())) {
                        sb.append(explainValue).append("只能为正整数");
                    }
                    /*字符串的长度判断*/
                    Integer maxLength = varExplain.maxLength();
                    Integer minLength = varExplain.minLength();
                    Integer length = value.toString().length();
                    if (minLength == maxLength && length != minLength) {
                        sb.append(explainValue).append("长度不能只能为").append(minLength);
                    } else if (length < minLength) {
                        sb.append(explainValue).append("长度不能小于").append(minLength);
                    } else if (length > maxLength) {
                        sb.append(explainValue).append("长度不能大于").append(maxLength);
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            String errMsg = ExceptionUtil.getMSG(e);
            sysErrorLog.error(errMsg);
            return errMsg;
        }
    }

    /**
     * 一个字段一个字段比较，得到字段改变前后的值
     * 返回的对象 resultVO.getData()得到是对字段改变前的值的封装，若为null表示没有改变。
     *
     * @param beforeAll        改变前的对象
     * @param afterAll         改变后的对象
     * @param afterChangeValue 字段改变后的值
     * @return
     */
    public static <T> ResultVO<T> getChangeValue(T beforeAll, T afterAll, OutPar<T> afterChangeValue) {
        if (beforeAll == null || afterAll == null) {
            return new ResultVO<>();
        }
        Boolean change = false;
        Class<T> c = (Class<T>) beforeAll.getClass();
        T afterChange=afterChangeValue.get();
        T beforeChange = null;
        try {
            beforeChange = c.getDeclaredConstructor().newInstance();
            if (afterChange==null){
                afterChange = c.getDeclaredConstructor().newInstance();
            }
            for (Field field : beforeAll.getClass().getDeclaredFields()) {
                String temp =StringUtil.upFirst(field.getName());
                String getMethodName = "get" + temp;
                String setMethodName = "get" + temp;
                Method getMethod = c.getMethod(getMethodName);
                Method setMethod = c.getMethod(setMethodName);
                Object beforValue=getMethod.invoke(beforeAll);
                Object afterValue=getMethod.invoke(afterAll);
                if (beforValue==null&&afterValue==null){
                    continue;
                }else if (beforValue==null&&afterValue!=null){
                    change=true;
                    setMethod.invoke(afterChange,afterValue);
                }else if (afterValue==null&&beforValue!=null){
                    change=true;
                    setMethod.invoke(beforeChange,beforValue);
                }else if (!beforValue.toString().equals(afterValue.toString())){
                    change=true;
                    setMethod.invoke(beforeChange,beforValue);
                    setMethod.invoke(afterChange,afterValue);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return new ResultVO<>();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ResultVO<>();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return new ResultVO<>();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return new ResultVO<>();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        if (!change){
            return new ResultVO<>();
        }
        afterChangeValue.set(afterChange);
        return new ResultVO<>(beforeChange);
    }
}
