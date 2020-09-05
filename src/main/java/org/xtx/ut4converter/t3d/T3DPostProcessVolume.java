/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.HSVColor;

/**
 *
 * @author XtremeXp
 */
public class T3DPostProcessVolume extends T3DBrush {
	
	/**
	 * MOVE to some .prop file
	 */
	final String DEFAULT_AMBIENT_CUBEMAP = "TextureCube'/Game/RestrictedAssets/Environments/Liandri/Materials/LightFunctions/Cubemap_DappledLight01.Cubemap_DappledLight01'";


	/**
	 * If true will apply post process effects to whole level
	 */
	private Boolean bUnbound = Boolean.FALSE;

	/**
	 * Color in post process volume
	 */
	private HSVColor ambientCubemapTint;
	private String ambientCubemap;



	public T3DPostProcessVolume(MapConverter mapConverter, T3DZoneInfo zoneInfo) {
		super(mapConverter, zoneInfo.t3dClass, zoneInfo);

		initialise();
		forceToBox(400d);

		ambientCubemap = DEFAULT_AMBIENT_CUBEMAP;


		if (zoneInfo.getAmbientColor() != null && zoneInfo.getAmbientColor().V > 0) {
			
			// saturation 'reversed' in UE1/UE2
			zoneInfo.getAmbientColor().S = Math.abs(zoneInfo.getAmbientColor().S - 255);
			zoneInfo.getAmbientColor().V += 16;
			
			ambientCubemapTint = zoneInfo.getAmbientColor();
		}
	}

	private void initialise() {
		brushClass = BrushClass.PostProcessVolume;
	}

	public void writeProps() {

		sbf.append(IDT).append("bUnbound=").append(bUnbound).append("\n");

		if (ambientCubemap != null) {
			sbf.append(IDT).append("Settings=(bOverride_AmbientCubemapTint=True,bOverride_AmbientCubemapIntensity=True");

			if (ambientCubemapTint != null) {
				sbf.append(",AmbientCubemapTint=");
				T3DUtils.writeRGBColor(sbf, ambientCubemapTint);
			}

			sbf.append(",AmbientCubemapIntensity=2.50000"); // compensate original brightness reduced with cubemap texture
			sbf.append(",IndirectLightingIntensity=2.00000");
			sbf.append(",AmbientCubemap=").append(ambientCubemap).append(")\n");
		}
	}

}
