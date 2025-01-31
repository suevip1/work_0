package com.dtstack.engine.master.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.vo.alert.AlertGateTestVO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.master.enums.SearchTypeEnum;
import com.dtstack.engine.common.enums.AlertGateCode;
import com.dtstack.engine.common.enums.AlertGateTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 2020/8/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class CheckUtils {
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String REGEX_MOBILE = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|195|(147))\\d{8}$";
    public static final String REGEX_EMAIL_NEW = "^[0-9a-zA-Z_.-]+[@][0-9a-zA-Z_.-]+([.][a-zA-Z]+){1,2}$";

    public static boolean isMobile(String mobile) {
        Pattern p = Pattern.compile(REGEX_MOBILE);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }


    public static boolean isEmail(String mobile) {
        Pattern p = Pattern.compile(REGEX_EMAIL_NEW);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }


    public static boolean stringLength(String input,int limit) {
        return StringUtils.isNotBlank(input) && input.length() <= limit;
    }

    public static boolean match(String input,String regex) {
        if (StringUtils.isBlank(input) || StringUtils.isBlank(regex)) {
            return false;
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static void checkAlertGateVOFormat(AlertGateVO alertGateVO) {
        if (StringUtils.isNotBlank(alertGateVO.getAlertGateJson())) {
            try {
                JSONObject jsonObject = JSON.parseObject(alertGateVO.getAlertGateJson());
            } catch (Exception e) {
                throw new IllegalArgumentException("请输入JSON格式数据");
            }
        }
        Assert.isTrue(stringLength(alertGateVO.getAlertGateName(),32)
                        && match(alertGateVO.getAlertGateName(),"^\\S+$")
                ,"通道名称不能超过32字符且不包含空格");

        Assert.isTrue(stringLength(alertGateVO.getAlertGateSource(),32)
                        && match(alertGateVO.getAlertGateSource(),"^\\w+$")
                ,"通道标识只支持英文、字符、下划线，限制32个字符");

        String alertGateSource = alertGateVO.getAlertGateSource();
        AlertGateTypeEnum[] values = AlertGateTypeEnum.values();

        for (AlertGateTypeEnum alertGateTypeEnum : values) {
            String defaultFiled = AlertGateTypeEnum.getDefaultFiled(alertGateTypeEnum);
            if (alertGateSource.equals(defaultFiled)) {
                throw new IllegalArgumentException("通道标识不能为:"+defaultFiled);
            }
        }

        if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(alertGateVO.getAlertGateType()) && StringUtils.isBlank(alertGateVO.getAlertGateCode())) {
            alertGateVO.setAlertGateCode(AlertGateCode.AG_GATE_CUSTOM_JAR.code());
        }



        if (alertGateVO.getClusterId() == null) {
            //暂时默认为0
//            alertGateVO.setClusterId(0);
        }

    }

    public static void checkFormat(AlertGateTestVO alertGateTestVO) {
        if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(alertGateTestVO.getAlertGateType()) && StringUtils.isBlank(alertGateTestVO.getAlertGateCode())) {
            alertGateTestVO.setAlertGateCode(AlertGateCode.AG_GATE_CUSTOM_JAR.code());
        }

        if (alertGateTestVO.getAlertGateCode().contains(AlertGateTypeEnum.SMS.getValue())) {
            List<String> phones = alertGateTestVO.getPhones();
            Assert.isTrue(CollectionUtils.isNotEmpty(phones), "手机号列表不能为空");
            for (String phone : phones) {
                Assert.isTrue(CheckUtils.isMobile(phone), phone + "不符合手机号规则");

            }
        }
        if (alertGateTestVO.getAlertGateCode().contains(AlertGateTypeEnum.MAIL.getValue())) {
            List<String> emails = alertGateTestVO.getEmails();
            Assert.isTrue(CollectionUtils.isNotEmpty(emails), "邮箱列表不能为空");
            for (String email : emails) {
                Assert.isTrue(CheckUtils.isEmail(email), email + "不符合邮箱规则");
            }
        }
        checkAlertGateVOFormat(alertGateTestVO);
    }

    public static void checkAlertFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        Assert.isTrue(StringUtils.isNotBlank(originalFilename), "上传文件名称不能为空");
        Assert.isTrue(originalFilename.endsWith(".jar"), "上传文件必须是jar包");
        Assert.isTrue(!originalFilename.contains(File.separator) && !originalFilename.contains("\\"), "上传文件包含非法目录字符");
    }

    public static String handlerStr(String name) {
        if (StringUtils.isNotBlank(name)) {
            name = name.replaceAll("%", "\\\\%");
            name = name.replaceAll("'", "\\\\'");
        }
        return name;
    }

    public static String handName(Integer searchType, String taskName) {
        if (com.dtstack.engine.common.util.StringUtils.isBlank(taskName) || searchType == null) {
            return taskName;
        }

        if (!SearchTypeEnum.PRECISE.getType().equals(searchType)) {
            return taskName.trim().replaceAll("\\s", "").replace("\\", "\\\\\\\\")
                    .replace("_", "\\_").replace("\'", "\\'")
                    .replace("%", "\\%").replace("*", "\\*");
        }
        return taskName;
    }

}
