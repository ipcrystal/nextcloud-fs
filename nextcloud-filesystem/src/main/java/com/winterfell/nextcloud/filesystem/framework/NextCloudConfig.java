package com.winterfell.nextcloud.filesystem.framework;

/**
 * NextCloud配置
 *
 * @author zhuzhenjie
 */
public class NextCloudConfig {

    /**
     * ip:port
     */
    private String url;

    private String username;

    private String password;

    /**
     * ip:port
     */
    private String simpleUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSimpleUrl() {
        return simpleUrl;
    }

    public void setSimpleUrl(String simpleUrl) {
        this.simpleUrl = simpleUrl;
    }
}
