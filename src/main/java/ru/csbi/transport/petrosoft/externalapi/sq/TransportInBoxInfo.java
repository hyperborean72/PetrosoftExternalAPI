package ru.csbi.transport.petrosoft.externalapi.sq;

import java.util.Date;

public class TransportInBoxInfo {

  String transportExternalId;
  private Date firstEntryDate;
  private Date lastEntryDate;


  public String getTransportExternalId() {
    return transportExternalId;
  }

  public void setTransportExternalId(String transportExternalId) {
    this.transportExternalId = transportExternalId;
  }

  public Date getFirstEntryDate() {
    return firstEntryDate;
  }

  public void setFirstEntryDate(Date firstEntryDate) {
    this.firstEntryDate = firstEntryDate;
  }

  public Date getLastEntryDate() {
    return lastEntryDate;
  }

  public void setLastEntryDate(Date lastEntryDate) {
    this.lastEntryDate = lastEntryDate;
  }


  public TransportInBoxInfo(String transportExternalId, Date firstEntryDate, Date lastEntryDate) {
    this.transportExternalId = transportExternalId;
    this.firstEntryDate = firstEntryDate;
    this.lastEntryDate = lastEntryDate;
  }
}