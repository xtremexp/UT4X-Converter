package org.xtx.ut4converter.tools;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.util.List;

public class PackageExporterService extends Service<List<String>> {

    /**
     * Exporter to use
     */
    final String exporter;

    /**
     * Unreal game the package is linked to
     */
    final UnrealGame game;

    /**
     * Output folder where extracted ressources should bve
     */
    final File outputFolder;


    /**
     * Unreal package file to extract ressources
     */
    final File pkgFile;

    private Task<List<String>> task;

    public PackageExporterService(String exporter, UnrealGame game, File outputFolder, File pkgFile) {
        this.exporter = exporter;
        this.game = game;
        this.outputFolder = outputFolder;
        this.pkgFile = pkgFile;
    }

    @Override
    protected Task<List<String>> createTask() {
        this.task = new PackageExporterTask(this.exporter, this.game, this.pkgFile, this.outputFolder);
        return this.task;
    }

    public Task<List<String>> getTask() {
        return task;
    }
}
