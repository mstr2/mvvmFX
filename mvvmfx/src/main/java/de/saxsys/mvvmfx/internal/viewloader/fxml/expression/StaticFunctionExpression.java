package de.saxsys.mvvmfx.internal.viewloader.fxml.expression;

import javafx.beans.value.ObservableValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticFunctionExpression extends Expression<Object> {

    private static final String[] DEFAULT_PACKAGES = new String[] { "java.lang" };
    private static Map<String, Method> methodCache = new HashMap<>();

    private final Method method;
    private final List<Expression<?>> args;

    public StaticFunctionExpression(KeyPath keyPath, List<Expression<?>> args) {
        String funcName = keyPath.toString();
        String funcKey = funcName + "`" + args.size();
        Method method = methodCache.get(funcKey);
        if (method == null) {
            method = getMethod(funcName, args.size());
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

    private Method getMethod(String name, int argCount) {
        if (!name.contains(".")) {
            throw new RuntimeException("Invalid method: " + name);
        }

        String className = name.substring(0, name.lastIndexOf("."));
        String methodName = name.substring(className.length() + 1);
        Class<?> clazz = null;

        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            for (String defaultPackage : DEFAULT_PACKAGES) {
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
            if (method.getName().equals(methodName)
                    && (!method.isVarArgs() && method.getParameterCount() == argCount
                        || method.isVarArgs() && method.getParameterCount() <= argCount)) {
                if (selectedMethod != null) {
                    throw new RuntimeException("Ambiguous method reference: " + className + "#" + methodName);
                }

                selectedMethod = method;
            }
        }

        if (selectedMethod == null) {
            throw new RuntimeException("Method not found: " + className + "#" + methodName);
        }

        return selectedMethod;
    }

}
