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
     * If true, program will check for updates at start
     */
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

    public static List<UnrealGame> getBaseGames(){

        final List<UnrealGame> games = new ArrayList<>();

        final UnrealGame u1Game = new UnrealGame("Unreal 1", "U1", 1, "/System", "ucc.exe", "/Maps", "unr", "utx", "uax","umx");
        final UnrealGame u2Game = new UnrealGame("Unreal 2", "U2", 2, "/System", "ucc.exe", "/Maps", "un2", "utx", "uax","ogg");
        final UnrealGame ut99Game = new UnrealGame("Unreal Tournament", "UT99", 1, "/System", "ucc.exe", "/Maps", "unr", "utx", "uax","umx");
        ut99Game.setUseTexDb(true);
        final UnrealGame ut2003Game = new UnrealGame("Unreal Tournament 2003", "UT2003", 2, "/System", "ucc.exe", "/Maps", "ut2", "utx", "uax","ogg");
        final UnrealGame ut2004Game = new UnrealGame("Unreal Tournament 2004", "UT2004", 2, "/System", "ucc.exe", "/Maps", "ut2", "utx", "uax","ogg");
        final UnrealGame udkGame = new UnrealGame("Unreal Development Kit", "UDK", 3, "/Binaries/Win64", "UDK.com", "/Maps", "udk", "pak", "pak","pak");
        final UnrealGame ut3Game = new UnrealGame("Unreal Tournament 3", "UT3", 3, "/Binaries", "ut3.com", "/UTGame/CookedPC/Maps", "ut3", "pak", "pak","pak");
        final UnrealGame ut4Game = new UnrealGame("Unreal Tournament 4", "UT4", 4, "/UnrealTournament/Binaries/Win64", "/Engine/Binaries/Win64/UnrealPak.exe", "/UnrealTournament/Content", "umap", "pak", "pak","pak");
        final UnrealGame dnf2001 = new UnrealGame("Duke Nukem Forever 2001", "DNF", 1, "/System", "ucc.exe", "/Maps", "dnf", "dtx", "dfx","mp3");
        u1Game.addConvertsToGame(ut2003Game);
        dnf2001.setUseTexDb(true);

        games.add(u1Game);
        games.add(u2Game);
        games.add(ut99Game);
        games.add(ut2003Game);
        games.add(ut2004Game);
        games.add(udkGame);
        games.add(ut3Game);
        games.add(ut4Game);
        games.add(dnf2001);

        return games;
    }

    public static ApplicationConfig load() throws IOException {

        File file = ApplicationConfig.getApplicationConfigFile();

        // auto-create config file
        if (!file.exists()) {
            ApplicationConfig appConfig = new ApplicationConfig();

            appConfig.getGames().addAll(getBaseGames());

            // init
            appConfig.getGameConversion().put("U1", Arrays.asList("UT3","UT4"));
            appConfig.getGameConversion().put("U2", Arrays.asList("UT3","UT4"));
            appConfig.getGameConversion().put("UT99", Arrays.asList("UT3","UT4"));
            appConfig.getGameConversion().put("UT2003", List.of("UT4"));
            appConfig.getGameConversion().put("UT2004", List.of("UT4"));
            appConfig.getGameConversion().put("UT3", List.of("UT4"));
            appConfig.getGameConversion().put("DNF", Arrays.asList("UT3","UT4"));

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

}
