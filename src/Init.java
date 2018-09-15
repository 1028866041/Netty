import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by Administrator on 2018/9/15 0015.
 */
public class Init extends ChannelInitializer<SocketChannel>{

    public void initChannel(SocketChannel e) throws Exception{
        e.pipeline().addLast("", new HttpServerCodec());
        e.pipeline().addLast("aggregator",new HttpObjectAggregator(65535));
        e.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        e.pipeline().addLast("handler", new Handler());
    }
}
