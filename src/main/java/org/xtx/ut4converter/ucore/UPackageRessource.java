/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore;

import java.io.File;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.t3d.T3DRessource;
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
    

    /**
     * Group of ressource (optional)
     */
    public String group;

    /**
     * Name of ressource
     */
    public String name;

    /**
     *
     * @param fullName Full package ressource name (e.g:
     * "AmbAncient.Looping.Stower51"
     * @param game
     * @param type
     */
    public UPackageRessource(String fullName, UTGame game, T3DRessource.Type type) {

        String s[] = fullName.split("\\.");

        // TODO handle brush polygon texture info
        // which only have "name" info
        String packageName = type != Type.LEVEL ? s[0] : fullName;
        
        parseNameAndGroup(fullName);
        
        unrealPackage = new UPackage(packageName, type);
    }
    
    public void setPackageFile(File f){
        unrealPackage.setFile(f);
    }
    
    /**
     * 
     * @param fullName
     * @param uPackage 
     */
    public UPackageRessource(String fullName, UPackage uPackage) {

        parseNameAndGroup(fullName);
        
        unrealPackage = uPackage;
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
    
    public boolean needExport(){
        return !exportFailed && exportedFile == null;
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
    
    
    
}
