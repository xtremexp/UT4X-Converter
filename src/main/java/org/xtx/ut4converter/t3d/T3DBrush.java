/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.Geometry;

/**
 * Generic Class for T3D brushes (includes movers as well)
 * @author XtremeXp
 */
public class T3DBrush extends T3DActor {

    BrushClass brushClass = BrushClass.BRUSH;
    
    protected enum BrushClass {
        BRUSH("Brush"),
        MOVER("Mover"),
        KILLZ_VOLUME("KillZVolume"),
        UT_PAIN_VOLUME("UTPainVolume"),
        UT_WATER_VOLUME("UTWaterVolume"),
        POST_PROCESS_VOLUME("PostProcessVolume");
        
        String className;
        
        BrushClass(String className){
            this.className = className;
        }
        
        public String getClassName(){
            return this.className;
        }
    }
    
    private static final String UE4_BRUSH_TYPE_SUBTRACT = "Brush_Subtract";
    
    private static final String UE4_BRUSH_TYPE_ADD = "Brush_Add";
    
    private static final String UE3_BRUSH_TYPE_ADD = "CSG_Add";
    
    private static final String UE3_BRUSH_TYPE_SUBTRACT = "CSG_Subtract";
    
    
    /**
     * Used by Unreal Engine 1
     */
    Vector3d mainScale;
    
    
    /**
     * Used by Unreal Engine 1
     */
    Vector3d postScale;
    
    /**
     * Used by Unreal Engine 1
     */
    Vector3d tempScale;
    
    /**
     * Pre-Pivot used for brushes
     * Changed the relative origin of brush
     */
    Vector3d prePivot;
    
    LinkedList<T3DPolygon> polyList = new LinkedList<>();

    
    // BrushType=Brush_Subtract / BrushType=Brush_Add UE4
    // CsgOper=CSG_Subtract UE <= 3
    /**
     * Begin Object Name="BrushComponent0"
            Brush=Model'Model_12'
            RelativeLocation=(X=-720.000000,Y=-640.000000,Z=230.000000)
         End Object
     */
    
    /**
     * If true then brush is in additive mode else in subtract mode
     */
    boolean isAdditiveMode = true;
            
    /**
     *
     * @param mc
     */
    public T3DBrush(MapConverter mc) {
        super(mc);
        
        if(mapConverter.getUnrealEngineTo().version < UTGames.UnrealEngine.UE4.version){
            throw new UnsupportedOperationException("Unsupported Unreal Engine "+mapConverter.getUnrealEngineTo()+" Version for convert to");
        }
    }
    
    private boolean isAnalysingPolyData = false;
    
    /**
     * If true reverse the order of vertices when writting converted t3d.
     * This is due to MainScale factor.
     * Depending on it it.
     */
    boolean reverseVertexOrder = false;

    @Override
    public boolean analyseT3DData(String line) {
        
        
        // CsgOper=CSG_Subtract
        // BrushType=Brush_Subtract
        if(line.contains("CsgOper")){
            String csgOper = line.split("\\=")[1];
            
            switch (csgOper) {
                case UE3_BRUSH_TYPE_SUBTRACT:
                    isAdditiveMode = false;
                    break;
                case UE3_BRUSH_TYPE_ADD:
                    isAdditiveMode = true;
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported Brush Csg:"+csgOper);
            }
        } 

        // MainScale=(Scale=(Y=-1.000000),SheerAxis=SHEER_ZX)
        // MainScale=(SheerAxis=SHEER_ZX)
        else if(line.contains("MainScale=") && line.contains("(Scale=")){
            mainScale = T3DUtils.getVector3d(line.split("\\(Scale")[1], 1D);
            reverseVertexOrder = mainScale.x * mainScale.y * mainScale.z < 0;
            //reverseVertexOrder = mainScale.x < 0;
        }
        
        // PostScale=(Scale=(X=1.058824,Y=1.250000,Z=0.920918),SheerAxis=SHEER_ZX)
        else if(line.contains("PostScale=") && line.contains("(Scale=")){
            postScale = T3DUtils.getVector3d(line.split("\\(Scale")[1], 1D);
        }
        
        //  TempScale=(Scale=(X=0.483090,Y=2.274808,Z=0.488054))
        else if(line.contains("TempScale") && line.contains("(Scale=")){
            tempScale = T3DUtils.getVector3d(line.split("\\(Scale")[1], 1D);
        }
        
        else if(line.contains("PrePivot")){
            prePivot = T3DUtils.getVector3d(line, 0D);
        }
        
        // Begin Polygon Item=Rise Texture=r-plates-g Link=0
        else if(line.contains("Begin Polygon")){
            isAnalysingPolyData = true;
            polyList.add(new T3DPolygon(line));
        }
        
        // Origin   -00128.000000,-00128.000000,-00128.000000
        else if(line.contains("Origin ") && isAnalysingPolyData){
            polyList.getLast().origin = T3DUtils.getPolyVector3d(line, "Origin");
        }
        
        else if(line.contains("Normal ") && isAnalysingPolyData){
            polyList.getLast().normal = T3DUtils.getPolyVector3d(line, "Normal");
        }
        
        else if(line.contains("TextureU ") && isAnalysingPolyData){
            polyList.getLast().texture_u = T3DUtils.getPolyVector3d(line, "TextureU");
        }
        
        else if(line.contains("TextureV ") && isAnalysingPolyData){
            polyList.getLast().texture_v = T3DUtils.getPolyVector3d(line, "TextureV");
        }
        
        else if(line.contains("Vertex ") && isAnalysingPolyData){
            Vector3d vertex = T3DUtils.getPolyVector3d(line, "Vertex");
            polyList.getLast().vertices.add(vertex);
        }
        
        // Pan      U=381 V=-7
        else if(line.contains(" Pan ") && isAnalysingPolyData){
            polyList.getLast().pan_u = Double.valueOf(line.split("U=")[1].split("\\ ")[0]);
            polyList.getLast().pan_v = Double.valueOf(line.split("V=")[1].split("\\ ")[0]);
        }
        
        else if(line.contains("End Polygon")){
            isAnalysingPolyData = false;
        }
        
        // Hack, normally analysed in T3DActor but needed 
        // for waterzone, lavazone to be converted ...
        else if(line.contains("Begin Actor")){
            t3dClass = getActorClass(line);
            
            if(isU1ZoneVolume(t3dClass)){
                forceToBox(90d);
            }
            
            // need force trigger function else name is null
            return super.analyseT3DData(line);
        }
        
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    /**
     * 
     * @param t3dBrushClass
     * @return 
     */
    private boolean isU1ZoneVolume(String t3dBrushClass){
        
        if(t3dBrushClass.equals(BrushClass.BRUSH.className)){
            return false;
        } 
        
        else if(t3dBrushClass.equals("LavaZone") 
                || t3dBrushClass.equals("SlimeZone") 
                || t3dBrushClass.equals("VaccuumZone")
                || t3dBrushClass.equals("NitrogenZone")
                || t3dBrushClass.equals("VaccuumZone")){
            brushClass = BrushClass.UT_PAIN_VOLUME;
            forcedWrittenLines.add("DamagePerSec=10.000000");
            return true;
        } 
        
        else if(t3dBrushClass.equals("WaterZone")){
            brushClass = BrushClass.UT_WATER_VOLUME;
            return true;
        }

        return false;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public boolean isValid(){
        
        // BUG UE4 DON'T LIKE SHEETS (one polygon) or "Light Brushes" mainly coming from UE1/UE2 ...
        // Else geometry building got holes so need to get rid of them ...
        if(mapConverter.toUnrealEngine4() && polyList.size() <= 4 ){
            // TODO add note? (some sheet brushes are movers ...)
            logger.warning("Skipped unsupported 'sheet brush' in "+mapConverter.getUnrealEngineTo().name()+" for "+name);
            return false;
        }
        
        return super.isValid();
    }
    
    /**
     *
     */
    public static DecimalFormat df = new DecimalFormat("+00000.000000;-00000.000000", new DecimalFormatSymbols(Locale.US));
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        
        
        sbf.append(IDT).append("Begin Actor Class=").append(brushClass.getClassName()).append(" Name=").append(name).append("\n");
                
        // Location Data
        sbf.append(IDT).append("\tBegin Object Name=\"BrushComponent0\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");
        
        sbf.append(IDT).append("\tBrushType=").append(isAdditiveMode?UE4_BRUSH_TYPE_ADD:UE4_BRUSH_TYPE_SUBTRACT).append("\n");
        
        sbf.append(IDT).append("\tBegin Brush Name=Model_6\n");
        sbf.append(IDT).append("\t\tBegin PolyList\n");
        
        

        int numPoly = 0;

        for(T3DPolygon t3dPolygon : polyList){
            t3dPolygon.toT3D(sbf, df, IDT, numPoly, reverseVertexOrder); numPoly++;
        }
        
        sbf.append(IDT).append("\t\tEnd PolyList\n");
        sbf.append(IDT).append("\tEnd Brush\n");
        
        sbf.append(IDT).append("\tBrush=Model'Model_6'\n");
        sbf.append(IDT).append("\tBrushComponent=BrushComponent0\n");
        
        for(String line : forcedWrittenLines){
            sbf.append(IDT).append(line).append("\n");
        }
        
        writeEndActor();
        
        if(brushClass == BrushClass.UT_WATER_VOLUME || brushClass == BrushClass.UT_PAIN_VOLUME){
            
            // add post processvolume
            T3DBrush postProcessVolume = createBox(mapConverter, 95d, isAdditiveMode);
            postProcessVolume.brushClass = BrushClass.POST_PROCESS_VOLUME;
            postProcessVolume.name = this.name+"PPVolume";
            postProcessVolume.location = this.location;
            
            if("SlimeZone".equals(t3dClass)){
                // slimy ppv copied/pasted from DM-DeckTest (UT4)
                postProcessVolume.forcedWrittenLines.add("Settings=(bOverride_FilmWhitePoint=True,bOverride_AmbientCubemapIntensity=True,bOverride_DepthOfFieldMethod=True,FilmWhitePoint=(R=0.700000,G=1.000000,B=0.000000,A=1.000000),FilmShadowTint=(R=0.000000,G=1.000000,B=0.180251,A=1.000000),AmbientCubemapIntensity=0.000000,DepthOfFieldMethod=DOFM_Gaussian)");
            }
            
            sbf.append(postProcessVolume.toString());
            
            // TODO add sheet surface
        }
        
        return sbf.toString();
    }
    
    /**
     * Since UE4 does not support create level in 'substractive' mode we
     * need to create a big brush in substractive mode to simulate this.
     * @param mc
     * @param size
     * @param isAdditiveMode
     * @return 
     */
    public static T3DBrush createBox(MapConverter mc, Double size, boolean isAdditiveMode){
        
        T3DBrush volume = new T3DBrush(mc);
        volume.isAdditiveMode = isAdditiveMode;
        volume.forceToBox(size);
        
        return volume;
    }
    
    /**
     * Force brush to be a box
     * @param size 
     */
    public void forceToBox(Double size){
        Double s = size;
        
        polyList.clear();
        
        T3DPolygon p = new T3DPolygon();
        p.setNormal(-1d, 0d, 0d); p.setTexU(0d, 1d, 0d); p.setTexV(0d, 0d, -1d);
        p.addVertex(-s, -s, -s).addVertex(-s, -s, s).addVertex(-s, s, s).addVertex(-s, s, -s);
        addPolygon(p);
        
        p = new T3DPolygon();
        p.setNormal(0d, 1d, 0d); p.setTexU(1d, 0d, 0d); p.setTexV(0d, 0d, -1d);
        p.addVertex(-s, s, -s).addVertex(-s, s, s).addVertex(s, s, s).addVertex(s, s, -s);
        addPolygon(p);
        
        p = new T3DPolygon();
        p.setNormal(1d, 0d, 0d); p.setTexU(0d, -1d, 0d); p.setTexV(0d, 0d, -1d);
        p.addVertex(s, s, -s).addVertex(s, s, s).addVertex(s, -s, s).addVertex(s, -s, -s);
        addPolygon(p);
        
        p = new T3DPolygon();
        p.setNormal(0d, -1d, 0d); p.setTexU(-1d, 0d, 0d); p.setTexV(0d, 0d, -1d);
        p.addVertex(s, -s, -s).addVertex(s, -s, s).addVertex(-s, -s, s).addVertex(-s, -s, -s);
        addPolygon(p);
        
        p = new T3DPolygon();
        p.setNormal(0d, 0d, 1d); p.setTexU(1d, 0d, 0d); p.setTexV(0d, 1d, 0d);
        p.addVertex(-s, s, s).addVertex(-s, -s, s).addVertex(s, -s, s).addVertex(s, s, s);
        addPolygon(p);
        
        p = new T3DPolygon();
        p.setNormal(0d, 0d, -1d); p.setTexU(1d, 0d, 0d); p.setTexV(0d, -1d, 0d);
        p.addVertex(-s, -s, -s).addVertex(-s, s, -s).addVertex(s, s, -s).addVertex(s, -s, -s);
        addPolygon(p);
    }
    
    /**
     *
     */
    @Override
    public void convert(){
        

        if(mapConverter.fromUE1OrUE2() && mapConverter.toUE3OrUE4()){
            transformPermanently();
        }
        
        // Update Location if prepivot set
        if(prePivot != null){
            
            prePivot.negate();
            // location = location - prepivot
            if(location == null){
                location = prePivot;
            } else {
                location.add(prePivot);
            }
            
            prePivot = null;
        }
        
        // TODO check texture alignement after convert
        // TODO export textures from UT packages
        // TODO change texture name on polygons
        
        super.convert();
    }
    
    /**
     * TransformPermanently a brush in UT/U1 Editor
     * After that rotation, mainscale and postscale are 'reset'
     * Origin = (Origin * MainScale x Rotate) * PostScale
     * PrePivot = PrePivot * Rotation
     * Normal = Normal * Rotation
     * MainScale * Vector -> Rotation * Vector -> PostScale * Vector
     */
    private void transformPermanently(){
        
        if(prePivot != null){
            Geometry.transformPermanently(prePivot, mainScale, rotation, postScale, false);
        }
        
        for(T3DPolygon polygon: polyList){
            
            Geometry.transformPermanently(polygon.origin, mainScale, rotation, postScale, false);
            
            Geometry.rotate(polygon.normal, rotation);
            
            if(mainScale != null){
                polygon.normal.x = mainScale.x / Math.abs(mainScale.x);
                polygon.normal.y = mainScale.y / Math.abs(mainScale.y);
                polygon.normal.z = mainScale.z / Math.abs(mainScale.z);
            }
            
            if(polygon.texture_u != null){
                Geometry.transformPermanently(polygon.texture_u, mainScale, rotation, postScale, true);
            }
            
            if(polygon.texture_v != null){
                Geometry.transformPermanently(polygon.texture_v, mainScale, rotation, postScale, true);
            }
                    
            for(Vector3d vertex : polygon.vertices){
                Geometry.transformPermanently(vertex, mainScale, rotation, postScale, false);
            }
        }
        
        rotation = null;
        mainScale = null;
        postScale = null;
        
        // TODO see purpose of TempScale
        // after tp in Unreal Engine, it is still set
    }

    /**
     * Rescale brush.
     * Must be done always after convert
     */
    @Override
    public void scale(Double newScale){
        
        for(T3DPolygon polygon: polyList){
            
            if(polygon.texture_u != null){
                polygon.texture_u.scale(1/newScale);
            }
            
            if(polygon.texture_v != null){
                polygon.texture_v.scale(1/newScale);
            }
            
            for(Vector3d vertex : polygon.vertices){
                vertex.scale(newScale);
            }
        }
        
        
        super.scale(newScale);
    }
    
    
    private void addPolygon(T3DPolygon p){
        polyList.add(p);
    }
}
