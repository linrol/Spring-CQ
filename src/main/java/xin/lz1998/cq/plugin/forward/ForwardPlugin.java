package xin.lz1998.cq.plugin.forward;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.Global;
import xin.lz1998.cq.event.message.CQGroupMessageEvent;
import xin.lz1998.cq.plugin.config.CommandEnum;
import xin.lz1998.cq.plugin.config.CommandPlugin;
import xin.lz1998.cq.robot.CQPlugin;
import xin.lz1998.cq.robot.CoolQ;
import xin.lz1998.cq.robot.qlight.Qlight;

/**
 * 转发插件
 * @author linrol
 *
 */
@SuppressWarnings("unchecked")
public class ForwardPlugin extends CQPlugin {
	
	private static Logger logger = LoggerFactory.getLogger(ForwardPlugin.class);
	
	public static Map<String,String> pidMap = new HashMap<String,String>();
	
	private static ForyouStack<String> msgStack = new ForyouStack<String>(20);
	
	static {
		pidMap.put("910092655", "mm_109302870_1080150328_109752250051");
		pidMap.put("198896490", "mm_109302870_1090250211_109781850271");
		pidMap.put("1706860030", "mm_109302870_1334100247_110058600353");
    }
	
    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
    	logger.info("QQ:{}收到群:{}消息", cq.getSelfId(), event.getGroupId());
    	List<Long> monitorGrouplist = ((Map<Long,List<Long>>)CommandPlugin.config.get(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand())).get(cq.getSelfId());
    	long group_id = event.getGroupId();
    	String msg = filterMsg(event.getMessage());
    	//if(!monitorGrouplist.contains(group_id) || !monitorUserMap.get(String.valueOf(cq.getSelfId())).contains(String.valueOf(event.getUserId()))) {
    	if(monitorGrouplist == null || !monitorGrouplist.contains(group_id)) {
    		return MESSAGE_IGNORE;
    	}
    	if(msgStack.containLike(StringUtil.getChineseString(msg), 0.8f)) {
    		logger.info("消息[{}]大于相似因子0.8，放弃本次转发", StringUtil.getChineseString(msg));
    		return MESSAGE_IGNORE;
    	}
    	msgStack.push(StringUtil.getChineseString(msg));
    	ImageUtil.downloadImage(event.getMessage());
    	List<Long> forwardGrouplist = (List<Long>) CommandPlugin.config.get(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand());
    	String sourceTkl = getSourceTkl(msg);
    	for(Long groupId:forwardGrouplist) {
    		if(StringUtils.isNotBlank(sourceTkl)) {
    			String newTkl = getChangeTklBy21ds(sourceTkl,pidMap.get(String.valueOf(groupId)));
    			logger.info("sourceTkl:" + sourceTkl.replaceAll("￥", "") + "-----" + newTkl.replaceAll("￥", ""));
    			Global.robots.get(779721310l).sendGroupMsg(groupId, msg.replaceAll(sourceTkl.replaceAll("￥", ""), newTkl.replaceAll("￥", "")), false);
    		}else {
    			Global.robots.get(779721310l).sendGroupMsg(groupId, msg, false);
    		}
    	}
        return MESSAGE_IGNORE; // 继续执行下一个插件
        // return MESSAGE_BLOCK; // 不执行下一个插件
    }
    
    @Override
    public int onGroupMessage(Qlight qlight,JSONObject jsonData) {
    	String group_id = jsonData.getJSONObject("params").getString("group");
    	String message = jsonData.getJSONObject("params").getString("content");
    	logger.info("机器人QQ:{}收到群:{}消息", qlight.getSelfId(), group_id);
    	Map<Long,List<Long>> monitorMap = (Map<Long, List<Long>>) CommandPlugin.config.get(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand());
    	if(monitorMap == null || !monitorMap.containsKey(qlight.getSelfId())) {
    		logger.error("机器人QQ:{}未设置监听的配置,结束执行", qlight.getSelfId());
    		return MESSAGE_IGNORE;
    	}
    	List<Long> monitorGrouplist = monitorMap.get(qlight.getSelfId());
    	String msg = filterMsg(message);
    	if(monitorGrouplist == null || !monitorGrouplist.contains(Long.valueOf(group_id)) || msg.contains("[QQ")) {
    		logger.error("机器人QQ:{}设置的监听群列表中不包含群:{}的配置,结束执行", qlight.getSelfId(), group_id);
    		return MESSAGE_IGNORE;
    	}
    	if(msgStack.containLike(StringUtil.getChineseString(msg), 0.8f)) {
    		logger.error("消息[{}]大于相似因子0.8，跳过转发插件", StringUtil.getChineseString(msg));
    		return MESSAGE_IGNORE;
    	}
    	logger.info("机器人QQ:{}收到群:{}消息{}", qlight.getSelfId(), group_id,msg);
    	msgStack.push(StringUtil.getChineseString(msg));
    	String sourceTkl = getSourceTkl(msg);
    	if(StringUtils.isBlank(sourceTkl)) {
    		logger.error("暂未检测到淘口令或JD链接，跳过转发插件");
    		return MESSAGE_IGNORE;
    	}
    	String newTkl = getChangeTklBy21ds(sourceTkl,pidMap.get(String.valueOf(qlight.getSelfId())));
    	if(sourceTkl.equals(newTkl)) {
    		logger.error("检测到原始淘口令或JD链接和新口令或链接相同,跳过转发插件");
    		return MESSAGE_IGNORE;
    	}
		logger.info("sourceTkl:" + sourceTkl.replaceAll("￥", "") + "-----" + newTkl.replaceAll("￥", ""));
		Global.qlightRobots.get(qlight.getSelfId()).sendQzoneMsg(msg.replaceAll("\r", "").replaceAll("\n", "").replaceAll(sourceTkl.replaceAll("￥", ""), newTkl.replaceAll("￥", "")));
		return MESSAGE_IGNORE;
    }
    
    public static String getSourceTkl(String msg) {
		if(msg.contains("￥")) {
    		String sourceTkl = msg.substring(msg.indexOf("￥"),msg.lastIndexOf("￥") + 1);
    		return sourceTkl;
    	}
		List<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile("(\\()([0-9a-zA-Z\\.\\/\\=])*(\\))");
		Matcher m = p.matcher(msg);
		while (m.find()) {
			list.add(m.group(0).substring(1, m.group().length() - 1));
		}
		return list.size() > 0 ? "￥" + list.get(0) + "￥":"";
	}
    
    private static String getChangeTklBy21ds(String sourceTkl,String pid) {
    	String url = "http://api.web.21ds.cn/taoke/doItemHighCommissionPromotionLinkByTpwd?apkey=%s&tpwdcode=%s&pid=%s&tbname=%s&tpwd=1&extsearch=1";
    	String apKey = "7918202b-ef4a-f251-291b-eb880302814c";
    	String tbname = "tb6746204";
    	JSONObject jsonResult;
		try {
			jsonResult = HttpUtil.sendGet(String.format(url, apKey,sourceTkl,pid,tbname));
			//logger.info("喵有券接口转换结果：" + jsonResult.toJSONString());
	    	if(jsonResult.getInteger("code") == 200) {
	    		return jsonResult.getJSONObject("data").getString("tpwd");
	    	}
	    	//logger.error("获取喵有券高佣转淘口令api接口错误，请立即排查处理，否则影响引导收入，失败原因:[{}]",DsErrorEnum.getByCode(jsonResult.getString("code")));
	    	return analyzeAndCreateNewTkl(sourceTkl);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return sourceTkl;
    }
    
    public static String analyzeAndCreateNewTkl(String sourceTkl) {
    	String analyzeUrl = "http://api.web.21ds.cn/taoke/jiexitkl?apkey=%s&kouling=%s";
    	String createUrl = "http://api.web.21ds.cn/taoke/createTaoPwd?apkey=%s&url=%s&title=%s&pic=%s";
    	String apKey = "7918202b-ef4a-f251-291b-eb880302814c";
    	try {
    		JSONObject analyzeJsonResult = HttpUtil.sendGet(String.format(analyzeUrl, apKey,sourceTkl));
    		if(analyzeJsonResult.getInteger("code") != 200) {
    			logger.error(analyzeJsonResult.toJSONString());
    			return sourceTkl;
	    	}
    		String url = URLEncoder.encode(analyzeJsonResult.getJSONObject("data").getString("url"),"utf-8");
    		String title = URLEncoder.encode("免单群910092655","utf-8");
    		String pic = URLEncoder.encode(analyzeJsonResult.getJSONObject("data").getString("pic"),"utf-8");
    		JSONObject createJsonResult = HttpUtil.sendGet(String.format(createUrl, apKey,url,title,pic));
    		if(createJsonResult.getInteger("code") != 200) {
    			logger.error(analyzeJsonResult.toJSONString());
    			return sourceTkl;
	    	}
    		return createJsonResult.getString("data");
    	} catch (Exception e) {
			e.printStackTrace();
			return sourceTkl;
		}
    }
    
    private String filterMsg(String msg) {
    	msg = msg.replaceAll("元", "").replaceAll("生活费147223", "生活费3925276");
    	for(Map.Entry<String, String> entry : CommandPlugin.filterMap.entrySet()){
    		msg = msg.replaceAll(entry.getKey(), entry.getValue());
    	}
    	return msg;
    }
}
