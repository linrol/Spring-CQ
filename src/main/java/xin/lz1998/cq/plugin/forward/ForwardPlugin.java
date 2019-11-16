package xin.lz1998.cq.plugin.forward;

import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.event.message.CQGroupMessageEvent;
import xin.lz1998.cq.plugin.config.CommandEnum;
import xin.lz1998.cq.plugin.config.CommandPlugin;
import xin.lz1998.cq.plugin.forward.enums.DsErrorEnum;
import xin.lz1998.cq.robot.CQPlugin;
import xin.lz1998.cq.robot.CoolQ;

/**
 * 转发插件
 * @author linrol
 *
 */
@SuppressWarnings("unchecked")
public class ForwardPlugin extends CQPlugin {
	
	private Logger logger = LoggerFactory.getLogger(ForwardPlugin.class);
    
    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
    	List<Long> monitorGrouplist = (List<Long>) CommandPlugin.config.get(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand());
    	long group_id = event.getGroupId();
    	String msg = event.getMessage().replaceAll("元", "");
    	if(!monitorGrouplist.contains(group_id)) {
    		return MESSAGE_IGNORE;
    	}
    	if(msg.contains("￥")) {
    		String sourceTkl = msg.substring(msg.indexOf("￥"),msg.lastIndexOf("￥") + 1);
    		msg = msg.substring(0,msg.indexOf("￥")) + getNewTklBy21ds(sourceTkl) + msg.substring(msg.lastIndexOf("￥") + 1);
    	}
    	List<Long> forwardGrouplist = (List<Long>) CommandPlugin.config.get(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand());
    	for(Long groupId:forwardGrouplist) {
    		cq.sendGroupMsg(groupId, msg, false);
    	}
        return MESSAGE_IGNORE; // 继续执行下一个插件
        // return MESSAGE_BLOCK; // 不执行下一个插件
    }
    
    
    private String getNewTklBy21ds(String sourceTkl) {
    	String url = "http://api.web.21ds.cn/taoke/doItemHighCommissionPromotionLinkByTpwd?apkey=%s&tpwdcode=%s&pid=%s&tbname=%s&tpwd=1&extsearch=1";
    	String apKey = "7918202b-ef4a-f251-291b-eb880302814c";
    	String pid = "mm_109302870_1080150328_109752250051";
    	String tbname = "tb6746204";
    	JSONObject jsonResult;
		try {
			jsonResult = HttpUtil.sendGet(String.format(url, apKey,sourceTkl,pid,tbname));
			logger.info("喵有券接口转换结果：" + jsonResult.toJSONString());
	    	if(jsonResult.getInteger("code") == 200) {
	    		return jsonResult.getJSONObject("data").getString("tpwd");
	    	}
	    	logger.error("获取喵有券高佣转淘口令api接口错误，请立即排查处理，否则影响引导收入，失败原因:[{}]",DsErrorEnum.getByCode(jsonResult.getString("code")));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return sourceTkl;
    }
    
    @SuppressWarnings("unused")
	private String getNewTklByDingdanxia(String sourceTkl) {
    	TreeMap<String, String> paramsMap = new TreeMap<>();
		paramsMap.put("apikey", "qSk3afesPu2hhl1ruoS3VSveXw229TNW");
		paramsMap.put("tkl", sourceTkl);
		paramsMap.put("pid", "mm_109302870_1080150328_109752250051");
		paramsMap.put("tpwd", "true");
		paramsMap.put("text", "群号910092655");
		String newtklResult = HttpUtil.httpMethodPost("http://api.tbk.dingdanxia.com/tbk/tkl_privilege", paramsMap, null);
		logger.info("订单侠接口转换结果：" + newtklResult);
		JSONObject jsonResult = JSON.parseObject(newtklResult);
		if(jsonResult.getIntValue("code") == 200) {
			return jsonResult.getJSONObject("data").getString("coupon_tpwd");
		}
		logger.error("获取订单侠高佣转淘口令api接口错误，请立即排查处理，否则影响引导收入，失败码:[{}]",jsonResult.getIntValue("code"));
    	return sourceTkl;
    }
    
}
