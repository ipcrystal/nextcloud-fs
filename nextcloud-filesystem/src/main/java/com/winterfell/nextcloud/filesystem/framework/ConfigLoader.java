package com.winterfell.nextcloud.filesystem.framework;

/**
 * @author zhuzhenjie
 */
public interface ConfigLoader {

    /**
     * 加载配置初始化一些内容
     *
     * @param config 配置
     * @return 运行时的环境
     */
    RuntimeEnv load(NextCloudConfig config);

}
