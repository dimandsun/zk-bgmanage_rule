package cn.lxt6.config.servlet;

import cn.lxt6.config.core.annotation.Par;
import cn.lxt6.config.core.enums.QuestEnum;
import cn.lxt6.config.core.model.RouteModel;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.ReqParQO;
import cn.lxt6.model.ResultVO;
import cn.lxt6.model.util.OutPar;
import cn.lxt6.model.util.StringMap;
import cn.lxt6.security.model.SecurityInfo;
import cn.lxt6.util.ClassUtil;
import cn.lxt6.util.DataUtil;
import cn.lxt6.util.LXTUtil;
import cn.lxt6.util.StringUtil;
import cn.lxt6.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Map;

/**
 * 所有请求入口(除静态资源请求),把请求分配给不同的controller
 *
 * @author 陈志源 on 2019-01-01.
 */
public class DispatchServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger("sys_error");
    protected static StringMap<RouteModel> routeModelMap = null;
    protected static String projectName = null;
    protected static String charEncoding = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        routeModelMap = (StringMap<RouteModel>) getServletContext().getAttribute("routeModelMap");
        projectName = getServletContext().getAttribute("projectName").toString();
        charEncoding = getServletContext().getAttribute("charEncoding").toString();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.exec(req, resp, QuestEnum.Put);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.exec(req, resp, QuestEnum.Delete);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.exec(req, resp, QuestEnum.Get);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.exec(req, resp, QuestEnum.Post);
    }

    protected String getNotFoundURL(){
       return  "/error/error_404";
    }
    private RouteModel getRouteModel(HttpServletRequest req, HttpServletResponse resp, QuestEnum questEnum, String url) {
        String noFoundURL = getNotFoundURL();
        try {
            req.setCharacterEncoding(charEncoding);
            resp.setCharacterEncoding(charEncoding);
            resp.setHeader("Content-type", "text/html;" + charEncoding);
            String method = req.getParameter("_method");
            if (StringUtil.isBlank(method)) {
                method = questEnum.getMsg();
            } else {
                method = method.trim().toLowerCase();
            }
            RouteModel routeModel = routeModelMap.get(method + url);
            if (routeModel == null) {
                //再看看是否有可以接受任意请求方式的接口
                routeModel=routeModelMap.get("all"+url);
                if (routeModel == null) {
                    req.getRequestDispatcher(noFoundURL).forward(req, resp);
                    return null;
                }
            }
            return routeModel;
        } catch (UnsupportedEncodingException e) {
            logger.error("编码转换异常，不支持编码:{}", charEncoding);
        } catch (ServletException e) {
            logger.error("请求转发异常,转发地址:{}", noFoundURL);
        } catch (IOException e) {
            logger.error("请求转发IO异常,转发地址:{}", noFoundURL);
        }
        return null;
    }

    /**
     * 处理结果,以josn形式输出
     */
    private void returnJson(Object result, HttpServletResponse resp) {
        String resultJosn = JsonUtil.model2Str(result);
        try {
            OutputStream outputStream = resp.getOutputStream();
            //将字符转换成字节数组，指定以UTF-8编码进行转换
            byte[] dataByteArr = resultJosn.getBytes(charEncoding);
            //使用OutputStream流向客户端输出字节数组
            outputStream.write(dataByteArr);
        } catch (IOException e) {
            logger.error("以json形式返回请求结果时IO异常，返回数据：{}",resultJosn);
        }
    }

    private void exec(HttpServletRequest req, HttpServletResponse resp, QuestEnum questEnum) {
        OutPar<Object> urlVerifyResult = new OutPar<>();
        String url = getURL(req,urlVerifyResult);
        {
            Object parVerify = urlVerifyResult.get();
            /*url验证不通过，直接返回错误信息*/
            if (parVerify!=null){
                returnJson(parVerify, resp);
                return;
            }
        }
        RouteModel routeModel = getRouteModel(req, resp, questEnum, url);
        if (routeModel == null) {
            return;
        }
        try {
            Method modelMethod = routeModel.getMethod();
            /*获取参数,子类实现getPar方法，即对参数可以做自定义的校验及解析*/
            OutPar<Object> parVerifyResult = new OutPar<>(null);
            Object[] pars = getPar(url,modelMethod, req, resp,parVerifyResult);
            Object parVerify = parVerifyResult.get();
            /*数据验证不通过，直接返回错误信息*/
            if (parVerify!=null){
                returnJson(parVerify, resp);
                return;
            }
            /*执行业务方法*/
            Object result = null;
            if (pars == null||pars.length==0) {
                result = modelMethod.invoke(routeModel.getBeanModel().getBean());
            } else {
                result = modelMethod.invoke(routeModel.getBeanModel().getBean(), pars);
            }
            /*输出结果*/
            returnJson(result, resp);
        } catch (Exception e) {
            logger.error("访问异常：[url：" + url + "]", e);
        }


    }
    protected String getURL(HttpServletRequest req, OutPar<Object> urlVerifyResult) {
        String url = req.getRequestURI();
        if (StringUtil.isNotBlank(projectName)) {
            url = url.replace("/" + projectName, "");
        }
        SecurityInfo securityInfo = SecurityInfo.getInstance();

        /*拦截除heartbeat/xx外的所有接口 判断是否处于维护状态。处于维护中的话，则直接返回维护信息*/
        if (securityInfo.getMaintenance() && !url.startsWith("/heartbeat")) {
            urlVerifyResult.set(new ResultVO(ResCodeEnum.BusInfo, "系统正在维护中，大约"
                    + Duration.between(LocalDateTime.now(),securityInfo.getEndTime()).toMinutes()
                    + "分钟后恢复，请稍后再尝试使用"));
        }
        return url;
    }
    /**
     * 解析参数
     * 对参数可以做自定义的校验及解析
     * @param method
     * @param req
     * @return
     */
    protected Object[] getPar(String url, Method method, HttpServletRequest req, HttpServletResponse resp, OutPar<Object> parVerifyResult) {
        Logger logger =  LXTUtil.getClientLog(method);
        String methodName = method.getName();
        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length < 1) {
            return null;
        }
        /*1、得到json格式参数*/
        StringMap reqParMap = getJson(method,req,resp);
        if (reqParMap == null || reqParMap.size() == 0) {
            return getemptyParList(parameters);
        }
        Map busDataJson =null;
        if (reqParMap.containsKey("requestType")) {
            /*2、json参数先解析成ReqParQO对象*/
            ReqParQO reqParQO = JsonUtil.model2Model(reqParMap, ReqParQO.class).get();
            logger.info("[{}]接收参数:{}", methodName, reqParQO.toString());
            /*3、对reqParQO进行校验*/
            {
                ResultVO result = DataUtil.dataVerify(reqParQO);
                if (result.isNotNormal()) {
                    logger.error("[{}]return data <<< {}", methodName, result);
                    parVerifyResult.set(result);
                    return null;
                }
            }
            /*4、数据解析*/
            ResultVO<Map> busDataResult = DataUtil.getBusDataJson(reqParQO);
            /*数据校验未通过，或者数据解析失败，不执行业务方法，记录错误日志后直接返回错误信息*/
            if (busDataResult.isNotNormal()) {
                logger.error("[{}]return data <<< {}", methodName, busDataResult);
                parVerifyResult.set(busDataResult);
                return null;
            }
            busDataJson = busDataResult.getData();
            logger.info("[{}]解析业务数据:{}", methodName, busDataJson);
        }else {
            busDataJson=reqParMap;
        }
        /*5、把reqParQO解析成更具体的业务数据
          需要注意这里的约定：1、若业务方法的参数是非基础数据类型，即不是八种基本数据类型极其包装类，以及String的参数，会尝试给此参数赋值
                            2、若业务方法的参数是基本数据类型，那么约定此参数有注解@Par，会尝试把json中与注解值对应key的数据给此参数赋值
        */
        Object[] resultPar = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class parClass = parameters[i].getType();
            /*非基础数据类型的参数，直接转换*/
            if (!ClassUtil.isBasicDataType(parClass)) {
                resultPar[i] = JsonUtil.model2Model(busDataJson, parClass).get();
            } else {
                /*基础数据类型则根据注解值取数据*/
                for (Annotation annotation : parameters[i].getAnnotations()) {
                    Object temp = getParObject(annotation, reqParMap, busDataJson,parClass);
                    resultPar[i] = temp;
                }
            }

        }
        return resultPar;
    }
    /**
     * 当没有接收参数，但是目标业务方法有参数时，生成空对象
     *
     * @param parameters
     * @return
     */
    private Object[] getemptyParList(Parameter[] parameters) {
        Object[] resultPar = new Object[parameters.length];
        try {
            for (int i = 0; i < parameters.length; i++) {
                Class parClass = parameters[i].getType();
                resultPar[i] = parClass.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return resultPar;
    }
    /**
     * 参数如果是reqParQO对象中的明文数据，取参数2的值，如果是reqParQO中的加密(包括拼接)数据，若是加密数据，则取参数三中的数据
     *
     * @param annotation
     * @param reqParMap
     * @param busDataJson
     * @return
     */
    private <T> T getParObject(Annotation annotation, StringMap reqParMap, Map busDataJson,Class<T> parClass) {
        if (annotation instanceof Par) {
            String parName = ((Par) annotation).value();
            Object value = reqParMap.get(parName);
            value=value != null ? value : busDataJson.get(parName);
            if (parClass==Long.class){
                return (T) StringUtil.getLong(value);
            }else if (parClass==Integer.class){
                return (T) StringUtil.getInt(value);
            }
            return parClass.cast(value);
        } else {
            return null;
        }
    }


    private StringMap getJson(Method method, HttpServletRequest req, HttpServletResponse resp) {
        Enumeration paramNames = req.getParameterNames();
//        ObjectNode objectNode = JsonUtil.createJsonNode();
        StringMap<Object> par = new StringMap<>();
        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement().toString().trim();
            if ("_method".equals(key)) {
                continue;
            }
            StringBuilder value = new StringBuilder();
            for (String v : req.getParameterValues(key)) {
                value.append(v);
            }
            par.put(key, value.toString());
        }
        return par;
    }
}
