package ccw.serviceinnovation.node.server.http;
 
 
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
 
 
public class MyHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
 
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            System.out.println("msg的真实类型" + msg.getClass());
            System.out.println("客服端地址" + ctx.channel().remoteAddress());
            // 回复信息给浏览器
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello 我是自定义的Http服务器, 我正在服务.....", CharsetUtil.UTF_8);
            // 构造一个http响应体，即HttpResponse
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            // 设置响应头信息
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf8");
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            // 将响应体写入到通道中
            ctx.writeAndFlush(defaultFullHttpResponse);
 
        }
    }
}