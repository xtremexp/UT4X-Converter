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

/**
 * Generic package exporter using either ucc.exe or umodel.exe
 */
public class PackageExporterTask extends Task<Object> {

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

    public int exportPackage() throws IOException, InterruptedException {

        final List<String> commands = new ArrayList<>();
        String command;

        if (exporter.equals("umodel")) {
            command = Installation.getUModelPath() + " -export -sounds -groups -notgacomp -nooverwrite -nolightmap -lods -uc \"" + pkgFile + "\"";
            command += " -out=\"" + outputFolder + "\" -path=\"" + game.getPath() + "\"";
            commands.add(command);
        }
        // UCC
        // For UE1/UE2 need to execute some .bat file
        // that execute ucc.exe from it's own dir else will likely fail since no support for whitespace folders
        else if (game.getUeVersion() < 3) {
            for (String uccCommand : getUccBatchExportCommands(game, pkgFile, outputFolder)) {
                System.out.println(uccCommand);
                commands.add(createExportFileBatch(uccCommand).getAbsolutePath());
            }
        } else {
            // TODO handle other commands for export for UE3/UE4 ...
            return 0;
        }


        int exitCode = 0;

        for (String cmd : commands) {
            exitCode |= Installation.executeProcess(cmd, logs, 10000L);
        }

        return exitCode;
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

        System.out.println("pakExt " + pakExt + " " + game.getTexExt());

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
        // .u package files can have many type of stuff inside
        else if ("u".equals(pakExt)) {
            if (game.getUeVersion() == 2) {
                batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_DDS);
            } else if (game.getUeVersion() == 1) {
                batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_PCX);
            }

            batchExportOptions.add(UCCExporter.UccOptions.CLASS_UC);
            batchExportOptions.add(UCCExporter.UccOptions.SOUND_WAV);
        }

        for (UCCExporter.UccOptions uccOptions : batchExportOptions) {
            // ucc is intended to be executed in its folder where it belongs to
            commandList.add(uccFullPath.getName() + " batchexport \"" + pkgFile + "\" " + uccOptions + " \"" + outputFolder + "\"");
        }

        return commandList;
    }

    public List<String> getLogs() {
        return logs;
    }

    @Override
    protected Object call() throws Exception {
        return exportPackage();
    }
}
