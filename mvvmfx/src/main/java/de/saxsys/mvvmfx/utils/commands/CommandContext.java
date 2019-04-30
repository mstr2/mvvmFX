/*
 * INTEL CONFIDENTIAL
 *
 *
 * Copyright (C) 2019 Intel Corporation
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
