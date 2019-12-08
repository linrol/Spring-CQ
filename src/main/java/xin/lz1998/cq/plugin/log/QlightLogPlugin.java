package xin.lz1998.cq.plugin.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xin.lz1998.cq.robot.qlight.Qlight;
import xin.lz1998.cq.robot.qlight.QlightPlugin;

// 这个插件用于记录日志
public class QlightLogPlugin extends QlightPlugin {

    private Logger logger = LoggerFactory.getLogger(QlightLogPlugin.class);

    @Override
    public int onPrivateMessage(Qlight qlight,JSONObject jsonData) {
        logger.info("qlight:{} jsonData:{}", JSON.toJSONString(qlight), jsonData.toString());
        qlight.sendPrivateMsg(jsonData.getJSONObject("params").getString("qq"), jsonData.getJSONObject("params").getString("content"));
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(Qlight qlight,JSONObject jsonData) {
    	logger.info("qlight:{} jsonData:{}", JSON.toJSONString(qlight), jsonData.toString());
        return MESSAGE_IGNORE;
    }
}
