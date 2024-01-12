package org.example;

public class StateMemory {
    private String product = "";
    private int price = 0;
    private boolean accepted = false;
    private Boolean reiterate = false;
    private String motivation = "";
    private String shipAddress = "";
    private int amount = 0;
    private String shipInfo = "";
    private String invoiceInfo = "";


    public StateMemory(String product, int price, Boolean accepted, Boolean reiterate, String motivation, String shipAddress, int amount, String shipInfo, String invoiceInfo) {
        this.product = product;
        this.price = price;
        this.accepted = accepted;
        this.reiterate = reiterate;
        this.motivation = motivation;
        this.shipAddress = shipAddress;
        this.amount = amount;
        this.shipInfo = shipInfo;
        this.invoiceInfo = invoiceInfo;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Boolean getReiterate() {
        return reiterate;
    }

    public void setReiterate(Boolean reiterate) {
        this.reiterate = reiterate;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getShipInfo() {
        return shipInfo;
    }

    public void setShipInfo(String shipInfo) {
        this.shipInfo = shipInfo;
    }

    public String getInvoiceInfo() {
        return invoiceInfo;
    }

    public void setInvoiceInfo(String invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }


}
