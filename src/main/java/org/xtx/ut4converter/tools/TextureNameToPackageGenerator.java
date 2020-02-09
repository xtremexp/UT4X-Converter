package org.xtx.ut4converter.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.util.Pair;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.model.UserConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextureNameToPackageGenerator {


    public static String UT99_TEXNAME_TO_PACKAGE_FILENAME = "UT99TexNameToPackage.json";


    public static class TextureInfo {
        String name;
        String group;
        String packageName;
        String textureType;

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
     * Generates the UT99TexNameToPackage.json file in /conf folder
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final UserConfig uc = UserConfig.load();


        final File texFolder = new File(uc.getGameConfigByGame(UTGames.UTGame.UT99).getPath() + "/Textures");
        final File systemFolder = new File(uc.getGameConfigByGame(UTGames.UTGame.UT99).getPath() + "/System");

        // filters .utx or .u files
        final FilenameFilter fn = (file, s) -> file != null && (s.endsWith(".utx") || s.endsWith(".u"));

        final List<TextureInfo> texInfos = new ArrayList<>();
        analyseTextures(Objects.requireNonNull(texFolder.listFiles(fn)), texInfos);
        analyseTextures(Objects.requireNonNull(systemFolder.listFiles(fn)), texInfos);

        // writes .txt file
        final File txtFile = new File("C:\\dev\\" + UT99_TEXNAME_TO_PACKAGE_FILENAME);

        System.out.print("Writting " + txtFile.getName() + "...");

        ObjectMapper objectMapper = new ObjectMapper();

        try (final FileWriter fw = new FileWriter(txtFile)) {
            objectMapper.writeValue(fw, texInfos);
        } catch (IOException e) {
            throw e;
        }

        System.out.println("OK");
    }

    private static void analyseTextures(File[] texPackages, List<TextureInfo> texInfos) throws InterruptedException, IOException {

        for (final File utxFile : texPackages) {

            final String command = Installation.getUtxAnalyser() + " \"" + utxFile.getAbsolutePath() + "\"";
            final List<String> logLines = new ArrayList<>();

            System.out.print("Analyzing " + utxFile.getName() + "...");
            Installation.executeProcess(command, logLines);
            System.out.println("OK");

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

                    final Pair<String, String> p = new Pair("x", "y");


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
}
