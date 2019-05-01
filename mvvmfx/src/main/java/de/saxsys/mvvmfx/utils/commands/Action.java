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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public abstract class Action<T> {

    private DoubleProperty progress;

    final void run(T parameter) {
        if (progress != null) {
            progress.set(0);
        }

        action(parameter);
    }

    protected abstract void action(T parameter);

    public final double getProgress() {
        return progressProperty().get();
    }

    public final ReadOnlyDoubleProperty progressProperty() {
        if (progress == null) {
            progress = new SimpleDoubleProperty(this, "progress");
        }

        return progress;
    }

    public void updateProgress(double progress, double max) {
        DoubleProperty property = (DoubleProperty)progressProperty();
        if (progress < 0) {
            property.set(0);
        } else if (progress > max) {
            property.set(1);
        } else {
            property.set(progress / max);
        }
    }

    public void updateProgress(long progress, long max) {
        DoubleProperty property = (DoubleProperty)progressProperty();
        if (progress < 0) {
            property.set(0);
        } else if (progress > max) {
            property.set(1);
        } else {
            property.set((double)progress / (double)max);
        }
    }

}
