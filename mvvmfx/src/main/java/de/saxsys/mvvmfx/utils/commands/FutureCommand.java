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
import com.google.common.util.concurrent.SettableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command implementation that wraps a future. The status of the command (i.e. {@link
 * AsyncCommand#runningProperty()} and {@link AsyncCommand#notRunningProperty()} will reflect the status of the
 * wrapped future.
 */
@SuppressWarnings("unused")
public class FutureCommand extends CommandBase implements AsyncCommand {

    private static Logger LOGGER = LoggerFactory.getLogger(FutureCommand.class);

    private final Supplier<ListenableFuture<?>> futureSupplier;
    private final Consumer<Throwable> errorHandler;

    /**
     * Initializes a new instance of the FutureCommand class.
     *
     * @param futureSupplier Supplies the future that is represented by this command while executing.
     */
    public FutureCommand(Supplier<ListenableFuture<?>> futureSupplier) {
        super(null, null, null);

        if (futureSupplier == null) {
            throw new IllegalArgumentException("futureSupplier");
        }

        this.futureSupplier = futureSupplier;
        this.errorHandler = null;
    }

    /**
     * Initializes a new instance of the FutureCommand class.
     *
     * @param futureSupplier Supplies the future that is represented by this command while executing.
     * @param context Shared context that controls the executability of a group of commands.
     */
    public FutureCommand(Supplier<ListenableFuture<?>> futureSupplier, CommandContext context) {
        super(null, null, context);

        if (futureSupplier == null) {
            throw new IllegalArgumentException("futureSupplier");
        }

        this.futureSupplier = futureSupplier;
        this.errorHandler = null;
    }

    /**
     * Initializes a new instance of the FutureCommand class.
     *
     * @param futureSupplier Supplies the future that is represented by this command while executing.
     * @param executableObservable Observable value that is reflected by {@link AsyncCommand#executableProperty()}.
     */
    public FutureCommand(Supplier<ListenableFuture<?>> futureSupplier, ObservableValue<Boolean> executableObservable) {
        super(null, executableObservable, null);

        if (futureSupplier == null) {
            throw new IllegalArgumentException("futureSupplier");
        }

        this.futureSupplier = futureSupplier;
        this.errorHandler = null;
    }

    /**
     * Initializes a new instance of the FutureCommand class.
     *
     * @param futureSupplier Supplies the future that is represented by this command while executing.
     * @param executableObservable Observable value that is reflected by {@link AsyncCommand#executableProperty()}.
     * @param context Shared context that controls the executability of a group of commands.
     */
    public FutureCommand(
            Supplier<ListenableFuture<?>> futureSupplier,
            ObservableValue<Boolean> executableObservable,
            CommandContext context) {
        super(null, executableObservable, context);

        if (futureSupplier == null) {
            throw new IllegalArgumentException("futureSupplier");
        }

        this.futureSupplier = futureSupplier;
        this.errorHandler = null;
    }

    /**
     * Initializes a new instance of the FutureCommand class.
     *
     * @param futureSupplier Supplies the future that is represented by this command while executing.
     * @param errorHandler Handles failures of the future. The handler will be called on the JavaFX application thread.
     */
    public FutureCommand(Supplier<ListenableFuture<?>> futureSupplier, Consumer<Throwable> errorHandler) {
        super(null, null, null);

        if (futureSupplier == null) {
            throw new IllegalArgumentException("futureSupplier");
        }

        if (errorHandler == null) {
            throw new IllegalArgumentException("errorHandler");
        }

        this.futureSupplier = futureSupplier;
        this.errorHandler = errorHandler;
    }

    /**
     * Initializes a new instance of the FutureCommand class.
     *
     * @param futureSupplier Supplies the future that is represented by this command while executing.
     * @param errorHandler Handles failures of the future. The handler will be called on the JavaFX application thread.
     * @param executableObservable Observable value that is reflected by {@link AsyncCommand#executableProperty()}.
     */
    public FutureCommand(
            Supplier<ListenableFuture<?>> futureSupplier,
            Consumer<Throwable> errorHandler,
            ObservableValue<Boolean> executableObservable) {
        super(null, executableObservable, null);

        if (futureSupplier == null) {
            throw new IllegalArgumentException("futureSupplier");
        }

        if (errorHandler == null) {
            throw new IllegalArgumentException("errorHandler");
        }

        this.futureSupplier = futureSupplier;
        this.errorHandler = errorHandler;
    }

    /**
     * Initializes a new instance of the FutureCommand class.
     *
     * @param futureSupplier Supplies the future that is represented by this command while executing.
     * @param errorHandler Handles failures of the future. The handler will be called on the JavaFX application thread.
     * @param executableObservable Observable value that is reflected by {@link AsyncCommand#executableProperty()}.
     * @param context Shared context that controls the executability of a group of commands.
     */
    public FutureCommand(
            Supplier<ListenableFuture<?>> futureSupplier,
            Consumer<Throwable> errorHandler,
            ObservableValue<Boolean> executableObservable,
            CommandContext context) {
        super(null, executableObservable, context);

        if (futureSupplier == null) {
            throw new IllegalArgumentException("futureSupplier");
        }

        if (errorHandler == null) {
            throw new IllegalArgumentException("errorHandler");
        }

        this.futureSupplier = futureSupplier;
        this.errorHandler = errorHandler;
    }

    /**
     * Executes the command asynchronously and returns immediately. The returned future completes when the wrapped
     * future completes (either by successful completion, throwing an exception or being cancelled).
     */
    @Override
    public ListenableFuture<Void> executeAsync() {
        CommandHelper.verifyAccess();

        if (!isExecutable()) {
            throw new RuntimeException("The command is not executable.");
        }

        setRunning(true);

        SettableFuture<Void> result = SettableFuture.create();

        Futures.addCallback(
            futureSupplier.get(),
            new FutureCallback<Object>() {
                @Override
                public void onSuccess(Object arg) {
                    setRunning(false);
                    result.set(null);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    try {
                        if (errorHandler != null) {
                            errorHandler.accept(throwable);
                        } else {
                            LOGGER.error("Error in async command execution.", throwable);
                        }
                    } finally {
                        setRunning(false);
                        result.set(null);
                    }
                }
            },
            Platform::runLater);

        return result;
    }

    @Override
    public void execute() {
        executeAsync();
    }

}
