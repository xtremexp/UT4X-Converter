/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;

import javax.vecmath.Vector3d;
import java.util.HashMap;
import java.util.Map;

import static org.xtx.ut4converter.ucore.UnrealEngine.*;

/**
 * Class for converting any actor related to sound (might be music as well) TODO
 * merge with T3D Actor and delete this class because any actors can have sound
 * property
 * <p>
 * In UE1/2, any actor can have sound properties (like lights)
 *
 * @author XtremeXp
 */
public class T3DSound extends T3DActor {


	/**
	 * Sound played
	 */
	protected UPackageRessource ambientSound;


	private AttenuationSettings attenuation;

	/**
	 * Sound volume
	 * UE1/2: Range 0->255 (default 190 for AmbientSound actors else 128 (e.g: Light))
	 * UE3/4: Range 0->1 (default: 1)
	 */
	protected Double soundVolume;

	/**
	 * Sound pitch (sound frequency modification)
	 * UE1/2: Range 0->255 (default 64)
	 * UE3/4: Range 0->1 (default: 1)
	 */
	protected Double soundPitch;

	/**
	 * Start distance where fallsoff begins
	 * UE3/UE4 property only
	 * Assuming soundMinRadius = 0 for UE1/UE2
	 */
	protected Double soundMinRadius;

	/**
	 * Sound radius
	 * UE1/2: Range 0->255 - 'Radius' - (default 64 for AmbientSound actors else 32 (e.g: Light))
	 * UE3/4: Range 0->1 with MinRadius and MaxRadius properties
	 */
	protected Double soundMaxRadius;

	/**
	 * Transient sound radius
	 * No idea how it works
	 * TODO handle transientSoundRadius
	 */
	private Double transientSoundRadius;

	/**
	 * Transient sound volume
	 * No idea how it works (no documentation online)
	 * TODO handle transientSoundVolume
	 */
	private Double transientSoundVolume;


	/**
	 * 
	 * Ambient sound actors classes for UT3/UE3
	 */
	public enum UE3_AmbientSoundActor {
		/**
		 * Sound actor using Cue
		 */
		AmbientSound,
		/**
		 * Loop sounds
		 */
		AmbientSoundSimple,
		/**
		 * Non-loop sounds
		 */
		AmbientSoundNonLoop,
		/**
		 * Toggable loop sounds
		 */
		AmbientSoundSimpleToggleable
	}

	/**
	 * Attenuation settings for sounds
	 * UE4: TODO move out to ucore package ?
	 * See https://docs.unrealengine.com/5.1/en-US/sound-attenuation-in-unreal-engine/
	 */
	static class AttenuationSettings {


		/**
		 * UE3/4
		 * How sounds is attenuated with distance
		 */
		enum DistanceAlgorithm {
			/**
			 * Default attenuation for UE3
			 * (need check for UE4)
			 */
			ATTENUATION_Linear, ATTENUATION_Logarithmic, ATTENUATION_Inverse, ATTENUATION_LogReverse, ATTENUATION_NaturalSound
		}

		/**
		 * Shape of "sound volume" only Sphere available for UE3
		 */
		enum Shape {
			Sphere, Capsule, Box, Cone
		}

		/**
		 * UE3
		 *
		 * @author XtremeXp
		 *
		 */
		enum DistributionType {
			DistributionDelayTime, DistributionMinRadius, DistributionMaxRadius, DistributionLPFMinRadius, DistributionLPFMaxRadius, DistributionVolume, DistributionPitch
		}

		/**
		 * UE3 'bSpatialize' - default true
		 * UE4: 'AttenuationOverrides=(bSpatialize=False)'
		 */
		boolean bSpatialize = true;

		/**
		 * 'Non spatialized radius'
		 * UE4 only default 20
		 */
		Double omniRadius;

		/**
		 * In UE3 it's "DistanceModel"
		 */
		DistanceAlgorithm distanceAlgorithm = DistanceAlgorithm.ATTENUATION_Linear;

		/**
		 * Only with UE4 and distance algorithm ATTENUATION_NaturalSound
		 */
		Double dBAttenuationAtMax = -60d;

		/**
		 * Only in UE4, in UE3 it seems to be sphere by default
		 */
		Shape attenuationShape = Shape.Sphere;

		DistributionType distributionType;

		/**
		 * UE4: is radius UE3: "MinRadius" (400 default)
		 * 
		 */
		Vector3d attenuationShapeExtents = new Vector3d(400d, 0, 0);

		/**
		 * UE4 only with attenuation shape "Cone" (default : 0)
		 */
		protected Double coneOffset;

		/**
		 * UE4 only
		 */
		protected Double fallOffDistance;

		/**
		 * UE3: LPFMinRadius (default 1500) UE4: default 3000
		 */
		protected Double LPFRadiusMin;

		/**
		 * UE3: LPFMaxRadius (default 2500) UE4: default 6000
		 */
		protected Double LPFRadiusMax;

		/**
		 * UE3: 'bAttenuateWithLowPassFilter' - default false
		 * UE4: 'AttenuationOverrides=(bAttenuateWithLPF=True)'
		 */
		boolean bAttenuateWithLPF = false;

		public AttenuationSettings(int ueVersion) {
			if (ueVersion == 3) {
				this.LPFRadiusMin = 1500d;
				this.LPFRadiusMax = 2500d;

			} else if (ueVersion >= 4) {
				this.LPFRadiusMin = 3000d;
				this.LPFRadiusMax = 6000d;
				this.fallOffDistance = 3600d;
			}
		}

		public String toString(int ueVersion) {
			if (ueVersion <= 3) {
				return null;
			}
			// only UE4 support right now
			else {
				StringBuilder s = new StringBuilder("(");

				Map<String, Object> props = new HashMap<>();

				props.put("DistanceAlgorithm", distanceAlgorithm.name());

				props.put("bAttenuateWithLPF", bAttenuateWithLPF);
				props.put("bSpatialize", bSpatialize);

				if (bSpatialize) {
					props.put("OmniRadius", omniRadius);
				}

				if (distanceAlgorithm == DistanceAlgorithm.ATTENUATION_NaturalSound) {
					props.put("dBAttenuationAtMax", dBAttenuationAtMax);
				}

				if (attenuationShape == Shape.Cone) {
					props.put("coneOffset", coneOffset);
				}

				props.put("AttenuationShapeExtents", attenuationShapeExtents);
				props.put("FalloffDistance", fallOffDistance);
				props.put("LPFRadiusMin", LPFRadiusMin);
				props.put("LPFRadiusMax", LPFRadiusMax);

				s.append(T3DUtils.getT3DLine(props));
				s.append(")");
				return s.toString();
			}
		}
	}

	public T3DSound(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);

		initialise();
	}


	/**
	 *
	 * @param mc Map converter instance
	 * @param t3dClass T3d class
	 */
	public T3DSound(MapConverter mc, String t3dClass, T3DActor actor) {
		super(mc, t3dClass, actor);

		initialise();
	}

	/**
	 * TODO some outside kind of UProperty class with defaults value for each UE
	 * version
	 */
	private void initialise() {

		ue4RootCompType = T3DMatch.UE4_RCType.AUDIO;

		// initialise default values for UE1/UE2
		if (mapConverter.isFrom(UnrealEngine.UE1, UE2)) {

			soundPitch = 64d;
			transientSoundRadius = 0d;
			transientSoundVolume = 1d;

			// default properties values for AmbientSound actor
			if (this.getClass() == T3DSound.class) {
				soundMaxRadius = 64d;
				soundVolume = 190d;
			}
			// other actors (such as lights, often used also as sounds)
			else {
				soundMaxRadius = 32d;
				soundVolume = 128d;
			}
			// this prop does not exists in UE1/UE2
			this.soundMinRadius = 0d;
		}
		// default UE3 vallues
		else if (mapConverter.isFrom(UnrealEngine.UE3)) {
			// default max sound radius for AmbientSoundSimple actor
			this.soundMinRadius = 400d;
			this.soundMaxRadius = 5000d;
			this.soundVolume = 1d;
			this.soundPitch = 1d;
		}

		this.attenuation = new AttenuationSettings(mapConverter.getInputGame().getUeVersion());
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.startsWith("SoundRadius")) {
			this.soundMaxRadius = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("TransientSoundRadius")) {
			transientSoundRadius = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("SoundVolume")) {
			soundVolume = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("TransientSoundVolume")) {
			transientSoundVolume = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("SoundPitch")) {
			soundPitch = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("DistanceModel")) {
			attenuation.distanceAlgorithm = AttenuationSettings.DistanceAlgorithm.valueOf(line.split("=")[1]);
		}

		// UE3
		else if (line.startsWith("Min=") && mapConverter.isFrom(UE3)) {

			double min = T3DUtils.getDouble(line);

			if (AttenuationSettings.DistributionType.DistributionPitch.name().equals(currentSubObjectName)) {
				soundPitch = min;
				this.soundPitch = Math.min(this.soundPitch, min);
			} else if (AttenuationSettings.DistributionType.DistributionVolume.name().equals(currentSubObjectName)) {
				soundVolume = min;
			} else if (AttenuationSettings.DistributionType.DistributionLPFMinRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMin = min;
			} else if (AttenuationSettings.DistributionType.DistributionLPFMaxRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMax = min;
			} else if (AttenuationSettings.DistributionType.DistributionMaxRadius.name().equals(currentSubObjectName)) {
				this.soundMinRadius = Math.min(this.soundMinRadius, min);
			} else if (AttenuationSettings.DistributionType.DistributionMinRadius.name().equals(currentSubObjectName)) {
				this.soundMinRadius = Math.min(this.soundMinRadius, min);
			}
		}

		// TODO handle MinPitch,MinVolume,MinLPF
		// UE3
		else if (line.startsWith("Max=") && mapConverter.isFrom(UE3)) {

			double max = T3DUtils.getDouble(line);

			if (AttenuationSettings.DistributionType.DistributionPitch.name().equals(currentSubObjectName)) {
				soundPitch = Math.max(soundPitch, max);
			} else if (AttenuationSettings.DistributionType.DistributionVolume.name().equals(currentSubObjectName)) {
				soundVolume = Math.max(soundVolume, max);
			} else if (AttenuationSettings.DistributionType.DistributionLPFMinRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMin = getMax(attenuation.LPFRadiusMin, max);
			} else if (AttenuationSettings.DistributionType.DistributionLPFMaxRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMax = getMax(attenuation.LPFRadiusMax, max);
			} else if (AttenuationSettings.DistributionType.DistributionMaxRadius.name().equals(currentSubObjectName)) {
				this.soundMaxRadius = max;
				this.attenuation.attenuationShapeExtents.x = max;
			} else if (AttenuationSettings.DistributionType.DistributionMinRadius.name().equals(currentSubObjectName)) {
				this.soundMaxRadius = max;
				this.attenuation.fallOffDistance = max;
			}
		}

		// UE1/2: AmbientSound=Sound'AmbAncient.Looping.Stower51'
		// UE3: Wave=SoundNodeWave'A_Ambient_Loops.Water.water_drain01'
		else if (line.startsWith("AmbientSound=") || line.startsWith("Wave=")) {
			ambientSound = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.SOUND);
		}
		// UC2: AmbientSoundCue="A_Whisper.cave_water_loop"
		// Note: will fail to extract sound package because its microsoft format (.xwb), however the ambient sound actor (without sound) will be in map
		// see: https://www.lifewire.com/xwb-file-2622644
		else if (line.startsWith("AmbientSoundCue=")) {
			ambientSound = mapConverter.getUPackageRessource(line.split("\"")[1], T3DRessource.Type.SOUND);
		}
		else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	private Double getMax(Double currentMax, Double newMax) {
		if (currentMax == null) {
			return newMax;
		} else {
			return Math.max(currentMax, newMax);
		}
	}

	@Override
	public void scale(double newScale) {

		this.soundMaxRadius *= newScale;

		T3DUtils.scale(attenuation.attenuationShapeExtents, newScale);
		attenuation.LPFRadiusMin = T3DUtils.scale(attenuation.LPFRadiusMin, newScale);
		attenuation.LPFRadiusMax = T3DUtils.scale(attenuation.LPFRadiusMax, newScale);
		attenuation.fallOffDistance = T3DUtils.scale(attenuation.fallOffDistance, newScale);
		attenuation.omniRadius = T3DUtils.scale(attenuation.omniRadius, newScale);

		super.scale(newScale);
	}

	/**
     *
     */
	@Override
	public void convert() {

		if (soundVolume !=  null && mapConverter.soundVolumeFactor != null) {
			soundVolume *= mapConverter.soundVolumeFactor;
		}

		if (mapConverter.isFromUE1UE2ToUE3UE4()) {

			if (soundVolume != null) {

				// switching from UE1/2 0->255 range to 0->1 UE4 range
				soundVolume /= 128D;  // default volume value is 128 in UE1/UE2 (190 for AmbientSound actor)

				// decreasing sound volume from UT2004 because seems "loudy" in
				// UT4 ...
				// not needed for Unreal 2 even if they share same engine version ..
				if (mapConverter.isFrom(UTGames.UTGame.UT2004.shortName)) {
					soundVolume *= 0.10;
				}
			}

			if (soundPitch != null) {
				soundPitch /= 64D; // default pitch is 64 in UE1/2
			}

			// need to scale up radius for UE1/2 -> UE3/4
			// tested DM-ArcaneTemple (UT99)
			soundMaxRadius *= 24;
		}

		if (isTo(UE4, UE5)) {
			attenuation.attenuationShapeExtents.x = soundMinRadius.intValue();
			attenuation.fallOffDistance = this.soundMaxRadius;
		}

		if (mapConverter.convertSounds() && ambientSound != null) {
			ambientSound.export(UTPackageExtractor.getExtractor(mapConverter, ambientSound));
		}

		super.convert();
	}

	protected Component buildMainAudioComponent() {

		int ueVersion = this.getOutputGame().getUeVersion();
		final String compClass = isTo(UE3) ? "SoundNodeAmbient" : "AudioComponent";
		final Component mainComp = new Component(compClass, compClass + "0", this);

		// if this component is referenced in actor, it won't import correctly in UE3
		// E.G: "Components(0)=SoundNodeAmbient'SoundNodeAmbient3908'" -> ""
		if (isTo(UE3)) {
			mainComp.noListInActorComponends = true;
		}

		if (ambientSound != null) {
			if (isTo(UE3)) {
				mainComp.addProp("Wave", "SoundNodeWave'" + ambientSound.getConvertedName() + "'");
			} else {
				mainComp.addProp("Sound", "SoundCue'" + ambientSound.getConvertedName() + "'");
			}
		}

		if (soundVolume != null) {
			if (isTo(UE3)) {
				final Component ue3VolumeComp = new Component("DistributionFloatUniform", "DistributionVolume", this);
				ue3VolumeComp.addProp("Min", soundVolume).addProp("Max", soundVolume);

				final String volModStr = "1.000000," + soundVolume + ",1.000000," + soundVolume + ",1.000000," + soundVolume;
				mainComp.addProp("PitchModulation", "(Distribution=" + ue3VolumeComp.getObjName() + ",LookupTable=(" + volModStr + ")");

				mainComp.addSubComponent(ue3VolumeComp);
			} else {
				mainComp.addProp("VolumeMultiplier", soundVolume);
			}
		}

		if (soundPitch != null) {
			if (isTo(UE3)) {
				final Component ue3PitchComp = new Component("DistributionFloatUniform", "DistributionPitch", this);
				ue3PitchComp.addProp("Min", soundPitch).addProp("Max", soundPitch);

				final String pitchModStr = soundPitch + "," + soundPitch + "," + soundPitch + "," + soundPitch + "," + soundPitch + "," + soundPitch;
				mainComp.addProp("VolumeModulation", "(Distribution=" + ue3PitchComp.getObjName() + ",LookupTable=(" + pitchModStr + ")");

				mainComp.addSubComponent(ue3PitchComp);
			} else {
				mainComp.addProp("PitchMultiplier", soundPitch);
			}
		}

		if (soundMaxRadius != null) {
			if (isTo(UE3)) {
				final Component ue3MinRadiusComp = new Component("DistributionFloatUniform", "DistributionMinRadius", this);
				final Component ue3MaxRadiusComp = new Component("DistributionFloatUniform", "DistributionMaxRadius", this);
				ue3MaxRadiusComp.addProp("Min", soundMaxRadius).addProp("Max", soundMaxRadius);

				final String maxRadValuesStr = soundMaxRadius + "," + soundMaxRadius + "," + soundMaxRadius + "," + soundMaxRadius + "," + soundMaxRadius + "," + soundMaxRadius;

				mainComp.addProp("MinRadius", "(Distribution=" + ue3MinRadiusComp.getReference(ueVersion) + ",LookupTable=(0,0,0,0,0,0)");
				mainComp.addProp("MaxRadius", "(Distribution=" + ue3MaxRadiusComp.getReference(ueVersion) + ",LookupTable=(" + maxRadValuesStr + ")");

				mainComp.addSubComponent(ue3MinRadiusComp);
				mainComp.addSubComponent(ue3MaxRadiusComp);
			} else {
				mainComp.addProp("bOverrideAttenuation", true);
				mainComp.addProp("AttenuationOverrides", attenuation.toString(mapConverter.getOutputGame().getUeVersion()));
			}
		}

		// UE3 only
		if (isTo(UE3)) {
			this.addConvProperty("AmbientProperties", mainComp.getReference(this.mapConverter.getOutputGame().getUeVersion()));
			this.addConvProperty("SoundNodeInstance", mainComp.getReference(this.mapConverter.getOutputGame().getUeVersion()));
		} else {
			mainComp.addProp("RootComponent", mainComp.getReference(this.mapConverter.getOutputGame().getUeVersion()));
		}

		return mainComp;
	}

	/**
	 *
	 * @return T3d value
	 */
	public String toT3d() {

		if (ambientSound == null) {
			return super.toString();
		}

		//if original class is not stricly a t3dsound (lights in UE1/UE2 with sound properties set), add suffix in name
		// e.g: Light4 -> Light4Sound
		if (!name.contains("Sound") && isFrom(UE1, UE2)) {
			name += "Sound";
		}

		this.t3dClass = isTo(UE3)?UE3_AmbientSoundActor.AmbientSoundSimple.name():"AmbientSound";
		final Component mainComp = buildMainAudioComponent();
		this.addComponent(mainComp);

		sbf.append(super.toT3dNew());

		return super.toString();
	}
}
