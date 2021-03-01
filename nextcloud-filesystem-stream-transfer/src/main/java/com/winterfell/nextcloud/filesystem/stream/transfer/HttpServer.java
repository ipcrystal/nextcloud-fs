package com.winterfell.nextcloud.filesystem.stream.transfer;

import com.winterfell.nextcloud.filesystem.framework.FileSystem;
import com.winterfell.nextcloud.filesystem.framework.NextCloudConfig;
import com.winterfell.nextcloud.filesystem.framework.NextCloudFileSystem;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhuzhenjie
 **/
public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    static FileSystem fileSystem;

    static AsyncHttpClient asyncHttpClient;

    private static int port;

    public static void start() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpRequestDecoder())
                                    .addLast(new HttpResponseEncoder());
                            pipeline.addLast(new HttpTransferHandler());
                        }
                    });
            logger.info("start next cloud file system success !!!");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    static void init(String url, String username, String password, int transferPort) {
        try {
            logger.info("init file system!!!");
            fileSystem = new NextCloudFileSystem(
                    new NextCloudConfig() {
                        {
                            this.setUrl(url);
                            this.setUsername(username);
                            this.setPassword(password);
                        }
                    }
            );
            logger.info("init file system!!!");
            asyncHttpClient = fileSystem.getClient();
            port = transferPort;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
