package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.vo.alert.AlertGateTestVO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckUtilsTest {

    @Test
    public void isMobile() {
        CheckUtils.isMobile("11111111111");
    }

    @Test
    public void isEmail() {
        CheckUtils.isEmail("111@111.com");
    }

    @Test
    public void stringLength() {
        CheckUtils.stringLength("123",3);
    }

    @Test
    public void match() {
        CheckUtils.match("$dsd","^\\S+$");
    }

    @Test
    public void checkAlertGateVOFormat() {
        AlertGateVO alertGateVO = new AlertGateVO();
        alertGateVO.setAlertGateCode("mail_jar");
        alertGateVO.setAlertGateJson("{\"className\":\"com.dtstack.sdk.example.IMailChannelExample\"}");
        alertGateVO.setAlertGateSource("dz_mail_k");
        alertGateVO.setFilePath("/home/admin/app/dt-center-engine/upload/normal/dz_mail_k/console-alert-plugin-sdk-example-4.0.0.jar&sftp:/data/sftp//home/admin/app/dt-center-engine/upload/normal/dz_mail_k/console-alert-plugin-sdk-example-4.0.0.jar");
        alertGateVO.setAlertGateName("大智冒烟-邮件-扩展");
        CheckUtils.checkAlertGateVOFormat(alertGateVO);
    }

    @Test
    public void checkFormat() {
        AlertGateTestVO vo = new AlertGateTestVO();
        vo.setEmails(Lists.newArrayList("123@123.com"));
        vo.setPhones(Lists.newArrayList("11111111111"));
        vo.setAlertGateType(4);
        vo.setAlertGateName("大智冒烟-邮件-扩展");
        vo.setAlertGateCode("mail_jar");
        vo.setAlertGateSource("dz_mail_k");
        CheckUtils.checkFormat(vo);
    }
}