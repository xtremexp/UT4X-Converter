/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.HSVColor;
import org.xtx.ut4converter.tools.RGBColor;

/**
 *
 * @author XtremeXp
 */
public class T3DPostProcessVolume extends T3DBrush {
	
	/**
	 * MOVE to some .prop file
	 */
	final String DEFAULT_AMBIENT_CUBEMAP = "TextureCube'/Game/RestrictedAssets/Environments/Liandri/Materials/LightFunctions/Cubemap_DappledLight01.Cubemap_DappledLight01'";

	final String ZONE_INFO_CLASS = "ZoneInfo";

	/**
	 * If true will apply post process effects to whole level
	 */
	Boolean bUnbound = Boolean.FALSE;
	Film film;
	DepthOfField dof;
	AutoExposure autoExposure;
	/**
	 * Color in post process volume
	 */
	HSVColor ambientCubemapTint;
	String ambientCubemap;

	class Film {

		RGBColor tint;
		Double saturation;
		Double contrast;
		RGBColor tintShadow;
		Double tintShadowBlind;
		Double tintShadowAmount;
		Double crushShadows;

	}

	class DepthOfField {
		DOFMethod method;
		Double focalDistance;
		Double focalRegion;
		Double nearTransitionRegion;
		Double farTransitionRegion;
		Double scale;
		Double maxBokehZise;
		Double nearBlurSize;
		Double farBlurSize;
	}

	class AutoExposure {
		Float minBrightness;
		Float maxBrightness;
		Float exposureBias;
	}

	enum DOFMethod {
		Gaussian, BokedDOH
	}


	public T3DPostProcessVolume(MapConverter mapConverter, T3DZoneInfo zoneInfo) {
		super(mapConverter, zoneInfo.t3dClass, zoneInfo);

		initialise();
		forceToBox(400d);

		ambientCubemap = DEFAULT_AMBIENT_CUBEMAP;
		if (zoneInfo.distanceFogColor != null) {
			film.tint = zoneInfo.distanceFogColor;
		}

		if (zoneInfo.ambientColor != null && zoneInfo.ambientColor.V > 0) {
			
			// saturation 'reversed' in UE1/UE2
			zoneInfo.ambientColor.S = Math.abs(zoneInfo.ambientColor.S - 255);
			zoneInfo.ambientColor.V += 16;
			
			ambientCubemapTint = zoneInfo.ambientColor;
			autoExposure.minBrightness = zoneInfo.ambientColor.V / 255;
		}
	}

	private void initialise() {
		brushClass = BrushClass.PostProcessVolume;
		film = new Film();
		dof = new DepthOfField();
		autoExposure = new AutoExposure();
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
