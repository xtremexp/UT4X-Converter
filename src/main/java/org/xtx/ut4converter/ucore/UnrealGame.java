/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.ucore;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.xtx.ut4converter.config.GameConversionConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnrealGame {


    /**
     * Short name
     */
    @JsonProperty("id")
    @Size(min = 2)
    @NotBlank
    private String shortName;

    /**
     * Full name
     */
    @NotBlank
    private String name;


    /**
     * Unreal Engine version
     */
    @Min(1)
    @Max(5)
    private int ueVersion;

    /**
     * Relative path from game path where maps are
     * E.g: '/Maps'
     */
    private String mapFolder;

    /**
     * Map filename extension
     */
    @NotBlank
    private String mapExt;

    /**
     * Texture file extension
     */
    private String texExt;

    /**
     * Music file extension
     */
    private String musicExt;

    /**
     * Relative path of export program
     * used to export level to t3d and also map resources
     * E.g: /System/ucc.exe
     */
    @NotBlank
    private String exportExecPath;

    /**
     * Where this unreal game is installed
     */
    private File path;


    /**
     * Suggestion where this unreal game might be installed.
     * (e.g: "C:\Program Files (x86)\Steam\steamapps\common\Unreal II The Awakening")
     */
    private File suggestedPath;

    /**
     * If true, this game will need texture db
     */
    private boolean useTexDb;

    /**
     * If true, this game was added by user and should not be deleted or modified
     * by program (except the 'path' property)
     */
    private boolean isCustom;


    /**
     * Sound file extension
     */
    private String soundExt;

    /**
     * List of id (=shortName) where the game can be converted to
     */
    @JsonProperty("convertsToGames")
    private List<GameConversionConfig> convertsTo = new ArrayList<>();



    // do not delete, constructor for jackson json lib
    public UnrealGame() {

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
    public static UnrealGame fromUeVersion(String name, String shortNameId, String mapExt, int ueVersion) {

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


    public String getMapFolder() {
        return mapFolder;
    }

    public void setMapFolder(String mapFolder) {
        this.mapFolder = mapFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getUeVersion() {
        return ueVersion;
    }

    public void setUeVersion(int ueVersion) {
        this.ueVersion = ueVersion;
    }

    public String getMapExt() {
        return mapExt;
    }

    public void setMapExt(String mapExt) {
        this.mapExt = mapExt;
    }

    public String getTexExt() {
        return texExt;
    }

    public void setTexExt(String texExt) {
        this.texExt = texExt;
    }

    public String getSoundExt() {
        return soundExt;
    }

    public void setSoundExt(String soundExt) {
        this.soundExt = soundExt;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public File getSuggestedPath() {
        return suggestedPath;
    }

    public void setSuggestedPath(File suggestedPath) {
        this.suggestedPath = suggestedPath;
    }

    public boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean custom) {
        isCustom = custom;
    }

    public List<GameConversionConfig> getConvertsTo() {
        return convertsTo;
    }

    public void setConvertsTo(List<GameConversionConfig> convertsTo) {
        this.convertsTo = convertsTo;
    }

    public boolean isUseTexDb() {
        return useTexDb;
    }

    public void setUseTexDb(boolean useTexDb) {
        this.useTexDb = useTexDb;
    }

    public String getMusicExt() {
        return musicExt;
    }

    public void setMusicExt(String musicExt) {
        this.musicExt = musicExt;
    }

    public String getExportExecPath() {
        return exportExecPath;
    }

    public void setExportExecPath(String exportExecPath) {
        this.exportExecPath = exportExecPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnrealGame that = (UnrealGame) o;
        return ueVersion == that.ueVersion && useTexDb == that.useTexDb && isCustom == that.isCustom && name.equals(that.name) && Objects.equals(mapFolder, that.mapFolder) && mapExt.equals(that.mapExt) && Objects.equals(texExt, that.texExt) && Objects.equals(musicExt, that.musicExt) && exportExecPath.equals(that.exportExecPath) && Objects.equals(path, that.path) && Objects.equals(suggestedPath, that.suggestedPath) && Objects.equals(soundExt, that.soundExt) && Objects.equals(convertsTo, that.convertsTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ueVersion, mapFolder, mapExt, texExt, musicExt, exportExecPath, path, suggestedPath, useTexDb, isCustom, soundExt, convertsTo);
    }

    @Override
    public String toString() {
        return "UnrealGame{" +
                "shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", ueVersion=" + ueVersion +
                ", mapFolder='" + mapFolder + '\'' +
                ", mapExt='" + mapExt + '\'' +
                ", texExt='" + texExt + '\'' +
                ", musicExt='" + musicExt + '\'' +
                ", exportExecPath='" + exportExecPath + '\'' +
                ", path=" + path +
                ", suggestedPath='" + suggestedPath + '\'' +
                ", useTexDb=" + useTexDb +
                ", isCustom=" + isCustom +
                ", soundExt='" + soundExt + '\'' +
                ", convertsTo=" + convertsTo +
                '}';
    }
}
