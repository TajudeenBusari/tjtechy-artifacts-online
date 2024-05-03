package com.tjtechy.artifactsOnline.system.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Endpoint(id = "custom-beans")
@Component
public class CustomBeansEnpoint {

  private final ApplicationContext applicationContext;

  public CustomBeansEnpoint(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @ReadOperation
  public int beanCount(){
    return this.applicationContext.getBeanDefinitionCount();
  }

}



//to create custom actuator endpoint