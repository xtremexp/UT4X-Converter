/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Base class for exporting stuff from Unreal Packages (including levels) such
 * as Textures, Sounds, StaticMeshes and so on.
 * 
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
	private File forcedExportFolder;

	/**
	 * Force package to be flaged as not exported even if it has been exported.
	 * This might be used in some cases to export terrain texture using ucc.exe
	 * but using umodel to get all other stuff from same package
	 */
	private boolean forceSetNotExported;

	/**
	 * 
	 * @param mapConverter
	 *            Map converter
	 */
	public UTPackageExtractor(MapConverter mapConverter) {
		this.mapConverter = mapConverter;
		this.logger = mapConverter.getLogger();
	}

	/**
	 * Tells where to export files. Basically:
	 * <programfolder>/Converted/<mapname>/Temp/<ressourcetype> (better package)
	 * 
	 * @param type
	 *            Type of ressource to export
	 * @return Returns the export folder of resource depending on it's type
	 */
	protected File getExportFolder(T3DRessource.Type type) {

		if (forcedExportFolder != null) {
			return forcedExportFolder;
		}

		return new File(mapConverter.getTempExportFolder() + File.separator + type.name() + File.separator);
	}

	/**
	 * Extract ressource, generally some package that contains multiple files
	 * (ressources)
	 * 
	 * @param ressource Unreal package resource
	 * @param forceExport If true will force export no matter if resource is not used in map
	 * @return Set of files exported
	 * @throws IOException, Interrupted Exception
	 *             If anythings goes wrong when exporting this ressource
	 */
	public abstract Set<File> extract(UPackageRessource ressource, boolean forceExport, boolean perfectMatchOnly) throws IOException, InterruptedException;

	public abstract File getExporterPath();

	public abstract String getName();

	/**
	 * Says if this extractor support linux.
	 * 
	 * @return <code>true<code> If this extractor does support linux
	 */
	public abstract boolean supportLinux();

	/**
	 * Returns and start an instance of an extractor. This depends of unreal
	 * engine version as well as game.
	 * 
	 * @param mapConverter Map converter
	 * @param ressource Unreal package resource
	 * @return Package resources extractor for the resource
	 */
	public static UTPackageExtractor getExtractor(final MapConverter mapConverter, final UPackageRessource ressource) {

		// umodel does not support unreal 2 at all ...
		if (mapConverter.getInputGame().getShortName().equals(UTGame.U2.shortName)) {
			if (ressource.getType() == Type.TEXTURE) {
				return getUtPackageExtractor(mapConverter, SimpleTextureExtractor.class);
			} else {
				return getUtPackageExtractor(mapConverter, UCCExporter.class);
			}
		}
		else if (ressource.getType() == T3DRessource.Type.MUSIC) {
			// only ucc can export .umx files
			if (mapConverter.getInputGame().getUeVersion() == UnrealEngine.UE1.version) {
				return getUtPackageExtractor(mapConverter, UCCExporter.class);
			}
			// Special case UT2004 .ogg files
			else if (mapConverter.getInputGame().getUeVersion() == UnrealEngine.UE2.version) {
				return getUtPackageExtractor(mapConverter, CopyExporter.class);
			}
			// else embedded .wav files
			else {
				return getUtPackageExtractor(mapConverter, UModelExporter.class);
			}
		}
		else {
			if (mapConverter.getPreferedTextureExtractorClass() != null) {
				return getUtPackageExtractor(mapConverter, mapConverter.getPreferedTextureExtractorClass());
			} else {
				return getUtPackageExtractor(mapConverter, UModelExporter.class);
			}
		}
	}

	/**
	 * Automatically get package extractor if it does exists or auto create one
	 * TODO factory
	 *
	 * @param mapConverter Map converter
	 * @param extractorClass Extractor class
	 * @return Return package extractor instance
	 */
	private static UTPackageExtractor getUtPackageExtractor(MapConverter mapConverter, Class<? extends UTPackageExtractor> extractorClass) {

		for (UTPackageExtractor extractor : mapConverter.packageExtractors) {
			if (extractor.getClass() == extractorClass) {
				return extractor;
			}
		}

		return null;
	}

	/**
	 * Force ut package to be exported to this folder rather than the default
	 * one /UT4Converter/<mapname>/Temp
	 * 
	 * @param forcedExportFolder Force export to this folder rathen than default
	 */
	public void setForcedExportFolder(File forcedExportFolder) {
		this.forcedExportFolder = forcedExportFolder;
	}

	public boolean isForceSetNotExported() {
		return forceSetNotExported;
	}

	public void setForceSetNotExported(boolean forceSetNotExported) {
		this.forceSetNotExported = forceSetNotExported;
	}

	
}
