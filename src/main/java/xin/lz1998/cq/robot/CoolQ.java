package xin.lz1998.cq.robot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import xin.lz1998.cq.entity.CQGroupAnonymous;
import xin.lz1998.cq.entity.CQStatus;
import xin.lz1998.cq.plugin.forward.HttpUtil;
import xin.lz1998.cq.retdata.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CoolQ {

    @Getter
    @Setter
    private long selfId;
    
    @Getter
    @Setter
    private WebSocketSession botApiSession;

    @Getter
    @Setter
    private WebSocketSession botEventSession;


    private Logger logger = LoggerFactory.getLogger(getClass());

    private int apiEcho = 0;//用于标记是哪次发送api，接受时作为key放入apiResponseMap

    private Map<String, ApiSender> apiCallbackMap = new HashMap<>();//用于存放api调用，收到响应时put，处理完成remove

    public CoolQ(long selfId) {
        this.selfId = selfId;
    }

    public void onReceiveApiMessage(JSONObject message) {
        logger.debug(selfId + " RECV API   {}", message);
        String echo = message.get("echo").toString();
        ApiSender apiSender = apiCallbackMap.get(echo);
        apiSender.onReceiveJson(message);
        apiCallbackMap.remove(echo);
    }
    
    private JSONObject sendApiMessage(ApiEnum action, JSONObject params) {
    	int defaultSize = 1024 * 50;	// 128M
    	return sendApiMessage(action,params,defaultSize);
    }

    private JSONObject sendApiMessage(ApiEnum action, JSONObject params,int maxSize) {
        JSONObject apiJSON = constructApiJSON(action, params);
        String echo = apiJSON.getString("echo");
        ApiSender apiSender = new ApiSender(botApiSession,maxSize);
        apiCallbackMap.put(echo, apiSender);
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

    public void onReceiveEventMessage(JSONObject message) {
        logger.debug(selfId + " RECV Event {}", message);
        new Thread(() -> EventHandler.handle(CoolQ.this, message)).start();
    }


    private JSONObject constructApiJSON(ApiEnum action, JSONObject params) {
        JSONObject apiJSON = new JSONObject();
        apiJSON.put("action", action.getUrl());
        if (params != null)
            apiJSON.put("params", params);
        apiJSON.put("echo", apiEcho++);

        return apiJSON;
    }

    public ApiData<MessageData> sendPrivateMsg(long user_id, String message, boolean auto_escape) {
        ApiEnum action = ApiEnum.SEND_PRIVATE_MSG;

        JSONObject params = new JSONObject();
        params.put("user_id", user_id);
        params.put("message", message);
        params.put("auto_escape", auto_escape);

        ApiData<MessageData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<MessageData>>() {
        });
        return result;
    }

    public ApiData<MessageData> sendGroupMsg(long group_id, String message, boolean auto_escape) {
    	try {
    		ApiEnum action = ApiEnum.SEND_GROUP_MSG;

            JSONObject params = new JSONObject();
            params.put("group_id", group_id);
            params.put("message", message);
            params.put("auto_escape", auto_escape);
        	ApiData<MessageData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<MessageData>>() {
            });
            return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException {
    	JSONObject params = new JSONObject();
        params.put("group_id", "963550879");
        params.put("message", URLEncoder.encode("29.9  买1送1同款\n" + 
        		"    			广州酒家乳酸菌小口袋面包整箱680g\n" + 
        		"    			 ￥gnRtYztdu6k￥ [CQ:image,file=4E7D3113134336E146537629AD5693FD.jpg,url=https://gchat.qpic.cn/gchatpic_new/1071893649/3915239590-2736407206-4E7D3113134336E146537629AD5693FD/0?vuin=779721310&amp;term=2]","UTF-8"));
        params.put("auto_escape", String.valueOf("false"));
        
        @SuppressWarnings("unchecked")
		TreeMap<String, String> paramsMap = JSON.parseObject(params.toString(),TreeMap.class);
    	System.out.println((HttpUtil.httpMethodPost("http://www.alinkeji.com:5700/send_group_msg", paramsMap)));
	}

    public ApiData<MessageData> sendDiscussMsg(long discuss_id, String message, boolean auto_escape) {
        ApiEnum action = ApiEnum.SEND_DISCUSS_MSG;

        JSONObject params = new JSONObject();
        params.put("discuss_id", discuss_id);
        params.put("message", message);
        params.put("auto_escape", auto_escape);

        ApiData<MessageData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<MessageData>>() {
        });
        return result;
    }
    
    public ApiData<MessageData> sendMsg(String message_type,long user_id,long group_id,long discuss_id, String message, boolean auto_escape) {
        ApiEnum action = ApiEnum.SEND_MSG;

        JSONObject params = new JSONObject();
        params.put("message_type", message_type);
        params.put("user_id", user_id);
        params.put("group_id", group_id);
        params.put("discuss_id", discuss_id);
        params.put("message", message);
        params.put("auto_escape", auto_escape);

        ApiData<MessageData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<MessageData>>() {
        });
        return result;
    }

    public ApiRawData deleteMsg(int message_id) {
        ApiEnum action = ApiEnum.DELETE_MSG;

        JSONObject params = new JSONObject();
        params.put("message_id", message_id);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData sendLike(long user_id, Integer times) {
        ApiEnum action = ApiEnum.SEND_LIKE;

        JSONObject params = new JSONObject();
        params.put("user_id", user_id);
        params.put("times", times);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupKick(long group_id, long user_id, boolean reject_add_request) {
        ApiEnum action = ApiEnum.SET_GROUP_KICK;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("user_id", user_id);
        params.put("reject_add_request", reject_add_request);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupBan(long group_id, long user_id, long duration) {
        ApiEnum action = ApiEnum.SET_GROUP_BAN;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("user_id", user_id);
        params.put("duration", duration);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupAnonymousBan(long group_id, CQGroupAnonymous cqGroupAnonymous, boolean duration) {
        ApiEnum action = ApiEnum.SET_GROUP_ANONYMOUS_BAN;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("anonymous", cqGroupAnonymous);
        params.put("duration", duration);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupAnonymousBan(long group_id, String flag, boolean duration) {
        ApiEnum action = ApiEnum.SET_GROUP_ANONYMOUS_BAN;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("flag", flag);
        params.put("duration", duration);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupWholeBan(long group_id, boolean enable) {
        ApiEnum action = ApiEnum.SET_GROUP_WHOLE_BAN;
        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("enable", enable);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupAdmin(long group_id, long user_id, boolean enable) {
        ApiEnum action = ApiEnum.SET_GROUP_ADMIN;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("user_id", user_id);
        params.put("enable", enable);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupAnonymous(long group_id, boolean enable) {
        ApiEnum action = ApiEnum.SET_GROUP_ANONYMOUS;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("enable", enable);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupCard(long group_id, long user_id, String card) {
        ApiEnum action = ApiEnum.SET_GROUP_CARD;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("user_id", user_id);
        params.put("card", card);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupLeave(long group_id, boolean is_dismiss) {
        ApiEnum action = ApiEnum.SET_GROUP_LEAVE;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("is_dismiss", is_dismiss);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupSpecialTitle(long group_id, long user_id, String special_title, boolean duration) {
        ApiEnum action = ApiEnum.SET_GROUP_SPECIAL_TITLE;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("user_id", user_id);
        params.put("special_title", special_title);
        params.put("duration", duration);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setDiscussLeave(long discuss_id) {
        ApiEnum action = ApiEnum.SET_DISCUSS_LEAVE;

        JSONObject params = new JSONObject();
        params.put("discuss_id", discuss_id);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setFriendAddRequest(String flag, boolean approve, String remark) {
        ApiEnum action = ApiEnum.SET_FRIEND_ADD_REQUEST;

        JSONObject params = new JSONObject();
        params.put("flag", flag);
        params.put("approve", approve);
        params.put("remark", remark);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData setGroupAddRequest(String flag, String sub_type, boolean approve, String reason) {
        ApiEnum action = ApiEnum.SET_GROUP_ADD_REQUEST;

        JSONObject params = new JSONObject();
        params.put("flag", flag);
        params.put("sub_type", sub_type);
        params.put("approve", approve);
        params.put("reason", reason);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiData<LoginInfoData> getLoginInfo() {
        ApiEnum action = ApiEnum.GET_LOGIN_INFO;

        ApiData<LoginInfoData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<LoginInfoData>>() {
        });
        return result;
    }

    public ApiData<StrangerInfoData> getStrangerInfo(long user_id, boolean no_cache) {

        ApiEnum action = ApiEnum.GET_STRANGER_INFO;

        JSONObject params = new JSONObject();
        params.put("user_id", user_id);
        params.put("no_cache", no_cache);

        ApiData<StrangerInfoData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<StrangerInfoData>>() {
        });
        return result;
    }

    public ApiListData<FriendData> getFriendList(){
    	int maxSize = 256 * 1024 * 1024;	// 256M
        ApiEnum action=ApiEnum.GET_FRIEND_LIST;
        ApiListData<FriendData> result = sendApiMessage(action, null,maxSize).toJavaObject(new TypeReference<ApiListData<FriendData>>() {
        });
        return result;
    }
    
    public JSONObject _getFriendList(){
    	int maxSize = 256 * 1024 * 1024;	// 256M
        ApiEnum action=ApiEnum._GET_FRIEND_LIST;
        
        JSONObject params = new JSONObject();
        params.put("flat", true);
        
        return sendApiMessage(action, null,maxSize);
    }
    
    public ApiListData<GroupData> getGroupList() {
        ApiEnum action = ApiEnum.GET_GROUP_LIST;

        ApiListData<GroupData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiListData<GroupData>>() {
        });
        return result;
    }

    public ApiData<GroupMemberInfoData> getGroupMemberInfo(long group_id, long user_id, boolean no_cache) {
        ApiEnum action = ApiEnum.GET_GROUP_MEMBER_INFO;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);
        params.put("user_id", user_id);
        params.put("no_cache", no_cache);

        ApiData<GroupMemberInfoData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<GroupMemberInfoData>>() {
        });
        return result;
    }
    
    public JSONObject getGroupMemberList(long group_id) {
    	int maxSize = 256 * 1024 * 1024;	// 256M
        ApiEnum action = ApiEnum.GET_GROUP_MEMBER_LIST;

        JSONObject params = new JSONObject();
        params.put("group_id", group_id);

        return sendApiMessage(action, params, maxSize);
    }
    

    public ApiData<CookiesData> getCookies(String domain) {
        ApiEnum action = ApiEnum.GET_COOKIES;

        JSONObject params = new JSONObject();
        params.put("domain", domain);

        ApiData<CookiesData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<CookiesData>>() {
        });
        return result;
    }

    public ApiData<CsrfTokenData> getCsrfToken() {
        ApiEnum action = ApiEnum.GET_CSRF_TOKEN;

        ApiData<CsrfTokenData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<CsrfTokenData>>() {
        });
        return result;
    }

    public ApiData<CredentialsData> getCredentials() {
        ApiEnum action = ApiEnum.GET_CREDENTIALS;

        ApiData<CredentialsData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<CredentialsData>>() {
        });
        return result;
    }

    public ApiData<FileData> getRecord(String file, String out_format, boolean full_path) {
        ApiEnum action = ApiEnum.GET_RECORD;

        JSONObject params = new JSONObject();
        params.put("file", file);
        params.put("out_format", out_format);
        params.put("full_path", full_path);

        ApiData<FileData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<FileData>>() {
        });
        return result;
    }

    public ApiData<FileData> getImage(String file) {
        ApiEnum action = ApiEnum.GET_IMAGE;

        JSONObject params = new JSONObject();
        params.put("file", file);

        ApiData<FileData> result = sendApiMessage(action, params).toJavaObject(new TypeReference<ApiData<FileData>>() {
        });
        return result;
    }

    public ApiData<BooleanData> canSendImage() {
        ApiEnum action = ApiEnum.CAN_SEND_IMAGE;

        ApiData<BooleanData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<BooleanData>>() {
        });
        return result;
    }

    public ApiData<BooleanData> canSendRecord() {
        ApiEnum action = ApiEnum.CAN_SEND_RECORD;

        ApiData<BooleanData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<BooleanData>>() {
        });
        return result;
    }

    public ApiData<CQStatus> getStatus() {
        ApiEnum action = ApiEnum.GET_STATUS;

        ApiData<CQStatus> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<CQStatus>>() {
        });
        return result;
    }

    public ApiData<VersionInfoData> getVersionInfo() {
        ApiEnum action = ApiEnum.GET_VERSION_INFO;

        ApiData<VersionInfoData> result = sendApiMessage(action, null).toJavaObject(new TypeReference<ApiData<VersionInfoData>>() {
        });
        return result;
    }

    public ApiRawData setRestartPlugin(int delay) {
        ApiEnum action = ApiEnum.SET_RESTART_PLUGIN;

        JSONObject params = new JSONObject();
        params.put("delay", delay);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }
    
    public void setRestart(boolean cleanLog,boolean cleanCache,boolean cleanEvent) {
        ApiEnum action = ApiEnum._SET_RESTART;

        JSONObject params = new JSONObject();
        params.put("clean_log", cleanLog);
        params.put("clean_cache", cleanCache);
        params.put("clean_event", cleanEvent);

        sendApiMessage(action, params);
    }

    public ApiRawData cleanDataDir(String data_dir) {
        ApiEnum action = ApiEnum.CLEAN_DATA_DIR;

        JSONObject params = new JSONObject();
        params.put("data_dir", data_dir);

        ApiRawData result = sendApiMessage(action, params).toJavaObject(ApiRawData.class);
        return result;
    }

    public ApiRawData cleanPluginLog() {
        ApiEnum action = ApiEnum.CLEAN_PLUGIN_LOG;

        ApiRawData result = sendApiMessage(action, null).toJavaObject(ApiRawData.class);
        return result;
    }
}
