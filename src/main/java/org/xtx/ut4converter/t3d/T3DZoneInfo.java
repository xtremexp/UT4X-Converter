/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.HSVColor;
import org.xtx.ut4converter.tools.ImageUtils;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.ue1.KillZType;
import org.xtx.ut4converter.ucore.ue1.ZoneEffect;
import static org.xtx.ut4converter.ucore.UnrealEngine.*;

/**
 * UE1/UE2 class only
 * Zone info actor used in Unreal Engine 1 and 2
 *
 * @author XtremeXp
 */
public class T3DZoneInfo extends T3DActor {

	private static final RGBColor DEFAULT_FOG_COLOR_UE2 = new RGBColor(128f, 128f, 128f, 0f, true);


	/**
	 * UE1/UE2 - default (0,255,0) = (AmbientHue,AmbientSaturation,AmbientBrightness)
	 */
	protected final HSVColor ambientColor;

	/**
	 * UE2
	 */
	private String locationName;

	/**
	 * UE2
	 */
	private String zoneTag;

	/**
	 * UE2
	 */
	private boolean bClearToFogColor;

	/**
	 * UE1 - default false - 'bFogZone'
	 * UE2 - default false - 'bDistanceFog'
	 */
	protected Boolean bFogZone;

	/**
	 * UE1 - N/A, if bFogZone, should render fog same color as ambientColor
	 * UE2 - default (128,128,128,0) = (R,G,B,A)
	 */
	protected RGBColor distanceFogColor;

	/**
	 * UE1 - N/A
	 * UE2 - default 3000 - 'distanceFogStart'
	 * UE4 - HeightFog actor -
	 */
	private double distanceFogStart;

	/**
	 * UE1 - N/A
	 * UE2 - default 8000 - 'distanceFogEnd'
	 */
	private double distanceFogEnd;

	/**
	 * UE1 - N/A
	 * UE2 - default 0 - 'distanceFogEndMin'
	 */
	private float distanceFogEndMin;

	/**
	 * UE1 - N/A
	 * UE2 - default 1 - 'distanceFogBlend'
	 */
	private float distanceFogBlend;

	/**
	 * UE1 - N/A
	 * UE2 - default -10000
	 */
	private Float killZ;

	/**
	 * UE1 - N/A
	 * UE2 - default None
	 */
	private ZoneEffect zoneEffect;

	/**
	 * UE1 - N/A - is a mix of UE1 properties bPain, bKillZone, DamagePerSec, DamageType
	 * UE2 - default KILLZ_None
	 */
	private KillZType killzType;

	public T3DZoneInfo(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);

		// UE1/UE2 main default values
		ambientColor = new HSVColor(0, 255, 0, true);

		this.killZ = -10000f;
		this.killzType = KillZType.KILLZ_None;
		this.distanceFogStart = 3000f;
		this.distanceFogEnd = 8000f;
		this.distanceFogEndMin = 0;
		this.distanceFogBlend = 1f;
		this.distanceFogColor = DEFAULT_FOG_COLOR_UE2;
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.startsWith("DistanceFogColor=")) {
			this.distanceFogColor = T3DUtils.parseRGBColor(line, true);
		}
		else if (line.startsWith("bDistanceFog=")) {
			this.bFogZone = T3DUtils.getBoolean(line);
		}
		else if (line.startsWith("bFogZone=")) {
			this.bFogZone = T3DUtils.getBoolean(line);
		}
		else if (line.startsWith("DistanceFogStart=")) {
			this.distanceFogStart = T3DUtils.getFloat(line);
		}
		else if (line.startsWith("DistanceFogEnd=")) {
			this.distanceFogEnd = T3DUtils.getFloat(line);
		}
		else if (line.startsWith("DistanceFogBlend=")) {
			this.distanceFogBlend = T3DUtils.getFloat(line);
		}
		else if (line.startsWith("ZoneTag=")) {
			this.zoneTag = T3DUtils.getString(line);
		}
		else if (line.startsWith("LocationName=")) {
			this.locationName = T3DUtils.getString(line);
		}
		else if (line.startsWith("AmbientBrightness=")) {
			this.ambientColor.V = T3DUtils.getFloat(line);
		}
		else if (line.startsWith("AmbientHue=")) {
			this.ambientColor.H = T3DUtils.getFloat(line);
		}
		else if (line.startsWith("AmbientSaturation=")) {
			this.ambientColor.S = T3DUtils.getFloat(line);
		}

		return super.analyseT3DData(line);
	}

	@Override
	public void convert() {

		// replace with post process volume if this zone info has fog or ambient color
		if (mapConverter.isFrom(UE1, UE2) && mapConverter.isTo(UE3, UE4, UE5)) {

			// in UE1/UE2 saturation is 'reversed' compared with standards, UE2 have sometimes 255+ brightness lights
			final RGBColor hsvConvertedRGBColor = ImageUtils.HSVToLinearRGB(ambientColor.H, Math.abs(ambientColor.S - 255), Math.min(ambientColor.V, 255), ambientColor.is255Range);

			// if ambient zone color, replace with postprocessvoume
			if (ambientColor.V > 0) {
				final T3DPostProcessVolume ppv = new T3DPostProcessVolume(mapConverter, this);
				ppv.ambientCubemapTint = hsvConvertedRGBColor;
				replaceWith(ppv);
			}

			// fog zone, replace with HeighFog
			if (bFogZone) {
				// UE1 has no DistanceFogColor property, so assuming it's the ambient color
				if (mapConverter.isFrom(UE1)) {
					this.distanceFogColor = hsvConvertedRGBColor;
				}

				final HeightFog heightFog = new HeightFog(mapConverter, this);
				heightFog.lightColor = this.distanceFogColor;
				heightFog.extinctionDistance = this.distanceFogEnd;
				heightFog.startDistance = this.distanceFogStart;

				replaceWith(heightFog);
			}
		}
	}

	@Override
	public void scale(double newScale) {

		distanceFogEnd *= newScale;
		distanceFogStart *= newScale;

		super.scale(newScale);
	}

	public String toT3d() {
		// replaced with postprocessvolume
		return null;
	}

	public HSVColor getAmbientColor() {
		return ambientColor;
	}
}
