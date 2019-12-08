package xin.lz1998.cq.robot.qlight;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.robot.RobotConfig;
import xin.lz1998.cq.websocket.client.RobotWebSocketClient;

class ApiSender extends Thread {
    private final RobotWebSocketClient robotWebSocketClient;
    private JSONObject responseJSON;

    ApiSender(RobotWebSocketClient robotWebSocketClient) {
        this.robotWebSocketClient = robotWebSocketClient;
    }

    JSONObject sendApiJson(JSONObject apiJSON) throws IOException, InterruptedException {
        synchronized (robotWebSocketClient){
        	robotWebSocketClient.send(apiJSON.toJSONString());
        }
        synchronized (this) {
            this.wait(RobotConfig.CQ_API_TIMEOUT);
        }
        return responseJSON;
    }


    void onReceiveJson(JSONObject responseJSON) {
    	JSONObject response = new JSONObject();
    	response.put("status", "true");
    	response.put("retcode", "ok");
    	response.put("data", responseJSON);
        this.responseJSON = response;
        synchronized (this) {
            this.notify();
        }
    }

}
