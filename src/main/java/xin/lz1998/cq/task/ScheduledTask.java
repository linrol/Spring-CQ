package xin.lz1998.cq.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.plugin.forward.HttpUtil;
import xin.lz1998.cq.retdata.ApiData;
import xin.lz1998.cq.robot.qqhd.MiPadShellAdb;
import xin.lz1998.cq.util.RedisUtil;

@Component
@SuppressWarnings("unchecked")
@ConditionalOnProperty(prefix="scheduler",name = "enable", havingValue = "true")
public class ScheduledTask {
	
	@Autowired
	private RedisUtil redisUtil;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);
	
	private int startGroupId = 1;
	
	private static String group[] = {"914629129","737215804"};
	
	//fixedRate 周期 频率
   	@Scheduled(initialDelay=10000,fixedDelay=600000)
    public void addFriendTask() throws Exception {
        List<Object> list = new ArrayList<Object>();
        if(!redisUtil.hasKey(group[(++startGroupId)%group.length] + "_group_list")) {
        	JSONObject jsonResult = HttpUtil.sendGet(String.format("http://www.alinkeji.com:8081/web_api/get_group_member_list?self_id=%s&group_id=%s", "2097736476",group[startGroupId%group.length]));
        	JSONArray jsonArray = jsonResult.getJSONArray("data");
        	for(int i=0;i<jsonArray.size();i++) {
        		Map<String,Object> itemMap = new HashMap<String, Object>();
				String friendQQ = jsonArray.getJSONObject(i).getString("user_id");
				itemMap.put("userId", friendQQ);
				itemMap.put("isAdd", false);
				list.add(itemMap);
				LOGGER.info("执行第{}条保存群内的qq用户号{}到本地内存的操作",i,friendQQ);
			}
        	if(list.size() > 0) {
        		LOGGER.info("执行本地内存的qq用户列表数量{}上传内存Redis[key={}]的操作",list.size(),group[startGroupId%group.length] + "_group_list");
        		redisUtil.lSet(group[startGroupId%group.length] + "_group_list", list);
        	}
        }
        Integer currentAddPosition = (Integer) redisUtil.get(group[startGroupId%group.length] + "_group_list_add_position");
		if(currentAddPosition == null) {
			redisUtil.set(group[startGroupId%group.length] + "_group_list_add_position", 0);
			currentAddPosition = 0; 
		}
		int randomBreakCount = (int)(Math.random()*4+8);
		int position = 0;
		Map<String,Object> itemWaitAddMap = (Map<String, Object>) redisUtil.lGetIndex(group[startGroupId%group.length] + "_group_list", currentAddPosition + 1);
		MiPadShellAdb.reloadResource(group[startGroupId%group.length]);
		LOGGER.info("本轮定时任务执行start,预计添加{}个好友",randomBreakCount);
		Long startTs = System.currentTimeMillis();
		while(itemWaitAddMap != null && !((Boolean)itemWaitAddMap.get("isAdd"))) {
			String addUserId = (String) itemWaitAddMap.get("userId");
			int random=(int)(Math.random()*80+200);
			LOGGER.info("当前执行第{}条添加qq好友{}操作，预计等待{}分钟进行下一次操作",currentAddPosition,addUserId,(random + 50)/60.0);
			MiPadShellAdb.addFriendByGroup(addUserId);
			Global.qlightRobots.get(1706860030l).addFriend(addUserId);
			itemWaitAddMap.put("isAdd", true);
			redisUtil.lUpdateIndex(group[startGroupId%group.length] + "_group_list", currentAddPosition, itemWaitAddMap);
			redisUtil.incr(group[startGroupId%group.length] + "_group_list_add_position", 1l);
			Thread.sleep(random * 1000);
			currentAddPosition = (Integer) redisUtil.get(group[startGroupId%group.length] + "_group_list_add_position");
			itemWaitAddMap = (Map<String, Object>) redisUtil.lGetIndex(group[startGroupId%group.length] + "_group_list", currentAddPosition + 1);
			position++;
			if(position > randomBreakCount) {
				MiPadShellAdb.addFriendByGroup("1445426299");
				Global.qlightRobots.get(1706860030l).addFriend("1445426299");
				LOGGER.info("本轮添加好友定时任务结束，共成功请求添加{}个好友,总耗时间{}分钟",randomBreakCount,(System.currentTimeMillis() - startTs)/60000.0);
				break;
			}
		}
    }
	
	// fixedRate 周期 频率
    // @Scheduled(initialDelay=10000,fixedDelay=1800000)
    public void inviteIntoGroupTask() {
        LOGGER.info("每隔30分钟执行邀请好友入群操作");
        try {
			 JSONArray jsonArray = new JSONArray();
			 ApiData<JSONObject> data = Global.qlightRobots.get(1706860030l).getFriendList();
			 JSONObject groupJsonObject = data.getData().getJSONObject("result").getJSONObject("result");
			 for (Map.Entry<String, Object> entry : groupJsonObject.entrySet()) {
				 jsonArray.addAll(((JSONObject)entry.getValue()).getJSONArray("mems"));
			 }
			 Integer newSize = jsonArray.size();
			 LOGGER.info("当前获取最新好友数:{}",newSize);
			 int randomBreakCount = (int)(Math.random()*20+5);
			 int position = 0;
			 List<Object> friendList = new ArrayList<Object>();
			 for(int i=0;i<newSize;i++) {
				 String friendQQ = jsonArray.getJSONObject(i).getString("uin");
				 friendList.add(friendQQ);
				 Object inviteBoolean = redisUtil.get("1706860030_friend_invite_" + friendQQ);
				 if(inviteBoolean != null && ((Boolean) inviteBoolean)) {
					 LOGGER.error("执行第{}条邀请qq好友{}加群操作异常,异常信息:好友已被邀请过",i,friendQQ);
					 continue;
				 }
				 int random=(int)(Math.random()*20+20);
				 LOGGER.info("执行第{}条邀请qq好友{}加群操作，并随机等待{}秒执行下一次邀请",i,friendQQ,random);
				 Global.qlightRobots.get(1706860030l).inviteIntoGroup(friendQQ);
				 redisUtil.set("1706860030_friend_invite_" + friendQQ, true);
				 position++;
				 if(position > randomBreakCount) {
					 break;
				 }
				 Thread.sleep(random * 1000);
			 }
			 redisUtil.del("1706860030_friend_list");
			 redisUtil.lSet("1706860030_friend_list", friendList);
		 } catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    // @Scheduled(initialDelay=10000,fixedDelay=300000)
    public void inviteIntoNExistGroupTask() {
        LOGGER.info("每隔五分钟轮寻不在群里的好友邀请入群操作");
        try {
        	int friendListSize = (int) redisUtil.lGetListSize("1706860030_friend_list");
        	if(friendListSize == 0) {
        		return;
        	}
        	Integer friendListSegmentSize = (int) (friendListSize / 12);
        	Integer friendListSegment = (Integer) redisUtil.get("1706860030_friend_list_segment");
    		if(friendListSegment == null) {
    			redisUtil.set("1706860030_friend_list_segment", 0);
    		}
        	List<Object> friendList = redisUtil.lGet("1706860030_friend_list", (friendListSegment * friendListSegmentSize) + 1, (friendListSegment + 1) * friendListSegmentSize);
        	redisUtil.set("1706860030_friend_list_segment", (++friendListSegment)%12);
        	ApiData<JSONObject> apiData = Global.qlightRobots.get(1706860030l).getGroupMemberList("910092655");
			JSONObject meberJson = apiData.getData().getJSONObject("result").getJSONObject("members");
        	List<Object> filterList = friendList.stream().filter(friend -> !meberJson.containsKey(friend)).collect(Collectors.toList());
        	LOGGER.info("获取第{}段数据筛选前好友总数:{},过滤已在组内后总数:{}",(friendListSegment + 1),friendList.size(),filterList.size());
        	for(Object friend:filterList) {
        		Global.qlightRobots.get(1706860030l).inviteIntoGroup(friend.toString());
        		//Global.qlightRobots.get(1706860030l).sendPrivateMsg(friend.toString(), "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<msg templateID=\"-1\" action=\"web\" brief=\"推荐群聊：线报发车交流群\" serviceID=\"15\" i_actionData=\"group:910092655\" url=\"https://jq.qq.com/?_wv=1027&amp;k=5aavukc\"><item layout=\"0\" mode=\"1\"><summary>推荐群聊</summary><hr/></item><item layout=\"2\" mode=\"1\"><picture cover=\"https://p.qlogo.cn/gh/910092655/910092655/100\"/><title>线报发车交流群</title><summary>大家好才是真的好</summary></item><source/></msg>\n");
        		LOGGER.info("执行邀请qq好友{}加群操作，并等待{}秒执行下一次邀请",friend.toString(),(3300 / filterList.size()));
        		Thread.sleep((3300 / filterList.size()) * 1000);
        	}
		 } catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
