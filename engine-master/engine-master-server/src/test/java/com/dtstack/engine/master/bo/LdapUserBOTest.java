package com.dtstack.engine.master.bo;

import org.junit.Test;

import static org.junit.Assert.*;

public class LdapUserBOTest {

    private LdapUserBO ldapUserBO = new LdapUserBO();

    @Test
    public void getLdapUserName() {
        LdapUserBO build = LdapUserBO.LdapUserBOBuilder.builder()
                .ldapPassWord("password")
                .ldapUserName("username")
                .build();
        String ldapUserName = build.getLdapUserName();
        String ldapPassWord = build.getLdapPassWord();
        build.setLdapUserName("");
        build.setLdapPassWord("");
    }

}