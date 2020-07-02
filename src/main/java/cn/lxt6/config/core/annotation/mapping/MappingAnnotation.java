package cn.lxt6.config.core.annotation.mapping;


import cn.lxt6.config.core.enums.QuestEnum;

import java.lang.annotation.*;

/**
 * @author chenzy
 * @since 2020-05-19
 *  元注解，用于标注mapping注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MappingAnnotation {
    QuestEnum value();
}
