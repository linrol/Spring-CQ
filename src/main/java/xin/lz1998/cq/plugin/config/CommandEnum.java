package xin.lz1998.cq.plugin.config;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 指令枚举配置类
 * @author linrol
 *
 */
@Getter
@AllArgsConstructor
public enum CommandEnum {
	CONTROLLER_QQ_LIST("controller_qq_list", "主人QQ列表"),
    CONTROLLER_QQ_ADD("controller_qq_add", "主人QQ添加"),
    CONTROLLER_QQ_REMOVE("controller_qq_remove", "主人QQ移除"),
    OPEN_PLUGIN("open_plugin", "启用插件"),
    SHUTDOWN_PLUGIN("shutdown_plugin", "停用插件"),
    MONITOR_GROUP_ID_LIST("monitor_group_id_list", "监听QQ群号列表"),
    MONITOR_GROUP_ID_ADD("monitor_group_id_add", "监听QQ群号添加"),
    MONITOR_GROUP_ID_REMOVE("monitor_group_id_remove", "监听QQ群号移除"),
    FORWARD_GROUP_ID_LIST("forward_group_id_list", "转发QQ群号列表"),
    FORWARD_GROUP_ID_ADD("forward_group_id_add", "转发QQ群号添加"),
    FORWARD_GROUP_ID_REMOVE("forward_group_id_remove", "转发QQ群号移除");

    private String command;
    private String desc;
    
    public static CommandEnum getCommandEnum(String command) {
    	if(StringUtils.isEmpty(command)) {
    		return null;
    	}
    	for(CommandEnum commandEnum:CommandEnum.values()) {
    		if(command.equals(commandEnum.getCommand()) || command.equals(commandEnum.getDesc())) {
    			return commandEnum;
    		}
    	}
    	return null;
    }
    
}
