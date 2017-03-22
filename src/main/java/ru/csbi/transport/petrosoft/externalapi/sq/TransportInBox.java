package ru.csbi.transport.petrosoft.externalapi.sq;

public class TransportInBox {

  String transportExternalId;


  public String getTransportExternalId() {
    return transportExternalId;
  }

  public void setTransportExternalId(String transportExternalId) {
    this.transportExternalId = transportExternalId;
  }

  public TransportInBox(String transporExternalId) {
    this.transportExternalId = transporExternalId;
  }
}