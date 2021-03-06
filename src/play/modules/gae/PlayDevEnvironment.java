package play.modules.gae;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.*;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import play.Play;
import play.mvc.Http;
import play.mvc.Scope.Session;
import play.server.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
//import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class PlayDevEnvironment implements Environment, LocalServerEnvironment {

    public static PlayDevEnvironment create() {
        PlayDevEnvironment instance = new PlayDevEnvironment();
        ApiProxyLocalFactory factory = new ApiProxyLocalFactory();
        ApiProxyLocal proxy = factory.create(instance);
		ApiProxy.setDelegate(proxy);

		proxy.setProperty(
            LocalDatastoreService.BACKING_STORE_PROPERTY,
            Play.getFile("tmp/datastore").getAbsolutePath());

        /* Commented this because using test configs poses problems in DEV mode

        // Save datastore file in tmp/
		LocalDatastoreServiceTestConfig datastoreConfig = new LocalDatastoreServiceTestConfig();
		datastoreConfig.setNoStorage(false);
		datastoreConfig.setBackingStoreLocation(Play.applicationPath + "/tmp/datastore");
		datastoreConfig.setUp();

		// Use local implementation for deferred queues
		LocalTaskQueueTestConfig taskQueueConfig = new LocalTaskQueueTestConfig();
		taskQueueConfig.setDisableAutoTaskExecution(false);
		taskQueueConfig.setShouldCopyApiProxyEnvironment(true);
		taskQueueConfig.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class);
		taskQueueConfig.setUp();
        */


        return instance;
    }

    @Override
    public String getAppId() {
        return Play.applicationPath.getName();
    }

    @Override
    public String getModuleId() {
        return "TODO"; // FIXME
    }

    @Override
    public String getVersionId() {
        return "1.0";
    }

    @Override
    public String getEmail() {
        if(Session.current() != null) {
            return Session.current().get("__GAE_EMAIL");
        } else {
            return "no-session@gmail.com";
        }
    }

    @Override
    public boolean isLoggedIn() {
        return Session.current() != null && Session.current().contains("__GAE_EMAIL");
    }

    @Override
    public boolean isAdmin() {
        return Session.current() != null && Session.current().contains("__GAE_ISADMIN") && Session.current().get("__GAE_ISADMIN").equals("true");
    }

    @Override
    public String getAuthDomain() {
        return "gmail.com";
    }

    @Override
    public String getRequestNamespace() {
        return "";
    }

    public String getDefaultNamespace() {
        return "";
    }

    public void setDefaultNamespace(String ns) {
    }

    @Override
    public Map<String, Object> getAttributes() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
      hashMap.put(LocalEnvironment.REQUEST_END_LISTENERS, new ArrayList<RequestEndListener>());

      return hashMap;
    }

    @Override
    public void waitForServerToStart() throws InterruptedException {
    }

    @Override
    public int getPort() {
        return Server.httpPort;
    }

    @Override
    public File getAppDir() {
        return new File(Play.applicationPath, "war");
    }

    @Override
    public String getAddress() {
        return "localhost";
    }

	@Override
	public boolean enforceApiDeadlines() {
		return false;
	}
    
	@Override
	public boolean simulateProductionLatencies() {
		return false;
	}
	
	@Override
	public String getHostName() {
		return getBaseUrl();
	}

	// code stolen from Play core as this function is protected in Router
	// Gets baseUrl from current request or application.baseUrl in application.conf
    protected static String getBaseUrl() {
        if (Http.Request.current() == null) {
            // No current request is present - must get baseUrl from config
            String appBaseUrl = Play.configuration.getProperty("application.baseUrl", "application.baseUrl");
            if (appBaseUrl.endsWith("/")) {
                // remove the trailing slash
                appBaseUrl = appBaseUrl.substring(0, appBaseUrl.length()-1);
            }
            return appBaseUrl;

        } else {
            return Http.Request.current().getBase();
        }
    }

    @Override
    public long getRemainingMillis() {
        return 60000;
    }
}

