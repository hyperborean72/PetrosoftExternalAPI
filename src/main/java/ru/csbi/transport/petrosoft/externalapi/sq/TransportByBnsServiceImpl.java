package ru.csbi.transport.petrosoft.externalapi.sq;

import ru.csbi.transport.services2.common.GeoBoundingBox;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebService(endpointInterface = "ru.csbi.transport.petrosoft.externalapi.sq.TransportByBnsService")
public class TransportByBnsServiceImpl implements TransportByBnsService
{
  private DBService service;

  public void setService(DBService _dbService)
  {
    service = _dbService;
  }


  @Override
  public List<TransportInBoxInfo> getTransportInBoxInfo(Date timeBegin, Date timeEnd, GeoBoundingBox bbox)
  {
    List<TransportInBoxInfo> result = new ArrayList<TransportInBoxInfo>();

    result = service.getTransportInBoxInfo(timeBegin, timeEnd, bbox);

    return result;
  }


  @Override
  public List<TransportInBox> getTransportInBox(Date timeBegin, Date timeEnd, GeoBoundingBox bbox)
  {
    List<TransportInBox> result = new ArrayList<TransportInBox>();

    result = service.getTransportInBox(timeBegin, timeEnd, bbox);

    return result;
  }
}