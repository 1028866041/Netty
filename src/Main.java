import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Administrator on 2018/9/15 0015.
 */
public class Main {

    public static void main(String[] args){
        EventLoopGroup boss= new NioEventLoopGroup();
        EventLoopGroup work= new NioEventLoopGroup();

        try{
            ServerBootstrap b= new ServerBootstrap();
            b.group(boss, work);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new Handler());
            Channel ch= b.bind(8888).sync().channel();
            ch.closeFuture().sync();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }
}
