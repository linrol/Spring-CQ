package xin.lz1998.cq.robot.qlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.plugin.PluginConfig;
import xin.lz1998.cq.util.RedisUtil;
import xin.lz1998.cq.util.SpringContextUtil;

@Service
class EventQlightHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventQlightHandler.class);

    static void handle(Qlight qlight, JSONObject eventJson) {
        String event = eventJson.getString("event");
        if(eventJson.getJSONObject("params") != null && eventJson.getJSONObject("params").getString("type") != null) {
        	event += eventJson.getJSONObject("params").getString("type");
        }
        switch (event){
	        case "message1":{
	            for(QlightPlugin plugin:PluginConfig.qlightPluginList){
	                if (plugin.onPrivateMessage(qlight,eventJson) == QlightPlugin.MESSAGE_BLOCK)
	                    break;
	            }
	            break;
	        }
	        case "message2":{
	            for(QlightPlugin plugin:PluginConfig.qlightPluginList){
	                if (plugin.onGroupMessage(qlight,eventJson) == QlightPlugin.MESSAGE_BLOCK)
	                    break;
	            }
	            break;
	        }
	        case "friendChange1":{
	        	String inviteQQ = eventJson.getJSONObject("params").getString("qq");
	        	LOGGER.info("已和qq:{}成为单向好友,邀请好友如群",inviteQQ);
	        	Global.qlightRobots.get(1706860030l).inviteIntoGroup(inviteQQ);
	        	RedisUtil redisUtil = SpringContextUtil.getBean(RedisUtil.class);
	        	if(redisUtil != null) {
	        		redisUtil.set("1706860030_friend_invite_" + inviteQQ, true);
					redisUtil.incr("1706860030_friend_size", 1l);
	        	}
	            break;
	        }
	        case "friendChange2":{
	        	String inviteQQ = eventJson.getJSONObject("params").getString("qq");
	        	LOGGER.info("已和qq:{}成为双向好友,邀请好友如群",inviteQQ);
	        	Global.qlightRobots.get(1706860030l).inviteIntoGroup(inviteQQ);
	        	RedisUtil redisUtil = SpringContextUtil.getBean(RedisUtil.class);
	        	if(redisUtil != null) {
	        		redisUtil.set("1706860030_friend_invite_" + inviteQQ, true);
					redisUtil.incr("1706860030_friend_size", 1l);
	        	}
	            break;
	        }
        }
    }
}
