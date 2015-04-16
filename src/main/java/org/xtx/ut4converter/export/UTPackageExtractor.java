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
     * Ressource to export, 
     * can be level, textures, sounds, ...
     */
    protected T3DRessource ressource;
    
    /**
     * Temporary logger until we embed one in MapConverter class
     */
    protected Logger logger;
    
    /**
     * 
     * @param mapConverter Map converter
     * @param ressource 
     */
    public UTPackageExtractor(MapConverter mapConverter, T3DRessource ressource) {
        this.mapConverter = mapConverter;
        this.ressource = ressource;
        this.logger = Logger.getLogger("PackageExtractor");
    }
    
    /**
     * Relative path from UT4 Converter program
     * where unreal stuff will be exported
     */
    final String CONVERSION_PATH = "Conversion";
    
    /**
     * Tells where to export files.
     * Basically: <programfolder>/Conversion/Export/<utshortname>/inName (better package)
     * @return 
     */
    protected File getExportFolder(){
        File programFolder = Installation.getProgramFolder();
        
        return new File(programFolder.getAbsolutePath() + File.separator + CONVERSION_PATH + File.separator + mapConverter.getInputGame().shortName + File.separator);
    }
    
    
    /**
     * Extract ressource, generally some package that contains multiple files (ressources)
     * @return List of files exported
     */
    public abstract List<File> extract();
    
    public abstract File getExporterPath();
    
    /**
     * Returns and start an instance of an extractor.
     * This depends of unreal engine version as well as game.
     * @param mapConverter
     * @param ressource
     * @return 
     */
    public static UTPackageExtractor getExtractor(MapConverter mapConverter, T3DRessource ressource){
        
        // Only this one for the moment
        // until we use UModel or make our own package reader to get stuff
        return new UCCExporter(mapConverter, ressource);
    }

    public T3DRessource getRessource() {
        return ressource;
    }
    
    
}
