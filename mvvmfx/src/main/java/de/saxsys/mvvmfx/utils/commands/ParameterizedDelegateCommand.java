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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ParameterizedDelegateCommand<T> extends CommandBase implements ParameterizedCommand<T> {

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

    public ParameterizedDelegateCommand(ParameterizedRunnable<T> parameterizedRunnable) {
        super(
            new DefaultParameterizedAction<>(parameterizedRunnable),
            null,
            null);

        if (parameterizedRunnable == null) {
            throw new IllegalArgumentException("parameterizedRunnable");
        }
    }

    public ParameterizedDelegateCommand(Action<T> action) {
        super(
            action,
            null,
            null);

        if (action == null) {
            throw new IllegalArgumentException("action");
        }
    }

    public ParameterizedDelegateCommand(ParameterizedRunnable<T> parameterizedRunnable, CommandContext context) {
        super(new DefaultParameterizedAction<>(parameterizedRunnable), null, context);

        if (parameterizedRunnable == null) {
            throw new IllegalArgumentException("parameterizedRunnable");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }
    }

    public ParameterizedDelegateCommand(Action<T> action, CommandContext context) {
        super(action, null, context);

        if (action == null) {
            throw new IllegalArgumentException("action");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }
    }

    public ParameterizedDelegateCommand(
            ParameterizedRunnable<T> parameterizedRunnable, ObservableValue<Boolean> executableObservable) {
        super(new DefaultParameterizedAction<>(parameterizedRunnable), executableObservable, null);

        if (parameterizedRunnable == null) {
            throw new IllegalArgumentException("parameterizedRunnable");
        }

        if (executableObservable == null) {
            throw new IllegalArgumentException("executableObservable");
        }
    }

    public ParameterizedDelegateCommand(
            Action<T> action, ObservableValue<Boolean> executableObservable) {
        super(action, executableObservable, null);

        if (action == null) {
            throw new IllegalArgumentException("action");
        }

        if (executableObservable == null) {
            throw new IllegalArgumentException("executableObservable");
        }
    }

    public ParameterizedDelegateCommand(
            ParameterizedRunnable<T> parameterizedRunnable,
            ObservableValue<Boolean> executableObservable,
            CommandContext context) {
        super(new DefaultParameterizedAction<>(parameterizedRunnable), executableObservable, context);

        if (parameterizedRunnable == null) {
            throw new IllegalArgumentException("parameterizedRunnable");
        }

        if (executableObservable == null) {
            throw new IllegalArgumentException("executableObservable");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }
    }

    public ParameterizedDelegateCommand(
            Action<T> action,
            ObservableValue<Boolean> executableObservable,
            CommandContext context) {
        super(action, executableObservable, context);

        if (action == null) {
            throw new IllegalArgumentException("action");
        }

        if (executableObservable == null) {
            throw new IllegalArgumentException("executableObservable");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void execute() {
        CommandHelper.verifyAccess();

        if (!isExecutable()) {
            throw new RuntimeException("The command is not executable.");
        }

        getAction().run(parameter.get());
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void execute(T parameter) {
        CommandHelper.verifyAccess();

        if (!isExecutable()) {
            throw new RuntimeException("The command is not executable.");
        }

        getAction().run(parameter);
    }

    @Override
    public ObjectProperty<T> parameterProperty() {
        return parameter;
    }

}
