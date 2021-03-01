package com.winterfell.nextcloud.filesystem.framework;

/**
 * @author zhuzhenjie
 */
public class RuntimeEnv {

    static final String AUTH_HEADER_NAME = "Authorization";

    private String baseUrl;

    private String simpleUrl;

    private String authHeader;

    private String username;

    private String basicDav = "/remote.php/dav/files/";

    /**
     * 分享的基础api
     */
    private String shareBasicApi = "/ocs/v2.php/apps/files_sharing/api/v1/shares";

    private String basicShareType = "3";

    /**
     * 查询文件夹要用的东西
     */
    private String propfindContent = "<?xml version=\"1.0\"?>" +
            "<d:propfind  xmlns:d=\"DAV:\" xmlns:oc=\"http://owncloud.org/ns\" xmlns:nc=\"http://nextcloud.org/ns\">" +
            "  <d:prop>" +
            "        <d:getlastmodified />" +
            "        <d:getetag />" +
            "        <d:getcontenttype />" +
            "        <d:resourcetype />" +
            "        <oc:fileid />" +
            "        <oc:permissions />" +
            "        <oc:size />" +
            "        <d:getcontentlength />" +
            "        <nc:has-preview />" +
            "        <oc:favorite />" +
            "        <oc:comments-unread />" +
            "        <oc:owner-display-name />" +
            "        <oc:share-types />" +
            "  </d:prop>" +
            "</d:propfind>";

    private String searchContentTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<d:searchrequest xmlns:d=\"DAV:\" xmlns:oc=\"http://owncloud.org/ns\">" +
            "    <d:basicsearch>" +
            "        <d:select>" +
            "            <d:prop>" +
            "                <d:resourcetype/>" +
            "            </d:prop>" +
            "        </d:select>" +
            "        <d:from>" +
            "            <d:scope>" +
            "                <d:href>/files/{username}{folder}</d:href>" +
            "            </d:scope>" +
            "        </d:from>" +
            "        <d:where>" +
            "            <d:like>" +
            "                <d:prop>" +
            "                    <d:displayname/>" +
            "                </d:prop>" +
            "                <d:literal>{name}</d:literal>" +
            "            </d:like>" +
            "        </d:where>" +
            "        <d:orderby>" +
            "            <d:prop>" +
            "                <oc:size/>" +
            "            </d:prop>" +
            "            <d:ascending/>" +
            "        </d:orderby>" +
            "    </d:basicsearch>" +
            "</d:searchrequest>";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSimpleUrl() {
        return simpleUrl;
    }

    public void setSimpleUrl(String simpleUrl) {
        this.simpleUrl = simpleUrl;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBasicDav() {
        return basicDav;
    }

    public void setBasicDav(String basicDav) {
        this.basicDav = basicDav;
    }

    public String getShareBasicApi() {
        return shareBasicApi;
    }

    public void setShareBasicApi(String shareBasicApi) {
        this.shareBasicApi = shareBasicApi;
    }

    public String getBasicShareType() {
        return basicShareType;
    }

    public void setBasicShareType(String basicShareType) {
        this.basicShareType = basicShareType;
    }

    public String getPropfindContent() {
        return propfindContent;
    }

    public String getSearchContentTemplate() {
        return searchContentTemplate;
    }
}
