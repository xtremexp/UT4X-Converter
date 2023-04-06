/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import org.slf4j.LoggerFactory;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackage;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

/**
 * Export ressources from map such as sounds to .wav, textures to .bmp and so on
 * ... using the original 'ucc.exe program' ... TODO test exporter when UT4
 * Converter as parent folder with spaces
 * 
 * @author XtremeXp
 */
public final class UCCExporter extends UTPackageExtractor {



	/**
	 * File path of ucc.exe or ut3.com program. Depends of user game settings
	 */
	private final File uccExporterPath;

	private UccOptions forcedUccOption;

	@Override
	public boolean supportLinux() {
		// oldunreal.com u1 patch as some UCCLinux.bin file (but is optional
		// during patch install)
		return mapConverter.getInputGame().getShortName().equals(UTGame.U1.shortName);
	}

	@Override
	public String getName() {
		return "UCC";
	}


	/**
	 * Exporter options of embedded UT extractor for Unreal Packages
	 */
	public enum UccOptions {

		UNKNOWN("UNKNOWN"), // fake option so will make crash export
		/**
		 * UE1/2/3
		 */
		LEVEL_T3D("Level t3d"),
		/**
		 * UE1/UE2 - For .uax sound packages
		 */
		SOUND_WAV("Sound wav"),
		/**
		 * UE1 - For .umx music packages
		 */
		MUSIC_XM("Music xm"),
		/**
		 * UE1 - For .umx music packages
		 */
		MUSIC_S3M("Music s3m"),
		TEXTURE_DDS("Texture dds"),
		/**
		 * Used to get 16bit grey terrain alpha layers
		 */
		TEXTURE_BMP("Texture bmp"),

		/**
		 * Always return 0 from UT2004 packages
		 */
		TEXTURE_TGA("Texture tga"),

		/**
		 * For U1/UT99
		 */
		TEXTURE_PCX("Texture pcx"),

		/**
		 * For .usx/.un2 - UE2 files
		 */
		STATICMESH_T3D("StaticMesh t3d"),

		/**
		 * For .upx files (a few files only in Unreal 2)
		 */
		PREFAB_T3D("Prefab t3d"),

		// Also for UE3
		CLASS_UC("Class uc"),

		/**
		 * Unknown usage
		 */
		POLYS_T3D("Polys T3D"),
		UE3_COMPONENT_T3D("Component T3D"),
		UE3_SOUNDNODEWAVE("SoundNodeWave wav"),
		UE3_TEXTURE2D_BMP("Texture2D BMP");

		final String option;

		UccOptions(String command) {
			this.option = command;
		}

		public String getOption() {
			return option;
		}

		@Override
		public String toString() {
			return this.option;
		}
	}

	/**
	 * Get the right command line option for ucc.exe for exporting ressources
	 * 
	 * @param type
	 *            Type of ressource
	 * @return ucc command line options
	 */
	private UccOptions getUccOptions(Type type, int engineVersion) {

		if (forcedUccOption != null) {
			return forcedUccOption;
		}

		if (type == Type.SOUND) {
			return UccOptions.SOUND_WAV;
		}

		else if (type == Type.MUSIC) {
			return UccOptions.MUSIC_S3M;
		}

		else if (type == Type.TEXTURE) {

			if (engineVersion == UnrealEngine.UE2.version) {
				return UccOptions.TEXTURE_DDS;
			}

			else if (engineVersion == UnrealEngine.UE1.version) {
				return UccOptions.TEXTURE_PCX;
			}
		}

		else if (type == Type.LEVEL) {
			return UccOptions.LEVEL_T3D;
		}

		else if (type == Type.STATICMESH) {
			return UccOptions.STATICMESH_T3D;
		}

		return UccOptions.UNKNOWN;
	}

	public UCCExporter(MapConverter mapConverter) {
		super(mapConverter);

		uccExporterPath = getExporterPath();
	}


	@Override
	public Set<File> extract(UPackageRessource ressource, boolean forceExport, boolean perfectMatchOnly) throws IOException, InterruptedException {

		// Ressource ever extracted, we skip ...
		if ((!forceExport && ressource.isExported()) || ressource.getUnrealPackage().getName().equals("null") || (!forceExport && ressource.getUnrealPackage().isExported())) {
			return null;
		}

		if (!uccExporterPath.exists()) {

			// For Unreal 1, by default ucc.exe program is not embedded, need
			// download latest patch from www.oldunreal.com !
			if (mapConverter.isTo(UTGame.U1.shortName)) {
				logger.log(Level.SEVERE, "{0} program does not exist. Download and install latest {1} patch at www.oldunreal.com", new Object[] { uccExporterPath.getName(), UTGames.UTGame.U1.name });
			} else {
				logger.log(Level.SEVERE, "Impossible to find {0} unreal package extractor", uccExporterPath.getAbsolutePath());
			}

			return null;
		}

		return exportPackage(ressource.getUnrealPackage());
	}

	/**
	 * Exports unreal map (.unr, .ut2, ...) to Unreal Text level file (.t3d)
	 * 
	 * @param mapConverter
	 *            MapConverter options, used to determined which game
	 * @param unrealMap
	 *            Map to export to .t3d
	 * @return t3d file map exported
	 */
	public static File exportLevelToT3d(MapConverter mapConverter, File unrealMap) throws Exception {

		if (unrealMap == null || !unrealMap.exists()) {
			LoggerFactory.getLogger(UCCExporter.class).warn("Impossible to export");
			return null;
		}

		UPackageRessource t3dRessource = new UPackageRessource(mapConverter, unrealMap.getAbsolutePath(), Type.LEVEL, true);
		UCCExporter ucE = new UCCExporter(mapConverter);

		Set<File> files = ucE.extract(t3dRessource, false, true);

		// UT3.com or UDK.com does not give info about t3d exported file in logs
		// but is always PersistentLevel.t3d in Binaries folder
		if (mapConverter.getInputGame().getUeVersion() == UnrealEngine.UE3.version) {
			//Introduce explaining variable
			String pathname = mapConverter.getInputGame().getPath() + File.separator + mapConverter.getInputGame().getPkgExtractorPath();
			final File binariesFolder = new File(pathname).getParentFile();
			return new File(binariesFolder + File.separator + UTGames.T3D_LEVEL_NAME_UE3);
		} else {
			return (files != null && !files.isEmpty()) ? files.iterator().next() : null;
		}
	}

	/**
	 * Creates a batch script to export ressources that creates a windows .bat
	 * script that will go to ucc.exe program and execute it. Used for Unreal
	 * Engine 1 based games because ucc.exe t3d exporter map program does not
	 * support whitespaces in folder name
	 * 
	 * @param type
	 *            Type of ressource to export (Textures, Sounds, ...)
	 * @param unrealPackage
	 *            Unreal package file to export (can be a map (.unr) file or
	 *            single package (.uax sound file package)
	 * @return Batch file created
	 */
	private File createExportFileBatch(File unrealPackage, Type type) throws IOException {

		final File fbat = File.createTempFile("UCCExportPackage", ".bat");

		try (final FileWriter fw = new FileWriter(fbat); final BufferedWriter bwr = new BufferedWriter(fw)) {

			String drive = uccExporterPath.getAbsolutePath().substring(0, 2);
			bwr.write(drive + "\n"); // switch to good drive (e.g, executing UT4
										// converter from Z:\\ drive but map is
										// in C:\\ drive
			bwr.write("cd \"" + uccExporterPath.getParent() + "\"\n");
			String cmd = getCommandLine(unrealPackage.getName(), type);
			logger.fine(cmd);
			bwr.write(getCommandLine(unrealPackage.getName(), type));
		}

		return fbat;
	}

	@Override
	public File getExporterPath() {

		final UnrealGame inputGame = this.mapConverter.getInputGame();
		return new File(inputGame.getPath() + inputGame.getPkgExtractorPath());
	}

	/**
	 * Get command line for exporting Unreal Package ressources
	 * 
	 * @param fileName
	 *            File name or full path filename of Unreal Package to extract
	 * @return Full command line for extracting stuff from unreal packages
	 *         (including maps)
	 */
	private String getCommandLine(String fileName, Type type) {

		if (mapConverter.getInputGame().getUeVersion() <= UnrealEngine.UE2.version) {
			return uccExporterPath.getName() + " batchexport  \"" + fileName + "\" " + getUccOptions(type, mapConverter.getInputGame().getUeVersion()) + " \"" + getExportFolder(type) + "\"";
		}

		else {
			return "\"" + uccExporterPath.getAbsolutePath() + "\" batchexport  " + fileName + " " + getUccOptions(type, mapConverter.getInputGame().getUeVersion());
		}
	}

	/**
	 * @param unrealPackage Unreal package to export
	 * @return Exported file for the ressource. If null means that ucc exported
	 *         was not able to export the ressource.
	 * @throws IOException Error while exporting package
	 * @throws InterruptedException Error while reading package
	 */
	private Set<File> exportPackage(UPackage unrealPackage) throws IOException, InterruptedException {

		File unrealMapCopy = null;
		File u1Batch = null;
		Set<File> exportedFiles = new HashSet<>();

		boolean noDelete = false;

		try {
			logger.log(Level.INFO, "Exporting " + unrealPackage.getFileContainer(mapConverter).getName() + " with " + getName() + " (mode: " + getUccOptions(unrealPackage.type, mapConverter.getInputGame().getUeVersion()) + ")");

			// Copy of unreal package to folder of ucc.exe (/System) for U1/U2
			unrealMapCopy = new File(uccExporterPath.getParent() + File.separator + unrealPackage.getFileContainer(mapConverter).getName());

			// we might be exporting a .u file
			if (unrealMapCopy.exists()) {
				noDelete = true;
			} else {
				logger.log(Level.FINE, "Creating " + unrealMapCopy.getAbsolutePath());
				unrealMapCopy.createNewFile();
				Files.copy(unrealPackage.getFileContainer(mapConverter).toPath(), unrealMapCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			List<String> globalLogLines = new ArrayList<>();
			List<String> logLines = new ArrayList<>();


			List<Type> stuffTypeList = new ArrayList<>();
			stuffTypeList.add(unrealPackage.type);

			if (unrealPackage.isMapPackage(mapConverter.getMapPackageName())) {
				stuffTypeList = Arrays.asList(Type.SOUND, Type.TEXTURE, Type.STATICMESH);
			}

			String command;
			boolean correctExit = true;

			// for map package need to export all types, else
			// at first time might export bmp only then since pkg will be marked as exported, won't export
			// other stuff such as staticmeshes, ...
			for (Type type : stuffTypeList) {
				// UE1/UE2 does not support whitespaces paths so creating .bat to execute ucc from its folder
				if (mapConverter.getInputGame().getUeVersion() <= UnrealEngine.UE2.version) {
					u1Batch = createExportFileBatch(unrealMapCopy, type);
					command = u1Batch.getAbsolutePath();
				} else {
					command = getCommandLine(unrealMapCopy.getName(), unrealPackage.type);
				}

				final Process process = Installation.executeProcess(command, logLines);

				int localExitValue = process.exitValue();

				for (String logLine : logLines) {
					// always display export logs on error
					if (localExitValue != 0) {
						logger.log(Level.WARNING, logLine);
					} else {
						logger.log(Level.FINE, logLine);
					}

					if (logLine.contains("Failed") && !logLine.contains("Warning") && !logLine.contains("Exported")) {
						String missingpackage = logLine.split("'")[2];
						logger.log(Level.SEVERE, "Impossible to export. Unreal Package " + missingpackage + " missing");
						localExitValue = 1;
					} else if (logLine.contains("Commandlet batchexport not found")) {
						logger.log(Level.SEVERE, logLine);
						localExitValue = 1;
					}
				}
				globalLogLines.addAll(logLines);
				correctExit &= (localExitValue == 0);
			}


			// Program did not work as expected
			// some ressources may have been partially extracted
			if (!correctExit) {
				logger.log(Level.SEVERE, "Export of " + unrealPackage.getFileContainer(mapConverter).getName() + " with ucc failed.");
			}


			if (!isForceSetNotExported()) {
				unrealPackage.setExported(true);
			}

			// note: do not return right now if ucc return exit error code, some packages may still have been exported
			// e.g: ONS-Dria (UT2004) exports terrain file but fails at the end -> no terrain converted

			for (String logLine : logLines) {

				// UE1 patch 227j has null chars between each char
				logLine = logLine.replaceAll("\0", "");

				// Exported Level MapCopy.MyLevel to Z:\TEMP\UT4Converter\Conversion\UT99\MyLevel.t3d
				// Exported Texture GenWarp.Sun128a to Z:\\TEMP\Sun128a.bmp
				if (logLine.contains("Exported ")) {

					File exportedFile = new File(logLine.split(" to ")[1]);
					exportedFiles.add(exportedFile);
					String ressourceName = logLine.split(" ")[2];

					UPackageRessource uRessource = unrealPackage.findRessource(ressourceName);

					if (uRessource != null) {
						uRessource.getExportInfo().addExportedFile(exportedFile);
						uRessource.parseNameAndGroup(ressourceName);
					} else {
						final List<File> exportedFilesList = new ArrayList<>();
						exportedFilesList.add(exportedFile);
						uRessource = new UPackageRessource(mapConverter, ressourceName, unrealPackage, exportedFilesList, this);
					}

					if (unrealPackage.isMapPackage(mapConverter.getMapPackageName())) {
						uRessource.setIsUsedInMap(true);
					}
				}
			}

		} finally {
			if (!noDelete && unrealMapCopy != null && unrealMapCopy.delete()) {
				logger.fine(unrealMapCopy + " unreal package file copy deleted");
			}

			if (u1Batch != null && u1Batch.delete()) {
				logger.fine(u1Batch + " batch file deleted");
			}
		}

		return exportedFiles;
	}


	/**
	 * Force ucc option. Might be used to force export to .tga (for terrain
	 * alpha map for example)
	 * 
	 * @param forcedUccOption forced UCC options
	 */
	public void setForcedUccOption(UccOptions forcedUccOption) {
		this.forcedUccOption = forcedUccOption;
	}

}
