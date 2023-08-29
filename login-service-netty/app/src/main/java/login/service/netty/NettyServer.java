package login.service.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class NettyServer {

	private final int port;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final String correctPassword = passwordEncoder.encode("!a");
	private final ObjectMapper objectMapper = new ObjectMapper();

	public NettyServer(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
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
									User user = objectMapper.readValue(jsonString, User.class);

									FullHttpResponse response;
									if (passwordEncoder.matches(user.getPassword(), correctPassword)) {
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

	public static void main(String[] args) throws Exception {
		new NettyServer(8080).start();
	}

}

class User {
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
