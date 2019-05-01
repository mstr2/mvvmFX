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

package de.saxsys.mvvmfx.utils.commands;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import de.saxsys.mvvmfx.internal.BackgroundExecutorManager;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command implementation that delegates execution to a user-provided {@link Runnable}. The command will be executed
 * on a background thread.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DelegateAsyncCommand extends DelegateCommand implements AsyncCommand {

    private static Logger LOGGER = LoggerFactory.getLogger(DelegateAsyncCommand.class);

    public DelegateAsyncCommand(Runnable runnable) {
        super(runnable);
    }

    public DelegateAsyncCommand(Action action) {
        super(action);
    }

    public DelegateAsyncCommand(Runnable runnable, CommandContext context) {
        super(runnable, context);
    }

    public DelegateAsyncCommand(Action action, CommandContext context) {
        super(action, context);
    }

    public DelegateAsyncCommand(Runnable runnable, ObservableValue<Boolean> executableObservable) {
        super(runnable, executableObservable);
    }

    public DelegateAsyncCommand(Action action, ObservableValue<Boolean> executableObservable) {
        super(action, executableObservable);
    }

    public DelegateAsyncCommand(
            Runnable runnable, ObservableValue<Boolean> executableObservable, CommandContext context) {
        super(runnable, executableObservable, context);
    }

    public DelegateAsyncCommand(
            Action action, ObservableValue<Boolean> executableObservable, CommandContext context) {
        super(action, executableObservable, context);
    }

    @Override
    public void execute() {
        executeAsync();
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public ListenableFuture<Void> executeAsync() {
        CommandHelper.verifyAccess();

        if (!isExecutable()) {
            throw new RuntimeException("The command is not executable.");
        }

        setRunning(true);

        ListenableFuture<Void> future =
            (ListenableFuture<Void>)BackgroundExecutorManager.getInstance().getExecutorService()
                .submit(() -> getAction().run(null));

        Futures.addCallback(
            future,
            new FutureCallback<Void>() {
                @Override
                public void onSuccess(Void arg) {
                    setRunning(false);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    LOGGER.error("Error in async command execution.", throwable);
                    setRunning(false);
                }
            },
            Platform::runLater);

        return future;
    }

}
