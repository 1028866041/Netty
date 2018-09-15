import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by Administrator on 2018/9/15 0015.
 */


public class Config {

    public static ChannelGroup group=  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
