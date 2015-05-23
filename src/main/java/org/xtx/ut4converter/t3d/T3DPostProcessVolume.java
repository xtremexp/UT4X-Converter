/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.RGBColor;

/**
 *
 * @author XtremeXp
 */
public class T3DPostProcessVolume extends T3DBrush {
    
    final String ZONE_INFO_CLASS = "ZoneInfo";
    
    Film film;
    DepthOfField dof;
    AutoExposure autoExposure;
    
    class Film {
    
        RGBColor tint;
        Double saturation;
        Double contrast;
        RGBColor tintShadow;
        Double tintShadowBlind;
        Double tintShadowAmount;
        Double crushShadows;
        
    }
    
    class DepthOfField{
        DOFMethod method;
        Double focalDistance;
        Double focalRegion;
        Double nearTransitionRegion;
        Double farTransitionRegion;
        Double scale;
        Double maxBokehZise;
        Double nearBlurSize;
        Double farBlurSize;
    }
    
    class AutoExposure{
        Float minBrightness;
        Float maxBrightness;
        Float exposureBias;
    }
    
    enum DOFMethod {
        Gaussian,
        BokedDOH
    }
    
    public T3DPostProcessVolume(MapConverter mapConverter, String t3dClass) {
        super(mapConverter, t3dClass);
        
        initialise();
    }
    
    public T3DPostProcessVolume(MapConverter mapConverter, T3DZoneInfo zoneInfo) {
        super(mapConverter, zoneInfo.t3dClass);
        
        initialise();
        forceToBox(400d);
        
        if(zoneInfo.distanceFogColor != null){
            film.tint = zoneInfo.distanceFogColor;
        }
        
        if(zoneInfo.ambientColor != null && zoneInfo.ambientColor.V > 0){
            autoExposure.minBrightness = zoneInfo.ambientColor.V / 255;
        }
    }
    
    private void initialise(){
        brushClass = BrushClass.PostProcessVolume;
        film = new Film();
        dof = new DepthOfField();
        autoExposure = new AutoExposure();
    }
    
}
