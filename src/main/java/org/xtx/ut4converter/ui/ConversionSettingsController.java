/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.model.UserConfig;
import org.xtx.ut4converter.config.model.UserGameConfig;
import org.xtx.ut4converter.export.SimpleTextureExtractor;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.export.UModelExporter;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.tools.Installation;

import javax.swing.*;
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
 * FXML Controller class
 *
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class ConversionSettingsController implements Initializable {
	@FXML
	public Label inputMapT3dLbl;

	@FXML
	public Button selectInputT3dMap;

	@FXML
	public Label t3dLabel;

	private Stage dialogStage;

	@FXML
	private Label inputGameLbl;
	@FXML
	private Label outputGameLbl;
	@FXML
	private Label inputMapLbl;
	@FXML
	private Label outputFolderLbl;

	/**
	 * Path where ressources will be referenced to
	 * (e.g: '/Game/RestrictedAssets/Maps/WIP/DM-Deck16'
	 */
	@FXML
	private Label ut4BaseReferencePath;

	private MainApp mainApp;

	private MapConverter mapConverter;

	private UTGames.UTGame inputGame;
	private UTGames.UTGame outputGame;

	private UserGameConfig userInputGameConfig;
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

	@FXML
	private ComboBox<String> texExtractorChoiceBox;

	/**
	 * Default light map resolution applied to brushes from Unreal Engine 1/2 converted maps
	 */
	static final int DEFAULT_LIGHTMAP_RESOLUTION_UE1_UE2 = 128;

	/**
	 * Default light map resolution applied to brushes from Unreal Engine 3 converted maps.
	 */
	static final int DEFAULT_LIGHTMAP_RESOLUTION_UE3 = 64;

	/**
	 * Default scale factor of converted maps from UE1 (Unreal, UT99) ut games to UT4
	 */
	public static final Float DEFAULT_SCALE_FACTOR_UE1_UE4 = 2.5f;

	/**
	 * Default scale when converting Unreal 2 map to UT4
	 */
	public static final Float DEFAULT_SCALE_UNREAL2_UE4 = 2.5f;

	/**
	 * Default scale factor of converted maps from UE2 (UT2003, UT2004) ut games to UT4
	 */
	public static final Float DEFAULT_SCALE_FACTOR_UE2_UE4 = 2.2f;

	/**
	 * Default scale factor of converted maps from UT3 (Unreal Engine 3) to UT4
	 */
	static final Float DEFAULT_SCALE_FACTOR_UE3_UE4 = 2.2f;

	@FXML
	private ComboBox<Integer> lightMapResolutionList;
	@FXML
	private ComboBox<Float> scaleFactorList;
	@FXML
	private ComboBox<Integer> lightningBrightnessFactor;
	@FXML
	private ComboBox<Integer> soundVolumeFactor;
	@FXML
	private Label outMapNameLbl;
	@FXML
	private Label ut4MapNameLbl;

	@FXML
	private Label ut4BaseReferencePathLbl;

	@FXML
	private Button ut4BaseReferencePathBtn;

	@FXML
	private CheckBox debugLogLevel;
	@FXML
	private Button changeMapNameBtn;


	@FXML
	private TextField classesNameFilter;

	/**
	 * Initializes the controller class.
	 *
	 * @param url Url
	 * @param rb Resource bundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		lightningBrightnessFactor.getSelectionModel().select(1);
		lightMapResolutionList.getItems().addAll(16,32,64,128,256,512,1024,2048,4096);
		lightMapResolutionList.getSelectionModel().select(lightMapResolutionList.getItems().indexOf(DEFAULT_LIGHTMAP_RESOLUTION_UE3));
		soundVolumeFactor.getSelectionModel().select(1);
		texExtractorChoiceBox.getSelectionModel().select("umodel");
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

	public void load() throws IOException {

		inputGameLbl.setText(inputGame.name);
		outputGameLbl.setText(outputGame.name);

		UserConfig userConfig = UserConfig.load();

		if (userConfig != null) {
			userInputGameConfig = userConfig.getGameConfigByGame(inputGame);
		}

		mapConverter = new MapConverter(inputGame, outputGame);

		// games we are working on and testing and adding blueprints
		if(inputGame == UTGame.U1 || inputGame == UTGame.U2) {
			mapConverter.setUseUbClasses(mainApp.isUseUbClasses());
		} else {
			mapConverter.setUseUbClasses(false);
		}

		if (inputGame == UTGame.UT3) {
			inputMapT3dLbl.setDisable(false);
			selectInputT3dMap.setDisable(false);
			t3dLabel.setDisable(false);
		} else {
			inputMapT3dLbl.setDisable(true);
			selectInputT3dMap.setDisable(true);
			t3dLabel.setDisable(true);
		}

		// set default scale value depending on input and output engine
		if(mapConverter.isTo(UTGames.UnrealEngine.UE4)) {
			switch (mapConverter.getInputGame().engine.version) {
				case 1 -> {
					scaleFactorList.getSelectionModel().select(DEFAULT_SCALE_FACTOR_UE1_UE4);
					lightMapResolutionList.getSelectionModel().select(DEFAULT_LIGHTMAP_RESOLUTION_UE1_UE2);
				}
				case 2 -> {
					if (inputGame == UTGame.U2) {
						scaleFactorList.getSelectionModel().select(DEFAULT_SCALE_UNREAL2_UE4);
					} else {
						scaleFactorList.getSelectionModel().select(DEFAULT_SCALE_FACTOR_UE2_UE4);
					}
					lightMapResolutionList.getSelectionModel().select(DEFAULT_LIGHTMAP_RESOLUTION_UE1_UE2);
				}
				default -> {
					scaleFactorList.getSelectionModel().select(DEFAULT_SCALE_FACTOR_UE3_UE4);
					lightMapResolutionList.getSelectionModel().select(DEFAULT_LIGHTMAP_RESOLUTION_UE3);
				}
			}
		} else if (mapConverter.isTo(UTGames.UnrealEngine.UE3)) {
			ut4MapNameLbl.setText("UT3 Map Name");
			ut4BaseReferencePath.setVisible(false);
			ut4BaseReferencePathLbl.setVisible(false);
			ut4BaseReferencePathBtn.setVisible(false);
			scaleFactorList.getSelectionModel().select(1.25f);
			lightMapResolutionList.getSelectionModel().select(DEFAULT_LIGHTMAP_RESOLUTION_UE3);
		}

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
	 */
	@FXML
	private void changeMapName() {

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
				mapConverter.initConvertedResourcesFolder();
				ut4BaseReferencePath.setText(mapConverter.getUt4ReferenceBaseFolder());
			}
		}

	}

	@FXML
	private void selectInputMap() throws IOException {

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
			mapConverter.initConvertedResourcesFolder();
			ut4BaseReferencePath.setDisable(false);
			ut4BaseReferencePath.setText(mapConverter.getUt4ReferenceBaseFolder());
			outputFolderLbl.setText(mapConverter.getOutPath().toString());
			outMapNameLbl.setText(mapConverter.getOutMapName());
		}
	}

	@FXML
	private void convert() {

		if (checkConversionSettings()) {

			try {
				mapConverter.setScale(scaleFactorList.getSelectionModel().getSelectedItem());
			} catch (ClassCastException ce) {
				ce.printStackTrace();
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Scale factor");
				alert.setContentText("Scale factor is not a valid number");
				scaleFactorList.setStyle("-fx-background-color: red");
				alert.showAndWait();
				return;
			}
			dialogStage.close();

			if (inputGame != UTGame.U2) {
				if ("umodel".equals(texExtractorChoiceBox.getSelectionModel().getSelectedItem())) {
					mapConverter.setPreferedTextureExtractorClass(UModelExporter.class);
				} else if ("UCC".equals(texExtractorChoiceBox.getSelectionModel().getSelectedItem())) {
					mapConverter.setPreferedTextureExtractorClass(UCCExporter.class);
				} else if ("Simple Texture Extractor".equals(texExtractorChoiceBox.getSelectionModel().getSelectedItem())) {
					mapConverter.setPreferedTextureExtractorClass(SimpleTextureExtractor.class);
				} else {
					mapConverter.setPreferedTextureExtractorClass(UModelExporter.class);
				}
			}


			mapConverter.setLightMapResolution(lightMapResolutionList.getSelectionModel().getSelectedItem());
			mapConverter.brightnessFactor = lightningBrightnessFactor.getSelectionModel().getSelectedItem();
			mapConverter.soundVolumeFactor = soundVolumeFactor.getSelectionModel().getSelectedItem();
			if (classesNameFilter.getLength() > 1) {
				mapConverter.setFilteredClasses(classesNameFilter.getText().trim().split(";"));
			}

			mapConverter.setConversionViewController(mainApp.showConversionView());

			SwingUtilities.invokeLater(mapConverter);
		}
	}

	/**
	 * All settings good
	 *
	 * @return <code>true</code> if conversion settings are fine else <code>false</code>
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

		// FOR UT3 need to have .t3d file created from UT3 editor
		// because the ut3.com batchexport command is buggy and messes up actors !
		if (mapConverter.getInputGame() == UTGame.UT3 && mapConverter.getInT3d() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Input .t3d map not set");
			alert.setHeaderText("Input map not set");
			alert.setContentText("Within UT3 editor, select all actors, copy and paste in a new .t3d file");

			alert.showAndWait();
			return false;
		}

		// for Unreal 1, needs to have oldunreal.com installed
		if(mapConverter.getInputGame() == UTGame.U1 && !new File(mapConverter.getUserConfig().getGameConfigByGame(UTGame.U1).getPath()+"/System/UCC.exe").exists()){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("OldUnreal Patch not installed");
			alert.setHeaderText("Patch from oldunreal.com is needed.");
			alert.setContentText("Install latest oldunreal.com patch and try again. It is required to extract some level resources.");

			alert.showAndWait();

			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.oldunreal.com/downloads/"));
				} catch (URISyntaxException | IOException ex) {
					Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			return false;
		}



		return true;
	}

	/**
	 * Select UT4 editor base reference path
	 */
	@FXML
	private void selectUt4BaseReferencePath() {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select ut4 editor reference base path");

		UserConfig userConfig;

		try {
			userConfig = UserConfig.load();

			if (userConfig != null) {
				File ut4RootPath = userConfig.getGameConfigByGame(UTGame.UT4).getPath();

				if (ut4RootPath.exists()) {
					File ut4RootContentPath = new File(ut4RootPath.getAbsolutePath() + File.separator + "UnrealTournament" + File.separator + "Content");

					if (ut4BaseReferencePath != null && ut4BaseReferencePath.getText() != null && !ut4BaseReferencePath.getText().trim().isEmpty()) {
						File ut4BaseRefPath = new File(ut4RootContentPath.getAbsolutePath() + File.separator + ut4BaseReferencePath);

						if (ut4BaseRefPath.getParentFile().exists()) {
							chooser.setInitialDirectory(ut4BaseRefPath.getParentFile());
						}
					}

					if (chooser.getInitialDirectory() == null) {
						chooser.setInitialDirectory(ut4RootContentPath);
					}

					File ut4RefFolder = chooser.showDialog(new Stage());

					if (ut4RefFolder != null) {
						if(ut4RefFolder.getPath().startsWith(ut4RootContentPath.getAbsolutePath())){
							String ut4BaseRef = "/Game" + ut4RefFolder.getAbsolutePath().substring(ut4RootContentPath.getAbsolutePath().length());
							ut4BaseRef = ut4BaseRef.replace("\\", "/");

							ut4BaseReferencePath.setText(ut4BaseRef);
							mapConverter.setUt4ReferenceBaseFolder(ut4BaseRef);
						} else {
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setTitle("Error");
							alert.setHeaderText("Operation failed");
							alert.setContentText("An error occured: Reference folder must be in ut4 content subfolder!");
							alert.showAndWait();
						}
					}
				} else {
					Logger.getGlobal().severe("UT4 Editor path not set !");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	private void close() {
		dialogStage.close();
	}

	@FXML
	private void toggleTexConversion() {
		mapConverter.setConvertTextures(convTexCheckBox.isSelected());
	}

	@FXML
	private void toggleSndConversion() {
		mapConverter.setConvertSounds(convSndCheckBox.isSelected());
	}

	@FXML
	private void toggleSmConversion() {
		mapConverter.setConvertStaticMeshes(convSmCheckBox.isSelected());
	}

	@FXML
	private void toggleMusicConversion() {
		mapConverter.setConvertMusic(convMusicCheckBox.isSelected());
	}

	/**
	 * Changes the log level
	 */
	@FXML
	private void toggleDebugLogLevel() {
		mapConverter.getLogger().setLevel(debugLogLevel.isSelected() ? Level.FINE : Level.INFO);
	}

	/**
	 * // FOR UT3 need to have the copied/pasted .td3 level from UT3 editor to have right order of brushes
	 * // because the UT3 commandlet is kinda in "alpha" stages
	 */
	public void selectInputT3dMap() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select " + inputGame.shortName + " .t3d map you created from " + mapConverter.getInputGame().shortName + " editor ");

		File mapFolder = UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame);

		if (mapFolder.exists()) {
			chooser.setInitialDirectory(UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame));
		}

		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.shortName + " Editor Map (*.t3d)", "*.t3d"));

		File t3dUt3EditorFile = chooser.showOpenDialog(new Stage());

		// UT3 commandlet export too buggy and messed .t3d file so need to use the exported one from UT3 editor
		if (t3dUt3EditorFile != null) {
			mapConverter.setInT3d(t3dUt3EditorFile);
		}
	}

	public void onScaleChange(KeyEvent keyEvent) {
		System.out.println("HELLO");
	}
}
