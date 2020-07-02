package cn.lxt6.config.core.annotation.bean;

import java.lang.annotation.*;

/**
 * dao层注解
 * @author 陈志源 on 2019-01-08.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanAnnotation
public @interface Dao {
    String value() default "";
}
