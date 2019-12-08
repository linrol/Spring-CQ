package xin.lz1998.cq.websocket.client;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.robot.qlight.Qlight;

public class RobotWebSocketClient extends WebSocketClient {
	 private static final Logger LOGGER = LoggerFactory.getLogger(RobotWebSocketClient.class);

	    public RobotWebSocketClient(URI serverUri) {
	        super(serverUri);
	    }

	    @Override
	    public void onOpen(ServerHandshake arg0) {
	        Global.qlightRobots.putIfAbsent(1706860030l, new Qlight(1706860030));
	        Qlight robot = Global.qlightRobots.get(1706860030l);
	        robot.setSelfId(1706860030l);
	        robot.setRobotWebSocketClient(this);
	    }

	    @Override
	    public void onClose(int arg0, String arg1, boolean arg2) {
	        // TODO Auto-generated method stub
	        LOGGER.info("------ MyWebSocket onClose ------{}",arg1);
	    }

	    @Override
	    public void onError(Exception arg0) {
	        // TODO Auto-generated method stub
	        LOGGER.info("------ MyWebSocket onError ------{}",arg0);
	    }

	    @Override
	    public void onMessage(String data) {
	    	long xSelfId = 1706860030l;
	    	LOGGER.info("{} received event notice....", xSelfId);
	    	Qlight robot = Global.qlightRobots.get(xSelfId);
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
