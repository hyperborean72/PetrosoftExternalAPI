package ru.csbi.transport.petrosoft.externalapi;

import java.util.Map;

/**
 * @author Sergey Astakhov
 * @version $Revision$
 */
public interface RemoteControl
{
  Map<Object, Object> status();

  void shutdown();
}
