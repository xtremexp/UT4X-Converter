/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.MaterialInfo;
import org.xtx.ut4converter.ucore.UPackage;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * Interface to umodel.exe program by Konstantin Nosov
 * http://www.gildor.org/en/projects/umodel Not embedding umodel.exe binary to
 * project since license of it is "undetermined"
 * 
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
		if ((!forceExport && ressource.isExported()) || ressource.getUnrealPackage().getName().equals("null") || (!forceExport && ressource.getUnrealPackage().isExported())) {
			return null;
		}

		String command = getExporterPath() + " -export -sounds -groups \"" + ressource.getUnrealPackage().getFileContainer(mapConverter) + "\"";
		command += " -out=\"" + mapConverter.getTempExportFolder() + "\"";
		command += " -path=\"" + mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame()).getPath() + "\"";

		List<String> logLines = new ArrayList<>();

		logger.log(Level.INFO, "Exporting " + ressource.getUnrealPackage().getFileContainer(mapConverter).getName() + " with " + getName());
		logger.log(Level.FINE, command);

		Installation.executeProcess(command, logLines);

		ressource.getUnrealPackage().setExported(true);

		for (String logLine : logLines) {

			logger.log(Level.FINE, logLine);

			if (logLine.startsWith("Exporting") && !logLine.startsWith("Exporting objects")) {
				try {
					parseRessourceExported(logLine, ressource.getUnrealPackage());
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error umodel batch line", e);
					System.out.println("Export Line: " + logLine);
				}
			}

		}

		return null;
	}

	/**
	 * From umodel batch log lines get exported files and extra info about
	 * package ressources
	 * 
	 * @param logLine
	 *            Log line from umodel
	 * @param unrealPackage
	 *            Current unreal package being exported with umodel
	 */
	private void parseRessourceExported(String logLine, UPackage unrealPackage) {

		// Exporting Texture bdr02BA to
		// Z:\\TEMP\\umodel_win32/UmodelExport/BarrensArchitecture/Borders
		// Exporting StaticMesh trophy1 to
		// Z:\\TEMP\\umodel_win32/UmodelExport/2k4Trophies/AllTrophies

		String split[] = logLine.split(" to ");
		String split2[] = split[0].split("\\ "); // Exporting Texture bdr02BA

		// for resources that have been exported by umodel
		// with package != original package (staticmesh package -> texture (in
		// another package) ..)
		boolean forceIsUsedInMap = false;

		// S_ASC_Arch2_SM_StonePillar_02
		// bdr02BA
		String name = split2[2];

		// Z:\\TEMP\\umodel_win32\\UmodelExport/ASC_Arch2/SM/Mesh
		String exportFolder = split[1];

		String packageName = split[1].substring(mapConverter.getTempExportFolder().getAbsolutePath().length() + 1).split("/")[0];

		String group = null;
		int startIdxGroup = exportFolder.indexOf(packageName, mapConverter.getTempExportFolder().getAbsolutePath().length()) + packageName.length() + 1;

		// Some ressources does not have group info
		if (startIdxGroup < exportFolder.length()) {
			// <xxx>CTF-Strident\Temp/LT_Deco/BSP/Materials -> BSP/Materials
			// sometimes umodel export other files then in original package
			// Exporting Texture detail40 to C:/Users/XXX/workspace/UT4
			// Converter/Converted/DM-Rankin/Temp/UCGeneric/DetailTextures
			group = exportFolder.substring(startIdxGroup, exportFolder.length());

			// BSP/Materials -> BSP.Materials
			group = group.replaceAll("\\/", ".");
		}

		// StaticMesh3
		String typeStr = split2[1];
		Type type = Type.UNKNOWN;

		if (typeStr.toLowerCase().contains("texture")) {
			type = Type.TEXTURE;
		}

		else if (typeStr.toLowerCase().contains("staticmesh")) {
			type = Type.STATICMESH;
		}

		else if (typeStr.toLowerCase().contains("sound")) {
			type = Type.SOUND;
		}

		// sometimes umodel exports other files from other package (e.g:
		// HumanoidArchitecture.utx from ut2004)
		// "Exporting Texture detail40 to C:/Users/XXX/workspace/UT4 Converter/Converted/DM-Rankin/Temp/UCGeneric/DetailTextures"
		if (!packageName.toLowerCase().equals(unrealPackage.getName().toLowerCase())) {
			unrealPackage = mapConverter.findPackageByName(packageName);

			if (unrealPackage == null) {
				unrealPackage = new UPackage(packageName, type, mapConverter.getInputGame(), null);
				// don't flag package as exported because umodel do a partial
				// export in that case
				// for some specific resource(s)
				// unrealPackage.setExported(true);
				mapConverter.getMapPackages().put(packageName.toLowerCase(), unrealPackage);
				forceIsUsedInMap = true;
			}
		}

		File exportedFile = null;
		boolean isMaterial = false;

		final String BASE_EXPORT_FILE = exportFolder + File.separator + name;

		if (type == Type.STATICMESH) {
			exportedFile = new File(BASE_EXPORT_FILE + ".pskx");
		} else if (type == Type.TEXTURE) {
			exportedFile = new File(BASE_EXPORT_FILE + ".tga");
		}
		// UMODEL does produce .mat files
		// TODO handle .mat files for conversion
		// either replace with Diffuse Texture or find out some library that can
		// do the merging "diffuse + normal" stuff
		else if (typeStr.toLowerCase().contains("material")) {
			exportedFile = new File(BASE_EXPORT_FILE + ".mat");
			isMaterial = true;
		} else if (type == Type.SOUND) {

			if (mapConverter.getInputGame().engine.version <= 2) {
				exportedFile = new File(BASE_EXPORT_FILE + ".wav");
			}

			else if (mapConverter.getInputGame().engine.version == 3) {
				exportedFile = new File(BASE_EXPORT_FILE + ".ogg");
			}
		}

		String ressourceName;

		if (group != null) {
			ressourceName = packageName + "." + group + "." + name;
		} else {
			ressourceName = packageName + "." + name;
		}

		UPackageRessource uRessource = unrealPackage.findRessource(ressourceName);

		if (uRessource != null) {
			if (isMaterial && uRessource.getMaterialInfo() == null) {
				uRessource.setMaterialInfo(getMatInfo(uRessource, exportedFile));
			}
			uRessource.setExportedFile(exportedFile);
			uRessource.parseNameAndGroup(ressourceName); // for texture db that
															// don't have group
															// we retrieve the
															// group ...
			if (unrealPackage.isMapPackage(mapConverter.getMapPackageName())) {
				uRessource.setIsUsedInMap(true);
			}
			uRessource.setType(type);
		} else {
			uRessource = new UPackageRessource(mapConverter, ressourceName, unrealPackage, exportedFile, this);
			uRessource.setType(type);

			if (isMaterial) {
				uRessource.setMaterialInfo(getMatInfo(uRessource, exportedFile));

				// replace material with diffuse texture if possible
				if (uRessource.getMaterialInfo() != null && uRessource.getMaterialInfo().getDiffuse() != null) {
					// export diffuse texture if not ever done
					uRessource.export(UTPackageExtractor.getExtractor(mapConverter, uRessource));

					uRessource.replaceWith(uRessource.getMaterialInfo().getDiffuse());
				}
			}
		}

		if (uRessource != null && forceIsUsedInMap) {
			uRessource.setIsUsedInMap(true);
		}
	}

	/**
	 * Get material info from .mat file created by umodel program
	 * 
	 * @param matFile
	 *            .mat file containing info about material texture (normal,
	 *            diffuse, ...)
	 * @return Material info
	 */
	private MaterialInfo getMatInfo(UPackageRessource parentRessource, File matFile) {

		MaterialInfo mi = new MaterialInfo();
		/**
		 * Diffuse=T_HU_Deco_SM_Machinery04Alt_D
		 * Normal=T_HU_Deco_SM_Machinery04Alt_N
		 * Specular=T_HU_Deco_SM_Machinery04Alt_S
		 * Emissive=T_HU_Deco_SM_Machinery04Alt_E
		 */

		try (FileReader fr = new FileReader(matFile); BufferedReader bfr = new BufferedReader(fr)) {

			String line = null;

			while ((line = bfr.readLine()) != null) {

				String spl[] = line.split("\\=");

				// Diffuse
				String type = spl[0];

				// T_HU_Deco_SM_Machinery04Alt_D
				String matName = spl[1];

				// guessing package name the material comes from
				String pakName = parentRessource.getUnrealPackage().getName();

				// .mat file does not only give ressource name not where it
				// belong to
				// we assume it belong to parent ressource which should work for
				// 75%+ of cases ...
				if (mapConverter.getUt3PackageFileFromName(pakName) == null) {
					continue;
				}

				UPackageRessource uRessource = mapConverter.getUPackageRessource(matName, pakName, Type.TEXTURE);

				if (uRessource != null) {

					uRessource.setIsUsedInMap(parentRessource.isUsedInMap());

					switch (type) {

					case "Diffuse":
						mi.setDiffuse(uRessource);
						break;
					case "Normal":
						mi.setNormal(uRessource);
						break;
					case "Specular":
						mi.setSpecular(uRessource);
						break;
					case "Emissive":
						mi.setEmissive(uRessource);
						break;
					case "SpecPower":
						mi.setSpecPower(uRessource);
						break;
					case "Opacity":
						mi.setOpacity(uRessource);
						break;
					default:
						logger.warning("Unhandled type " + type + " Value:" + matName);
						break;
					}

				}
			}

		} catch (IOException exception) {
			logger.log(Level.WARNING, "Could not get material info from " + parentRessource.getFullName());
		}

		return mi;
	}

	@Override
	public File getExporterPath() {
		return Installation.getUModelPath(mapConverter);
	}

	@Override
	public boolean supportLinux() {
		// false for the moment but might be possible to activate in some future
		// ...
		return false;
	}

	@Override
	public String getName() {
		return "umodel";
	}

	@Override
	public UnrealEngine[] getSupportedEngines() {
		return new UnrealEngine[] { UnrealEngine.UE1, UnrealEngine.UE2, UnrealEngine.UE3, UnrealEngine.UE4 };
	}

}
