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

import com.google.common.util.concurrent.ListenableFuture;

public interface AsyncCommand extends Command {

    /** Executes the command asynchronously and returns immediately. */
    ListenableFuture<Void> executeAsync();

}
