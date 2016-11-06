package org.xtx.ut4converter.t3d;

import java.math.BigDecimal;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.tools.RGBColor;

public class HeightFog extends T3DActor {
	
	private Double extinctionDistance;
	
	private Double density;
	
	private Double startDistance;
	
	private RGBColor lightColor;

	public HeightFog(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		// TODO Auto-generated constructor stub
	}
	
	
	public boolean analyseT3DData(String line) {

		// UE3
		if (line.startsWith("StartDistance")) {
			startDistance = T3DUtils.getDouble(line);
		}
		else if (line.startsWith("Density")) {
			density = T3DUtils.getDouble(line);
		}
		else if (line.startsWith("LightColor")) {
			lightColor = T3DUtils.getRGBColor(line);
		}
		else if (line.startsWith("ExtinctionDistance")) {
			extinctionDistance = T3DUtils.getDouble(line);
		}

		else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	
	@Override
	public void scale(Double newScale) {

		if (startDistance != null) {
			startDistance *= newScale;
		}
		
		if (density != null) {
			density *= newScale;
		}
		
		if (extinctionDistance != null) {
			extinctionDistance *= newScale;
		}

		super.scale(newScale);
	}

	@Override
	public String toString() {

		if (mapConverter.toUnrealEngine4()) {
			sbf.append(IDT).append("Begin Actor Class=AtmosphericFog Name=").append(name).append("\n");
			sbf.append(IDT).append("\tBegin Object Class=BillboardComponent Name=\"Sprite\" Archetype=BillboardComponent'Default__AtmosphericFog:Sprite'\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Class=ArrowComponent Name=\"ArrowComponent0\" Archetype=ArrowComponent'Default__AtmosphericFog:ArrowComponent0'\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Class=AtmosphericFogComponent Name=\"AtmosphericFogComponent0\" Archetype=AtmosphericFogComponent'Default__AtmosphericFog:AtmosphericFogComponent0'\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Name=\"Sprite\"\n");
			sbf.append(IDT).append("\t\tAttachParent=NewDecalComponent\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Name=\"ArrowComponent0\"\n");
			sbf.append(IDT).append("\t\tAttachParent=AtmosphericFogComponent0\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Name=\"AtmosphericFogComponent0\"\n");

			if (startDistance != null) {
				sbf.append(IDT).append("\t\tStartDistance='").append(startDistance).append("\n");
			}
			else if (lightColor != null) {
				sbf.append(IDT).append("\t\t");
				lightColor.toT3D(sbf);
				sbf.append("\n");
			}
			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\t\tAtmosphericFogComponent=AtmosphericFogComponent0\n");
			sbf.append(IDT).append("\t\tArrowComponent=ArrowComponent0\n");
			sbf.append(IDT).append("\t\tSpriteComponent=Sprite\n");
			sbf.append(IDT).append("\t\tRootComponent=AtmosphericFogComponent0\n");
			writeEndActor();
		}

		return super.toString();
	}

}
