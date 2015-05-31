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
import javafx.scene.control.TitledPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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

    
    private Stage dialogStage;
    @FXML
    private TitledPane gamePathsPane;
    @FXML
    private TitledPane uModelPane;
    @FXML
    private TextField uModelPath;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSettings();
        uModelPane.setText("UModel Settings");
        gamePathsPane.setText("Unreal games paths");
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
        setUTxFolder(UTGame.UT2003, ut2003Folder);
    }

    @FXML
    private void selectUt2004Folder(ActionEvent event) {
        setUTxFolder(UTGame.UT2004, ut2004Path);
    }

    @FXML
    private void selectUt3Folder(ActionEvent event) {
        setUTxFolder(UTGame.UT3, ut3Folder);
    }

    @FXML
    private void selectUt4EditorFolder(ActionEvent event) {
        setUTxFolder(UTGame.UT4, ut4EditorFolder);
    }

    @FXML
    private void selectU2Folder(ActionEvent event) {
        setUTxFolder(UTGame.U2, u2Path);
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
            
            if(userConfig.getUModel() != null){
                uModelPath.setText(userConfig.getUModel().getAbsolutePath());
            }
            
            for(UserGameConfig game : userConfig.getGame()){
                
                if(game.getPath() != null){
                    if(null != game.getId()) switch (game.getId()) {
                        case UT99:
                            ut99Path.setText(game.getPath().getAbsolutePath());
                            break;
                        case U1:
                            u1Path.setText(game.getPath().getAbsolutePath());
                            break;
                        case U2:
                            u2Path.setText(game.getPath().getAbsolutePath());
                            break;
                        case UT2003:
                            ut2003Folder.setText(game.getPath().getAbsolutePath());
                            break;
                        case UT2004:
                            ut2004Path.setText(game.getPath().getAbsolutePath());
                            break;
                        case UT3:
                            ut3Folder.setText(game.getPath().getAbsolutePath());
                            break;
                        case UT4:
                            ut4EditorFolder.setText(game.getPath().getAbsolutePath());
                            break;
                        default:
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

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void closeDialog(ActionEvent event) {
        this.dialogStage.close();
    }

    @FXML
    private void uModelPath(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select umodel.exe file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("umodel.exe", "*.exe"));
        
        File umodelPath = chooser.showOpenDialog(new Stage());
        
        if(umodelPath != null){
            try {
                userConfig.setUModel(umodelPath);
                userConfig.saveFile();
                uModelPath.setText(umodelPath.getAbsolutePath());
            } catch (JAXBException ex) {
                Logger.getLogger(SettingsSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
