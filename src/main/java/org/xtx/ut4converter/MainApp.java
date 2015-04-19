
package org.xtx.ut4converter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle(PROGRAM_NAME+" - "+VERSION);
        stage.setScene(scene);
        stage.show();
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

}
