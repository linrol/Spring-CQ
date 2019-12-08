package xin.lz1998.cq.robot.qlight;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiEnum {
    SEND_MSG("sendMessage", "发送消息");
    private String url;
    private String desc;
}
