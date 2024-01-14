package org.example.model;

import org.example.model.ElementState;

/**
 * description: add a description [描述信息]
 *
 * @author Ruan [作者]
 * @version 1.0.0 [版本信息]
 * @date 2024/01/14 18:27:38 [时间，这里是年/月/日 时:分:秒的格式]
 */
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
