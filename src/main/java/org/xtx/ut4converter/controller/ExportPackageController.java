package org.xtx.ut4converter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.xtx.ut4converter.config.ApplicationConfig;
import org.xtx.ut4converter.tools.PackageExporterTask;
import org.xtx.ut4converter.tools.UIUtils;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.xtx.ut4converter.config.ApplicationConfig.loadApplicationConfig;

public class ExportPackageController implements Initializable {

    @FXML
    public TextField exportFolder;
    public ComboBox<String> pkgExtractorCbBox;
    public TextArea logContent;
    /**
     * Package to export ressources
     */
    private File unrealPakFile;

    private File outputFolder;

    @FXML
    private TextField unrealPakPath;

    @FXML
    private Button convertBtn;

    private ApplicationConfig applicationConfig;

    @FXML
    private ComboBox<UnrealGame> unrealGamesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.pkgExtractorCbBox.getSelectionModel().select("umodel");
            this.applicationConfig = loadApplicationConfig();

            this.applicationConfig.getGames().forEach(g -> {
                if (!g.isDisabled()) {
                    unrealGamesList.getItems().add(g);
                }
            });

            unrealGamesList.setConverter(new StringConverter<>() {

                @Override
                public String toString(UnrealGame object) {
                    if (object == null) return null;
                    return object.getName();
                }

                @Override
                public UnrealGame fromString(String string) {
                    if (string != null) {
                        return applicationConfig.getGames().stream().filter(g -> g.getShortName().equals(string)).findFirst().orElse(null);
                    } else {
                        return null;
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectPackage(ActionEvent actionEvent) {


        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select unreal package");


        final UnrealGame selectedGame = this.unrealGamesList.getSelectionModel().getSelectedItem();

        if (selectedGame != null) {
            chooser.setInitialDirectory(selectedGame.getPath());

            final Set<String> extList = new LinkedHashSet<>();
            extList.add("*." + selectedGame.getMapExt());
            extList.add("*." + selectedGame.getSoundExt());
            extList.add("*." + selectedGame.getTexExt());
            extList.add("*." + selectedGame.getMusicExt());

            if (selectedGame.getUeVersion() == 2) {
                extList.add("*.usx");
            }

            if (selectedGame.getUeVersion() <= 2) {
                extList.add("*.u");
            }

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Unreal package", extList.toArray(new String[0])));
        } else {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Unreal package", "*.unr", "*.un2", "*.ut2", "*.ut3", "*.utx", "*.uax", "*.usx", "*.dtx", "*.umx"));
        }


        this.unrealPakFile = chooser.showOpenDialog(new Stage());

        if (this.unrealPakFile != null) {
            this.unrealPakPath.setText(this.unrealPakFile.getAbsolutePath());
            this.convertBtn.setDisable(this.outputFolder == null);
        }
    }

    public void exportPackage(ActionEvent actionEvent) {

        try {
            final UnrealGame selectedGame = this.unrealGamesList.getSelectionModel().getSelectedItem();

            if (selectedGame.getPath() == null || !selectedGame.getPath().exists()) {
                showError("Error", "Game path for " + selectedGame.getName() + " is not set in settings.");
            }

            // (final String exporter, final UnrealGame game, final File pkgFile, final File outputFolder, final List<String> logs
            final PackageExporterTask packageExporter = new PackageExporterTask(pkgExtractorCbBox.getSelectionModel().getSelectedItem(), selectedGame, this.unrealPakFile, this.outputFolder);

            int exitCode = packageExporter.exportPackage();
            StringBuilder sb = new StringBuilder();

            for (String log : packageExporter.getLogs()) {
                sb.append(log).append("\n");
            }

            logContent.setText(sb.toString());


            if (exitCode != 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Package " + this.unrealPakFile.getName() + " failed to be exported.");
                alert.showAndWait();

                UIUtils.openExplorer(this.outputFolder);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Success");
                alert.setContentText("Package " + this.unrealPakFile.getName() + " was successfully exported");
                alert.showAndWait();

                UIUtils.openExplorer(this.outputFolder);
            }
        } catch (InterruptedException | IOException e) {
            showError("Error exporting", e.getMessage());
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    public void selectFolder(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select output folder");

        this.outputFolder = chooser.showDialog(new Stage());

        if (this.outputFolder != null) {
            exportFolder.setText(this.outputFolder.getAbsolutePath());
            this.convertBtn.setDisable(this.unrealPakFile == null);
        }


    }
}
