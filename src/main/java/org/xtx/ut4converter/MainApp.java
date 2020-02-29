package org.xtx.ut4converter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.ui.ConversionViewController;
import org.xtx.ut4converter.ui.MainSceneController;

import java.io.IOException;

/**
 * 
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class MainApp extends Application {

	private Logger logger = LoggerFactory.getLogger(MainApp.class);

	/**
	 * Program Name
	 */
	public static final String PROGRAM_NAME = "UT4X Converter";

	/**
	 * Version of UT4 Converter
	 */
	public static final String VERSION = "1.0.0";

	/**
	 * Author
	 */
	public static final String AUTHOR = "XtremeXp / WinterIsComing";

	/**
	 * All scenes should be enumerated here
	 */
	public enum FXMLoc {

		MAIN("/fxml/Scene.fxml"), WELCOME("/fxml/WelcomeView.fxml"), SETTINGS("/fxml/SettingsScene.fxml"), CONV_SETTINGS("/fxml/ConversionSettings.fxml"), CONVERSION("/fxml/ConversionView.fxml");

		String path;

		FXMLoc(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

	}

	private boolean useUbClasses;

	Stage primaryStage;
	BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(PROGRAM_NAME + " - " + VERSION);

		// main scene with only menu
		initRootLayout();

		if(getParameters().getRaw().contains("-useubclasses")){
			this.useUbClasses = true;
		}

		showWelcomeView();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXMLoc.MAIN.path));
			rootLayout = loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);

			primaryStage.setScene(scene);
			primaryStage.show();

			primaryStage.setOnCloseRequest(t -> {
				try {
					stop();
				} catch (Exception e) {
				}
				System.exit(0);
			});

			MainSceneController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the welcome overview inside the root layout.
	 */
	public void showWelcomeView() {
		showView(FXMLoc.WELCOME.path);
		logger.info(PROGRAM_NAME + " " + VERSION + " started");
	}

	/**
	 * 
	 * @param name
	 *            fxml view file relative path
	 * @return Controller of view
	 */
	private Object showView(String name) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(name));
			Pane view = loader.load();

			rootLayout.setCenter(view);

			return loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Shows user settings panel
	 */
	public void showUserSettingsView() {
		showView(FXMLoc.SETTINGS.path);
	}

	/**
	 * Show conversion view (log table, ...)
	 * 
	 * @return Controller of view
	 */
	public ConversionViewController showConversionView() {
		return (ConversionViewController) showView(FXMLoc.CONVERSION.path);
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public BorderPane getRootLayout() {
		return rootLayout;
	}

	public boolean isUseUbClasses() {
		return useUbClasses;
	}
}
