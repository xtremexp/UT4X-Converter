/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.tools.ImageUtils;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.UPackageRessource;

import java.util.ArrayList;
import java.util.List;

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
		LE_None, LE_TorchWaver, LE_FireWaver, LE_WateryShimmer, LE_Searchlight, LE_SlowWave, LE_FastWave, LE_CloudCast, LE_StaticSpot, LE_Shock, LE_Disco, LE_Warp, LE_Spotlight, LE_NonIncidence, LE_Shell, LE_OmniBumpMap, LE_Interference, LE_Cylinder, LE_Rotor, LE_Unused,
		// Unreal Engine 2 new light effects
		LE_Sunlight, LE_Spotlight2, LE_SquareSpotlight, LE_QuadraticNonIncidence
	}

	/**
	 * Light Type UE 1 / 2
	 */
	enum UE12_LightType {
		LT_None, LT_Steady, LT_Pulse, LT_Blink, LT_Flicker, LT_Strobe, LT_BackdropLight, LT_SubtlePulse, LT_TexturePaletteLoop, LT_TexturePaletteOnce
	}

	/**
     * 
     */
	enum UE4_Mobility {
		Static, // don't move and don't change color
		Stationary, // don't move but can change of color
		Movable // can move and change color
	}

	/**
	 * Light actors for UT99. Basically are normal lights with either some skin
	 * or special light effect + type TODO move out this to some "core class"
	 */
	public enum UE12_LightActors {
		Light, ChargeLight, DistanceLightning, FlashLightBeam, OverHeatLight, QueenTeleportLight, SightLight, SpotLight, TorchFlame, TriggerLight, TriggerLightRad, WeaponLight, EffectLight, PurpleLight,
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
	 * For spot lights UE3 only default 0
	 */
	private Double innerConeAngle;

	/**
	 * UE1/2: LightCone - default 128 UE4: OuterConeAngle - default 44
	 */
	private Double outerConeAngle;


	private boolean isCorona;

	/**
	 * UE1/2 brightness UE1 range: 0-255 UE2 range: 0-Infinite
	 */
	private float brightness;

	/**
	 * UE1/2 hue
	 */
	private float hue;

	/**
	 * UE1/2 saturation
	 */
	private float saturation;
	private float radius;

	// *** Unreal Engine 3 / 4 Properties ***

	/**
	 * Range Value 0->1
	 */
	private float red, green, blue, alpha = 255;

	/**
	 * Unreal Engine 4 Default Intensity
	 */
	private static final int DEFAULT_INTENSITY = 5000;

	/**
	 * Default Attenuation Radius for unreal engine 4
	 */
	private static final float DEFAULT_ATTENUATION_RADIUS = 1000f;

	/**
	 * How much attenuation radius will be multiplied Attenuation Radius =
	 * Radius(UE123) * Factor
	 */
	private static final int UE1_UE4_ATTENUATION_RADIUS_FACTOR = 20;

	/**
	 * How much attenuation radius will be multiplied Attenuation Radius =
	 * Radius(UE123) * Factor
	 */
	private static final int UE2_UE4_ATTENUATION_RADIUS_FACTOR = 22;

	private Double intensity;

	/**
	 * UE4 Only
	 */
	private Double lightFalloffExponent;

	/**
	 * Attenuation Radius (dunno how it works yet compared with sourceRadius)
	 */
	private float attenuationRadius = DEFAULT_ATTENUATION_RADIUS;

	private UE4_Mobility mobility = UE4_Mobility.Static;

	/**
	 * As seen in UT2004.
	 * Overrides spot light class to directional light if true
	 */
	private Boolean isDirectional;

	/**
	 *
	 * @param mc
	 * @param t3dClass
	 */
	public T3DLight(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);

		// Default Values when u put some light in UE1/UE2 editor
		if (mc.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {
			this.hue = 0;
			this.saturation = 255f;
			this.brightness = 64f;
			this.radius = 64f;

			if (mc.isFrom(UnrealEngine.UE1)) {
				this.lightFalloffExponent = 2.5d;
				this.intensity = 60d;
			} else if (mc.isFrom(UnrealEngine.UE2)) {
				this.lightFalloffExponent = 1.9d;
				this.intensity = 80d;
			}
		}
		// Default Values when u put some light in UE4 editor
		else if (mc.isFrom(UnrealEngine.UE3)) {
			this.radius = 1024f;
			this.brightness = 1f;

			this.red = 255;
			this.blue = 255;
			this.green = 255;
			this.alpha = 0;
			this.lightFalloffExponent = 2d;
			this.intensity = 10d;
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
			lightEffect = UE12_LightEffect.valueOf(line.split("\\=")[1]);
		}

		else if (line.startsWith("LightType")) {
			lightType = UE12_LightType.valueOf(line.split("\\=")[1]);
		}

		else if (line.startsWith("InnerConeAngle")) {
			innerConeAngle = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("LightCone") || line.startsWith("OuterConeAngle")) {
			outerConeAngle = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("bCorona=")) {
			isCorona = "true".equals(line.split("\\=")[1].toLowerCase());
		}

		// UT3
		// LightColor=(B=58,G=152,R=197,A=0)
		else if (line.startsWith("LightColor=")) {

			RGBColor rgbColor = T3DUtils.getRGBColor(line);
			this.green = rgbColor.G;
			this.blue = rgbColor.B;
			this.red = rgbColor.R;
			this.alpha = rgbColor.A;
		}

		else if (line.startsWith("FalloffExponent=")) {
			lightFalloffExponent = T3DUtils.getDouble(line);
		}

		// Skin=Texture'GenFX.LensFlar.3'
		else if (line.startsWith("Skin=") || line.startsWith("Skins(")) {
			if(this.skins != null){
				this.skins = new ArrayList<>();
			}

			this.skins.add(mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.TEXTURE));
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
	 * @return
	 */
	private boolean isSpotLight() {
		return t3dClass.equals(UE4_LightActor.SpotLight.name()) || lightEffect == UE12_LightEffect.LE_Spotlight || lightEffect == UE12_LightEffect.LE_StaticSpot
				|| lightEffect == UE12_LightEffect.LE_Spotlight2 || lightEffect == UE12_LightEffect.LE_SquareSpotlight;
	}

	/**
	 * Tells if current light is sunlight if true
	 * 
	 * @return
	 */
	private boolean isSunLight() {
		return lightEffect == UE12_LightEffect.LE_Sunlight || t3dClass.equals(UE12_LightActors.Sunlight.name());
	}

	/**
	 * 
	 * @return
	 */
	private void convertClassAndMobility() {

		if (mapConverter.isFrom(UnrealEngine.UE3)) {

			convertUE3ClassAndMobility();

		} else if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {

			convertUE4ClassAndMobility();
		} else {
			t3dClass = UE4_LightActor.PointLight.name();
		}
	}

	/**
	 * Convert UE1/2 light class and mobility to UE4
	 */
	private void convertUE4ClassAndMobility() {

		this.t3dClass = UE4_LightActor.PointLight.name();

		if (isSpotLight()) {
			this.t3dClass = UE4_LightActor.SpotLight.name();
        }

        // seen in some map light as spot but bDirectional set to true
        if (isSunLight() || (this.isDirectional != null && this.isDirectional)) {
			this.t3dClass = UE4_LightActor.DirectionalLight.name();
        }


		// disabled for the moment for perf issues
		/**
         * if(lightEffect == UE12_LightEffect.LE_None || lightEffect ==
         * UE12_LightEffect.LE_StaticSpot || lightEffect ==
         * UE12_LightEffect.LE_Unused || lightEffect ==
         * UE12_LightEffect.LE_Cylinder ){ return UE4_Mobility.Static; }
         * else { t3dClass = UE4_Mobility.Stationary; }
         */}

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
            } else if (t3dClass.equals(UE3_LightActor.PointLightToggleable.name())) {
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
	 * Default light source lenght for converted UE1/2 light with cylinder light
	 * effect
	 */
	final int lightEffectCylinderSourceLength = 100;

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {

		if (mapConverter.toUnrealEngine4()) {
			String componentLightClass = null;

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

			sbf.append(IDT).append("\tBegin Object Class=").append(componentLightClass).append(" Name=\"LightComponent0\"\n");
			sbf.append(IDT).append("\tEnd Object\n");

			sbf.append(IDT).append("\tBegin Object Name=\"LightComponent0\"\n");

			// not applicable to directional light
			if (!UE4_LightActor.DirectionalLight.name().equals(t3dClass)) {
				sbf.append(IDT).append("\t\tbUseInverseSquaredFalloff=False\n");
				sbf.append(IDT).append("\t\tLightFalloffExponent=").append(lightFalloffExponent).append("\n");
				sbf.append(IDT).append("\t\tAttenuationRadius=").append(attenuationRadius).append("\n");
			}

			if (lightEffect == UE12_LightEffect.LE_Cylinder) {
				sbf.append(IDT).append("\t\tSourceLength=").append(attenuationRadius / 2).append("\n");
				sbf.append(IDT).append("\t\tSourceRadius=").append(attenuationRadius / 2).append("\n");
			} else {
				sbf.append(IDT).append("\t\tSourceRadius=").append(radius).append("\n");
			}

			sbf.append(IDT).append("\t\tLightColor=(B=").append(blue).append(",G=").append(green).append(",R=").append(red).append(",A=").append(alpha).append(")\n");

			if (intensity != null) {
				sbf.append(IDT).append("\t\tIntensity=").append(intensity).append("\n");
			}

			if (isSpotLight()) {
				// 128 is default angle for UE1/2 (in 0 -> 255 range) = 90 in (0
				// -> 180° range)
				Double angle = outerConeAngle != null ? outerConeAngle : 90d;
				sbf.append(IDT).append("\t\tInnerConeAngle=").append((angle / 2)).append("\n");
				sbf.append(IDT).append("\t\tOuterConeAngle=").append(angle).append("\n");
			}

			sbf.append(IDT).append("\t\tMobility=").append(mobility.name()).append("\n");

			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");

			sbf.append(IDT).append("\tLightComponent=\"LightComponent0\"\n");
			writeEndActor();
		}

		return super.toString();
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

		// UE1 has brightness range 0-255
		// but UE2 has not limit for brightness
		// so we make sure brightness does not go above 255
		// but give extra intensity "boost"
		// e.g: brightness of 1000 --> brightness = 255 and intensity = 60 +
		// log(1000) * 6 = 60 + 3 * 6 = 78
		if (brightness > 255) {
			intensity += Math.log(brightness - 255) * 6; // using log because
															// brightness can be
															// nearly infinite
		}

		brightness = Math.min(brightness, 255);

		// Old engines using hue, brightness, saturation system
		if (mapConverter.isFromUE1UE2ToUE3UE4()) {
			convertColorToRGB();

			if (isCorona) {
				radius = 0;
			}
		}

		// TODO handle UT3 brightness correctly
		if (intensity != null && mapConverter.brightnessFactor != null) {
			intensity *= mapConverter.brightnessFactor;
		}

		attenuationRadius = radius;

		if (mapConverter.isFromUE1UE2ToUE3UE4()) {
			if (mapConverter.isFrom(UnrealEngine.UE1)) {
				attenuationRadius *= UE1_UE4_ATTENUATION_RADIUS_FACTOR;
			} else {
				attenuationRadius *= UE2_UE4_ATTENUATION_RADIUS_FACTOR;
			}

			if (outerConeAngle != null) {
				// 0 -> 255 range to 0 -> 180 range
				outerConeAngle *= (255d / 360d) / 2;
			}
		}

		// UE4 does not care about negative scale for lights
		// so need to change rotation (for directional lights)
		if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2, UnrealEngine.UE3) && mapConverter.isTo(UnrealEngine.UE4)) {
			if (scale3d != null) {

				if ((scale3d.x < 0 || scale3d.y < 0 || scale3d.z < 0) && rotation == null) {
					rotation = new Vector3d();
				}

				final double DEFAULT_PI_UE = Rotator.getDefaultTwoPi(mapConverter.getInputGame().engine);
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
		}

		convertClassAndMobility();

		super.convert();
	}

	/**
	 * Convert Hue Saturation Brightness values from old UT2004 lightning system
	 * to Red Blue Green values with new UT3 lightning system
	 *
	 */
	private void convertColorToRGB() {
		// Saturation is reversed in Unreal Engine 1/2 compared with standards
		// ...
		saturation = Math.abs(saturation - 255);

		RGBColor rgb = ImageUtils.HSVToLinearRGB(hue, saturation, brightness, false);

		this.red = rgb.R;
		this.blue = rgb.B;
		this.green = rgb.G;
	}

	@Override
	public void scale(Double newScale) {

		attenuationRadius *= newScale;
		radius *= newScale;

		if (intensity != null) {
			// intensity *= newScale;
		}

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
