package org.xtx.ut4converter.ucore;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource.Type;

/**
 * Material info
 * 
 * @author XtremeXp
 *
 */
public class MaterialInfo {

	/**
	 * Diffuse texture or material
	 */
	UPackageRessource diffuse;

	/**
	 * 
	 */
	private String diffuseName;

	/**
	 * Normal texture or material
	 */
	UPackageRessource normal;

	private String normalName;

	/**
	 * Specular texture or material
	 */
	UPackageRessource specular;

	private String specularName;

	/**
	 * Emissive texture or material
	 */
	UPackageRessource emissive;

	private String emissiveName;

	/**
	 * Spec power
	 */
	UPackageRessource specPower;

	private String specPowerName;

	/**
	 * Opacity
	 */
	UPackageRessource opacity;

	private String opacityName;

	public UPackageRessource getDiffuse() {
		return diffuse;
	}

	public UPackageRessource getNormal() {
		return normal;
	}

	public UPackageRessource getSpecular() {
		return specular;
	}

	public UPackageRessource getEmissive() {
		return emissive;
	}

	public UPackageRessource getSpecPower() {
		return specPower;
	}

	public UPackageRessource getOpacity() {
		return opacity;
	}

	public void setDiffuseName(String diffuseName) {
		this.diffuseName = diffuseName;
	}

	public void setNormalName(String normalName) {
		this.normalName = normalName;
	}

	public void setSpecularName(String specularName) {
		this.specularName = specularName;
	}

	public void setEmissiveName(String emissiveName) {
		this.emissiveName = emissiveName;
	}

	public void setSpecPowerName(String specPowerName) {
		this.specPowerName = specPowerName;
	}

	public void setOpacityName(String opacityName) {
		this.opacityName = opacityName;
	}

	public void findRessourcesFromNames(MapConverter mapConverter) {

		if (diffuseName != null && diffuse == null) {
			diffuse = mapConverter.findRessourceByNameOnly(diffuseName, Type.TEXTURE);
		}

		if (normalName != null && normalName == null) {
			normal = mapConverter.findRessourceByNameOnly(normalName, Type.TEXTURE);
		}

		if (specularName != null && specularName == null) {
			specular = mapConverter.findRessourceByNameOnly(specularName, Type.TEXTURE);
		}

		if (emissiveName != null && emissiveName == null) {
			emissive = mapConverter.findRessourceByNameOnly(emissiveName, Type.TEXTURE);
		}

		if (specPowerName != null && specPowerName == null) {
			specPower = mapConverter.findRessourceByNameOnly(specPowerName, Type.TEXTURE);
		}

		if (opacityName != null && opacityName == null) {
			opacity = mapConverter.findRessourceByNameOnly(opacityName, Type.TEXTURE);
		}
	}

	public void setIsUsedInMap(boolean isUsedInMap) {

		// ressources might be ever used by some other parent ones (e.g
		// material)
		if (diffuse != null)
			diffuse.setIsUsedInMap(isUsedInMap || diffuse.isUsedInMap);
		if (normal != null)
			normal.setIsUsedInMap(isUsedInMap || normal.isUsedInMap);
		if (specular != null)
			specular.setIsUsedInMap(isUsedInMap || specular.isUsedInMap);
		if (emissive != null)
			emissive.setIsUsedInMap(isUsedInMap || emissive.isUsedInMap);
		if (specPower != null)
			specPower.setIsUsedInMap(isUsedInMap || specPower.isUsedInMap);
		if (opacity != null)
			opacity.setIsUsedInMap(isUsedInMap || opacity.isUsedInMap);
	}

}
