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

public class ParameterizedCommandInvocation<T> implements Runnable {

    private final ParameterizedCommand<T> command;
    private final T payload;

    public ParameterizedCommandInvocation(ParameterizedCommand<T> command, T payload) {
        if (command == null) {
            throw new IllegalArgumentException("command");
        }

        if (payload == null) {
            throw new IllegalArgumentException("payload");
        }

        this.command = command;
        this.payload = payload;
    }

    @Override
    public void run() {
        command.execute(payload);
    }

}
