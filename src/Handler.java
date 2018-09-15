import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * Created by Administrator on 2018/9/15 0015.
 */
@ChannelHandler.Sharable
public class Handler extends SimpleChannelInboundHandler<Object>{

    private WebSocketServerHandshaker handshaker;

    private static final String URL= "ws://localhost:8888";

    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {

    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        //super.channelActive(ctx);
        Config.group.add(ctx.channel());
        System.out.println("channelActive");
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        //super.channelInactive(ctx);
        Config.group.remove(ctx.channel());
        System.out.println("channelInactive");
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
       //super.exceptionCaught(ctx, cause);
       cause.printStackTrace();
       ctx.close();
    }

    protected void messgaeReceived(ChannelHandlerContext ctx,Object msg) throws Exception {

            if(msg instanceof FullHttpRequest){
                //client to server request:
                handHttpRequest(ctx, (FullHttpRequest)msg);

            }else if(msg instanceof WebSocketFrame){
                //server to client response：
                handWebSocketFrame(ctx, (WebSocketFrame)msg);
            }
    }

    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
        if(!req.getDecoderResult().isSuccess()
                || !("websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.BAD_REQUEST));
                return;
        }

        WebSocketServerHandshakerFactory wsFactory= new WebSocketServerHandshakerFactory(URL, null,false);
        handshaker= wsFactory.newHandshaker(req);
        if(handshaker!= null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(), req);
        }

    }

    public void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp){
        if(resp.getStatus().code()!=200){
            ByteBuf buf= Unpooled.copiedBuffer(resp.getStatus().toString(), CharsetUtil.UTF_8);
            resp.content().writableBytes();
            buf.release();
        }

        ChannelFuture f= ctx.channel().writeAndFlush(resp);
        if(resp.getStatus().code()!=200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        }
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if(!(frame instanceof TextWebSocketFrame)){
            System.out.println("not support");
            throw new RuntimeException(this.getClass().getName());
        }

        String request= ((TextWebSocketFrame)frame).text();
        System.out.println("server receive: "+request);
        WebSocketFrame tws= new TextWebSocketFrame(new Date().toString()+ctx.channel().id()+request);

        Config.group.writeAndFlush(tws);
    }
}
