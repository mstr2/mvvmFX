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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class CompositeCommand implements Command {

    private final BooleanProperty running =
        new SimpleBooleanProperty(false) {
            @Override
            protected void invalidated() {
                if (notRunning != null) {
                    notRunning.set(!get());
                }

                super.invalidated();
            }
        };

    private final BooleanProperty executable =
        new SimpleBooleanProperty(true) {
            @Override
            protected void invalidated() {
                if (notExecutable != null) {
                    notExecutable.set(!get());
                }

                super.invalidated();
            }
        };

    private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress");

    private final List<Command> commands;

    private BooleanProperty notExecutable;
    private BooleanProperty notRunning;

    public CompositeCommand(Command... commands) {
        this.commands = new ArrayList<>(Arrays.asList(commands));
        reinitialize();
    }

    public void register(Command command) {
        if (!commands.contains(command)) {
            commands.add(command);
            reinitialize();
        }
    }

    public void unregister(Command command) {
        if (commands.contains(command)) {
            commands.remove(command);
            reinitialize();;
        }
    }

    /** Executes the commands synchronously in the order they were added to this CompositeCommand instance. */
    @Override
    public void execute() {
        if (!isExecutable()) {
            throw new RuntimeException("The command is not executable.");
        }

        for (Command command : commands) {
            command.execute();
        }
    }

    @Override
    public boolean isExecutable() {
        return executable.get();
    }

    @Override
    public ReadOnlyBooleanProperty executableProperty() {
        return executable;
    }

    @Override
    public boolean isNotExecutable() {
        return !executable.get();
    }

    @Override
    public ReadOnlyBooleanProperty notExecutableProperty() {
        if (notExecutable == null) {
            notExecutable = new SimpleBooleanProperty(!executable.get());
        }

        return notExecutable;
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    public boolean isNotRunning() {
        return notRunning.get();
    }

    @Override
    public ReadOnlyBooleanProperty notRunningProperty() {
        if (notRunning == null) {
            notRunning = new SimpleBooleanProperty(!running.get());
        }

        return notRunning;
    }

    @Override
    public double getProgress() {
        return progress.get();
    }

    @Override
    public DoubleProperty progressProperty() {
        return progress;
    }

    private void reinitialize() {
        Observable[] dependencies = this.commands.stream().map(Command::executableProperty).toArray(Observable[]::new);

        this.executable.unbind();
        this.executable.bind(
            Bindings.createBooleanBinding(
                () -> {
                    for (Command command : this.commands) {
                        if (!command.isExecutable()) {
                            return false;
                        }
                    }

                    return true;
                },
                dependencies));

        this.running.unbind();
        this.running.bind(
            Bindings.createBooleanBinding(
                () -> {
                    for (Command command : this.commands) {
                        if (!command.isRunning()) {
                            return false;
                        }
                    }

                    return true;
                },
                dependencies));

        this.progress.unbind();
        this.progress.bind(
            Bindings.createDoubleBinding(
                () -> {
                    double progress = 0;
                    for (Command command : this.commands) {
                        progress += command.getProgress();
                    }

                    return progress / this.commands.size();
                },
                dependencies));
    }

}
