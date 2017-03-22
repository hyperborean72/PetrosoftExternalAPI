package ru.csbi.transport.petrosoft.externalapi.sq;

import ru.csbi.transport.services2.common.GeoBoundingBox;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;
import java.util.List;

@WebService
public interface TransportByBnsService {


  /**
   * Набор externalId для ТС, которые находились в заданное время в заданной области,
   * а также время первого и последнего пересечения границ заданной области

   * @param timeBegin время начала интервала (включено).
   * @param timeEnd время окончания интервала (включено).
   * @param bbox ограниченная область (границы входят).
   * @return список БО.
   */
  @WebMethod
  List<TransportInBoxInfo> getTransportInBoxInfo(
          @WebParam(name = "timeBegin")@XmlElement(required=true, nillable=false)Date timeBegin,
          @WebParam(name = "timeEnd")@XmlElement(required=true, nillable=false)Date timeEnd,
          @WebParam(name = "geoBoundingBox")@XmlElement(required=true, nillable=false)GeoBoundingBox bbox);



  /**
   * Набор externalId для ТС, которые находились в заданное время в заданной области

   * @param timeBegin время начала интервала (включено).
   * @param timeEnd время окончания интервала (включено).
   * @param bbox ограниченная область (границы входят).
   * @return список БО.
   */
  @WebMethod
  List<TransportInBox> getTransportInBox(
          @WebParam(name = "timeBegin")@XmlElement(required=true, nillable=false)Date timeBegin,
          @WebParam(name = "timeEnd")@XmlElement(required=true, nillable=false)Date timeEnd,
          @WebParam(name = "geoBoundingBox")@XmlElement(required=true, nillable=false)GeoBoundingBox bbox);


}
