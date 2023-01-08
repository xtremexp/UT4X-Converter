/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.model.ApplicationConfig;
import org.xtx.ut4converter.config.model.UserConfig;
import org.xtx.ut4converter.ucore.UnrealGame;

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


	/**
	 * Current user configuration
	 */
	ApplicationConfig appConfig;
	@FXML
	private Label settingsLog;

	/**
	 * If this checkbox is checked, program will check if there is an update at startup
	 */
	@FXML
	private CheckBox chkBoxCheckUpdates;

	@FXML
	private GridPane gridPane;

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


	/**
	 * Saves game path to UserConfig object
	 * 
	 * @param textFile Text field where new ut game path is stored
	 * @param utGame UT game to save
	 */
	private void saveGamePath(TextField textFile, UnrealGame utGame) {

		File gameFolder = new File(textFile.getText());

		if (gameFolder.exists()) {
			utGame.setPath(gameFolder);

			try {
				appConfig.saveFile();
				settingsLog.setText(utGame.getName() + " folder saved to " + ApplicationConfig.getApplicationConfigFile().getName());
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
			appConfig = ApplicationConfig.load();

			int gameIdx = 0;

			for (UnrealGame game : appConfig.getGames()) {

				// For UE4 needs to select the editor path and not the normal packaged game (which does not have editor)
				final Label gameLabel = new Label(game.getShortName()+ (game.getUeVersion() == 4 ? " Editor":""));
				final TextField textField = new TextField(game.getPath() != null ? game.getPath().getAbsolutePath() : "");
				textField.setEditable(false);
				textField.setPrefWidth(400d);
				textField.setPromptText("C:\\Program Files (x86)\\" + game.getName());

				final Button button = new Button("Select");
				button.setOnMouseClicked(mouseEvent -> setUTxFolder(game, textField));

				gridPane.add(gameLabel, 0, gameIdx);
				gridPane.add(textField, 1, gameIdx);
				gridPane.add(button, 2, gameIdx);

				gameIdx ++;
			}

			chkBoxCheckUpdates.setSelected(appConfig.isCheckForUpdates());
			chkBoxCheckUpdates.selectedProperty().addListener((observable, oldValue, newValue) -> {
				appConfig.setCheckForUpdates(newValue);
				try {
					appConfig.saveFile();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException ex) {
			ex.printStackTrace();
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
	private void setUTxFolder(UnrealGame utGame, TextField utPathTxtField) {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select " + utGame.getName() + " folder");

		if (utPathTxtField != null && utPathTxtField.getText() != null && new File(utPathTxtField.getText()).exists()) {
			chooser.setInitialDirectory(new File(utPathTxtField.getText()));
		}

		if (utGame.getUeVersion() >= 4) {
			chooser.setTitle("Select " + utGame.getName() + " editor folder");
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
