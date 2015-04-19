/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DRessource.Type;

/**
 * Very basic implementation of unreal package
 * @author XtremeXp
 */
public class UPackage {
    
    private UTGames.UnrealEngine engine = UTGames.UnrealEngine.NONE;
    
    /**
     * Name of package
     */
    String name;
    
    /**
     * File of package
     */
    File file;
    
    /**
     * Package ressources (textures, staticmeshes, ...)
     */
    Set<UPackageRessource> ressources = new HashSet<>();
    
    /**
     * Type of package (level, sound, textures, ...)
     */
    Type type;
    
    /**
     * 
     * @param name Package Name
     * @param type Type of package (sounds, textures, ...)
     */
    public UPackage(String name, Type type){
        this.name = name;
        this.type = type;
    }

    public UPackage(UTGames.UnrealEngine engine, File file, Type type) {
        this.engine = engine;
        this.file = file;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    
    public void addRessource(UPackageRessource ressource){
        ressources.add(ressource);
    }

    public Set<File> getExportedFiles() {
        
        Set<File> exportedFiles = new HashSet<>();
        
        for(UPackageRessource upr : ressources){
            if(upr.getExportedFile() != null){
                exportedFiles.add(upr.getExportedFile());
            }
        }
        
        return exportedFiles;
    }
    
}
