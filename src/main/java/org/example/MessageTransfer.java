package org.example;

import org.example.model.ElementState;
import org.example.model.Gateway;
import org.example.model.Message;
import org.example.model.StateMemory;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

@Contract(
        name = "basic",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary message transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))

@Default
public class MessageTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum MsgTransferErrors {
        MESSAGE_NOT_FOUND,
        MESSAGE_ALREADY_EXISTS,
        MESSAGE_TRANSFER_FAILED,
        GATEWAY_NOT_EXISTS,
        GATEWAY_NOT_FOUND,
        GATEWAY_TRANSFER_FAILED
    }

    public static String startID;

    private StateMemory currentMemory = new StateMemory();

    private String mockFireflyID = "0x";     // mock firefly id

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateGateway(ctx,"ExclusiveGateway_0hs3ztq" , ElementState.ENABLE);
        CreateGateway(ctx,"ExclusiveGateway_106je4z" , ElementState.DISABLE);
        CreateGateway(ctx,"EventBasedGateway_1fxpmyn" , ElementState.DISABLE);
        CreateGateway(ctx,"ExclusiveGateway_0nzwv7v" , ElementState.DISABLE);
//        CreateGateway(ctx,"EndEvent_0366pfz" , ElementState.DISABLE);

        CreateMessage(ctx, "Message_1em0ee4", "", "", "", ElementState.DISABLE);
        CreateMessage(ctx, "Message_1nlagx2", "", "", "",ElementState.DISABLE);
        CreateMessage(ctx, "Message_045i10y", "", "", "",ElementState.DISABLE);
        CreateMessage(ctx, "Message_0r9lypd", "", "", "",ElementState.DISABLE);
        CreateMessage(ctx, "Message_0o8eyir", "", "", "",ElementState.DISABLE);
        CreateMessage(ctx, "Message_1xm9dxy", "", "", "",ElementState.DISABLE);
        CreateMessage(ctx, "Message_1ljlm4g", "", "", "",ElementState.DISABLE);
        CreateMessage(ctx, "Message_05isfw9", "", "", "",ElementState.DISABLE);
        CreateMessage(ctx, "Message_1joj7ca", "", "", "", ElementState.DISABLE);
        CreateMessage(ctx, "Message_1etcmvl", "", "", "", ElementState.DISABLE);

        ExclusiveGateway_0hs3ztq(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Gateway CreateGateway(final Context ctx, final String gatewayID, final ElementState gatewayState) {
        ChaincodeStub stub = ctx.getStub();

        if (MsgExists(ctx, gatewayID)) {
            String errorMessage = String.format("Msg %s already exists", gatewayID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.GATEWAY_NOT_EXISTS.toString());
        }

        Gateway gtw = new Gateway(gatewayID, gatewayState);
        // Use Genson to convert the Message into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(gtw);
        stub.putStringState(gatewayID, sortedJson);

        return gtw;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Message CreateMessage(final Context ctx, final String messageID, final String sendMspID, final String receiveMspID,
                                 final String fireflyTranID, final ElementState msgState) {
        ChaincodeStub stub = ctx.getStub();

        if (MsgExists(ctx, messageID)) {
            String errorMessage = String.format("Msg %s already exists", messageID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_ALREADY_EXISTS.toString());
        }

        Message msg = new Message(messageID, sendMspID, receiveMspID, fireflyTranID, msgState);
        // Use Genson to convert the Message into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(msg);
        stub.putStringState(messageID, sortedJson);

        return msg;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Message ReadMsg(final Context ctx, final String messageID) {
        ChaincodeStub stub = ctx.getStub();
        String messageJSON = stub.getStringState(messageID);

        if (messageJSON == null || messageJSON.isEmpty()) {
            String errorMessage = String.format("Msg %s does not exist", messageID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_NOT_FOUND.toString());
        }

        Message msg = genson.deserialize(messageJSON, Message.class);
        return msg;
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void ChangeMsgState(final Context ctx, final String messageID, ElementState msgState) {
        //only change state
        ChaincodeStub stub = ctx.getStub();
        Message  msg = ReadMsg(ctx, messageID);
        msg.setMsgState(msgState);
        String sortedJson = genson.serialize(msg);
        stub.putStringState(messageID, sortedJson);

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void ChangeGtwState(final Context ctx, final String gatewayID, ElementState gtwState) {
        //only change state
        ChaincodeStub stub = ctx.getStub();
        Gateway  msg = ReadGtw(ctx, gatewayID);
        msg.setGatewayState(gtwState);
        String sortedJson = genson.serialize(msg);
        stub.putStringState(gatewayID, sortedJson);

    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Gateway ReadGtw(final Context ctx, final String gatewayID) {
        ChaincodeStub stub = ctx.getStub();
        String gatewayJSON = stub.getStringState(gatewayID);

        if (gatewayJSON == null || gatewayJSON.isEmpty()) {
            String errorMessage = String.format("Gateway %s does not exist", gatewayID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.GATEWAY_NOT_FOUND.toString());
        }

        Gateway gtw = genson.deserialize(gatewayJSON, Gateway.class);
        return gtw;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean MsgExists(final Context ctx, final String messageID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(messageID);

        return (assetJSON != null && !assetJSON.isEmpty());

    }

    //====================================================================================================================================================

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Msg_Message_1pam53q_Complete(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1pam53q");

        if (msg.getMsgState() != ElementState.ENABLE) {
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1pam53q", sortedJson);

        Message  msg2 = ReadMsg(ctx, "Message_1rnq4x3");
        msg2.setMsgState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_1rnq4x3", sortedJson2);

        Msg_Message_1rnq4x3_Complete(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Msg_Message_1rnq4x3_Complete(final Context ctx) {       //还可补充消息中的参数
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1rnq4x3");

        if (msg.getMsgState() != ElementState.ENABLE) {
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1rnq4x3", sortedJson);

//        Message  msg2 = ReadMsg(ctx, "Message_0plbqmg");
//        msg2.setMsgState(Message.state.ENABLE);
//        String sortedJson2 = genson.serialize(msg2);
//        stub.putStringState("Message_0plbqmg", sortedJson2);
        //下一个为网关
        Gateway_Gateway_197f4ys_Complete(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Msg_Message_0plbqmg_Complete(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_0plbqmg");

        if (msg.getMsgState() != ElementState.ENABLE) {
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_0plbqmg", sortedJson);

    }

    //不添加默认查询
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    private void Gateway_Gateway_197f4ys_Complete(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        //exclusive 参数择路
        Message  msg2 = ReadMsg(ctx, "Message_0plbqmg");
        msg2.setMsgState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_0plbqmg", sortedJson2);
        //一个结束事件

        //

        Msg_Message_0plbqmg_Complete(ctx);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void ExclusiveGateway_0hs3ztq(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Gateway gtw = ReadGtw(ctx, "ExclusiveGateway_0hs3ztq");

        if(gtw.getGatewayState()!=ElementState.ENABLE){
            String errorMessage = String.format("Gateway state %s does not allowed", gtw.getGatewayID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.GATEWAY_TRANSFER_FAILED.toString());
        }

        gtw.setGatewayState(ElementState.DONE);
        String sortedJson = genson.serialize(gtw);
        stub.putStringState("ExclusiveGateway_0hs3ztq", sortedJson);

        Message msg2 = ReadMsg(ctx, "Message_045i10y");
        msg2.setMsgState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_045i10y", sortedJson2);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_045i10y(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_045i10y");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_045i10y", sortedJson);

        Message  msg2 = ReadMsg(ctx, "Message_0r9lypd");
        msg2.setMsgState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_0r9lypd", sortedJson2);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_0r9lypd(final Context ctx, boolean confirm) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_0r9lypd");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_0r9lypd", sortedJson);

        currentMemory.setConfirm(confirm);

        Gateway gtw = ReadGtw(ctx, "ExclusiveGateway_106je4z");
        gtw.setGatewayState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(gtw);
        stub.putStringState("ExclusiveGateway_106je4z", sortedJson2);

        ExclusiveGateway_106je4z(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void ExclusiveGateway_106je4z(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Gateway gtw = ReadGtw(ctx, "ExclusiveGateway_106je4z");

        if(gtw.getGatewayState()!=ElementState.ENABLE){
            String errorMessage = String.format("Gateway state %s does not allowed", gtw.getGatewayID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.GATEWAY_TRANSFER_FAILED.toString());
        }

        gtw.setGatewayState(ElementState.DONE);
        String sortedJson = genson.serialize(gtw);
        stub.putStringState("ExclusiveGateway_106je4z", sortedJson);

        if(true == currentMemory.isConfirm()){
            Message  msg2 = ReadMsg(ctx, "Message_1em0ee4");
            msg2.setMsgState(ElementState.ENABLE);
            String sortedJson2 = genson.serialize(msg2);
            stub.putStringState("Message_1em0ee4", sortedJson2);
        } else {
            Gateway gtw2 = ReadGtw(ctx, "ExclusiveGateway_0hs3ztq");
            gtw2.setGatewayState(ElementState.ENABLE);
            String sortedJson2 = genson.serialize(gtw2);
            stub.putStringState("ExclusiveGateway_0hs3ztq", sortedJson2);

            ExclusiveGateway_0hs3ztq(ctx);
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1em0ee4(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1em0ee4");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1em0ee4", sortedJson);

        ChangeMsgState(ctx,"Message_1nlagx2",ElementState.ENABLE);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1nlagx2(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1nlagx2");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1nlagx2", sortedJson);

        ChangeGtwState(ctx,"EventBasedGateway_1fxpmyn",ElementState.ENABLE);

        EventBasedGateway_1fxpmyn(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void EventBasedGateway_1fxpmyn(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Gateway gtw = ReadGtw(ctx, "EventBasedGateway_1fxpmyn");

        if(gtw.getGatewayState()!=ElementState.ENABLE){
            String errorMessage = String.format("Gateway state %s does not allowed", gtw.getGatewayID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.GATEWAY_TRANSFER_FAILED.toString());
        }

        gtw.setGatewayState(ElementState.DONE);
        String sortedJson = genson.serialize(gtw);
        stub.putStringState("EventBasedGateway_1fxpmyn", sortedJson);

        ChangeMsgState(ctx,"Message_0o8eyir",ElementState.ENABLE);
        ChangeMsgState(ctx,"Message_1xm9dxy",ElementState.ENABLE);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_0o8eyir(final Context ctx, boolean cancel) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_0o8eyir");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_0o8eyir", sortedJson);

        ChangeMsgState(ctx,"Message_1xm9dxy",ElementState.DISABLE);
        ChangeGtwState( ctx,"ExclusiveGateway_0nzwv7v",ElementState.ENABLE);

        //中间transfer过程设计以太币先省略，直接跳到网关
        currentMemory.setCancel(cancel);

        ExclusiveGateway_0nzwv7v(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void ExclusiveGateway_0nzwv7v(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Gateway gtw = ReadGtw(ctx, "ExclusiveGateway_0nzwv7v");

        if(gtw.getGatewayState()!=ElementState.ENABLE){
            String errorMessage = String.format("Gateway state %s does not allowed", gtw.getGatewayID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.GATEWAY_TRANSFER_FAILED.toString());
        }

        gtw.setGatewayState(ElementState.DONE);
        String sortedJson = genson.serialize(gtw);
        stub.putStringState("ExclusiveGateway_0nzwv7v", sortedJson);

        if(true == currentMemory.isCancel()){
            Message  msg2 = ReadMsg(ctx, "Message_1joj7ca");
            msg2.setMsgState(ElementState.ENABLE);
            String sortedJson2 = genson.serialize(msg2);
            stub.putStringState("Message_1joj7ca", sortedJson2);
        } else {
            //done
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1joj7ca(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1joj7ca");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1joj7ca", sortedJson);

        ChangeMsgState(ctx,"Message_1etcmvl",ElementState.ENABLE);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1etcmvl(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1etcmvl");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1etcmvl", sortedJson);

        //done
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1xm9dxy(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1xm9dxy");

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1xm9dxy", sortedJson);

        //done
    }

}
