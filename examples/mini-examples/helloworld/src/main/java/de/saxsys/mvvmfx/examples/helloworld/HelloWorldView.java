package de.saxsys.mvvmfx.examples.helloworld;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

public class HelloWorldView implements FxmlView<HelloWorldViewModel> {

	@FXML
	private Label helloLabel;

	@InjectViewModel
	private HelloWorldViewModel viewModel;

}
