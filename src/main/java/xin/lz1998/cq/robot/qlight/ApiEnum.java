package xin.lz1998.cq.robot.qlight;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiEnum {
    SEND_MSG("sendMessage", "发送消息"),
    ADD_FRIEND("addFriend", "添加好友"),
    INVITE_INTO_GROUP("inviteIntoGroup", "邀请好友入群"),
    GET_GROUP_MEMBER_LIST("getGroupMemberList", "获取群成员列表")
    ;
    private String url;
    private String desc;
}
