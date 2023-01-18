/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.RGBColor;

/**
 *
 * @author XtremeXp
 */
public class T3DPostProcessVolume extends T3DBrush {
	
	/**
	 * MOVE to some .prop file or config file
	 */
	final String UT4_AMBIENT_CUBEMAP = "TextureCube'/Game/RestrictedAssets/Environments/Liandri/Materials/LightFunctions/Cubemap_DappledLight01.Cubemap_DappledLight01'";


	/**
	 * If true will apply post process effects to whole level
	 */
	private final Boolean bUnbound = Boolean.FALSE;

	/**
	 * Color in post process volume
	 */
	protected RGBColor ambientCubemapTint;

	private final String ambientCubemap;

	public T3DPostProcessVolume(MapConverter mapConverter, T3DActor actor) {
		super(mapConverter, actor.t3dClass, actor);
		this.brushClass = BrushClass.PostProcessVolume;
		// TODO UE5 check available texture cubemap for UE5
		this.ambientCubemap = UT4_AMBIENT_CUBEMAP;
		forceToBox(400d);
	}


	public void writeProps() {

		sbf.append(IDT).append("bUnbound=").append(bUnbound).append("\n");

		if (ambientCubemap != null) {
			sbf.append(IDT).append("Settings=(bOverride_AmbientCubemapTint=True,bOverride_AmbientCubemapIntensity=True");

			if (ambientCubemapTint != null) {
				sbf.append(",AmbientCubemapTint=").append(ambientCubemapTint.toT3D(false));
			}

			sbf.append(",AmbientCubemapIntensity=2.50000"); // compensate original brightness reduced with cubemap texture
			sbf.append(",IndirectLightingIntensity=2.00000");
			sbf.append(",AmbientCubemap=").append(ambientCubemap).append(")\n");
		}
	}

}
