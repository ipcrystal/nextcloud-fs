package com.winterfell.nextcloud.filesystem.stream.transfer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;

import static com.winterfell.nextcloud.filesystem.stream.transfer.HttpServer.asyncHttpClient;
import static com.winterfell.nextcloud.filesystem.stream.transfer.HttpServer.fileSystem;

/**
 * @author zhuzhenjie
 **/
@ChannelHandler.Sharable
public class HttpTransferHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HttpTransferHandler.class);

    private static final String DEFAULT_RESULT = StringUtils.EMPTY;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        FullHttpResponse response;
        URI uri = new URI(request.uri());
        if ("/favicon.ico".equals(uri.getPath())) {
            response = defaultResponse();
            ctx.writeAndFlush(response);
            return;
        }

        if ("/".equals(uri.getPath())) {
            ByteBuf content = Unpooled.copiedBuffer("This is stream transfer service !", CharsetUtil.UTF_8);
            FullHttpResponse slashResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    content);
            slashResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            ctx.writeAndFlush(slashResponse);
            return;
        }

        String picPath = uri.getPath();
        logger.info("path {}", picPath);
        String pic = fileSystem.getUrl(picPath);
        logger.info("url {}", pic);
        if (Objects.isNull(pic)) {
            response = defaultResponse();
            ctx.writeAndFlush(response);
            return;
        }
        ListenableFuture<Response> execute = asyncHttpClient.prepareGet(pic).execute();
        ByteBuf content = Unpooled.copiedBuffer(execute.get().getResponseBodyAsByteBuffer());
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("exception caught ... ");
        logger.error(cause.getMessage());
        ctx.close();
    }

    private FullHttpResponse defaultResponse() {
        ByteBuf content = Unpooled.copiedBuffer(DEFAULT_RESULT, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                content);

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        return response;
    }
}
