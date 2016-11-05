/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.tools.FileUtils;
import org.xtx.ut4converter.tools.ImageUtils;
import org.xtx.ut4converter.tools.SoundConverter;
import org.xtx.ut4converter.tools.TextureConverter;
import org.xtx.ut4converter.tools.TextureFormat;
import org.xtx.ut4converter.tools.psk.Material;

/**
 * Some ressource such as texture, sound, ... embedded into some unreal package
 * 
 * @author XtremeXp
 */
public class UPackageRessource {

	/**
	 * Reference to map converter to know if this ressource should be converted
	 * or not ...
	 */
	MapConverter mapConverter;

	/**
	 * Unreal Package this ressource belongs to
	 */
	UPackage unrealPackage;

	/**
	 * Where this ressource have been exported. TODO handle multi export file
	 * (for textures we might need export as .bmp and .tga as well) If it's null
	 * and exportFailed is false the we should try to export it
	 */
	ExportInfo exportInfo = new ExportInfo();

	/**
	 * If true means export of this ressource failed and we must not try to
	 * export it again
	 */
	boolean exportFailed;

	/**
	 * Means this ressource is used in map being converted. If used the exported
	 * file should not be deleted otherwise should be
	 */
	private boolean isUsedInMap;

	/**
	 * Means this
	 */
	boolean isUsedInStaticMesh;

	/**
	 * Group of ressource (optional)
	 */
	public String group;

	/**
	 * Name of ressource
	 */
	public String name;

	/**
	 * Type of ressource (texture, sounds, staticmesh, mesh, ...)
	 */
	Type type;

	/**
	 * Texture dimension if type of ressource is texture
	 */
	Dimension textureDimensions;

	/**
	 * Only for material package ressources
	 */
	MaterialInfo materialInfo;
	/**
	 * Use this ressource as replacement. Might be used for material ressources
	 * which are replaced with diffuse texture (until we can manage properly
	 * materials)
	 */
	UPackageRessource replacement;

	/**
     * 
     */
	class ExportInfo {

		/**
		 * Exported files, there might be several ones for same ressource (e.g:
		 * terrain textures
		 */
		File exportedFile;

		/**
		 * Extractor used to export this package ressource
		 */
		UTPackageExtractor extractor;

		public ExportInfo() {
		}

		public ExportInfo(File exportedFile, UTPackageExtractor extractor) {
			this.exportedFile = exportedFile;
			this.extractor = extractor;
		}

		public File getExportedFile() {
			return exportedFile;
		}

		public UTPackageExtractor getExtractor() {
			return extractor;
		}
	}

	/**
	 * Creates an unreal package ressource information object.
	 * 
	 * @param fullName
	 *            Full package ressource name (e.g:
	 *            "AmbAncient.Looping.Stower51")
	 * @param type
	 *            Type of ressource (texture, sound, staticmesh, mesh, ...)
	 * @param game
	 *            UT game this ressource belongs to
	 * @param isUsedInMap
	 *            if <code>true</code> means ressource is being used
	 */
	public UPackageRessource(MapConverter mapConverter, String fullName, Type type, boolean isUsedInMap) {

		this.mapConverter = mapConverter;
		String s[] = fullName.split("\\.");

		// TODO handle brush polygon texture info
		// which only have "name" info
		// TODO move out creating upackageressource from level
		// which should be directly an unreal package
		String packageName = type != Type.LEVEL ? s[0] : fullName;

		parseNameAndGroup(fullName);

		this.type = type;
		unrealPackage = new UPackage(packageName, type, mapConverter.getInputGame(), this);
		this.isUsedInMap = isUsedInMap;
	}

	public void setPackageFile(File f) {
		unrealPackage.setFile(f);
	}

	/**
	 * Creates a package ressource
	 * 
	 * @param fullName
	 *            Full name of ressource
	 * @param uPackage
	 *            Package this ressource belongs to
	 * @param game
	 * @param ressourceType
	 *            Type of ressource (texture, sound, ...)
	 * @param isUsedInMap
	 *            <code>true<code> if this ressource is used in map that is being converted
	 */
	public UPackageRessource(MapConverter mapConverter, String fullName, Type ressourceType, UPackage uPackage, boolean isUsedInMap) {

		parseNameAndGroup(fullName);

		this.mapConverter = mapConverter;
		this.type = ressourceType;
		unrealPackage = uPackage;
		unrealPackage.addRessource(this);
		this.isUsedInMap = isUsedInMap;
	}

	/**
	 * 
	 * @param fullName
	 *            Full ressource name (e.g: "AmbAncient.Looping.Stower51")
	 * @param uPackage
	 * @param exportedFile
	 * @param extractor
	 */
	public UPackageRessource(MapConverter mapConverter, String fullName, UPackage uPackage, File exportedFile, UTPackageExtractor extractor) {

		parseNameAndGroup(fullName);

		this.mapConverter = mapConverter;
		this.exportInfo = new ExportInfo(exportedFile, extractor);

		this.unrealPackage = uPackage;
		this.type = uPackage.type;

		// if ressource in map package
		// means is always used
		if (uPackage.isMapPackage(mapConverter.getMapPackageName())) {
			setIsUsedInMap(true);
		}
		uPackage.ressources.add(this);
	}

	public void readTextureDimensions() {
		if (replacement != null) {
			readTextureDimensions(mapConverter, replacement);
		} else {
			readTextureDimensions(mapConverter, this);
		}
	}

	/**
	 * From exported texture file get the dimension of the texture Should be
	 * only called when converting polygon with the texture associated
	 */
	public static void readTextureDimensions(MapConverter mapConverter, UPackageRessource texRessource) {

		if (texRessource.type != Type.TEXTURE || texRessource.exportInfo.exportedFile == null || texRessource.textureDimensions != null) {
			return;
		}

		try {
			// FIXME this part is sloooow specially if large textures (about
			// 1s/tex which may take a while for a whole map!)
			texRessource.textureDimensions = ImageUtils.getTextureDimensions(texRessource.exportInfo.getExportedFile());

			if (texRessource.exportInfo.getExportedFile() != null && texRessource.textureDimensions != null) {
				mapConverter.getLogger().log(Level.FINE, texRessource.exportInfo.getExportedFile().getName() + " dimension read: " + texRessource.textureDimensions.toString());
			}
		} catch (Exception e) {
			mapConverter.getLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Parse group and name of ressource from full name. e.g: Full ressource
	 * name (e.g: "AmbAncient.Looping.Stower51") x) group = "Looping" x) name =
	 * "Stower51"
	 * 
	 * @param fullName
	 *            Full ressource name (e.g: "AmbAncient.Looping.Stower51")
	 */
	public void parseNameAndGroup(String fullName) {
		String s[] = fullName.split("\\.");

		// TODO handle brush polygon texture info
		// which only have "name" info
		name = s[s.length - 1];

		if (s.length == 2) {
			group = null;
		} else if (s.length == 3) {
			group = s[1];
		}
		// UT3 does not handle groups like previous unreal engines
		// e.g: "A_Gameplay.JumpPad.Cue.A_Gameplay_JumpPad_Activate_Cue"
		else if (s.length > 3) {
			group = "";

			for (int i = 1; i <= s.length - 2; i++) {
				group += s[i];

				if (i < s.length - 2) {
					group += ".";
				}
			}
		}

	}

	/**
	 * Tells if this ressource can be exported. If it has never been exported or
	 * export ever failed, it cannot be exported again
	 * 
	 * @return <code>true</code> if the file need to be exported
	 */
	public boolean needExport() {
		return !exportFailed && exportInfo.exportedFile == null;
	}

	/**
	 * Export the ressource from unreal package to file
	 * 
	 * @param packageExtractor
	 * @param forceExport
	 *            Force export of package even if it has ever been extracted
	 */
	public void export(UTPackageExtractor packageExtractor, boolean forceExport) {

		if ((needExport() || forceExport) && packageExtractor != null) {
			try {
				packageExtractor.extract(this, forceExport);
			} catch (Exception ex) {
				packageExtractor.logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			}
		}
	}

	/**
	 * Export the ressource from unreal package to file
	 * 
	 * @param packageExtractor
	 */
	public void export(UTPackageExtractor packageExtractor) {
		export(packageExtractor, false);
	}

	/**
	 * returns
	 * <pkgName>_<group>_<name>_<suffix>.<pkgName>_<group>_<name>_<suffix>
	 * 
	 * with suffix depending on type of ressource (_Mat for texture, _Cue for
	 * sound, ...)
	 * 
	 * @param mapConverter
	 * @return
	 */
	public String getConvertedBaseName(MapConverter mapConverter) {

		if (replacement != null) {
			return replacement.getConvertedBaseName(mapConverter);
		}

		String suffix = "";

		if (type == Type.SOUND) {
			// UE4 can handle both cue or normal sounds
			// but better use cue since lift sounds need volume attenuation
			// depending on player distance
			// ("AttenuationSettings=Attenuation_Lifts")
			if (mapConverter.isTo(UnrealEngine.UE3, UnrealEngine.UE4)) {
				// beware UT3 needs cue for AmbientSound but not for
				// AmbientSoundSimple actor
				suffix = "_Cue";
			}
		}

		else if (type == Type.TEXTURE) {
			suffix = "_Mat";
		}

		String baseName = getFullNameWithoutDots() + suffix;

		// have to fit base material name with max size of material names in
		// .psk
		// staticmesh files
		if (isTextureUsedInStaticMesh()) {

			// try <packagename>_<name>
			if (baseName.length() > Material.MATNAME_MAX_SIZE) {
				String fullNameWithoutGroup = getFullNameWithoutGroup().replaceAll("\\.", "\\_");
				baseName = fullNameWithoutGroup + suffix + "." + fullNameWithoutGroup + suffix;
			}

			// <name>
			if (baseName.length() > Material.MATNAME_MAX_SIZE) {
				baseName = this.name + suffix + "." + this.name + suffix;
			}
		}

		return baseName;
	}

	/**
	 * Guess the converted name used in converted t3d unreal map E.G: UT99:
	 * Returns: //
	 * /Game/Maps/<convertedmapname>/<pkgName>_<group>_<name>_<suffix
	 * >.<pkgName>_<group>_<name>_<suffix>
	 * 
	 * @param mapConverter
	 *            Map Converter
	 * @return
	 */
	public String getConvertedName(MapConverter mapConverter) {

		if (replacement != null) {
			return replacement.getConvertedName(mapConverter);
		}

		final String baseName = getConvertedBaseName(mapConverter);
		//return UTGames.UE4_FOLDER_MAP + "/" + mapConverter.getOutMapName() + "/" + baseName + "." + baseName;
		
		return mapConverter.getUt4ReferenceBaseFolder() + "/" + baseName + "." + baseName;
	}

	/**
	 * Sometimes we need to change name of exported file to have info about from
	 * which package this file comes from
	 * 
	 * @return
	 */
	public String getConvertedFileName() {
		String s[] = exportInfo.getExportedFile().getName().split("\\.");
		String currentFileExt = s[s.length - 1];

		// umodel does export staticmeshes as .pskx
		// but we want rename as .psk so we can import in blender
		if (getType() == Type.STATICMESH && "pskx".equals(currentFileExt)) {
			currentFileExt = "psk";
		}

		return getFullNameWithoutDots() + "." + currentFileExt;
	}

	/**
	 * Return the full name of package ressource
	 * 
	 * @return <packagename>.<group>.<name>
	 */
	public String getFullName() {

		if (unrealPackage.name != null && group != null && name != null) {
			return unrealPackage.name + "." + group + "." + name;
		}

		if (unrealPackage.name != null && group == null && name != null) {
			return unrealPackage.name + "." + name;
		}

		if (unrealPackage.name == null && group == null && name != null) {
			return name;
		}

		// other cases should not happen normally ...

		return "";
	}

	public String getFullNameWithoutGroup() {
		if (unrealPackage.name != null && name != null) {
			return unrealPackage.name + "." + name;
		} else {
			return "";
		}
	}

	/**
	 * Replace all dots in full name with underscores. This is used to get
	 * converted name or filename
	 * 
	 * @return
	 */
	public String getFullNameWithoutDots() {
		return getFullName().replaceAll("\\.", "_");
	}

	public String getGroupAndNameWithoutDots() {

		if (group != null) {
			return group + "_" + name;
		} else {
			return name;
		}
	}

	/**
	 * 
	 * @return Unreal Package this ressource belongs to
	 */
	public UPackage getUnrealPackage() {
		return unrealPackage;
	}

	/**
	 * 
	 * @return true if this ressource has been exported to a file
	 */
	public boolean isExported() {
		return exportInfo != null && exportInfo.exportedFile != null;
	}

	public File getExportedFile() {
		return exportInfo.getExportedFile();
	}

	public void setExportedFile(File exportedFile) {
		this.exportInfo.exportedFile = exportedFile;
	}

	/**
	 * Set this ressource as "used in map" This helps deleting unused ressources
	 * after extracting multiple ressources from single unreal package
	 * 
	 * @param isUsedInMap
	 *            true if this ressource is used in map
	 */
	public void setIsUsedInMap(boolean isUsedInMap) {

		this.isUsedInMap |= isUsedInMap;

		if (materialInfo != null && isUsedInMap) {
			materialInfo.setIsUsedInMap(true);
		}
	}

	/**
	 * 
	 * @return true if this ressource is used in the map being converted
	 */
	public boolean isUsedInMap() {
		return this.isUsedInMap;
	}

	/**
	 * Says if this ressource needs to be converted to be correctly imported in
	 * unreal engine 4. For example so old sounds from unreal 1 / ut99 are not
	 * correctly imported we need to convert them to 44k frequency
	 * 
	 * @param mc
	 *            Map Converter
	 * @return <code>true</code> true if needs conversion
	 */
	public boolean needsConversion(MapConverter mc) {

		if (exportInfo.exportedFile == null) {
			return false;
		}

		if (mc.isFromUE1UE2ToUE3UE4()) {

			// UE1/2 sounds might be 8 bit 22Khz and UE3 sounds are .ogg files
			if (type == Type.SOUND) {
				return true;
			}

			// ucc exporter exports malformed .dds textures ...
			// that can't be imported in UE4
			if (type == Type.TEXTURE && exportInfo.extractor != null && exportInfo.exportedFile.getName().endsWith(".dds") && (exportInfo.extractor instanceof UCCExporter)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Convert ressource to good format if needed
	 * 
	 * @param logger
	 * @return
	 */
	public File convert(Logger logger, UserConfig userConfig) {

		if (type == Type.SOUND) {
			SoundConverter scs = new SoundConverter(logger);

			try {
				File tempFile = File.createTempFile(getFullNameWithoutDots(), ".wav");
				scs.convertTo16BitSampleSize(exportInfo.getExportedFile(), tempFile);
				return tempFile;
			} catch (IOException ex) {
				mapConverter.getLogger().log(Level.SEVERE, null, ex);
			}
		}

		else if (type == Type.TEXTURE) {
			TextureConverter texConverter = new TextureConverter(logger, userConfig);

			if (!texConverter.isNConvertAvailable()) {
				logger.log(Level.WARNING, "Impossible to convert " + name + " texture: nconvert path not set in settings");
				return exportInfo.getExportedFile();
			}

			try {
				File tempFile = File.createTempFile(getFullNameWithoutDots(), "." + FileUtils.getExtension(exportInfo.getExportedFile()));
				Files.copy(exportInfo.getExportedFile().toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				texConverter.convert(tempFile, TextureFormat.tga);
				tempFile = FileUtils.changeExtension(tempFile, TextureFormat.tga.name());
				return tempFile;
			} catch (IOException ex) {
				mapConverter.getLogger().log(Level.SEVERE, null, ex);
			}
		}

		return null;
	}

	/**
	 * Get the type of this ressource (texture, sound, music, ...)
	 * 
	 * @return Type of ressource
	 */
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Return the dimension of the texture if ressource is a texture. Might be
	 * null if this ressource is unused in map (optimisation)
	 * 
	 * @return Dimension of texture
	 */
	public Dimension getTextureDimensions() {
		return textureDimensions;
	}

	/**
	 * Return material info Only for textures type
	 * 
	 * @return
	 */
	public MaterialInfo getMaterialInfo() {
		return materialInfo;
	}

	public void setMaterialInfo(MaterialInfo materialInfo) {
		this.materialInfo = materialInfo;
	}

	@Override
	public String toString() {
		return getFullName();
	}

	/**
	 * Replaces current resources with new one
	 * 
	 * @param ressource
	 */
	public void replaceWith(UPackageRessource ressource) {
		this.replacement = ressource;
		ressource.setIsUsedInMap(true);
	}

	public UPackageRessource getReplacement() {
		return replacement;
	}

	/**
	 * Return the simple name of the ressource E.G:
	 * Texture'HumanoidArchitecture.Bases.bas05bHA' -> bas05bHA
	 * 
	 * @return Simple name of texture
	 */
	public String getName() {
		return name;
	}

	public boolean isUsedInStaticMesh() {
		return isUsedInStaticMesh;
	}

	/**
	 * If true means the material name will have to be renamed to fit with
	 * material name max size of 64 bytes. Should be only used for texture
	 * ressources in .psk staticmeshes files
	 * 
	 * @param isUsedInStaticMesh
	 *            <code>true</code> if this ressource is used in static meshes
	 */
	public void setUsedInStaticMesh(boolean isUsedInStaticMesh) {

		// only apply for texture ressources
		if (this.type != Type.TEXTURE) {
			return;
		}
		this.isUsedInStaticMesh = isUsedInStaticMesh;
	}

	private boolean isTextureUsedInStaticMesh() {
		return this.isUsedInStaticMesh() && this.type == Type.TEXTURE;
	}
}
