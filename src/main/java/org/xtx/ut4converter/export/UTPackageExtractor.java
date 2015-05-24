/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
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
        
        return new File(mapConverter.getTempExportFolder() + File.separator + type.name() + File.separator);
    }
    
    
    /**
     * Extract ressource, generally some package that contains multiple files (ressources)
     * @param ressource
     * @return List of files exported
     * @throws java.lang.Exception If anythings goes wrong when exporting this ressource
     */
    public abstract Set<File> extract(UPackageRessource ressource) throws Exception;
    
    public abstract File getExporterPath();
    
    
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
        
        // Special case UT2004 .ogg files
        if(ressource.getType() == T3DRessource.Type.MUSIC && mapConverter.getInputGame().engine == UnrealEngine.UE2){
            mapConverter.packageExtractor = new CopyExporter(mapConverter);
        } 

        else {
            if(mapConverter.packageExtractor == null 
                    || (mapConverter.packageExtractor != null && !(mapConverter.packageExtractor instanceof UCCExporter))){
                mapConverter.packageExtractor = UCCExporter.getInstance(mapConverter);
            }
        }

        return mapConverter.packageExtractor;
    }
    
}
