package org.tiogasolutions.identity.engine.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.lib.spring.SpringUtils;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;

import java.nio.file.Path;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

public class IdentityMain {

    private static final String PREFIX = "identity";
    private static final Logger log = getLogger(IdentityMain.class);

    public static void main(String... args) throws Exception {
        // Priority #1, configure default logging levels. This will be
        // overridden later when/if the logback.xml is found and loaded.
        AppUtils.initLogback(Level.WARN);

        // Assume we want by default INFO on when & how the grizzly server
        // is started. Possibly overwritten by logback.xml if used.
        AppUtils.setLogLevel(Level.INFO, IdentityMain.class);
        AppUtils.setLogLevel(Level.INFO, GrizzlyServer.class);

        // Load the resolver which gives us common tools for identifying
        // the runtime & config directories, logback.xml, etc.
        AppPathResolver resolver = new AppPathResolver(PREFIX+".");
        Path runtimeDir = resolver.resolveRuntimePath();
        Path configDir = resolver.resolveConfigDir(runtimeDir);

        // Re-init logback if we can find the logback.xml
        Path logbackFile = AppUtils.initLogback(configDir, PREFIX+".log.config", "logback.xml");

        // Locate the spring file for this app.
        String springConfigPath = resolver.resolveSpringPath(configDir, format("classpath:/tioga-%s-engine/spring-config.xml", PREFIX));
        String[] activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

        boolean shuttingDown = asList(args).contains("-shutdown");
        String action = (shuttingDown ? "Shutting down" : "Starting");

        String msg = format("%s server:\n", action);
        msg += format("  *  Runtime Dir     (%s.runtime.dir)     %s\n", PREFIX, runtimeDir);
        msg += format("  *  Config Dir      (%s.config.dir)      %s\n", PREFIX, configDir);
        msg += format("  *  Logback File    (%s.log.config)      %s\n", PREFIX, logbackFile);
        msg += format("  *  Spring Path     (%s.spring.config)   %s\n", PREFIX, springConfigPath);
        msg += format("  *  Active Profiles (%s.active.profiles) %s\n", PREFIX, asList(activeProfiles));
        log.info(msg);

        AbstractXmlApplicationContext applicationContext = SpringUtils.createXmlConfigApplicationContext(springConfigPath, activeProfiles);

        GrizzlyServer grizzlyServer = applicationContext.getBean(GrizzlyServer.class);

        if (shuttingDown) {
            ShutdownUtils.shutdownRemote(grizzlyServer.getConfig());
            log.warn("Shut down server at {}:{}", grizzlyServer.getConfig().getHostName(), grizzlyServer.getConfig().getShutdownPort());
            System.exit(0);
            return;
        }

        // Lastly, start the server.
        grizzlyServer.start();
    }
}
