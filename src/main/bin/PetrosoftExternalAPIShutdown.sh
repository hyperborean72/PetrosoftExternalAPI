#!/bin/sh

java -Dlog4j.configuration=file:context/log4j.xml -Duser.timezone=Europe/Moscow -cp PetrosoftExternalAPI.jar ru.csbi.transport.petrosoft.externalapi.RemoteControlImpl localhost:19050 SHUTDOWN