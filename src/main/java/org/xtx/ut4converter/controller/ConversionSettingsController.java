
/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.controller;


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
import org.xtx.ut4converter.ConversionSettings.ExportOption;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.ApplicationConfig;
import org.xtx.ut4converter.config.ConversionSettings;
import org.xtx.ut4converter.config.GameConversionConfig;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.tools.Installation;
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

import static org.xtx.ut4converter.tools.UIUtils.createLabelWithTooltip;

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

	private org.xtx.ut4converter.ConversionSettings conversionSettings;

	@FXML
	private CheckBox convTexCheckBox;
	@FXML
	private CheckBox convSndCheckBox;
	@FXML
	private CheckBox convSmCheckBox;
	@FXML
	private CheckBox convMusicCheckBox;

	@FXML
	private ComboBox<Integer> lightMapResolutionList;
	@FXML
	private ComboBox<Double> scaleFactorList;
	@FXML
	private ComboBox<Float> lightningRadiusFactor;
	@FXML
	private ComboBox<Float> soundVolumeFactor;
	@FXML
	private Label outMapNameLbl;

	@FXML
	private CheckBox debugLogLevelCheckBox;

	@FXML
	private TextField classesNameFilter;

	@FXML
	private GridPane gridPaneMainSettings;

	private Label inputMapPathLbl;

	private Label ue4RefPathLbl;

	private Button changeMapNameBtn;

	private Button changeUe4RefPathBtn;

	/**
	 * Export option combo box
	 */
	final ComboBox<ExportOption> exportOptComboBox = new ComboBox<>();

	ApplicationConfig applicationConfig;

	/**
	 * Initializes the controller class.
	 *
	 * @param url Url
	 * @param rb Resource bundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		try {
			this.applicationConfig = ApplicationConfig.loadApplicationConfig();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// populate combobox list settings from config
		scaleFactorList = new ComboBox<>();
		scaleFactorList.setEditable(true);
		scaleFactorList.getItems().addAll(this.applicationConfig.getConversionSettingsPanelConfig().getScaleFactorList());
		scaleFactorList.getItems().sort(Comparator.naturalOrder());
		scaleFactorList.getSelectionModel().select(this.applicationConfig.getConversionSettingsPanelConfig().getDefaultScaleFactor());

		lightningRadiusFactor.getItems().addAll(this.applicationConfig.getConversionSettingsPanelConfig().getLightRadiusFactorList());
		lightningRadiusFactor.getItems().sort(Comparator.naturalOrder());
		lightningRadiusFactor.getSelectionModel().select(this.applicationConfig.getConversionSettingsPanelConfig().getDefaultLightRadiusFactor());

		lightMapResolutionList.getItems().addAll(this.applicationConfig.getConversionSettingsPanelConfig().getLightMapResolutionList());
		lightMapResolutionList.getItems().sort(Comparator.naturalOrder());
		lightMapResolutionList.getSelectionModel().select(this.applicationConfig.getConversionSettingsPanelConfig().getDefaultLightMapResolution());

		soundVolumeFactor.getItems().addAll(this.applicationConfig.getConversionSettingsPanelConfig().getSoundVolumeFactorList());
		soundVolumeFactor.getItems().sort(Comparator.naturalOrder());
		soundVolumeFactor.getSelectionModel().select(this.applicationConfig.getConversionSettingsPanelConfig().getDefaultSoundVolumeFactor());

		exportOptComboBox.getItems().add(ExportOption.BY_TYPE);
		exportOptComboBox.getItems().add(ExportOption.BY_PACKAGE);
		exportOptComboBox.getSelectionModel().select(this.applicationConfig.getConversionSettingsPanelConfig().getDefaultExport());
		exportOptComboBox.setConverter(new StringConverter<>() {
			@Override
			public String toString(ExportOption exportOption) {
				return exportOption.getLabel();
			}

			@Override
			public ExportOption fromString(String s) {
				for (ExportOption eop : ExportOption.values()) {
					if (eop.getLabel().equals(s)) {
						return eop;
					}
				}
				return null;
			}
		});

	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}



	/**
	 * Init from menu File-> "Recent conversions"
	 *
	 * @param conversionSettings Conversion settings
	 */
	public void initFromConversionSettings(final ConversionSettings conversionSettings) {

		final UnrealGame inputGame = this.applicationConfig.getUnrealGameById(conversionSettings.getInputGameId());
		final UnrealGame outputGame = this.applicationConfig.getUnrealGameById(conversionSettings.getOutputGameId());

		initFromInputAndOutputGame(inputGame, outputGame);
		setInputMap(conversionSettings.getInputMap());
		setNewMapName(conversionSettings.getOutputMapName());

		if (outputGame.getUeVersion() >= 4) {
			setUt4RefFolder(conversionSettings.getUe4RefBaseFolder());
		}

		if (!scaleFactorList.getItems().contains(conversionSettings.getScaleFactor())) {
			scaleFactorList.getItems().add(conversionSettings.getScaleFactor());
		}

		scaleFactorList.getSelectionModel().select(conversionSettings.getScaleFactor());
		exportOptComboBox.getSelectionModel().select(ExportOption.valueOf(conversionSettings.getExportOption()));
	}

	/**
	 * Init from File->[Game]-> Convert to [Game] Menu and "File-> "Recent conversions""
	 *
	 * @param inputGame  Input Unreal game
	 * @param outputGame Output Unreal game
	 */
	public void initFromInputAndOutputGame(final UnrealGame inputGame, final UnrealGame outputGame) {

		this.conversionSettings = new org.xtx.ut4converter.ConversionSettings(inputGame, outputGame);

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


		// set default scale depending on config
		final GameConversionConfig inGame = inputGame.getConvertsTo().stream().filter(g -> g.getGameId().equals(outputGame.getShortName())).findFirst().orElse(null);

		if (inGame != null && inGame.getScale() != null) {
			if (!scaleFactorList.getItems().contains(inGame.getScale())) {
				scaleFactorList.getItems().add(inGame.getScale());
			}
			scaleFactorList.getSelectionModel().select(inGame.getScale());
		}

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

		// Added export options
		// 14012023 - disabled for input UE2+ games since staticmeshes don't have right texture path for now
		if (outputGame.getUeVersion() >= 4 && inputGame.getUeVersion() == 1) {
			gridPaneMainSettings.add(createLabelWithTooltip("Export structure: ", "How ressources will be split into folder.\nE.g:\nBy type: /CTF-Face/Textures/GenEarth_Rock_Rock9.tga\nBy package: /CTF-Face/GenEarth/Rock_Rock9.tga"), 0, rowIdx);
			gridPaneMainSettings.add(exportOptComboBox, 1, rowIdx++);
		}

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

		this.conversionSettings = new org.xtx.ut4converter.ConversionSettings(inputGame, outputGame);

		// games we are working on and testing and adding blueprints
		if("U1".equals(inputGame.getShortName()) || "U2".equals(inputGame.getShortName())) {
			this.conversionSettings.setUseU1BPActorClasses(mainApp.isUseUbClasses());
		} else {
			this.conversionSettings.setUseU1BPActorClasses(false);
		}
	}

	/**
	 * Allow changing the default ut4 map name suggested by ut4 converter
	 */
	@FXML
	private void changeMapName() {

		TextInputDialog dialog = new TextInputDialog(this.conversionSettings.getOutputMapName());

		dialog.setTitle("Text Input Dialog");
		dialog.setHeaderText("Map Name Change");
		dialog.setContentText("Enter UT4 map name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			String newMapName = result.get();
			setNewMapName(newMapName);
		}

	}

	private void setNewMapName(String newMapName) {
		newMapName = T3DUtils.filterName(newMapName);

		if (newMapName.length() > 3) {
			this.conversionSettings.setOutputMapName(newMapName);
			outMapNameLbl.setText(this.conversionSettings.getOutputMapName());
			this.conversionSettings.refreshOutputMapNameAndUT4RefBaseFolder();

			if (ue4RefPathLbl != null) {
				ue4RefPathLbl.setText(this.conversionSettings.getUt4ReferenceBaseFolder());
			}
		}
	}

	@FXML
	private void selectInputMap()  {

		final UnrealGame inputGame = this.conversionSettings.getInputGame();
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select " + this.conversionSettings.getInputGame().getName() + " map");

		File mapFolder = new File(inputGame.getPath() + File.separator + inputGame.getMapFolder());

		if (mapFolder.exists()) {
			chooser.setInitialDirectory(mapFolder);
		}

		if (Installation.isLinux()) {
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.getName() + " Map (*.t3d)", "*.t3d"));
		} else {
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.getName() + " Map (*." + inputGame.getMapExt() + ", *.t3d)", "*." + inputGame.getMapExt(), "*.t3d"));
		}

		File unrealMap = chooser.showOpenDialog(new Stage());

		if (unrealMap != null) {
			setInputMap(unrealMap);
		}
	}

	private void setInputMap(File unrealMap) {

		changeMapNameBtn.setDisable(false);
		inputMapPathLbl.setText(unrealMap.getPath());
		this.conversionSettings.setInputMapFile(unrealMap);
		this.conversionSettings.refreshOutputMapNameAndUT4RefBaseFolder();

		if (ue4RefPathLbl != null) {
			ue4RefPathLbl.setText(this.conversionSettings.getUt4ReferenceBaseFolder());
		}

		if (changeUe4RefPathBtn != null) {
			changeUe4RefPathBtn.setDisable(false);
		}

		outputFolderLbl.setText(this.conversionSettings.getOutputFolder().toString());
		outMapNameLbl.setText(this.conversionSettings.getOutputMapName());
	}

	@FXML
	private void convert() throws IOException {

		if (checkConversionSettings()) {

			try {
				this.conversionSettings.setScaleFactor(scaleFactorList.getSelectionModel().getSelectedItem());
				this.conversionSettings.setExportOption(exportOptComboBox.getSelectionModel().getSelectedItem());
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


			this.conversionSettings.setLightMapResolution(lightMapResolutionList.getSelectionModel().getSelectedItem());
			this.conversionSettings.setLightRadiusFactor(lightningRadiusFactor.getSelectionModel().getSelectedItem());
			this.conversionSettings.setSoundVolumeFactor(soundVolumeFactor.getSelectionModel().getSelectedItem());
			this.conversionSettings.setLogLevel(debugLogLevelCheckBox.isSelected() ? Level.FINE : Level.INFO);
			if (classesNameFilter.getLength() > 1) {
				this.conversionSettings.setFilteredClasses(classesNameFilter.getText().trim().split(";"));
			}


			final ConversionSettings conversionSettings = new ConversionSettings();
			conversionSettings.setInputGameId(this.conversionSettings.getInputGame().getShortName());
			conversionSettings.setOutputGameId(this.conversionSettings.getOutputGame().getShortName());
			conversionSettings.setExportOption(this.conversionSettings.getExportOption().name());
			conversionSettings.setUe4RefBaseFolder(this.conversionSettings.getUt4ReferenceBaseFolder());
			conversionSettings.setScaleFactor(this.conversionSettings.getScaleFactor());
			conversionSettings.setOutputMapName(this.conversionSettings.getOutputMapName());
			conversionSettings.setInputMap(this.conversionSettings.getInputMapFile());

			final ApplicationConfig appConfig = ApplicationConfig.loadApplicationConfig();
			appConfig.addRecentConversion(conversionSettings);
			appConfig.saveFile();

			// refresh main menu and add recent conversation
			// not working yet, controller returned is null
			/*
			MainSceneController sc = (MainSceneController) this.mainApp.getPrimaryStage().getUserData();
			sc.addRecentConversationsMenu(appConfig);
			*/

			final MapConverter mapConverter = new MapConverter(this.conversionSettings);
			mapConverter.setConversionViewController(mainApp.showConversionView());
			mapConverter.getLogger().setLevel(debugLogLevelCheckBox.isSelected() ? Level.FINE : Level.INFO);
			SwingUtilities.invokeLater(mapConverter);
		}
	}

	/**
	 * All settings good
	 *
	 * @return <code>true</code> if conversion settings are fine else <code>false</code>
	 */
	private boolean checkConversionSettings() {

		final UnrealGame inputGame = this.conversionSettings.getInputGame();

		if (this.conversionSettings.getInputMapFile() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Input map not set");
			alert.setHeaderText("Input map not set");
			alert.setContentText("Select your " + inputGame.getName() + " input map");

			alert.showAndWait();
			return false;
		}

		// FOR UT3 need to have .t3d file created from UT3 editor
		// because the ut3.com batchexport command is buggy and messes up actors !
		if (UTGame.UT3.shortName.equals(this.conversionSettings.getInputGame().getShortName()) && this.conversionSettings.getInputT3DMapFile() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Input .t3d map not set");
			alert.setHeaderText("Input map not set");
			alert.setContentText("Within UT3 editor, select all actors, copy and paste in a new .t3d file");

			alert.showAndWait();
			return false;
		}

		// for Unreal 1, needs to have oldunreal.com installed
		if (UTGame.U1.shortName.equals(this.conversionSettings.getInputGame().getShortName()) && !new File(inputGame.getPath() + File.separator + inputGame.getPkgExtractorPath()).exists()) {
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

					setUt4RefFolder(ut4BaseRef);
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

	private void setUt4RefFolder(String ut4BaseRef) {
		ue4RefPathLbl.setText(ut4BaseRef);
		this.conversionSettings.setUt4ReferenceBaseFolder(ut4BaseRef);
	}

	@FXML
	private void close() {
		dialogStage.close();
	}

	@FXML
	private void toggleTexConversion() {
		this.conversionSettings.setConvertTextures(convTexCheckBox.isSelected());
	}

	@FXML
	private void toggleSndConversion() {
		this.conversionSettings.setConvertSounds(convSndCheckBox.isSelected());
	}

	@FXML
	private void toggleSmConversion() {
		this.conversionSettings.setConvertStaticMeshes(convSmCheckBox.isSelected());
	}

	@FXML
	private void toggleMusicConversion() {
		this.conversionSettings.setConvertMusic(convMusicCheckBox.isSelected());
	}


	/**
	 * // FOR UT3 need to have the copied/pasted .td3 level from UT3 editor to have right order of brushes
	 * // because the UT3 commandlet is kinda in "alpha" stages
	 */
	public void selectInputT3dMap() {

		final UnrealGame inputGame = this.conversionSettings.getInputGame();
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select " + inputGame.getShortName() + " .t3d map you created from " + this.conversionSettings.getInputGame().getShortName() + " editor ");

		File mapFolder = new File(inputGame.getPath() + File.separator + inputGame.getMapFolder());

		if (mapFolder.exists()) {
			chooser.setInitialDirectory(mapFolder);
		}

		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.getShortName() + " Editor Map (*.t3d)", "*.t3d"));

		File t3dUt3EditorFile = chooser.showOpenDialog(new Stage());

		// UT3 commandlet export too buggy and messed .t3d file so need to use the exported one from UT3 editor
		if (t3dUt3EditorFile != null) {
			inputMapT3dLbl.setText(t3dUt3EditorFile.getPath());
			this.conversionSettings.setInputT3DMapFile(t3dUt3EditorFile);
		}
	}

}
