package com.tjtechy.artifactsOnline.system.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class UsableMemoryHealthIndicator implements HealthIndicator {
  @Override
  public Health health() {
    File path = new File(".");//. current directory //path used to compute available disk space
    long diskUsableInBytes = path.getUsableSpace();
    boolean isHealth = diskUsableInBytes >= 10 * 1024 * 1024; //10MB
    Status status = isHealth ? Status.UP : Status.DOWN; //UP means there is enough usable memory.
    return Health
            .status(status)
            .withDetail("usable memory", diskUsableInBytes) // in addition to status, we can attach additional key-value details using withDetail(key, value)
            .withDetail("threshold", 10 * 1024 * 1024)
            .build();
  }
}
//this class is used to configure the disk space
//we use builder pattern here