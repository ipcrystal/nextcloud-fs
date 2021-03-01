package com.winterfell.nextcloud.filesystem.framework;

import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhuzhenjie
 */
public interface FileSystem {

    /**
     * 创建文件夹
     *
     * @param folder 文件夹的绝对目录
     * @return 是否创建成功
     */
    boolean createFolder(String folder);

    /**
     * 创建文件夹
     *
     * @param parentFolder 父文件夹的绝对目录
     * @param folder       文件夹名称 或 文件夹的目录
     * @return 是否创建成功
     */
    boolean createFolder(String parentFolder, String folder);

    /**
     * 删除文件夹 或 文件
     *
     * @param path 文件或文件夹的绝对路径
     * @return 是否删除成功
     */
    boolean delete(String path);

    /**
     * 上传文件
     *
     * @param folder      文件夹
     * @param fileName    文件名称
     * @param inputStream 输入流
     * @param overwrite   是否覆盖原来的文件
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String upload(String folder, String fileName, InputStream inputStream, boolean overwrite);

    /**
     * 上传文件 （如果上传的路径下有同样名称的文件，默认覆盖）
     *
     * @param folder      文件夹
     * @param fileName    文件名
     * @param inputStream 输入流
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String upload(String folder, String fileName, InputStream inputStream);

    /**
     * 上传文件 （如果上传的路径下有同样名称的文件，默认覆盖）
     *
     * @param fileToPath  上传文件的绝对路径
     * @param inputStream 上传的文件流
     * @param overwrite   是否覆盖原来的文件
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String upload(String fileToPath, InputStream inputStream, boolean overwrite);

    /**
     * 上传文件 （如果上传的路径下有同样名称的文件，默认覆盖）
     *
     * @param fileToPath  上传文件的绝对路径
     * @param inputStream 上传的文件流
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String upload(String fileToPath, InputStream inputStream);

    /**
     * 上传文件
     *
     * @param folder      文件夹
     * @param fileName    文件名称
     * @param inputStream 输入流
     * @param overwrite   是否覆盖原来的文件
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String uploadAndReturnSimpleUrl(String folder, String fileName, InputStream inputStream, boolean overwrite);

    /**
     * 上传文件 （如果上传的路径下有同样名称的文件，默认覆盖）
     *
     * @param folder      文件夹
     * @param fileName    文件名
     * @param inputStream 输入流
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String uploadAndReturnSimpleUrl(String folder, String fileName, InputStream inputStream);

    /**
     * 上传文件 （如果上传的路径下有同样名称的文件，默认覆盖）
     *
     * @param fileToPath  上传文件的绝对路径
     * @param inputStream 上传的文件流
     * @param overwrite   是否覆盖原来的文件
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String uploadAndReturnSimpleUrl(String fileToPath, InputStream inputStream, boolean overwrite);

    /**
     * 上传文件 （如果上传的路径下有同样名称的文件，默认覆盖）
     *
     * @param fileToPath  上传文件的绝对路径
     * @param inputStream 上传的文件流
     * @return url 如果是图片返回一个预览地址，如果是文件返回一个下载地址
     */
    String uploadAndReturnSimpleUrl(String fileToPath, InputStream inputStream);


    /**
     * 查询文件
     *
     * @param folder 文件夹
     * @param name   文件名称（可以是文件夹，也可以是文件）
     * @return true: 文件存在，false: 文件不存在
     */
    boolean search(String folder, String name);

    /**
     * 获取文件的url
     *
     * @param folder 文件夹路径
     * @param name   文件名称
     * @return 文件url （如果有返回，如果没有返回null）
     */
    String getUrl(String folder, String name);

    /**
     * 获取文件的url
     *
     * @param fileToPath 文件绝对路径
     * @return 文件url （如果有返回，如果没有返回null）
     */
    String getUrl(String fileToPath);

    /**
     * 获取简单的url
     *
     * @param folder 文件夹路径
     * @param name   文件名称
     * @return 文件url 简单的文件url
     */
    String getSimpleUrl(String folder, String name);

    /**
     * 获取简单的url
     *
     * @param fileToPath 文件绝对路径
     * @return 文件url （如果有返回，如果没有返回null）
     */
    String getSimpleUrl(String fileToPath);

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     *
     * @throws IOException
     */
    void stop();

    /**
     * AsyncHttpClient
     *
     * @return AsyncHttpClient
     */
    AsyncHttpClient getClient();

}
