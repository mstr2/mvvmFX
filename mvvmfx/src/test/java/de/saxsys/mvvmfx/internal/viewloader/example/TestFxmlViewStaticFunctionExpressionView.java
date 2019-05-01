package de.saxsys.mvvmfx.internal.viewloader.example;

import de.saxsys.mvvmfx.FxmlView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TestFxmlViewStaticFunctionExpressionView
        implements FxmlView<TestFxmlViewStaticFunctionExpressionViewModel> {

    @FXML
    public Label label1;

    @FXML
    public Label label2;

    private final IntegerProperty number1 = new SimpleIntegerProperty(this, "number1");

    public IntegerProperty number1Property() {
        return number1;
    }

    public int getNumber1() {
        return number1.get();
    }

    public int getNumber2() {
        return 9;
    }

    public void setNumber1(int value) {
        number1.set(value);
    }

}
