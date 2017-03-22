package ru.csbi.transport.petrosoft.externalapi.sq;

import ru.csbi.transport.services2.common.GeoBoundingBox;

import java.util.Date;
import java.util.List;

public interface DBService {
  List<TransportInBoxInfo> getTransportInBoxInfo(Date timeBegin, Date timeEnd, GeoBoundingBox bbox);
  List<TransportInBox> getTransportInBox(Date timeBegin, Date timeEnd, GeoBoundingBox bbox);
}
