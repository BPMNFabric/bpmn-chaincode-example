package org.example.model;

import org.example.model.ElementState;


public class Gateway {
    private String gatewayID;
    private ElementState gatewayState;

    public Gateway() {
    }

    public Gateway(String gatewayID, ElementState gatewayState) {
        this.gatewayID = gatewayID;
        this.gatewayState = gatewayState;
    }

    public String getGatewayID() {
        return gatewayID;
    }

    public void setGatewayID(String gatewayID) {
        this.gatewayID = gatewayID;
    }

    public ElementState getGatewayState() {
        return gatewayState;
    }

    public void setGatewayState(ElementState gatewayState) {
        this.gatewayState = gatewayState;
    }
}
