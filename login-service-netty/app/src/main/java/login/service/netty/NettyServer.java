package login.service.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyServer {

	private final int port;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
	private final String correctPassword = passwordEncoder.encode("!aa");

	public NettyServer(int port) {
		this.port = port;
	}

	private static final Logger logger = Logger.getLogger(NettyServer.class.getName());

	public void start() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		logger.log(Level.INFO, "Server started on port: " + port);

		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new HttpServerCodec());
							ch.pipeline().addLast(new HttpObjectAggregator(65536));
							ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
								@Override
								protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
									ByteBuf content = req.content();
									String jsonString = content.toString(CharsetUtil.UTF_8);
									var password = extractPassword(jsonString);

									FullHttpResponse response;
									if (passwordEncoder.matches(password, correctPassword)) {
										response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
										response.content().writeBytes(("Login successful").getBytes(CharsetUtil.UTF_8));
									} else {
										response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
										response.content().writeBytes(("Validation failed").getBytes(CharsetUtil.UTF_8));
									}

									ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
								}
							});
						}
					});

			ChannelFuture future = bootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private String extractPassword(String json) {
		int startIdx = json.indexOf("\"password\":");
		if (startIdx != -1) {
			startIdx = json.indexOf("\"", startIdx + 11) + 1;
			int endIdx = json.indexOf("\"", startIdx);
			if (endIdx != -1) {
				return json.substring(startIdx, endIdx);
			}
		} else {
			throw new RuntimeException("No password found");
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		new NettyServer(8080).start();
	}

}