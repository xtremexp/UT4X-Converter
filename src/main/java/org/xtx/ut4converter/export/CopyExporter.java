/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * A simple "copy" of file Used for copying music .ogg files
 * 
 * @author XtremeXp
 */
public class CopyExporter extends UTPackageExtractor {

	public CopyExporter(MapConverter mapConverter) {
		super(mapConverter);
	}

	@Override
	public Set<File> extract(UPackageRessource ressource, boolean forceExport, boolean perfectMatchOnly) throws Exception {

		File inputFile = ressource.getUnrealPackage().getFileContainer(mapConverter);
		File outputFile = new File(getExportFolder(ressource.getType()).getAbsolutePath() + File.separator + inputFile.getName());
		outputFile.mkdirs();

		logger.log(Level.INFO, "Copying " + inputFile.getName() + " " + ressource.getType() + " package");
		Files.copy(inputFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		Set<File> files = new HashSet<>();
		files.add(outputFile);
		return files;
	}

	@Override
	public File getExporterPath() {
		return null;
	}

	@Override
	public boolean supportLinux() {
		return true;
	}

	@Override
	protected File getExportFolder(T3DRessource.Type type) {
		return new File(mapConverter.getMapConvertFolder() + File.separator + type.name() + File.separator);
	}

	@Override
	public String getName() {
		return "File Copier";
	}

	@Override
	public UnrealEngine[] getSupportedEngines() {
		return new UnrealEngine[] { UTGames.UnrealEngine.UE1, UTGames.UnrealEngine.UE2, UTGames.UnrealEngine.UE3, UTGames.UnrealEngine.UE4 };
	}

}
