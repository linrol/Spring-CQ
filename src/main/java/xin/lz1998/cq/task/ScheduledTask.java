package xin.lz1998.cq.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.plugin.forward.HttpUtil;
import xin.lz1998.cq.util.RedisUtil;

@Component
@SuppressWarnings("unchecked")
public class ScheduledTask {
	
	@Autowired
	private RedisUtil redisUtil;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);
	
	//fixedRate 周期 频率
    @Scheduled(initialDelay=10000,fixedDelay=600000)
    public void addFriendTask() throws Exception {
        LOGGER.info("每隔10分钟执行好友添加操作");
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        if(!redisUtil.hasKey("753210700_group_list")) {
        	JSONObject jsonResult = HttpUtil.sendGet(String.format("http://www.alinkeji.com:8081/web_api/get_group_member_list?self_id=%s&group_id=%s", "779721310","753210700"));
        	JSONArray jsonArray = jsonResult.getJSONArray("data");
        	for(int i=200;i<jsonArray.size();i++) {
        		Map<String,Object> itemMap = new HashMap<String, Object>();
				String friendQQ = jsonArray.getJSONObject(i).getString("user_id");
				itemMap.put("userId", friendQQ);
				itemMap.put("isAdd", false);
				list.add(itemMap);
				LOGGER.info("执行第{}条保存群内的qq用户号{}到本地内存的操作",i,friendQQ);
			}
        	if(list.size() > 0) {
        		LOGGER.info("执行本地内存的qq用户列表上传内存Redis[key={}]的操作","753210700_group_list");
        		redisUtil.lSet("753210700_group_list", list);
        	}
        }
        Object currentAddPosition = redisUtil.get("753210700_group_list_add_position");
		if(currentAddPosition == null) {
			redisUtil.set("753210700_group_list_add_position", 0);
			currentAddPosition = 0; 
		}
		Map<String,Object> itemWaitAddMap = (Map<String, Object>) redisUtil.lGetIndex("753210700_group_list", (long)currentAddPosition);
		while(itemWaitAddMap != null && !((Boolean)itemWaitAddMap.get("isAdd"))) {
			String addUserId = (String) itemWaitAddMap.get("userId");
			int random=(int)(Math.random()*40+20);
			LOGGER.info("当前执行第{}条添加qq好友{}操作，并随机等待{}秒执行下一次添加",currentAddPosition,addUserId,random);
			Global.qlightRobots.get(1706860030l).addFriend(addUserId);
			itemWaitAddMap.put("isAdd", true);
			redisUtil.lUpdateIndex("753210700_group_list", (long)currentAddPosition, itemWaitAddMap);
			redisUtil.incr("753210700_group_list_add_position", 1l);
			Thread.sleep(random * 1000);
		}
    }
	
	//fixedRate 周期 频率
    @Scheduled(initialDelay=10000,fixedDelay=300000)
    public void inviteIntoGroupTask() {
        LOGGER.info("每隔5分钟执行邀请好友入群操作");
        try {
			 JSONObject jsonResult = HttpUtil.sendGet(String.format("http://www.alinkeji.com:8081/web_api/get_friend_list?self_id=%s", "1706860030"));
			 JSONArray jsonArray = jsonResult.getJSONArray("data");
			 Integer newSize = jsonArray.size();
			 Object oldSizeObject = redisUtil.get("1706860030_friend_size");
			 if(oldSizeObject == null) {
				 oldSizeObject = 0; 
			 }
			 Integer oldSize = (Integer) oldSizeObject;
			 LOGGER.info("上一次获取到的好友数:{},当前获取最新好友数:{}",oldSize,newSize);
			 if(!newSize.equals(oldSize) && newSize >= oldSize) {
				 Integer changeSize = newSize - oldSize;
				 LOGGER.info("最新获取好友数变化了:{}个,执行第{}个好友后的邀请入群",changeSize,oldSize);
				 for(int i=0;i<newSize;i++) {
					 String friendQQ = jsonArray.getJSONObject(i).getString("userId");
					 Object inviteBoolean = redisUtil.get("1706860030_friend_invite_" + friendQQ);
					 if(inviteBoolean != null && ((Boolean) inviteBoolean)) {
						 LOGGER.error("执行第{}条邀请qq好友{}加群操作异常,异常信息:好友已被邀请过",i,friendQQ);
						 continue;
					 }
					 int random=(int)(Math.random()*20+20);
					 LOGGER.info("执行第{}条邀请qq好友{}加群操作，并随机等待{}秒执行下一次邀请",i,friendQQ,random);
					 Global.qlightRobots.get(1706860030l).inviteIntoGroup(friendQQ);
					 redisUtil.set("1706860030_friend_invite_" + friendQQ, true);
					 redisUtil.incr("1706860030_friend_size", 1l);
					 Thread.sleep(random * 1000);
				 }
			 }
		 } catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
