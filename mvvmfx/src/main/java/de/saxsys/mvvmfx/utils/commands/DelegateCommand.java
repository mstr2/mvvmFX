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

import javafx.beans.value.ObservableValue;

/**
 * A command implementation that delegates execution to a user-provided {@link Runnable}. The command will be executed
 * on the JavaFX application thread.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DelegateCommand extends CommandBase implements Command {

    public DelegateCommand(Runnable runnable) {
        super(
            new DefaultAction(runnable),
            null,
            null);

        if (runnable == null) {
            throw new IllegalArgumentException("runnable");
        }
    }

    public DelegateCommand(Action action) {
        super(
            action,
            null,
            null);

        if (action == null) {
            throw new IllegalArgumentException("action");
        }
    }

    public DelegateCommand(Runnable runnable, CommandContext context) {
        super(
            new DefaultAction(runnable),
            null,
            context);

        if (runnable == null) {
            throw new IllegalArgumentException("runnable");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }
    }

    public DelegateCommand(Action action, CommandContext context) {
        super(
            action,
            null,
            context);

        if (action == null) {
            throw new IllegalArgumentException("action");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }
    }

    public DelegateCommand(Runnable runnable, ObservableValue<Boolean> executableObservable) {
        super(
            new DefaultAction(runnable),
            executableObservable,
            null);

        if (runnable == null) {
            throw new IllegalArgumentException("runnable");
        }

        if (executableObservable == null) {
            throw new IllegalArgumentException("executableObservable");
        }
    }

    public DelegateCommand(Action action, ObservableValue<Boolean> executableObservable) {
        super(
            action,
            executableObservable,
            null);

        if (action == null) {
            throw new IllegalArgumentException("action");
        }

        if (executableObservable == null) {
            throw new IllegalArgumentException("executableObservable");
        }
    }

    public DelegateCommand(Runnable runnable, ObservableValue<Boolean> executableObservable, CommandContext context) {
        super(
            new DefaultAction(runnable),
            executableObservable,
            context);

        if (runnable == null) {
            throw new IllegalArgumentException("runnable");
        }

        if (executableObservable == null) {
            throw new IllegalArgumentException("executableObservable");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }
    }

    public DelegateCommand(Action action, ObservableValue<Boolean> executableObservable, CommandContext context) {
        super(
            action,
            executableObservable,
            context);

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

        getAction().run(null);
    }

}
