package org.tiogasolutions.identity.engine;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.core.PubLinks;

import javax.xml.bind.DatatypeConverter;

/**
 * Temporarily moved to main source so that we can reuse it in other modules.
 */
@Component
@Profile("test")
public class TestUtils {

  public String toHttpAuth(String username, String password) {
    byte[] value = (username + ":" + password).getBytes();
    return "Basic " + DatatypeConverter.printBase64Binary(value);
  }

}
