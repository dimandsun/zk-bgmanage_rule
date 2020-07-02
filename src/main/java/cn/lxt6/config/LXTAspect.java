package cn.lxt6.config;

import cn.lxt6.config.core.annotation.Aspect;
import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.bean.Config;
import cn.lxt6.config.core.enums.AspectTypeEnum;
import cn.lxt6.config.db.DaoUtil;
import cn.lxt6.model.ResultVO;
import cn.lxt6.util.ExceptionUtil;
import cn.lxt6.util.LXTUtil;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author chenzy
 * @since 2020-04-03
 */
@Config
public class LXTAspect {

    @Auto
    private Logger clientLog;

    /**
     * 获取 mapper，执行对应sql
     *
     * @param target
     * @param args
     * @return
     */
    @Aspect(pointcuts = "public.* cn.lxt6\\.[(a-zA-Z_)]*\\.dao\\..*.*(..)", order = 1, type = AspectTypeEnum.Around)
    public Object daoAspect(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return DaoUtil.exeSql(target, method, args, methodProxy);
    }

    @Aspect(pointcuts = "public.* cn.lxt6\\.[(a-zA-Z_)]*\\.controller\\..*.*(..)", order = 1, type = AspectTypeEnum.Around)
    public Object dataAspect(Object target, Method method, Object[] args, MethodProxy methodProxy) {
        String methodName = method.getName();
        Logger logger =  LXTUtil.getClientLog(method);
        Object result = null;
        try {
            result = methodProxy.invokeSuper(target, args);
        } catch (IllegalAccessException e) {
            result = ExceptionUtil.getResult(e);
        } catch (InvocationTargetException e) {
            result = ExceptionUtil.getResult(e);
        } catch (Exception e){
            result = ExceptionUtil.getResult(e);
        }finally {
            resultLog(result, methodName, logger);
            return result;
        }
    }
    private void resultLog(Object result, String methodName, Logger logger) {
        if (result != null && result instanceof ResultVO) {
            ResultVO resultVO = (ResultVO) result;
            if (resultVO.getCode().isNotError()) {
                logger.info("[{}]成功，返回结果：{}", methodName, result.toString());
            } else {
                logger.error("[{}]失败:{}，错误信息：{}", methodName,resultVO.getMessage(), resultVO.toString());
            }
        }
    }
}
