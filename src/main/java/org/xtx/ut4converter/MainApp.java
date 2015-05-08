
package org.xtx.ut4converter;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.xtx.ut4converter.ui.ConversionViewController;
import org.xtx.ut4converter.ui.MainSceneController;

/**
 * 
 * @author XtremeXp
 */
public class MainApp extends Application {

    /**
     * Program Name
     */
    public static final String PROGRAM_NAME = "UT4 Converter";
    
    /**
     * Version of UT4 Converter
     */
    public static final String VERSION = "0.3-DEV";
    
    /**
     * Author
     */
    public static final String AUTHOR = "XtremeXp";
    
    Stage primaryStage;
    BorderPane rootLayout;

    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(PROGRAM_NAME+" - "+VERSION);

        // main scene with only menu
        initRootLayout();

        showWelcomeView();
    }
    
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/Scene.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            
            primaryStage.setScene(scene);
            primaryStage.show();
            MainSceneController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * Shows the welcome overview inside the root layout.
     */
    public void showWelcomeView() {
        showView("/fxml/WelcomeView.fxml");
    }
    
    /**
     * 
     * @param name fxml view file relative path
     * @return Controller of view
     */
    private Object showView(String name){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(name));
            Pane view = (Pane) loader.load();

            rootLayout.setCenter(view);
            
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Shows user settings panel
     */
    public void showUserSettingsView(){
        showView("/fxml/SettingsScene.fxml");
    }
    
    /**
     * Show conversion view (log table, ...)
     * @return Controller of view
     */
    public ConversionViewController showConversionView(){
        return (ConversionViewController) showView("/fxml/ConversionView.fxml");
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }
    
    
    
    

}
