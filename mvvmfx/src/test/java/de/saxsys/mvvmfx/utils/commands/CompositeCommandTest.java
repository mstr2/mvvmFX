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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.cedarsoft.test.utils.CatchAllExceptionsRule;
import de.saxsys.mvvmfx.testingutils.JfxToolkitExtension;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.saxsys.mvvmfx.testingutils.GCVerifier;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JfxToolkitExtension.class)
public class CompositeCommandTest {



	private BooleanProperty condition1;
	private BooleanProperty called1;
	private DelegateCommand delegateCommand1;
	private BooleanProperty condition2;
	private BooleanProperty called2;
	private DelegateCommand delegateCommand2;
	
	@BeforeEach
	public void init() {
		condition1 = new SimpleBooleanProperty(true);
		called1 = new SimpleBooleanProperty();
		delegateCommand1 = new DelegateCommand(() -> called1.set(true), condition1);
		
		condition2 = new SimpleBooleanProperty(true);
		called2 = new SimpleBooleanProperty();
		delegateCommand2 = new DelegateCommand(() -> called2.set(true), condition2);
	}
	
	@Test
	public void executable() throws Exception {
		CompositeCommand compositeCommand = new CompositeCommand(delegateCommand1, delegateCommand2);
		
		GCVerifier.forceGC();
		
		assertTrue(compositeCommand.isExecutable());
		assertFalse(compositeCommand.isNotExecutable());
		
		condition1.set(false);
		
		assertFalse(compositeCommand.isExecutable());
		assertTrue(compositeCommand.isNotExecutable());
		
		condition2.set(false);
		
		assertFalse(compositeCommand.isExecutable());
		assertTrue(compositeCommand.isNotExecutable());
		
		condition1.set(true);
		
		assertFalse(compositeCommand.isExecutable());
		assertTrue(compositeCommand.isNotExecutable());
		
		condition2.set(true);
		
		assertTrue(compositeCommand.isExecutable());
		assertFalse(compositeCommand.isNotExecutable());
	}
	
	@Test
	public void executable2() throws Exception {
		CompositeCommand compositeCommand = new CompositeCommand();
		GCVerifier.forceGC();
		
		
		assertThat(compositeCommand.isExecutable()).isTrue();
		assertThat(compositeCommand.isNotExecutable()).isFalse();
		
		compositeCommand.register(delegateCommand1);
		GCVerifier.forceGC();
		
		assertThat(compositeCommand.isExecutable()).isTrue();
		assertThat(compositeCommand.isNotExecutable()).isFalse();
		
		condition1.setValue(false);
		assertThat(compositeCommand.isExecutable()).isFalse();
		assertThat(compositeCommand.isNotExecutable()).isTrue();
		
		condition1.setValue(true);
		assertThat(compositeCommand.isExecutable()).isTrue();
		assertThat(compositeCommand.isNotExecutable()).isFalse();
		
		condition2.setValue(false);
		assertThat(compositeCommand.isExecutable()).isTrue();
		assertThat(compositeCommand.isNotExecutable()).isFalse();
		
		compositeCommand.register(delegateCommand2);
		GCVerifier.forceGC();
		assertThat(compositeCommand.isExecutable()).isFalse();
		assertThat(compositeCommand.isNotExecutable()).isTrue();
		
		compositeCommand.unregister(delegateCommand2);
		GCVerifier.forceGC();
		assertThat(compositeCommand.isExecutable()).isTrue();
		assertThat(compositeCommand.isNotExecutable()).isFalse();
	}
	
	@Test
	public void register() throws Exception {
		
		CompositeCommand compositeCommand = new CompositeCommand(delegateCommand1);
		
		assertTrue(compositeCommand.isExecutable());
		assertFalse(compositeCommand.isNotExecutable());
		// prepare delegateCommand2
		condition2.set(false);
		
		compositeCommand.register(delegateCommand2);
		assertFalse(compositeCommand.isExecutable());
		assertTrue(compositeCommand.isNotExecutable());
		
		compositeCommand.unregister(delegateCommand2);
		assertTrue(compositeCommand.isExecutable());
		assertFalse(compositeCommand.isNotExecutable());
	}
	
	@Test
	public void allCommandsAreUnregistered() throws Exception {
		CompositeCommand compositeCommand = new CompositeCommand(delegateCommand1, delegateCommand2);
		
		compositeCommand.unregister(delegateCommand1);
		compositeCommand.unregister(delegateCommand2);
	}
	
	@Disabled("unstable test. Needs to be fixed. see bug #260")
	@Test
	public void longRunningAsyncComposite() throws Exception {
		
		BooleanProperty condition = new SimpleBooleanProperty(true);
		CompletableFuture<Void> commandStarted = new CompletableFuture<>();
		CompletableFuture<Void> commandCompleted = new CompletableFuture<>();
		CompletableFuture<Void> future = new CompletableFuture<>();
		
		DelegateAsyncCommand delegateCommand1 = new DelegateAsyncCommand(() -> sleep(500), condition);
		
		DelegateAsyncCommand delegateCommand2 = new DelegateAsyncCommand(() -> {
				sleep(1000);
				future.complete(null);
			}, condition);
		
		DelegateCommand delegateCommand3 = new DelegateCommand(() -> {}, condition);
		
		CompositeCommand compositeCommand = new CompositeCommand(delegateCommand1, delegateCommand2, delegateCommand3);
		
		GCVerifier.forceGC();
		
		compositeCommand.runningProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue && !oldValue) {  // command is now running, wasn't running before
				Platform.runLater(() -> {
					assertTrue(compositeCommand.runningProperty().get());
					assertTrue(delegateCommand1.runningProperty().get());
					assertTrue(delegateCommand2.runningProperty().get());
					assertTrue(delegateCommand3.runningProperty().get());
					assertFalse(compositeCommand.notRunningProperty().get());
					assertFalse(delegateCommand1.notRunningProperty().get());
					assertFalse(delegateCommand2.notRunningProperty().get());
					assertFalse(delegateCommand3.notRunningProperty().get());
					
					commandStarted.complete(null);
				});
			}
			if (oldValue && !newValue) { // command was running, isn't running now
				Platform.runLater(() -> {
					assertFalse(compositeCommand.runningProperty().get());
					assertFalse(delegateCommand1.runningProperty().get());
					assertFalse(delegateCommand2.runningProperty().get());
					assertFalse(delegateCommand3.runningProperty().get());
					assertTrue(compositeCommand.notRunningProperty().get());
					assertTrue(delegateCommand1.notRunningProperty().get());
					assertTrue(delegateCommand2.notRunningProperty().get());
					assertTrue(delegateCommand3.notRunningProperty().get());
					
					commandCompleted.complete(null);
				});
			}
		});
		
		compositeCommand.execute();
		commandStarted.get(5, TimeUnit.SECONDS);
		future.get(5, TimeUnit.SECONDS);
		commandCompleted.get(5, TimeUnit.SECONDS);
	}
	
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
