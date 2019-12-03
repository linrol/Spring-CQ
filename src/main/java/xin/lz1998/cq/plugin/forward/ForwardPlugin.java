package xin.lz1998.cq.plugin.forward;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * 转发插件
 * @author linrol
 *
 */
@SuppressWarnings("unchecked")
public class ForwardPlugin extends CQPlugin {
	
	private Logger logger = LoggerFactory.getLogger(ForwardPlugin.class);
	
	public static Map<String,String> pidMap = new HashMap<String,String>();
	
	public static Map<String,List<String>> monitorUserMap = new HashMap<String,List<String>>();
	
	private static ForyouStack<String> msgStack = new ForyouStack<String>(20);
	
	static {
		pidMap.put("910092655", "mm_109302870_1080150328_109752250051");
		pidMap.put("198896490", "mm_109302870_1090250211_109781850271");
		monitorUserMap.put("1706860030", Arrays.asList("3317628455","2267793115","1096471489"));
		monitorUserMap.put("779721310", Arrays.asList("1012230561","1256647017","1071893649"));
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
	
    
    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
    	logger.info("QQ:{}收到群:{}消息", cq.getSelfId(), event.getGroupId());
    	List<Long> monitorGrouplist = ((Map<Long,List<Long>>)CommandPlugin.config.get(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand())).get(cq.getSelfId());
    	long group_id = event.getGroupId();
    	String msg = filterMsg(event.getMessage());
    	//if(!monitorGrouplist.contains(group_id) || !monitorUserMap.get(String.valueOf(cq.getSelfId())).contains(String.valueOf(event.getUserId()))) {
    	if(!monitorGrouplist.contains(group_id)) {
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
    		//cq.sendGroupMsg(groupId, msg, false);
    	}
        return MESSAGE_IGNORE; // 继续执行下一个插件
        // return MESSAGE_BLOCK; // 不执行下一个插件
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
    			return sourceTkl;
	    	}
    		String url = URLEncoder.encode(analyzeJsonResult.getJSONObject("data").getString("url"),"utf-8");
    		String title = URLEncoder.encode("免单群910092655","utf-8");
    		String pic = URLEncoder.encode(analyzeJsonResult.getJSONObject("data").getString("pic"),"utf-8");
    		JSONObject createJsonResult = HttpUtil.sendGet(String.format(createUrl, apKey,url,title,pic));
    		if(createJsonResult.getInteger("code") != 200) {
    			return sourceTkl;
	    	}
    		return createJsonResult.getString("data");
    	} catch (Exception e) {
			e.printStackTrace();
			return sourceTkl;
		}
    }
    
    private String filterMsg(String msg) {
    	return msg.replaceAll("元", "").replaceAll("生活费147223", "生活费3925276");
    }
}
