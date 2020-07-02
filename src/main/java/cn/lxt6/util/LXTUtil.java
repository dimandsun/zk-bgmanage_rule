package cn.lxt6.util;

import cn.lxt6.config.core.CoreContainer;
import cn.lxt6.config.core.annotation.LogConfig;
import cn.lxt6.config.core.model.BeanModel;
import cn.lxt6.model.util.StringMap;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * @author chenzy
 *
 * @since 2020-05-18
 */
public class LXTUtil {
    private LXTUtil() {

    }

    public static Logger getClientLog(Method method) {
        StringMap<BeanModel> beanModelMap = CoreContainer.getInstance().getBeanMap();
        if (method.isAnnotationPresent(LogConfig.class)) {
            LogConfig logConfig = method.getAnnotation(LogConfig.class);
            BeanModel beanModel = beanModelMap.get(logConfig.value());
            if (beanModel == null) {
                return (Logger) beanModelMap.get("clientLog").getBean();
            }
            return (Logger) beanModel.getBean();
        }
        return (Logger) beanModelMap.get("clientLog").getBean();
    }
}
