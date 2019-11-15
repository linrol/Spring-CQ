package xin.lz1998.cq.plugin.forward;

import xin.lz1998.cq.event.message.CQGroupMessageEvent;
import xin.lz1998.cq.robot.CQPlugin;
import xin.lz1998.cq.robot.CoolQ;

/**
 * 转发插件
 * @author linrol
 *
 */
public class ForwardPlugin extends CQPlugin {
	
	// 来源群
	private long sourceGroupId = 963550879;
	
	// 待转发的群
	private long forwardGroupId = 425239590;
    
    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
    	long group_id = event.getGroupId();
    	String msg = event.getMessage();
    	if(group_id != sourceGroupId || !msg.contains("￥")) {
    		return MESSAGE_IGNORE;
    	}
    	cq.sendGroupMsg(forwardGroupId, msg, false);
        return MESSAGE_IGNORE; // 继续执行下一个插件
        // return MESSAGE_BLOCK; // 不执行下一个插件
    }
}
