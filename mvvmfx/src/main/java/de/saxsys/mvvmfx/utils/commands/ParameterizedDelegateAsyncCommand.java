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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class ParameterizedDelegateAsyncCommand<T>
        extends ParameterizedDelegateCommand<T>
        implements ParameterizedAsyncCommand<T> {

    private static Logger LOGGER = LoggerFactory.getLogger(DelegateCommand.class);

    private final ObjectProperty<T> parameter =
        new SimpleObjectProperty<T>() {
            @Override
            protected void invalidated() {
                if (isRunning()) {
                    throw new RuntimeException("Parameter cannot be changed while the command is executing.");
                }

                super.invalidated();
            }
        };

    public ParameterizedDelegateAsyncCommand(ParameterizedRunnable<T> parameterizedRunnable) {
        super(parameterizedRunnable);
    }

    public ParameterizedDelegateAsyncCommand(Action<T> action) {
        super(action);
    }

    public ParameterizedDelegateAsyncCommand(ParameterizedRunnable<T> parameterizedRunnable, CommandContext context) {
        super(parameterizedRunnable, context);
    }

    public ParameterizedDelegateAsyncCommand(Action<T> action, CommandContext context) {
        super(action, context);
    }

    public ParameterizedDelegateAsyncCommand(
            ParameterizedRunnable<T> parameterizedRunnable, ObservableValue<Boolean> executableObservable) {
        super(parameterizedRunnable, executableObservable);
    }

    public ParameterizedDelegateAsyncCommand(
            Action<T> action, ObservableValue<Boolean> executableObservable) {
        super(action, executableObservable);
    }

    public ParameterizedDelegateAsyncCommand(
            ParameterizedRunnable<T> parameterizedRunnable,
            ObservableValue<Boolean> executableObservable,
            CommandContext context) {
        super(parameterizedRunnable, executableObservable, context);
    }

    public ParameterizedDelegateAsyncCommand(
            Action<T> action,
            ObservableValue<Boolean> executableObservable,
            CommandContext context) {
        super(action, executableObservable, context);
    }

    @Override
    public void execute() {
        executeAsync();
    }

    @Override
    public void execute(T parameter) {
        executeAsync(parameter);
    }

    @Override
    public ListenableFuture<Void> executeAsync() {
        return executeAsync(parameter.get());
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public ListenableFuture<Void> executeAsync(T parameter) {
        CommandHelper.verifyAccess();

        if (!isExecutable()) {
            throw new RuntimeException("The command is not executable.");
        }

        setRunning(true);

        ListenableFuture<Void> future =
            (ListenableFuture<Void>)BackgroundExecutorManager.getInstance().getExecutorService()
                .submit(() -> getAction().run(parameter));

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

    @Override
    public ObjectProperty<T> parameterProperty() {
        return parameter;
    }

}