/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;

/**
 * FXML Controller class
 *
 * @author XtremeXp
 */
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
    
    BigDecimal scaleFactor = new BigDecimal("2.2");
    @FXML
    private ComboBox<String> scaleFactorList;
    @FXML
    private ComboBox<String> lightningBrightnessFactor;
    @FXML
    private ComboBox<String> soundVolumeFactor;
    @FXML
    private Label outMapNameLbl;
    
    /**
     * Initializes the controller class.
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
        
        if(userConfig != null){
            userInputGameConfig = userConfig.getGameConfigByGame(inputGame);
            userOutputGameConfig = userConfig.getGameConfigByGame(outputGame);
        }
        
        mapConverter = new MapConverter(inputGame, outputGame);
        
        disableConversionType();
        
        initConvCheckBoxes();
    }
    
    private void initConvCheckBoxes(){
        convSndCheckBox.setSelected(mapConverter.convertSounds());
        convTexCheckBox.setSelected(mapConverter.convertTextures());
        convMusicCheckBox.setSelected(mapConverter.convertMusic());
        convSmCheckBox.setSelected(mapConverter.convertStaticMeshes());
    }
    
    /**
     * Temp hack
     * Disable conversion of some type of ressources depending on game
     * because all ressource converter not done yet
     * TODO implement if conversion is possible for some specific type of ressource
     */
    private void disableConversionType(){
        
        // SM converter not yet operational
        mapConverter.setConvertStaticMeshes(false);
        convSmCheckBox.setDisable(true);
        
        // need add interface with umodel or my custom tex extractor
        // until we can re-enable conversion of some ressources
        if(inputGame == UTGames.UTGame.UT3){
            mapConverter.noConvertRessources();
            
            convSndCheckBox.setDisable(true);
            convTexCheckBox.setDisable(true);
            convMusicCheckBox.setDisable(true);
            convSmCheckBox.setDisable(true);
        }
        else if(inputGame == UTGames.UTGame.U2){
            mapConverter.setConvertTextures(false);
            convTexCheckBox.setDisable(true);
        }
    }

    @FXML
    private void selectInputMap(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select "+inputGame.shortName+" map");
        
        File mapFolder = UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame);
        
        if(mapFolder.exists()){
            chooser.setInitialDirectory(UTGames.getMapsFolder(userInputGameConfig.getPath(), inputGame));
        }
        
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.shortName+" Map (*."+inputGame.mapExtension+", *.t3d)", "*."+inputGame.mapExtension, "*.t3d"));
        File unrealMap = chooser.showOpenDialog(new Stage());
        
        if(unrealMap != null){
            inputMapLbl.setText(unrealMap.getName());
            mapConverter.setInMap(unrealMap);
            outputFolderLbl.setText(mapConverter.getOutPath().toString());
            outMapNameLbl.setText(mapConverter.getOutMapName());
        }
    }
    
    
    @FXML
    private void convert(ActionEvent event) {
        
        if(checkConversionSettings()){
            
            dialogStage.close();
            
            mapConverter.setScale(Double.valueOf(scaleFactorList.getSelectionModel().getSelectedItem()));
            mapConverter.brightnessFactor = Float.valueOf(lightningBrightnessFactor.getSelectionModel().getSelectedItem());
            mapConverter.soundVolumeFactor = Float.valueOf(soundVolumeFactor.getSelectionModel().getSelectedItem());
            
            mapConverter.setConversionViewController(mainApp.showConversionView());

            SwingUtilities.invokeLater(mapConverter);
        }
    }
    
    /**
     * All settings good
     * @return 
     */
    private boolean checkConversionSettings(){
        
        if(mapConverter.getInMap() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input map not set");
            alert.setHeaderText("Input map not set");
            alert.setContentText("Select your "+inputGame.name + " input map");

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
    
    
}
