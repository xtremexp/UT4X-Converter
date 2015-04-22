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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBException;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.t3d.T3DRessource;
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
    @FXML
    private MenuItem menuCheckForUpdates;
    @FXML
    private MenuItem menuCheckoutSourceCode;
    @FXML
    private Pane conversion;
    @FXML
    private TableView<TableRowLog> convLogTableView;

    @FXML
    private TableColumn<TableRowLog, String> logTime;
    @FXML
    private TableColumn<TableRowLog, String> logLevel;
    @FXML
    private TableColumn<TableRowLog, String> logMsg;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        logTime.setCellValueFactory(new PropertyValueFactory<TableRowLog, String>("time"));
        logLevel.setCellValueFactory(new PropertyValueFactory<TableRowLog, String>("level"));
        logMsg.setCellValueFactory(new PropertyValueFactory<TableRowLog, String>("message"));
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
        // TODo refactor that code ...
        
        UserConfig uc = null;
        try {
            
            uc = UserConfig.load();
        } catch (JAXBException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       FileChooser chooser = new FileChooser();
        chooser.setTitle("Select UT99 t3d map");
        
        
        UserGameConfig ugc;
        boolean needSetGamePath = true;
        
        if(uc != null){
            ugc = uc.getGameConfigByGame(UTGames.UTGame.UT99);
            
            if(ugc != null && ugc.getPath() != null){
                chooser.setInitialDirectory(new File(ugc.getPath().getAbsolutePath() + File.separator + "Maps"));
                needSetGamePath = false;
            } else {
                // TODO redirect to settings panel so user can set game path?
                ugc = new UserGameConfig();
                ugc.setId(UTGames.UTGame.UT99);

            }
        } else {
            uc = new UserConfig();
            ugc = new UserGameConfig();
            ugc.setId(UTGames.UTGame.UT99);
        }

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(UTGame.UT99.shortName+" Map (*."+UTGame.UT99.mapExtension+")", "*."+UTGame.UT99.mapExtension));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(UTGame.UT99.shortName+" Text Map (*.t3d)", "*.t3d"));
        
        File unrealMap = chooser.showOpenDialog(new Stage());
        
        if(unrealMap != null){
            try {
                // stops if selected file if .unr map file and user did not set game path in settings
                if(unrealMap.getName().endsWith(".unr") && needSetGamePath){
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Need to set game path");
                    alert.setHeaderText("UT Game path not set");
                    alert.setContentText("You need to set "+UTGames.UTGame.UT99+" game path to convert from .unr binary map file\n You may also convert without game path set from .t3d unreal text map.");

                    alert.showAndWait();
                    
                    paneSettings.setVisible(true);
                    welcomeLabel.setVisible(false);
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
                Double scaleFactor = null;
                
                if (result.isPresent()){
                    scaleFactor = Double.valueOf(result.get());
                } else {
                    return;
                }
                
                MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, unrealMap, scaleFactor);
                mc.setMainSceneController(this);
                // TODO make getter in mc to know where to convert stuff!
                mc.convertTo(Installation.getProgramFolder().getAbsolutePath() + File.separator + "Converted" + File.separator + unrealMap.getName().split("\\.")[0] + File.separator + T3DRessource.Type.LEVEL.name());
                
                String msg = "Map was succesfully converted to "+mc.getOutT3d().getAbsolutePath();
                mc.getLogger().info(msg);

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Map Converted");
                alert.setContentText(msg + ".\n Do you want to open folder with converted stuff?");

                Optional<ButtonType> result2 = alert.showAndWait();

                if (result2.get() != ButtonType.OK){
                    return;
                }
                
                UIUtils.openExplorer(mc.getOutT3d().getParentFile().getParentFile());
            } catch (Exception ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        paneSettings.setVisible(true);
        welcomeLabel.setVisible(false);
    }

    /**
     * Close settings panel and go pack to main page
     * @param event 
     */
    @FXML
    private void closeSettings(ActionEvent event) {
        paneSettings.setVisible(false);
        welcomeLabel.setVisible(true);
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

    public TableView<TableRowLog> getConvLogTableView() {
        return convLogTableView;
    }
    
    
}
