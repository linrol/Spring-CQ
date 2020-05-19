package xin.lz1998.cq.robot;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

class ApiSender extends Thread {

  private final WebSocketSession apiSession;
  private JSONObject responseJSON;

  ApiSender(WebSocketSession apiSession, int maxSize) {
    this.apiSession = apiSession;
    this.apiSession.setBinaryMessageSizeLimit(maxSize);
    this.apiSession.setTextMessageSizeLimit(maxSize);
  }

  JSONObject sendApiJson(JSONObject apiJSON) throws IOException, InterruptedException {
    synchronized (apiSession) {
      apiSession.sendMessage(new TextMessage(apiJSON.toJSONString()));
    }
    synchronized (this) {
      this.wait(RobotConfig.CQ_API_TIMEOUT);
    }
    return responseJSON;
  }


  void onReceiveJson(JSONObject responseJSON) {
    this.responseJSON = responseJSON;
    synchronized (this) {
      this.notify();
    }
  }

}
