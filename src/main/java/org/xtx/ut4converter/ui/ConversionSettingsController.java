/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import javafx.scene.control.TextField;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.tools.Installation;

/**
 * FXML Controller class
 *
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class ConversionSettingsController implements Initializable {
	@FXML
	private AnchorPane ConversionSettings;

	Stage dialogStage;

	@FXML
	private Label inputGameLbl;
	@FXML
	private Label outputGameLbl;
	@FXML
	private Label inputMapLbl;
	@FXML
	private Label outputFolderLbl;

	MainApp mainApp;

	MapConverter mapConverter;

	UTGames.UTGame inputGame;
	UTGames.UTGame outputGame;

	UserConfig userConfig;
	UserGameConfig userInputGameConfig;
	UserGameConfig userOutputGameConfig;
	@FXML
	private TitledPane advancedSettingsTitle;
	@FXML
	private TitledPane mainSettingsTitle;
	@FXML
	private CheckBox convTexCheckBox;
	@FXML
	private CheckBox convSndCheckBox;
	@FXML
	private CheckBox convSmCheckBox;
	@FXML
	private CheckBox convMusicCheckBox;

	/**
	 * Default scale factor of converted maps.
	 */
	BigDecimal scaleFactor = new BigDecimal("2.5");
	@FXML
	private ComboBox<String> scaleFactorList;
	@FXML
	private ComboBox<String> lightningBrightnessFactor;
	@FXML
	private ComboBox<String> soundVolumeFactor;
	@FXML
	private Label outMapNameLbl;
	// @FXML
	// private Label relativeUtMapPathLbl;
	@FXML
	private CheckBox debugLogLevel;
	@FXML
	private Button changeMapNameBtn;

	@FXML
	private Button changeRelativeUtMapPathBtn;

	@FXML
	private Label warningMessage;

	@FXML
	private TextField classesNameFilter;

	/**
	 * Initializes the controller class.
	 * 
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
		advancedSettingsTitle.setText("Advanced Settings");
		mainSettingsTitle.setText("Main Settings");

		scaleFactorList.getSelectionModel().select(String.valueOf(scaleFactor));
		lightningBrightnessFactor.getSelectionModel().select("1");
		soundVolumeFactor.getSelectionModel().select("1");
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setInputGame(UTGames.UTGame inputGame) {
		this.inputGame = inputGame;
	}

	public void setOutputGame(UTGames.UTGame outputGame) {
		this.outputGame = outputGame;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void load() throws JAXBException {

		inputGameLbl.setText(inputGame.name);
		outputGameLbl.setText(outputGame.name);

		userConfig = UserConfig.load();

		if (userConfig != null) {
			userInputGameConfig = userConfig.getGameConfigByGame(inputGame);
			userOutputGameConfig = userConfig.getGameConfigByGame(outputGame);
		}

		if (userConfig.getUModelPath() == null || !userConfig.getUModelPath().exists()) {
			warningMessage.setText("UModel not set in settings. Some ressources might not be exported.");
			warningMessage.setStyle("-fx-text-fill: red; -fx-font-size: 16;");
		}

		mapConverter = new MapConverter(inputGame, outputGame);

		// relativeUtMapPathLbl.setText(mapConverter.getRelativeUtMapPath());
		disableConversionType();

		initConvCheckBoxes();
	}

	private void initConvCheckBoxes() {
		convSndCheckBox.setSelected(mapConverter.convertSounds());
		convTexCheckBox.setSelected(mapConverter.convertTextures());
		convMusicCheckBox.setSelected(mapConverter.convertMusic());
		convSmCheckBox.setSelected(mapConverter.convertStaticMeshes());
	}

	/**
	 * Disable conversion of some type of ressources depending on game because
	 * all ressource converter not done yet
	 */
	private void disableConversionType() {

		boolean canConvertTextures = mapConverter.canConvertTextures();
		mapConverter.setConvertTextures(canConvertTextures);
		convTexCheckBox.setDisable(!canConvertTextures);

		boolean canConvertSounds = mapConverter.canConvertSounds();
		mapConverter.setConvertSounds(canConvertSounds);
		convSndCheckBox.setDisable(!canConvertSounds);

		boolean canConvertMusic = mapConverter.canConvertMusic();
		mapConverter.setConvertMusic(canConvertMusic);
		convMusicCheckBox.setDisable(!canConvertMusic);

		boolean canConvertStaticMeshes = mapConverter.canConvertStaticMeshes();
		mapConverter.setConvertStaticMeshes(canConvertStaticMeshes);
		convSmCheckBox.setDisable(!canConvertStaticMeshes);
	}

	/**
	 * Allow changing the default ut4 map name suggested by ut4 converter
	 * 
	 * @param event
	 */
	@FXML
	private void changeMapName(ActionEvent event) {

		TextInputDialog dialog = new TextInputDialog(mapConverter.getOutMapName());

		dialog.setTitle("Text Input Dialog");
		dialog.setHeaderText("Map Name Change");
		dialog.setContentText("Enter UT4 map name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			String newMapName = result.get();
			newMapName = T3DUtils.filterName(newMapName);

			if (newMapName.length() > 3) {
				mapConverter.setOutMapName(newMapName);
				outMapNameLbl.setText(mapConverter.getOutMapName());
			}
		}

	}

	@FXML
	private void changeRelativeUtMapPath(ActionEvent event) {

		// TODO
		TextInputDialog dialog = new TextInputDialog(mapConverter.getOutMapName());

		dialog.setTitle("Text Input Dialog");
		dialog.setHeaderText("Map Name Change");
		dialog.setContentText("Enter UT4 map name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			String newMapName = result.get();
			newMapName = T3DUtils.filterName(newMapName);

			if (newMapName.length() > 3) {
				mapConverter.setOutMapName(newMapName);
				outMapNameLbl.setText(mapConverter.getOutMapName());
			}
		}

	}

	@FXML
	private void selectInputMap(ActionEvent event) {

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select " + inputGame.shortName + " map");

		File mapFolder = UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame);

		if (mapFolder.exists()) {
			chooser.setInitialDirectory(UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame));
		}

		// TODO check U1 uccbin oldunreal.com patch for export U1 maps to unreal
		// text files with linux
		if (Installation.isLinux()) {
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.shortName + " Map (*.t3d)", "*.t3d"));
		} else {
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.shortName + " Map (*." + inputGame.mapExtension + ", *.t3d)", "*." + inputGame.mapExtension, "*.t3d"));
		}

		File unrealMap = chooser.showOpenDialog(new Stage());

		if (unrealMap != null) {
			changeMapNameBtn.setDisable(false);
			inputMapLbl.setText(unrealMap.getName());
			mapConverter.setInMap(unrealMap);
			outputFolderLbl.setText(mapConverter.getOutPath().toString());
			outMapNameLbl.setText(mapConverter.getOutMapName());
		}
	}

	@FXML
	private void convert(ActionEvent event) {

		if (checkConversionSettings()) {

			dialogStage.close();

			mapConverter.setScale(Double.valueOf(scaleFactorList.getSelectionModel().getSelectedItem()));
			mapConverter.brightnessFactor = Float.valueOf(lightningBrightnessFactor.getSelectionModel().getSelectedItem());
			mapConverter.soundVolumeFactor = Float.valueOf(soundVolumeFactor.getSelectionModel().getSelectedItem());
			if (classesNameFilter.getLength() > 1) {
				mapConverter.setFilteredClasses(classesNameFilter.getText().trim().split(";"));
			}

			mapConverter.setConversionViewController(mainApp.showConversionView());

			if (mapConverter.isFrom(UnrealEngine.UE3)) {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Select " + inputGame.shortName + " .t3d map you created from UT3 editor ");

				File mapFolder = UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame);

				if (mapFolder.exists()) {
					chooser.setInitialDirectory(UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame));
				}

				chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.shortName + " Editor Map (*.t3d)", "*.t3d"));

				File t3dUt3EditorFile = chooser.showOpenDialog(new Stage());

				if (t3dUt3EditorFile != null) {
					mapConverter.setIntT3dUt3Editor(t3dUt3EditorFile);
				} else {
					return;
				}
			}

			SwingUtilities.invokeLater(mapConverter);
		}
	}

	/**
	 * All settings good
	 * 
	 * @return
	 */
	private boolean checkConversionSettings() {

		if (mapConverter.getInMap() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Input map not set");
			alert.setHeaderText("Input map not set");
			alert.setContentText("Select your " + inputGame.name + " input map");

			alert.showAndWait();
			return false;
		}

		return true;
	}

	@FXML
	private void close(ActionEvent event) {
		dialogStage.close();
	}

	@FXML
	private void toggleTexConversion(ActionEvent event) {
		mapConverter.setConvertTextures(convTexCheckBox.isSelected());
	}

	@FXML
	private void toggleSndConversion(ActionEvent event) {
		mapConverter.setConvertSounds(convSndCheckBox.isSelected());
	}

	@FXML
	private void toggleSmConversion(ActionEvent event) {
		mapConverter.setConvertStaticMeshes(convSmCheckBox.isSelected());
	}

	@FXML
	private void toggleMusicConversion(ActionEvent event) {
		mapConverter.setConvertMusic(convMusicCheckBox.isSelected());
	}

	/**
	 * Changes the log level
	 * 
	 * @param event
	 */
	@FXML
	private void toggleDebugLogLevel(ActionEvent event) {
		mapConverter.getLogger().setLevel(debugLogLevel.isSelected() ? Level.FINE : Level.INFO);
	}

}
