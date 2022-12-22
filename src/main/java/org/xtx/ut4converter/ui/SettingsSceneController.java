/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.model.UserConfig;
import org.xtx.ut4converter.config.model.UserGameConfig;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.tools.UIUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 * 
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class SettingsSceneController implements Initializable {
	@FXML
	private TextField ut99Path;
	@FXML
	private TextField u1Path;
	@FXML
	private TextField ut2004Path;
	@FXML
	private TextField ut2003Folder;
	@FXML
	private TextField ut3Folder;
	@FXML
	private TextField udkFolder;
	@FXML
	private TextField ut4EditorFolder;
	@FXML
	private TextField u2Path;

	/**
	 * Current user configuration
	 */
	UserConfig userConfig;
	@FXML
	private Label settingsLog;

	private Stage dialogStage;


	/**
	 * Initializes the controller class.
	 * 
	 * @param url Url
	 * @param rb Resource Bundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		loadSettings();
	}

	@FXML
	private void selectU1Folder() {
		setUTxFolder(UTGame.U1, u1Path);
	}

	@FXML
	private void selectUt99Folder() {
		setUTxFolder(UTGame.UT99, ut99Path);
	}

	@FXML
	private void selectUt2003Folder() {
		setUTxFolder(UTGame.UT2003, ut2003Folder);
	}

	@FXML
	private void selectUt2004Folder() {
		setUTxFolder(UTGame.UT2004, ut2004Path);
	}

	@FXML
	private void selectUt3Folder() {
		setUTxFolder(UTGame.UT3, ut3Folder);
	}

	@FXML
	private void selectUdkFolder() {
		setUTxFolder(UTGame.UDK, udkFolder);
	}

	@FXML
	private void selectUt4EditorFolder() {
		setUTxFolder(UTGame.UT4, ut4EditorFolder);
	}

	@FXML
	private void selectU2Folder() {
		setUTxFolder(UTGame.U2, u2Path);
	}


	@FXML
	private void gotoUModelWebsite() {

		final String uModelUrl = "http://www.gildor.org/en/projects/umodel";
		UIUtils.openUrl(uModelUrl, false, "Press ok to go to umodel website for download:\n" + uModelUrl);
	}

	/**
	 * Saves game path to UserConfig object
	 * 
	 * @param textFile Text field where new ut game path is stored
	 * @param utGame UT game to save
	 */
	private void saveGamePath(TextField textFile, UTGames.UTGame utGame) {

		UserGameConfig gc = userConfig.getGameConfigByGame(utGame);
		File gameFolder = new File(textFile.getText());

		if (gameFolder.exists()) {
			if (gc == null) {
				userConfig.getGame().add(new UserGameConfig(utGame, gameFolder));
			} else {
				gc.setPath(new File(textFile.getText()));
			}

			try {
				userConfig.saveFile();
				settingsLog.setText(utGame.name + " folder saved to " + UserConfig.getUserConfigFile().getName());
			} catch (IOException ex) {
				Logger.getLogger(SettingsSceneController.class.getName()).log(Level.SEVERE, null, ex);
				settingsLog.setText("An error occured while saving " + UserConfig.USER_CONFIG_JSON_FILE + " : " + ex.getMessage());
			}
		} else {
			showErrorMessage(textFile.getText() + " is not valid folder");
		}
	}

	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Operation failed");
		alert.setContentText("An error occured: " + message);
		alert.showAndWait();
	}

	/**
	 * Load current user settings from XML file and display game paths if
	 * available.
	 */
	private void loadSettings() {
		try {
			userConfig = UserConfig.load();

			for (UserGameConfig game : userConfig.getGame()) {

				if (game.getPath() != null && null != game.getId()) {
					switch (game.getId()) {
						case UT99 -> ut99Path.setText(game.getPath().getAbsolutePath());
						case U1 -> u1Path.setText(game.getPath().getAbsolutePath());
						case U2 -> u2Path.setText(game.getPath().getAbsolutePath());
						case UT2003 -> ut2003Folder.setText(game.getPath().getAbsolutePath());
						case UT2004 -> ut2004Path.setText(game.getPath().getAbsolutePath());
						case UT3 -> ut3Folder.setText(game.getPath().getAbsolutePath());
						case UDK -> udkFolder.setText(game.getPath().getAbsolutePath());
						case UT4 -> ut4EditorFolder.setText(game.getPath().getAbsolutePath());
						default -> {
						}
					}
				}
			}

		} catch (IOException ex) {
			Logger.getLogger(SettingsSceneController.class.getName()).log(Level.SEVERE, null, ex);
			showErrorMessage("An error occured while loading UserConfig file :" + ex.getMessage());
		}
	}

	/**
	 * Sets and save ut path to xml user config file on click "Select"
	 * 
	 * @param utGame
	 *            UT game to set path
	 * @param utPathTxtField
	 *            Textfield for path game display in settings
	 */
	private void setUTxFolder(UTGame utGame, TextField utPathTxtField) {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select " + utGame.name + " folder");

		if (utPathTxtField != null && utPathTxtField.getText() != null && new File(utPathTxtField.getText()).exists()) {
			chooser.setInitialDirectory(new File(utPathTxtField.getText()));
		}

		if (utGame == UTGame.UT4) {
			chooser.setTitle("Select " + utGame.name + " editor folder");
		}

		File utxFolder = chooser.showDialog(new Stage());

		if (utPathTxtField != null && utxFolder != null) {
			utPathTxtField.setText(utxFolder.getAbsolutePath());
			saveGamePath(utPathTxtField, utGame);
		}
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void closeDialog() {
		this.dialogStage.close();
	}


}
