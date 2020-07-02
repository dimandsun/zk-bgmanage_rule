package cn.lxt6.util.json;

import cn.lxt6.config.enums.IEnum;
import cn.lxt6.config.enums.WashEnum;
import cn.lxt6.util.ClassUtil;
import cn.lxt6.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.ClassUtils;
import org.postgresql.util.PGobject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Jackson对json数据的操作
 *
 * @author chenzy
 * @date 2019.12.19
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        SimpleModule simpleModule = new SimpleModule();
        /**
         * 序列化：对象=>jsonString
         */
        simpleModule.addSerializer(WashEnum.class, new WashEnumSerializer());
        simpleModule.addSerializer(IEnum.class, new EnumSerializer());
        simpleModule.addSerializer(PGobject.class, new PGobjectSerializer());
        simpleModule.addSerializer(Boolean.class, new BooleanSerializer());
        //忽略null字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        /**
         * 反序列化：jsonString=>对象
         */
        //允许json属性名不使用双引号
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //忽略不存在字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        simpleModule.addDeserializer(String.class, new StringDeserializer());
        simpleModule.addDeserializer(PGobject.class, new PGobjectDeserializer());
        simpleModule.addDeserializer(WashEnum.class, new WashEnumDeserializer());
        simpleModule.addDeserializer(Enum.class, new EnumDeserializer());//反序列化枚举，
        simpleModule.addDeserializer(Boolean.class, new BooleanDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    /**
     * 对象=>json字符串=>map
     *
     * @param model
     * @return
     */
    public static Map<String, Object> model2Map(Object model) {
        String json = model2Str(model);
        return str2Map(json);
    }

    public static <Model> Optional<Model> map2Model(Map<String, Object> beanMap, Class<Model> modelClass) {
        if (beanMap == null || beanMap.isEmpty()) {
            return Optional.empty();
        }
        String json = model2Str(beanMap);
        return str2ModelSimple(json, modelClass);
    }

    /**
     * 对象=>json字符串=>另一个对象
     *
     * @param model
     * @return
     */
    public static <Target> Optional<Target> model2Model(Object model, Class<Target> targetClass) {
        if (model == null) {
            return Optional.empty();
        }
        //对象是Target类型或子类，
        if (ClassUtils.isAssignable(model.getClass(), targetClass)) {
            return Optional.of((Target) model);
        }
        String json = model2Str(model);
        return str2ModelSimple(json, targetClass);
    }

    /**
     * 对象的字段是泛型时也能转换
     *
     * @param modelClass1
     * @param modelClass2
     * @param <T1>        对象类型
     * @param <T2>        字段的泛型
     * @return
     */
    public static <T1, T2> T1 model2Model(Object model, Class<T1> modelClass1, Class<T2> modelClass2) {
        String json = model2Str(model);
        return str2Model(json, modelClass1, modelClass2);
    }

    /**
     * 对象=>json字符串
     *
     * @param model
     * @return
     */
    public static String model2Str(Object model) {
        if (model==null){
            return null;
        }
        if (ClassUtil.isBasicDataType(model.getClass())){
            return model.toString();
        }
        try {
            return objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
           e.printStackTrace();
        }
        return null;
    }

    /**
     * json->model
     * 有些json格式不正确时，str2ModelSimple会出错，只能先转Map，map再转json，json再转model
     * str2ModelByBean()效率更低，不推荐使用
     * 例，正确格式：result:{data:{}}
     * 但当data为空时，接收到的是result:{data:""}。用
     *
     * @param jsonStr
     * @param modelClass
     * @param <T>
     * @return
     */
    public static <T> Optional<T> str2Model(String jsonStr, Class<T> modelClass) {
        if (StringUtil.isBlank(jsonStr)) {
            return Optional.empty();
        }
        jsonStr =StringUtil.str2JsonStr(jsonStr);

//        Map<String, Object> result = str2Map(jsonStr);
//        String json = model2Str(result);
        return str2ModelSimple(jsonStr, modelClass);
    }

    /**
     * 对象的字段是其他对象时也能转换
     *
     * @param jsonStr
     * @param modelClass1
     * @param <T>         对象类型
     * @return
     */
    public static <T> T str2Model(String jsonStr, Class<T> modelClass1, Class... mchildMdelClass) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(modelClass1, mchildMdelClass);
            T model = objectMapper.readValue(jsonStr, javaType);
            return model;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 对象的字段是泛型时也能转换
     *
     * @param jsonStr
     * @param modelClass1
     * @param modelClass2
     * @param <T1>        对象类型
     * @param <T2>        字段的泛型
     * @return
     */
    /*public static <T1, T2> T1 str2Model(String jsonStr, Class<T1> modelClass1, Class<T2> modelClass2) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(modelClass1, modelClass2);
            T1 model = objectMapper.readValue(jsonStr, javaType);
            return model;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }*/
//{"ble":[{"mac":"00:0E:0B:0E:C1:D2","uptime":0}],"id":1576122585098,"investor":140000000000000001,"mdata":[{"index":"","machinedata":""}],"mtype":0,"nb":[{"cmd":"","imei":"868221049310231","nb_random":"","nbdeviceid":"b02c5e40-de12-4d5a-8368-d69851d55844","nbflag":0,"status":1,"uptime":0}],"pminfo":null,"schoolid":1,"serverid":1,"serviceinfo":null,"softver":"V1.02","stateinfo":[{"info":"00","uptime":"1576808348938"}],"uptime":1576808348938,"use":1,"ver":"","zfbiot":[]}

    /**
     * json字符转model的简单实现，适合简单的标准json字符
     * 使用此方法时要注意 json格式要统一。若打算转result:{data:{}}，却传入result:{data:""}，会出问题。
     * {@link this.str2Model()}已解决此问题
     *
     * @param jsonStr
     * @param modelClass
     * @param <T>
     * @return
     */
    public static <T> Optional<T> str2ModelSimple(String jsonStr, Class<T> modelClass) {
        try {
            T ben = objectMapper.readValue(jsonStr, modelClass);
            return Optional.of(ben);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Map str2Map(String jsonStr) {
        if (StringUtil.isBlank(jsonStr)) {
            return new HashMap();
        }
        Integer index = jsonStr.indexOf("{");
        if (index > 0) {
            jsonStr = jsonStr.substring(index);
        }
        return str2ModelSimple(jsonStr, Map.class).get();
    }

    /**
     * 创建一个json对象
     *
     * @return
     */
    public static ObjectNode createJsonNode() {
        return objectMapper.createObjectNode();
    }

    public static void main(String[] ars) {
        {
            //ObjectNode
            String str = "{\"investor_id\":\"2\",\"school_id\":\"1\",\"type_id\":\"5\",\"routine_json\":\"{\\\"a\\\":\\\"常用基本信息\\\"}\",\"wallet_rechange_json\":\"{\\\"b\\\":\\\"钱包充值支付选择\\\"}\",\"wallet_refund_flag\":\"0\",\"deposit_refund_flag\":\"0\",\"service_time_flag\":\"0\",\"handler_id\":\"123\",\"handler_name\":\"陈志源测试\"}";
            System.out.println(str);
            str="{\"investor_id\":\"2\",\"school_id\":\"1\",\"type_id\":\"5\",\"routine_json\":{\"a\":\"常用基本信息\"},\"wallet_rechange_json\":{\"b\":\"钱包充值支付选择\"}\", \t\"wallet_refund_flag\": \"0\", \t\"deposit_refund_flag\": \"0\", \t\"service_time_flag\": \"0\", \t\"handler_id\": \"123\", \t\"handler_name\": \"陈志源测试\" }";
            str="{\"investor_id\":\"2\",\"school_id\":\"1\",\"type_id\":\"5\",\"routine_json\":\"{\\\"a\\\":\\\"常用基本信息\\\"}\",\"wallet_rechange_json\":\"{\\\"b\\\":\\\"钱包充值支付选择\\\"}\",\"wallet_refund_flag\":\"0\",\"deposit_refund_flag\":\"0\",\"service_time_flag\":\"0\",\"handler_id\":\"123\",\"handler_name\":\"陈志源测试\"}";
            System.out.println(str);
//            BaseManagerPO baseManagerPO= str2Model(str, BaseManagerPO.class);
//            System.out.println(baseManagerPO);
        }
        {

            ObjectNode objectNode=createJsonNode();
            objectNode.put("a","城城市爱的");
            String str="{\"investor_id\":\"2\",\"school_id\":\"1\",\"type_id\":\"5\",\"routine_json\":\"{\\\"a\\\":\\\"常用基本信息\\\"}\",\"wallet_rechange_json\":\"{\\\"b\\\":\\\"钱包充值支付选择\\\"}\",\"wallet_refund_flag\":\"0\",\"deposit_refund_flag\":\"0\",\"service_time_flag\":\"0\",\"handler_id\":\"123\",\"handler_name\":\"陈志源测试\"}";
            System.out.println(str2Model(str,ObjectNode.class));
            System.out.println(model2Str(objectNode));
            System.out.println(objectNode.toString());

        }
    }


}
