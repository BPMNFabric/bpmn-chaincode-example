package org.example;

import java.util.Objects;

public class Message {
    private String messageID;
    private String sendMsgID;
    private String receiveMsgID;
    private String fireflyTranID;
    private ElementState msgState;

    public Message(String messageID, String sendMsgID, String receiveMsgID, String fireflyTranID, ElementState msgState) {
        this.messageID = messageID;
        this.sendMsgID = sendMsgID;
        this.receiveMsgID = receiveMsgID;
        this.fireflyTranID = fireflyTranID;
        this.msgState = msgState;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSendMsgID() {
        return sendMsgID;
    }

    public void setSendMsgID(String sendMsgID) {
        this.sendMsgID = sendMsgID;
    }

    public String getReceiveMsgID() {
        return receiveMsgID;
    }

    public void setReceiveMsgID(String receiveMsgID) {
        this.receiveMsgID = receiveMsgID;
    }

    public String getFireflyTranID() {
        return fireflyTranID;
    }

    public void setFireflyTranID(String fireflyTranID) {
        this.fireflyTranID = fireflyTranID;
    }

    public ElementState getMsgState() {
        return msgState;
    }

    public void setMsgState(ElementState msgState) {
        this.msgState = msgState;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Message other = (Message) obj;

        return Objects.deepEquals(
                new String[] {getMessageID(), getSendMsgID(), getReceiveMsgID(), getFireflyTranID()},
                new String[] {other.getMessageID(), other.getSendMsgID(), other.getReceiveMsgID(), other.getFireflyTranID()})
                &&
                Objects.deepEquals(
                        new int[] {getMsgState().ordinal()},
                        new int[] {other.getMsgState().ordinal()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(Objects.hashCode(getMessageID() + getSendMsgID() + getReceiveMsgID() + getFireflyTranID()), Objects.hashCode(getMsgState().ordinal()));
    }


}
