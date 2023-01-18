/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.t3d.ue1.UBLight;
import org.xtx.ut4converter.tools.ImageUtils;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.xtx.ut4converter.ucore.UnrealEngine.*;

/**
 * Class for converting lights.
 *
 * @author XtremeXp TODO handle "light skins" (coronas)
 */
public class T3DLight extends T3DSound {

	/**
	 * Light Effect Used in unreal engine 1 / 2
	 */
	enum UE12_LightEffect {
		LE_None, LE_TorchWaver, LE_FireWaver, LE_Negative, LE_WateryShimmer, LE_Searchlight, LE_SlowWave, LE_FastWave, LE_CloudCast, LE_StaticSpot, LE_Shock, LE_Disco, LE_Warp, LE_Spotlight, LE_NonIncidence, LE_Shell, LE_OmniBumpMap, LE_Interference, LE_Cylinder, LE_Rotor,
		// Unreal Engine 2 new light effects
		LE_Sunlight,  LE_Unused, LE_Spotlight2, LE_SquareSpotlight, LE_QuadraticNonIncidence
	}

	/**
	 * Light Type UE 1 / 2
	 */
	public enum UE12_LightType {
		LT_None, LT_Steady, LT_Pulse, LT_Blink, LT_Flicker, LT_Strobe, LT_BackdropLight, LT_SubtlePulse, LT_TexturePaletteLoop, LT_TexturePaletteOnce,
		/**
		 * Duke Nukem Forever
		 */
		LT_StringLight
	}

	/**
     *
     */
	enum UE4_Mobility {
		Static, // don't move and don't change color
		Stationary, // don't move but can change of color and switch off/on
		Movable // can move and change color
	}

	/**
	 * Light actors for UT99. Basically are normal lights with either some skin
	 * or special light effect + type TODO move out this to some "core class"
	 */
	public enum UE12_LightActors {
		Light, ChargeLight, DistanceLightning, FlashLightBeam, OverHeatLight, QueenTeleportLight, SightLight, Spotlight, TorchFlame, TriggerLight, TriggerLightRad, WeaponLight, EffectLight, PurpleLight,
		// Unreal Engine 2 new light actors
		Sunlight
	}

	/**
	 *
	 */
	public enum UE3_LightActor {

		DirectionalLight, DirectionalLightToggleable,
		/**
		 *
		 */
		PointLight,
		/**
		 * For UE4: PointLight with mobility Movable
		 */
		PointLightMovable,
		/**
		 * is PointLight
		 */
		PointLightToggleable, SkyLight, SkyLightToggleable, SpotLight, SpotLightToggleable, SpotLightMovable
	}

	/**
	 * Unreal Engine 4 light actors TODO check UE3 (might be same)
	 */
	public enum UE4_LightActor {
		PointLight, SkyLight, SpotLight, DirectionalLight
	}

	/**
	 * Default light effect Used in Unreal Engine 1 / 2
	 */
	private UE12_LightEffect lightEffect = UE12_LightEffect.LE_None;

	/**
	 * Default light type (TODO check isdefaut) Used in Unreal Engine 1 / 2
	 */
	private UE12_LightType lightType = UE12_LightType.LT_Steady;

	/**
	 * Skins of light for corona
	 */
	private List<UPackageRessource> skins;


	/**
	 * UE1/2: LightCone - default 128 UE4: OuterConeAngle - default 44
	 */
	private Double outerConeAngle;


	private boolean isCorona;

	/**
	 * How bright color is, in UE3+ it's how bright the light is
	 * UE1/UE2: default 64 - 'LightBrightness' - Range 0->255 (note: UE2 has range 0->Infinite)
	 * UE3: default 1 - 'Brightness' (note correlated with color) -> replaced with 'intensity' in UE4+
	 */
	private float brightness;

	/**
	 * Hue of the light
	 * UE1/2: default 0 - 'LightHue' - Range 0->255 (White->Red)
	 * UE3+: N/A replaced with RGB
	 */
	private float hue;

	/**
	 * Light saturation
	 * UE1/2 saturation: Range 0->255 (default: 255)
	 * UE3+: N/A replaced with RGB
	 * The higher saturation is, the more light looks white (no matter if hue is not white)
	 */
	private float saturation;

	/**
	 * How far the light goes
	 * UE1/UE2: default 64 - 'LightRadius' (note: real light radius in game = radius * 32 = 2048)
	 * UE3: default 1024 - 'Radius'
	 * UE4+: default 1000 - 'AttenuationRadius'
	 */
	private float radius;

	// *** Unreal Engine 3 / 4 Properties ***

	/**
	 * UE3+: default 255 - Range [0->1]
	 */
	private RGBColor rgbColor;

	/**
	 * UE3+: default 0 - Range [0->1]
	 */
	private float alpha = 0;

	/**
	 * UE4+ Default attenuation radius / radius
	 */
	private static final float UE4_DEFAULT_ATTENUATION_RADIUS = 1000f;

	/**
	 * UE3+: default 1 - 'Brightness'
	 * UE4+: default 5000 - 'Intensity'
	 */
	private Float intensity;

	/**
	 * UE3: default 2 - 'FalloffExponent'
	 * UE4+: default 8 - 'LightFalloffExponent'
	 */
	private Double lightFalloffExponent;

	/**
	 * UE1/2/3: N/A
	 * UE4+: default 0 - 'SourceRadius'
	 */
	private float sourceRadius;

	private UE4_Mobility mobility = UE4_Mobility.Static;

	/**
	 * As seen in UT2004 and UE1
	 * Overrides spot light class to directional light if true
	 */
	private Boolean isDirectional = Boolean.FALSE;

	/**
	 * @param mc       Map converter instance
	 * @param t3dClass T3d class
	 */
	public T3DLight(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);

		// UE1/UE2 default values for lights
		// default light color is white
		if (mc.isFrom(UnrealEngine.UE1, UE2)) {
			this.hue = 0;
			this.saturation = 255f;
			this.brightness = 64f;
			this.radius = 64f;
		}
		// UE3 default values for lights
		else if (mc.isFrom(UnrealEngine.UE3)) {
			this.radius = 1024f;
			this.brightness = 1f;
			this.lightFalloffExponent = 2d;
		}
		// UE4 default values for lights
		else if (mc.isFrom(UnrealEngine.UE4, UnrealEngine.UE5)) {
			this.radius = 1000;
			// for UE5 with IntensityUnits=Unitless else 8.f for Candela
			this.intensity = 5000f;
		}

		// UE3+, default light color is white
		if (mc.isFrom(UE3, UE4, UE5)) {
			this.rgbColor = new RGBColor(255, 255, 255, 0, true);
		}
	}

	@Override
	public boolean analyseT3DData(String line) {

		// TODO skip this if UT3 and class not a light class
		// unlike in UE1/UE2 all actors do not have lightning properties in UT3

		// UE1/2, UE3
		if (line.startsWith("LightBrightness") || line.startsWith("Brightness")) {
			brightness = T3DUtils.getFloat(line);
		}

		else if (line.startsWith("LightHue")) {
			hue = T3DUtils.getFloat(line);
		}

		else if (line.startsWith("LightSaturation")) {
			saturation = T3DUtils.getFloat(line);
		}

		// UE1/2, UE3
		else if (line.startsWith("LightRadius") || line.startsWith("Radius")) {
			radius = T3DUtils.getFloat(line);
		}

		else if (line.startsWith("LightEffect")) {
			lightEffect = UE12_LightEffect.valueOf(line.split("=")[1]);
		}

		else if (line.startsWith("LightType")) {
			lightType = UE12_LightType.valueOf(line.split("=")[1]);
		}

		else if (line.startsWith("LightCone") || line.startsWith("OuterConeAngle")) {
			outerConeAngle = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("bCorona=")) {
			isCorona = "true".equalsIgnoreCase(line.split("=")[1]);
		}

		else if (line.startsWith("SourceRadius=")) {
			this.sourceRadius = T3DUtils.getFloat(line);
		}

		// UT3
		// LightColor=(B=58,G=152,R=197,A=0)
		else if (line.startsWith("LightColor=")) {
			this.rgbColor = T3DUtils.parseRGBColor(line, this.mapConverter.isFrom(UE1, UE2, UE3));
		}

		else if (line.startsWith("FalloffExponent=") || line.startsWith("LightFalloffExponent")) {
			lightFalloffExponent = T3DUtils.getDouble(line);
		}

		// Skin=Texture'GenFX.LensFlar.3'
		else if (line.startsWith("Skin=") || line.startsWith("Skins(")) {
			if(this.skins == null){
				this.skins = new ArrayList<>();
			}

			this.skins.add(mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.TEXTURE));
		}

		// seen in ut2004 overrides spot light type ?
		else if (line.startsWith("bDirectional=")) {
			this.isDirectional = T3DUtils.getBoolean(line);
		}

		else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	/**
	 * Tell if this light is spotlight or not
	 *
	 * @return <code>true</code> if light is a spot light
	 */
	private boolean isSpotLight() {

		return t3dClass.equalsIgnoreCase(UE12_LightActors.Spotlight.name())
				|| lightEffect == UE12_LightEffect.LE_Spotlight || lightEffect == UE12_LightEffect.LE_StaticSpot
				|| lightEffect == UE12_LightEffect.LE_Spotlight2 || lightEffect == UE12_LightEffect.LE_SquareSpotlight;
	}

	/**
	 * Tells if current light is sunlight if true
	 *
	 * @return <code>true</code> if light is a sun light
	 */
	private boolean isSunLight() {
		return lightEffect == UE12_LightEffect.LE_Sunlight || t3dClass.equals(UE12_LightActors.Sunlight.name());
	}

	public UE12_LightType getLightType() {
		return lightType;
	}

	/**
	 * Converts mobility class
	 */
	private void convertClassAndMobility() {

		if (mapConverter.isFrom(UnrealEngine.UE3)) {
			convertUE3ClassAndMobility();
		} else if (mapConverter.isFrom(UnrealEngine.UE1, UE2)) {
			convertUE12ClassAndMobility();
		} else {
			t3dClass = UE4_LightActor.PointLight.name();
		}
	}

	/**
	 * Convert UE1/2 light class and mobility to UE4
	 */
	private void convertUE12ClassAndMobility() {

		if (isSpotLight()) {
			this.t3dClass = UE4_LightActor.SpotLight.name();
		} else if (!isSpotLight() && (isSunLight() || (this.isDirectional != null && this.isDirectional))) {
			this.t3dClass = UE4_LightActor.DirectionalLight.name();
		} else {
			this.t3dClass = UE4_LightActor.PointLight.name();
		}
	}

	/**
	 * Convert UE3 light class and mobility to UE4
	 */
	private void convertUE3ClassAndMobility() {
		if (t3dClass.equals(UE3_LightActor.DirectionalLight.name()) || t3dClass.equals(UE3_LightActor.DirectionalLightToggleable.name())) {

            if (t3dClass.equals(UE3_LightActor.DirectionalLightToggleable.name())) {
                mobility = UE4_Mobility.Stationary;
            }

            t3dClass = UE4_LightActor.DirectionalLight.name();

        } else if (t3dClass.equals(UE3_LightActor.PointLight.name()) || t3dClass.equals(UE3_LightActor.PointLightToggleable.name()) || t3dClass.equals(UE3_LightActor.PointLightMovable.name())) {

            if (t3dClass.equals(UE3_LightActor.PointLightMovable.name())) {
                mobility = UE4_Mobility.Movable;
            } else if (t3dClass.equals(UE3_LightActor.PointLightToggleable.name())
					|| this instanceof T3DTriggerLight
					|| (this instanceof UBLight && this.getLightType() != UE12_LightType.LT_Steady)) {
                mobility = UE4_Mobility.Stationary;
            }

            t3dClass = UE4_LightActor.PointLight.name();

        } else if (t3dClass.equals(UE3_LightActor.SkyLight.name()) || t3dClass.equals(UE3_LightActor.SkyLightToggleable.name())) {
            t3dClass = UE4_LightActor.SkyLight.name();

        } else if (t3dClass.equals(UE3_LightActor.SpotLight.name()) || t3dClass.equals(UE3_LightActor.SpotLightMovable.name()) || t3dClass.equals(UE3_LightActor.SpotLightToggleable.name())) {

            if (t3dClass.equals(UE3_LightActor.SpotLightMovable.name())) {
                mobility = UE4_Mobility.Movable;
            } else if (t3dClass.equals(UE3_LightActor.SpotLightToggleable.name())) {
                mobility = UE4_Mobility.Stationary;
            }

            t3dClass = UE4_LightActor.SpotLight.name();
        } else {
            t3dClass = UE4_LightActor.PointLight.name();
        }
	}


	/**
	 *
	 * @return T3D
	 */
	public String toT3d() {

		String componentLightClass;

		if (UE4_LightActor.SkyLight.name().equals(t3dClass)) {
			componentLightClass = "SkyLightComponent";
		} else if (isSpotLight()) {
			componentLightClass = "SpotLightComponent";
		} else if (UE4_LightActor.DirectionalLight.name().equals(t3dClass)) {
			componentLightClass = "DirectionalLightComponent";
		} else {
			componentLightClass = "PointLightComponent";
		}

		sbf.append(IDT).append("Begin Actor Class=").append(t3dClass).append(" Name=").append(name).append("\n");

		if (isTo(UnrealEngine.UE4)) {

			sbf.append(IDT).append("\tBegin Object Class=").append(componentLightClass).append(" Name=\"LightComponent0\"\n");
			sbf.append(IDT).append("\tEnd Object\n");

			sbf.append(IDT).append("\tBegin Object Name=\"LightComponent0\"\n");

			// not applicable to directional light
			if (!UE4_LightActor.DirectionalLight.name().equals(t3dClass)) {

				if (lightFalloffExponent != null) {
					sbf.append(IDT).append("\t\tbUseInverseSquaredFalloff=False\n");
					sbf.append(IDT).append("\t\tLightFalloffExponent=").append(lightFalloffExponent).append("\n");
				}

				sbf.append(IDT).append("\t\tAttenuationRadius=").append(radius).append("\n");
			}

			// not sure if the real source radius is radius / 2
			if (lightEffect == UE12_LightEffect.LE_Cylinder) {
				sbf.append(IDT).append("\t\tSourceLength=").append(radius / 2).append("\n");
				sbf.append(IDT).append("\t\tSourceRadius=").append(radius / 2).append("\n");
			}

			sbf.append(IDT).append("\t\tLightColor=").append(rgbColor.toT3D(true)).append("\n");

			if (intensity != null) {
				sbf.append(IDT).append("\t\tIntensity=").append(intensity).append("\n");
			}

			if (isSpotLight()) {
				// 128 is default angle for UE1/2 (in 0 -> 255 range) = 90 in (0
				// -> 180° range)
				double angle = outerConeAngle != null ? outerConeAngle : 90d;
				sbf.append(IDT).append("\t\tInnerConeAngle=").append((angle / 2)).append("\n");
				sbf.append(IDT).append("\t\tOuterConeAngle=").append(angle).append("\n");
			}

			sbf.append(IDT).append("\t\tMobility=").append(mobility.name()).append("\n");

			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");

			writeSimpleProperties();

			if(this instanceof final T3DTriggerLight t3DTriggerLight){

				if(t3DTriggerLight.getInitialState() != null){
					sbf.append(IDT).append("\tInitialState=NewEnumerator").append(t3DTriggerLight.getInitialState().ordinal()).append("\n");
				}
			}

			// FOR UBLIGHT ONLY
			if(this.lightEffect != null){
				sbf.append(IDT).append("\tLightEffect=NewEnumerator").append(lightEffect.ordinal()).append("\n");
			}

			if(this.lightType != null){
				sbf.append(IDT).append("\tLightType=NewEnumerator").append(lightType.ordinal()).append("\n");
			}

			sbf.append(IDT).append("\tLightComponent=\"LightComponent0\"\n");
            sbf.append(IDT).append("\tRootComponent=\"LightComponent0\"\n");


		} else if (isTo(UnrealEngine.UE3)) {

			final String drawRadObjName = "DrawLightRadiusComponent_" + new Random().nextInt(10000);

			// CastShadow=False
			sbf.append(IDT).append("\tBegin Object Class=DrawLightRadiusComponent Name=DrawLightRadius0 ObjName=").append(drawRadObjName).append(" Archetype=DrawLightRadiusComponent'Engine.Default__PointLight:DrawLightRadius0'\n");
			sbf.append(IDT).append("\t\tSphereRadius=").append(radius).append("\n");
			// for directional lights do not cast shadows because in original UE1/UE2 maps
			// the sky is "blocked" by a brush (with fakebackdrop sky) thus light rays not going
			if (isDirectional) {
				sbf.append(IDT).append("\t\tCastShadow=False\n");
			}
			sbf.append(IDT).append("\tEnd Object\n");

			final String pointCompObjName = componentLightClass + "_" + new Random().nextInt(10000);

			sbf.append(IDT).append("\tBegin Object Class=").append(componentLightClass).append(" Name=\"").append(componentLightClass).append("_0 ");
			sbf.append("ObjName=").append(pointCompObjName).append(" Archetype=").append(componentLightClass).append("'Engine.Default__PointLight:").append(componentLightClass).append("0'\n");

			sbf.append(IDT).append("\t\tRadius=").append(radius).append("\n");
			sbf.append(IDT).append("\t\tBrightness=1.0\n");

			// for directional lights do not cast shadows else it looks very dark
			if (isDirectional) {
				sbf.append(IDT).append("\t\tCastShadows=False\n");
			}

			sbf.append(IDT).append("\t\tName=\"").append(pointCompObjName).append("\"\n");
			// R,G,B as integers for UE3
			sbf.append(IDT).append("\t\tLightColor=").append(rgbColor.toT3D(true)).append("\n");
			sbf.append(IDT).append("\tEnd Object\n");

			final String spritCompObjName = "SpriteComponent_" + new Random().nextInt(10000);
			sbf.append(T3DUtils.writeSimpleObject("\t\t", "SpriteComponent", "Sprite", spritCompObjName, "SpriteComponent'Engine.Default__PointLight:Sprite'", null, null));

			writeLocRotAndScale();
			sbf.append(IDT).append("\tLightComponent=").append(componentLightClass).append("'").append(pointCompObjName).append("'\n");

			sbf.append(IDT).append("\tComponents(0)=SpriteComponent'").append(spritCompObjName).append("'\n");
			sbf.append(IDT).append("\tComponents(1)=DrawLightRadiusComponent'").append(drawRadObjName).append("'\n");
			sbf.append(IDT).append("\tComponents(2)=").append(componentLightClass).append("'").append(pointCompObjName).append("'\n");
		}

		writeEndActor();

		return super.toT3d();
	}

	/**
     *
     */
	@Override
	public void convert() {

		if (isCorona && skins != null && !skins.isEmpty() && mapConverter.convertTextures()) {

			for(final UPackageRessource skinMaterial : this.skins) {
				skinMaterial.export(UTPackageExtractor.getExtractor(mapConverter, skinMaterial));
				replaceWith(T3DEmitter.createLensFlare(mapConverter, this));
			}

			return;
		}

		// Old engines using hue, brightness, saturation system
		if (mapConverter.isFrom(UnrealEngine.UE1, UE2) && mapConverter.isTo(UnrealEngine.UE3, UnrealEngine.UE4, UnrealEngine.UE5)) {
			// in UE1/UE2 saturation is 'reversed' compared with standards, UE2 have sometimes 255+ brightness lights
			this.rgbColor = ImageUtils.HSVToLinearRGB(hue, Math.abs(saturation - 255), Math.min(brightness, 255), true);

			if (isCorona) {
				radius = 0;
			}
		}

		// UE4 default intensity is 5000
		// UE3 default brightness is 1 but always using FalloffExponent
		// brightness seems to have been replaced with intensity in UE4
		/* Tests in editor with a light inside a 1024x1024 cube (1000x1000 for UE4)
		* at center at 1/4 of cube height
		* Game: LightFallOffExponent, Radius -> Real Light Radius
		* UT3: LFO: 2, RAD: 512 -> 1024
		* UT4: LFO: 2, RAD: 500 -> 750
		* UT4: LFO: 2, RAD: 750 -> 1000
		* -> good radius needs to be 1.25x
		* */
		if (mapConverter.isFrom(UnrealEngine.UE3) && mapConverter.isTo(UnrealEngine.UE4, UnrealEngine.UE5)) {
			this.intensity = this.brightness; // seems right effect
			// radius needs to be increased to have same real radius range
			// with LightFallofExponent=2
			this.radius *= 1.25;
		}

		// UE1/UE2 don't have by default LightFalloffExponent
		// In UE4 without lightfalloffponent, a default light with intensity = 5000
		// needs to have a radius
		if (mapConverter.isFrom(UnrealEngine.UE1, UE2) && mapConverter.isTo(UnrealEngine.UE4, UnrealEngine.UE5)) {
			// real radius in UE1/UE2 is 32X more important in game
			this.radius *= 32;

			// needs to scale up again to fit correct radius in UE4+
			//this.radius *= 1.12;

			this.lightFalloffExponent = 2d;
			this.intensity = mapConverter.isFrom(UE1) ? 35f : 1f;

			// UE2, unlike UE1 can have lights with brightness > 255 so need to increase intensity a bit
			if (mapConverter.isFrom(UE2) && brightness > 255) {
				intensity += Double.valueOf(Math.log(Math.min(brightness - 255f, 100)) / 2).floatValue(); // using log because
			}
		}

		if (intensity != null && mapConverter.brightnessFactor != null) {
			intensity *= mapConverter.brightnessFactor;
		}

		if (mapConverter.isFromUE1UE2ToUE3UE4()) {

			if (outerConeAngle != null) {
				// 0 -> 255 range to 0 -> 180 range
				outerConeAngle *= (255d / 360d) / 2;
			}
		}

		// UE4 does not care about negative scale for lights
		// so need to change rotation (for directional lights)
		if (mapConverter.isFrom(UnrealEngine.UE1, UE2, UnrealEngine.UE3) && mapConverter.isTo(UnrealEngine.UE4) && scale3d != null) {

				if ((scale3d.x < 0 || scale3d.y < 0 || scale3d.z < 0) && rotation == null) {
					rotation = new Vector3d();
				}

				final double DEFAULT_PI_UE = Rotator.getDefaultTwoPi(UnrealEngine.from(mapConverter.getInputGame().getUeVersion()));
				final double DEFAULT_PI_UE_HALF = DEFAULT_PI_UE / 2d;

				if (scale3d.x < 0) {
					rotation.x += DEFAULT_PI_UE_HALF;
					rotation.z += DEFAULT_PI_UE_HALF;
					scale3d.x = Math.abs(scale3d.x);
				}

				if (scale3d.y < 0) {
					rotation.y += DEFAULT_PI_UE_HALF;
					rotation.z += DEFAULT_PI_UE_HALF;
					scale3d.y = Math.abs(scale3d.y);
				}

				if (scale3d.z < 0) {
					rotation.x += DEFAULT_PI_UE_HALF;
					rotation.y += DEFAULT_PI_UE_HALF;
					scale3d.z = Math.abs(scale3d.z);
				}
		}

		convertClassAndMobility();

		super.convert();
	}


	@Override
	public void scale(double newScale) {

		radius *= newScale;

		super.scale(newScale);
	}

	@Override
	public boolean isValidWriting() {

		if (brightness == 0) {
			return false;
		} else {
			return super.isValidWriting();
		}
	}


}
