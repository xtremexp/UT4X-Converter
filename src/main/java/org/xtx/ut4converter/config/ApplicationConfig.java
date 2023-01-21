/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static org.xtx.ut4converter.ucore.UnrealGame.fromUeVersion;

/**
 * Class for representing application configuration such as games supported,
 * user game paths, ...
 */
public class ApplicationConfig implements Serializable {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Used for internal purpose for saving json file
     */
    @JsonIgnore
    private File file;

    /**
     * Version of config file.
     * Might be used for config upgrade for future ut converter versions.
     */
    @Min(1)
    private int version = 1;

    /**
     * Null at very first start.
     * If so will display a welcome message to user and redirect him to program settings window.
     */
    private Boolean isFirstRun;

    /**
     * If true, program will check for updates at start
     */
    private boolean checkForUpdates = true;

    public ApplicationConfig() {

    }

    /**
     * List of supported games by the converter
     */
    private List<UnrealGame> games = new ArrayList<>();

    private List<ConversionSettings> recentConversions = new ArrayList<>();

    private ConversionSettingsPanelConfig conversionSettingsPanelConfig;

    public List<UnrealGame> getGames() {
        return games;
    }

    public void setGames(List<UnrealGame> games) {
        this.games = games;
    }

    /**
     * Return the application config file.
     * If it's user application config file, it's in /Documents/UT4X-Converter
     *
     * @param defaultConfig If true will retrive the default application config file else the user application config file.
     * @return Application config file
     */
    public static File getApplicationConfigFile(boolean defaultConfig) {
        if (defaultConfig) {
            return new File(Installation.getConfFolder() + File.separator + "DefaultApplicationConfig.json");
        } else {
            return new File(Installation.getDocumentProgramFolder().getAbsolutePath() + File.separator + "ApplicationConfig.json");
        }
    }

    /**
     * Save application config file to json
     */
    public void saveFile() throws IOException {

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("Could not create directory " + file.getParentFile());
        }

        // default config file must never be overidden
        if (file.getName().equals(ApplicationConfig.getApplicationConfigFile(true).getName())) {
            throw new UnsupportedOperationException("DefaultApplicationConfig.json must not be overidden !");
        }

        try (final FileWriter fw = new FileWriter(file)) {
            logger.info("Saving " + file.getAbsolutePath());
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
        udkGame.setPkgExtractorPath("/Binaries/Win64/UDK.com");
        udkGame.setMapFolder("/UDKGame/Content");
        games.add(udkGame);

        final UnrealGame ut3Game = fromUeVersion("Unreal Tournament 3", "UT3", "ut3", 3);
        ut3Game.setMapFolder("/UTGame/CookedPC/Maps");
        ut3Game.setPkgExtractorPath("/Binaries/ut3.com");
        games.add(ut3Game);

        final UnrealGame ut4Game = fromUeVersion("Unreal Tournament 4", "UT4", "umap", 4);
        ut4Game.setMapFolder("/UnrealTournament/Content");
        ut4Game.setPkgExtractorPath("/Engine/Binaries/Win64/UnrealPak.exe");
        games.add(ut4Game);

        return games;
    }


    public static ApplicationConfig loadDefaultApplicationConfig() throws IOException {
        return ApplicationConfig.loadFromConfigFile(getApplicationConfigFile(true));
    }


    public static ApplicationConfig loadApplicationConfig() throws IOException {
        return ApplicationConfig.loadFromConfigFile(getApplicationConfigFile(false));
    }

    /**
     * Load application config object from ApplicationConfig.json file
     *
     * @param appConfigFile Application config json file
     * @return Application config
     * @throws IOException Exception thrown when reading config file
     */
    public static ApplicationConfig loadFromConfigFile(File appConfigFile) throws IOException {

        if (appConfigFile == null || !appConfigFile.exists()){
            return null;
        }

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final ApplicationConfig applicationConfig = objectMapper.readValue(appConfigFile, ApplicationConfig.class);
        applicationConfig.setFile(appConfigFile);
        return applicationConfig;
    }


    /**
     * From DefaultApplicationConfig, merge ApplicationConfig.json file into it.
     *
     * @param defaultConfig Default app config
     */
    public void mergeWithDefaultConfig(final ApplicationConfig defaultConfig) {

        this.isFirstRun = false;

        // clear all games but custom ones and adds one from the default config file
        final List<UnrealGame> appConfigCustomGames = this.games.stream().filter(UnrealGame::getIsCustom).toList();

        final List<UnrealGame> defAppConfigGamesCopy = new ArrayList<>(defaultConfig.games);
        defaultConfig.setCheckForUpdates(this.checkForUpdates);

        this.conversionSettingsPanelConfig =  defaultConfig.getConversionSettingsPanelConfig();
        // the reference app config must not be updated !

        for (final UnrealGame userGame : defAppConfigGamesCopy) {
            UnrealGame uGame = this.getGames().stream().filter(g -> g.getShortName().equals(userGame.getShortName())).findFirst().orElse(null);

            if (uGame != null) {
                userGame.setPath(uGame.getPath());
            }
            // game is custom and does not exist in default config, add it then
            else if (userGame.getIsCustom()) {
                defAppConfigGamesCopy.add(userGame);
            }
        }

        this.games = defAppConfigGamesCopy;
        this.games.addAll(appConfigCustomGames);
    }

    public List<ConversionSettings> getRecentConversions() {
        return recentConversions;
    }

    public void addRecentConversion(final ConversionSettings conversionSettings) {

        this.recentConversions.add(0, conversionSettings);
        // limit history to 8
        this.recentConversions = recentConversions.subList(0, Math.min(this.recentConversions.size() - 1, 7));
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


    public UnrealGame getUnrealGameById(final String shortName) {
        return this.games.stream().filter(u -> u.getShortName().equals(shortName)).findFirst().orElse(null);
    }

    public Boolean getIsFirstRun() {
        return isFirstRun;
    }

    public void setIsFirstRun(Boolean firstRun) {
        isFirstRun = firstRun;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ConversionSettingsPanelConfig getConversionSettingsPanelConfig() {
        return conversionSettingsPanelConfig;
    }

    public void setConversionSettingsPanelConfig(ConversionSettingsPanelConfig conversionSettingsPanelConfig) {
        this.conversionSettingsPanelConfig = conversionSettingsPanelConfig;
    }

    @Override
    public String toString() {
        return "ApplicationConfig{" +
                "version=" + version +
                ", isFirstRun=" + isFirstRun +
                ", checkForUpdates=" + checkForUpdates +
                ", games=" + games +
                '}';
    }
}
