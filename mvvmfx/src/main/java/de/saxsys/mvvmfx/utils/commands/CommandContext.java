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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * An instance of this class can be shared among a group of commands to control the executability of the entire group.
 * If any command in the group is executing, all of the other commands will not be executable.
 */
public class CommandContext {

    private final BooleanProperty running = new SimpleBooleanProperty(this, "running");
    private int runningCount;

    ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    void setRunning(boolean value) {
        if (value) {
            ++runningCount;
            running.set(true);
        } else {
            --runningCount;
            if (runningCount == 0) {
                running.set(false);
            }
        }
    }

}
