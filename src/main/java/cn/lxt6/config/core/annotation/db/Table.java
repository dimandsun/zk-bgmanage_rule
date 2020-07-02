package cn.lxt6.config.core.annotation.db;

import java.lang.annotation.*;

/**
 * @author 陈志源 on 2020.04.29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String value() default "";
}
