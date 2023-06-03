package com.test.chat.domain;



//服务器转发给服务器的数据
public class ResultMessage {
    private boolean isSystem;//是否是系统通知信息。
    private String fromName;
    private Object message;

    public boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean system) {
        isSystem = system;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
