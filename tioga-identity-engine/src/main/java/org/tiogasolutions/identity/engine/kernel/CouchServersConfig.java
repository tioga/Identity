package org.tiogasolutions.identity.engine.kernel;

import com.fasterxml.jackson.databind.Module;
import org.tiogasolutions.couchace.core.api.CouchSetup;
import org.tiogasolutions.couchace.jackson.JacksonCouchJsonStrategy;
import org.tiogasolutions.couchace.jersey.JerseyCouchHttpClient;

import java.util.Collection;

public class CouchServersConfig {

    private final String url;
    private final String databaseName;

    private final String username;
    private final String password;

    private final Collection<? extends Module> modules;

    public CouchServersConfig(String url, String databaseName, String username, String password, Collection<? extends Module> modules) {
        this.url = url;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.modules = modules;
    }

    public Collection<? extends Module> getModules() {
        return modules;
    }

    public String getUrl() {
        return url;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public CouchSetup toCouchSetup() {
        CouchSetup setup = new CouchSetup(url);
        setup.setUserName(username);
        setup.setPassword(password);
        setup.setHttpClient(JerseyCouchHttpClient.class);

        Module[] array = modules.toArray(new Module[modules.size()]);
        JacksonCouchJsonStrategy strategy = new JacksonCouchJsonStrategy(array);
        setup.setJsonStrategy(strategy);

        return setup;
    }
}
