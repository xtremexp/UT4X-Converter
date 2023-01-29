package org.xtx.ut4converter.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.xtx.ut4converter.config.ApplicationConfig;
import org.xtx.ut4converter.tools.PackageExporterService;
import org.xtx.ut4converter.tools.UIUtils;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.xtx.ut4converter.config.ApplicationConfig.loadApplicationConfig;

public class ExportPackageController implements Initializable {

    public static final String EXPORTER_EPIC_GAMES = "Epic Games";

    public static final String EXPORTER_UMODEL = "Umodel";

    /**
     * Exporter for UE1/UE2 games only. The only one that can extract Unreal 2 textures
     */
    public static final String EXPORTER_STE = "Simple Texture Exporter (UE1/2 only)";

    private static final String TEX_LABEL_NO_CONV = "No conversion";

    private static final String TEX_LABEL_DDS = "Convert to .dds";

    private static final String TEX_LABEL_BMP = "Convert to .bmp";

    private static final String TEX_LABEL_PNG = "Convert to .png";

    private static final String TEX_LABEL_PCX = "Convert to .pcx";

    @FXML
    public TextField exportFolder;
    public ComboBox<String> pkgExtractorCbBox;
    public TextArea logContentTxtArea;

    @FXML
    public Button stopExportBtn;
    public Button selectPackageBtn;
    public ComboBox<String> textureConvCb;
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

    @FXML
    private Label progressIndicatorLbl;

    private PackageExporterService pkgExporterService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            stopExportBtn.setVisible(false);

            //this.textureConvCb = new ComboBox<>();
            this.textureConvCb.getItems().add(TEX_LABEL_NO_CONV);
            this.textureConvCb.getItems().add(TEX_LABEL_BMP);
            //this.textureConvCb.getItems().add(TEX_LABEL_DDS);
            //this.textureConvCb.getItems().add(TEX_LABEL_PCX);
            this.textureConvCb.getItems().add(TEX_LABEL_PNG);
            this.textureConvCb.getSelectionModel().select(TEX_LABEL_NO_CONV);

            this.pkgExtractorCbBox.getItems().add(EXPORTER_EPIC_GAMES);
            this.pkgExtractorCbBox.getItems().add(EXPORTER_UMODEL);

            this.pkgExtractorCbBox.getSelectionModel().select(EXPORTER_UMODEL);
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

    /**
     * Action when choosing Unreal Package from file dialog
     */
    public void selectPackage() {

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

            // staticmeshes - ut2003/4
            if (selectedGame.getUeVersion() == 2) {
                extList.add("*.usx");
            }

            // Prefab - unreal 2
            if (selectedGame.getUeVersion() == 2) {
                extList.add("*.upx");
            }

            if (selectedGame.getUeVersion() <= 3) {
                extList.add("*.u");
            }

            if (selectedGame.getUeVersion() >= 4) {
                extList.add("*.pak");
            }

            extList.remove("*.null");

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Unreal package", extList.toArray(new String[0])));
        } else {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Unreal package", "*.unr", "*.un2", "*.ut2", "*.ut3", "*.utx", "*.uax", "*.usx", "*.umx", "*.upx", "*.dtx", "*.dnf"));
        }


        File uPakFile = chooser.showOpenDialog(new Stage());

        if (uPakFile != null) {
            this.unrealPakFile = uPakFile;
            this.unrealPakPath.setText(this.unrealPakFile.getAbsolutePath());
            this.convertBtn.setDisable(this.outputFolder == null);
        }
    }

    public void exportPackage() {

        final UnrealGame selectedGame = this.unrealGamesList.getSelectionModel().getSelectedItem();

        if (selectedGame.getPath() == null || !selectedGame.getPath().exists()) {
            showError("Game path for " + selectedGame.getName() + " is not set in settings.");
            return;
        }

        if (this.outputFolder == null) {
            showError("Select output folder.");
            return;
        }

        // .pak files can only be extracted with UCC
        if (this.unrealPakFile != null) {
            if (this.unrealPakFile.getName().endsWith(".pak")) {
                this.pkgExtractorCbBox.getSelectionModel().select(EXPORTER_EPIC_GAMES);
            }
            // uasset files can only be extracted with umodel
            else if (this.unrealPakFile.getName().endsWith(".uasset")) {
                this.pkgExtractorCbBox.getSelectionModel().select(EXPORTER_UMODEL);
            }
        }

        // exports to /outputfolder/<PackageName>
        File outputFolder2 = outputFolder;

        // umodel split export folders by package unlike UCC and SimpleTextureExtractor
        // for better visibility export to /exportfolder/<PackageName> when using them
        if (this.pkgExtractorCbBox.getSelectionModel().getSelectedItem().equals(EXPORTER_EPIC_GAMES) || this.pkgExtractorCbBox.getSelectionModel().getSelectedItem().equals(EXPORTER_STE)) {
            outputFolder2 = new File(this.outputFolder + File.separator + this.unrealPakFile.getName().substring(0, this.unrealPakFile.getName().lastIndexOf(".")).replaceAll("\\.", ""));
        }


        String textureFileExt = null;

        switch (this.textureConvCb.getSelectionModel().getSelectedItem()) {
            case TEX_LABEL_BMP -> textureFileExt = "bmp";
            case TEX_LABEL_DDS -> textureFileExt = "dds";
            case TEX_LABEL_PCX -> textureFileExt = "pcx";
            case TEX_LABEL_PNG -> textureFileExt = "png";
            default -> {
            }
        }

        this.pkgExporterService = new PackageExporterService(pkgExtractorCbBox.getSelectionModel().getSelectedItem(), selectedGame, outputFolder2, this.unrealPakFile, textureFileExt);

        File finalOutputFolder = outputFolder2;
        pkgExporterService.setOnSucceeded(t -> {
            pkgExporterService.reset();
            displayLogs(pkgExporterService);
            progressIndicatorLbl.setText("All done !");
            UIUtils.openExplorer(finalOutputFolder);
            convertBtn.setDisable(false);
            convertBtn.setVisible(true);
            stopExportBtn.setVisible(false);
        });

        pkgExporterService.setOnFailed(t -> {
            pkgExporterService.reset();
            displayLogs(pkgExporterService);
            progressIndicatorLbl.setText("Error !");
            convertBtn.setDisable(false);
            convertBtn.setVisible(true);
            stopExportBtn.setVisible(false);
        });

        pkgExporterService.setOnCancelled(t -> {
            pkgExporterService.reset();
            progressIndicatorLbl.setText("Cancelled");
            convertBtn.setDisable(false);
            convertBtn.setVisible(false);
            stopExportBtn.setVisible(false);
        });

        this.logContentTxtArea.setText("");
        stopExportBtn.setVisible(true);
        convertBtn.setDisable(true);
        convertBtn.setVisible(false);
        pkgExporterService.start();
        progressIndicatorLbl.setText("Please wait ...");
    }

    private void displayLogs(PackageExporterService pkgExporterService) {
        StringBuilder sb = new StringBuilder();

        try {
            for (String log : pkgExporterService.getTask().get()) {
                sb.append(log).append("\n");
            }

            this.logContentTxtArea.setText(sb.toString());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(msg);

        alert.showAndWait();
    }

    public void selectFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select output folder");

        File outputFolder2 = chooser.showDialog(new Stage());

        if (outputFolder2 != null) {
            this.outputFolder = outputFolder2;
            exportFolder.setText(this.outputFolder.getAbsolutePath());
            this.convertBtn.setDisable(this.unrealPakFile == null);
        }
    }

    public void stopExport() {
        this.pkgExporterService.cancel();
    }

    public void selectGame() {
        this.selectPackageBtn.setDisable(false);

        // simple texture exporter only works for Unreal Engine 1/2 games
        if (this.unrealGamesList.getSelectionModel().getSelectedItem().getUeVersion() <= 2) {
            this.pkgExtractorCbBox.getItems().add(EXPORTER_STE);
        } else {
            this.pkgExtractorCbBox.getItems().remove(EXPORTER_STE);
        }
    }
}
