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

import javafx.util.BuilderFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public interface FXMLLoaderImpl {

    void setParentLoader(FXMLLoaderImpl parentLoader);

    FXMLLoaderImpl getParentLoader();

    Object getCurrentElement();

    int getLineNumber();

    ClassLoader getClassLoader();

    void setClassLoader(ClassLoader classLoader);

    boolean isStaticLoad();

    void setStaticLoad(boolean staticLoad);

    <T> T getController();

    void setController(Object controller);

    <T> T getRoot();

    void setRoot(Object root);

    ResourceBundle getResources();

    void setResources(ResourceBundle resources);

    URL getLocation();

    void setLocation(URL location);

    BuilderFactory getBuilderFactory();

    void setBuilderFactory(BuilderFactory builderFactory);

    Callback<Class<?>, Object> getControllerFactory();

    void setControllerFactory(Callback<Class<?>, Object> factory);

    <T> T load() throws IOException;

    <T> T load(InputStream stream) throws IOException;

    <T> T load(Class<?> callerClass) throws IOException;

}
