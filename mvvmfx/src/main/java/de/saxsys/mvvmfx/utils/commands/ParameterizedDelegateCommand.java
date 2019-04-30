/*
 * INTEL CONFIDENTIAL
 *
 *
 * Copyright (C) 2018 Intel Corporation
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
