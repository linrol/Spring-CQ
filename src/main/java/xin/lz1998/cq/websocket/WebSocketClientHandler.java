package xin.lz1998.cq.websocket;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.robot.qlight.Qlight;

public class WebSocketClientHandler extends WebSocketClient {
	 private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClientHandler.class);
	 
	 private Long qlightSelfId;
	 
	 public WebSocketClientHandler(long qlightSelfId, URI serverUri) {
		 super(serverUri);
		 this.qlightSelfId = qlightSelfId;
	 }
	 @Override
	 public void onOpen(ServerHandshake arg0) {
		 Global.qlightRobots.putIfAbsent(qlightSelfId, new Qlight(qlightSelfId));
		 Qlight robot = Global.qlightRobots.get(qlightSelfId);
		 robot.setSelfId(qlightSelfId);
		 robot.setRobotWebSocketClient(this);
	 }

	 @Override
	 public void onClose(int arg0, String arg1, boolean arg2) {
		 LOGGER.info("------ MyWebSocket onClose ------{}",arg1);
		 Global.robots.remove(qlightSelfId);
	 }

	 @Override
	 public void onError(Exception arg0) {
		 LOGGER.info("------ MyWebSocket onError ------{}",arg0);
	 }

	 @Override
	 public void onMessage(String data) {
		 LOGGER.info("{} received event notice....", qlightSelfId);
		 Qlight robot = Global.qlightRobots.get(qlightSelfId);
		 String apiId = JSON.parseObject(data).getString("id");
		 if(robot.getApiCallbackMap().containsKey(apiId)) {
			 // 说明是调用api的返回数据
			 robot.onReceiveApiMessage(JSON.parseObject(data));
		 }else {
			 // 事件上报
			 robot.onReceiveEventMessage(JSON.parseObject(data));
		 }
	 }
}
