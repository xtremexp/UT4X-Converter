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
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MainApp.FXMLoc;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.model.UserConfig;
import org.xtx.ut4converter.config.model.UserGameConfig;
import org.xtx.ut4converter.export.SimpleTextureExtractor;
import org.xtx.ut4converter.tools.GitHubReleaseJson;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.tools.UIUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

	public MainSceneController() {
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		this.mainStage = mainApp.getPrimaryStage();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		try {
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

	@FXML
	private void convertUT99ToUT3Map() {
		convertUtxMap(UTGame.UT99, UTGame.UT3);
	}

	/**
	 * Opens file browser for UT99 .t3d map, then convert it.
	 */
	@FXML
	private void convertUT99ToUT4Map() {
		convertUtxMap(UTGame.UT99, UTGame.UT4);
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


	@FXML
	private void convertU1toUT3Map() {
		convertUtxMap(UTGame.U1, UTGame.UT3);
	}

	@FXML
	private void convertU1toUT4Map() {
		convertUtxMap(UTGame.U1, UTGame.UT4);
	}

	/**
	 *
	 * @param inputGame
	 *            Input UT Game
	 * @param outputGame
	 *            Output UT Game
	 */
	private void convertUtxMap(UTGame inputGame, UTGame outputGame) {

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
				alert.setContentText(inputGame.name + " and/or " + outputGame.name + " game path not set.");

				alert.showAndWait();
				showSettings();
			}
		} catch (Throwable t) {
			logger.error("convertUtxMap " + inputGame.name + " to " + outputGame.name, t);

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("Error detail :" + t.getMessage());

			alert.showAndWait();
		}

	}

	private boolean checkGamePathSet(UTGame... games) throws IOException {
		UserConfig userConfig = UserConfig.load();
		if(userConfig != null){
			boolean gamePathSet = true;

			for (UTGame game : games) {
				gamePathSet &= userConfig.hasGamePathSet(game);
			}

			return gamePathSet;
		}

		return false;
	}

	@FXML
	private void convertUt2004ToUT3Map() {
		convertUtxMap(UTGame.UT2004, UTGame.UT3);
	}

	@FXML
	private void convertUt2004ToUT4Map() {
		convertUtxMap(UTGame.UT2004, UTGame.UT4);
	}

	@FXML
	private void convertUT3ToUT4Map() {
		convertUtxMap(UTGame.UT3, UTGame.UT4);
	}

	@FXML
	private void convertUdkToUT4Map() {
		convertUtxMap(UTGame.UDK, UTGame.UT4);
	}

	@FXML
	private void convertUT2003ToUT3Map() {
		convertUtxMap(UTGame.UT2003, UTGame.UT3);
	}

	@FXML
	private void convertUT2003ToUT4Map() {
		convertUtxMap(UTGame.UT2003, UTGame.UT4);
	}

	@FXML
	private void convertU2ToUT3Map() {
		convertUtxMap(UTGame.U2, UTGame.UT3);
	}

	@FXML
	private void convertU2ToUT4Map() {
		convertUtxMap(UTGame.U2, UTGame.UT4);
	}

	/**
	 * Allow extract textures from Unreal 2 package
	 */
	@FXML
	private void extractTexturesFromU2Package(){

		final UTGame unreal2 = UTGame.U2;
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select your " + unreal2.name + " texture package");

		final UserGameConfig userGameConfigU2 = this.userConfig.getGameConfigByGame(UTGame.U2);

		if (userGameConfigU2 == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unreal 2 path not set");
			alert.setHeaderText("Unreal 2 installation path needs to be set.");
			alert.setContentText("Adjust settings for unreal 2 game");

			alert.showAndWait();
			showSettings();
			return;
		}

		File texFolder = UTGames.getTexturesFolder(userGameConfigU2.getPath(), UTGame.U2);

		if (texFolder != null && texFolder.exists()) {
			chooser.setInitialDirectory(texFolder);
		}

		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(unreal2.shortName + " texture package (*.utx)", "*.utx"));
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(unreal2.shortName + " texture package (*.u)", "*.u"));

		File texturePackage = chooser.showOpenDialog(new Stage());

		if(texturePackage != null){
			final DirectoryChooser dcOutputFolder = new DirectoryChooser();
			dcOutputFolder.setTitle("Select where you want to extract textures");

			final File outputFolder = dcOutputFolder.showDialog(new Stage());
			if(outputFolder != null){
				try {
					SimpleTextureExtractor.extractSimple(texturePackage, outputFolder);
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Texture package succesfully extracted !");
					alert.setHeaderText("Texture package succesfully extracted !");
					alert.showAndWait();
				} catch (Exception e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error while extracting texture package.");
					alert.setHeaderText("Error while extracting texture package.");
					alert.showAndWait();
					e.printStackTrace();
				}
			}
		}
	}

}
