/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MainApp.FXMLoc;
import org.xtx.ut4converter.config.model.ApplicationConfig;
import org.xtx.ut4converter.config.model.UserConfig;
import org.xtx.ut4converter.tools.GitHubReleaseJson;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

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

	private UserConfig userConfig;

	private ApplicationConfig applicationConfig;

	@FXML
	private Menu menuFile;

	public MainSceneController() {
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		this.mainStage = mainApp.getPrimaryStage();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		try {
			this.applicationConfig = ApplicationConfig.load();

			// adds dynamically game to file menu
			for (Map.Entry<String, List<String>> entry : this.applicationConfig.getGameConversion().entrySet()) {

				final UnrealGame inputGame = this.applicationConfig.getUnrealGameById(entry.getKey());

				if (inputGame.isEditorOnly()) {
					continue;
				}

				final Menu gameFromMenu = new Menu(this.applicationConfig.getUnrealGameById(entry.getKey()).getName());
				gameFromMenu.setId("convert" + entry.getKey());

				for (final String outputShortNameGame : entry.getValue()) {

					final UnrealGame outputGame = this.applicationConfig.getUnrealGameById(outputShortNameGame);

					if (outputGame.isEditorOnly()) {
						continue;
					}
					final MenuItem gameToMenuItem = new MenuItem("Convert map to " + outputGame.getShortName() + " ...");
					gameToMenuItem.setId("convert" + entry.getKey() + outputGame);
					gameFromMenu.getItems().add(gameToMenuItem);

					gameToMenuItem.setOnAction(t -> {
						convertUtxMap(inputGame, outputGame);
					});
				}

				menuFile.getItems().add(0, gameFromMenu);
			}

			this.userConfig = UserConfig.load();

			if (userConfig.getIsFirstRun() == null || userConfig.getIsFirstRun()) {
				userConfig.setIsFirstRun(Boolean.FALSE);
				userConfig.saveFile();
				showAlertFirstTime();
			}

			// check for update at startup if user said so
			if (userConfig != null && userConfig.isCheckForUpdates()) {
				checkForUpdate(false);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			logger.error("initialize " + url, e);
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
		alert.setContentText("Version: " + MainApp.VERSION + "\nAuthor: " + MainApp.AUTHOR + "\nPowered by Java " + System.getProperty("java.version"));

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


	/**
	 *
	 * @param inputGame
	 *            Input UT Game
	 * @param outputGame
	 *            Output UT Game
	 */
	private void convertUtxMap(UnrealGame inputGame, UnrealGame outputGame) {

		try {
			if (checkGamePathSet(inputGame, outputGame)) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource(FXMLoc.CONV_SETTINGS.getPath()));
				AnchorPane page = loader.load();

				Stage dialogStage = new Stage();
				dialogStage.setTitle("Conversion Settings");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(mainStage);
				Scene scene = new Scene(page);
				dialogStage.setScene(scene);

				ConversionSettingsController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setInputGame(inputGame);
				controller.setOutputGame(outputGame);
				controller.setMainApp(mainApp);
				controller.load();

				// Show the dialog and wait until the user closes it
				dialogStage.showAndWait();
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Game paths not set");
				alert.setHeaderText("Need to set game path");
				alert.setContentText(inputGame.getName() + " and/or " + outputGame.getName() + " game path not set.");

				alert.showAndWait();
				showSettings();
			}
		} catch (Throwable t) {
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
		this.applicationConfig = ApplicationConfig.load();

		if (applicationConfig != null) {
			boolean gamePathSet = true;

			for (UnrealGame game : games) {
				gamePathSet &= game.getPath() != null && game.getPath().exists();
			}

			return gamePathSet;
		}

		return false;
	}


}
