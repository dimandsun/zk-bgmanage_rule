package cn.lxt6.config.core;

import cn.lxt6.config.core.annotation.Aspect;
import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.bean.*;
import cn.lxt6.config.core.annotation.mapping.*;
import cn.lxt6.config.core.enums.ActiveEnum;
import cn.lxt6.config.core.enums.QuestEnum;
import cn.lxt6.config.core.model.BeanModel;
import cn.lxt6.config.core.model.ProjectInfo;
import cn.lxt6.config.core.model.RouteModel;
import cn.lxt6.config.db.DataSourceEnum;
import cn.lxt6.config.db.MybatisInfo;
import cn.lxt6.config.db.TypeAliases;
import cn.lxt6.model.util.OutPar;
import cn.lxt6.model.util.StringMap;
import cn.lxt6.util.FileUtil;
import cn.lxt6.util.StringUtil;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author chenzy
 * 应用容器，维护应用下的属性和bean对象
 * @since 2020-03-31
 */
public class CoreContainer implements Closeable {
    private static Logger logger = null;
    private static CoreContainer instance = new CoreContainer();
    /*key是 请求方法+url,value是RouteModel*/
    private StringMap<BeanModel> beanMap = new StringMap();
    private StringMap<RouteModel> routeMap = new StringMap();


    /**
     * 启动的时候使用，启动完毕清除
     */
    private List<Class> classList;

    public static CoreContainer getInstance() {
        return instance;
    }

    /*数据源集合*/
    private Map<DataSourceEnum, SqlSessionFactory> dataSourceMap = new HashMap<>();

    protected CoreContainer() {
    }

    @Override
    public void close() {
        beanMap.values().forEach(beanModel -> {
            Optional.ofNullable(beanModel).map(temp -> temp.getBean()).ifPresent(bean -> {
                if (bean instanceof Closeable) {
                    Closeable closeable = (Closeable) bean;
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        logger.error("关闭异常：资源释放IO异常",e);
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    /*初始化项目*/
    public void initProject() {
        try {
            /*启动日志*/
            initLog();
            logger = LoggerFactory.getLogger("sys_error");
            /*读取并设置配置文件application-xx.xml中的数据*/
            reloadBasicCofig();
            /*注入bean：把代码中的bean信息放入beanMap和routeMap*/
            setJavaBeanMap();
            /*注入bean:把代码中的config注解类中的注解在方法上的bean和切面放入容器*/
            setConfigClassInner();
            /*处理等待自动注入的属性*/
            doWaitAutoFieldMap();
            /*mybatis配置多数据源*/
            setMybatisConfig();
            classList = null;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    protected void setMybatisConfig() {
        String packageName = null;
        if (beanMap.get("mybatisInfo") != null) {
            packageName = ((MybatisInfo) beanMap.get("mybatisInfo").getBean()).getTypeAliases().getPackageName();
        }
        for (DataSourceEnum dataSourceEnum : DataSourceEnum.values()) {
            if (dataSourceEnum.getDataSource() == null) continue;
            Configuration configuration = new Configuration(new Environment("development", new JdbcTransactionFactory(), dataSourceEnum.getDataSource()));
            /*mybatis设置对象别名*/
            if (packageName != null) {
                TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
                for (Class c : getClassList(packageName)) {
                    typeAliasRegistry.registerAlias(c);
                }
            }
            /*设值mapper*/
            setMapper(configuration);
            configuration.addLoadedResource("mybatis-config.xml");
            dataSourceMap.put(dataSourceEnum, new SqlSessionFactoryBuilder().build(configuration));
        }
    }

    private void setClassList() {
        if (classList == null) {
            classList = new ArrayList<>();
        }
        String projectGroupId = ProjectInfo.getInstance().getProjectGroupId();
        String classPath = this.getClass().getResource("/").getPath() + projectGroupId.replace(".", File.separator);
        classList.addAll(FileUtil.getClassList(classPath, projectGroupId));
    }

    private List<Class> getClassList() {
        if (classList == null || classList.isEmpty()) {
            setClassList();
        }
        return classList;

    }

    private List<Class> getClassList(String packageName) {
        List<Class> result = new ArrayList<>();
        getClassList().forEach(clas -> {
            if (StringUtil.matcher(clas.getName(), packageName)) {
                result.add(clas);
            }
        });
        return result;
    }

    private List<Class> getClassList(Class<? extends Annotation> annotationClass) {
        List<Class> result = new ArrayList<>();
        getClassList().forEach(clas -> {
            if (clas.isAnnotationPresent(annotationClass)) {
                result.add(clas);
            }
        });
        return result;
    }

    /**
     * 把代码中的config注解类中的bean和切面放入容器。
     * 注意config注解的类也是个bean，在setBeanMap()中注入了
     */
    public void setConfigClassInner() {
        for (Class c : getClassList(Config.class)) {
            try {
                String configBeanName = StringUtil.lowFirst(c.getSimpleName().replace(".class", ""));
                Object o = beanMap.get(configBeanName).getBean();
                for (Method method : c.getMethods()) {
                    if (method.isAnnotationPresent(Bean.class)) {
                        BeanModel beanModel = new BeanModel(method.getName(), method.invoke(o), method.getReturnType());
                        beanMap.add(method.getName(), beanModel);
                    } else if (method.isAnnotationPresent(Aspect.class)) {
                        AspectContainer.getInstance().add(o, method);
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error("启动异常：注入config类失败");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                logger.error("启动异常：注入config类失败");
                e.printStackTrace();
            } catch (NullPointerException e) {
                logger.error("启动异常：注入config类失败");
                e.printStackTrace();
            }
        }
    }


    /*重新加载容器*/
    public void reLoadContainer() {
        /*初始化框架*/
        initProject();
    }

    private void setMapper(Configuration configuration) {
        for (Class c : getClassList(Dao.class)) {
            if (!configuration.hasMapper(c)) {
                configuration.addMapper(c);
            }
        }
    }

    /**
     * 获取主配置文件application-xx.yml信息
     */
    public Map<String, Object> getBasicConfigMap() {
        String proFileName = "application.yml";
        Map<String, Map<String, Object>> proMap = FileUtil.readConfigFileByYML(proFileName);
        String active = proMap.get("profiles").get("active").toString();
        ActiveEnum activeEnum = ActiveEnum.getEnum(active);
        ProjectInfo.getInstance().setActive(activeEnum);
        proFileName = "application-" + activeEnum.getMsg() + ".yml";
        Map<String, Object> resultMap = FileUtil.readConfigFileByYML(proFileName);
        return resultMap;
    }

    protected void setProBean(Map<String, Object> basicConfigMap) {
        try {
            /*1、注入数据源*/
            List<Map<String, Object>> datasourceList = (List<Map<String, Object>>) basicConfigMap.get("datasource");
            if (datasourceList!=null&&!datasourceList.isEmpty()) {
                datasourceList.forEach(map -> {
                    try {
                        DataSourceEnum.setDataSource((String)map.get("datasourceName"), DruidDataSourceFactory.createDataSource(map));
                    } catch (Exception e) {
                        logger.error("启动异常：注入数据源失败");
                        e.printStackTrace();
                    }
                });
            }
            /*写入mybatis配置信息*/
            String packageName = Optional.ofNullable((Map<String, Map<String, String>>) basicConfigMap.get("mybatis"))
                    .map(projectMap -> projectMap.get("typeAliases")).map(projectMap -> projectMap.get("packageName")).orElse("");
            if (packageName != "") {
                MybatisInfo mybatisInfo = new MybatisInfo(new TypeAliases(packageName));
                beanMap.add("mybatisInfo", new BeanModel("mybatisInfo", mybatisInfo, MybatisInfo.class));
            }
            /*2、注入redis*/
            setBeanRedis(basicConfigMap);
            /*3-注入memcache*/
            setBeanMemcah(basicConfigMap);

        } catch (Exception e) {
            logger.error("启动异常：加载配置文件失败");
            e.printStackTrace();
        }
    }

    public void setProjectInfo(Map<String, Object> projectMap) {
        if (projectMap != null) {
            Object projectName = projectMap.get("name");
            Object projectGroupId = projectMap.get("projectGroupId");
            if (projectName != null) {
                ProjectInfo.getInstance().setProjectName(projectName.toString());
            }
            if (projectGroupId != null) {
                ProjectInfo.getInstance().setProjectGroupId(projectGroupId.toString());
            }
        }
    }

    protected void setBeanRedis(Map<String, Object> basicConfigMap) {
        List<Map<String, Object>> redisMapList = (List<Map<String, Object>>) basicConfigMap.get("redis");
        if (redisMapList == null || redisMapList.isEmpty()) {
            return;
        }
        redisMapList.forEach(map -> {
            String poolBeanName = StringUtil.getStr(map.get("pool-name"), "jedisPool");
            String configBeanName = StringUtil.getStr(map.get("config-name"), "jedisPoolConfig");
            if (beanMap.containsKey(configBeanName)) {
                return;
            }
            if (beanMap.containsKey(poolBeanName)) {
                return;
            }
            /*2.1、创建jedisPoolConfig*/
            Map<String, Object> poolProMap = Optional.ofNullable((Map<String, Object>) map.get("lettuce"))
                    .map(lettuceMap -> (Map<String, Object>) lettuceMap.get("pool")).orElse(new HashMap<>(0));
            Integer maxIdle = StringUtil.getInt(poolProMap.get("max-idle"), 300);
            Integer minIdle = StringUtil.getInt(poolProMap.get("min-idle"), 0);
            Integer maxActive = StringUtil.getInt(poolProMap.get("max-active"), -1);
            Long maxWait = StringUtil.getLongByMS(poolProMap.get("max-wait"), 1000L);
            Boolean testOnBorrow = StringUtil.getBoolean(poolProMap.get("test-on-borrow"));
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(maxIdle);
            jedisPoolConfig.setMinIdle(minIdle);
            jedisPoolConfig.setMaxTotal(maxActive);
            jedisPoolConfig.setMaxWaitMillis(maxWait);
            jedisPoolConfig.setTestOnBorrow(testOnBorrow);
            /*2.2-创建JedisPool*/
            String host = StringUtil.getStr(map.get("host"), "127.0.0.1");
            Integer port = StringUtil.getInt(map.get("port"), 6379);
            String password = StringUtil.getStr(map.get("password"), null);
            Integer database = StringUtil.getInt(map.get("database"), 0);
            Integer timeout = StringUtil.getLongByMS(map.get("timeout"), 60L).intValue();
            JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);

            beanMap.add(configBeanName, new BeanModel(configBeanName, jedisPoolConfig, JedisPoolConfig.class));
            beanMap.add(poolBeanName, new BeanModel(poolBeanName, jedisPool, JedisPool.class));

        });

    }

    private void setBeanMemcah(Map<String, Object> proMap) {
        Map<String, Object> memcacheProMap = Optional.ofNullable((Map<String, Object>) proMap.get("memcache")).orElse(new HashMap<>(0));
        String[] servers = StringUtil.getStrlist(memcacheProMap.get("servers"), new String[]{""});
        Boolean failover = StringUtil.getBoolean(memcacheProMap.get("failover"), true);
        Integer initConn = StringUtil.getInt(memcacheProMap.get("initConn"), 20);
        Integer minConn = StringUtil.getInt(memcacheProMap.get("minConn"), 10);
        Integer maxConn = StringUtil.getInt(memcacheProMap.get("maxConn"), 200);
        Integer maintSleep = StringUtil.getInt(memcacheProMap.get("maintSleep"), 3000);
        Boolean nagel = StringUtil.getBoolean(memcacheProMap.get("nagel"), false);
        Integer socketTO = StringUtil.getInt(memcacheProMap.get("socketTO"), 3000);
        Boolean aliveCheck = StringUtil.getBoolean(memcacheProMap.get("aliveCheck"), true);
        String poolName = StringUtil.getStr(memcacheProMap.get("poolName"), "memcachedPool");
        SockIOPool sockIOPool = SockIOPool.getInstance(poolName);
        sockIOPool.setServers(servers);
        sockIOPool.setFailover(failover);
        sockIOPool.setInitConn(initConn);
        sockIOPool.setMinConn(minConn);
        sockIOPool.setMaxConn(maxConn);
        sockIOPool.setMaintSleep(maintSleep);
        sockIOPool.setNagle(nagel);
        sockIOPool.setSocketTO(socketTO);
        sockIOPool.setAliveCheck(aliveCheck);
        sockIOPool.initialize();
        beanMap.add("sockIOPool", new BeanModel("sockIOPool", sockIOPool, SockIOPool.class));
        beanMap.add("memCachedClient", new BeanModel("memCachedClient", new MemCachedClient(poolName), MemCachedClient.class));
    }

    public Map<DataSourceEnum, SqlSessionFactory> getDataSourceMap() {
        return dataSourceMap;
    }


    public StringMap<RouteModel> getRouteMap() {
        return routeMap;
    }

    public StringMap<BeanModel> getBeanMap() {
        return beanMap;
    }


    /**
     * java代码配置的bean放入容器中
     */
    public void setJavaBeanMap() {
        for (Class c : getClassList()) {
            /*1、获取beanName*/
            OutPar<Class> primaryInterfaceClassPar = new OutPar();
            String beanName = getBeanName(c, primaryInterfaceClassPar);
            if (StringUtil.isBlank(beanName)) {
                continue;
            }
            BeanModel beanModel = new BeanModel(beanName);
            if (primaryInterfaceClassPar.get() != null) {
                beanModel.setPrimaryInterfaceClass(primaryInterfaceClassPar.get());
            }
            /*2、获取bean对象*/
            Object bean = null;
            if (c.isAnnotationPresent(Dao.class)) {
                /*Dao、Controller、Service 动态代理，用于切面编程。
                    注意：dao只是个接口，不能生成bean对象，只能生成代理对象，*/
                bean = CGLIB.getInstance().getInstance(c);
            } else if (c.isAnnotationPresent(Service.class) || c.isAnnotationPresent(Controller.class)) {
                bean = CGLIB.getInstance().getInstance(getBean(c));
            } else {
                /*其他bean不实现动态代理*/
                bean = getBean(c);
            }
            beanModel.setBean(bean);
            /*3、bean的class*/
            beanModel.setPrimaryBeanClass(c);
            /*4、添加自动注入字段信息*/
            addWaitAutoFieldMap(beanModel);
            /*5、bean放入beanMap*/
            beanMap.add(beanModel.getBeanName(), beanModel);
            /*6、controller层的接口路由注入*/
            if (c.isAnnotationPresent(Controller.class)) {
                setRouteMap(beanModel);
            }
        }
    }

    private Object getBean(Class beanClass) {
        try {
            return beanClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            logger.error("启动异常：创建bean失败");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            logger.error("启动异常：创建bean失败");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            logger.error("启动异常：创建bean失败");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            logger.error("启动异常：创建bean失败");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理等待自动注入的属性
     */
    private void doWaitAutoFieldMap() {
        try {
            for (BeanModel beanModel : beanMap.values()) {
                if (beanModel.hasWaitAutoField()) {
                    Map<Field, Object> waitAutoFieldMap = beanModel.getWaitAutoFieldMap();
                    for (Field field : waitAutoFieldMap.keySet()) {
                        BeanModel fieldBeanModel = beanMap.get(field.getName());
                        if (fieldBeanModel == null) {
                            logger.error("auto自动注入失败，类：{},属性：{}", beanModel.getPrimaryBeanClass(), field.getType());
                        } else {
                            field.setAccessible(true);
                            field.set(beanModel.getBean(), fieldBeanModel.getBean());
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("启动异常：创建bean时为auto属性赋值失败");
            e.printStackTrace();
        }
    }

    private void setRouteMap(BeanModel controllerBeanModel) {
        Class c = controllerBeanModel.getPrimaryBeanClass();
        //路由前缀，在Controller类上
        String urlPrefix = null;
        if (c.isAnnotationPresent(Mapping.class)) {
            Mapping mappingAnnotation = (Mapping) c.getAnnotation(Mapping.class);
            urlPrefix = trim(mappingAnnotation.value());
        }
        //路由后缀，在方法的Mapping注解上
        String urlSuffix = null;
        QuestEnum questEnum = null;
        for (Method method : c.getMethods()) {
            if (method.isAnnotationPresent(Mapping.class)) {
                questEnum = QuestEnum.All;
                urlSuffix = method.getAnnotation(Mapping.class).value();
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                questEnum = QuestEnum.Post;
                urlSuffix = method.getAnnotation(PostMapping.class).value();
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                questEnum = QuestEnum.Put;
                urlSuffix = method.getAnnotation(PutMapping.class).value();
            } else if (method.isAnnotationPresent(GetMapping.class)) {
                questEnum = QuestEnum.Get;
                urlSuffix = method.getAnnotation(GetMapping.class).value();
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                questEnum = QuestEnum.Delete;
                urlSuffix = method.getAnnotation(DeleteMapping.class).value();
            } else {
                continue;
            }
            RouteModel routeModel = new RouteModel();
            String url = "";
            if (StringUtil.isNotBlank(urlPrefix)) {
                url = urlPrefix;
            }
            urlSuffix = trim(urlSuffix);
            if (StringUtil.isNotBlank(urlSuffix)) {
                url += urlSuffix;
            }
            routeModel.setUrl(url);
            routeModel.setQuestEnum(questEnum);
            routeModel.setBeanModel(controllerBeanModel);
            routeModel.setMethod(method);
            routeMap.add(routeModel.getRouteKey(), routeModel);
        }

    }

    private String trim(String url) {
        if (StringUtil.isBlank(url)) {
            return url;
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private String getBeanNameByInterface(Class interfaceClass) {
        String inteSimpleName = interfaceClass.getSimpleName();
        if (inteSimpleName.startsWith("I")) {
            inteSimpleName = inteSimpleName.substring(1);
        }
        String projectGroupId = ProjectInfo.getInstance().getProjectGroupId();
        if (interfaceClass.getName().contains(projectGroupId)) {
            return StringUtil.lowFirst(inteSimpleName.replace(".class", ""));
        }
        return null;
    }

    /**
     * 获取beanName及primaryInterfaceClass
     * 注意objectClass本身是接口时，不返还class实现的接口。
     * <p>
     * 从注解值中获取beanName
     * 从接口中获取beanName
     * 从类中获取beanName
     *
     * @param objectClass
     * @return
     */
    public String getBeanName(Class objectClass, OutPar<Class> primaryInterfaceClassPar) {
        Annotation[] annotations = objectClass.getAnnotations();
        if (annotations == null || annotations.length < 1) {
            return null;
        }
        String beanName = null;
        /*1、从注解值中获取beanName*/
        Boolean isBean = false;
        for (Annotation annotation : annotations) {
            Class annotationClass = annotation.annotationType();
            if (!annotationClass.isAnnotationPresent(BeanAnnotation.class)) {
                continue;
            }
            isBean = true;
            try {
                Method method = annotationClass.getMethod("value");
                Object temp = method.invoke(annotation);
                if (temp != null) {
                    beanName = temp.toString();
                }
            } catch (NoSuchMethodException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            }
        }
        if (!isBean) {
            return null;
        }
        if (StringUtil.isNotBlank(beanName)) {
            return beanName;
        }
        if (objectClass.isInterface()) {
            return getBeanNameByInterface(objectClass);
        }
        /*2、从接口中获取beanName*/
        Class primaryInterfaceClass = null;
        for (Class interfaceClass : objectClass.getInterfaces()) {
            beanName = getBeanNameByInterface(interfaceClass);
            if (beanName != null) {
                primaryInterfaceClass = interfaceClass;
                break;
            }
        }
        /*3、从类中获取beanName*/
        if (StringUtil.isBlank(beanName)) {
            beanName = StringUtil.lowFirst(objectClass.getSimpleName().replace(".class", ""));
        }
        if (beanMap.containsKey(beanName)) {
            logger.error("bean名称重复：{}", beanName);
            return null;
        }
        if (primaryInterfaceClass != null) {
            primaryInterfaceClassPar.set(primaryInterfaceClass);
        }
        return beanName;
    }


    /**
     * bean已经在beanMap中时，直接赋值
     * bean没有在beanMap中时，暂时放到noAutoFieldMap中
     *
     * @param beanModel
     */
    private void addWaitAutoFieldMap(BeanModel beanModel) {
        Class primaryBeanClass = beanModel.getPrimaryBeanClass();
        if (primaryBeanClass == null) {
            return;
        }
        try {
            for (Field field : primaryBeanClass.getDeclaredFields()) {
                /*字段存在自动注入的注解，需要给字段赋值*/
                if (field.isAnnotationPresent(Auto.class)) {
                    BeanModel fieldBeanModel = beanMap.get(field.getName());
                    if (fieldBeanModel != null) {
                        if (notClassType(field.getType(), fieldBeanModel.getPrimaryBeanClass(), fieldBeanModel.getPrimaryInterfaceClass())) {
                            logger.error("要注入的bean类型错误，请检查：{}.{}", primaryBeanClass.getName(), field.getName());
                            continue;
                        }
                        /*bean已经在beanMap中时，直接赋值*/
                        field.setAccessible(true);
                        field.set(beanModel.getBean(), fieldBeanModel.getBean());
                    } else {
                        /*bean没有在beanMap中时，暂时放到noAutoFieldMap中*/
                        beanModel.addWaiteAutoField(field);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("启动异常：创建bean失败");
            e.printStackTrace();
        }
    }

    /**
     * 判断参数1是否是参数2或者参数3的类型。是返回true
     * 参数3可为空
     *
     * @param c
     * @param c1
     * @param c2
     * @return
     */
    private Boolean notClassType(Class c, Class c1, Class c2) {
        return c != c1 && (c2 == null || c != c2);
    }


    protected void initLog() {
        //lo4j2
   /*
        File file = FileUtil.getFile("logback-"+ ProjectInfo.getInstance().getActive().getMsg() +".xml");
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(file.toURI());
        //重新初始化Log4j2的配置上下文
        context.reconfigure();

*/
   /*
        System.out.println("------------------------------日志正在初始化------------------------------");
        logger = LoggerFactory.getLogger(this.getClass());
        LoggerContext loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        File file = FileUtil.getFile("config/logback-"+ ProjectInfo.getInstance().getActive().getMsg() +".xml");
        if (!file.exists()) {
            logger.error("logbackConfigPath file is no exist");
        } else {
            if (!file.isFile()) {
                logger.error("logbackConfigPath file is not a file");
            } else {
                if (!file.canRead()) {
                    logger.error("logbackConfigPath file can not read");
                } else {
                    JoranConfigurator joranConfigurator = new JoranConfigurator();
                    joranConfigurator.setContext(loggerContext);
                    loggerContext.reset();
                    try {
                        joranConfigurator.doConfigure(file);
                    } catch (Exception e) {
                        logger.error("logbackConfigPath Load logback config file error. Message: {}", e.getMessage());
                    }
//                    StatusPrinter.printIfErrorsOccured(loggerContext);
//                    StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
                }
            }
        }
        System.out.println("------------------------------日志初始化完成------------------------------");
        */
    }

    public void reloadBasicCofig() {
        /*读取配置文件application-xx.xml中的数据*/
        Map<String, Object> basicConfigMap = getBasicConfigMap();
        /*初始化项目信息*/
        setProjectInfo((Map<String, Object>) basicConfigMap.get("project"));
        // 注入bean: 配置文件中bean
        setProBean(basicConfigMap);
    }

    public void reloadLogCofig() {
        initLog();
    }

    public void reloadMybatis() {
        setMybatisConfig();
    }

    public static void main(String[] args) {
//        CoreContainer instance = CoreContainer.getInstance();
//        instance.initProject();
        System.out.println(Config.class.isAnnotationPresent(BeanAnnotation.class));
        System.out.println(Service.class.isAnnotationPresent(BeanAnnotation.class));
    }
}
