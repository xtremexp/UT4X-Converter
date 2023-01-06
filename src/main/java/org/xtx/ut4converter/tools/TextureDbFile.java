package org.xtx.ut4converter.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TextureDbFile {


    /**
     * Logger
     */
    static final Logger logger = Logger.getLogger("TextureNameToPackageGenerator");


    public static class TextureFileInfo {
        File texFile;

        List<TextureInfo> textures = new ArrayList<>();

        public TextureFileInfo() {

        }

        public TextureFileInfo(File texFile) {
            this.texFile = texFile;
        }

        public File getTexFile() {
            return texFile;
        }

        public void setTexFile(File texFile) {
            this.texFile = texFile;
        }

        public List<TextureInfo> getTextures() {
            return textures;
        }

        public void setTextures(List<TextureInfo> textures) {
            this.textures = textures;
        }
    }

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

        public TextureInfo(String name, String group, String packageName, String textureType, String fileName) {
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
     * Generates the texture database json file "/conf/[GameShortName]TextureDb.json
     * It is used afterwards for conversion to get the package name from texture name (packagename info not in .t3d level file)
     * Only Unreal 1 with oldunreal.com patch does not need it
     *
     * @param ue1ue2Game             Unreal engine 1 or 2 game
     * @param outJsonFile            .json file to write
     * @param currentTexFileInfoList Current texture files info if
     * @return List of new texture info
     * @throws IOException          Exception throw when writing json file
     * @throws InterruptedException Exception thrown when analysing texture files
     */
    private static void createOrUpdateTexNameToPackageFile(UnrealGame ue1ue2Game, File outJsonFile, List<TextureFileInfo> currentTexFileInfoList) throws IOException, InterruptedException {

        final List<String> currentTexFileAnalysed = new ArrayList<>();

        // list of all texture file packaged ever analyzed
        if (currentTexFileInfoList != null) {
            currentTexFileAnalysed.addAll(currentTexFileInfoList.stream().map(tfi -> tfi.getTexFile().getName()).distinct().toList());
        }

        final File texFolder = new File(ue1ue2Game.getPath() + "/" + UTGames.getPackageBaseFolderByResourceType(T3DRessource.Type.TEXTURE));
        final File systemFolder = new File(ue1ue2Game.getPath() + "/" + UTGames.getPackageBaseFolderByResourceType(T3DRessource.Type.SCRIPT));

        final String texFileExtension = UTGames.getPackageFileExtensionByGameAndType(ue1ue2Game, T3DRessource.Type.TEXTURE);

        // Filter by texture packages only (.u or .utx)
        final FilenameFilter fn = (dir, name) -> (!currentTexFileAnalysed.contains(name) && (name.toLowerCase().endsWith("." + texFileExtension.toLowerCase()) || name.endsWith(".u")));

        final List<TextureFileInfo> newTexInfosList = new ArrayList<>();

        // read texture packages from /Textures folder
        newTexInfosList.addAll(extractTexturesInfoFromFileTexPackages(Objects.requireNonNull(texFolder.listFiles(fn))));

        // read texture packages from /System folder
        newTexInfosList.addAll(extractTexturesInfoFromFileTexPackages(Objects.requireNonNull(systemFolder.listFiles(fn))));

        if (!newTexInfosList.isEmpty()) {

            currentTexFileInfoList.addAll(newTexInfosList);

            // Export to .json file the texture db
            logger.info("Writing " + outJsonFile.getName() + "...");

            final ObjectMapper objectMapper = new ObjectMapper();

            try (final FileWriter fw = new FileWriter(outJsonFile)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(fw, currentTexFileInfoList);
            }
        }
    }

    /**
     * UE1/UE2 only
     * From texture package files (.utx or .u), extract texture info (all textures, with type, group, ...)
     *
     * @param texPackages File texture packages to analyse
     * @return Texture info
     * @throws InterruptedException UtxAnalyser.exe process exception
     * @throws IOException          Texture file read exception
     */
    private static List<TextureFileInfo> extractTexturesInfoFromFileTexPackages(File[] texPackages) throws InterruptedException, IOException {

        List<TextureFileInfo> texFileInfos = new ArrayList<>();

        for (final File utxFile : texPackages) {

            TextureFileInfo tfi = new TextureFileInfo(utxFile);
            final String command = Installation.getUtxAnalyser() + " \"" + utxFile.getAbsolutePath() + "\"";
            final List<String> logLines = new ArrayList<>();

            logger.info("Analyzing " + utxFile.getName());
            int exitCode = Installation.executeProcess(command, logLines, logger, Level.FINE);

            if (exitCode != 0) {
                logger.warning("Error analyzing " + utxFile.getName());
                continue;
            }

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

                    if ("NoGroup".equals(group)) {
                        tfi.getTextures().add(new TextureInfo(name, null, utxFile.getName().split("\\.")[0], texType, utxFile.getName()));
                    } else {
                        tfi.getTextures().add(new TextureInfo(name, group, utxFile.getName().split("\\.")[0], texType, utxFile.getName()));
                    }
                }

                idx++;
            }

            texFileInfos.add(tfi);
        }

        return texFileInfos;
    }

    /**
     * Loads the texture db file to get package name from texture name
     *
     * @param inputGame Unreal input game
     */
    public static void createOrUpdateTexDbForGame(final UnrealGame inputGame) throws IOException, InterruptedException {

        List<TextureFileInfo> currentTexFileInfoList = new ArrayList<>();

        File confFolder = new File(Installation.getProgramFolder() + File.separator + Installation.APP_FOLDER + File.separator + "conf");

        if (!confFolder.exists()) {
            confFolder = new File(Installation.getProgramFolder() + File.separator + "conf");
        }

        File dbFile = new File(confFolder + File.separator + TextureDbFile.getBaseFileName(inputGame));

        if (!dbFile.exists()) {
            dbFile = new File(confFolder + File.separator + TextureDbFile.getBaseFileName(inputGame));
        }

        final ObjectMapper om = new ObjectMapper();

        // reads current texture db info if exists
        if (dbFile.exists()) {
            // note: collection returned by objectmapper is not modifiable so have to create a new array
            currentTexFileInfoList = new ArrayList<>(Arrays.asList(om.readValue(dbFile, TextureDbFile.TextureFileInfo[].class)));
        }

        // creates db texture info file or update it (if there are some newer texture packages (with new map installed))
        TextureDbFile.createOrUpdateTexNameToPackageFile(inputGame, dbFile, currentTexFileInfoList);

    }

    /**
     * @param utgame Input game
     * @return Texture db filename
     */
    public static String getBaseFileName(UnrealGame utgame) {
        return utgame.getShortName() + "TextureDb2.json";
    }

}
