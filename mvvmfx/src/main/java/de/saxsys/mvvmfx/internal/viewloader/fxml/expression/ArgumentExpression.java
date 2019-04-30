package de.saxsys.mvvmfx.internal.viewloader.fxml.expression;

import de.saxsys.mvvmfx.internal.viewloader.fxml.FXMLLoader;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ArgumentExpression extends Expression<Object> {

    private static final String BINDING_EXPRESSION_PREFIX = "{";
    private static final String BINDING_EXPRESSION_POSTFIX = "}";

    enum Type {
        RESOURCE_KEY,
        LITERAL,
        BINDING
    }

    private final KeyPath keyPath;
    private final String value;
    private final Type type;

    ArgumentExpression(String value) {
        if (isBindingExpression(value)) {
            this.value = null;
            keyPath = KeyPath.parse(
                value.substring(BINDING_EXPRESSION_PREFIX.length(), value.length() - BINDING_EXPRESSION_POSTFIX.length()));
            type = Type.BINDING;
        } else if (isResourceKey(value)) {
            this.value = value.substring(FXMLLoader.RESOURCE_KEY_PREFIX.length());
            keyPath = null;
            type = Type.RESOURCE_KEY;
        } else {
            this.value = value;
            keyPath = null;
            type = Type.LITERAL;
        }
    }

    public Type getType() {
        return type;
    }

    @Override
    public Object evaluate(Object namespace, boolean throwIfNotResolved) {
        if (type == Type.LITERAL) {
            return value;
        }

        if (type == Type.RESOURCE_KEY) {
            ResourceBundle resources = null;
            if (namespace instanceof Map) {
                resources = (ResourceBundle)((Map)namespace).get(FXMLLoader.RESOURCES_KEY);
            }

            if (resources != null) {
                return resources.getString(value);
            }

            return null;
        }

        return get(namespace, keyPath, throwIfNotResolved);
    }

    @Override
    public void update(Object namespace, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDefined(Object namespace) {
        if (keyPath != null) {
            return isDefined(namespace, keyPath);
        }

        return true;
    }

    @Override
    public boolean isLValue() {
        return true;
    }

    @Override
    protected void getArguments(List<KeyPath> arguments) {
        if (keyPath != null) {
            arguments.add(keyPath);
        }
    }

    private static boolean isBindingExpression(String aValue) {
        return aValue.startsWith(BINDING_EXPRESSION_PREFIX) && aValue.endsWith(BINDING_EXPRESSION_POSTFIX);
    }

    private static boolean isResourceKey(String aValue) {
        return aValue.startsWith(FXMLLoader.RESOURCE_KEY_PREFIX);
    }

}
