package org.xtx.ut4converter.tools;

import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.export.SimpleTextureExtractor;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.ucore.UnrealGame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.xtx.ut4converter.controller.ExportPackageController.EXPORTER_STE;
import static org.xtx.ut4converter.controller.ExportPackageController.EXPORTER_UMODEL;

/**
 * Generic package exporter using either ucc.exe or umodel.exe
 */
public class PackageExporterTask extends Task<List<String>> {


    private static final Logger logger = LoggerFactory.getLogger(PackageExporterTask.class);

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

    final String textureFileExt;

    /**
     * Logs during extract
     */
    final List<String> logs = new ArrayList<>();

    /**
     * List of processes associated with the task (umodel, ...)
     */
    final List<Process> processList = new ArrayList<>();

    public PackageExporterTask(String exporter, UnrealGame game, File pkgFile, File outputFolder, final String textureFileExt) {
        this.exporter = exporter;
        this.game = game;
        this.outputFolder = outputFolder;
        this.pkgFile = pkgFile;
        this.textureFileExt = textureFileExt;
    }

    public List<String> exportPackage() throws IOException, InterruptedException {

        final List<String> commands = new ArrayList<>();
        String command;

        Files.createDirectories(outputFolder.toPath());

        if (exporter.equals(EXPORTER_UMODEL)) {
            command = Installation.getUModelPath() + " -export -sounds -groups -notgacomp -nooverwrite -nolightmap -lods -uc \"" + pkgFile + "\"";
            command += " -out=\"" + outputFolder + "\" -path=\"" + game.getPath() + "\"";
            commands.add(command);
        }
        else if (exporter.equals(EXPORTER_STE)) {
            commands.add(SimpleTextureExtractor.getCommand(pkgFile, outputFolder));
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
            command = "\"" + game.getPath() + game.getPkgExtractorPath() + "\" \"" + this.pkgFile + "\" " + " -Extract \"" + this.outputFolder + "\"";
            commands.add(command);
        }


        List<CompletableFuture<List<String>>> futureCmdTaskList = new ArrayList<>();

        for (String cmd : commands) {

            CompletableFuture<List<String>> xxx = CompletableFuture.supplyAsync(() -> {
                List<String> processLogs = new ArrayList<>();
                try {
                    Process process = Runtime.getRuntime().exec(cmd);

                    this.processList.add(process);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String log;

                        while ((log = reader.readLine()) != null) {
                            processLogs.add(log);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return processLogs;
            });

            futureCmdTaskList.add(xxx);
        }


        // wait all extracts are done
        CompletableFuture.allOf(futureCmdTaskList.toArray(new CompletableFuture[0])).join();

        try {
            for (CompletableFuture<List<String>> futureCmdTask : futureCmdTaskList) {
                logs.addAll(futureCmdTask.get());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        // texture format conversion
        if (textureFileExt != null) {
            for (final File texFile : org.apache.commons.io.FileUtils.listFiles(this.outputFolder, new String[]{"pcx", "tga", "dds", "bmp", "psd"}, true)) {

                String currentFileExt = FilenameUtils.getExtension(texFile.getName());

                if (textureFileExt.equals(currentFileExt)) {
                    continue;
                }

                try {
                    final BufferedImage img = ImageIO.read(texFile);
                    final File convTexFile = new File(texFile.getParent() + File.separator + texFile.getName().replaceAll("." + currentFileExt, "." + textureFileExt));

                    if (img != null && ImageIO.write(img, textureFileExt, convTexFile)) {
                        logs.add(texFile.getName() + " -> " + convTexFile.getName());
                    } else {
                        logs.add("Could not convert " + texFile.getName() + " to " + convTexFile.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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


        // TODO find other export commands for UE3
        if (game.getUeVersion() == 3) {
            //batchExportOptions.add(UCCExporter.UccOptions.CLASS_UC);
            batchExportOptions.add(UCCExporter.UccOptions.UE3_SOUNDNODEWAVE);
            batchExportOptions.add(UCCExporter.UccOptions.UE3_TEXTURE2D_BMP);
            //batchExportOptions.add(UCCExporter.UccOptions.UE3_COMPONENT_T3D);
        }
        // UE1/UE2
        else {
            if ("upx".equals(pakExt)) {
                batchExportOptions.add(UCCExporter.UccOptions.PREFAB_T3D);
            } else if ("usx".equals(pakExt)) {
                batchExportOptions.add(UCCExporter.UccOptions.STATICMESH_T3D);
            } else if (game.getTexExt().equals(pakExt)) {
                if (game.getUeVersion() == 2) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_DDS);
                } else if (game.getUeVersion() == 1) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_PCX);
                }

                batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_BMP);
            } else if (game.getSoundExt().equals(pakExt)) {
                batchExportOptions.add(UCCExporter.UccOptions.SOUND_WAV);
            } else if (game.getMusicExt().equals(pakExt)) {
                batchExportOptions.add(UCCExporter.UccOptions.MUSIC_S3M);
            } else if ("u".equals(pakExt) || game.getMapExt().equals(pakExt)) {

                if (game.getUeVersion() == 2) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_DDS);
                    batchExportOptions.add(UCCExporter.UccOptions.STATICMESH_T3D);
                } else if (game.getUeVersion() == 1) {
                    batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_PCX);
                }

                batchExportOptions.add(UCCExporter.UccOptions.TEXTURE_BMP);
                batchExportOptions.add(UCCExporter.UccOptions.CLASS_UC);
                batchExportOptions.add(UCCExporter.UccOptions.SOUND_WAV);

                if (game.getMapExt().equals(pakExt)) {
                    batchExportOptions.add(UCCExporter.UccOptions.LEVEL_T3D);
                }
            }
        }

        for (UCCExporter.UccOptions uccOptions : batchExportOptions) {
            // ucc is intended to be executed in its folder where it belongs to
            commandList.add(uccFullPath.getName() + " batchexport \"" + pkgFile + "\" " + uccOptions + " \"" + outputFolder + "\"");
        }

        return commandList;
    }

    @Override
    protected List<String> call() {
        try {
            return exportPackage();
        } catch (Exception e) {
            logger.error("Error extracting package " + this.pkgFile, e);
            this.failed();
            return logs;
        }
    }

    public List<Process> getProcessList() {
        return processList;
    }
}
