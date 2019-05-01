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

package de.saxsys.mvvmfx.internal.viewloader.fxml;

import de.saxsys.mvvmfx.internal.viewloader.fxml.compat.FXMLLoader11;
import javafx.util.BuilderFactory;
import javafx.util.Callback;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class FXMLLoaderFactory {

    public static FXMLLoaderImpl create(URL location) throws IOException {
        InputStream inputStream = location.openStream();
        if (inputStream.markSupported()) {
            inputStream.mark(Integer.MAX_VALUE);
        } else {
            inputStream = new BufferedInputStream(inputStream);
        }

        FXMLLoaderImpl fxmlLoader;
        if (useNewFxmlLoader(inputStream)) {
            fxmlLoader = new FXMLLoader(
                inputStream, location, null, null, null,
                    Charset.forName(FXMLLoader.DEFAULT_CHARSET_NAME), new LinkedList<FXMLLoaderImpl>());
        } else {
            fxmlLoader = new FXMLLoader11(
                inputStream, location, null, null, null,
                    Charset.forName(FXMLLoader11.DEFAULT_CHARSET_NAME), new LinkedList<FXMLLoaderImpl>());
        }

        inputStream.reset();
        return fxmlLoader;
    }

    public static FXMLLoaderImpl create(URL location, ResourceBundle resources, BuilderFactory builderFactory,
            Callback<Class<?>, Object> controllerFactory, Charset charset,
            LinkedList<FXMLLoaderImpl> loaders) throws IOException {
        InputStream inputStream = location.openStream();
        if (inputStream.markSupported()) {
            inputStream.mark(Integer.MAX_VALUE);
        } else {
            inputStream = new BufferedInputStream(inputStream);
        }

        FXMLLoaderImpl fxmlLoader;
        if (useNewFxmlLoader(inputStream)) {
            fxmlLoader = new FXMLLoader(
                inputStream, location, resources, builderFactory, controllerFactory, charset, loaders);
        } else {
            fxmlLoader = new FXMLLoader11(
                inputStream, location, resources, builderFactory, controllerFactory, charset, loaders);
        }

        inputStream.reset();
        return fxmlLoader;
    }

    private static boolean useNewFxmlLoader(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            int pos = line.indexOf("xmlns:fx=");
            if (pos >= 0) {
                return line.substring(pos).toLowerCase().startsWith("xmlns:fx=\"http://javafx.com/fxml/2019.1\"");
            }
        }

        return false;
    }

}
