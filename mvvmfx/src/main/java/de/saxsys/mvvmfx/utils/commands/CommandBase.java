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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("WeakerAccess")
abstract class CommandBase {

    private final BooleanProperty running =
        new SimpleBooleanProperty(this, "running") {
            @Override
            protected void invalidated() {
                boolean executing = get();

                if (notRunning != null) {
                    notRunning.set(!executing);
                }

                if (context != null) {
                    context.setRunning(executing);
                }

                super.invalidated();
            }
        };

    private final BooleanProperty executable =
        new SimpleBooleanProperty(this, "executable", true) {
            @Override
            protected void invalidated() {
                if (notExecutable != null) {
                    notExecutable.set(!get());
                }

                super.invalidated();
            }
        };

    private final CommandContext context;
    private final Action action;

    private BooleanProperty notExecutable;
    private BooleanProperty notRunning;
    private DoubleProperty progress;

    protected CommandBase(
            @Nullable Action action,
            @Nullable ObservableValue<Boolean> executableObservable,
            @Nullable CommandContext context) {
        this.action = action;
        this.context = context;
        BooleanBinding executableBinding;

        if (executableObservable != null && context == null) {
            executableBinding = Bindings.createBooleanBinding(
                () -> executableObservable.getValue() && !running.get(),
                executableObservable, running);
        } else if (executableObservable == null && context != null) {
            executableBinding = context.runningProperty().not();
        } else if (executableObservable != null) {
            executableBinding =
                Bindings.createBooleanBinding(
                    () -> executableObservable.getValue() && !running.get() && !context.runningProperty().get(), running,
                    executableObservable,
                    context.runningProperty());
        } else {
            executableBinding = running.not();
        }

        executable.bind(executableBinding);
    }

    protected @Nullable Action getAction() {
        return action;
    }

    public boolean isExecutable() {
        return executable.get();
    }

    public boolean isNotExecutable() {
        return !executable.get();
    }

    public ReadOnlyBooleanProperty executableProperty() {
        return executable;
    }

    public ReadOnlyBooleanProperty notExecutableProperty() {
        if (notExecutable == null) {
            notExecutable = new SimpleBooleanProperty(!executable.get());
        }

        return notExecutable;
    }

    public boolean isRunning() {
        return running.get();
    }

    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    public boolean isNotRunning() {
        return !running.get();
    }

    public ReadOnlyBooleanProperty notRunningProperty() {
        if (notRunning == null) {
            notRunning = new SimpleBooleanProperty(!running.get());
        }

        return notRunning;
    }

    void setRunning(boolean running) {
        this.running.set(running);
    }

    public double getProgress() {
        return progressProperty().get();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        if (progress == null) {
            progress = new SimpleDoubleProperty(this, "progress");

            if (action != null) {
                progress.bind(action.progressProperty());
            }
        }

        return progress;
    }

}
