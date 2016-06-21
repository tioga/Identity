package org.tiogasolutions.identity.engine.grizzly;

import org.tiogasolutions.app.standard.system.JarClassLoader;

import java.net.URL;

public class IdentityMainWrapper {

  public static void main(String...args) throws Throwable {
    URL location = IdentityMain.class.getProtectionDomain().getCodeSource().getLocation();
    System.out.println("Starting application from " + location);

    if (location.getPath().endsWith(".jar")) {
      JarClassLoader jcl = new JarClassLoader();
      jcl.invokeStart(IdentityMain.class.getName(), args);

    } else {
      IdentityMain.main(args);
    }
  }
}
