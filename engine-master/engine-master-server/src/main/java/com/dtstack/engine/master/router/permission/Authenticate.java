package com.dtstack.engine.master.router.permission;

import java.lang.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2022/4/13 4:31 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authenticate {

    String all();

    String tenant() default "";

}
