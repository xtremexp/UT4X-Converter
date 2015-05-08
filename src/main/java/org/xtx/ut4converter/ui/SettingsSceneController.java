/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBException;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.config.UserConfig;

/**
 * FXML Controller class
 * @author XtremeXp
 */
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
    private TextField ut4EditorFolder;
    @FXML
    private TextField u2Path;
    
    /**
     * Current user configuration
     */
    UserConfig userConfig;
    @FXML
    private Label settingsLog;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSettings();
    }    

    @FXML
    private void selectU1Folder(ActionEvent event) {
        setUTxFolder(UTGame.U1, u1Path);
    }
    
    @FXML
    private void selectUt99Folder(ActionEvent event) {
        setUTxFolder(UTGame.UT99, ut99Path);
    }

    @FXML
    private void selectUt2003Folder(ActionEvent event) {
    }

    @FXML
    private void selectUt2004Folder(ActionEvent event) {
    }

    @FXML
    private void selectUt3Folder(ActionEvent event) {
    }

    @FXML
    private void selectUt4EditorFolder(ActionEvent event) {
        setUTxFolder(UTGame.UT4, ut4EditorFolder);
    }

    @FXML
    private void selectU2Folder(ActionEvent event) {
    }



    
    /**
     * Saves game path to UserConfig object
     * @param textFile
     * @param utGame 
     */
    private void saveGamePath(TextField textFile, UTGames.UTGame utGame){
        
        UserGameConfig gc = userConfig.getGameConfigByGame(utGame);
        File gameFolder = new File(textFile.getText());
        
        if(gameFolder.exists()){
            if(gc == null){
                userConfig.getGame().add(new UserGameConfig(utGame, gameFolder));
            } else {
                gc.setPath(new File(textFile.getText()));
            }
            
            try {
                userConfig.saveFile();
                settingsLog.setText(utGame.name+" folder was successfully saved to "+UserConfig.getUserConfigFile().getAbsolutePath());
            } catch (JAXBException ex) {
                Logger.getLogger(SettingsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                settingsLog.setText("An error occured while saving "+UserConfig.USER_CONFIG_XML_FILE+" : "+ex.getMessage());
            }
        } else {
            showErrorMessage(textFile.getText()+" is not valid folder");
        }
    }
    
    private void showErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText("An error occured: "+message);
        alert.showAndWait();
    }

    /**
     * Load current user settings from XML file
     * and display game paths if available.
     */
    private void loadSettings() {
        try {
            userConfig = UserConfig.load();
            
            for(UserGameConfig game : userConfig.getGame()){
                
                if(game.getPath() != null){
                    if(game.getId() == UTGames.UTGame.UT99){
                        ut99Path.setText(game.getPath().getAbsolutePath());
                    } 
                    
                    else if(game.getId() == UTGames.UTGame.UT4){
                        ut4EditorFolder.setText(game.getPath().getAbsolutePath());
                    } 
                    
                    else if(game.getId() == UTGames.UTGame.U1){
                        u1Path.setText(game.getPath().getAbsolutePath());
                    }
                }
            }
            
        } catch (JAXBException ex) {
            Logger.getLogger(SettingsSceneController.class.getName()).log(Level.SEVERE, null, ex);
            showErrorMessage("An error occured while loading UserConfig file.");
        }
    }

    /**
     * Sets and save ut path to xml user config file 
     * on click "Select"
     * @param utGame UT game to set path
     * @param utPathTxtField Textfield for path game display in settings
     */
    private void setUTxFolder(UTGame utGame, TextField utPathTxtField){
        
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select "+utGame.name+" folder");
        
        if(utPathTxtField != null && utPathTxtField.getText() != null && new File(utPathTxtField.getText()).exists()){
            chooser.setInitialDirectory(new File(utPathTxtField.getText()));
        }
        
        if(utGame == UTGame.UT4){
            chooser.setTitle("Select "+utGame.name+" editor folder");
        }

        File utxFolder = chooser.showDialog(new Stage());
        
        if(utxFolder != null){
            utPathTxtField.setText(utxFolder.getAbsolutePath());
            saveGamePath(utPathTxtField, utGame);
        }
    }
    
}
