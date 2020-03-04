package xin.lz1998.cq.robot.qlight;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    
    private String[] addMessage = {"加我一下，换号了","附近朋友","你好，我关注你好久了,通过一下","你好，微信好友","朋友推荐的，认识一下","交个朋友，认识一下"};

    public Qlight(long selfId) {
        this.selfId = selfId;
    }
    
    public void onReceiveEventMessage(JSONObject message) {
        logger.debug(selfId + " RECV Event {}", message);
        CompletableFuture.runAsync(() -> {
        	EventQlightHandler.handle(Qlight.this, message);
		 });
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
    
    public ApiData<JSONObject> sendQzoneMsg(String content) {
        ApiEnum action = ApiEnum.SEND_QZONE;

        JSONObject params = new JSONObject();
        params.put("content", content);

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
    
    public ApiData<JSONObject> addFriend(String qq) {
    	ApiEnum action = ApiEnum.ADD_FRIEND;

        JSONObject params = new JSONObject();
        params.put("qq", qq);
        params.put("message", addMessage[(int)(Math.random()*5)]);

        ApiData<JSONObject> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<JSONObject>>() {
        });
        return result;
    }
    
    public ApiData<JSONObject> getFriendList() {
    	ApiEnum action = ApiEnum.GET_FRIEND_LIST;

        JSONObject params = new JSONObject();
        params.put("cache", false);

        ApiData<JSONObject> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<JSONObject>>() {
        });
        return result;
    }
    
    public ApiData<JSONObject> inviteIntoGroup(String qq) {
    	ApiEnum action = ApiEnum.INVITE_INTO_GROUP;

        JSONObject params = new JSONObject();
        params.put("qq", qq);
        params.put("group", "910092655");

        ApiData<JSONObject> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<JSONObject>>() {
        });
        return result;
    }
    
    public ApiData<JSONObject> getGroupMemberList(String group) {
    	ApiEnum action = ApiEnum.GET_GROUP_MEMBER_LIST;

        JSONObject params = new JSONObject();
        params.put("group", group);
        params.put("cache", false);

        ApiData<JSONObject> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<JSONObject>>() {
        });
        return result;
    }
}
