package com.dtstack.engine.master.faced.base;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *@author leon
 *@date 2022-10-09 19:26
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface DtStackSdk {

    String sdkName();

    String clientName() default "";
}
