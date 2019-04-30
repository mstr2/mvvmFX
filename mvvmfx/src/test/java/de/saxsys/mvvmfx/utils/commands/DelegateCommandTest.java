/*******************************************************************************
 * Copyright 2015 Alexander Casall, Manuel Mauky
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
 ******************************************************************************/
package de.saxsys.mvvmfx.utils.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import de.saxsys.mvvmfx.testingutils.JfxToolkitExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.saxsys.mvvmfx.testingutils.FxTestingUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.junit.jupiter.api.extension.ExtendWith;



@ExtendWith(JfxToolkitExtension.class)
public class DelegateCommandTest {

	
	@Test
	public void executable() {
		BooleanProperty condition = new SimpleBooleanProperty(true);
		
		DelegateCommand delegateCommand = new DelegateCommand(() -> {}, condition);
		
		assertTrue(delegateCommand.isExecutable());
		assertFalse(delegateCommand.isNotExecutable());
		
		condition.set(false);
		
		assertFalse(delegateCommand.isExecutable());
		assertTrue(delegateCommand.isNotExecutable());
		
		condition.set(true);
		
		assertTrue(delegateCommand.isExecutable());
		assertFalse(delegateCommand.isNotExecutable());
	}
	
	@Test
	public void commandNotExecutable() {
		BooleanProperty condition = new SimpleBooleanProperty(false);
		
		DelegateCommand delegateCommand = new DelegateCommand(() -> {}, condition);

		Assertions.assertThrows(RuntimeException.class, () -> {
			delegateCommand.execute();
		});
	}
	
	@Test
	public void longRunningAsync() throws Exception {
		
		BooleanProperty condition = new SimpleBooleanProperty(true);
		
		CompletableFuture<Void> commandStarted = new CompletableFuture<>();
		CompletableFuture<Void> commandCompleted = new CompletableFuture<>();
		
		DelegateAsyncCommand delegateCommand = new DelegateAsyncCommand(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
		}, condition);
		
		assertFalse(delegateCommand.runningProperty().get());
		assertTrue(delegateCommand.notRunningProperty().get());
		
		delegateCommand.runningProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				assertTrue(delegateCommand.runningProperty().get());
				assertFalse(delegateCommand.notRunningProperty().get());
				assertFalse(delegateCommand.executableProperty().get());
				assertTrue(delegateCommand.notExecutableProperty().get());
				commandStarted.complete(null);
			}
			if (!newValue && oldValue) {
				assertFalse(delegateCommand.runningProperty().get());
				assertTrue(delegateCommand.notRunningProperty().get());
				assertTrue(delegateCommand.executableProperty().get());
				assertFalse(delegateCommand.notExecutableProperty().get());
				commandCompleted.complete(null);
			}
			
		});
		
		Platform.runLater(delegateCommand::execute);
		commandStarted.get(3, TimeUnit.SECONDS);
		commandCompleted.get(4, TimeUnit.SECONDS);
	}
	
	
	@Test
	public void progressProperty() throws Exception {
		
		CompletableFuture<Void> stepOne = new CompletableFuture<>();
		CompletableFuture<Void> stepTwo = new CompletableFuture<>();
		CompletableFuture<Void> stepThree = new CompletableFuture<>();
		CompletableFuture<Void> stepFour = new CompletableFuture<>();
		
		DelegateAsyncCommand command = new DelegateAsyncCommand(new Action() {
			@Override
			protected void action(Object parameter) {
				updateProgress(0, 3);
				stepOne.complete(null);
				sleep(500);
				updateProgress(1, 3);
				stepTwo.complete(null);
				sleep(500);
				updateProgress(2, 3);
				stepThree.complete(null);
				sleep(500);
				updateProgress(3, 3);
				stepFour.complete(null);
			}
		});
		
		Platform.runLater(command::execute);
		
		stepOne.get(1, TimeUnit.SECONDS);
		Platform.runLater(() -> assertThat(command.getProgress()).isEqualTo(0.0));
		
		stepTwo.get(1, TimeUnit.SECONDS);
		Platform.runLater(() -> assertThat(command.getProgress()).isEqualTo(0.3, offset(0.1)));
		
		stepThree.get(1, TimeUnit.SECONDS);
		Platform.runLater(() -> assertThat(command.getProgress()).isEqualTo(0.6, offset(0.1)));
		
		stepFour.get(1, TimeUnit.SECONDS);
		Platform.runLater(() -> assertThat(command.getProgress()).isEqualTo(1, offset(0.1)));
		
		// sleep to prevent the Junit thread from exiting
		// before eventual assertion errors from the JavaFX Thread are detected
		sleep(500);
	}
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
