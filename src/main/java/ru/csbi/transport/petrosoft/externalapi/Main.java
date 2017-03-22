package ru.csbi.transport.petrosoft.externalapi;

import java.util.concurrent.CountDownLatch;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import ru.csbi.transport.petrosoft.externalapi.sq.TransportByBnsService;


public class Main
{
  private static final Logger log = Logger.getLogger(Main.class);

  static final String wsport = new String ("9999");
  static final String hostname = new String("psv3.prod.msk-codd.local");
  //static final String hostname = new String("localhost");

  public static void main(String[] args)
  {
    try
    {
      log.info("Loading application context...");

      ConfigurableApplicationContext context = new FileSystemXmlApplicationContext("config/application-config.xml");
      context.registerShutdownHook();
      CountDownLatch shutdownLatch = (CountDownLatch) context.getBean("shutdownLatch");

      TransportByBnsService service = context.getBean("transportByBnsService", TransportByBnsService.class);
      /*String wsport = service.getWsport();
      String hostname = service.getHostname();*/
      String wsurl = "http://"+hostname+":"+wsport+"/ws/petrosoft";
      Endpoint.publish(wsurl, service);

      log.info("Petrosoft External API server started.");
      shutdownLatch.await();
      log.info("Petrosoft External API server shutdown...");
      context.close();
    }
    catch( Exception e )
    {
      log.error("Error: " + e, e);
    }
  }
}
