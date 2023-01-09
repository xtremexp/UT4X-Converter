
/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.model.ApplicationConfig;
import org.xtx.ut4converter.export.SimpleTextureExtractor;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.export.UModelExporter;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.UnrealGame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;
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

	private Stage dialogStage;

	@FXML
	private Label outputFolderLbl;


	private MainApp mainApp;

	private MapConverter mapConverter;

	/**
	 * Input unreal game to convert from
	 */
	private UnrealGame inputGame;

	/**
	 * Output unreal game to convert to
	 */
	private UnrealGame outputGame;

	private ApplicationConfig applicationConfig;

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
	public static final Double DEFAULT_SCALE_FACTOR_UE1_UE4 = 2.5d;

	/**
	 * Default scale when converting Unreal 2 map to UT4
	 */
	public static final Double DEFAULT_SCALE_UNREAL2_UE4 = 2.5d;

	/**
	 * Default scale factor of converted maps from UE2 (UT2003, UT2004) ut games to UT4
	 */
	public static final Double DEFAULT_SCALE_FACTOR_UE2_UE4 = 2.2d;

	/**
	 * Default scale factor of converted maps from UT3 (Unreal Engine 3) to UT4
	 */
	static final Double DEFAULT_SCALE_FACTOR_UE3_UE4 = 2.2d;

	@FXML
	private ComboBox<Integer> lightMapResolutionList;
	@FXML
	private ComboBox<Double> scaleFactorList;
	@FXML
	private ComboBox<Float> lightningBrightnessFactor;
	@FXML
	private ComboBox<Float> soundVolumeFactor;
	@FXML
	private Label outMapNameLbl;

	@FXML
	private CheckBox debugLogLevel;

	@FXML
	private TextField classesNameFilter;

	@FXML
	private GridPane gridPaneMainSettings;

	private Label inputMapPathLbl;


	private Label ue4RefPathLbl;

	private Button changeMapNameBtn;

	private Button changeUe4RefPathBtn;

	/**
	 * Initializes the controller class.
	 *
	 * @param url Url
	 * @param rb Resource bundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		lightningBrightnessFactor.getItems().addAll(0.6f,0.8f,1f,1.2f,1.4f,1.6f,1.8f,2f);
		lightningBrightnessFactor.getSelectionModel().select(2); //1

		lightMapResolutionList.getItems().addAll(16,32,DEFAULT_LIGHTMAP_RESOLUTION_UE3,128,256,512,1024,2048,4096);
		lightMapResolutionList.getItems().sort(Comparator.naturalOrder());
		lightMapResolutionList.getSelectionModel().select(lightMapResolutionList.getItems().indexOf(DEFAULT_LIGHTMAP_RESOLUTION_UE3));

		soundVolumeFactor.getItems().addAll(0.6f,0.8f,1f,1.2f,1.4f,1.6f,1.8f,2f);
		soundVolumeFactor.getSelectionModel().select(1f);

		texExtractorChoiceBox.getSelectionModel().select("umodel");
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setInputGame(UnrealGame inputGame) {
		this.inputGame = inputGame;
	}

	public void setOutputGame(UnrealGame outputGame) {
		this.outputGame = outputGame;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}


	private Label createLabelWithTooltip(String text, String tooltip){
		final Label lbl = new Label(text);
		lbl.setTooltip(new Tooltip(tooltip));
		return lbl;
	}

	public void load() throws IOException {

		// INPUT MAP SETTINGS
		gridPaneMainSettings.add( createLabelWithTooltip("Input Game:", "Input unreal game"), 0, 0);
		gridPaneMainSettings.add(new Label(inputGame.getName()), 1, 0);

		// Select map
		gridPaneMainSettings.add(new Label("Map :"), 0, 1);
		inputMapPathLbl = new Label("Select map !");
		gridPaneMainSettings.add(inputMapPathLbl, 1, 1);
		Button selectInputMapBtn = new Button("Select");
		selectInputMapBtn.setOnAction(t -> selectInputMap());
		gridPaneMainSettings.add(selectInputMapBtn, 2, 1);

		// FOR input UE3 game we need the .t3d from editor to get the right brush order
		int rowIdx = 3;

		// UT3 Only - Select .t3d map
		if (inputGame.getShortName().equals("UT3")) {
			gridPaneMainSettings.add(new Label("Map (.t3d):"), 0, rowIdx);
			inputMapT3dLbl = new Label("Select UT3 .t3d Map!");
			gridPaneMainSettings.add(inputMapT3dLbl, 1, rowIdx);
			Button selectInputT3dMapBtn = new Button("Select");
			selectInputT3dMapBtn.setOnAction(t -> selectInputT3dMap());
			gridPaneMainSettings.add(selectInputT3dMapBtn, 2, rowIdx++);
		}

		gridPaneMainSettings.add(new Separator(), 0, rowIdx++, 3, 1);

		// OUTPUT MAP SETTINGS
		gridPaneMainSettings.add(createLabelWithTooltip("Output Game :", "Output game map will be converted to."), 0, rowIdx);
		gridPaneMainSettings.add(new Label(outputGame.getName()), 1, rowIdx++);

		gridPaneMainSettings.add(createLabelWithTooltip("Output Folder: ", "Folder where map and their resources (textures, ...)\n will be converted to."), 0, rowIdx);
		outputFolderLbl = new Label("");
		gridPaneMainSettings.add(outputFolderLbl, 1, rowIdx++);

		gridPaneMainSettings.add(createLabelWithTooltip("Scale Factor:", "How much the map will be scaled to."), 0, rowIdx);
		scaleFactorList = new ComboBox<>();
		scaleFactorList.setEditable(true);
		scaleFactorList.getItems().addAll(.5, .8, .9, 1., 1.1, 1.25, 1.5, 1.5625, 1.75, 1.875, 2., 2.1878, 2.2, 2.25, 2.3, 2.35, 2.4, 2.45, DEFAULT_SCALE_FACTOR_UE1_UE4, 2.55, 2.6, 3., 3.125, 3.5, 4., 4.5, 5.);
		scaleFactorList.getItems().sort(Comparator.naturalOrder());
		scaleFactorList.setConverter(new StringConverter<>() {

			@Override
			public String toString(Double object) {
				if (object == null) return null;
				return object.toString();
			}

			@Override
			public Double fromString(String string) {
				if (string != null) {
					return Double.parseDouble(string);
				} else {
					return null;
				}
			}
		});
		gridPaneMainSettings.add(scaleFactorList, 1, rowIdx++);

		gridPaneMainSettings.add(createLabelWithTooltip(outputGame.getShortName() + " Map Name :", "Output map name"), 0, rowIdx);
		outMapNameLbl = new Label("");
		gridPaneMainSettings.add(outMapNameLbl, 1, rowIdx);

		changeMapNameBtn = new Button("Change");
		changeMapNameBtn.setDisable(true);
		changeMapNameBtn.setOnAction(t -> changeMapName());
		gridPaneMainSettings.add(changeMapNameBtn, 2, rowIdx++);

		// UE4 Editor only
		if (outputGame.getUeVersion() == 4) {
			gridPaneMainSettings.add(createLabelWithTooltip("UE4 Editor ref. base path:", "Folder within UE4 editor where map resources will be stored."), 0, rowIdx);
			ue4RefPathLbl = new Label("");
			gridPaneMainSettings.add(ue4RefPathLbl, 1, rowIdx);
			changeUe4RefPathBtn = new Button("Change");
			changeUe4RefPathBtn.setDisable(true);
			changeUe4RefPathBtn.setOnAction(t -> selectUe4EditorBaseRefPath(outputGame));
			gridPaneMainSettings.add(changeUe4RefPathBtn, 2, rowIdx);
		}


		if (applicationConfig == null) {
			applicationConfig = ApplicationConfig.load();
		}

		mapConverter = new MapConverter(inputGame, outputGame);

		// games we are working on and testing and adding blueprints
		if("U1".equals(inputGame.getShortName()) || "U2".equals(inputGame.getShortName())) {
			mapConverter.setUseUbClasses(mainApp.isUseUbClasses());
		} else {
			mapConverter.setUseUbClasses(false);
		}


		// set default scale value depending on input and output engine
		if(mapConverter.isTo(UnrealEngine.UE4)) {
			switch (mapConverter.getInputGame().getUeVersion()) {
				case 1 -> {
					scaleFactorList.getSelectionModel().select(DEFAULT_SCALE_FACTOR_UE1_UE4);
					lightMapResolutionList.getSelectionModel().select(DEFAULT_LIGHTMAP_RESOLUTION_UE1_UE2);
				}
				case 2 -> {
					if ("U2".equals(inputGame.getShortName())) {
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
		} else if (mapConverter.isTo(UnrealEngine.UE3)) {
			scaleFactorList.getSelectionModel().select(1.25d);
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

				if(ue4RefPathLbl != null) {
					ue4RefPathLbl.setText(mapConverter.getUt4ReferenceBaseFolder());
				}
			}
		}

	}

	@FXML
	private void selectInputMap()  {

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select " + inputGame.getShortName() + " map");

		File mapFolder = new File(this.inputGame.getPath() + File.separator + this.inputGame.getMapFolder());

		if (mapFolder.exists()) {
			chooser.setInitialDirectory(mapFolder);
		}

		// TODO check U1 uccbin oldunreal.com patch for export U1 maps to unreal
		// text files with linux
		if (Installation.isLinux()) {
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.getShortName() + " Map (*.t3d)", "*.t3d"));
		} else {
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.getShortName() + " Map (*." + inputGame.getMapExt() + ", *.t3d)", "*." + inputGame.getMapExt(), "*.t3d"));
		}

		File unrealMap = chooser.showOpenDialog(new Stage());

		if (unrealMap != null) {
			changeMapNameBtn.setDisable(false);
			inputMapPathLbl.setText(unrealMap.getName());
			mapConverter.setInMap(unrealMap);
			mapConverter.initConvertedResourcesFolder();
			if (ue4RefPathLbl != null) {
				ue4RefPathLbl.setText(mapConverter.getUt4ReferenceBaseFolder());
			}

			if(changeUe4RefPathBtn != null){
				changeUe4RefPathBtn.setDisable(false);
			}
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

			if (!"U2".equals(inputGame.getShortName())) {
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
			alert.setContentText("Select your " + inputGame.getName() + " input map");

			alert.showAndWait();
			return false;
		}

		// FOR UT3 need to have .t3d file created from UT3 editor
		// because the ut3.com batchexport command is buggy and messes up actors !
		if (UTGame.UT3.shortName.equals(mapConverter.getInputGame().getShortName()) && mapConverter.getInT3d() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Input .t3d map not set");
			alert.setHeaderText("Input map not set");
			alert.setContentText("Within UT3 editor, select all actors, copy and paste in a new .t3d file");

			alert.showAndWait();
			return false;
		}

		// for Unreal 1, needs to have oldunreal.com installed
		if (UTGame.U1.shortName.equals(mapConverter.getInputGame().getShortName()) && !new File(inputGame.getPath() + File.separator + inputGame.getExportExecPath()).exists()) {
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
	private void selectUe4EditorBaseRefPath(UnrealGame unrealGame) {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select ue4 editor reference base path");

		final File ut4RootPath = unrealGame.getPath();

		if (ut4RootPath.exists()) {
			File ut4RootContentPath = new File(ut4RootPath.getAbsolutePath() + File.separator + "UnrealTournament" + File.separator + "Content");

			if (ue4RefPathLbl != null && ue4RefPathLbl.getText() != null && !ue4RefPathLbl.getText().trim().isEmpty()) {
				File ut4BaseRefPath = new File(ut4RootContentPath.getAbsolutePath() + File.separator + ue4RefPathLbl);

				if (ut4BaseRefPath.getParentFile().exists()) {
					chooser.setInitialDirectory(ut4BaseRefPath.getParentFile());
				}
			}

			if (chooser.getInitialDirectory() == null) {
				chooser.setInitialDirectory(ut4RootContentPath);
			}

			File ut4RefFolder = chooser.showDialog(new Stage());

			if (ut4RefFolder != null) {
				if (ut4RefFolder.getPath().startsWith(ut4RootContentPath.getAbsolutePath())) {
					String ut4BaseRef = "/Game" + ut4RefFolder.getAbsolutePath().substring(ut4RootContentPath.getAbsolutePath().length());
					ut4BaseRef = ut4BaseRef.replace("\\", "/");

					ue4RefPathLbl.setText(ut4BaseRef);
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
		chooser.setTitle("Select " + inputGame.getShortName() + " .t3d map you created from " + mapConverter.getInputGame().getShortName() + " editor ");

		File mapFolder = new File(inputGame.getPath() + File.separator + inputGame.getMapFolder());

		if (mapFolder.exists()) {
			chooser.setInitialDirectory(mapFolder);
		}

		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.getShortName() + " Editor Map (*.t3d)", "*.t3d"));

		File t3dUt3EditorFile = chooser.showOpenDialog(new Stage());

		// UT3 commandlet export too buggy and messed .t3d file so need to use the exported one from UT3 editor
		if (t3dUt3EditorFile != null) {
			mapConverter.setInT3d(t3dUt3EditorFile);
		}
	}

}
