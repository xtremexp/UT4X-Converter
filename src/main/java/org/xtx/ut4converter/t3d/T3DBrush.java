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
        
        
        sbf.append(IDT).append("Begin Actor Class=Brush Name=").append(name).append("\n");
                
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
        
        writeEndActor();
        
        return sbf.toString();
    }
    
    /**
     * Since UE4 does not support create level in 'substractive' mode we
     * need to create a big brush in substractive mode to simulate this.
     * @param boxSize
     * @param isAdditiveMode
     * @param material Défault material to aply
     * @return 
     */
    public static String writeUE4BoxBrush(Integer boxSize, boolean isAdditiveMode, String material){
        
        String size = "25000.00000";
        
        StringBuilder sbf = new StringBuilder();
        sbf.append(IDT).append("Begin Actor Class=Brush\n");
        sbf.append(IDT).append("Begin Object Class=BrushComponent Name=\"BrushComponent0\"\n");
        sbf.append(IDT).append("End Object\n");
        sbf.append(IDT).append("Begin Object Name=\"BrushComponent0\"\n");
        sbf.append(IDT).append("\tBrush=Model'Model_10'\n");
        sbf.append(IDT).append("End Object\n");
        sbf.append(IDT).append("BrushType=").append(isAdditiveMode ? UE4_BRUSH_TYPE_ADD : UE3_BRUSH_TYPE_SUBTRACT).append("\n");
        sbf.append(IDT).append("Begin Brush Name=Model_10\n");
        sbf.append(IDT).append("\tBegin PolyList\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\t\tOrigin   -").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tNormal   -00001.000000,+00000.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureU +00000.000000,+00001.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureV +00000.000000,+00000.000000,-00001.000000\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",-").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",+").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\t\tOrigin   -").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tNormal   +00000.000000,+00001.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureU +00001.000000,-00000.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureV +00000.000000,+00000.000000,-00001.000000\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",+").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",+").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\t\tOrigin   +").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tNormal   +00001.000000,+00000.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureU +00000.000000,-00001.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureV +00000.000000,+00000.000000,-00001.000000\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",+").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",-").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\t\tOrigin   +").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tNormal   +00000.000000,-00001.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureU -00001.000000,-00000.000000,-00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureV +00000.000000,+00000.000000,-00001.000000\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",-").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",-").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\t\tOrigin   -").append(size).append(",+").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tNormal   +00000.000000,+00000.000000,+00001.000000\n");
        sbf.append(IDT).append("\t\t\tTextureU +00001.000000,+00000.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureV +00000.000000,+00001.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",+").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",-").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",-").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",+").append(size).append(",+").append(size).append("\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\t\t\tOrigin   -").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tNormal   +00000.000000,+00000.000000,-00001.000000\n");
        sbf.append(IDT).append("\t\t\tTextureU +00001.000000,+00000.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tTextureV +00000.000000,-00001.000000,+00000.000000\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   -").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",+").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\t\tVertex   +").append(size).append(",-").append(size).append(",-").append(size).append("\n");
        sbf.append(IDT).append("\t\tBegin Polygon\n");
        sbf.append(IDT).append("\tEnd PolyList\n");
        sbf.append(IDT).append("End Brush\n");
        sbf.append(IDT).append("Brush=Model'Model_10'\n");
        sbf.append(IDT).append("BrushComponent=BrushComponent0\n");
        sbf.append(IDT).append("RootComponent=BrushComponent0\n");
        sbf.append(IDT).append("ActorLabel=\"Box Brush\"\n");
        sbf.append(IDT).append("End Actor\n");

        return sbf.toString();
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
}
