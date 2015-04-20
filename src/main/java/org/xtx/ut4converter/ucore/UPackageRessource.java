/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore;

import java.io.File;
import java.util.logging.Level;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.t3d.T3DRessource.Type;

/**
 * TODO refactor / merge with T3DRessource.
 * It's becoming messy!
 * @author XtremeXp
 */
public class UPackageRessource {

    /**
     * Unreal Package linked to this ressource
     */
    UPackage unrealPackage;
    
    /**
     * Where this ressource have been exported.
     * TODO handle multi export file
     * (for textures we might need export as .bmp and .tga as well)
     * If it's null and exportFailed is false the we should try to export it
     */
    File exportedFile;
    
    /**
     * If true means export of this ressource failed
     * and we must not try to export it again
     */
    boolean exportFailed;
    
    /**
     * Where this ressource have been converted
     * (sometime needed).
     * E.G: UT99 do export .pcx files as textures
     * but UTs with engine > 2 do not support them
     */
    File convertedFile;
    
    boolean isUsedInMap;

    /**
     * Group of ressource (optional)
     */
    public String group;

    /**
     * Name of ressource
     */
    public String name;
    
    Type type;

    /**
     *
     * @param fullName Full package ressource name (e.g:
     * "AmbAncient.Looping.Stower51"
     * @param type
     * @param game UT game this ressource belongs to
     * @param isUsedInMap
     */
    public UPackageRessource(String fullName, Type type, UTGame game, boolean isUsedInMap) {

        String s[] = fullName.split("\\.");

        // TODO handle brush polygon texture info
        // which only have "name" info
        String packageName = type != Type.LEVEL ? s[0] : fullName;
        
        parseNameAndGroup(fullName);
        
        this.type = type;
        unrealPackage = new UPackage(packageName, type, game, this);
        this.isUsedInMap = isUsedInMap;
    }
    
    public void setPackageFile(File f){
        unrealPackage.setFile(f);
    }
    
    /**
     * Creates a package ressource
     * @param fullName Full name of ressource
     * @param uPackage Package this ressource belongs to
     * @param game
     * @param ressourceType Type of ressource (texture, sound, ...)
     * @param isUsedInMap <code>true<code> if this ressource is used in map that is being converted
     */
    public UPackageRessource(String fullName, Type ressourceType, UTGame game, UPackage uPackage, boolean isUsedInMap) {

        parseNameAndGroup(fullName);
        
        this.type = ressourceType;
        unrealPackage = uPackage;
        unrealPackage.addRessource(this);
        this.isUsedInMap = isUsedInMap;
    }
    
    /**
     * 
     * @param fullName
     * @param uPackage 
     * @param exportedFile 
     */
    public UPackageRessource(String fullName, UPackage uPackage, File exportedFile) {

        parseNameAndGroup(fullName);
        
        this.unrealPackage = uPackage;
        this.exportedFile = exportedFile;
        this.type = uPackage.type;
        uPackage.ressources.add(this);
    }
    
    private void parseNameAndGroup(String fullName){
        String s[] = fullName.split("\\.");

        // TODO handle brush polygon texture info
        // which only have "name" info
        name = s[s.length - 1];

        if (s.length == 3) {
            group = s[1];
        }
    }
    
    /**
     * Tells if this ressource can be exported.
     * If it has never been exported or export ever failed,
     * it cannot be exported again
     * @return 
     */
    public boolean needExport(){
        return !exportFailed && exportedFile == null;
    }
    
    
    /**
     * Export the ressource from unreal package to file
     * @param packageExtractor
     */
    public void export(UTPackageExtractor packageExtractor) {
        if(needExport()){
            try {
                packageExtractor.extract(this);
            } catch (Exception ex) {
                packageExtractor.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 
     * @param mapConverter
     * @return 
     */
    public String getConvertedName(MapConverter mapConverter){
        
        final String UE4_BASEPATH = "/Game/RestrictedAssets/Maps/WIP";
        
        String suffix = "";
            
        // in UT4 editor click on ressource file -> Create Cue adds a "_Cue" suffix
        if(type == Type.SOUND){
            suffix = "_Cue";
        } 

        else if(type == Type.TEXTURE){
            suffix = "_Mat";
        }
        
        return UE4_BASEPATH + "/" + mapConverter.getOutMapName() + "/" + type.getName() + "/" + getFullNameWithoutDots() + suffix + "." + getFullNameWithoutDots() + suffix;
    }
    
    /**
     * Sometimes we need to change name of exported file
     * to have info about from which package this file comes from
     * @return 
     */
    public String getConvertedFileName(){
        String s[] = exportedFile.getName().split("\\.");
        String currentFileExt = s[s.length -1];
            
        // TODO SYNC WITH T3DRessource.outName !
        return exportedFile.getParent() + File.separator + getFullNameWithoutDots() + "." +currentFileExt;
    }
    
    /**
     * Return the full name of package ressource
     * @return <packagename>.<group>.<name>
     */
    public String getFullName(){
        
        
        if(unrealPackage.name != null && group != null && name != null){
            return unrealPackage.name + "." + group + "." + name;
        }
        
        if(unrealPackage.name != null && group == null && name != null){
            return unrealPackage.name + "." + name;
        }
        
        if(unrealPackage.name == null && group == null && name != null){
            return name;
        }
        
        // other cases should not happen normally ...
        
        return "";
    }
    
    public String getFullNameWithoutDots(){
        return getFullName().replaceAll("\\.", "_");
    }

    public UPackage getUnrealPackage() {
        return unrealPackage;
    }
    
    public boolean isExported(){
        return exportedFile != null;
    }

    public File getExportedFile() {
        return exportedFile;
    }

    public void setExportedFile(File exportedFile) {
        this.exportedFile = exportedFile;
    }

    
    public void setIsUsedInMap(boolean isUsedInMap){
        this.isUsedInMap = isUsedInMap;
    }
    
    public boolean isUsedInMap(){
        return this.isUsedInMap;
    }
    
}
