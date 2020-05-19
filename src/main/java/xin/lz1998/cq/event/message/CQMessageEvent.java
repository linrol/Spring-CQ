package xin.lz1998.cq.event.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xin.lz1998.cq.event.CQEvent;

public class CQMessageEvent extends CQEvent {

  @JSONField(name = "message_type")
  private String messageType;
  @JSONField(name = "message_id")
  private int messageId;
  @JSONField(name = "user_id")
  private long userId;
  @JSONField(name = "message")
  private String message;
  @JSONField(name = "raw_message")
  private String rawMessage;
  @JSONField(name = "font")
  private int font;

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getRawMessage() {
    return rawMessage;
  }

  public void setRawMessage(String rawMessage) {
    this.rawMessage = rawMessage;
  }

  public int getFont() {
    return font;
  }

  public void setFont(int font) {
    this.font = font;
  }
}
