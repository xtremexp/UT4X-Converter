package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.RGBColor;

import static org.xtx.ut4converter.ucore.UnrealEngine.UE3;
import static org.xtx.ut4converter.ucore.UnrealEngine.UE4;

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

		this.t3dClass = isTo(UE4) ? "AtmosphericFog" :"HeightFog";
		final Component fogComp = new Component(this.t3dClass + "Component", this.t3dClass + "Component0", this);

		// UE3/UE4 same prop name/comp
		if (startDistance != null) {
			fogComp.addProp("StartDistance", startDistance);
		}

		if (lightColor != null) {
			fogComp.addProp(isTo(UE4) ? "DefaultLightColor" : "LightColor", lightColor.toT3D(true));
		}

		// UE3/UE4 same prop name/comp
		if (extinctionDistance != null) {
			fogComp.addProp("ExtinctionDistance", extinctionDistance);
		}

		if (isTo(UE3)) {
			this.addComponent(new Component("SpriteComponent", "Sprite", this));
		}

		this.addComponent(fogComp);

		sbf.append(super.toT3dNew());

		return sbf.toString();
	}

}
