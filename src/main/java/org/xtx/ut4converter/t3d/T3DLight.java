/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.tools.ImageUtils;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Class for converting lights.
 * @author XtremeXp
 * TODO handle "light skins" (coronas)
 */
public class T3DLight extends T3DSound {

    /**
     * Light Effect
     * Used in unreal engine 1 / 2
     */
    enum UE12_LightEffect {
        LE_None,
        LE_TorchWaver,
        LE_FireWaver,
        LE_WateryShimmer,
        LE_Searchlight,
        LE_SlowWave,
        LE_FastWave,
        LE_CloudCast,
        LE_StaticSpot,
        LE_Shock,
        LE_Disco,
        LE_Warp,
        LE_Spotlight,
        LE_NonIncidence,
        LE_Shell,
        LE_OmniBumpMap,
        LE_Interference,
        LE_Cylinder,
        LE_Rotor,
        LE_Unused,
        // Unreal Engine 2 new light effects
        LE_Sunlight,
        LE_Spotlight2,
        LE_SquareSpotlight,
        LE_QuadraticNonIncidence
        ;
    }
    
    /**
     * Light Type 
     * UE 1 / 2
     */
    enum UE12_LightType {
        LT_None,
        LT_Steady,
        LT_Pulse,
        LT_Blink,
        LT_Flicker,
        LT_Strobe,
        LT_BackdropLight,
        LT_SubtlePulse,
        LT_TexturePaletteLoop,
        LT_TexturePaletteOnce
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
     * Light actors for UT99.
     * Basically are normal lights with either some skin
     * or special light effect + type
     * TODO move out this to some "core class"
     */
    public static enum UE12_LightActors {
        Light,
        ChargeLight,
        DistanceLightning,
        FlashLightBeam,
        OverHeatLight,
        QueenTeleportLight,
        SightLight,
        SpotLight,
        TorchFlame,
        TriggerLight,
        TriggerLightRad,
        WeaponLight,
        EffectLight,
        PurpleLight;
    }
    
    /**
     * Unreal Engine 4 light actors
     * TODO check UE3 (might be same)
     */
    public static enum UE4_LightActor {
        PointLight,
        SkyLight,
        SpotLight,
        DirectionalLight;
    }
    
    /**
     * Default light effect
     * Used in Unreal Engine 1 / 2
     */
    UE12_LightEffect lightEffect = UE12_LightEffect.LE_None;
    
    /**
     * Default light type (TODO check isdefaut)
     * Used in Unreal Engine 1 / 2
     */
    UE12_LightType lightType = UE12_LightType.LT_Steady;
    
    
    /**
     * For spot lights
     * UE3 only default 0
     */
    Double innerConeAngle;
    
    /**
     * UE1/2: LightCone - default 128
     * UE4: OuterConeAngle - default 44
     */
    Double outerConeAngle;
    
    // *** Unreal Engine 1 / 2 Properties ***
    // Skin=Texture'GenFX.LensFlar.3'
    UPackageRessource skin;
    

    boolean isCorona;

    /**
     * UE1/2 brightness
     * UE1 range: 0-255
     * UE2 range: 0-Infinite
     */
    float brightness;
    
    /**
     * UE1/2 hue
     */
    float hue;

    /**
     * UE1/2 saturation
     */
    float saturation;
    float radius;
    
    // *** Unreal Engine 3 / 4 Properties ***
    
    /**
     * Range Value 0->1
     */
    float red, green, blue, alpha = 255;
    
    /**
     * Unreal Engine 4
     * Default Intensity
     */
    private static final int DEFAULT_INTENSITY = 5000;
    
    
    /**
     * Default Attenuation Radius for unreal engine 4
     */
    private static final float DEFAULT_ATTENUATION_RADIUS = 1000f;
    
    /**
     * How much attenuation radius will be multiplied
     * Attenuation Radius = Radius(UE123) * Factor
     */
    private static final int UE123_UE4_ATTENUATION_RADIUS_FACTOR = 20;

    Double intensity;
    
    
    /**
     * UE4 Only
     */
    Double lightFalloffExponent;
    
    /**
     * Attenuation Radius
     * (dunno how it works yet compared with sourceRadius)
     */
    float attenuationRadius = DEFAULT_ATTENUATION_RADIUS;
    
   
    
    /**
     *
     * @param mc
     * @param t3dClass
     */
    public T3DLight(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
        
        // Default Values when u put some light in UE1/UE2 editor
        this.hue = 0;
        this.saturation = 255f;
        this.brightness = 64f;
        this.radius = 64f;
        
        
        this.intensity = 60d;
        this.lightFalloffExponent = 2.5d;
    }
    
    @Override
    public boolean analyseT3DData(String line) {
        
        // TODO skip this if UT3 and class not a light class
        // unlike in UE1/UE2 all actors do not have lightning properties in UT3
        
        if(line.startsWith("LightBrightness")){
            brightness = T3DUtils.getFloat(line);
        }
        
        else if(line.startsWith("LightHue")){
            hue = T3DUtils.getFloat(line);
        }
        
        else if(line.startsWith("LightSaturation")){
            saturation = T3DUtils.getFloat(line);
        }
        
        else if(line.startsWith("LightRadius")){
            radius = T3DUtils.getFloat(line);
        } 
        
        else if(line.startsWith("LightEffect")){
            lightEffect = UE12_LightEffect.valueOf(line.split("\\=")[1]);
        }
        
        else if(line.startsWith("LightType")){
            lightType = UE12_LightType.valueOf(line.split("\\=")[1]);
        }
        
        else if(line.startsWith("InnerConeAngle")){
            innerConeAngle = T3DUtils.getDouble(line);
        }
        
        else if(line.startsWith("LightCone") || line.startsWith("OuterConeAngle")){
            outerConeAngle = T3DUtils.getDouble(line);
        }
        
        else if(line.startsWith("bCorona=")){
            isCorona = "true".equals(line.split("\\=")[1].toLowerCase());
        }
        
        
        // UT3
        // LightColor=(B=58,G=152,R=197,A=0)
        else if(line.startsWith("LightColor=")){
            RGBColor rgbColor = T3DUtils.getRGBColor(line);
            this.green = rgbColor.G;
            this.blue = rgbColor.B;
            this.red = rgbColor.R;
            this.alpha = rgbColor.A;
        }
        
        else if(line.startsWith("FalloffExponent=")){
            lightFalloffExponent = T3DUtils.getDouble(line);
        }
        
        // Skin=Texture'GenFX.LensFlar.3'
        else if(line.startsWith("Skin=")){
            skin = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.TEXTURE);
        }
        
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    /**
     * Tell if this light is spotlight or not
     * @return 
     */
    private boolean isSpotLight(){
        return t3dClass.equals(UE4_LightActor.SpotLight.name()) 
                || lightEffect == UE12_LightEffect.LE_Spotlight 
                || lightEffect == UE12_LightEffect.LE_StaticSpot
                || lightEffect == UE12_LightEffect.LE_Spotlight2
                || lightEffect == UE12_LightEffect.LE_SquareSpotlight;
    }
    
    /**
     * Tells if current light is sunlight
     * TODO convert to SunLight actor for UT4 if true
     * @return 
     */
    private boolean isSunLight(){
        return lightEffect == UE12_LightEffect.LE_Sunlight;
    }
    
    /**
     * 
     * @return 
     */
    private String getConvertedLightClass(){
        
        if(isSpotLight()){
            return UE4_LightActor.SpotLight.name();
        }
        
        else {
            return UE4_LightActor.PointLight.name();
        }
        
    }
    
    private UE4_Mobility getMobility() {
        
        // force to stationary until we fix
        // perf issues with stationary mobility
        /**
        if(lightEffect == UE12_LightEffect.LE_None 
                || lightEffect == UE12_LightEffect.LE_StaticSpot 
                || lightEffect == UE12_LightEffect.LE_Unused
                || lightEffect == UE12_LightEffect.LE_Cylinder
                ){
            return UE4_Mobility.Static;
        } else {
            return UE4_Mobility.Stationary;
        }*/
        
        return UE4_Mobility.Static;
    }
    
    /**
     * Default light source lenght for 
     * converted UE1/2 light with cylinder light effect
     */
    final int lightEffectCylinderSourceLength = 100;
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        
        if(mapConverter.toUnrealEngine4()){
            sbf.append(IDT).append("Begin Actor Class=").append(getConvertedLightClass()).append(" Name=").append(name).append("\n");
            
            sbf.append(IDT).append("\tBegin Object Class=").append(isSpotLight()?"SpotLightComponent":"PointLightComponent").append(" Name=\"LightComponent0\"\n");
            sbf.append(IDT).append("\tEnd Object\n");
            
            sbf.append(IDT).append("\tBegin Object Name=\"LightComponent0\"\n");
            
            
            sbf.append(IDT).append("\t\tbUseInverseSquaredFalloff=False\n");
            sbf.append(IDT).append("\t\tLightFalloffExponent=").append(lightFalloffExponent).append("\n");
            

            if(lightEffect == UE12_LightEffect.LE_Cylinder){
                sbf.append(IDT).append("\t\tSourceLength=").append(attenuationRadius/2).append("\n");
                sbf.append(IDT).append("\t\tSourceRadius=").append(attenuationRadius/2).append("\n");
            } else {
                sbf.append(IDT).append("\t\tSourceRadius=").append(radius).append("\n");
            }
            
            sbf.append(IDT).append("\t\tAttenuationRadius=").append(attenuationRadius).append("\n");
            sbf.append(IDT).append("\t\tLightColor=(B=").append(blue).append(",G=").append(green).append(",R=").append(red).append(",A=").append(alpha).append(")\n");
            
            if(intensity != null){
                sbf.append(IDT).append("\t\tIntensity=").append(intensity).append("\n");
            }
            
            if(isSpotLight()){
                // 128 is default angle for UE1/2 (in 0 -> 255 range) = 90 in (0 -> 180° range)
                Double angle = outerConeAngle != null ? outerConeAngle : 90d;  
                sbf.append(IDT).append("\t\tInnerConeAngle=").append((angle/2)).append("\n");
                sbf.append(IDT).append("\t\tOuterConeAngle=").append(angle).append("\n");
            }
            
            sbf.append(IDT).append("\t\tMobility=").append(getMobility().name()).append("\n");
            
            writeLocRotAndScale();
            sbf.append(IDT).append("\tEnd Object\n");
            
            sbf.append(IDT).append("\t").append(isSpotLight()?"SpotLightComponent":"PointLightComponent").append("\"LightComponent0\"\n");
            sbf.append(IDT).append("\tLightComponent=\"LightComponent0\"\n");

            writeEndActor();
        }
        

        
        
        return super.toString();
    }
    
    
    /**
     * 
     */
    @Override
    public void convert(){
        
        if(isCorona && skin != null){
            skin.export(UTPackageExtractor.getExtractor(mapConverter, skin));
            replaceWith(T3DEmitter.createLensFlare(mapConverter, this));
            return;
        }
            
        // UE1 has brightness range 0-255
        // but UE2 has not limit for brightness
        // so we make sure brightness does not go above 255
        // but give extra intensity "boost"
        // e.g: brightness of 1000 --> brightness = 255 and intensity = 60 + log(1000) * 6 = 60 + 3 * 6 = 78
        if(brightness > 255){
            intensity += Math.log(brightness - 255) * 6; // using log because brightness can be nearly infinite
        }

        brightness = Math.min(brightness, 255);

        // Old engines using hue, brightness, saturation system
        if(mapConverter.isFromUE1UE2ToUE3UE4()){
            convertColorToRGB();
            
            if(isCorona){
                radius = 0;
            }
        }
        
        if(intensity != null && mapConverter.brightnessFactor != null){
            intensity *= mapConverter.brightnessFactor;
        }
        

        attenuationRadius = radius;

        attenuationRadius *= UE123_UE4_ATTENUATION_RADIUS_FACTOR;

        if(outerConeAngle != null){
            // 0 -> 255 range to 0 -> 180 range
            outerConeAngle *= (255d/360d)/2;
        }
        
        
        super.convert();
    }
    
    /**
     * Convert Hue Saturation Brightness values from old UT2004 lightning system
     * to Red Blue Green values with new UT3 lightning system
     * @param h Hue, float Range: 0->255
     * @param s Saturation, float Range: 0->255
     * @param b Brightness, float Range: 0->255
     */
    private void convertColorToRGB()
    {
        // Saturation is reversed in Unreal Engine 1/2 compared with standards ...
        saturation = Math.abs(saturation - 255);

        RGBColor rgb = ImageUtils.HSVToLinearRGB(hue, saturation, brightness);
        
        this.red = rgb.R;
        this.blue = rgb.B;
        this.green = rgb.G;
    }
    
    @Override
    public void scale(Double newScale){
        
        attenuationRadius *= newScale;
        radius *= newScale;
        
        if(intensity != null){
            //intensity *= newScale;
        }
        
        super.scale(newScale);
    }
    
    @Override
    public boolean isValidWriting(){
        return brightness > 0 && super.isValidWriting();
    }

}
