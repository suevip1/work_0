package com.dtstack.engine.common.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Date: 2020/5/22
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public enum AlertGateCode {

    AG_GATE_SMS_YP("sms_yp"),
    AG_GATE_SMS_DY("sms_dy"),
    AG_GATE_SMS_API("sms_API"),

    AG_GATE_MAIL_DT("mail_dt"),

    AG_GATE_MAIL_API("mail_api"),

    AG_GATE_DING_DT("ding_dt"),
    AG_GATE_DING_API("ding_api"),

    AG_GATE_SMS_JAR("sms_jar"),
    AG_GATE_DING_JAR("ding_jar"),
    AG_GATE_MAIL_JAR("mail_jar"),

    AG_GATE_PHONE_TC("phone_tc"),

    AG_GATE_CUSTOM_JAR("custom_jar"),
    ;

    public static List<String> codeList = Lists.newArrayList(AG_GATE_SMS_JAR.code,AG_GATE_DING_JAR.code,
            AG_GATE_MAIL_JAR.code,AG_GATE_CUSTOM_JAR.code);

    private String code;

    AlertGateCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static Boolean isJarType(String code){
       return codeList.contains(code);
    }

    public static AlertGateCode parse(String code) {
        AlertGateCode[] values = values();
        for (AlertGateCode value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unsupported code " + code);
    }

}
