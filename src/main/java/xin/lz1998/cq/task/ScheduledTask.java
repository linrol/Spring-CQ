package xin.lz1998.cq.task;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.lliiooll.RedisUtil;
import xin.lz1998.cq.Global;
import xin.lz1998.cq.plugin.forward.HttpUtil;

@Component
public class ScheduledTask {
	
	@Autowired
	private RedisUtil redisUtil;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);
	
	//fixedRate 周期 频率
    @Scheduled(initialDelay=10000,fixedRate=300000)
    public void task() {
        LOGGER.info("每隔300秒执行邀请好友入群操作");
        CompletableFuture.runAsync(() -> {
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
				 if(!newSize.equals(oldSize)) {
					 Integer changeSize = newSize - oldSize;
					 LOGGER.info("最新获取好友数变化了:{}个,执行第{}个好友后的邀请入群",changeSize,oldSize);
					 for(int i=oldSize;i<newSize;i++) {
						 String friendQQ = jsonArray.getJSONObject(i).getString("user_id");
						 Object inviteBoolean = redisUtil.get("1706860030_friend_invite_" + friendQQ);
						 if(inviteBoolean != null && ((Boolean) inviteBoolean)) {
							 LOGGER.error("执行第{}条邀请qq好友{}加群操作异常,异常信息:好友已被邀请过",changeSize,oldSize);
							 continue;
						 }
						 int random=(int)(Math.random()*20+20);
						 LOGGER.info("执行第{}条邀请qq好友{}加群操作，并随机等待{}秒执行下一次邀请",i,friendQQ,random);
						 Global.qlightRobots.get(1706860030l).inviteIntoGroup(friendQQ);
						 redisUtil.set("1706860030_friend_invite_" + friendQQ, true);
						 Thread.sleep(random * 1000);
					 }
					 redisUtil.set("1706860030_friend_size", newSize);
				 }
			 } catch (Exception e) {
				e.printStackTrace();
			}
		 });
    }

}
