package xin.lz1998.cq.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xin.lz1998.cq.Global;
import xin.lz1998.cq.robot.CoolQ;


public class WebSocketHandler extends TextWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Getter
    @Setter
    private String socketType;
    
    public WebSocketHandler(String socketType) {
    	this.socketType = socketType;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        long xSelfId = Long.valueOf(session.getHandshakeHeaders().get("x-self-id").get(0));
        logger.info("{} received {} websocket handle....", xSelfId, this.getSocketType());
        CoolQ robot = Global.robots.get(xSelfId);
        JSONObject recvJson=JSON.parseObject(message.getPayload());
        if("api".equals(this.getSocketType())) {
        	robot.onReceiveApiMessage(recvJson);
        }else if("event".equals(this.getSocketType())) {
        	robot.setBotEventSession(session);
        	robot.onReceiveEventMessage(recvJson);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
    	long xSelfId = Long.valueOf(session.getHandshakeHeaders().get("x-self-id").get(0));
        logger.info("{} {} connected", xSelfId, this.getSocketType());
        Global.robots.putIfAbsent(xSelfId, new CoolQ(xSelfId));
        CoolQ robot = Global.robots.get(xSelfId);
        robot.setSelfId(xSelfId);
        if(779721310l == xSelfId) {
        	int maxSize = 256 * 1024 * 1024;	// 128M
        	logger.info("{} set maxSize", xSelfId);
        	session.setBinaryMessageSizeLimit(maxSize);
        	session.setTextMessageSizeLimit(maxSize);
        }
        if(1706860030l == xSelfId ) {
        	int maxSize = 128 * 1024 * 1024;	// 128M
        	logger.info("{} set maxSize", xSelfId);
        	session.setBinaryMessageSizeLimit(maxSize);
        	session.setTextMessageSizeLimit(maxSize);
        }
        if("api".equals(this.getSocketType())) {
        	robot.setBotApiSession(session);
        }else if("event".equals(this.getSocketType())) {
        	robot.setBotEventSession(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        long xSelfId = Long.valueOf(session.getHandshakeHeaders().get("x-self-id").get(0));
        logger.info("{} disconnected", xSelfId);
        Global.robots.remove(xSelfId);
    }
}