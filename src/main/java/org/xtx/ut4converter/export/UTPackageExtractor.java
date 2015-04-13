/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.File;
import java.util.List;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.tools.Installation;

/**
 * Will allow extra ressources from Unreal Engine packages
 * Work in progress!
 * @author XtremeXp
 */
public abstract class UTPackageExtractor {
    
    protected MapConverter mapConverter;
    protected T3DRessource ressource;

    public UTPackageExtractor(MapConverter mapConverter, T3DRessource ressource) {
        this.mapConverter = mapConverter;
        this.ressource = ressource;
    }
    
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
        return new UCCExporter(mapConverter, ressource);
    }
}
