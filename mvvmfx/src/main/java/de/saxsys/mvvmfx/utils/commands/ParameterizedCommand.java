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

public interface ParameterizedCommand<T> extends Command {

    /**
     * Executes the command with the given parameter. This is equivalent to setting the {@link
     * ParameterizedCommand#parameterProperty()} value and calling the parameterless {@link Command#execute()} method.
     */
    void execute(T parameter);

    ObjectProperty<T> parameterProperty();

}
