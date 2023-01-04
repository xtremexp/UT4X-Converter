package org.xtx.ut4converter.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UE1/UE2 games sometimes do not share package info about texture being used in .t3d file.
 * E.g: "Begin Polygon Item=2DLoftTOP Texture=swinerds1RC Flags=524288 Link=1" (texture is swinerds1RC but package is unknown)
 * In order to get package name, utxanalyser get the package info then program generates a json file
 * that can be used for map conversion for it to get the package name and extract the textures
 */
public class TextureNameToPackageGenerator {

    /**
     * Logger
     */
    static final Logger logger = Logger.getLogger("TextureNameToPackageGenerator");


    public static class TextureInfo {

        /**
         * Name of texture (e.g: 'Grey')
         */
        String name;
        /**
         * Name of group (e.g: 'Base')
         */
        String group;

        /**
         * Package name (e.g: 'StarShip')
         */
        String packageName;
        /**
         * Type of texture (Texture, FireTexture, WetTexture, ...)
         */
        String textureType;


        /**
         * DO NOT DELETE, empty constructor for json jackson lib
         */
        public TextureInfo() {

        }

        public TextureInfo(String name, String group, String packageName, String textureType) {
            this.name = name;
            this.group = group;
            this.packageName = packageName;
            this.textureType = textureType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getTextureType() {
            return textureType;
        }

        public void setTextureType(String textureType) {
            this.textureType = textureType;
        }
    }


    /**
     * Generates the texture database json file
     * It is used afterwards for conversion to get the package name from texture name (packagename info not in .t3d level file)
     * Only Unreal 1 with oldunreal.com patch does not need it
     *
     * @param ue1ue2Game Unreal engine 1 or 2 game
     * @param outJsonFile .json file to write
     *
     * @throws IOException Exception throw when writing json file
     * @throws InterruptedException Exception thrown when analysing texture files
     */
    public static void GenerateTexNameToPackageFile(UnrealGame ue1ue2Game, File outJsonFile) throws IOException, InterruptedException {


        final File texFolder = new File(ue1ue2Game.getPath() + "/" + UTGames.getPackageBaseFolderByResourceType(T3DRessource.Type.TEXTURE));
        final File systemFolder = new File(ue1ue2Game.getPath() + "/System");

        // filters texture or system files that can contains texture resources
        final String texFileExtension = UTGames.getPackageFileExtensionByGameAndType(ue1ue2Game, T3DRessource.Type.TEXTURE);
        // equalsIgnoreCase because DukeNukemForever have .dtx and .Dtx files for textures ...
        final FilenameFilter fn = (file, s) -> file != null && (("."+FilenameUtils.getExtension(s)).equalsIgnoreCase(texFileExtension) || s.endsWith(".u"));

        final List<TextureInfo> texInfos = new ArrayList<>();
        analyseTextures(Objects.requireNonNull(texFolder.listFiles(fn)), texInfos);
        analyseTextures(Objects.requireNonNull(systemFolder.listFiles(fn)), texInfos);

        // Export to .json file the texture db
        System.out.print("Writting " + outJsonFile.getName() + "...");

        final ObjectMapper objectMapper = new ObjectMapper();

        try (final FileWriter fw = new FileWriter(outJsonFile)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(fw, texInfos);
        }

        System.out.println("OK");
    }

    private static void analyseTextures(File[] texPackages, List<TextureInfo> texInfos) throws InterruptedException, IOException {

        for (final File utxFile : texPackages) {

            final String command = Installation.getUtxAnalyser() + " \"" + utxFile.getAbsolutePath() + "\"";
            final List<String> logLines = new ArrayList<>();

            logger.info("Analyzing " + utxFile.getName());
            Installation.executeProcess(command, logLines, logger, Level.FINE);

            // analyze log lines

            // line structure logLines "NoGroup,shore10,-,WetTexture"

            int idx = 0;

            for (final String line : logLines) {

                // skips first line (e.g: "File:DDayFX.utx") and last one
                if (idx > 0 && !"--End--".equals(line)) {
                    // Group,TextureName,Unknown,TextureType
                    final String[] split = line.split(",");

                    final String group = split[0];
                    final String name = split[1];
                    final String texType = split[3];


                    //if ("Texture".equals(texType)) {
                    if ("NoGroup".equals(group)) {
                        texInfos.add(new TextureInfo(name, null, utxFile.getName().split("\\.")[0], texType));
                    } else {
                        texInfos.add(new TextureInfo(name, group, utxFile.getName().split("\\.")[0], texType));
                    }
                    //}
                }

                idx++;
            }
        }
    }

    /**
     *
     * @param utgame Input game
     * @return Texture db filename
     */
    public static String getBaseFileName(UnrealGame utgame) {
        return utgame.getShortName() + "TextureDb.json";
    }
}
