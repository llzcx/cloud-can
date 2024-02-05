package ccw.serviceinnovation.node.server.http;
 
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
 
public class MyChannelInitalizer extends ChannelInitializer<SocketChannel> {
 
    protected void initChannel(SocketChannel ch) throws Exception {
        // 获取管道
        ChannelPipeline pipeline = ch.pipeline();
 
        // 设置编码解码处理器
        pipeline.addLast("MyHttpResponseEncoder", new HttpResponseEncoder());
        pipeline.addLast("MyHttpRequestDecoder", new HttpRequestDecoder());
        // 设置自定义处理器
        pipeline.addLast("MyHttpServerHandler", new MyHttpServerHandler());
    }
}