package com.winterfell.nextcloud.filesystem.framework;

import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;

/**
 * @author zhuzhenjie
 */
public abstract class AbstractFileSystem implements FileSystem, ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(AbstractFileSystem.class);

    static final String SLASH = "/";

    /**
     * preview suffix
     */
    static final String PREVIEW_SUFFIX = "/preview";

    /**
     * download suffix
     */
    static final String DOWNLOAD_SUFFIX = "/download";

    protected static final DateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    protected RuntimeEnv runtimeEnv;

    protected AsyncHttpClient client;

    /**
     * 默认重写
     */
    private boolean defaultOverwrite = true;

    public AbstractFileSystem(NextCloudConfig config) {
        client = new DefaultAsyncHttpClient();
        this.runtimeEnv = this.load(config);
    }

    @Override
    public boolean createFolder(String parentFolder, String folder) {
        if (StringUtils.isBlank(parentFolder) || StringUtils.isBlank(folder)) {
            return false;
        }
        parentFolder = beautifyFolder(parentFolder);
        // /mysql/data -> mysql/data
        if (folder.startsWith(SLASH)) {
            folder = folder.substring(SLASH.length());
        }
        String folder0 = parentFolder + folder;
        return this.createFolder(folder0);
    }

    @Override
    public boolean delete(String path) {
        Request deleteFileRequest = baseRequestBuilder(WebDavMethod.DELETE)
                .setUrl(
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getBasicDav()
                                + runtimeEnv.getUsername()
                                + path
                )
                .build();
        try {
            ListenableFuture<Response> responseListenableFuture = client.executeRequest(deleteFileRequest);

            Response deleteResponse = responseListenableFuture.get();
            if (StringUtils.isBlank(deleteResponse.getResponseBody())) {
                log.info("DELETE FILE (or FOLDER) {} SUCCESS !!!" + path);
                return true;
            } else {
                log.info("DELETE FILE (or FOLDER) {} FAILED !!!" + path);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 文件上传
     *
     * @param folder      文件夹
     * @param fileName    文件名
     * @param inputStream 流
     * @param overwrite   是否重写
     * @param simple      是否是简单url
     * @return url
     */
    protected abstract String upload(String folder, String fileName, InputStream inputStream, boolean overwrite, boolean simple);

    @Override
    public String upload(String folder, String fileName, InputStream inputStream, boolean overwrite) {
        return this.upload(folder, fileName, inputStream, overwrite, false);
    }

    @Override
    public String upload(String folder, String fileName, InputStream inputStream) {
        // 默认不重写
        return this.upload(folder, fileName, inputStream, defaultOverwrite);
    }

    @Override
    public String upload(String fileToPath, InputStream inputStream, boolean overwrite) {
        if (fileToPath.endsWith(SLASH)) {
            fileToPath = fileToPath.substring(0, fileToPath.length() - SLASH.length());
        }
        int lastSlash = fileToPath.lastIndexOf(SLASH);
        String folder = fileToPath.substring(0, lastSlash + 1);
        String fileName = fileToPath.substring(lastSlash + 1);
        return this.upload(folder, fileName, inputStream, overwrite);
    }

    @Override
    public String upload(String fileToPath, InputStream inputStream) {
        return this.upload(fileToPath, inputStream, defaultOverwrite);
    }

    @Override
    public String uploadAndReturnSimpleUrl(String folder, String fileName, InputStream inputStream, boolean overwrite) {
        return this.upload(folder, fileName, inputStream, overwrite, true);
    }

    @Override
    public String uploadAndReturnSimpleUrl(String folder, String fileName, InputStream inputStream) {
        return this.uploadAndReturnSimpleUrl(folder, fileName, inputStream, defaultOverwrite);
    }

    @Override
    public String uploadAndReturnSimpleUrl(String fileToPath, InputStream inputStream, boolean overwrite) {
        if (fileToPath.endsWith(SLASH)) {
            fileToPath = fileToPath.substring(0, fileToPath.length() - SLASH.length());
        }
        int lastSlash = fileToPath.lastIndexOf(SLASH);
        String folder = fileToPath.substring(0, lastSlash + 1);
        String fileName = fileToPath.substring(lastSlash + 1);
        return this.uploadAndReturnSimpleUrl(folder, fileName, inputStream, overwrite);
    }

    @Override
    public String uploadAndReturnSimpleUrl(String fileToPath, InputStream inputStream) {
        return this.uploadAndReturnSimpleUrl(fileToPath, inputStream, defaultOverwrite);
    }

    @Override
    public boolean search(String folder, String name) {
        folder = beautifyFolder(folder);
        Request request = baseRequestBuilder(WebDavMethod.SEARCH)
                .addHeader("Content-Type", "text/xml")
                .setBody(
                        runtimeEnv.getSearchContentTemplate()
                                .replace("{username}", runtimeEnv.getUsername())
                                .replace("{folder}", folder)
                                .replace("{name}", name)
                )
                .setUrl(
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getBasicDav().replace("files/", StringUtils.EMPTY)
                )
                .build();
        ListenableFuture<Response> responseListenableFuture = client.executeRequest(request);
        try {
            Response response = responseListenableFuture.get();
            SAXReader reader = new SAXReader();
            Document document = reader.read(response.getResponseBodyAsStream());
            Element root = document.getRootElement();
            if ("error".equals(root.getName())) {
                return false;
            } else if ("multistatus".equals(root.getName())) {
                List elements = root.elements();
                if (elements.size() <= 0) {
                    return false;
                } else {
                    Element responseElmt = root.element("response");
                    Element hrefElmt = responseElmt.element("href");
                    String href = hrefElmt.getTextTrim();
                    if (href.contains(folder + name)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public String getUrl(String folder, String name) {
        folder = beautifyFolder(folder);
        return getUrl(folder + name);
    }

    @Override
    public String getUrl(String fileToPath) {
        String suffix = FileUtils.isPicture(fileToPath) ? PREVIEW_SUFFIX : DOWNLOAD_SUFFIX;
        String sharedUrl = getShare(fileToPath, false);
        return StringUtils.isBlank(sharedUrl) ? null : sharedUrl + suffix;
    }

    @Override
    public String getSimpleUrl(String folder, String name) {
        folder = beautifyFolder(folder);
        return getSimpleUrl(folder + name);
    }

    @Override
    public String getSimpleUrl(String fileToPath) {
        return StringUtils.isEmpty(getUrl(fileToPath)) ?
                null :
                runtimeEnv.getSimpleUrl() + fileToPath;
    }

    @Override
    public RuntimeEnv load(NextCloudConfig config) {
        // 运行时的环境
        return new RuntimeEnv() {
            {
                this.setUsername(config.getUsername());
                this.setBaseUrl("http://" + config.getUrl());
                this.setSimpleUrl("http://" + config.getSimpleUrl());
                String auth = config.getUsername() + ":" + config.getPassword();
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                String authHeader = "Basic " + encodedAuth;
                this.setAuthHeader(authHeader);
            }
        };
    }

    @Override
    public void start() {
        client = new DefaultAsyncHttpClient();
    }

    @Override
    public void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    protected RequestBuilder baseRequestBuilder(String method) {
        return new RequestBuilder(method)
                .addHeader(RuntimeEnv.AUTH_HEADER_NAME, runtimeEnv.getAuthHeader());
    }

    protected String beautifyFolder(String folder) {
        // usr/local -> /usr/local
        if (!folder.startsWith(SLASH)) {
            folder = SLASH + folder;
        }
        // /usr/local -> /usr/local/
        if (!folder.endsWith(SLASH)) {
            folder = folder + SLASH;
        }
        return folder;
    }

    /**
     * 创建分享的路径
     *
     * @param path 绝对路径
     * @return 分享路径
     */
    protected String share(String path) {
        Request request = baseRequestBuilder(WebDavMethod.POST)
                .addHeader("OCS-APIRequest", "true")
                .addFormParam("shareType", runtimeEnv.getBasicShareType())
                .addFormParam("path", path)
                .setUrl(
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getShareBasicApi()
                )
                .build();

        ListenableFuture<Response> shareFuture = client.executeRequest(request);
        try {
            Response shareResponse = shareFuture.get();
            SAXReader reader = new SAXReader();
            Document share = reader.read(shareResponse.getResponseBodyAsStream());
            Element root = share.getRootElement();
            Element data = root.element("data");
            Element url = data.element("url");
            log.info("Create shared link for {} . Shared links is {}", path, url.getTextTrim());
            return url.getTextTrim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取分享路径 如果有则获取
     *
     * @param path             绝对路径
     * @param shareIfNotExists 如果没有 创建分享分享
     * @return 分享链接
     */
    protected String getShare(String path, boolean shareIfNotExists) {
        Request request = baseRequestBuilder(WebDavMethod.GET)
                .addHeader("OCS-APIRequest", "true")
                .addQueryParam("path", path)
                .setUrl(
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getShareBasicApi()
                )
                .build();
        ListenableFuture<Response> getShareFuture = client.executeRequest(request);
        try {
            Response getShareRes = getShareFuture.get();
            SAXReader reader = new SAXReader();
            Document document = reader.read(getShareRes.getResponseBodyAsStream());
            Element root = document.getRootElement();
            Element data = root.element("data");
            List elements = data.elements();
            // 如果没有则分享
            if (elements.size() <= 0) {
                if (shareIfNotExists) {
                    return share(path);
                } else {
                    log.info("Do not have {} shared link and do not create !!!", path);
                    return null;
                }
            } else {
                Element element = (Element) elements.get(0);
                return element.element("url").getTextTrim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AsyncHttpClient getClient() {
        return client;
    }
}
