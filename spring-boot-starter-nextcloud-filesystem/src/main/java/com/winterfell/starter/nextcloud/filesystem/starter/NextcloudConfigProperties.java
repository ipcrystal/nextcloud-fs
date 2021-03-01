package com.winterfell.starter.nextcloud.filesystem.starter;

import com.winterfell.nextcloud.filesystem.framework.NextCloudConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhuzhenjie
 **/
@ConfigurationProperties(
        prefix = "filesystem"
)
public class NextcloudConfigProperties {

    private NextCloudConfig nextcloud;

    public NextCloudConfig getNextcloud() {
        return nextcloud;
    }

    public void setNextcloud(NextCloudConfig nextcloud) {
        this.nextcloud = nextcloud;
    }
}
