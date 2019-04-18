package com.lab1;

import java.io.Serializable;

/**
 * @Author HITLSQ
 * @Date 2019/4/15
 */

// 消息类（实现了Serializable接口）
public class Message implements Serializable {
    // Java的序列化机制通过判断类的serialVersionUID来验证版本一致性。
    private static final long serialVersionUID = -449233249590289596L;
    // 定义消息Id和消息内容
    private int messageId;
    private String messageContent;
    public Message(int messageId, String messageContent) {
        this.messageId = messageId;
        this.messageContent = messageContent;
    }
    public int getMessageId() {
        return messageId;
    }
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    public String getMessageContent() {
        return messageContent;
    }
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
