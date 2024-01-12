package org.example;

import org.camunda.bpm.model.bpmn.instance.Gateway;
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
        MESSAGE_TRANSFER_FAILED
    }

    public static String startID;

    private StateMemory currentMemory;

    private String mockFireflyID = "0x";     // mock firefly id

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateMessage(ctx, "Message_1pam53q", "", "", "",Message.state.ENABLE);
        CreateMessage(ctx, "Message_1rnq4x3", "", "", "",Message.state.DISABLE);
        CreateMessage(ctx, "Message_0plbqmg", "", "", "",Message.state.DISABLE);

        Msg_Message_1pam53q_Complete(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Message CreateMessage(final Context ctx,  final String messageID, final String sendMsgID, final String receiveMsgID,
                              final String fireflyTranID, final Message.state msgState) {
        ChaincodeStub stub = ctx.getStub();

        if (MsgExists(ctx, messageID)) {
            String errorMessage = String.format("Msg %s already exists", messageID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_ALREADY_EXISTS.toString());
        }

        Message msg = new Message(messageID, sendMsgID, receiveMsgID, fireflyTranID, msgState);
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

        if (msg.getMsgState() != Message.state.ENABLE) {
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(Message.state.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1pam53q", sortedJson);

        Message  msg2 = ReadMsg(ctx, "Message_1rnq4x3");
        msg2.setMsgState(Message.state.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_1rnq4x3", sortedJson2);

        Msg_Message_1rnq4x3_Complete(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Msg_Message_1rnq4x3_Complete(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1rnq4x3");

        if (msg.getMsgState() != Message.state.ENABLE) {
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(Message.state.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1rnq4x3", sortedJson);

        Message  msg2 = ReadMsg(ctx, "Message_0plbqmg");
        msg2.setMsgState(Message.state.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_0plbqmg", sortedJson2);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Msg_Message_0plbqmg_Complete(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_0plbqmg");

        if (msg.getMsgState() != Message.state.ENABLE) {
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(Message.state.DONE);
        msg.setFireflyTranID(mockFireflyID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_0plbqmg", sortedJson);

    }


}
