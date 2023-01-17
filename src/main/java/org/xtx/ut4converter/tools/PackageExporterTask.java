package org.xtx.ut4converter.tools;

import javafx.concurrent.Task;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.xtx.ut4converter.controller.ExportPackageController.EXPORTER_UMODEL;

/**
 * Generic package exporter using either ucc.exe or umodel.exe
 */
public class PackageExporterTask extends Task<List<String>> {

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

    /**
     * Logs during extract
     */
    final List<String> logs = new ArrayList<>();

    public PackageExporterTask(String exporter, UnrealGame game, File pkgFile, File outputFolder) {
        this.exporter = exporter;
        this.game = game;
        this.outputFolder = outputFolder;
        this.pkgFile = pkgFile;
    }

    public List<String> exportPackage() throws IOException, InterruptedException {

        final List<String> commands = new ArrayList<>();
        String command;

        if (exporter.equals(EXPORTER_UMODEL)) {
            command = Installation.getUModelPath() + " -export -sounds -groups -notgacomp -nooverwrite -nolightmap -lods -uc \"" + pkgFile + "\"";
            command += " -out=\"" + outputFolder + "\" -path=\"" + game.getPath() + "\"";
            commands.add(command);
        }
        // UCC
        // For UE1/UE2 need to execute some .bat file
        // that execute ucc.exe from it's own dir else will likely fail since no support for whitespace folders
        else if (game.getUeVersion() <= 3) {
            for (String uccCommand : getUccBatchExportCommands(game, pkgFile, outputFolder)) {
                commands.add(createExportFileBatch(uccCommand).getAbsolutePath());
            }
        }
        // UE4/UE5
        else {
            // unrealpak only allows .pak extract
            command = "\"" + game.getPath() + game.getPkgExtractorPath() + "\" \""+ this.pkgFile + "\" " + " -Extract \"" + this.outputFolder + "\"";
            commands.add(command);
        }



        for (String cmd : commands) {
            logs.add(cmd);
            Installation.executeProcess(cmd, logs, 10000L);
        }

        return logs;
    }

    private File createExportFileBatch(String command) throws IOException {

        final File uccFullPath = new File(game.getPath() + game.getPkgExtractorPath());
        final File fbat = File.createTempFile("UCCExportPackage", ".bat");

        try (final FileWriter fw = new FileWriter(fbat); final BufferedWriter bwr = new BufferedWriter(fw)) {

            // switch to good drive (e.g, executing UT4)
            String drive = game.getPath().getAbsolutePath().substring(0, 2);
            bwr.write(drive + "\n");

            // use ucc.exe from it's folder
            bwr.write("cd \"" + uccFullPath.getParent() + "\"\n");
            bwr.write(command);
        }

        return fbat;
    }

    private static List<String> getUccBatchExportCommands(UnrealGame game, File pkgFile, File outputFolder) {

        final File uccFullPath = new File(game.getPath() + game.getPkgExtractorPath());

        List<String> commandList = new ArrayList<>();
        List<UCCExporter.UccOptions> batchExportOptions = new ArrayList<>();
        String pakExt = FileUtils.getExtension(pkgFile);


        if (game.getUeVersion() == 3) {
            //batchExportOptions.add(UCCExporter.UccOptions.CLASS_UC);
            batchExportOptions.add(UCCExporter.UccOptions.UE3_SOUNDNODEWAVE);
            batchExportOptions.add(UCCExporter.UccOptions.UE3_TEXTURE2D_BMP);
            //batchExportOptions.add(UCCExporter.UccOptions.UE3_COMPONENT_T3D);
        }
        // UE1/UE2
        else {
            if ("usx".equals(pakExt)) {
                batchExportOptions.add(UCCExporter.UccOptions.STATICMESH_T3D);
            } else if (game.getTexExt().equals(pakExt)) {
                if (game.getUeVersion() == 2) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_DDS);
                } else if (game.getUeVersion() == 1) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_PCX);
                }
            } else if (game.getSoundExt().equals(pakExt)) {
                batchExportOptions.add(UCCExporter.UccOptions.SOUND_WAV);
            } else if (game.getMusicExt().equals(pakExt)) {
                batchExportOptions.add(UCCExporter.UccOptions.MUSIC_S3M);
            }
            // .u or map package files can have many type of stuff inside
            else if ("u".equals(pakExt) || game.getMapExt().equals(pakExt)) {
                if (game.getUeVersion() == 2) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_DDS);
                } else if (game.getUeVersion() == 1) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_PCX);
                }

                batchExportOptions.add(UCCExporter.UccOptions.CLASS_UC);
                batchExportOptions.add(UCCExporter.UccOptions.SOUND_WAV);
            }
        }

        for (UCCExporter.UccOptions uccOptions : batchExportOptions) {
            // ucc is intended to be executed in its folder where it belongs to
            commandList.add(uccFullPath.getName() + " batchexport \"" + pkgFile + "\" " + uccOptions + " \"" + outputFolder + "\"");
        }

        return commandList;
    }

    @Override
    protected List<String> call() throws Exception {
        return exportPackage();
    }
}
