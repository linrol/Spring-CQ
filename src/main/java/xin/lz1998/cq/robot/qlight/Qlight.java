package xin.lz1998.cq.robot.qlight;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.Getter;
import lombok.Setter;
import xin.lz1998.cq.retdata.ApiData;
import xin.lz1998.cq.websocket.WebSocketClientHandler;

public class Qlight {

    @Getter
    @Setter
    private long selfId;

    @Getter
    @Setter
    private WebSocketClientHandler robotWebSocketClient;


    private Logger logger = LoggerFactory.getLogger(getClass());

    private int apiId = 0;//用于标记是哪次发送api，接受时作为key放入apiResponseMap

    @Getter
    public Map<String, ApiSender> apiCallbackMap = new HashMap<>();//用于存放api调用，收到响应时put，处理完成remove

    public Qlight(long selfId) {
        this.selfId = selfId;
    }
    
    public void onReceiveEventMessage(JSONObject message) {
        logger.debug(selfId + " RECV Event {}", message);
        new Thread(() -> EventQlightHandler.handle(Qlight.this, message)).start();
    }

    public void onReceiveApiMessage(JSONObject message) {
        logger.debug(selfId + " RECV API   {}", message);
        String apiId = message.get("id").toString();
        ApiSender apiSender = apiCallbackMap.get(apiId);
        apiSender.onReceiveJson(message);
        apiCallbackMap.remove(apiId);
    }

    private JSONObject sendApiMessage(ApiEnum action, JSONObject params) {
        JSONObject apiJSON = constructApiJSON(action, params);
        String apiId = apiJSON.getString("id");
        ApiSender apiSender = new ApiSender(robotWebSocketClient);
        apiCallbackMap.put(apiId, apiSender);
        logger.info("{} SEND API   {} {}", selfId, action.getDesc(), params);
        JSONObject retJson;
        try {
            retJson = apiSender.sendApiJson(apiJSON);
        } catch (Exception e) {
            logger.error(e.toString());
            retJson = new JSONObject();
            retJson.put("status", "failed");
            retJson.put("retcode", -1);
        }
        logger.info("{} SEND API   {} {} Result:{}", selfId, action.getDesc(), params,retJson.toString());
        return retJson;
    }

    private JSONObject constructApiJSON(ApiEnum action, JSONObject params) {
        JSONObject apiJSON = new JSONObject();
        apiJSON.put("method", action.getUrl());
        if (params != null)
            apiJSON.put("params", params);
        apiJSON.put("id", "qqlight_" + apiId++);

        return apiJSON;
    }

    public ApiData<JSONObject> sendPrivateMsg(String user_id, String message) {
        ApiEnum action = ApiEnum.SEND_MSG;

        JSONObject params = new JSONObject();
        params.put("type", 1);
        params.put("group", "");
        params.put("qq", user_id);
        params.put("content", message);

        ApiData<JSONObject> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<JSONObject>>() {
        });
        return result;
    }

    public ApiData<JSONObject> sendGroupMsg(String group_id, String message) {
    	ApiEnum action = ApiEnum.SEND_MSG;

        JSONObject params = new JSONObject();
        params.put("type", 2);
        params.put("group", group_id);
        params.put("qq", "");
        params.put("content", message);

        ApiData<JSONObject> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<JSONObject>>() {
        });
        return result;
    }

}
