package org.tiogasolutions.identity.engine.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.identity.engine.AbstractEngineJaxRsTest;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

@Test
public class RootResourceTest extends AbstractEngineJaxRsTest {

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeMethod
  public void beforeClass() throws Exception {
  }

  @AfterMethod
  public void afterClass() throws Exception {
  }

  private Invocation.Builder request(WebTarget webTarget) {
    return webTarget.request().header("Authorization", toHttpAuth("admin", "Testing123"));
  }
  
  public void someTest() {
  }
}