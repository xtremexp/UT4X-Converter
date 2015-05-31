/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Base class for exporting stuff from Unreal Packages (including levels)
 * such as Textures, Sounds, StaticMeshes and so on.
 * @author XtremeXp
 */
public abstract class UTPackageExtractor {
    
    /**
     * Map converter
     */
    protected MapConverter mapConverter;
    
    
    /**
     * Temporary logger until we embed one in MapConverter class
     */
    public Logger logger;
    
    /**
     * Force export folder
     */
    File forcedExportFolder;
    
    /**
     * 
     * @param mapConverter Map converter 
     */
    public UTPackageExtractor(MapConverter mapConverter) {
        this.mapConverter = mapConverter;
        this.logger = mapConverter.getLogger();
    }
    

    
    /**
     * Tells where to export files.
     * Basically: <programfolder>/Converted/<mapname>/Temp/<ressourcetype> (better package)
     * @param type Type of ressource to export
     * @return 
     */
    protected File getExportFolder(T3DRessource.Type type){
        
        if(forcedExportFolder != null){
            return forcedExportFolder;
        }
        
        return new File(mapConverter.getTempExportFolder() + File.separator + type.name() + File.separator);
    }
    
    
    /**
     * Extract ressource, generally some package that contains multiple files (ressources)
     * @param ressource
     * @param forceExport
     * @return List of files exported
     * @throws java.lang.Exception If anythings goes wrong when exporting this ressource
     */
    public abstract Set<File> extract(UPackageRessource ressource, boolean forceExport) throws Exception;
    
    public abstract File getExporterPath();
    
    public abstract String getName();
    
    /**
     * Supported Unreal Engines for extractor
     * e.g: ucc extractor only for UE1/UE2
     * but umodel extractor for all Unreal Engines
     * @return 
     */
    public abstract List<UnrealEngine> getSupportedEngines();
    
    /**
     * Says if this extractor support linux.
     * @return <code>true<code> If this extractor does support linux 
     */
    public abstract boolean supportLinux();
    
    /**
     * Returns and start an instance of an extractor.
     * This depends of unreal engine version as well as game.
     * @param mapConverter
     * @param ressource
     * @return 
     */
    public static UTPackageExtractor getExtractor(MapConverter mapConverter, UPackageRessource ressource){
        
        UTPackageExtractor extractor = null;
        
        try {
            // Special case UT2004 .ogg files
            if(ressource.getType() == T3DRessource.Type.MUSIC && mapConverter.getInputGame().engine == UnrealEngine.UE2){
                extractor = getUtPackageExtractor(mapConverter, CopyExporter.class);
            } 

            else {
                UTPackageExtractor uccExtractor = getUtPackageExtractor(mapConverter, UCCExporter.class);
                UTPackageExtractor uModelExtractor = getUtPackageExtractor(mapConverter, UModelExporter.class);
                
                return uccExtractor;
                //return uModelExtractor != null ? uModelExtractor : uccExtractor;
            }
        } catch (Exception e) {
            mapConverter.getLogger().log(Level.SEVERE, "Error getting the extractor", e);
        }

        return extractor;
    }
    
    /**
     * Automatically get package extractor if it does exists or auto create one
     * @param mapConverter
     * @param extractorClass
     * @return
     * @throws Exception 
     */
    private static UTPackageExtractor getUtPackageExtractor(MapConverter mapConverter, Class<? extends UTPackageExtractor> extractorClass) throws Exception {
        
        for(UTPackageExtractor extractor : mapConverter.packageExtractors){
            if(extractor.getClass() == extractorClass){
                return extractor;
            }
        }

        return null;
    }

    /**
     * Force ut package to be exported to this folder
     * rather than the default one /UT4Converter/<mapname>/Temp
     * @param forcedExportFolder 
     */
    public void setForcedExportFolder(File forcedExportFolder) {
        this.forcedExportFolder = forcedExportFolder;
    }
    
    
    
}
