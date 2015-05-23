/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.HSVColor;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.ue1.KillZType;
import org.xtx.ut4converter.ucore.ue1.ZoneEffect;

/**
 * Zone info actor used
 * in Unreal Engine 1 and 2
 * @author XtremeXp
 */
public class T3DZoneInfo extends T3DActor {
    
    HSVColor ambientColor;
    
    String locationName;
    String zoneTag;
    
    Boolean bClearToFogColor;
    Boolean bTerrainZone;
    Boolean bDistanceFog;
    Boolean bFogZone;
    
    RGBColor distanceFogColor;
    Double distanceFogStart;
    Double distanceFogEnd;
    Double distanceFogBlend;
    Double killZ;
    
    ZoneEffect zoneEffect;
    
    KillZType killzType = KillZType.KILLZ_None;
    
    public T3DZoneInfo(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
    }
    
    @Override
    public boolean analyseT3DData(String line){
        
        // DistanceFogColor=(B=172,G=202,R=221)
        if(line.startsWith("DistanceFogColor=")){
            distanceFogColor = T3DUtils.getRGBColor(line);
        }
        
        else if(line.startsWith("bDistanceFog=")){
            bDistanceFog = T3DUtils.getBoolean(line);
        }
        
        else if(line.startsWith("bTerrainZone=")){
            bTerrainZone = T3DUtils.getBoolean(line);
        }
        
        else if(line.startsWith("bFogZone=")){
            bFogZone = T3DUtils.getBoolean(line);
        }
        
        else if(line.startsWith("DistanceFogStart=")){
            distanceFogStart = T3DUtils.getDouble(line);
        }
        
        else if(line.startsWith("DistanceFogEnd=")){
            distanceFogEnd = T3DUtils.getDouble(line);
        }
        
        else if(line.startsWith("DistanceFogBlend=")){
            distanceFogBlend = T3DUtils.getDouble(line);
        }
        
        else if(line.startsWith("ZoneTag=")){
            zoneTag = T3DUtils.getString(line);
        }
        
        else if(line.startsWith("LocationName=")){
            locationName = T3DUtils.getString(line);
        }
        
        else if(line.startsWith("AmbientBrightness=")){
            ambientColor = ambientColor != null ? ambientColor : HSVColor.getDefaultUE12Color();
            ambientColor.V = T3DUtils.getFloat(line);
        }
        
        else if(line.startsWith("AmbientHue=")){
            ambientColor = ambientColor != null ? ambientColor : HSVColor.getDefaultUE12Color();
            ambientColor.H = T3DUtils.getFloat(line);
        }
        
        else if(line.startsWith("AmbientSaturation=")){
            ambientColor = ambientColor != null ? ambientColor : HSVColor.getDefaultUE12Color();
            ambientColor.S = T3DUtils.getFloat(line);
        }

        return false;
    }
    
    @Override
    public void convert(){
        
        if(distanceFogColor != null){
            distanceFogColor.toOneRange();
        }
    }
    
    @Override
    public void scale(Double newScale){
        
        if(distanceFogEnd != null){
            distanceFogEnd *= newScale;
        }
        
        if(distanceFogStart != null){
            distanceFogStart *= newScale;
        }
    }
    
    @Override
    public String toString(){
        
        if(mapConverter.isTo(UTGames.UnrealEngine.UE3, UTGames.UnrealEngine.UE4)){
            
            // replace with postprocess volume if light or fog info set
            if(distanceFogColor != null || ambientColor != null){
                T3DPostProcessVolume ppv = new T3DPostProcessVolume(mapConverter, this);
                replaceWith(ppv);
            }
            
        } else {
            // not implemented yet
        }
        
        return null;
    }
    
}
