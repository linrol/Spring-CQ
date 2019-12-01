package xin.lz1998.cq.plugin.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xin.lz1998.cq.event.message.CQPrivateMessageEvent;
import xin.lz1998.cq.robot.CQPlugin;
import xin.lz1998.cq.robot.CoolQ;

/**
 * 配置插件
 * @author linrol
 *
 */
public class CommandPlugin extends CQPlugin {
	
	public static Map<String,Object> config = new HashMap<String,Object>();
	
	static {
		List<Long> lcontrollerList = new ArrayList<Long>();
		lcontrollerList.add(1071893649l);
		config.put(CommandEnum.CONTROLLER_QQ_LIST.getCommand(), lcontrollerList);
		Map<Long,List<Long>> monitorGroupMap = new HashMap<Long,List<Long>>();
		List<Long> monitorGroupList = new ArrayList<Long>();
		monitorGroupList.add(737215804l);
		monitorGroupMap.put(2097736476l, monitorGroupList);
		
		List<Long> monitorGroupList2 = new ArrayList<Long>();
		monitorGroupList2.add(286624903l);
		monitorGroupMap.put(779721310l, monitorGroupList2);
		config.put(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand(), monitorGroupMap);
		
		
		List<Long> forwardGroupList = new ArrayList<Long>();
		forwardGroupList.add(910092655l);
		forwardGroupList.add(198896490l);
		config.put(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand(), forwardGroupList);
    }
	
	private Logger logger = LoggerFactory.getLogger(CommandPlugin.class);
	
	@Override
	@SuppressWarnings("unchecked")
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
		//long userId = event.getSender().getUserId();
		String msg = event.getMessage();
		List<Long> controllerQQlist = (List<Long>) config.get(CommandEnum.CONTROLLER_QQ_LIST.getCommand());
		if(!msg.startsWith("指令-")) {
			return MESSAGE_IGNORE;
		}
		String[] msgs = msg.split("-");
		CommandEnum commandEnum = CommandEnum.getCommandEnum(msgs[1]);
		if(commandEnum == null || msgs.length != 3) {
			logger.error("未知指令或指令格式不对:{}",msg);
			// cq.sendPrivateMsg(userId, "未知指令或指令格式不对", false);
			return MESSAGE_BLOCK;
		}
        if(commandEnum == CommandEnum.CONTROLLER_QQ_ADD && !controllerQQlist.contains(Long.parseLong(msgs[2]))) {
        	controllerQQlist.add(Long.parseLong(msgs[2]));
        	config.put(CommandEnum.CONTROLLER_QQ_LIST.getCommand(), controllerQQlist);
        	// cq.sendPrivateMsg(userId, "主人[" + msgs[2] + "]已添加,即刻开始enjoy吧!", false);
        	logger.info("主人QQ[{}]已添加,即刻开始enjoy吧!",msgs[2]);
			return MESSAGE_BLOCK;
        }else if(commandEnum == CommandEnum.MONITOR_GROUP_ID_ADD) {
        	Map<Long,List<Long>> monitorGroupMap = (Map<Long,List<Long>>) config.get(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand());
        	List<Long> monitorGrouplist = monitorGroupMap.get(cq.getSelfId());
        	if(monitorGrouplist.contains(Long.parseLong(msgs[2]))){
        		logger.error("监听QQ群号[{}]已在列表中，请不要重复添加",msgs[2]);
        		return MESSAGE_BLOCK;
        	}
        	monitorGrouplist.add(Long.parseLong(msgs[2]));
        	monitorGroupMap.put(cq.getSelfId(), monitorGrouplist);
        	config.put(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand(), monitorGroupMap);
        	logger.info("监听QQ群号[{}]已添加",msgs[2]);
        	// cq.sendPrivateMsg(userId, "监听G[" + msgs[2] + "]已添加", false);
			return MESSAGE_BLOCK;
        }else if(commandEnum == CommandEnum.MONITOR_GROUP_ID_REMOVE) {
        	Map<Long,List<Long>> monitorGroupMap = (Map<Long,List<Long>>) config.get(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand());
        	List<Long> monitorGrouplist = monitorGroupMap.get(cq.getSelfId());
        	if(!monitorGrouplist.contains(Long.parseLong(msgs[2]))){
        		logger.error("监听QQ群号[{}]已不在列表中，无需移除",msgs[2]);
        		return MESSAGE_BLOCK;
        	}
        	monitorGrouplist.remove(Long.parseLong(msgs[2]));
        	monitorGroupMap.put(cq.getSelfId(), monitorGrouplist);
        	config.put(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand(), monitorGroupMap);
        	logger.info("监听QQ群号[{}]已移除",msgs[2]);
        	// cq.sendPrivateMsg(userId, "监听G[" + msgs[2] + "]已添加", false);
			return MESSAGE_BLOCK;
        }else if(commandEnum == CommandEnum.FORWARD_GROUP_ID_ADD) {
        	List<Long> forwardGrouplist = (List<Long>) config.get(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand());
        	if(forwardGrouplist.contains(Long.parseLong(msgs[2]))){
        		logger.error("转发QQ群号[{}]已在列表中，请不要重复添加",msgs[2]);
        		return MESSAGE_BLOCK;
        	}
        	forwardGrouplist.add(Long.parseLong(msgs[2]));
        	config.put(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand(), forwardGrouplist);
        	logger.info("转发QQ群号[{}]已添加",msgs[2]);
        	// cq.sendPrivateMsg(userId, "转发G[" + msgs[2] + "]已添加", false);
			return MESSAGE_BLOCK;
        }else if(commandEnum == CommandEnum.FORWARD_GROUP_ID_REMOVE) {
        	List<Long> forwardGrouplist = (List<Long>) config.get(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand());
        	if(!forwardGrouplist.contains(Long.parseLong(msgs[2]))){
        		logger.error("转发QQ群号[{}]已不在列表中，无需移除",msgs[2]);
        		return MESSAGE_BLOCK;
        	}
        	forwardGrouplist.remove(Long.parseLong(msgs[2]));
        	config.put(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand(), forwardGrouplist);
        	logger.info("转发QQ群号[{}]已移除",msgs[2]);
        	// cq.sendPrivateMsg(userId, "转发G[" + msgs[2] + "]已添加", false);
			return MESSAGE_BLOCK;
        }else {
        	logger.error("指令正在{}开发中...",commandEnum.getDesc());
        	// cq.sendPrivateMsg(userId, "指令正在{" + commandEnum.getDesc() + "}开发中...", false);
        }
        return MESSAGE_BLOCK; // 继续执行下一个插件
        // return MESSAGE_BLOCK; // 不执行下一个插件
    }
}
