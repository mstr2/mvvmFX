/*
 * INTEL CONFIDENTIAL
 *
 *
 * Copyright (C) 2019 Intel Corporation
 * Copyright (C) 2017 Intel Deutschland GmbH
 * Copyright (C) 2016 MAVinci GmbH | A Part of Intel
 *
 *
 * This software and the related documents are Intel copyrighted materials, and
 * your use of them is governed by the express license under which they were
 * provided to you (License). Unless the License provides otherwise, you may not
 * use, modify, copy, publish, distribute, disclose or transmit this software or
 * the related documents without Intel's prior written permission.
 *
 *
 * This software and the related documents are provided as is, with no express
 * or implied warranties, other than those that are expressly stated in the
 * License.
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
