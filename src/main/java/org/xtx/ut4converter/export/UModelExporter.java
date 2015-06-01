/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackage;
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
        
        // Ressource ever extracted, we skip ...
        if((!forceExport && ressource.isExported()) || ressource.getUnrealPackage().getName().equals("null") || (!forceExport && ressource.getUnrealPackage().isExported())){
            return null;
        }
        
        File gamePath = mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame()).getPath();
        
        String command = getExporterPath() + " -export -sounds -groups \"" + ressource.getUnrealPackage().getFileContainer(gamePath) + "\"";
        command += " -out=\"" + mapConverter.getTempExportFolder() + "\"";
        command += " -path=\"" + mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame()).getPath() + "\"";
        
        List<String> logLines = new ArrayList<>();
        
        logger.log(Level.INFO, "Exporting " + ressource.getUnrealPackage().getFileContainer(gamePath).getName() + " with "+getName());
        logger.log(Level.FINE, command);
        
        Installation.executeProcess(command, logLines);
        
        ressource.getUnrealPackage().setExported(true);
        
        for(String logLine : logLines){
            
            logger.log(Level.FINE, logLine);
            
            if(logLine.startsWith("Exporting") && !logLine.startsWith("Exporting objects")){
                parseRessourceExported(logLine, ressource.getUnrealPackage());
            }
 
        }
        
        return null;
    }
    
    /**
     * From umodel batch log lines get exported files
     * and extra info about package ressources
     * @param logLine Log line from umodel
     * @param unrealPackage Current unreal package being exported with umodel
     */
    private void parseRessourceExported(String logLine, UPackage unrealPackage){
        
        // Exporting Texture bdr02BA to Z:\\TEMP\\umodel_win32/UmodelExport/BarrensArchitecture/Borders
        // Exporting StaticMesh trophy1 to Z:\\TEMP\\umodel_win32/UmodelExport/2k4Trophies/AllTrophies
        // TODO handle UT3 which has different log export data
        
        String split[] = logLine.split(" to ");
        String split2[] = split[0].split("\\ "); // Exporting Texture bdr02BA

        // S_ASC_Arch2_SM_StonePillar_02
        // bdr02BA
        String name = split2[2];

        // Z:\\TEMP\\umodel_win32\\UmodelExport/ASC_Arch2/SM/Mesh
        String exportFolder = split[1];
        
        int startIdx = exportFolder.indexOf(unrealPackage.getName()) + unrealPackage.getName().length() + 1;
        
        String group = null;
        // Some ressources does not have group info
        if(exportFolder.length() >= startIdx){
            group = exportFolder.substring(exportFolder.indexOf(unrealPackage.getName()) + unrealPackage.getName().length() + 1, exportFolder.length());
        }

        // StaticMesh3
        String typeStr = split2[1];
        Type type = Type.UNKNOWN;

        if(typeStr.toLowerCase().contains("texture")){
            type = Type.TEXTURE;
        } 

        else if(typeStr.toLowerCase().contains("staticmesh")){
            type = Type.STATICMESH;
        }
        
        else if(typeStr.toLowerCase().contains("sound")){
            type = Type.SOUND;
        }

        File exportedFile = null;

        if(type == Type.STATICMESH){
            exportedFile = new File(exportFolder + File.separator + name + ".pskx");
        }
        else if(type == Type.TEXTURE){
            exportedFile = new File(exportFolder + File.separator + name + ".tga");
        }
        else if(type == Type.SOUND){
            exportedFile = new File(exportFolder + File.separator + name + ".wav");
        }

        
        String ressourceName;
        
        if(group != null){
            ressourceName = unrealPackage.getName() + "." + group + "." + name;
        } else {
            ressourceName = unrealPackage.getName() + "." + name;
        }
        
        UPackageRessource uRessource = unrealPackage.findRessource(ressourceName);
                    
        if(uRessource != null){
            uRessource.setExportedFile(exportedFile);
            uRessource.parseNameAndGroup(ressourceName); // for texture db that don't have group we retrieve the group ...
        }
        else {
            UPackageRessource r = new UPackageRessource(ressourceName, unrealPackage, exportedFile, this);
        }
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
