/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.ImageUtils;

/**
 * Class for converting lights.
 * @author XtremeXp
 */
public class T3DLight extends T3DActor {

    // *** Unreal Engine 1 / 2 Properties ***
    // Skin=Texture'GenFX.LensFlar.3'
    String skin;

    int brightness;
    int hue;

    int saturation;
    int radius;
    
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
    private static final int DEFAULT_ATTENUATION_RADIUS = 1000;
    
    /**
     * How much attenuation radius will be multiplied
     * Attenuation Radius = Radius(UE123) * Factor
     */
    private static final int UE123_UE4_ATTENUATION_RADIUS_FACTOR = 15;

    Double intensity;
    
    
    /**
     * UE4 Only
     */
    Double lightFalloffExponent;
    
    /**
     * Attenuation Radius
     * (dunno how it works yet compared with sourceRadius)
     */
    int attenuationRadius = DEFAULT_ATTENUATION_RADIUS;
    
    /**
    * A color represented by the four components Hue, Saturation, Value, and Alpha.
    * H must be between 0 and 360 (degrees).
    * S must be between 0 and 1.
    * V and A are normally between 0 and 1, but may exceed that range to give bloom.
    */
   public static class HSVColor
   {
        /**
         * Hue, Saturatio, Value, Alpha
         */
        public float H, S, V, A = 1.f;

        /**
         *
         */
        public HSVColor(){
            
        }
        
        /**
         *
         * @param H
         * @param S
         * @param V
         */
        public HSVColor(float H, float S, float V) {
            this.H = H;
            this.S = S;
            this.V = V;
        }

        
   }
   
    /**
     * Defines RGB format
     */
    public static class RGBColor
   {
        /**
         * Red, Green, Blue, Alpha
         */
        public float R, G, B, A;

        /**
         *
         */
        public RGBColor(){
            
        }
        
        /**
         *
         * @param R Red value (0->1.0f)
         * @param G Green (0->1.0f)
         * @param B Blue (0->1.0f)
         */
        public RGBColor(float R, float G, float B) {
            this.R = R;
            this.G = G;
            this.B = B;
        }
   }
    
    /**
     *
     * @param mc
     */
    public T3DLight(MapConverter mc) {
        super(mc);
        
        // Default Values when u put some light in UE1/UE2 editor
        this.hue = 0;
        this.saturation = 255;
        this.brightness = 64;
        this.radius = 64;
        
        
        this.intensity = 100d;
        this.lightFalloffExponent = 2.5d;
    }
    
    @Override
    public boolean analyseT3DData(String line) {
        
        if(line.contains("LightBrightness")){
            brightness = T3DUtils.getShort(line);
        }
        
        else if(line.contains("LightHue")){
            hue = T3DUtils.getShort(line);
        }
        
        else if(line.contains("LightSaturation")){
            saturation = T3DUtils.getShort(line);
        }
        
        else if(line.contains("LightRadius")){
            radius = T3DUtils.getShort(line);
        } 
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        
        if(mapConverter.toUnrealEngine4()){
            sbf.append(IDT).append("Begin Actor Class=PointLight Name=").append(name).append("\n");
            
            sbf.append(IDT).append("\tBegin Object Class=PointLightComponent Name=\"LightComponent0\"\n");
            sbf.append(IDT).append("\tEnd Object\n");
            
            sbf.append(IDT).append("\tBegin Object Name=\"LightComponent0\"\n");
            
            
            sbf.append(IDT).append("\t\tbUseInverseSquaredFalloff=False\n");
            sbf.append(IDT).append("\t\tLightFalloffExponent=").append(lightFalloffExponent).append("\n");
            
            sbf.append(IDT).append("\t\tSourceRadius=").append(radius).append("\n");
            sbf.append(IDT).append("\t\tAttenuationRadius=").append(attenuationRadius).append("\n");
            sbf.append(IDT).append("\t\tLightColor=(B=").append(blue).append(",G=").append(green).append(",R=").append(red).append(",A=").append(alpha).append(")\n");
            
            if(intensity != null){
                sbf.append(IDT).append("\t\tIntensity=").append(intensity).append("\n");
            }
            
            sbf.append(IDT).append("\t\tMobility=Static\n");
            
            writeLocRotAndScale();
            sbf.append(IDT).append("\tEnd Object\n");
            
            sbf.append(IDT).append("\tPointLightComponent=\"LightComponent0\"\n");
            sbf.append(IDT).append("\tLightComponent=\"LightComponent0\"\n");

            writeEndActor();
        }
        

        
        
        return sbf.toString();
    }
    
    
    /**
     * 
     */
    @Override
    public void convert(){
        
        // Convert HSB to RGB
        if(mapConverter.isFromUE1UE2ToUE3UE4()){
            convertColorToRGB();
            
            attenuationRadius = radius;
            
            attenuationRadius *= UE123_UE4_ATTENUATION_RADIUS_FACTOR;
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
    public boolean isValid(){
        // Lights with brightness = 0 are corona lights (light with skin) in UE1/UE2
        if(mapConverter.isFromUE1UE2ToUE3UE4()){
            return true; //return brightness < 255 && super.isValid();
        } else {
            return true;
        }
    }
    
    
}
