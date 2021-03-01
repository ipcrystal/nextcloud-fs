package com.winterfell.starter.nextcloud.filesystem.starter;

import com.winterfell.nextcloud.filesystem.framework.FileSystem;
import com.winterfell.nextcloud.filesystem.framework.NextCloudFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * @author zhuzhenjie
 **/
@Configuration
@ConditionalOnClass(FileSystem.class)
@EnableConfigurationProperties(NextcloudConfigProperties.class)
public class EnableFileSystemAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(EnableFileSystemAutoConfiguration.class);

    @Autowired
    NextcloudConfigProperties nextcloudConfigProperties;

    @Bean
    @ConditionalOnProperty(prefix = "filesystem.nextcloud",name = {"url","username","password"})
    @ConditionalOnMissingBean(FileSystem.class)
    public FileSystem fileSystem() {
        LOG.info("Init bean FileSystem");
        return new NextCloudFileSystem(
                nextcloudConfigProperties.getNextcloud()
        );
    }

}
