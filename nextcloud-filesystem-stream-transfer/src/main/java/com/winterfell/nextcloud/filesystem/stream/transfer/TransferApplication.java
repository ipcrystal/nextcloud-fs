package com.winterfell.nextcloud.filesystem.stream.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * @author zhuzhenjie
 **/
@SpringBootApplication
public class TransferApplication implements CommandLineRunner, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(TransferApplication.class);

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        new SpringApplication(TransferApplication.class).run(args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {

        Environment environment = applicationContext.getEnvironment();
        String url = environment.getProperty("nextcloud.url");
        log.info("old url {} ", url);
        String hostName = url.split(":")[0];
        String hostPort = url.split(":")[1];
        String hostAddress = InetAddress.getByName(hostName).getHostAddress();
        url = hostAddress + ":" + hostPort;
        String username = environment.getProperty("nextcloud.username");
        String password = environment.getProperty("nextcloud.password");
        int port = Integer.parseInt(environment.getProperty("transfer.port"));
        HttpServer.init(url, username, password, port);
        HttpServer.start();
    }

}
