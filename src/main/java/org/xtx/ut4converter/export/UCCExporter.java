/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import com.sun.istack.internal.logging.Logger;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackage;
import org.xtx.ut4converter.ucore.UPackageRessource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	 * User game configuration used to get UT game path and so Unreal Package
	 * extractor as well
	 */
	protected UserGameConfig userGameConfig;

	/**
	 * File path of ucc.exe or ut3.com program. Depends of user game settings
	 */
	private File uccExporterPath;

	private UccOptions forcedUccOption;

	@Override
	public boolean supportLinux() {
		// oldunreal.com u1 patch as some UCCLinux.bin file (but is optional
		// during patch install)
		return mapConverter.getInputGame() == UTGame.U1;
	}

	@Override
	public String getName() {
		return "UCC";
	}

	@Override
	public UnrealEngine[] getSupportedEngines() {
		return new UnrealEngine[] { UTGames.UnrealEngine.UE1, UTGames.UnrealEngine.UE2 };
	}

	/**
	 * Exporter names
	 */
	private enum Name {

		UCC_EXE("ucc.exe"),
		/**
		 * Linux version of ucc (Unreal 1 only)
		 */
		UCC_BIN("UCCLinux.bin"),
		/**
		 * Unreal Tournament 3
		 */
		UT3_COM("ut3.com"),
		/**
		 * UDK
		 */
		UDK_COM("UDK.com");

		/**
		 * Filename of exporter
		 */
		private String programName;

		Name(String programName) {
			this.programName = programName;
		}

		@Override
		public String toString() {
			return this.programName;
		}
	}

	/**
	 * Exporter options of embedded UT extractor for Unreal Packages
	 */
	public enum UccOptions {

		UNKNOWN("UNKNOWN"), // fake option so will make crash export
		LEVEL_T3D("Level t3d"), SOUND_WAV("Sound wav"), MUSIC_XM("Music xm"), MUSIC_S3M("Music s3m"), // todo
		// check
		// might
		// not
		// always
		// be
		// s3m
		// but
		// it
		// or
		// xm
		TEXTURE_DDS("Texture dds"), TEXTURE_BMP("Texture bmp"), // mainly for
																// terrain alpha
																// map
		TEXTURE_TGA("Texture tga"), // not working good always 0 bytes created
									// files ... (tested UT2004)
		TEXTURE_PCX("Texture pcx"), // for U1, UT99
		STATICMESH_T3D("StaticMesh t3d"), CLASS_UC("Class uc");

		String option;

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
	private UccOptions getUccOptions(Type type, UTGames.UnrealEngine engine) {

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

			if (engine == UTGames.UnrealEngine.UE2) {
				return UccOptions.TEXTURE_DDS;
			}

			else if (engine == UTGames.UnrealEngine.UE1) {
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

		userGameConfig = mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame());
		uccExporterPath = getExporterPath();
	}

	/**
	 * 
	 * @param mapConverter
	 * @return
	 */
	public static UCCExporter getInstance(MapConverter mapConverter) {

		UserGameConfig userGameConfig = mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame());

		if (userGameConfig == null) {
			return null;
		} else {
			return new UCCExporter(mapConverter);
		}
	}

	@Override
	public Set<File> extract(UPackageRessource ressource, boolean forceExport, boolean perfectMatchOnly) throws Exception {

		// Ressource ever extracted, we skip ...
		if ((!forceExport && ressource.isExported()) || ressource.getUnrealPackage().getName().equals("null") || (!forceExport && ressource.getUnrealPackage().isExported())) {
			return null;
		}

		if (userGameConfig.getPath() == null || !userGameConfig.getPath().exists()) {
			logger.log(Level.SEVERE, "Game path not set or does not exists in user settings for game " + mapConverter.getInputGame().name);
			return null;
		}

		if (!uccExporterPath.exists()) {

			// For Unreal 1, by default ucc.exe program is not embedded, need
			// download latest patch from www.oldunreal.com !
			if (mapConverter.getOutputGame() == UTGames.UTGame.U1) {
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
			Logger.getLogger(UCCExporter.class).warning("Impossible to export");
			return null;
		}

		UPackageRessource t3dRessource = new UPackageRessource(mapConverter, unrealMap.getAbsolutePath(), Type.LEVEL, true);
		UCCExporter ucE = new UCCExporter(mapConverter);

		Set<File> files = ucE.extract(t3dRessource, false, true);

		// UT3.com or UDK.com does not give info about t3d exported file in logs
		// but is always PersistentLevel.t3d in Binaries folder
		if (mapConverter.getInputGame() == UTGame.UT3 || mapConverter.getInputGame() == UTGame.UDK) {
			final File binariesFolder = UTGames.getBinariesFolder(mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame()).getPath(), mapConverter.getInputGame());
			return new File(binariesFolder + File.separator + UTGames.T3D_LEVEL_NAME_UE3);
		}
		else {
			return !files.isEmpty() ? files.iterator().next() : null;
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

		if (userGameConfig == null) {
			return null;
		}

		if (mapConverter.getInputGame().engine.version < UTGames.UnrealEngine.UE3.version) {
			String basePathUcc = userGameConfig.getPath() + File.separator + "System" + File.separator;

			if (supportLinux() && Installation.isLinux()) {
				basePathUcc += Name.UCC_BIN;
			} else {
				basePathUcc += Name.UCC_EXE;
			}

			return new File(basePathUcc);
		}

		// UT3
		else if (mapConverter.getInputGame() == UTGames.UTGame.UT3) {
			return new File(userGameConfig.getPath() + File.separator + "Binaries" + File.separator + Name.UT3_COM);
		}

		// UDK
		else if (mapConverter.getInputGame() == UTGames.UTGame.UDK) {
			return new File(UTGames.getBinariesFolder(userGameConfig.getPath(), UTGame.UDK) + File.separator + Name.UDK_COM);
		}

		// UT4 TODO CHECK if exporter does exists in command line
		else {
			throw new UnsupportedOperationException("Unsupported UCC exporter for Unreal Engine " + mapConverter.getInputGame().engine.name());
		}
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

		UTGames.UnrealEngine inEngine = mapConverter.getInputGame().engine;

		if (inEngine.version <= UTGames.UnrealEngine.UE2.version) {
			return uccExporterPath.getName() + " batchexport  " + fileName + " " + getUccOptions(type, inEngine) + " \"" + getExportFolder(type) + "\"";
		}

		else {
			return "\"" + uccExporterPath.getAbsolutePath() + "\" batchexport  " + fileName + " " + getUccOptions(type, inEngine);
		}
	}

	/**
	 * 
	 * @param unrealPackage
	 * @return Exported file for the ressource. If null means that ucc exported
	 *         was not able to export the ressource.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private Set<File> exportPackage(UPackage unrealPackage) throws IOException, InterruptedException {

		File unrealMapCopy = null;
		File u1Batch = null;
		File gamePath = userGameConfig.getPath();
		Set<File> exportedFiles = new HashSet<>();

		boolean noDelete = false;

		try {
			logger.log(
					Level.INFO,
					"Exporting " + unrealPackage.getFileContainer(mapConverter).getName() + " " + unrealPackage.type.name() + " package (mode: "
							+ getUccOptions(unrealPackage.type, mapConverter.getInputGame().engine) + ")");

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

			List<String> logLines = new ArrayList<>();

			String command;

			// For unreal 1 or ut99 we do need to create a batch file
			// because ucc.exe don't work if executing itself with parent
			// folders with whitespaces in name
			// TODO use only if whitespaces in ucc.exe or map folder name
			if (mapConverter.getInputGame().engine.version <= UTGames.UnrealEngine.UE2.version) {
				u1Batch = createExportFileBatch(unrealMapCopy, unrealPackage.type);
				command = u1Batch.getAbsolutePath();
			} else {
				command = getCommandLine(unrealMapCopy.getName(), unrealPackage.type);
			}

			int exitValue = Installation.executeProcess(command, logLines);

			// Program did not work as expected
			// some ressources may have been partially extracted
			if (exitValue != 0) {
				logger.log(Level.SEVERE, "Full export for " + unrealPackage.getFileContainer(mapConverter).getName() + " failed with ucc.exe batchexport");
			}

			if (!isForceSetNotExported()) {
				unrealPackage.setExported(true);
			}

			for (String logLine : logLines) {

				logger.log(Level.FINE, logLine);

				if (logLine.contains("Failed") && !logLine.contains("Warning")) {
					String missingpackage = logLine.split("\\'")[2];
					logger.log(Level.SEVERE, "Impossible to export. Unreal Package " + missingpackage + " missing");
					return exportedFiles;
				}

				else if (logLine.contains("Commandlet batchexport not found")) {
					logger.log(Level.SEVERE, logLine);
					return exportedFiles;
				}

				// Exported Level MapCopy.MyLevel to
				// Z:\TEMP\UT4Converter\Conversion\UT99\MyLevel.t3d
				// Exported Texture GenWarp.Sun128a to Z:\\TEMP\Sun128a.bmp
				else if (logLine.contains("Exported ")) {

					File exportedFile = new File(logLine.split(" to ")[1]);
					exportedFiles.add(exportedFile);
					String ressourceName = logLine.split("\\ ")[2];

					UPackageRessource uRessource = unrealPackage.findRessource(ressourceName);

					if (uRessource != null) {
						uRessource.getExportInfo().addExportedFile(exportedFile);
						uRessource.parseNameAndGroup(ressourceName); // for
																		// texture
																		// db
																		// that
																		// don't
																		// have
																		// group
																		// we
																		// retrieve
																		// the
																		// group
																		// ...
					} else {
						final List<File> exportedFilesList = new ArrayList<>();
						exportedFilesList.add(exportedFile);
						new UPackageRessource(mapConverter, ressourceName, unrealPackage, exportedFilesList, this);
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
	 * For testing export of map to t3d unreal text map ...
	 */
	public static void test() {

		File unrealMap = new File("Z:\\TEMP\\UT99Maps\\AS-Mazon.unr");

		MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, unrealMap, null);

		try {
			UCCExporter.exportLevelToT3d(mc, unrealMap);
		} catch (Exception e) {
			System.exit(-1);
		}

		System.exit(0);
	}

	/**
	 * Force ucc option. Might be used to force export to .tga (for terrain
	 * alpha map for example)
	 * 
	 * @param forcedUccOption
	 */
	public void setForcedUccOption(UccOptions forcedUccOption) {
		this.forcedUccOption = forcedUccOption;
	}

}
