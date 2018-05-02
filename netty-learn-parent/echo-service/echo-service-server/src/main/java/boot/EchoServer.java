package boot;

import handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by bricks on 2018/5/1.
 */
public class EchoServer {
    private final int port;
    public EchoServer(int port){
        this.port = port;
    }

    public void start() throws Exception{
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception{
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println(EchoServer.class.getName() +
                    " started and listening for connections on" + future.channel().localAddress());
            future.channel().closeFuture().sync();

        }finally {
            {
                group.shutdownGracefully().sync();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        if(args.length != 1){
            System.err.println("Usage :" + EchoServer.class.getSimpleName()
            + " <port>");
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();

    }
}
