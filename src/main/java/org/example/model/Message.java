package org.example.model;

import java.util.Objects;

public class Message {
    private String messageID;
    private String sendMspID;
    private String receiveMspID;
    private String fireflyTranID;
    private ElementState msgState;

    public Message(String messageID, String sendMspID, String receiveMspID, String fireflyTranID, ElementState msgState) {
        this.messageID = messageID;
        this.sendMspID = sendMspID;
        this.receiveMspID = receiveMspID;
        this.fireflyTranID = fireflyTranID;
        this.msgState = msgState;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getsendMspID() {
        return sendMspID;
    }

    public void setsendMspID(String sendMspID) {
        this.sendMspID = sendMspID;
    }

    public String getreceiveMspID() {
        return receiveMspID;
    }

    public void setreceiveMspID(String receiveMspID) {
        this.receiveMspID = receiveMspID;
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
                new String[] {getMessageID(), getsendMspID(), getreceiveMspID(), getFireflyTranID()},
                new String[] {other.getMessageID(), other.getsendMspID(), other.getreceiveMspID(), other.getFireflyTranID()})
                &&
                Objects.deepEquals(
                        new int[] {getMsgState().ordinal()},
                        new int[] {other.getMsgState().ordinal()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(Objects.hashCode(getMessageID() + getsendMspID() + getreceiveMspID() + getFireflyTranID()), Objects.hashCode(getMsgState().ordinal()));
    }


}
