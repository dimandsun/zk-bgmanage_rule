package cn.lxt6.config.core;

import cn.lxt6.config.core.annotation.Aspect;
import cn.lxt6.config.core.model.AspectModel;
import cn.lxt6.util.StringUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author chenzy
 * @since 2020-04-03
 */
public class AspectContainer {
    private static AspectContainer instance = new AspectContainer();

    private AspectContainer() {
    }

    public static AspectContainer getInstance() {
        return instance;
    }

    private Set<AspectModel> aspectModelList = new HashSet<>();

    public void add(Object aspectObject, Method aspectMethod) {
        if (aspectMethod.isAnnotationPresent(Aspect.class)) {
            Aspect aspect = aspectMethod.getAnnotation(Aspect.class);
            for (String pointcut : aspect.pointcuts()) {
                aspectModelList.add(new AspectModel(pointcut, aspectMethod, aspect.order(), aspectObject, aspect.type()));
            }
        }
    }

    public void clear() {
        aspectModelList.clear();
    }

    public List<AspectModel> getAspectModelList(String targetMethod) {
        List<AspectModel> result = new ArrayList<>();
        for (AspectModel aspectModel : aspectModelList) {
            if (StringUtil.matcher(targetMethod, aspectModel.getPointcut())) {
                result.add(aspectModel);
            }
        }
        Collections.sort(result, AspectModel::compareTo);
        return result;
    }

    public static void main(String[] args) {
        String a = "public abstract cn.lxt6.bgmanage_rule.model.db.BaseManagerPO cn.lxt6.bgmanage_rule.dao.IBaseManagerDao.selectById(java.lang.Long)";
        String patter = "public.* cn.lxt6\\.[(a-zA-Z_)]*\\.dao\\..*.*(..)";
//        patter = "public.* cn.lxt6.bgmanage_rule.dao\\..*.*(..)";

        Boolean b= StringUtil.matcher(a, patter);
        System.out.println(b);
    }
}
