package xin.lz1998.cq.websocket;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.plugin.forward.HttpUtil;
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
		 CompletableFuture.runAsync(() -> {
			 // 753210700
			 Global.qlightRobots.get(1706860030l).inviteIntoGroup("3021561689");
			 /*try {
				JSONObject jsonResult = HttpUtil.sendGet(String.format("http://www.alinkeji.com:8081/web_api/get_group_member_list?self_id=%s&group_id=%s", "779721310","753210700"));
				JSONArray jsonArray = jsonResult.getJSONArray("data");
				for(int i=0;i<200;i++) {
					String friendQQ = jsonArray.getJSONObject(i).getString("user_id");
					int random=(int)(Math.random()*40+20);
					LOGGER.info("当前执行第{}条添加qq好友{}操作，并随机等待{}秒执行下一次添加",i,friendQQ,random);
					Global.qlightRobots.get(1706860030l).addFriend(friendQQ);
					Thread.sleep(random * 1000);
				}
			 } catch (Exception e) {
				e.printStackTrace();
			}*/
		 });
		 LOGGER.info("{} WebSocketClient open success....", qlightSelfId);
	 }

	 @Override
	 public void onClose(int arg0, String arg1, boolean arg2) {
		 Global.robots.remove(qlightSelfId);
		 LOGGER.info("{} WebSocketClient close success....", qlightSelfId);
	 }

	 @Override
	 public void onError(Exception arg0) {
		 LOGGER.info("------ WebSocketClient onError ------{}",arg0);
	 }

	 @Override
	 public void onMessage(String data) {
		 LOGGER.info("{} received event notice....data:{}", qlightSelfId,data);
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
