package cn.lxt6.config.core.annotation.mapping;


import cn.lxt6.config.core.enums.QuestEnum;

import java.lang.annotation.*;

/**
 * @author 陈志源 on 2019-01-02.
 * 路由,controller的方法上的注解 对应QuestEnum.Delete
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MappingAnnotation(QuestEnum.Delete)
public @interface DeleteMapping {
    String value() default "";
}
