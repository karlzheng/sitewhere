package com.sitewhere.microservice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.sitewhere.SiteWhere;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.configuration.ConfigurableMicroservice;
import com.sitewhere.microservice.spi.IGlobalMicroservice;
import com.sitewhere.microservice.spi.configuration.ConfigurationState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Microservice that serves a global function in a SiteWhere instance.
 * 
 * @author Derek
 */
public abstract class GlobalMicroservice extends ConfigurableMicroservice implements IGlobalMicroservice {

    /** Relative path to instance global configuration file */
    private static final String INSTANCE_GLOBAL_CONFIGURATION_PATH = "/instance-global.xml";

    /** Executor for loading/parsing configuration updates */
    private ExecutorService executor = Executors.newSingleThreadExecutor(new ConfigurationLoaderThreadFactory());

    @Autowired
    private ApplicationContext microserviceContext;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.Microservice#initialize(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Wait for microservice to be configured.
	waitForConfigurationReady();

	// Call logic for initializing microservice subclass.
	microserviceInitialize(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Call logic for starting microservice subclass.
	microserviceStart(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Call logic for stopping microservice subclass.
	microserviceStop(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * onConfigurationCacheInitialized()
     */
    @Override
    public void onConfigurationCacheInitialized() {
	super.onConfigurationCacheInitialized();

	// Load and parse configuration in separate thread.
	executor.execute(new ConfigurationLoader());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#
     * getInstanceGlobalConfigurationPath()
     */
    @Override
    public String getInstanceGlobalConfigurationPath() {
	return getInstanceConfigurationPath() + INSTANCE_GLOBAL_CONFIGURATION_PATH;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#
     * getInstanceGlobalConfigurationData()
     */
    @Override
    public byte[] getInstanceGlobalConfigurationData() throws SiteWhereException {
	return getConfigurationMonitor().getConfigurationDataFor(getInstanceGlobalConfigurationPath());
    }

    public ApplicationContext getMicroserviceContext() {
	return microserviceContext;
    }

    public void setMicroserviceContext(ApplicationContext microserviceContext) {
	this.microserviceContext = microserviceContext;
    }

    /**
     * Allow configurations to be loaded and parsed in a separate thread.
     * 
     * @author Derek
     */
    public class ConfigurationLoader implements Runnable {

	@Override
	public void run() {
	    try {
		setConfigurationState(ConfigurationState.Loading);
		byte[] global = getInstanceGlobalConfigurationData();
		if (global == null) {
		    throw new SiteWhereException("Global instance configuration file not found.");
		}
		GenericApplicationContext globalContext = ConfigurationUtils.buildGlobalContext(global,
			SiteWhere.getVersion(), getMicroserviceContext());

		Map<String, ApplicationContext> contexts = new HashMap<String, ApplicationContext>();
		for (String path : getConfigurationPaths()) {
		    String fullPath = getInstanceConfigurationPath() + "/" + path;
		    getLogger().info("Loading configuration at path: " + fullPath);
		    byte[] data = getConfigurationMonitor().getConfigurationDataFor(fullPath);
		    if (data != null) {
			ApplicationContext subcontext = ConfigurationUtils.buildSubcontext(data, SiteWhere.getVersion(),
				globalContext);
			contexts.put(path, subcontext);
		    } else {
			throw new SiteWhereException("Required microservice configuration not found: " + fullPath);
		    }
		}

		// Store contexts for later use.
		setInstanceGlobalContext(globalContext);
		setGlobalContexts(contexts);

		// Allow components depending on configuration to proceed.
		initializeFromSpringContexts(globalContext, contexts);
		setConfigurationState(ConfigurationState.Succeeded);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to load configuration data.", e);
		setConfigurationState(ConfigurationState.Failed);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception while loading configuration data.", e);
		setConfigurationState(ConfigurationState.Failed);
	    }
	}
    }

    /** Used for naming tenant operation threads */
    private class ConfigurationLoaderThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Configuration Loader " + counter.incrementAndGet());
	}
    }
}