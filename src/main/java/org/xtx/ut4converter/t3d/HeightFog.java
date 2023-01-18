package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.UnrealEngine;

/**
 * UE3 actor
 */
public class HeightFog extends T3DActor {

	protected Double extinctionDistance;

	private Double density;

	protected Double startDistance;

	protected RGBColor lightColor;

	public HeightFog(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	public HeightFog(MapConverter mapConverter, T3DActor actor) {
		super(mapConverter, actor.t3dClass, actor);
	}

	public boolean analyseT3DData(String line) {

		// UE3
		if (line.startsWith("StartDistance")) {
			startDistance = T3DUtils.getDouble(line);
		} else if (line.startsWith("Density")) {
			density = T3DUtils.getDouble(line);
		} else if (line.startsWith("LightColor")) {
			lightColor = T3DUtils.parseRGBColor(line, true);
		} else if (line.startsWith("ExtinctionDistance")) {
			extinctionDistance = T3DUtils.getDouble(line);
		}

		else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	@Override
	public void scale(double newScale) {

		if (startDistance != null) {
			startDistance *= newScale;
		}

		if (extinctionDistance != null) {
			extinctionDistance *= newScale;
		}

		super.scale(newScale);
	}

	public String toT3d() {

		if (mapConverter.isTo(UnrealEngine.UE4)) {

			sbf.append(IDT).append("Begin Actor Class=AtmosphericFog Name=").append(name).append("\n");
			sbf.append(IDT).append("\tBegin Object Class=BillboardComponent Name=\"Sprite\" Archetype=BillboardComponent'Default__AtmosphericFog:Sprite'\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Class=ArrowComponent Name=\"ArrowComponent0\" Archetype=ArrowComponent'Default__AtmosphericFog:ArrowComponent0'\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Class=AtmosphericFogComponent Name=\"AtmosphericFogComponent0\" Archetype=AtmosphericFogComponent'Default__AtmosphericFog:AtmosphericFogComponent0'\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Name=\"Sprite\"\n");
			sbf.append(IDT).append("\t\tAttachParent=AtmosphericFogComponent0\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Name=\"ArrowComponent0\"\n");
			sbf.append(IDT).append("\t\tAttachParent=AtmosphericFogComponent0\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Name=\"AtmosphericFogComponent0\"\n");

			if (startDistance != null) {
				sbf.append(IDT).append("\t\tStartDistance='").append(startDistance).append("\n");
			} 
			
			if (lightColor != null) {
				sbf.append(IDT).append("\t\tDefaultLightColor=").append(lightColor.toT3D(true)).append("\n");
			}
			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");

			sbf.append(IDT).append("\tAtmosphericFogComponent=AtmosphericFogComponent0\n");
			sbf.append(IDT).append("\tArrowComponent=ArrowComponent0\n");
			sbf.append(IDT).append("\tSpriteComponent=Sprite\n");
			sbf.append(IDT).append("\tRootComponent=AtmosphericFogComponent0\n");
			writeEndActor();
		}

		return super.toString();
	}

}
