package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.RdosDefineException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author chener
 * @Classname ValidateUtil
 * @Description 参数校验工具
 * @Date 2020/11/19 13:59
 * @Created chener@dtstack.com
 */
public class ValidateUtil {
    /**
     * 数字、字母、下划线，必须以字母打头
     */
    public static final Pattern REGULAR_NAME = Pattern.compile("^[a-zA-z][a-zA-Z0-9_]*$");

    public static void validateNotNull(Object object,String info){
        if (Objects.isNull(object)){
            throw new RdosDefineException(info);
        }
    }

    public static boolean isRegularName(String name) {
        return REGULAR_NAME.matcher(name).matches();
    }
}
