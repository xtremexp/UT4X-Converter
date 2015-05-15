/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBException;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.tools.UIUtils;

/**
 * FXML Controller class
 * TODO i18n
 * @author XtremeXp
 */
public class MainSceneController implements Initializable {
    
    /**
     * Link to UT3 Converter topic 
     * Until we create topic for UT4 converter
     */
    final String URL_UTCONV_FORUM = "http://utforums.epicgames.com/showthread.php?p=25131566";
    
    /**
     * Url to git hub for source code
     */
    final String URL_UTCONV_GITHUB = "https://github.com/xtremexp/UT4Converter";
    
    @FXML
    private MenuItem menuExit;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private Menu menuOptions;
    @FXML
    private MenuItem menuSettings;

    @FXML
    private MenuItem menuCheckForUpdates;
    @FXML
    private MenuItem menuCheckoutSourceCode;

    
    public MainApp mainApp;
    public Stage mainStage;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.mainStage = mainApp.getPrimaryStage();
    }
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }   

    /**
     * Exit program
     * @param event 
     */
    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
    
    /**
     * Opens file browser for UT99 .t3d map,
     * then convert it.
     * @param event 
     */
    @FXML
    private void handleConvert(ActionEvent event) {
        convertUtxMap(UTGame.UT99);
    }


    
    /**
     * Show credits about program
     * TODO history, library used, licence
     * @param event 
     */
    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("About "+MainApp.PROGRAM_NAME+": ");
        alert.setContentText("Version: "+MainApp.VERSION+"\nAuthor: "+MainApp.AUTHOR+"\nPowered by Java "+System.getProperty("java.version"));

        alert.showAndWait();
    }

    /**
     * Display Settings panel
     * @param event 
     */
    @FXML
    private void handleSettings(ActionEvent event) {
        mainApp.showUserSettingsView();
    }


    
    /**
     * Opens url in web browser
     * @param if <code>true</code> then display a confirmation dialog before opening directly web browser.
     * @param url Url to open with web browser 
     */
    private void openUrl(String url, boolean confirmBeforeOpen) {
        
        if(url == null){
            return;
        }
        
        if(confirmBeforeOpen){
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Web browser access");
            alert.setContentText("Do you want to open web browser to this url ?\n"+url);

            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.get() != ButtonType.OK){
                return;
            }
        }
        
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                
                desktop.browse(new URI(url));
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Impossible to open web browser");
            alert.setContentText("Your system is not or does not support desktop. \n Manually go to:"+url);

            alert.showAndWait();
        }
    }

    @FXML
    private void openUtTopicUrl(ActionEvent event) {
        openUrl(URL_UTCONV_FORUM, true);
    }

    @FXML
    private void openGitHubUrl(ActionEvent event) {
        openUrl(URL_UTCONV_GITHUB, true);
    }

    @FXML
    private void handleConvertU1Map(ActionEvent event) {
        convertUtxMap(UTGame.U1);
    }
    
    /**
     * Display file chooser for map to convert
     * and launch the conversion
     * TODO refactor/cleanup
     * @param inputGame UT game
     */
    private void convertUtxMap(UTGame inputGame){
        
        UserConfig uc = null;
        try {
            
            uc = UserConfig.load();
        } catch (JAXBException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       FileChooser chooser = new FileChooser();
        chooser.setTitle("Select "+inputGame.shortName+" t3d map");
        
        
        UserGameConfig ugc;
        UserGameConfig ugcUt4;
        boolean needSetGamePath = true;
        
        if(uc != null){
            ugc = uc.getGameConfigByGame(inputGame);
            ugcUt4 = uc.getGameConfigByGame(UTGame.UT4);
            
            if(ugcUt4 == null || ugcUt4.getPath() == null || !ugcUt4.getPath().exists()){
                
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Need to set UT4 editor folder");
                alert.setHeaderText("Path not set");
                alert.setContentText("You need to set "+UTGame.UT4.shortName+" editor path (go to \"Options\" -> \"Settings\" in menu, set UT4 Editor path and try again)");

                alert.showAndWait();

                return;
            }
            
            if(ugc != null && ugc.getPath() != null){
                chooser.setInitialDirectory(UTGames.getMapsFolder(ugc.getPath(), inputGame));
                needSetGamePath = false;
            } else {
                // TODO redirect to settings panel so user can set game path?
                ugc = new UserGameConfig();
                ugc.setId(inputGame);

            }
        } else {
            uc = new UserConfig();
            ugc = new UserGameConfig();
            ugc.setId(inputGame);
        }

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(inputGame.shortName+" Map (*."+inputGame.mapExtension+", *.t3d)", "*."+inputGame.mapExtension, "*.t3d"));
        
        File unrealMap = chooser.showOpenDialog(new Stage());
        MapConverter mapConverter = null;
        
        if(unrealMap != null){
            try {
                // stops if selected file if .unr map file and user did not set game path in settings
                if(unrealMap.getName().endsWith(".unr") && needSetGamePath){
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Need to set game path");
                    alert.setHeaderText("UT Game path not set");
                    alert.setContentText("You need to set "+inputGame.shortName+" game path to convert from .unr binary map file\n You may also convert without game path set from .t3d unreal text map.");

                    alert.showAndWait();
                    
                    return;
                }
                
                ugc.setLastConverted(unrealMap);
                uc.saveFile();
                
                List<String> choices = new ArrayList<>();
                
                choices.add("1");
                choices.add("1.5");
                choices.add("2");
                choices.add("2.1");
                choices.add("2.2");
                choices.add("2.3");
                choices.add("2.4");
                choices.add("2.5");
                choices.add("3");

                ChoiceDialog<String> dialog = new ChoiceDialog<>("2.2", choices);
                dialog.setTitle("Which map scale factor you want to use?");
                dialog.setHeaderText("Set map scale factor");
                dialog.setContentText("Choose the map scale factor (2.2 is default):");

                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                Double scaleFactor;
                
                if (result.isPresent()){
                    scaleFactor = Double.valueOf(result.get());
                } else {
                    return;
                }
                
                mapConverter = new MapConverter(inputGame, UTGames.UTGame.UT4, unrealMap, scaleFactor);
                
                // UT3 in testing, no convert/export textures, ....
                if(inputGame == UTGames.UTGame.UT3){
                    mapConverter.noConvertRessources();
                }

                mapConverter.setConversionViewController(mainApp.showConversionView());
                // TODO make getter in mc to know where to convert stuff!
                mapConverter.convertTo(Installation.getProgramFolder().getAbsolutePath() + File.separator + "Converted" + File.separator + unrealMap.getName().split("\\.")[0] + File.separator + Type.LEVEL.name());
                
                String msg = "Map was succesfully converted to "+mapConverter.getOutT3d().getAbsolutePath();
                mapConverter.getLogger().info(msg);

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Map Converted");
                alert.setContentText(msg + ".\n Do you want to open folder with converted stuff?");

                Optional<ButtonType> result2 = alert.showAndWait();

                if (result2.get() != ButtonType.OK){
                    return;
                }
                
                UIUtils.openExplorer(mapConverter.getOutT3d().getParentFile().getParentFile());
            } catch (Exception ex) {
                
                if( mapConverter != null ){
                    mapConverter.getLogger().log(Level.SEVERE, null, ex);
                } 
                
                else {
                    Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }

    @FXML
    private void convertUt2004Map(ActionEvent event) {
        convertUtxMap(UTGame.UT2004);
    }

    @FXML
    private void convertUt3Map(ActionEvent event) {
        convertUtxMap(UTGame.UT3);
    }

    @FXML
    private void convertUt2003Map(ActionEvent event) {
        convertUtxMap(UTGame.UT2003);
    }

    @FXML
    private void convertU2Map(ActionEvent event) {
        convertUtxMap(UTGame.U2);
    }

}
