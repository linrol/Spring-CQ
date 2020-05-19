package xin.lz1998.cq.event.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xin.lz1998.cq.entity.CQGroupAnonymous;
import xin.lz1998.cq.entity.CQGroupUser;

public class CQGroupMessageEvent extends CQMessageEvent {

  @JSONField(name = "sub_type")
  private String subType;
  @JSONField(name = "group_id")
  private long groupId;
  @JSONField(name = "anonymous")
  private CQGroupAnonymous anonymous;
  @JSONField(name = "sender")
  private CQGroupUser sender;

  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public long getGroupId() {
    return groupId;
  }

  public void setGroupId(long groupId) {
    this.groupId = groupId;
  }

  public CQGroupAnonymous getAnonymous() {
    return anonymous;
  }

  public void setAnonymous(CQGroupAnonymous anonymous) {
    this.anonymous = anonymous;
  }

  public CQGroupUser getSender() {
    return sender;
  }

  public void setSender(CQGroupUser sender) {
    this.sender = sender;
  }
}
