package xin.lz1998.cq.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.plugin.forward.ForwardPlugin;
import xin.lz1998.cq.plugin.forward.HttpUtil;

@RestController
@RequestMapping("/web_api")
public class WebApiController {


    @RequestMapping("/send_private_msg")
    public String sendPrivateMsg(long self_id, long user_id, String message, Boolean auto_escape) throws IOException, InterruptedException {
        Global.robots.get(self_id).sendPrivateMsg(user_id,message,auto_escape);
        return "ok";
    }

    @RequestMapping("/send_group_msg")
    public String sendGroupMsg(long self_id, long group_id, String message, Boolean auto_escape) throws IOException,  InterruptedException {
        Global.robots.get(self_id).sendGroupMsg(group_id,message,auto_escape);
        return "ok";
    }

    @RequestMapping("/send_discuss_msg")
    public String sendDiscussMsg(long self_id, long discuss_id, String message, Boolean auto_escape) throws IOException,  InterruptedException {
        Global.robots.get(self_id).sendDiscussMsg(discuss_id,message,auto_escape);
        return "ok";
    }

    @RequestMapping("/delete_msg")
    public String deleteMsg(long self_id, int message_id) throws IOException,  InterruptedException {
        Global.robots.get(self_id).deleteMsg(message_id);
        return "ok";
    }

    @RequestMapping("/send_like")
    public String sendLike(long self_id, long user_id, Integer times) throws IOException,InterruptedException {
        Global.robots.get(self_id).sendLike(user_id,times);
        return "ok";
    }
    @RequestMapping("/set_group_kick")
    public String setGroupKick(long self_id, long group_id, long user_id, boolean reject_add_request) throws IOException, InterruptedException {
        Global.robots.get(self_id).setGroupKick(group_id,user_id,reject_add_request);
        return "ok";
    }

    @RequestMapping("/set_group_ban")
    public String setGroupBan(long self_id,long group_id, long user_id, long duration) throws InterruptedException,  IOException {
        Global.robots.get(self_id).setGroupBan(group_id,user_id,duration);
        return "ok";
    }


    @RequestMapping("/get_group_member_info")
    public String getGroupMemberInfo(long self_id,long group_id,long user_id,Boolean no_cache) throws IOException,  InterruptedException {
        Global.robots.get(self_id).getGroupMemberInfo(group_id,user_id,no_cache);
        return "ok";
    }
    
    @RequestMapping("/get_group_member_list")
    public Object getGroupMemberList(long self_id,long group_id) throws IOException,  InterruptedException {
    	return Global.robots.get(self_id).getGroupMemberList(group_id);
    }
    
    @RequestMapping("/get_friend_list")
    public Object getFriendList(long self_id) throws IOException,  InterruptedException {
    	//Global.robots.get(self_id).setRestart(true, true, true);
    	return Global.robots.get(self_id).getFriendList();
    }
    
    @RequestMapping("/get_tkl_api_test")
    public Object getTklApiTest(@RequestBody JSONObject json) throws Exception {
    	String url = json.getString("url");
    	Object[] params = json.getObject("params", Object[].class);
    	url = String.format(url,params);
    	if(url.startsWith("https")) {
    		return HttpUtil.sendHttpsGet(url);
    	}
    	return HttpUtil.sendGet(url);
    }
    
    @RequestMapping("/get_analyze_to_generate")
    public Object getAnalyzeToGenerateTkl(String tkl) throws Exception {
    	return ForwardPlugin.analyzeAndCreateNewTkl(tkl);
    }

}
