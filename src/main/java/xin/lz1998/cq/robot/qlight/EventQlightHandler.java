package xin.lz1998.cq.robot.qlight;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.plugin.PluginConfig;

@Service
class EventQlightHandler {


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
        }
    }
}
