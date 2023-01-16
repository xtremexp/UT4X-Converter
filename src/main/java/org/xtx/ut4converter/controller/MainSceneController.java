/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MainApp.FXMLoc;
import org.xtx.ut4converter.config.ApplicationConfig;
import org.xtx.ut4converter.config.ConversionSettings;
import org.xtx.ut4converter.config.GameConversionConfig;
import org.xtx.ut4converter.tools.GitHubReleaseJson;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static org.xtx.ut4converter.tools.UIUtils.openUrl;

/**
 * FXML Controller class TODO i18n
 *
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class MainSceneController implements Initializable {


	/**
	 * Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(MainSceneController.class);


	public MainApp mainApp;
	public Stage mainStage;

	/**
	 * Application config file
	 */
	private ApplicationConfig applicationConfig;

	@FXML
	private Menu menuFile;


	@FXML
	final Menu menuRecent = new Menu("Recent conversions");

	public MainSceneController() {
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		this.mainStage = mainApp.getPrimaryStage();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		try {
			// at start load and merge is necessary from DefaultApplicationConfig.json file
			this.applicationConfig = ApplicationConfig.loadApplicationConfig();
			final ApplicationConfig defaultAppConfig = ApplicationConfig.loadDefaultApplicationConfig();

			// no config file, creates it from DefaultConfig
			if (this.applicationConfig == null) {
				this.applicationConfig = ApplicationConfig.loadDefaultApplicationConfig();
			}
			// update config file from DefaultConfig
			else {
				this.applicationConfig.mergeWithDefaultConfig(defaultAppConfig);
			}

			this.applicationConfig.setFile(ApplicationConfig.getApplicationConfigFile(false));
			this.applicationConfig.saveFile();

			// adds dynamically game to file menu
			for (final UnrealGame inputGame : this.applicationConfig.getGames()) {

				// Do not add menu for games that have no conversion possible to
				if (inputGame.getConvertsTo().isEmpty()) {
					continue;
				}

				final Menu gameFromMenu = new Menu(inputGame.getName());
				gameFromMenu.setId("convert" + inputGame.getShortName());

				for (final GameConversionConfig gameConvConfig : inputGame.getConvertsTo()) {

					final UnrealGame outputGame = this.applicationConfig.getUnrealGameById(gameConvConfig.getGameId());
					final MenuItem gameToMenuItem = new MenuItem("Convert map to " + outputGame.getShortName() + " ...");

					gameToMenuItem.setId("convert" + inputGame.getShortName() + outputGame);
					gameFromMenu.getItems().add(gameToMenuItem);
					gameToMenuItem.setOnAction(t -> convertUtxMap(inputGame, outputGame));
				}

				// add before the separator and exit submenu
				menuFile.getItems().add(menuFile.getItems().size() - 2, gameFromMenu);
			}

			addRecentConversationsMenu(this.applicationConfig);

			if (applicationConfig.getIsFirstRun() == null || applicationConfig.getIsFirstRun()) {
				applicationConfig.setIsFirstRun(Boolean.FALSE);
				applicationConfig.saveFile();
				showAlertFirstTime();
			}

			// check for update at startup if user said so
			if (applicationConfig != null && applicationConfig.isCheckForUpdates()) {
				checkForUpdate(false);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			logger.error("initialize " + url, e);
		}
	}

	public void addRecentConversationsMenu(ApplicationConfig appConfig) throws IOException {


		if (!appConfig.getRecentConversions().isEmpty()) {

			for (ConversionSettings convSettings : appConfig.getRecentConversions()) {
				final MenuItem menuItemRecentConv = new MenuItem(convSettings.getInputMap().getName() + " (" + convSettings.getInputGameId() + "->" + convSettings.getOutputGameId() + ", " + convSettings.getScaleFactor() + "X)");
				menuItemRecentConv.setOnAction(t -> convertUtxMap(convSettings));
				menuRecent.getItems().add(menuItemRecentConv);
			}

			if (!menuFile.getItems().contains(menuRecent)) {
				menuFile.getItems().add(menuFile.getItems().size() - 2, menuRecent);
			}
		}
	}

	private void showAlertFirstTime() {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Welcome!");
		alert.setHeaderText("Welcome to UT4 Converter");

		String msg = "In order to fully use the converter you need to set: \n";
		msg += "- the unreal game(s) path(s)\n";
		msg += "\nPress OK to go to the settings panel";

		alert.setContentText(msg);

		alert.showAndWait();
		showSettings();
	}

	/**
	 * Exit program
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}


	/**
	 * Show credits about program TODO history, library used, licence
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("About " + MainApp.PROGRAM_NAME + ": ");
		alert.setContentText("Version: " + MainApp.VERSION + "\nAuthor: " + MainApp.AUTHOR + "\nPowered by Java " + Runtime.version().feature() + "." + Runtime.version().interim()+ "." + Runtime.version().update());

		alert.showAndWait();
	}

	/**
	 * Display Settings panel
	 */
	@FXML
	private void handleSettings() {

		showSettings();
	}

	private void showSettings() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXMLoc.SETTINGS.getPath()));
			AnchorPane page = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Settings");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			SettingsSceneController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
		} catch (IOException e) {
			logger.error("showSettings", e);
		}
	}

	private void checkForUpdate(boolean showAlertIfNoUpdate) throws IOException, InterruptedException {
		final GitHubReleaseJson newLatestRelease = Installation.checkForUpdate();

		if (newLatestRelease != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Information");
			alert.setHeaderText("New update available");
			alert.setContentText("A new update is available do you want to go download site ?\nYour version: v" + MainApp.VERSION + "\nLatest version: " + newLatestRelease.getTagName());

			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get() == ButtonType.OK) {
				openUrl(newLatestRelease.getHtmlUrl(), false, null);
			}
		} else if(showAlertIfNoUpdate) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information");
			alert.setHeaderText("No new update is available");
			alert.setContentText("No new update is available");

			alert.showAndWait();
		}
	}

	/**
	 * Checks from github api if there is a new update for program
	 */
	@FXML
	private void openGitHubUrlReleases() {
		try {
			checkForUpdate(true);
		} catch (IOException | InterruptedException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error while checking for update.");
			alert.setContentText("Error while checking for update " + e.getMessage());

			alert.showAndWait();
			throw new RuntimeException(e);
		}
	}

	@FXML
	private void openGitHubWiki() {
		openUrl("https://github.com/xtremexp/UT4X-Converter/wiki", true, null);
	}

	@FXML
	private void openGitHubUrl() {
		openUrl("https://github.com/xtremexp/UT4X-Converter", true, null);
	}

	private void convertUtxMap(final ConversionSettings conversionSettings)  {

		try {
			this.applicationConfig = ApplicationConfig.loadApplicationConfig();
			final UnrealGame inputGame = this.applicationConfig.getUnrealGameById(conversionSettings.getInputGameId());
			final UnrealGame outputGame = this.applicationConfig.getUnrealGameById(conversionSettings.getOutputGameId());

			if (checkGamePathSet(inputGame, outputGame)) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource(FXMLoc.CONV_SETTINGS.getPath()));
				VBox page = loader.load();

				Stage dialogStage = new Stage();
				dialogStage.setTitle("Conversion Settings");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(mainStage);
				Scene scene = new Scene(page);
				dialogStage.setScene(scene);

				ConversionSettingsController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setMainApp(mainApp);
				controller.initFromConversionSettings(conversionSettings);

				// Show the dialog and wait until the user closes it
				dialogStage.showAndWait();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error loading file", e);
		}
	}

	/**
	 *
	 * @param inputGame
	 *            Input UT Game
	 * @param outputGame
	 *            Output UT Game
	 */
	private void convertUtxMap(UnrealGame inputGame, UnrealGame outputGame) {

		try {
			// refresh input game/output game config
			// user may have set unreal game install path after trying to convert if it was not set
			this.applicationConfig = ApplicationConfig.loadApplicationConfig();
			UnrealGame finalInputGame = inputGame;
			final UnrealGame inUnrealGameConfig = this.applicationConfig.getGames().stream().filter(g -> g.getShortName().equals(finalInputGame.getShortName())).findFirst().orElse(null);

			if (inUnrealGameConfig != null) {
				inputGame = inUnrealGameConfig;
			}

			UnrealGame finalOutputGame = outputGame;
			final UnrealGame outUnrealGameConfig = this.applicationConfig.getGames().stream().filter(g -> g.getShortName().equals(finalOutputGame.getShortName())).findFirst().orElse(null);

			if (outUnrealGameConfig != null) {
				outputGame = outUnrealGameConfig;
			}

			if (checkGamePathSet(inputGame, outputGame)) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource(FXMLoc.CONV_SETTINGS.getPath()));
				VBox page = loader.load();

				Stage dialogStage = new Stage();
				dialogStage.setTitle("Conversion Settings");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(mainStage);
				Scene scene = new Scene(page);
				dialogStage.setScene(scene);

				ConversionSettingsController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setMainApp(mainApp);
				controller.initFromInputAndOutputGame(inputGame, outputGame);

				// Show the dialog and wait until the user closes it
				dialogStage.showAndWait();
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Unreal game paths not set");
				alert.setContentText(inputGame.getName() + " and/or " + outputGame.getName() + " game path not set or invalid.");

				alert.showAndWait();
				showSettings();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			logger.error("convertUtxMap " + inputGame.getName() + " to " + outputGame.getName(), t);

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("Error detail :" + t.getMessage());

			alert.showAndWait();
		}

	}

	private boolean checkGamePathSet(UnrealGame... games) throws IOException {

		// need to reload if user changed path in settings
		this.applicationConfig = ApplicationConfig.loadApplicationConfig();
		// refresh game path if user has changed it

		boolean gamePathSet = true;

		for (UnrealGame game : games) {
			// sync game path with one from config file
			this.applicationConfig.getGames().stream().filter(g -> g.getShortName().equals(game.getShortName())).findFirst().ifPresent(configGame -> game.setPath(configGame.getPath()));
			gamePathSet &= game.getPath() != null && game.getPath().exists();
		}

		return gamePathSet;
	}


	public void goToConversionWiki() {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/xtremexp/UT4X-Converter/wiki"));
			} catch (URISyntaxException | IOException ex) {
				java.util.logging.Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
