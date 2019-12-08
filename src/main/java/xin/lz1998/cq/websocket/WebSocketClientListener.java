package xin.lz1998.cq.websocket;

import java.net.URI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WebSocketClientListener implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		WebSocketClientHandler webSocketClient = new WebSocketClientHandler(1706860030l,new URI("ws://aliyun.alinkeji.com:49632/"));
        webSocketClient.connect();
	}

}
