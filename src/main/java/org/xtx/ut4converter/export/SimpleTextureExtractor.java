package org.xtx.ut4converter.export;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackage;
import org.xtx.ut4converter.ucore.UPackageRessource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Simple texture extractor once i had done with UT3 converter probably compiled
 * from partial delphi package unit sources
 * "<a href="http://www.acordero.org/projects/unreal-tournament-package-delphi-library/">...</a>"
 * but can't find my original sources ..
 * This is the ONLY one working for texture extraction from Unreal 2
 *
 * @author XtremeXp
 *
 */
public class SimpleTextureExtractor extends UTPackageExtractor {

	public SimpleTextureExtractor(MapConverter mapConverter) {
		super(mapConverter);
	}

	private static String getCommand(final File exporterPath, final File texturePackageFile, final File outputFolder){
		return "\"" + exporterPath + "\"  \"" + texturePackageFile + "\" \"" + outputFolder + "\"";
	}

	@Override
	public Set<File> extract(UPackageRessource ressource, boolean forceExport, boolean perfectMatchOnly) throws IOException, InterruptedException {

		// Ressource ever extracted, we skip ...
		if ((!forceExport && ressource.isExported()) || ressource.getUnrealPackage().getName().equals("null") || (!forceExport && ressource.getUnrealPackage().isExported())) {
			return null;
		}

		String command = getCommand(getExporterPath(), ressource.getUnrealPackage().getFileContainer(mapConverter), mapConverter.getTempExportFolder());
		command += " \"" + mapConverter.getTempExportFolder() + "\"";

		List<String> logLines = new ArrayList<>();

		logger.log(Level.INFO, "Exporting " + ressource.getUnrealPackage().getFileContainer(mapConverter).getName() + " with " + getName());
		logger.log(Level.FINE, command);

		Installation.executeProcess(command, logLines);


		for (String logLine : logLines) {

			logger.log(Level.FINE, logLine);

			/*
			 * Analyzing package Glands.utx... Extracting texture
			 * GrssFlorU08J012... OK Extracting texture MetlWall_U06B441_new...
			 * OK
			 */
			if (logLine.trim().startsWith("Extracting")) {
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

		if (uRessource != null) {
			uRessource.getExportInfo().setExportedFile(exportedFile);
			// uRessource.parseNameAndGroup(ressourceName); // for texture db
			// that don't have group we retrieve the group ...
		} else {
			final List<File> exportedFiles = new ArrayList<>();
			exportedFiles.add(exportedFile);
			new UPackageRessource(mapConverter, name, unrealPackage, exportedFiles, this);
		}
	}

	@Override
	public File getExporterPath() {
		return Installation.getExtractTextures();
	}

	@Override
	public String getName() {
		return "Simple Texture Extractor";
	}

	@Override
	public boolean supportLinux() {
		return false;
	}

}
