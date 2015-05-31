/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * Interface to umodel.exe program by Konstantin Nosov
 * http://www.gildor.org/en/projects/umodel
 * Not embedding umodel.exe binary to project since license of it is "undetermined"
 * @author XtremeXp
 */
public class UModelExporter extends UTPackageExtractor {

    public static final String program = "umodel.exe";
    
    public UModelExporter(MapConverter mapConverter) {
        super(mapConverter);
    }

    @Override
    public Set<File> extract(UPackageRessource ressource, boolean forceExport) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getExporterPath() {
        return Installation.getUModel(mapConverter);
    }

    @Override
    public boolean supportLinux() {
        // false for the moment but might be possible to activate in some future ...
        return false;
    }

    @Override
    public String getName() {
        return "umodel";
    }

    @Override
    public List<UTGames.UnrealEngine> getSupportedEngines() {
        return Arrays.asList(UnrealEngine.UE1, UnrealEngine.UE2, UnrealEngine.UE3, UnrealEngine.UE4);
    }
    
}
