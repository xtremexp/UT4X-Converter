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
import java.util.Random;

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
	private UPackageRessource ambientSound;


	private final AttenuationSettings attenuation = new AttenuationSettings();

	/**
	 * Sound volume
	 * UE1/2: Range 0->255 (default 190 for AmbientSound actors else 128 (e.g: Light))
	 * UE3/4: Range 0->1 (default: 1)
	 */
	private Double soundVolume;

	/**
	 * Sound pitch (sound frequency modification)
	 * UE1/2: Range 0->255 (default 64)
	 * UE3/4: Range 0->1 (default: 1)
	 */
	private Double soundPitch;

	/**
	 * Sound radius
	 * UE1/2: Range 0->255 (default 64 for AmbientSound actors else 32 (e.g: Light))
	 * UE3/4: Range 0->1 with MinRadius and MaxRadius properties
	 */
	private Double soundRadius;

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
		 * UE3/UE4: default true
		 */
		Boolean bSpatialize;

		/**
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
		Double coneOffset;

		/**
		 * in UE4: "MaxRadius" ? (5000 default) UE3: default 3600
		 */
		Double fallOffDistance;

		/**
		 * UE3: LPFMinRadius (default 1500) UE4: default 3000
		 */
		Double LPFRadiusMin;

		/**
		 * UE3: LPFMaxRadius (default 2500) UE4: default 6000
		 */
		Double LPFRadiusMax;

		/**
		 * UE3: bAttenuateWithLowPassFilter UE4: default false
		 */
		Boolean bAttenuateWithLPF;

		public String toString(UnrealEngine engine) {
			if (engine.version <= 3) {
				return null;
			}
			// only UE4 support right now
			else {
				StringBuilder s = new StringBuilder("AttenuationOverrides=(");

				Map<String, Object> props = new HashMap<>();

				props.put("DistanceAlgorithm", distanceAlgorithm.name());

				props.put("bAttenuateWithLPF", bAttenuateWithLPF);
				props.put("bSpatialize", bSpatialize);

				if (bSpatialize != null && bSpatialize) {
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
				s.append(")\n");
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
		if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {

			soundPitch = 64d;
			transientSoundRadius = 0d;
			transientSoundVolume = 1d;

			// default properties values for AmbientSound actor
			if (this.getClass() == T3DSound.class) {
				soundRadius = 64d;
				soundVolume = 190d;
			}
			// other actors (such as lights, often used also as sounds)
			else {
				soundRadius = 32d;
				soundVolume = 128d;
			}
		}
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.startsWith("SoundRadius")) {
			soundRadius = T3DUtils.getDouble(line);
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
		else if (line.startsWith("Min=") && mapConverter.isFrom(UnrealEngine.UE3)) {

			Double min = T3DUtils.getDouble(line);

			if (AttenuationSettings.DistributionType.DistributionPitch.name().equals(currentSubObjectName)) {
				soundPitch = min;
			} else if (AttenuationSettings.DistributionType.DistributionVolume.name().equals(currentSubObjectName)) {
				soundVolume = min;
			} else if (AttenuationSettings.DistributionType.DistributionLPFMinRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMin = min;
			} else if (AttenuationSettings.DistributionType.DistributionLPFMaxRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMax = min;
			} else if (AttenuationSettings.DistributionType.DistributionMaxRadius.name().equals(currentSubObjectName)) {
				attenuation.omniRadius = min;
			} else if (AttenuationSettings.DistributionType.DistributionMinRadius.name().equals(currentSubObjectName)) {
				attenuation.omniRadius = min;
			}
		}

		// UE3
		else if (line.startsWith("Max=") && mapConverter.isFrom(UnrealEngine.UE3)) {

			Double max = T3DUtils.getDouble(line);

			if (AttenuationSettings.DistributionType.DistributionPitch.name().equals(currentSubObjectName)) {
				soundPitch = getMax(soundPitch, max);
			} else if (AttenuationSettings.DistributionType.DistributionVolume.name().equals(currentSubObjectName)) {
				soundVolume = getMax(soundVolume, max);
			} else if (AttenuationSettings.DistributionType.DistributionLPFMinRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMin = getMax(attenuation.LPFRadiusMin, max);
			} else if (AttenuationSettings.DistributionType.DistributionLPFMaxRadius.name().equals(currentSubObjectName)) {
				attenuation.LPFRadiusMax = getMax(attenuation.LPFRadiusMax, max);
			} else if (AttenuationSettings.DistributionType.DistributionMaxRadius.name().equals(currentSubObjectName)) {
				attenuation.omniRadius = getMax(attenuation.omniRadius, max);
			} else if (AttenuationSettings.DistributionType.DistributionMinRadius.name().equals(currentSubObjectName)) {
				attenuation.omniRadius = getMax(attenuation.omniRadius, max);
			}
		}

		// UE1/2: AmbientSound=Sound'AmbAncient.Looping.Stower51'
		// UE3: Wave=SoundNodeWave'A_Ambient_Loops.Water.water_drain01'
		else if (line.startsWith("AmbientSound=") || line.startsWith("Wave=")) {
			ambientSound = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.SOUND);
		} else {
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

		soundRadius *= newScale;
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

				if (mapConverter.soundVolumeFactor != null) {
					soundVolume *= mapConverter.soundVolumeFactor;
				}
			}


			if (soundPitch != null) {
				soundPitch /= 64D; // default pitch is 64 in UE1/2
			}


			attenuation.attenuationShapeExtents.x = soundRadius.intValue();

			// need to scale up radius for UE1/2 -> UE3/4
			// tested DM-ArcaneTemple (UT99)
			soundRadius *= 24;

			// UE4
			attenuation.fallOffDistance = attenuation.attenuationShapeExtents.x * 24;
		}

		if (mapConverter.convertSounds() && ambientSound != null) {
			ambientSound.export(UTPackageExtractor.getExtractor(mapConverter, ambientSound));
		}

		super.convert();
	}

	void writeAudioComponent(){
		sbf.append(IDT).append("\tBegin Object Class=AudioComponent Name=\"AudioComponent0\"\n");
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tBegin Object Name=\"AudioComponent0\"\n");

		sbf.append(IDT).append("\t\tbOverrideAttenuation=True\n");
		// ????
		//sbf.append(IDT).append("\t\t").append(attenuation.toString(mapConverter.getOutputGame().engine));

		if (ambientSound != null) {
			sbf.append(IDT).append("\t\tSound=SoundCue'").append(ambientSound.getConvertedName(mapConverter)).append("'\n");
		}

		if (soundVolume != null) {
			sbf.append(IDT).append("\t\tVolumeMultiplier=").append(soundVolume).append("\n");
		}

		if (soundPitch != null) {
			sbf.append(IDT).append("\t\tPitchMultiplier=").append(soundPitch).append("\n");
		}

		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");
	}

	/**
	 *
	 * @return T3d value
	 */
	public String toT3d() {

		if (ambientSound == null) {
			return super.toString();
		}

		//if original class is not stricly a t3dsound (lights in UE1/UE1 with sound properties set), add suffix in name
		// e.g: Light4 -> Light4Sound
		if (!name.contains("Sound")) {
			name += "Sound";
		}

		if (mapConverter.isTo(UnrealEngine.UE4)) {

			sbf.append(IDT).append("Begin Actor Class=AmbientSound Name=").append(name).append("\n");

			writeAudioComponent();
			sbf.append(IDT).append("\tAudioComponent=AudioComponent0\n");
			sbf.append(IDT).append("\tRootComponent=AudioComponent0\n");
			writeEndActor();
		} else if (mapConverter.isTo(UnrealEngine.UE3)) {

			//
			sbf.append(IDT).append("Begin Actor Class=").append(UE3_AmbientSoundActor.AmbientSoundSimple.name()).append(" Name=").append(name).append("\n");
			sbf.append(IDT).append("\tBegin Object Class=SoundNodeAmbient Name=SoundNodeAmbient_5 ObjName=SoundNodeAmbient_5\n");

			// Min Sound Radius
			int idxDistMinRad = new Random().nextInt(10000);
			sbf.append(IDT).append("\t\tBegin Object Class=DistributionFloatUniform Name=DistributionMinRadius ObjName=DistributionFloatUniform_").append(idxDistMinRad).append("\n");
			sbf.append(IDT).append("\t\t\tName=\"DistributionFloatUniform_").append(idxDistMinRad).append("\"\n");
			sbf.append(IDT).append("\t\tEnd Object\n");

			// Max Sound Radius
			int idxDistMaxRad = new Random().nextInt(10000);
			sbf.append(IDT).append("\t\tBegin Object Class=DistributionFloatUniform Name=DistributionMinRadius ObjName=DistributionFloatUniform_").append(idxDistMaxRad).append("\n");
			sbf.append(IDT).append("\t\t\tMin=").append(soundRadius).append("\n");
			sbf.append(IDT).append("\t\t\tMax=").append(soundRadius).append("\n");
			sbf.append(IDT).append("\t\t\tName=\"DistributionFloatUniform_").append(idxDistMaxRad).append("\"\n");
			sbf.append(IDT).append("\t\tEnd Object\n");

			// Volume
			int idxVolumeMod = new Random().nextInt(10000);
			sbf.append(IDT).append("\t\tBegin Object Class=DistributionFloatUniform Name=DistributionVolume ObjName=DistributionFloatUniform_").append(idxVolumeMod).append("\n");
			sbf.append(IDT).append("\t\t\tMin=").append(soundVolume).append("\n");
			sbf.append(IDT).append("\t\t\tMax=").append(soundVolume).append("\n");
			sbf.append(IDT).append("\t\t\tName=\"DistributionFloatUniform_").append(idxVolumeMod).append("\"\n");
			sbf.append(IDT).append("\t\tEnd Object\n");

			// Pitch
			int idxPitchMod = new Random().nextInt(10000);
			sbf.append(IDT).append("\t\tBegin Object Class=DistributionFloatUniform Name=DistributionVolume ObjName=DistributionFloatUniform_").append(idxPitchMod).append("\n");
			sbf.append(IDT).append("\t\t\tMin=").append(soundPitch).append("\n");
			sbf.append(IDT).append("\t\t\tMax=").append(soundPitch).append("\n");
			sbf.append(IDT).append("\t\t\tName=\"DistributionFloatUniform_").append(idxPitchMod).append("\"\n");
			sbf.append(IDT).append("\t\tEnd Object\n");


			final String maxRadStr = soundRadius + "," + soundRadius + "," + soundRadius + "," + soundRadius + "," + soundRadius + "," + soundRadius;
			final String volModStr = "1.000000," + soundVolume + ",1.000000," + soundVolume + ",1.000000," + soundVolume;
			final String pitchModStr = soundPitch + "," + soundPitch + "," + soundPitch + "," + soundPitch + "," + soundPitch + "," + soundPitch;

			// E.G: MaxRadius=(Distribution=DistributionFloatUniform'DistributionFloatUniform_1169',LookupTable=(3333.000000,3333.000000,3333.000000,3333.000000,3333.000000,3333.000000))
			sbf.append(IDT).append("\t\tMinRadius=(Distribution=DistributionFloatUniform'DistributionFloatUniform_").append(idxDistMinRad).append("',LookupTable=(0.0,0.0,0.0,0.0,0.0,0.0))\n");
			sbf.append(IDT).append("\t\tMaxRadius=(Distribution=DistributionFloatUniform'DistributionFloatUniform_").append(idxDistMaxRad).append("',LookupTable=(").append(maxRadStr).append("))\n");
			sbf.append(IDT).append("\t\tVolumeModulation=(Distribution=DistributionFloatUniform'DistributionFloatUniform_").append(idxVolumeMod).append("',LookupTable=(").append(volModStr).append("))\n");
			sbf.append(IDT).append("\t\tPitchModulation=(Distribution=DistributionFloatUniform'DistributionFloatUniform_").append(idxPitchMod).append("',LookupTable=(").append(pitchModStr).append("))\n");

			sbf.append(IDT).append("\t\tWave=SoundNodeWave'").append(ambientSound.getConvertedName(mapConverter)).append("'\n");
			sbf.append(IDT).append("\t\tName=\"SoundNodeAmbient_5\"\n");
			sbf.append(IDT).append("\tEnd Object\n");

			sbf.append(IDT).append("\tAmbientProperties=SoundNodeAmbient'SoundNodeAmbient_5'\n");
			sbf.append(IDT).append("\tSoundNodeInstance=SoundNodeAmbient'SoundNodeAmbient_5'\n");

			writeLocRotAndScale();
			writeEndActor();
		}

		return super.toString();
	}
}
