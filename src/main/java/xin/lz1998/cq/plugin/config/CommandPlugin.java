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
		List<Long> list = new ArrayList<Long>();
		list.add(1071893649l);
		config.put(CommandEnum.CONTROLLER_QQ_LIST.getCommand(), list);
		config.put(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand(), new ArrayList<Long>());
		config.put(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand(), new ArrayList<Long>());
    }
	
	private Logger logger = LoggerFactory.getLogger(CommandPlugin.class);
	
	@Override
	@SuppressWarnings("unchecked")
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
		long userId = event.getSender().getUserId();
		String msg = event.getMessage();
		List<Long> controllerQQlist = (List<Long>) config.get(CommandEnum.CONTROLLER_QQ_LIST.getCommand());
		if(!controllerQQlist.contains(userId) || !msg.startsWith("指令-")) {
			return MESSAGE_IGNORE;
		}
		String[] msgs = msg.split("-");
		CommandEnum commandEnum = CommandEnum.getCommandEnum(msgs[1]);
		if(commandEnum == null || msgs.length != 3) {
			logger.error("未知指令或指令格式不对:{}",msg);
			cq.sendPrivateMsg(userId, "未知指令或指令格式不对", false);
			return MESSAGE_BLOCK;
		}
        if(commandEnum == CommandEnum.CONTROLLER_QQ_ADD) {
        	controllerQQlist.add(Long.parseLong(msgs[2]));
        	config.put(CommandEnum.CONTROLLER_QQ_LIST.getCommand(), controllerQQlist);
        	cq.sendPrivateMsg(userId, "主人QQ[" + msgs[2] + "]已添加,即刻开始enjoy吧!", false);
        	logger.info("主人QQ[{}]已添加,即刻开始enjoy吧!",msgs[2]);
			return MESSAGE_BLOCK;
        }
        if(commandEnum == CommandEnum.MONITOR_GROUP_ID_ADD) {
        	List<Long> monitorGrouplist = (List<Long>) config.get(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand());
        	monitorGrouplist.add(Long.parseLong(msgs[2]));
        	config.put(CommandEnum.MONITOR_GROUP_ID_LIST.getCommand(), monitorGrouplist);
        	cq.sendPrivateMsg(userId, "监听群QQ号[" + msgs[2] + "]已添加", false);
        	logger.info("监听群QQ号[{}]已添加",msgs[2]);
			return MESSAGE_BLOCK;
        }
        if(commandEnum == CommandEnum.FORWARD_GROUP_ID_LIST) {
        	List<Long> forwardGrouplist = (List<Long>) config.get(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand());
        	forwardGrouplist.add(Long.parseLong(msgs[2]));
        	config.put(CommandEnum.FORWARD_GROUP_ID_LIST.getCommand(), forwardGrouplist);
        	cq.sendPrivateMsg(userId, "转发群QQ号[" + msgs[2] + "]已添加", false);
        	logger.info("转发群QQ号[{}]已添加",msgs[2]);
			return MESSAGE_BLOCK;
        }
        return MESSAGE_BLOCK; // 继续执行下一个插件
        // return MESSAGE_BLOCK; // 不执行下一个插件
    }
}
