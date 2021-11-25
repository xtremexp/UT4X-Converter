/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
	private Logger logger = LoggerFactory.getLogger(MainSceneController.class);

	// TODO - should be in properties file
	/**
	 * Url to git hub for source code
	 */
	private final String URL_UTCONV_GITHUB = "https://github.com/xtremexp/UT4X-Converter";

	/**
	 * Url to git hub releases
	 */
	private final String URL_UTCONV_GITHUB_RELEASES = "https://github.com/xtremexp/UT4X-Converter/releases";

	/**
	 * Url to github project for reporting issues
	 */
	private final String URL_UTCONV_GITHUB_ISSUES = "https://github.com/xtremexp/UT4X-Converter/issues";

	@FXML
	private MenuItem menuExit;
	@FXML
	private MenuItem menuItemAbout;
	@FXML
	private Menu menuOptions;
	@FXML
	private MenuItem menuSettings;

	@FXML
	private MenuItem menuCheckForUpdates;

	/**
	 * Entry meny to github project page
	 */
	@FXML
	private MenuItem menuGitHub;

	/**
	 * Entry meny to github releases
	 */
	@FXML
	private MenuItem menuGitHubReleases;

	public MainApp mainApp;
	public Stage mainStage;

	private UserConfig userConfig;

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
		} catch (IOException e) {
			logger.error("initialize " + url, e);
		}
	}

	private void showAlertFirstTime() {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Welcome!");
		alert.setHeaderText("Welcome to UT4 Converter");

		String msg = "In order to fully use the converter you need to set: \n";
		msg += "- the unreal game(s) path(s)\n";
		msg += "- download and set the umodel program file path (download at: http://www.gildor.org/en/projects/umodel ) \n";
		msg += "\nPress OK to go to the settings panel";

		alert.setContentText(msg);

		alert.showAndWait();
		showSettings();
	}

	/**
	 * Exit program
	 *
	 * @param event
	 */
	@FXML
	private void handleExit(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * Opens file browser for UT99 .t3d map, then convert it.
	 *
	 * @param event
	 */
	@FXML
	private void handleConvert(ActionEvent event) {
		convertUtxMap(UTGame.UT99);
	}

	/**
	 * Show credits about program TODO history, library used, licence
	 *
	 * @param event Event
	 */
	@FXML
	private void handleAbout(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("About " + MainApp.PROGRAM_NAME + ": ");
		alert.setContentText("Version: " + MainApp.VERSION + "\nAuthor: " + MainApp.AUTHOR + "\nPowered by Java " + System.getProperty("java.version"));

		alert.showAndWait();
	}

	/**
	 * Display Settings panel
	 *
	 * @param event
	 */
	@FXML
	private void handleSettings(ActionEvent event) {

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


	@FXML
	private void openGitHubIssues(ActionEvent event){
		openUrl(URL_UTCONV_GITHUB_ISSUES, true, "Game and map info are needed.");
	}

	@FXML
	private void openGitHubUrlReleases(ActionEvent event) {
		openUrl(URL_UTCONV_GITHUB_RELEASES, true, null);
	}

	@FXML
	private void openGitHubUrl(ActionEvent event) {
		openUrl(URL_UTCONV_GITHUB, true, null);
	}

	@FXML
	private void handleConvertU1Map(ActionEvent event) {
		convertUtxMap(UTGame.U1);
	}

	private void convertUtxMap(UTGame inputGame) {

		convertUtxMap(inputGame, UTGame.UT4);
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
			} else if(this.userConfig.getUModelPath() == null || !this.userConfig.getUModelPath().exists()){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("umodel.exe path not set or invalid.");
				alert.setHeaderText("Need to set umodel.exe path");
				alert.setContentText("See umodel.exe path in settings and start again.");

				alert.showAndWait();
				showSettings();
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
	private void convertUt2004Map(ActionEvent event) {
		convertUtxMap(UTGame.UT2004);
	}

	@FXML
	private void convertUt3Map(ActionEvent event) {
		convertUtxMap(UTGame.UT3);
	}

	@FXML
	private void convertUdkMap(ActionEvent event) {
		convertUtxMap(UTGame.UDK);
	}


	@FXML
	private void convertUt2003Map(ActionEvent event) {
		convertUtxMap(UTGame.UT2003);
	}

	@FXML
	private void convertU2Map(ActionEvent event) {
		convertUtxMap(UTGame.U2);
	}

	/**
	 * Allow extract textures from Unreal 2 package
	 * @param event
	 */
	@FXML
	private void extractTexturesFromU2Package(ActionEvent event){

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

		if (texFolder.exists()) {
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
