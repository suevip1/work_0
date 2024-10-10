package com.dtstack.engine.master.bo;

/**
 * @author leon
 * @date 2022-09-23 11:03
 **/
public class LdapUserBO {

    /**
     * ldap用户名
     */
    private String ldapUserName;

    /**
     * ldap用户组名
     */
    private String ldapGroupName;

    /**
     * ldap密码
     */
    private String ldapPassWord;


    public String getLdapUserName() {
        return ldapUserName;
    }

    public void setLdapUserName(String ldapUserName) {
        this.ldapUserName = ldapUserName;
    }

    public String getLdapPassWord() {
        return ldapPassWord;
    }

    public void setLdapPassWord(String ldapPassWord) {
        this.ldapPassWord = ldapPassWord;
    }

    public String getLdapGroupName() {
        return ldapGroupName;
    }

    public void setLdapGroupName(String ldapGroupName) {
        this.ldapGroupName = ldapGroupName;
    }

    public static final class LdapUserBOBuilder {
        private String ldapUserName;
        private String ldapPassWord;

        private String ldapGroupName;

        private LdapUserBOBuilder() {
        }

        public static LdapUserBOBuilder builder() {
            return new LdapUserBOBuilder();
        }

        public LdapUserBOBuilder ldapUserName(String ldapUserName) {
            this.ldapUserName = ldapUserName;
            return this;
        }

        public LdapUserBOBuilder ldapPassWord(String ldapPassWord) {
            this.ldapPassWord = ldapPassWord;
            return this;
        }

        public LdapUserBOBuilder ldapGroupName(String ldapGroupName) {
            this.ldapGroupName = ldapGroupName;
            return this;
        }

        public LdapUserBO build() {
            LdapUserBO ldapUserBO = new LdapUserBO();
            ldapUserBO.setLdapUserName(ldapUserName);
            ldapUserBO.setLdapPassWord(ldapPassWord);
            ldapUserBO.setLdapGroupName(ldapGroupName);
            return ldapUserBO;
        }
    }
}
