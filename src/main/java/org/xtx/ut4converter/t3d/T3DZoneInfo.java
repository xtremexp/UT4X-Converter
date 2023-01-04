/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.tools.HSVColor;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.ue1.KillZType;
import org.xtx.ut4converter.ucore.ue1.ZoneEffect;

/**
 * Zone info actor used in Unreal Engine 1 and 2
 * 
 * @author XtremeXp
 */
public class T3DZoneInfo extends T3DActor {

	private HSVColor ambientColor;

	private String locationName;
	private String zoneTag;

	private Boolean bClearToFogColor;
	private Boolean bTerrainZone;
	private Boolean bDistanceFog;
	private Boolean bFogZone;

	private RGBColor distanceFogColor;
	private Double distanceFogStart;
	private Double distanceFogEnd;
	private Double distanceFogBlend;
	private Double killZ;

	private ZoneEffect zoneEffect;

	private final KillZType killzType = KillZType.KILLZ_None;

	public T3DZoneInfo(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	@Override
	public boolean analyseT3DData(String line) {

		// DistanceFogColor=(B=172,G=202,R=221)
		if (line.startsWith("DistanceFogColor=")) {
			distanceFogColor = T3DUtils.getRGBColor(line);
		}

		else if (line.startsWith("bDistanceFog=")) {
			bDistanceFog = T3DUtils.getBoolean(line);
		}

		else if (line.startsWith("bTerrainZone=")) {
			bTerrainZone = T3DUtils.getBoolean(line);
		}

		else if (line.startsWith("bFogZone=")) {
			bFogZone = T3DUtils.getBoolean(line);
		}

		else if (line.startsWith("DistanceFogStart=")) {
			distanceFogStart = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("DistanceFogEnd=")) {
			distanceFogEnd = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("DistanceFogBlend=")) {
			distanceFogBlend = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("ZoneTag=")) {
			zoneTag = T3DUtils.getString(line);
		}

		else if (line.startsWith("LocationName=")) {
			locationName = T3DUtils.getString(line);
		}

		else if (line.startsWith("AmbientBrightness=")) {
			ambientColor = ambientColor != null ? ambientColor : HSVColor.getDefaultUE12Color();
			ambientColor.V = T3DUtils.getFloat(line);
		}

		else if (line.startsWith("AmbientHue=")) {
			ambientColor = ambientColor != null ? ambientColor : HSVColor.getDefaultUE12Color();
			ambientColor.H = T3DUtils.getFloat(line);
		}

		else if (line.startsWith("AmbientSaturation=")) {
			ambientColor = ambientColor != null ? ambientColor : HSVColor.getDefaultUE12Color();
			ambientColor.S = T3DUtils.getFloat(line);
		}

		return super.analyseT3DData(line);
	}

	@Override
	public void convert() {

		if (distanceFogColor != null) {
			distanceFogColor.toOneRange();
		}
		
		if (mapConverter.isTo(UnrealEngine.UE3, UnrealEngine.UE4) && (distanceFogColor != null || ambientColor != null)) {

			// replace with postprocess volume if light or fog info set
				T3DPostProcessVolume ppv = new T3DPostProcessVolume(mapConverter, this);
				replaceWith(ppv);
		}
	}

	@Override
	public void scale(double newScale) {

		if (distanceFogEnd != null) {
			distanceFogEnd *= newScale;
		}

		if (distanceFogStart != null) {
			distanceFogStart *= newScale;
		}
		
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
