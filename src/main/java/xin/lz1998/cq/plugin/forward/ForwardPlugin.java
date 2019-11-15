package xin.lz1998.cq.plugin.forward;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.event.message.CQGroupMessageEvent;
import xin.lz1998.cq.robot.CQPlugin;
import xin.lz1998.cq.robot.CoolQ;

/**
 * 转发插件
 * @author linrol
 *
 */
public class ForwardPlugin extends CQPlugin {
	
	// 测试来源群
	private long testSourceGroupId = 963550879;
	
	// 来源群
	private long sourceGroupId = 914494716;
	
	// 待转发的群
	private long forwardGroupId = 910092655;
	
	private Logger logger = LoggerFactory.getLogger(ForwardPlugin.class);
    
    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
    	long group_id = event.getGroupId();
    	String msg = event.getMessage().replaceAll("元", "");
    	if((group_id != sourceGroupId && group_id != testSourceGroupId)) {
    		return MESSAGE_IGNORE;
    	}
    	if(msg.contains("￥")) {
    		String tkl = msg.substring(msg.indexOf("￥"),msg.lastIndexOf("￥") + 1);
    		TreeMap<String, String> paramsMap = new TreeMap<>();
    		paramsMap.put("apikey", "qSk3afesPu2hhl1ruoS3VSveXw229TNW");
    		paramsMap.put("tkl", tkl);
    		paramsMap.put("pid", "mm_109302870_1080150328_109752250051");
    		paramsMap.put("tpwd", "true");
    		paramsMap.put("text", "群号910092655");
    		String newtklResult = HttpUtil.httpMethodPost("http://api.tbk.dingdanxia.com/tbk/tkl_privilege", paramsMap, null);
    		logger.info("淘口令接口转换结果：" + newtklResult);
			JSONObject jsonResult = JSON.parseObject(newtklResult);
			if(jsonResult.getIntValue("code") == 200) {
				String newTkl = jsonResult.getJSONObject("data").getString("coupon_tpwd");
				msg = msg.substring(0,msg.indexOf("￥")) + newTkl + msg.substring(msg.lastIndexOf("￥") + 1);
			}
    	}
    	cq.sendGroupMsg(forwardGroupId, msg, false);
        return MESSAGE_IGNORE; // 继续执行下一个插件
        // return MESSAGE_BLOCK; // 不执行下一个插件
    }
}
