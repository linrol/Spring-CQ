package xin.lz1998.cq.event.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xin.lz1998.cq.entity.CQUser;

public class CQPrivateMessageEvent extends CQMessageEvent {

  @JSONField(name = "sub_type")
  private String subType;
  @JSONField(name = "sender")
  private CQUser sender;

  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public CQUser getSender() {
    return sender;
  }

  public void setSender(CQUser sender) {
    this.sender = sender;
  }
}
