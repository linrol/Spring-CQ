package xin.lz1998.cq;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import xin.lz1998.cq.websocket.client.RobotWebSocketClient;

@SpringBootApplication
public class SpringCQApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCQApplication.class, args);
    }

    @Bean
    public WebSocketClient webSocketClient() {
        try {
            RobotWebSocketClient webSocketClient = new RobotWebSocketClient(new URI("ws://aliyun.alinkeji.com:49632/"));
            webSocketClient.connect();
            
            return webSocketClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
