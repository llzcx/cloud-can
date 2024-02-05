package ccw.serviceinnovation.node.server.http;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class HttpServer {
    public static void start(){
        // boss工作组
        NioEventLoopGroup bossGroup = null;
        // worker工作组
        NioEventLoopGroup workerGroup = null;
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();

            // 设置启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 配置启动对象
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MyChannelInitalizer());
            // 同步绑定端口
            ChannelFuture cf = bootstrap.bind(RegisterConstant.HTTP_PORT).sync();

            // 绑定监听关闭状态
            cf.channel().closeFuture().sync();
            System.out.println("Started http server at port:"+RegisterConstant.HTTP_PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
        }
    }

}
