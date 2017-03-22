package ru.csbi.transport.petrosoft.externalapi.sq;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import ru.csbi.transport.services2.common.EquipmentUin;
import ru.csbi.transport.services2.common.GeoBoundingBox;
import ru.csbi.transport.services2.datactl.sub.DataProvider;
import ru.csbi.transport.services2.datactl.sub.PointsCollection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

public class DBServiceImpl implements DBService{

  private static final Logger log = Logger.getLogger(DBServiceImpl.class);

  // note: HH for 0~24 hrs
  private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  private DataProvider dataControl;

  public void setDataControl(DataProvider dataControl) {
    this.dataControl = dataControl;
  }

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * получить информацию о ТС, пересекавших заданную локацию в интересующем интервале времени,
   * а также времени первого и последнего пересечения границ заданной области
   *
   * @param timeBegin
   * @param timeEnd
   * @param bbox
   * @return список экземпляров TransportInBoxInfo
   */

  @Transactional(readOnly = true)
  public List<TransportInBoxInfo> getTransportInBoxInfo(Date timeBegin, Date timeEnd, GeoBoundingBox bbox)
  {
    Date a, b;
    long timeSpent;
    List<TransportInBoxInfo> result = new ArrayList<TransportInBoxInfo>();
    PointsCollection pc;
    TransportInBoxInfo transportInBoxInfo;

    List<Set<Integer>> splitSetsOfDeviceUins;

    log.debug("Начало наблюдения: " + dateTimeFormatter.format(timeBegin) +
            "; окончание наблюдения: " + dateTimeFormatter.format(timeEnd) +
            "; широта-долгота: {" + bbox.getLat1() + ", " + bbox.getLon1() + "; " + bbox.getLat2() + ", " + bbox.getLon2() + "}");

    a = new Date();

    Set<EquipmentUin> equipmentsInBox = dataControl.getEquipmentsInBox(null, timeBegin, timeEnd, bbox);

    b = new Date();
    timeSpent = b.getTime() - a.getTime();
    log.debug("getEquipmentsInBox(): " + timeSpent + " мсек.");


    /* Связка EquipmentUin - Transport.externalId
    *  для этапа 'поиск геоточек - формирование TransportInBoxInfo'*/

    Map<EquipmentUin, String> uinToTransportExternalId = new HashMap<>();

    /* Карта для хранения EquipmentUins
    *  в формате deviceUin - deviceType */

    Map<Integer, Integer> equipmentUins = new HashMap();
    for (EquipmentUin equipmentUin : equipmentsInBox)
      equipmentUins.put(equipmentUin.getDeviceUin(), equipmentUin.getDeviceType());

    a = new Date();

    splitSetsOfDeviceUins = split(equipmentUins.keySet(),1000);

    for (Set<Integer> singleSet : splitSetsOfDeviceUins)
    {
      Query query = entityManager.createNativeQuery("select e.transport, e.uin, t.external_id " +
              "from nsi_equipment e, nsi_transport t " +
              "where e.transport = t.id and e.uin in (:deviceUins)")
              .setParameter("deviceUins", singleSet);

      List<Object[]> nativeResult = query.getResultList();
      for (Object[] ob : nativeResult)
      {
        String transportExternalId = (String)ob[2];

        if (transportExternalId == null)
          transportExternalId = String.valueOf(((Number)ob[0]).intValue());

        Integer uin = ((Number)ob[1]).intValue();

        uinToTransportExternalId.put(new EquipmentUin(equipmentUins.get(uin), uin), transportExternalId);
      }
    }

    b = new Date();
    timeSpent = b.getTime() - a.getTime();
    log.debug("get Equipment(s) by uin: " + timeSpent + " мсек.");


    timeSpent = 0;

    for (Map.Entry<EquipmentUin, String> entry : uinToTransportExternalId.entrySet())
    {
      a = new Date();

      /* ВРЕМЯ ПЕРВОГО / ПОСЛЕДНЕГО ПЕРЕСЕЧЕНИЯ ГРАНИЦ ЗАДАННОЙ ОБЛАСТИ
            pc.getFirstTime() / pc.getLastTime() */
      pc = dataControl.getGeoPoints(null, entry.getKey(), timeBegin, timeEnd, bbox, 0, -1);

      b = new Date();
      timeSpent = timeSpent + b.getTime() - a.getTime();

      transportInBoxInfo = new TransportInBoxInfo(entry.getValue(), pc.getFirstTime(), pc.getLastTime());

      result.add(transportInBoxInfo);
    }
    log.debug("getGeoPoints(): " + timeSpent + " мсек.");

    return result;
  }


  /**
   * получить информацию о ТС, пересекавших заданную локацию в интересующем интервале времени
   *
   * @param timeBegin
   * @param timeEnd
   * @param bbox
   * @return список экземпляров TransportInBox
   */

  @Transactional(readOnly = true)
  public List<TransportInBox> getTransportInBox(Date timeBegin, Date timeEnd, GeoBoundingBox bbox)
  {
    Date a, b;
    long timeSpent;
    List<TransportInBox> result = new ArrayList<TransportInBox>();
    TransportInBox transportInBox;

    List<Set<Integer>> splitSetsOfDeviceUins;

    log.debug("Начало наблюдения: " + dateTimeFormatter.format(timeBegin) +
            "; окончание наблюдения: " + dateTimeFormatter.format(timeEnd) +
            "; широта-долгота: {" + bbox.getLat1() + ", " + bbox.getLon1() + "; " + bbox.getLat2() + ", " + bbox.getLon2() + "}");
    a = new Date();

    Set<EquipmentUin> equipmentsInBox = dataControl.getEquipmentsInBox(null, timeBegin, timeEnd, bbox);

    b = new Date();
    timeSpent = b.getTime() - a.getTime();
    log.debug("getEquipmentsInBox(): " + timeSpent + " мсек.");


    /* Карта для хранения EquipmentUins
    *  в формате deviceUin - deviceType */

    Map<Integer, Integer> equipmentUins = new HashMap();
    for (EquipmentUin equipmentUin : equipmentsInBox)
      equipmentUins.put(equipmentUin.getDeviceUin(), equipmentUin.getDeviceType());

    a = new Date();

    splitSetsOfDeviceUins = split(equipmentUins.keySet(),1000);

    for (Set<Integer> singleSet : splitSetsOfDeviceUins)
    {
      Query query = entityManager.createNativeQuery("select e.transport, e.uin, t.external_id " +
              "from nsi_equipment e, nsi_transport t " +
              "where e.transport = t.id and e.uin in (:deviceUins)")
              .setParameter("deviceUins", singleSet);

      List<Object[]> nativeResult = query.getResultList();
      for (Object[] ob : nativeResult)
      {
        String transportExternalId = (String)ob[2];

        if (transportExternalId == null)
          transportExternalId = String.valueOf(((Number)ob[0]).intValue());


        transportInBox = new TransportInBox(transportExternalId);

        result.add(transportInBox);
      }
    }

    b = new Date();
    timeSpent = b.getTime() - a.getTime();
    log.debug("get Equipment(s) by uin: " + timeSpent + " мсек.");

    return result;
  }


  /**
   * разбить сет на несколько в соответствии с указанным размером
   *
   * @param original
   * @param size
   * @return список параметризованных сетов
   */

  public static <T> List<Set<T>> split(Set<T> original, int size) {
    // List of sets to return.
    ArrayList<Set<T>> l = new ArrayList<Set<T>>(size);

    // Create an iterator for the original set.
    Iterator<T> it = original.iterator();

    // number of sets
    int count = (int)Math.ceil((double)(original.size())/size);
    log.debug("обнаружено ТС - "+original.size()+"; количество сетов после разбиения - "+count);

    for (int i=0; i<count; i++) {
      // Create the set.
      HashSet<T> s = new HashSet<T>(size);

      // Add to the list.
      l.add(s);

      for (int j=0; j<size && it.hasNext(); j++) {
        // Add the element to the set.
        s.add(it.next());
      }
    }
    return l;
  }
}