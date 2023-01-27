/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.t3d.T3DRessource.Type;
import org.xtx.ut4converter.tools.ImageUtils;
import org.xtx.ut4converter.tools.SoundConverter;
import org.xtx.ut4converter.tools.psk.Material;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.xtx.ut4converter.ucore.UnrealEngine.UE4;
import static org.xtx.ut4converter.ucore.UnrealEngine.UE5;

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
	private final MapConverter mapConverter;

	/**
	 * Unreal Package this ressource belongs to
	 */
	private final UPackage unrealPackage;

	/**
	 * Where this ressource have been exported. TODO handle multi export file
	 * (for textures we might need export as .bmp and .tga as well) If it's null
	 * and exportFailed is false the we should try to export it
	 */
	private ExportInfo exportInfo = new ExportInfo();

	/**
	 * If true means export of this ressource failed and we must not try to
	 * export it again
	 */
	private boolean exportFailed;

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
	private Type type;

	/**
	 * Texture dimension if type of ressource is texture
	 */
	private Dimension textureDimensions;

	/**
	 * Only for material package ressources
	 */
	private MaterialInfo materialInfo;
	/**
	 * Use this ressource as replacement. Might be used for material ressources
	 * which are replaced with diffuse texture (until we can manage properly
	 * materials)
	 */
	private UPackageRessource replacement;
	

	private String forcedFileName;

	/**
     * 
     */
	public static class ExportInfo {

		/**
		 * Exported files, there might be several ones for same ressource (e.g:
		 * terrain textures
		 */
		final List<File> exportedFiles = new ArrayList<>();

		/**
		 * Extractor used to export this package ressource
		 */
		UTPackageExtractor extractor;

		public ExportInfo() {
		}

		public ExportInfo(List<File> exportedFiles, UTPackageExtractor extractor) {
			this.exportedFiles.addAll(exportedFiles);
			this.extractor = extractor;
		}
		
		public void setExportedFile(File exportedFile){
			this.exportedFiles.add(exportedFile);
		}

		public List<File> getExportedFiles() {
			return exportedFiles;
		}

		public File getExportedFileByExtension(final String... extensions){
			if(extensions != null){
				for (String extension : extensions) {
					final File fileMatch = this.exportedFiles.stream().filter(f -> f.getName().endsWith(extension)).findFirst().orElse(null);

					if(fileMatch != null){
						return fileMatch;
					}
				}
			}

			return null;
		}

		/**
		 * Get exported file by extension
		 * @param extension Extension (e.g: "bmp")
		 * @return Exported file with that extension else null
		 */
		public File getExportedFileByExtension(final String extension){
			if(extension != null){
				return this.exportedFiles.stream().filter(f -> f.getName().endsWith(extension)).findFirst().orElse(null);
			}

			return null;
		}

		public UTPackageExtractor getExtractor() {
			return extractor;
		}
		
		public void replaceExportedFile(File exportedFile, File newExportedFile){
			if(this.exportedFiles.isEmpty()){
				return;
			}
			
			final int fileIdx = this.exportedFiles.indexOf(exportedFile);
			
			if(fileIdx != -1){
				this.exportedFiles.set(fileIdx, newExportedFile);
			}
		}

		public void addExportedFiles(List<File> exportedFiles){
			for(final File file : exportedFiles){
				addExportedFile(file);
			}
		}

		public void addExportedFile(File exportedFile) {
			if (exportedFile == null) {
				return;
			}

			if (!this.exportedFiles.contains(exportedFile)) {
				this.exportedFiles.add(exportedFile);
			}
		}

		@Override
		public String toString() {
			return "ExportInfo{" +
					"exportedFiles=" + exportedFiles +
					'}';
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
	 * @param mapConverter Map converter
	 * @param isUsedInMap
	 *            if <code>true</code> means ressource is being used
	 */
	public UPackageRessource(MapConverter mapConverter, String fullName, Type type, boolean isUsedInMap) {

		this.mapConverter = mapConverter;
		String[] s = fullName.split("\\.");

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

	/**
	 * Creates a package ressource
	 * 
	 * @param fullName
	 *            Full name of ressource
	 * @param uPackage
	 *            Package this ressource belongs to
	 * @param mapConverter Map converter
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
	 * @param uPackage Unreal package
	 * @param exportedFiles Exported files
	 * @param extractor Extractor
	 */
	public UPackageRessource(MapConverter mapConverter, String fullName, UPackage uPackage, List<File> exportedFiles, UTPackageExtractor extractor) {

		parseNameAndGroup(fullName);

		this.mapConverter = mapConverter;
		this.exportInfo = new ExportInfo(exportedFiles, extractor);

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

		if (texRessource.type != Type.TEXTURE  || texRessource.exportInfo.exportedFiles.isEmpty() || texRessource.textureDimensions != null) {
			return;
		}

		try {
			// FIXME this part is sloooow specially if large textures (about
			// 1s/tex which may take a while for a whole map!)
			texRessource.textureDimensions = ImageUtils.getTextureDimensions(texRessource.exportInfo.getExportedFiles().get(0));

			if (texRessource.exportInfo.getExportedFiles() != null && !texRessource.exportInfo.getExportedFiles().isEmpty() && texRessource.textureDimensions != null) {
				mapConverter.getLogger().log(Level.FINE, texRessource.exportInfo.getExportedFiles().get(0) + " dimension read: " + texRessource.textureDimensions.toString());
			}
		} catch (Exception e) {
			mapConverter.getLogger().log(Level.WARNING, e.getMessage());
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
		String[] s = fullName.split("\\.");

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
		return !exportFailed && exportInfo.exportedFiles.isEmpty();
	}

	/**
	 * Export the ressource from unreal package to file
	 * 
	 * @param packageExtractor Package extractor to use
	 * @param forceExport
	 *            Force export of package even if it has ever been extracted
	 */
	public void export(UTPackageExtractor packageExtractor, boolean forceExport, boolean perfectMatchOnly) {

		if (packageExtractor != null && (needExport() || forceExport || packageExtractor.isForceSetNotExported())) {
			try {
				packageExtractor.extract(this, forceExport, perfectMatchOnly);
			} catch (Exception ex) {
				packageExtractor.logger.log(Level.WARNING, ex.getLocalizedMessage(), ex);
			}
		}
	}

	
	public void export(UTPackageExtractor packageExtractor) {
		export(packageExtractor, false, true);
	}
	
	/**
	 * Export the ressource from unreal package to file
	 * 
	 * @param packageExtractor Package extractor to use for export
	 */
	public void export(UTPackageExtractor packageExtractor, boolean perfectMatchOnly) {
		export(packageExtractor, false, perfectMatchOnly);
	}

	/**
	 * returns
	 * <pkgName>_<group>_<name>_<suffix>.<pkgName>_<group>_<name>_<suffix>
	 * or <group>_<name>_<suffix>.<group>_<name>_<suffix>
	 * if it has export option by package in map converted
	 * with suffix depending on type of ressource (_Mat for texture, _Cue for
	 * sound, ...)
	 * 
	 * @return Converted base name
	 */
	public String getConvertedBaseName() {

		if (replacement != null) {
			return replacement.getConvertedBaseName();
		}

		String suffix = "";

		if (type == Type.SOUND) {
			// UE4 can handle both cue or normal sounds
			// but better use cue since lift sounds need volume attenuation
			// depending on player distance
			// ("AttenuationSettings=Attenuation_Lifts")
			if (mapConverter.isTo(UE4)) {
				suffix = "_Cue";
			}
			// in UE3 using AmbientSoundSimple as sound actor which does not require Cue
		}

		else if (type == Type.TEXTURE) {
			suffix = "_Mat";
		}

		String baseName = getFullNameWithoutDots(this.mapConverter.getExportOption() == MapConverter.ExportOption.BY_TYPE) + suffix;

		// have to fit base material name with max size of material names in
		// .psk
		// staticmesh files
		if (isTextureUsedInStaticMesh()) {

			// try <packagename>_<name>
			if (baseName.length() > Material.MATNAME_MAX_SIZE) {
				String fullNameWithoutGroup = getFullNameWithoutGroup().replaceAll("\\.", "_");
				baseName = fullNameWithoutGroup + "_" + fullNameWithoutGroup + suffix;
				forcedFileName = fullNameWithoutGroup + "_" + fullNameWithoutGroup;
			}

			// <name>
			if (baseName.length() > Material.MATNAME_MAX_SIZE) {
				baseName = this.name + "_" + this.name + suffix;
				forcedFileName = this.name + "_" + this.name;
			}

			if (baseName.length() > Material.MATNAME_MAX_SIZE) {
				final int maxNameSize = ((Material.MATNAME_MAX_SIZE - 1 - suffix.length()) / 2) - 1;
				this.name = this.name.substring(0, maxNameSize - 1);
				baseName = this.name + "_" + this.name + suffix;
				forcedFileName = this.name + "_" + this.name;
			}
		}

		return baseName;
	}

	/**
	 * Guess the converted name used in converted t3d unreal map E.G: UT99:
	 * Returns: //
	 * /Game/Maps/<convertedmapname>/<pkgName>_<group>_<name>_<suffix
	 * >.<pkgName>_<group>_<name>_<suffix>
	 *            Map Converter
	 * @return Converted name
	 */
	public String getConvertedName() {

		String convName;

		if (replacement != null) {
			convName =  replacement.getConvertedName();
		} else {

			final String baseName = getConvertedBaseName();

			//return UTGames.UE4_FOLDER_MAP + "/" + mapConverter.getOutMapName() + "/" + baseName + "." + baseName;
			if (mapConverter.isTo(UE4, UE5)) {
				// e.g: Texture=/Game/Converted/DmFith-U1/Texture/Starship_Base_sh_bs4_Mat.Starship_Base_sh_bs4_Mat
				if (mapConverter.getExportOption() == MapConverter.ExportOption.BY_PACKAGE) {
					convName = mapConverter.getUt4ReferenceBaseFolder() + "/" + this.getUnrealPackage().getName() + "/" + baseName + "." + baseName;
				}
				// e.g: Texture=/Game/Converted/DmFith-U1/Starship/Base_sh_bs4_Mat.Base_sh_bs4_Mat
				else {
					//return mapConverter.getUt4ReferenceBaseFolder() + "/" + this.type.getName() + "/" + baseName + "." + baseName;
					// no split by type for now else staticmeshes won't have textures applied (relative path)
					convName = mapConverter.getUt4ReferenceBaseFolder() + "/" + baseName + "." + baseName;
				}
			} else {
				// e.g: Texture=DM-Malevolence-UT99.Starship_Base_basic9_Mat
				convName = mapConverter.getOutMapName() + "." + baseName;
			}
		}

		if (convName != null) {
			// UT4 editor replaces "~" with "_" when importing ressources files in content browser
			// e.g: 'PlayrShp_Wall_Hullpn~1' will be imported as 'PlayrShp.Wall.Hullpn_1'
			return convName.replaceAll("~", "_");
		} else {
			return null;
		}
	}
	
	public String getConvertedFileName(File exportedFile, boolean includePackageName) {
		
		// don't change original name for .mtl file
		if(exportedFile.getName().endsWith(".mtl")){
			return exportedFile.getName();
		}
		
		String[] s = exportedFile.getName().split("\\.");
		String currentFileExt = s[s.length - 1];

		// umodel does export staticmeshes as .pskx or .psk
		// therefore UT4 converter convert them to .obj
		if (getType() == Type.STATICMESH) {
			if(mapConverter.isTo(UE4, UE5)) {
				currentFileExt = "obj";
			} else {
				currentFileExt = "ase";
			}
		}

		// used with materials whose name have been reduced to 64 max (due to psk staticmeshes)
		if (forcedFileName != null) {
			return forcedFileName.replaceAll("\\.", "_") + "." + currentFileExt;
		} else {
			return getFullNameWithoutDots(includePackageName) + "." + currentFileExt;
		}
	}

	/**
	 * Return the full name of package ressource
	 * 
	 * @return <packagename>.<group>.<name>
	 */
	public String getFullName(boolean includePackageName) {

		if (includePackageName) {
			if (unrealPackage.name != null && group != null && name != null) {
				return unrealPackage.name + "." + group + "." + name;
			}

			if (unrealPackage.name != null && group == null && name != null) {
				return unrealPackage.name + "." + name;
			}
		} else {
			if (group != null && name != null) {
				return group + "." + name;
			} else if (group == null && name != null) {
				return name;
			}
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
	 * @return Full name without dots
	 */
	public String getFullNameWithoutDots(boolean includePackageName) {
		return getFullName(includePackageName).replaceAll("\\.", "_");
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
		return exportInfo != null && !exportInfo.exportedFiles.isEmpty();
	}

	public List<File> getExportedFiles() {
		return exportInfo.getExportedFiles();
	}


	public void addExportedFiles(File... exportedFileList) {
		for (File exportedFile : exportedFileList) {
			addExportedFile(exportedFile);
		}
	}

	public void addExportedFile(File exportedFile) {
		if (exportedFile != null && exportedFile.length() > 0) {
			this.exportInfo.addExportedFile(exportedFile);
		}
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
	 * Convert ressource to good format if needed for UE3 or UE4
	 * 
	 * @param logger Logger
	 * @return <code>null</code> If no conversion was needed, else file converted
	 */
	public File convertResourceIfNeeded(Logger logger) {

		try {
			if (exportInfo.exportedFiles.isEmpty()) {
				return null;
			}

			// UE1/UE2 sounds sometimes are 8 bits
			// UE3+ only supports 16 bits sounds so need to convert
			if (type == Type.SOUND && this.mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {
				SoundConverter scs = new SoundConverter(logger);
				File tempFile = File.createTempFile(getFullNameWithoutDots(true), ".wav");
				scs.convertTo16BitSampleSize(exportInfo.getExportedFiles().get(0), tempFile);
				return tempFile;
			}
			// UE1 supported formats: .bmp, .pcx
			// UE2 supported formats: .bmp, .pcx, .tga, .upt, .dds
			// UE3 supported formats: .bmp, .pcx, .tga, .float, .psd
			// UE4 supported formats: .bmp, .pcx, .tga, .jpeg/.jpg, .png, .dds, .exr
			// UE5 supported formats: .bmp, .pcx, .tga, .jpeg/.jpg, .png, .dds, .exr, .tif/.tiff, .psd
			// .dds textures exported from UE2 (via 'ucc.exe batchexport' cmd) cannot be read by UE4+
			else if (type == Type.TEXTURE) {

				if (getExportedFiles().get(0).getName().endsWith(".dds") || getExportedFiles().get(0).getName().endsWith(".bmp")) {

					String extFormat = "png";

					// UE3 does not support .png for import
					// For UT3 need to resave to bmp because Unreal 2 export textures in an unsupported format
					if (mapConverter.isTo(UnrealEngine.UE3)) {
						extFormat = "bmp";
					}

					File tempFile = File.createTempFile(getFullNameWithoutDots(true), "." + extFormat);
					final BufferedImage img = ImageIO.read(exportInfo.getExportedFiles().get(0));
					ImageIO.write(img, extFormat, tempFile);

					return tempFile;
				}
			}
			// TODO handle type MESH (.3d file)
		} catch (IOException ex) {
			mapConverter.getLogger().log(Level.WARNING, null, ex);
			throw new RuntimeException(ex);
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
	 * For testing purpose only
	 * @param textureDimensions Texture dimensions
	 */
	public void setTextureDimensions(Dimension textureDimensions) {
		this.textureDimensions = textureDimensions;
	}

	/**
	 * Return material info Only for textures type
	 * 
	 * @return Material info
	 */
	public MaterialInfo getMaterialInfo() {
		return materialInfo;
	}

	public void setMaterialInfo(MaterialInfo materialInfo) {
		this.materialInfo = materialInfo;
	}

	@Override
	public String toString() {
		return getFullName(true);
	}

	/**
	 * Replaces current resources with new one
	 * 
	 * @param ressource Ressource to replace with
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

	public ExportInfo getExportInfo() {
		return exportInfo;
	}
	
	
}
