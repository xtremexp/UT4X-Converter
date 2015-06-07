package org.xtx.ut4converter.ucore;

/**
 * Material info
 * @author XtremeXp
 *
 */
public class MaterialInfo {

	/**
	 * Diffuse texture or material
	 */
	UPackageRessource diffuse;
	
	/**
	 * Normal texture or material
	 */
	UPackageRessource normal;
	
	/**
	 * Specular texture or material
	 */
	UPackageRessource specular;
	
	/**
	 * Emissive texture or material
	 */
	UPackageRessource emissive;
	
	/**
	 * Spec power
	 */
	UPackageRessource specPower;
	
	/**
	 * Opacity
	 */
	UPackageRessource opacity;

	public UPackageRessource getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(UPackageRessource diffuse) {
		this.diffuse = diffuse;
	}

	public UPackageRessource getNormal() {
		return normal;
	}

	public void setNormal(UPackageRessource normal) {
		this.normal = normal;
	}

	public UPackageRessource getSpecular() {
		return specular;
	}

	public void setSpecular(UPackageRessource specular) {
		this.specular = specular;
	}

	public UPackageRessource getEmissive() {
		return emissive;
	}

	public void setEmissive(UPackageRessource emissive) {
		this.emissive = emissive;
	}

	public UPackageRessource getSpecPower() {
		return specPower;
	}

	public void setSpecPower(UPackageRessource specPower) {
		this.specPower = specPower;
	}

	public UPackageRessource getOpacity() {
		return opacity;
	}

	public void setOpacity(UPackageRessource opacity) {
		this.opacity = opacity;
	}
	
	
	public void setIsUsedInMap(boolean isUsedInMap){
		
		if(diffuse != null) diffuse.setIsUsedInMap(isUsedInMap);
		if(normal != null) normal.setIsUsedInMap(isUsedInMap);
		if(specular != null) specular.setIsUsedInMap(isUsedInMap);
		if(emissive != null) emissive.setIsUsedInMap(isUsedInMap);
		if(specPower != null) specPower.setIsUsedInMap(isUsedInMap);
		if(opacity != null) opacity.setIsUsedInMap(isUsedInMap);
	}
	
	
	
	
}
