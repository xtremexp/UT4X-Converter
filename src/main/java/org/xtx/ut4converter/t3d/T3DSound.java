/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Class for converting any actor related to sound (might be music as well)
 * TODO merge with T3D Actor and delete this class
 * because any actors can have sound property
 * @author XtremeXp
 */
public class T3DSound extends T3DActor {

    /**
     * UE1, UE4
     */
    UPackageRessource ambientSound;
    
    AttenuationSettings attenuation = new AttenuationSettings();
    
    /**
     * UE1/2: (default: 190 max 255)
     * UE3:
     * UE4: 
     */
    Double soundVolume;
    
    /**
     * UE1/2: default 64
     * UE3: default 1
     * UE4: "Pitch Multiplier" default 1
     */
    Double soundPitch;
    
    /**
     * UE3/4
     */
    enum DistanceAlgorithm {
        ATTENUATION_Linear,
        ATTENUATION_Logarithmic,
        ATTENUATION_Inverse,
        ATTENUATION_LogReverse,
        ATTENUATION_NaturalSound
    }
    
    /**
     * Shape of "sound volume" only Sphere available for UE3
     */
    enum Shape {
        Sphere, Capsule, Box, Cone
    }
    
    /**
     * TODO move out to ucore package ?
     */
    class AttenuationSettings {
        
        /**
         * UE3/UE4: default true
         */
        Boolean bAttenuate;
        
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
         * Only with UE4
         * and distance algorithm ATTENUATION_NaturalSound
         */
        Double dBAttenuationAtMax = -60d;
        
        /**
         * Only in UE4, in UE3 it seems to be sphere by default
         */
        Shape attenuationShape = Shape.Sphere;
        
        
        /**
         * UE4: is radius
         * UE3: "MinRadius" (400 default)
         * 
         */
        Vector3d attenuationShapeExtents = new Vector3d(400d, 0, 0);
        
        /**
         * UE4 only with attenuation shape "Cone" (default : 0)
         */
        Double coneOffset;
        
        /**
         * in UE4: "MaxRadius" ? (5000 default)
         * UE3: default 3600
         */
        Double fallOffDistance;
        
        /**
         * UE3: LPFMinRadius (default 1500)
         * UE4: default 3000
         */
        Double LPFRadiusMin;
        
        /**
         * UE3: LPFMaxRadius (default 2500)
         * UE4: default 6000
         */
        Double LPFRadiusMax;
        
        /**
         * UE3: bAttenuateWithLowPassFilter
         * UE4: default false
         */
        Boolean bAttenuateWithLPF;
        
        public String toString(UTGames.UnrealEngine engine){
            if(engine.version <= 3){
                return null;
            }
            // only UE4 support right now
            else 
            {
                StringBuilder s = new StringBuilder("AttenuationOverrides=(");
                
                Map<String, Object> props = new HashMap<>();
                
                props.put("bAttenuateWithLPF", bAttenuateWithLPF);
                props.put("bSpatialize", bSpatialize);
                
                if(bSpatialize != null && bSpatialize){
                    props.put("OmniRadius", omniRadius);
                }
                
                if(distanceAlgorithm == DistanceAlgorithm.ATTENUATION_NaturalSound){
                    props.put("dBAttenuationAtMax", dBAttenuationAtMax);
                }
                
                if(attenuationShape == Shape.Cone){
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
    
    /**
     *
     * @param mc
     * @param t3dClass
     */
    public T3DSound(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
        ue4RootCompType = T3DMatch.UE4_RCType.AUDIO;
        
        setDefaults();
    }
    
    /**
     * TODO some outside kind of UProperty class with defaults value for each UE version
     */
    private void setDefaults(){
        
        if(mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)){
            attenuation.attenuationShapeExtents.x = 64d; // Default Radius in UE1/2
            soundVolume = 190d; // Default volume in UE1/2
            soundPitch = 64d; // Default pitch in UE1/2
        }
    }
    
    @Override
    public boolean analyseT3DData(String line) {
        
        if(line.startsWith("SoundRadius")){
            attenuation.attenuationShapeExtents.x = T3DUtils.getDouble(line);
        }
        
        else if(line.startsWith("SoundVolume")){
            soundVolume = T3DUtils.getDouble(line);
        }
        
        else if(line.startsWith("SoundPitch")){
            soundPitch = T3DUtils.getDouble(line);
        }
        
        // AmbientSound=Sound'AmbAncient.Looping.Stower51'
        else if(line.startsWith("AmbientSound=")){
            ambientSound = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.SOUND);
        } 
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    @Override
    public void scale(Double newScale){
        
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
    public void convert(){
        
        if(mapConverter.isFromUE1UE2ToUE3UE4()){
            
            if(soundVolume != null){
                
                soundVolume /= 255D; // default volume is 190 in UE1/2, default is 1 in UE3/4
               
                // decreasing sound volume from UT2004 because seems "loudy" in UT4 ...
                if(mapConverter.isFrom(UTGames.UnrealEngine.UE2)){
                    soundVolume *= 0.15;
                }
                
                if(mapConverter.soundVolumeFactor != null){
                    soundVolume = Math.min(1, soundVolume * mapConverter.soundVolumeFactor);
                }
            }
            
            
            
            if(soundPitch != null){
                soundPitch /= 64D; // default pitch is 64 in UE1/2
            }
            
            // tested DM-ArcaneTemple (UT99)
            attenuation.fallOffDistance = attenuation.attenuationShapeExtents.x * 24;

        }
        
        if(mapConverter.convertSounds() && ambientSound != null){
            ambientSound.export(UTPackageExtractor.getExtractor(mapConverter, ambientSound));
        }
        
        super.convert();
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        
        if(ambientSound == null){
            return super.toString();
        }
        
        if(mapConverter.toUnrealEngine4()){
            
            if(!name.contains("Sound")){
                name += "Sound";
            }
            
            sbf.append(IDT).append("Begin Actor Class=AmbientSound Name=").append(name).append("\n");
            sbf.append(IDT).append("\tBegin Object Class=AudioComponent Name=\"AudioComponent0\"\n");
            sbf.append(IDT).append("\tEnd Object\n");
            sbf.append(IDT).append("\tBegin Object Name=\"AudioComponent0\"\n");
            
            sbf.append(IDT).append("\t\tbOverrideAttenuation=True\n");
            sbf.append(IDT).append("\t\t").append(attenuation.toString(mapConverter.getOutputGame().engine));
            
            if(ambientSound != null){
                sbf.append(IDT).append("\t\tSound=SoundCue'").append(ambientSound.getConvertedName(mapConverter)).append("'\n");
            }
            
            //bOverrideAttenuation=True
            if(soundVolume != null){
                sbf.append(IDT).append("\t\tVolumeMultiplier=").append(soundVolume).append("\n");
            }
            
            if(soundPitch != null){
                sbf.append(IDT).append("\t\tPitchMultiplier=").append(soundPitch).append("\n");
            }
            
            writeLocRotAndScale();
            sbf.append(IDT).append("\tEnd Object\n");
            sbf.append(IDT).append("\tAudioComponent=AudioComponent0\n");
            sbf.append(IDT).append("\tRootComponent=AudioComponent0\n");
            writeEndActor();
        }
        
        
        return super.toString();
    }
}
