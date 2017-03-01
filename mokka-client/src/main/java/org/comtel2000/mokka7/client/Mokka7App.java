package org.comtel2000.mokka7.client;

import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.client.presentation.MainView;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.slf4j.LoggerFactory;

import com.airhacks.afterburner.injection.Injector;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Mokka7App extends Application {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Mokka7App.class);

	private final SimpleDoubleProperty sceneWidthProperty = new SimpleDoubleProperty(1024);
	private final SimpleDoubleProperty sceneHeightProperty = new SimpleDoubleProperty(600);

	@Override
	public void start(Stage stage) throws Exception {

		stage.setTitle("Mokka7 client (" + System.getProperty("javafx.runtime.version") + ")");
		stage.setResizable(true);

		Injector.setLogger((t) -> logger.trace(t));

		Injector.setModelOrService(S7Client.class, new S7Client());

		SessionManager session = (SessionManager) Injector.instantiateModelOrService(SessionManager.class);
		session.setSession(getClass().getName().toLowerCase());
		session.loadSession();

		session.bind(sceneWidthProperty, "scene.width");
		session.bind(sceneHeightProperty, "scene.height");

		MainView main = new MainView();

		final Scene scene = new Scene(main.getView(), sceneWidthProperty.get(), sceneHeightProperty.get());
		stage.setOnCloseRequest((e) -> {
			sceneWidthProperty.set(scene.getWidth());
			sceneHeightProperty.set(scene.getHeight());
			Injector.forgetAll();
			System.exit(0);
		});
		stage.setScene(scene);
		stage.getIcons().add(new Image(Mokka7App.class.getResourceAsStream("icon.png")));
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
