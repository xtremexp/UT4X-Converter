/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ApplicationConfig {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Version of config file
     */
    private int version = 1;

    /**
     *
     */
    @JsonProperty("is_first_run")
    private Boolean isFirstRun;

    /**
     * If true, program will check for updates at start
     */
    @JsonProperty("check_for_updates")
    private boolean checkForUpdates = true;

    public ApplicationConfig(){

    }

    /**
     * List of supported games by the converter
     */
    private List<UnrealGame> games = new ArrayList<>();

    /**
     * Map of supported conversion with unreal game shortName as key
     * and list of shortName for output unreal games
     * E.g: U1 -> UT3/UT4 would give <U1, [UT3,UT4]>
     */
    @JsonProperty("game_conversion")
    private Map<String, List<String>> gameConversion = new HashMap<>();

    public List<UnrealGame> getGames() {
        return games;
    }

    public void setGames(List<UnrealGame> games) {
        this.games = games;
    }

    public Map<String, List<String>> getGameConversion() {
        return gameConversion;
    }

    public void setGameConversion(Map<String, List<String>> gameConversion) {
        this.gameConversion = gameConversion;
    }

    public static File getApplicationConfigFile() {
        return new File(Installation.getDocumentProgramFolder().getAbsolutePath() + File.separator + "ApplicationConfig.json");
    }

    /**
     * Save application config file to json
     *
     */
    public void saveFile() throws IOException {
        File configFile = getApplicationConfigFile();

        if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
            throw new IOException("Could not create directory " + configFile.getParentFile());
        }

        try (final FileWriter fw = new FileWriter(configFile)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(fw, this);
        }
    }

    /**
     * Return list of supported unreal games by the converter
     *
     * @return List of supported unreal games by the converter
     */
    public static List<UnrealGame> getBaseGames() {

        final List<UnrealGame> games = new ArrayList<>();

        final UnrealGame u1Game = fromUeVersion("Unreal 1", "U1", "unr", 1);
        u1Game.setUseTexDb(false);
        games.add(u1Game);

        games.add(fromUeVersion("Unreal Tournament", "UT99", "unr", 1));

        final UnrealGame dnf = fromUeVersion("Duke Nukem Forever 2001", "DNF", "dnf", 1);
        dnf.setTexExt("dtx");
        dnf.setSoundExt("dfx");
        dnf.setMusicExt("ext");
        dnf.setUseTexDb(true);
        games.add(dnf);

        games.add(fromUeVersion("Unreal 2", "U2", "un2", 2));
        games.add(fromUeVersion("Unreal Tournament 2003", "UT2003", "ut2", 2));
        games.add(fromUeVersion("Unreal Tournament 2004", "UT2004", "ut2", 2));
        final UnrealGame udkGame = fromUeVersion("Unreal Development Kit", "UDK", "udk", 3);
        udkGame.setExportExecPath("/Binaries/Win64/udk.com");
        games.add(udkGame);

        final UnrealGame ut3Game = fromUeVersion("Unreal Tournament 3", "UT3", "ut3", 3);
        ut3Game.setMapFolder("/UTGame/CookedPC/Maps");
        ut3Game.setExportExecPath("/Binaries/ut3.com");
        games.add(ut3Game);

        final UnrealGame ut4Game = fromUeVersion("Unreal Tournament 4", "UT4", "umap", 4);
        ut4Game.setMapFolder("/UnrealTournament/Content");
        ut4Game.setExportExecPath("/Engine/Binaries/Win64/UnrealPak.exe");
        games.add(ut4Game);

        return games;
    }

    /**
     * Init unreal game from name, shortname, map extension and ueversion
     *
     * @param name        Full game name
     * @param shortNameId Short name (used as id)
     * @param mapExt      Map extension
     * @param ueVersion   Unreal engine version
     * @return Unreal game
     */
    private static UnrealGame fromUeVersion(String name, String shortNameId, String mapExt, int ueVersion) {

        final UnrealGame unrealGame = new UnrealGame();
        unrealGame.setUeVersion(ueVersion);
        unrealGame.setMapExt(mapExt);
        unrealGame.setName(name);
        unrealGame.setShortName(shortNameId);

        if (ueVersion <= 2) {
            unrealGame.setExportExecPath("/System/ucc.exe");
            unrealGame.setMapFolder("/Maps");
            unrealGame.setTexExt("utx");
            unrealGame.setSoundExt("uax");

            if (ueVersion == 1) {
                unrealGame.setMusicExt("umx");
                // ucc batch export does not provide unreal package name in Level.t3d file for brush surfaces
                unrealGame.setUseTexDb(true);
            } else {
                unrealGame.setMusicExt("ogg");
            }
        } else if (ueVersion == 4) {
            unrealGame.setExportExecPath("/Engine/Binaries/Win64/UnrealPak.exe");
            unrealGame.setMusicExt("pak");
            unrealGame.setTexExt("pak");
            unrealGame.setSoundExt("pak");
        }

        return unrealGame;
    }

    /**
     * Load application config from hson file
     *
     * @return Application config
     * @throws IOException Exception thrown when reading json file
     */
    public static ApplicationConfig load() throws IOException {

        final File file = ApplicationConfig.getApplicationConfigFile();

        // auto-create config file
        if (!file.exists()) {
            ApplicationConfig appConfig = new ApplicationConfig();

            appConfig.getGames().addAll(getBaseGames());

            // init
            appConfig.getGameConversion().put("U1", Arrays.asList("UT3", "UT4"));
            appConfig.getGameConversion().put("U2", Arrays.asList("UT3", "UT4"));
            appConfig.getGameConversion().put("UT99", Arrays.asList("UT3", "UT4"));
            appConfig.getGameConversion().put("UT2003", List.of("UT4"));
            appConfig.getGameConversion().put("UT2004", List.of("UT4"));
            appConfig.getGameConversion().put("UT3", List.of("UT4"));
            appConfig.getGameConversion().put("DNF", Arrays.asList("UT3", "UT4"));

            appConfig.saveFile();

            return appConfig;
        }

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(file, ApplicationConfig.class);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isCheckForUpdates() {
        return checkForUpdates;
    }

    public void setCheckForUpdates(boolean checkForUpdates) {
        this.checkForUpdates = checkForUpdates;
    }


    public UnrealGame getUnrealGameById(final String shortName){
        return this.games.stream().filter(u -> u.getShortName().equals(shortName)).findFirst().orElse(null);
    }

    public Boolean isFirstRun() {
        return isFirstRun;
    }

    public void setIsFirstRun(Boolean firstRun) {
        isFirstRun = firstRun;
    }
}
