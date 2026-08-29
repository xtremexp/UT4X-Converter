package org.xtx.ut4converter;

import com.twelvemonkeys.imageio.plugins.bmp.BMPImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.pcx.PCXImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.tga.TGAImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.controller.ConversionViewController;
import org.xtx.ut4converter.controller.MainSceneController;

import javax.imageio.spi.IIORegistry;
import java.io.IOException;

/**
 *
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class MainApp extends Application {

	private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

	/**
	 * Program Name
	 */
	public static final String PROGRAM_NAME = "UT Converter";

	/**
	 * Version of UT4 Converter
	 */
	public static final String VERSION = "1.4.11";

	/**
	 * Author
	 */
	public static final String AUTHOR = "Thomas 'WinterIsComing/XtremeXp' P.";

	/**
	 * All scenes should be enumerated here
	 */
	public enum FXMLoc {

		/**
		 *
		 */
		MAIN("/fxml/Scene.fxml"),

		/**
		 * App settings
		 */
		SETTINGS("/fxml/SettingsScene.fxml"),
		/**
		 * Conversion settings view
		 */
		CONV_SETTINGS("/fxml/ConversionSettings.fxml"),

		/**
		 * View to export selected package
		 */
		EXPORT_PACKAGE("/fxml/ExportPackage.fxml"),

		GAMES_EDIT("/fxml/EditGames.fxml"),
		CONV_VIEW("/fxml/ConversionView.fxml");

		final String path;

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

		//this.useUbClasses = true;

		logger.info(PROGRAM_NAME + " " + VERSION + " started");

		// support for reading/writing tga/bmp/dds/tiff/pcx texture files
		final IIORegistry registry = IIORegistry.getDefaultInstance();
		registry.registerServiceProvider(new TGAImageReaderSpi());
		registry.registerServiceProvider(new BMPImageReaderSpi());
		registry.registerServiceProvider(new PCXImageReaderSpi());
		registry.registerServiceProvider(new TIFFImageReaderSpi());
		registry.registerServiceProvider(new net.nikr.dds.DDSImageReaderSpi());
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

			primaryStage.getIcons().add(new Image("images/ut-converter-logo.png"));
			primaryStage.setScene(scene);
			primaryStage.show();

			primaryStage.setOnCloseRequest(t -> {
				try {
					stop();
				} catch (Exception e) {
					logger.error("Error while closing", e);
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
			logger.error("Error loading view " + name, e);
		}

		return null;
	}


	/**
	 * Show conversion view (log table, ...)
	 *
	 * @return Controller of view
	 */
	public ConversionViewController showConversionView() {
		return (ConversionViewController) showView(FXMLoc.CONV_VIEW.path);
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


	public boolean isUseUbClasses() {
		return useUbClasses;
	}
}
