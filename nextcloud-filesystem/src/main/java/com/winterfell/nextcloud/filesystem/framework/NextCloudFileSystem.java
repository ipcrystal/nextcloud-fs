package com.winterfell.nextcloud.filesystem.framework;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author zhuzhenjie
 */
public class NextCloudFileSystem extends AbstractFileSystem {

    private static final Logger log = LoggerFactory.getLogger(NextCloudConfig.class);

    public NextCloudFileSystem(NextCloudConfig config) {
        super(config);
    }

    @Override
    public boolean createFolder(String folder) {
        folder = beautifyFolder(folder);
        // 判断上层是否存在
        String parentFolder = getParentFolder(folder);
        if (!SLASH.equals(parentFolder)) {
            if (!exists(parentFolder)) {
                // 递归创建不存在的父文件夹
                createFolder(parentFolder);
            }
        }
        // 查询文件夹是否存在
        if (!exists(folder)) {
            log.info("文件夹 {} folder 不存在 正在创建 !!!", folder);
            Request request = baseRequestBuilder(WebDavMethod.MKCOL)
                    .setUrl(
                            runtimeEnv.getBaseUrl()
                                    + runtimeEnv.getBasicDav()
                                    + runtimeEnv.getUsername()
                                    + folder
                    )
                    .build();
            ListenableFuture<Response> createFuture = client.executeRequest(request);
            try {
                Response createResponse = createFuture.get();
                String createResponseBody = createResponse.getResponseBody();
                if (StringUtils.isBlank(createResponseBody)) {
                    log.info("FOLDER {} create success ! ! !", folder);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("文件夹 {} folder 已存在 无需创建 !!!", folder);
        return false;
    }

    private String getParentFolder(String folder) {
        // /usr/local/ -> /usr/
        // / -> /
        folder = folder.substring(0, folder.length() - 1);
        int lastSlash = folder.lastIndexOf(SLASH);
        if (lastSlash < 0) {
            return SLASH;
        }
        return folder.substring(0, lastSlash + 1);
    }

    /**
     * 判断是否存在
     *
     * @param folder folder
     * @return 如果存在 返回true
     */
    private boolean exists(String folder) {
        SAXReader reader = new SAXReader();
        Request request = baseRequestBuilder(WebDavMethod.PROPFIND)
                .addHeader("Depth", "1")
                .setUrl(
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getBasicDav()
                                + runtimeEnv.getUsername()
                                + folder
                )
                .build();

        ListenableFuture<Response> folderFindResponseFuture = client.executeRequest(request);
        try {
            Response folderFindResponse = folderFindResponseFuture.get();
            Document document = reader.read(folderFindResponse.getResponseBodyAsStream());
            String rootEltName = document.getRootElement().getName();
            if ("error".equals(rootEltName)) {
                log.info("FOLDER {} does not exists ! ! !", folder);
                return false;
            } else if ("multistatus".equals(rootEltName)) {
                log.info("FOLDER {} exists ! ! !", folder);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    protected String upload(String folder, String fileName, InputStream inputStream, boolean overwrite, boolean simple) {

        folder = beautifyFolder(folder);
        String path = folder + fileName;
        // 如果文件夹不存在 就要先创建文件夹，如果文件夹存在就不用管 利用createFolder
        this.createFolder(folder);
        // 后缀
        String suffix = FileUtils.isPicture(fileName) ? PREVIEW_SUFFIX : DOWNLOAD_SUFFIX;

        boolean search = search(folder, fileName);

        if (search && overwrite) {
            // 原本文件存在 并覆盖 （覆盖 <=> 删除原来的文件 ，导致 原来的文件不存在 直接上传分享 ）

            // 删除原来的文件
            this.delete(path);

        } else if (search && !overwrite) {
            // 原本文件存在 不覆盖
            // 转移原文件 上传新文件 获取新文件的url
            // 转移原文件 test.jpg -> test20190101005959.jpg
            String fileSuffix = FileUtils.getFileSuffix(fileName);
            String newFileName = new StringBuilder()
                    .append(fileName.substring(0, fileName.length() - fileSuffix.length()))
                    .append(FORMAT.format(new Date()))
                    .append(fileSuffix)
                    .toString();
            // 转移
            move((folder + fileName), (folder + newFileName));
        }

        // 原来的文件不存在 直接上传分享
        // 上传
        byte[] body = null;
        try {
            try {
                if (inputStream == null) {
                    log.error("input stream cannot null");
                    return null;
                }
                body = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Request request = baseRequestBuilder(WebDavMethod.PUT)
                .setUrl(
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getBasicDav()
                                + runtimeEnv.getUsername()
                                + folder
                                + fileName
                )
                .setBody(body)
                .build();
        ListenableFuture<Response> responseListenableFuture = client.executeRequest(request);
        try {
            Response response = responseListenableFuture.get();
            String resBody = response.getResponseBody();
            if (StringUtils.isBlank(resBody)) {
                log.info("upload {} success", fileName);
                // 分享
                String url = share(path) + suffix;
                return simple ?
                        runtimeEnv.getSimpleUrl() + folder + fileName
                        :
                        url;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 移动文件
     *
     * @param src  源绝对路径
     * @param dest 目标绝对路径
     */
    private void move(String src, String dest) {
        Request request = baseRequestBuilder(WebDavMethod.MOVE)
                .setHeader(
                        "Destination",
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getBasicDav()
                                + runtimeEnv.getUsername()
                                + dest
                )
                .setUrl(
                        runtimeEnv.getBaseUrl()
                                + runtimeEnv.getBasicDav()
                                + runtimeEnv.getUsername()
                                + src
                )
                .build();
        try {
            client.executeRequest(request).get();
            log.info("MOVE {} to {}", src, dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
