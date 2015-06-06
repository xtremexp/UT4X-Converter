package org.xtx.ut4converter.export;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackage;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Simple texture extractor once i had done with UT3 converter
 * probably compiled from partial delphi package unit sources
 * "http://www.acordero.org/projects/unreal-tournament-package-delphi-library/"
 * but can't find my original sources ..
 * 
 * This is the ONLY one working for texture extraction from Unreal 2
 * 
 * @author XtremeXp
 *
 */
public class SimpleTextureExtractor extends UTPackageExtractor {

	public SimpleTextureExtractor(MapConverter mapConverter) {
		super(mapConverter);
	}

	@Override
	public Set<File> extract(UPackageRessource ressource, boolean forceExport)
			throws Exception {


		// Ressource ever extracted, we skip ...
        if((!forceExport && ressource.isExported()) || ressource.getUnrealPackage().getName().equals("null") || (!forceExport && ressource.getUnrealPackage().isExported())){
            return null;
        }
        
        String command = getExporterPath() + "  \"" + ressource.getUnrealPackage().getFileContainer(mapConverter) + "\"";
        command += " \"" + mapConverter.getTempExportFolder() + "\"";

        List<String> logLines = new ArrayList<>();
        
        logger.log(Level.INFO, "Exporting " + ressource.getUnrealPackage().getFileContainer(mapConverter).getName() + " with "+getName());
        logger.log(Level.FINE, command);
        
        Installation.executeProcess(command, logLines);
        
        ressource.getUnrealPackage().setExported(true);
        
        for(String logLine : logLines){
            
            logger.log(Level.FINE, logLine);
            
            /*
             * Analyzing package Glands.utx...
				   Extracting texture GrssFlorU08J012...  OK
				   Extracting texture MetlWall_U06B441_new...  OK
             */
            if(logLine.trim().startsWith("Extracting")){
                parseRessourceExported(logLine, ressource.getUnrealPackage());
            }
 
        }
		
		return null;
	}

	private void parseRessourceExported(String logLine, UPackage unrealPackage) {
		
		String name = logLine.split("texture ")[1].split("\\.")[0];
		// not sharing group info unfortunately ..
		
		File exportedFile = new File(mapConverter.getTempExportFolder().getAbsolutePath() + File.separator + name + ".bmp");
		
		name = unrealPackage.getName() + "." + name;
		
		UPackageRessource uRessource = unrealPackage.findRessource(name);
		
		if(uRessource != null){
            uRessource.setExportedFile(exportedFile);
            //uRessource.parseNameAndGroup(ressourceName); // for texture db that don't have group we retrieve the group ...
        }
        else {
            new UPackageRessource(name, unrealPackage, exportedFile, this);
        }
	}

	@Override
	public File getExporterPath() {
		return Installation.getExtractTextures(mapConverter);
	}

	@Override
	public String getName() {
		return "Simple Texture Extractor";
	}

	@Override
	public UnrealEngine[] getSupportedEngines() {
		return new UnrealEngine[] { UnrealEngine.UE1, UnrealEngine.UE2 };
	}

	@Override
	public boolean supportLinux() {
		return false;
	}

}
