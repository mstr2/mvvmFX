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

package de.saxsys.mvvmfx.internal.viewloader.fxml.expression;

import javafx.beans.value.ObservableValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StaticFunctionExpression extends Expression<Object> {

    private static final Set<String> knownPackages = new HashSet<>();
    private static final Map<String, Method> methodCache = new HashMap<>();

    private final Method method;
    private final List<ArgumentExpression> args;

    static {
        knownPackages.add("java.lang");
    }

    public static void addKnownPackage(String packageName) {
        knownPackages.add(packageName);
    }

    StaticFunctionExpression(KeyPath keyPath, List<ArgumentExpression> args) {
        String funcName = keyPath.toString();
        String funcKey = funcName + "`" + args.size();
        Method method = methodCache.get(funcKey);
        if (method == null) {
            method = getMethod(funcName, args);
            methodCache.put(funcKey, method);
        }

        this.method = method;
        this.args = args;
    }

    @Override
    public Object evaluate(Object namespace, boolean throwIfNotResolved) {
        int paramsCount = method.getParameterCount();
        Object[] params = new Object[paramsCount];
        if (method.isVarArgs()) {
            for (int i = 0; i < paramsCount - 1; ++i) {
                params[i] = getValue(args.get(i), namespace);
            }

            Object[] arrayParams = new Object[args.size() - paramsCount + 1];
            for (int i = paramsCount - 1; i < args.size(); ++i) {
                arrayParams[i - paramsCount + 1] = getValue(args.get(i), namespace);
            }

            params[params.length - 1] = arrayParams;
        } else {
            for (int i = 0; i < params.length; ++i) {
                params[i] = getValue(args.get(i), namespace);
            }
        }

        try {
            return method.invoke(null, params);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Object namespace, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDefined(Object namespace) {
        for (Expression arg : args) {
            if (!arg.isDefined(namespace)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isLValue() {
        return true;
    }

    @Override
    protected void getArguments(List<KeyPath> arguments) {
        for (Expression<?> arg : args) {
            arguments.addAll(arg.getArguments());
        }
    }

    private Object getValue(Expression expression, Object namespace) {
        Object res = expression.evaluate(namespace, false);
        if (res instanceof ObservableValue) {
            return ((ObservableValue)res).getValue();
        }

        return res;
    }

    private Method getMethod(String name, List<ArgumentExpression> args) {
        if (!name.contains(".")) {
            throw new RuntimeException("Invalid method reference: " + name);
        }

        String className = name.substring(0, name.lastIndexOf("."));
        String methodName = name.substring(className.length() + 1);
        Class<?> clazz = null;

        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            for (String defaultPackage : knownPackages) {
                try {
                    clazz = Class.forName(defaultPackage + "." + className);
                    break;
                } catch (ClassNotFoundException ignored) {
                }
            }
        }

        if (clazz == null) {
            throw new RuntimeException("Class not found: " + className);
        }

        Method selectedMethod = null;
        for (Method method : clazz.getMethods()) {
            boolean compatibleParamCount =
                method.getName().equals(methodName)
                    && (!method.isVarArgs() && method.getParameterCount() == args.size()
                    || method.isVarArgs() && method.getParameterCount() <= args.size());

            if (!compatibleParamCount) {
                continue;
            }

            boolean compatibleParamTypes = true;
            Class<?>[] params = method.getParameterTypes();
            for (int i = 0; i < (method.isVarArgs() ? params.length - 1 : params.length); ++i) {
                boolean isString =
                    args.get(i).getType() == ArgumentExpression.Type.LITERAL
                        || args.get(i).getType() == ArgumentExpression.Type.RESOURCE_KEY;
                if (isString && !params[i].isAssignableFrom(String.class)) {
                    compatibleParamTypes = false;
                    break;
                }
            }

            if (!compatibleParamTypes) {
                continue;
            }

            if (selectedMethod != null
                    && Modifier.isStatic(selectedMethod.getModifiers())
                    && Modifier.isStatic(method.getModifiers())) {
                throw new RuntimeException(
                    "Ambiguous method reference: " + className + "." + methodName
                        + " [found " + selectedMethod + ", " + method + "]");
            }

            selectedMethod = method;
        }

        if (selectedMethod == null) {
            throw new RuntimeException("Method not found: " + className + "." + methodName);
        }

        if (!Modifier.isStatic(selectedMethod.getModifiers())) {
            throw new RuntimeException("Method must be static: " + selectedMethod);
        }

        return selectedMethod;
    }

}
