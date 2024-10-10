package com.dtstack.engine.common.exception;

/**
 * @Auther: dazhi
 * @Date: 2023-11-09 10:49
 * @Email: dazhi@dtstack.com
 * @Description: FillLimitException
 */
public class FillLimitException extends RdosDefineException {

    public FillLimitException(String errorMessage) {
        super(errorMessage);
    }
}
