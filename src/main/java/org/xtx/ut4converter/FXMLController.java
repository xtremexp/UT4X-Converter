/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBException;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.tools.Installation;

/**
 * FXML Controller class
 * @author XtremeXp
 */
public class FXMLController implements Initializable {
    
    final String URL_UTCONV_FORUM = "http://utforums.epicgames.com/showthread.php?p=25131566";
    
    @FXML
    private Menu menuConvert;
    @FXML
    private MenuItem menuItemUT99Map;
    @FXML
    private MenuItem menuExit;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private Menu menuOptions;
    @FXML
    private MenuItem menuSettings;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Pane paneSettings;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML
    private void handleConvert(ActionEvent event) {
        // TODo refactor that code ...
        
        UserConfig uc = null;
        try {
            
            uc = UserConfig.load();
        } catch (JAXBException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       FileChooser chooser = new FileChooser();
        chooser.setTitle("Select UT99 t3d map");
        
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UT99 Text Map (*.t3d)", "*.t3d"));
        
        UserGameConfig ugc;
        
        if(uc != null){
            ugc = uc.getGameConfigByGame(UTGames.UTGame.UT99);
            
            if(ugc != null && ugc.getLastConverted() != null){
                chooser.setInitialDirectory(ugc.getLastConverted().getParentFile());
            } else {
                ugc = new UserGameConfig();
                ugc.setId(UTGames.UTGame.UT99);
            }
        } else {
            uc = new UserConfig();
            ugc = new UserGameConfig();
            ugc.setId(UTGames.UTGame.UT99);
        }

        // disabled until the "Unr" to "t3d" Ucc Exporter is finalized
        //chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UT99 Map (*.unr)", "*.unr"));
        
        File t3dFile = chooser.showOpenDialog(new Stage());
        
        if(t3dFile != null){
            try {
                MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, t3dFile, 2.2d);
                mc.convertTo(Installation.getProgramFolder().getAbsolutePath() + File.separator + "Converted");
                
                ugc.setLastConverted(t3dFile);
                uc.saveFile();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Map Converted!");
                alert.setHeaderText("Operation successful.");
                alert.setContentText("Map was succesfully converted to "+mc.getOutT3d().getAbsolutePath());
                
                alert.showAndWait();
            } catch (Exception ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    
    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About UT4 Converter");
        alert.setHeaderText("Version: "+MainApp.VERSION);
        alert.setContentText("Using Java 1.8_40 version");

        alert.showAndWait();
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        paneSettings.setVisible(true);
        welcomeLabel.setVisible(false);
    }

    @FXML
    private void closeSettings(ActionEvent event) {
        paneSettings.setVisible(false);
        welcomeLabel.setVisible(true);
    }
    
    /**
     * Opens url in web browser
     * @param uri 
     */
    private void open(URI uri) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException e) {}
        } else {
        }
    }
}
