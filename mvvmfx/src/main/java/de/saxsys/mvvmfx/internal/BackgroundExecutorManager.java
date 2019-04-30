package de.saxsys.mvvmfx.internal;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import eu.lestard.doc.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class BackgroundExecutorManager {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundExecutorManager.class);

    private static BackgroundExecutorManager INSTANCE;

    public static BackgroundExecutorManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BackgroundExecutorManager();
        }

        return INSTANCE;
    }

    private ListeningExecutorService customBackgroundExecutorService;
    private ListeningExecutorService backgroundExecutorService;

    public ListeningExecutorService getExecutorService() {
        if (customBackgroundExecutorService == null) {
            if (backgroundExecutorService == null) {
                backgroundExecutorService = MoreExecutors.listeningDecorator(
                    java.util.concurrent.Executors.newCachedThreadPool(
                        runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setName("MvvmFX background execution thread");
                            thread.setDaemon(true);
                            thread.setUncaughtExceptionHandler(
                                (t, e) -> {
                                    if (e instanceof ThreadDeath) {
                                        return;
                                    }

                                    LOG.error("Error in async execution.", e);
                                });

                            return thread;
                        }));
            }

            return backgroundExecutorService;
        }

        return customBackgroundExecutorService;
    }

    public void setCustomExecutorService(ListeningExecutorService executorService) {
        if (executorService != null && backgroundExecutorService != null) {
            backgroundExecutorService.shutdown();
            backgroundExecutorService = null;
        }

        customBackgroundExecutorService = executorService;
    }

}
