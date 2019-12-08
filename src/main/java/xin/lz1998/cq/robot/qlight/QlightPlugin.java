package xin.lz1998.cq.robot.qlight;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class QlightPlugin {
    public static final int MESSAGE_BLOCK = 1;
    public static final int MESSAGE_IGNORE = 0;

    public int onPrivateMessage(Qlight qlight,JSONObject jsonData) {
        return MESSAGE_IGNORE;
    }

    public int onGroupMessage(Qlight qlight,JSONObject jsonData) {
        return MESSAGE_IGNORE;
    }
}
