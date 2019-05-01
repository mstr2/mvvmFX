/*
 * Copyright 2019 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
