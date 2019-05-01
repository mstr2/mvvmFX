/*
 * Copyright 2019 Intel Corporation
 * Copyright 2015 Alexander Casall, Manuel Mauky
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
