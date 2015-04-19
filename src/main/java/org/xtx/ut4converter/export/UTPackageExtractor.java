/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.tools.Installation;

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
    protected Logger logger;
    
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
     * Basically: <programfolder>/Conversion/Export/<utshortname>/inName (better package)
     * @return 
     */
    protected File getExportFolder(){
        File programFolder = Installation.getProgramFolder();
        
        return new File(programFolder.getAbsolutePath() + File.separator + MapConverter.CONV_PATH + File.separator + mapConverter.getInputGame().shortName + File.separator);
    }
    
    
    /**
     * Extract ressource, generally some package that contains multiple files (ressources)
     * @param ressource
     * @return List of files exported
     */
    public abstract List<File> extract(T3DRessource ressource);
    
    public abstract File getExporterPath();
    
    /**
     * Returns and start an instance of an extractor.
     * This depends of unreal engine version as well as game.
     * @param mapConverter
     * @param ressource
     * @return 
     */
    public static UTPackageExtractor getExtractor(MapConverter mapConverter, T3DRessource ressource){
        
        if(mapConverter.packageExtractor != null){
            return mapConverter.packageExtractor;
        } else {
            // TODO handle for multiple extractors
            mapConverter.packageExtractor = new UCCExporter(mapConverter);
            return mapConverter.packageExtractor;
        }
    }

}
