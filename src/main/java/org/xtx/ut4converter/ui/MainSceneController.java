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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MainApp.FXMLoc;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.model.UserConfig;
import org.xtx.ut4converter.export.SimpleTextureExtractor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class TODO i18n
 * 
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class MainSceneController implements Initializable {

	/**
	 * Link to UT3 Converter topic (at old ut forums)
	 */
	final String URL_UT3CONV_FORUM = "http://utforums.epicgames.com/showthread.php?p=25131566";

	/**
	 * Link to UT4 Converter topic
	 */
	private final String URL_UT4CONV_FORUM = "https://forums.unrealtournament.com/showthread.php?18198";

	/**
	 * Url to git hub for source code
	 */
	private final String URL_UTCONV_GITHUB = "https://github.com/xtremexp/UT4Converter";

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showAlertFirstTime() {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Welcome!");
		alert.setHeaderText("Welcome to UT4 Converter");

		String msg = "Note that since staticmesh and terrain conversion is not yet supported \n";
		msg += "conversion of some maps from Unreal 2/UT2003/UT2004/UT3 might be pretty bad\n\n";

		msg += "In order to fully use the converter you need to set: \n";
		msg += "- the unreal game(s) path(s)\n";
		msg += "- download and set the umodel program file path (download at: http://www.gildor.org/en/projects/umodel ) \n";
		msg += "- download and set the nconvert program file path (download at: http://www.xnview.com/en/nconvert/ ) \n";
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
	 * @param event
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
			e.printStackTrace();
		}
	}

	/**
	 * Opens url in web browser
	 *
	 * @param url
	 *            Url to open with web browser
	 */
	public static void openUrl(String url, boolean confirmBeforeOpen, String message) {

		if (url == null) {
			return;
		}

		if (confirmBeforeOpen) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Web browser access");

			message = message != null ? (message + " \n") : "";
			message += "Do you want to open web browser to this url ?\n" + url;

			alert.setContentText(message);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() != ButtonType.OK) {
				return;
			}
		} else if (message != null) {

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Information");
			alert.setHeaderText("Message");

			alert.setContentText(message);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() != ButtonType.OK) {
				return;
			}
		}

		if (Desktop.isDesktopSupported()) {
			try {
				Desktop desktop = Desktop.getDesktop();

				desktop.browse(new URI(url));
			} catch (URISyntaxException | IOException ex) {
				Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Impossible to open web browser");
			alert.setContentText("Your system is not or does not support desktop. \n Manually go to:" + url);

			alert.showAndWait();
		}
	}

	/**
	 * OPen web browser to ut4 converter topic at ut forums
	 * 
	 * @param event
	 */
	@FXML
	private void openUtTopicUrl(ActionEvent event) {
		openUrl(URL_UT4CONV_FORUM, true, null);
	}


	/*
	@FXML
	private void openGitHubIssues(ActionEvent event){
		openUrl(URL_UTCONV_GITHUB_ISSUES, true, "Game and map info are needed.");
	}*/

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
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Game paths not set");
				alert.setHeaderText("Need to set game path");
				alert.setContentText(inputGame.name + " and/or " + outputGame.name + " game path not set.");

				alert.showAndWait();
				showSettings();
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO alert
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

		File texFolder = UTGames.getTexturesFolder(this.userConfig.getGameConfigByGame(UTGame.U2).getPath(), UTGame.U2);

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
