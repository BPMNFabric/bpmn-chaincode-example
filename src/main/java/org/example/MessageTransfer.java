package org.example;

import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.example.model.*;
import org.hyperledger.fabric.contract.ClientIdentity;
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

import javax.swing.*;

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
        GATEWAY_TRANSFER_FAILED,
        CHAINCODE_HAS_INITED,
        EVENT_TRANSFER_FAILED
    }

    public Boolean isInited= false;

    private StateMemory currentMemory = new StateMemory();


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        //Determines whether the chain code is initialized
        if(isInited){
            String errorMessage = String.format("Chaincode has not  been inited");
            System.out.println("Chaincode Has Inited");
            throw new ChaincodeException(errorMessage, MsgTransferErrors.CHAINCODE_HAS_INITED.toString());
        }

        CreateActionEvent(ctx,"StartEvent_1jtgn3j",ElementState.ENABLE);

        CreateGateway(ctx,"ExclusiveGateway_0hs3ztq" , ElementState.DISABLE);
        CreateGateway(ctx,"ExclusiveGateway_106je4z" , ElementState.DISABLE);
        CreateGateway(ctx,"EventBasedGateway_1fxpmyn" , ElementState.DISABLE);
        CreateGateway(ctx,"ExclusiveGateway_0nzwv7v" , ElementState.DISABLE);
//        CreateGateway(ctx,"EndEvent_0366pfz" , ElementState.DISABLE);

//mspid    hotel:Participant_0sktaei       client:Participant_1080bkg
        CreateMessage(ctx, "Message_045i10y", "Participant_1080bkg", "Participant_0sktaei", "", ElementState.DISABLE);    // Check_room(string date, uint bedrooms)"
        CreateMessage(ctx, "Message_0r9lypd", "Participant_0sktaei", "Participant_1080bkg", "",ElementState.DISABLE);     //Give_availability(bool confirm)
        CreateMessage(ctx, "Message_1em0ee4", "Participant_0sktaei", "Participant_1080bkg", "",ElementState.DISABLE);     //Price_quotation(uint quotation)
        CreateMessage(ctx, "Message_1nlagx2", "Participant_1080bkg", "Participant_0sktaei", "",ElementState.DISABLE);     //Book_room(bool confirmation)
        CreateMessage(ctx, "Message_0o8eyir", "Participant_1080bkg", "Participant_0sktaei", "",ElementState.DISABLE);     //payment0(address payable to)
        CreateMessage(ctx, "Message_1ljlm4g", "Participant_0sktaei", "Participant_1080bkg", "",ElementState.DISABLE);     //Give_ID(string booking_id)
        CreateMessage(ctx, "Message_0m9p3da", "Participant_1080bkg", "Participant_0sktaei", "",ElementState.DISABLE);     //cancel_order(bool cancel)
        CreateMessage(ctx, "Message_1joj7ca", "Participant_1080bkg", "Participant_0sktaei", "",ElementState.DISABLE);     //ask_refund(string ID)
        CreateMessage(ctx, "Message_1etcmvl", "Participant_0sktaei", "Participant_1080bkg", "", ElementState.DISABLE);    //payment1(address payable to)
        CreateMessage(ctx, "Message_1xm9dxy", "Participant_1080bkg", "Participant_0sktaei", "", ElementState.DISABLE);    //Cancel_order(string motivation)

        CreateActionEvent(ctx,"EndEvent_146eii4",ElementState.DISABLE);
        CreateActionEvent(ctx,"EndEvent_08edp7f",ElementState.DISABLE);
        CreateActionEvent(ctx,"EndEvent_0366pfz",ElementState.DISABLE);

        isInited= true;
        StartEvent_1jtgn3j( ctx);


        stub.setEvent("initLedgerEvent", "Contract has been inited successfully".getBytes());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    private Gateway CreateGateway(final Context ctx, final String gatewayID, final ElementState gatewayState) {
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
    private ActionEvent CreateActionEvent(final Context ctx, final String eventID, final ElementState eventState){
        ChaincodeStub stub = ctx.getStub();

        ActionEvent actionEvent = new ActionEvent(eventID, eventState);
        String sortedJson = genson.serialize(actionEvent);
        stub.putStringState(eventID, sortedJson);

        return actionEvent;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    private Message CreateMessage(final Context ctx, final String messageID, final String sendMspID, final String receiveMspID,
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
    private void ChangeMsgState(final Context ctx, final String messageID, ElementState msgState) {
        //only change state
        ChaincodeStub stub = ctx.getStub();
        Message  msg = ReadMsg(ctx, messageID);
        msg.setMsgState(msgState);
        String sortedJson = genson.serialize(msg);
        stub.putStringState(messageID, sortedJson);

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    private void ChangeGtwState(final Context ctx, final String gatewayID, ElementState gtwState) {
        //only change state
        ChaincodeStub stub = ctx.getStub();
        Gateway  msg = ReadGtw(ctx, gatewayID);
        msg.setGatewayState(gtwState);
        String sortedJson = genson.serialize(msg);
        stub.putStringState(gatewayID, sortedJson);

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    private void ChangeEventState(final Context ctx, final String eventID, ElementState eventState) {
        //only change state
        ChaincodeStub stub = ctx.getStub();
        Message  msg = ReadMsg(ctx, eventID);
        msg.setMsgState(eventState);
        String sortedJson = genson.serialize(msg);
        stub.putStringState(eventID, sortedJson);

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
    public ActionEvent ReadEvent(final Context ctx, final String eventID) {
        ChaincodeStub stub = ctx.getStub();
        String eventJSON = stub.getStringState(eventID);

        if (eventJSON == null || eventJSON.isEmpty()) {
            String errorMessage = String.format("Event state %s does not allowed", eventID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.EVENT_TRANSFER_FAILED.toString());
        }

        ActionEvent event = genson.deserialize(eventJSON, ActionEvent.class);
        return event;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean MsgExists(final Context ctx, final String messageID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(messageID);

        return (assetJSON != null && !assetJSON.isEmpty());

    }

    //====================================================================================================================================================

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void StartEvent_1jtgn3j(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        ActionEvent actionEvent = ReadEvent(ctx, "StartEvent_1jtgn3j");

        if(actionEvent.getEventState()!=ElementState.ENABLE){
            String errorMessage = String.format("Event state %s does not allowed", actionEvent.getEventID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.EVENT_TRANSFER_FAILED.toString());
        }

        actionEvent.setEventState(ElementState.DONE);
        String sortedJson = genson.serialize(actionEvent);
        stub.putStringState("StartEvent_1jtgn3j", sortedJson);

        stub.setEvent("StartEvent_1jtgn3j", "Contract has been started successfully".getBytes());

        Gateway gtw = ReadGtw(ctx, "ExclusiveGateway_0hs3ztq");
        gtw.setGatewayState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(gtw);
        stub.putStringState("ExclusiveGateway_0hs3ztq", sortedJson2);

        ExclusiveGateway_0hs3ztq(ctx);
    }

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

        stub.setEvent( "ExclusiveGateway_0hs3ztq", "ExclusiveGateway_0hs3ztq has been done".getBytes());

        Message msg2 = ReadMsg(ctx, "Message_045i10y");
        msg2.setMsgState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_045i10y", sortedJson2);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_045i10y(final Context ctx, String fireflyTranID) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_045i10y");

        //TODO  待确认 如何确认有权限的mspid
        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_045i10y", sortedJson);

        stub.setEvent("Message_045i10y", "Message_045i10y has been done".getBytes());

        Message  msg2 = ReadMsg(ctx, "Message_0r9lypd");
        msg2.setMsgState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(msg2);
        stub.putStringState("Message_0r9lypd", sortedJson2);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_0r9lypd(final Context ctx, String fireflyTranID, boolean confirm) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_0r9lypd");

        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_0r9lypd", sortedJson);

        stub.setEvent( "Message_0r9lypd", "Message_0r9lypd has been done".getBytes());

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

        stub.setEvent( "ExclusiveGateway_106je4z", "ExclusiveGateway_106je4z has been done".getBytes());

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
    public void Message_1em0ee4(final Context ctx, String fireflyTranID) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1em0ee4");

        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1em0ee4", sortedJson);

        stub.setEvent( "Message_1em0ee4", "Message_1em0ee4 has been done".getBytes());

        ChangeMsgState(ctx,"Message_1nlagx2",ElementState.ENABLE);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1nlagx2(final Context ctx, String fireflyTranID) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1nlagx2");

        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1nlagx2", sortedJson);

        stub.setEvent( "Message_1nlagx2", "Message_1nlagx2 has been done".getBytes());

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

        stub.setEvent( "EventBasedGateway_1fxpmyn", "EventBasedGateway_1fxpmyn has been done".getBytes());

        ChangeMsgState(ctx,"Message_0o8eyir",ElementState.ENABLE);
        ChangeMsgState(ctx,"Message_1xm9dxy",ElementState.ENABLE);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_0o8eyir(final Context ctx, boolean cancel, String fireflyTranID) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_0o8eyir");

        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_0o8eyir", sortedJson);

        stub.setEvent( "Message_0o8eyir", "Message_0o8eyir has been done".getBytes());

        ChangeMsgState(ctx,"Message_1xm9dxy",ElementState.DISABLE);
        ChangeGtwState( ctx,"ExclusiveGateway_0nzwv7v",ElementState.ENABLE);

        //中间transfer两个消息交换过程设计以太币先省略，直接跳到网关

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

        stub.setEvent( "ExclusiveGateway_0nzwv7v", "ExclusiveGateway_0nzwv7v has been done".getBytes());

        if(true == currentMemory.isCancel()){
            Message  msg2 = ReadMsg(ctx, "Message_1joj7ca");
            msg2.setMsgState(ElementState.ENABLE);
            String sortedJson2 = genson.serialize(msg2);
            stub.putStringState("Message_1joj7ca", sortedJson2);
        } else {
            //done
            ActionEvent event = ReadEvent( ctx, "EndEvent_08edp7f");
            event.setEventState(ElementState.ENABLE);
            String sortedJson2 = genson.serialize(event);
            stub.putStringState("EndEvent_08edp7f", sortedJson2);

            EndEvent_08edp7f(ctx);
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1joj7ca(final Context ctx, String fireflyTranID) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1joj7ca");

        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1joj7ca", sortedJson);

        stub.setEvent( "Message_1joj7ca", "Message_1joj7ca has been done".getBytes());

        ChangeMsgState(ctx,"Message_1etcmvl",ElementState.ENABLE);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1etcmvl(final Context ctx, String fireflyTranID) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1etcmvl");

        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.DONE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1etcmvl", sortedJson);

        stub.setEvent( "Message_1etcmvl", "Message_1etcmvl has been done".getBytes());

        //done
        ActionEvent event = ReadEvent( ctx, "EndEvent_146eii4");
        event.setEventState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(event);
        stub.putStringState("EndEvent_146eii4", sortedJson2);

        EndEvent_146eii4(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void Message_1xm9dxy(final Context ctx, String fireflyTranID) {
        ChaincodeStub stub = ctx.getStub();
        Message msg = ReadMsg(ctx, "Message_1xm9dxy");

        ClientIdentity clientIdentity=ctx.getClientIdentity();
        String clientMspId =clientIdentity.getMSPID();
        if(clientMspId != msg.getsendMspID( )){
            String errorMessage = String.format("Msp denied");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        if(msg.getMsgState()!=ElementState.ENABLE){
            String errorMessage = String.format("Msg state %s does not allowed", msg.getMessageID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.MESSAGE_TRANSFER_FAILED.toString());
        }

        msg.setMsgState(ElementState.ENABLE);
        msg.setFireflyTranID(fireflyTranID);
        String sortedJson = genson.serialize(msg);
        stub.putStringState("Message_1xm9dxy", sortedJson);

        stub.setEvent( "Message_1xm9dxy", "Message_1xm9dxy has been done".getBytes());

        //done
        ActionEvent event = ReadEvent( ctx, "EndEvent_0366pfz");
        event.setEventState(ElementState.ENABLE);
        String sortedJson2 = genson.serialize(event);
        stub.putStringState("EndEvent_0366pfz", sortedJson2);

        EndEvent_0366pfz(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void EndEvent_08edp7f(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        ActionEvent event = ReadEvent(ctx, "EndEvent_08edp7f");

        if(event.getEventState()!=ElementState.ENABLE){
            String errorMessage = String.format("Event state %s does not allowed", event.getEventID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.EVENT_TRANSFER_FAILED.toString());
        }

        event.setEventState(ElementState.DONE);
        String sortedJson = genson.serialize(event);
        stub.putStringState("EndEvent_08edp7f", sortedJson);

        stub.setEvent( "EndEvent_08edp7f", "EndEvent_08edp7f has been done".getBytes());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void EndEvent_146eii4(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        ActionEvent event = ReadEvent(ctx, "EndEvent_146eii4");

        if(event.getEventState()!=ElementState.ENABLE){
            String errorMessage = String.format("Event state %s does not allowed", event.getEventID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.EVENT_TRANSFER_FAILED.toString());
        }

        event.setEventState(ElementState.DONE);
        String sortedJson = genson.serialize(event);
        stub.putStringState("EndEvent_146eii4", sortedJson);

        stub.setEvent( "EndEvent_146eii4", "EndEvent_146eii4 has been done".getBytes());

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void EndEvent_0366pfz(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        ActionEvent event = ReadEvent(ctx, "EndEvent_0366pfz");

        if(event.getEventState()!=ElementState.ENABLE){
            String errorMessage = String.format("Event state %s does not allowed", event.getEventID());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MsgTransferErrors.EVENT_TRANSFER_FAILED.toString());
        }

        event.setEventState(ElementState.DONE);
        String sortedJson = genson.serialize(event);
        stub.putStringState("EndEvent_0366pfz", sortedJson);

        stub.setEvent( "EndEvent_0366pfz", "EndEvent_0366pfz has been done".getBytes());
    }

}
