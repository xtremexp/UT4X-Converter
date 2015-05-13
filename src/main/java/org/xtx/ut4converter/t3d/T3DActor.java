/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.T3DMatch.Match;

/**
 * 
 * @author XtremeXp
 */
public abstract class T3DActor {

    /**
     * Current game compatibility state for actor.
     * Should automatically change after convert
     */
    UTGame game = UTGame.NONE;
    
    /**
     * All original properties stored for this actor.
     * Basically is a map of "key" and value"
     * E.G:
     * Brush=Model'MyLevel.Brush'
     * -> Key = Brush, Value = Model'MyLevel.Brush'
     * Might be used after parsing t3d actor data to convert it.
     */
    protected Map<String, String> properties;
    
    /**
     * Possible match for t3d actor
     */
    protected Match match;
    
    /**
     * Unreal Engine 4 only.
     * Root Component Type
     */
    protected T3DMatch.UE4_RCType ue4RootCompType = T3DMatch.UE4_RCType.UNKNOWN;
    
    /**
     * Original actor class
     */
    protected String t3dClass;
    
    
    /**
     * UE1/2/3? property in Events->Tag 
     */
    protected String tag;
    
    /**
     * Name or label of actor
     */
    protected String name;
    
    
    /**
     * Location of actor (if null means 0 location)
     */
    protected Vector3d location;
    
    /**
     * Co-Location of actor
     * Used by some old Unreal 1 / UT99 maps ...
     * Useless for convert.
     */
    protected Vector3d coLocation;
    
    /**
     * Old-Location of actor
     * Used by some old Unreal 1 / UT99 maps ...
     */
    protected Vector3d oldLocation;
    
    /**
     * Rotation of actor
     */
    protected Vector3d rotation;
    
    /**
     * 3D Scaling
     */
    protected Vector3d scale3d;
    
    /**
     * Scale in unreal editor of sprite
     */
    protected Double drawScale;
    
    
    String otherdata="";
    
    boolean usecolocation=false;
    
    /**
     * Reference to map converter
     */
    protected MapConverter mapConverter;
    
    /**
     * Used to add extra Z location
     * (for converting pickup for exemple not having same 'origin')solar
     */
    Double offsetZLocation = 0D;
    
    /**
     * TODO make global StringBuilder
     * that we would 'reset' after write of each actor
     * (avoiding creating one for each single actor / perf issues)
     */
    protected StringBuilder sbf;
    
    /**
     * Minimal indentation when writing t3d converted actor
     */
    public final static String IDT = "\t";
    
    /**
     *
     */
    protected boolean validWriting = true;
    
    /**
     * Force these lines to be written
     * (not used yet for each subclass of this class)
     */
    protected List<String> forcedWrittenLines = new ArrayList<>();
    
    protected Logger logger;
    
    /**
     * Linked actors to this one.
     * (e.g: teleporters)
     */
    protected List<T3DActor> linkedTo = new ArrayList<>();
    
    
    /**
     * If this actor has been created from another one.
     * This property should be not null
     */
    T3DActor parent;
    
    /**
     * If not empty current actor will be replaced by these ones
     * when writing t3d converted stuff.
     */
    protected List<T3DActor> children = new ArrayList<>();
    
    

    /**
     * Read line of t3d file to parse data about current t3d actor being read
     * @param line 
     * @return  true if data has been extracted from line false else (useful to check which data has not been parsed)
     */
    public boolean analyseT3DData(String line){
        return parseOtherData(line);
    }
    
    /**
     *
     * @param mc
     */
    public T3DActor(MapConverter mc){
        this.mapConverter = mc;
        sbf = new StringBuilder();
        properties = new HashMap<>();
        logger = mc.getLogger();
        game = mapConverter.getInputGame();
    }
    
    /**
     * Get some important info about actors like location,rotation,drawscale,...
     * @param line T3D level line being analyzed
     * @return true if some Data has been parsed.
     */
    public boolean parseOtherData(String line)
    {
        int equalsIdx = line.indexOf("=");
        
        // BlockAll hack overide
        if((!(this instanceof T3DBrush) || "BlockAll".equals(t3dClass)) && equalsIdx != -1){
            properties.put(line.substring(0, equalsIdx).trim(), line.substring(equalsIdx+1, line.length()));
        }
        
        if(line.contains(" Location=")||line.contains("\tLocation=")){
            location = T3DUtils.getVector3d(line, 0D);
            return true;
        }
        
        if(line.contains(" OldLocation=")||line.contains("\tOldLocation=")){
            oldLocation = T3DUtils.getVector3d(line, 0D);
            return true;
        }
        
        else if(line.contains(" ColLocation=") || line.contains("\tColLocation=")|| line.contains("ColLocation="))
        {
            coLocation = T3DUtils.getVector3d(line, 0D);
            return true;
        }
        
        else if(line.contains("DrawScale3D")){
            scale3d = T3DUtils.getVector3d(line, 1D);
            return true;
        }
        
        else if(line.contains("DrawScale=")){
            drawScale = T3DUtils.getDouble(line);
            return true;
        }
        
        else if(line.contains("Rotation")){
            rotation = T3DUtils.getVector3dRot(line);
            return true;
        }
        
        // Begin Actor Class=Brush Name=Brush2
        else if(line.contains("Begin Actor")){
        
            t3dClass = getActorClass(line);
            name = T3DUtils.getString(line, "Name");
        }
        
        else if(line.contains(" Group=")||line.contains("\tGroup=")){
            addOtherData(line); 
        } 
        
        else if(line.contains("Tag=")){
            tag = line.split("Tag=")[1];
        }
        
        else {
            return false;
        }
        
        return true;
    }

    protected void writeLocRotAndScale(){
        writeLocRotAndScale(sbf, getOutputGame().engine, location, rotation, scale3d);
    }
    
    /**
     * Write Location Rotation and drawScale of converted actor
     * @param sbf
     * @param outEngine
     * @param location
     * @param rotation
     * @param scale3d
     */
    public static void writeLocRotAndScale(StringBuilder sbf, UnrealEngine outEngine, Vector3d location, Vector3d rotation, Vector3d scale3d){
        
        String baseText = IDT+"\t\t";
        
        if(outEngine.version >= UTGames.UnrealEngine.UE4.version){
            if(location != null){
                sbf.append(baseText).append("RelativeLocation=(X=").append(fmt(location.x)).append(",Y=").append(fmt(location.y)).append(",Z=").append(fmt(location.z)).append(")\n");
            }
            
            // RelativeRotation=(Pitch=14.179391,Yaw=13.995641,Roll=14.179387)
            if(rotation != null){
                sbf.append(baseText).append("RelativeRotation=(Pitch=").append(fmt(rotation.x)).append(",Yaw=").append(fmt(rotation.y)).append(",Roll=").append(fmt(rotation.z)).append(")\n");
            }

            // RelativeScale3D=(X=4.000000,Y=3.000000,Z=2.000000)
            if(scale3d != null){
                sbf.append(baseText).append("RelativeScale3D=(X=").append(fmt(scale3d.x)).append(",Y=").append(fmt(scale3d.y)).append(",Z=").append(fmt(scale3d.z)).append(")\n");
            }
        } 
        // checked U1, UT99, U2, UT2004, UT3
        else {
            if(location != null){
                sbf.append(baseText).append("Location=(X=").append(fmt(location.x)).append(",Y=").append(fmt(location.y)).append(",Z=").append(fmt(location.z)).append(")\n");
            }
            
            // RelativeRotation=(Pitch=14.179391,Yaw=13.995641,Roll=14.179387)
            if(rotation != null){
                sbf.append(baseText).append("Rotation=(Pitch=").append(fmt(rotation.x)).append(",Yaw=").append(fmt(rotation.y)).append(",Roll=").append(fmt(rotation.z)).append(")\n");
            }

            // RelativeScale3D=(X=4.000000,Y=3.000000,Z=2.000000)
            if(scale3d != null){
                sbf.append(baseText).append("Scale3D=(X=").append(fmt(scale3d.x)).append(",Y=").append(fmt(scale3d.y)).append(",Z=").append(fmt(scale3d.z)).append(")\n");
            }
        }
    }
    
    
    /**
     * 
     * @param newScale
     */
    public void scale(Double newScale){
        
        if(newScale == null){
            return;
        }
        
        if(newScale > 1){
            if(location != null) location.scale(newScale);
            if(coLocation != null) coLocation.scale(newScale);
            if(drawScale != null ) drawScale *= newScale;
            if(scale3d != null) scale3d.scale(newScale);
        }
    }
    
    

    private void addOtherData(String somedata)
    {
        this.otherdata += somedata+"\n";
    }

    /**
     *
     * @param otherdata
     */
    public void setOtherdata(String otherdata) {
        this.otherdata = otherdata;
    }

    /**
     *
     * @return
     */
    public Vector3d getLocation() {
        return location;
    }

    /**
     *
     * @param value
     * @return
     */
    public static String fmt(double value){
        return formatValue(value);
    }
    
    /**
     *
     * @param value
     * @return
     */
    public static String formatValue(double value)
    {
        DecimalFormat df = new DecimalFormat("0.000000",new DecimalFormatSymbols(Locale.US));
        return df.format(value);
    }

    /**
     *
     * @param line
     * @return
     */
    public static String getActorClass(String line)
    {
        return (line.split("=")[1]).split(" ")[0];
    }
    

    /**
     * Get the input game this actor come from
     * @return 
     */
    protected UTGames.UTGame getInputGame(){
        return mapConverter.getInputGame();
    }
    
    /**
     * Get the output game to which it must be converted
     * @return 
     */
    protected UTGames.UTGame getOutputGame(){
        return mapConverter.getOutputGame();
    }

    /**
     *
     * @param offsetZLocation
     */
    public void setOffsetZLocation(Double offsetZLocation) {
        this.offsetZLocation = offsetZLocation;
    }

    /**
     *
     * @return
     */
    protected MapConverter getMapConverter(){
        return this.mapConverter;
    }
    
    /**
     *
     */
    public void convert(){
        
        if(coLocation != null && mapConverter.getInputGame().engine == UTGames.UnrealEngine.UE1){
            if(location != null){
                location.add(coLocation);
            } else {
                location = coLocation;
            }
            
            coLocation = null;
        }

        //changes height of actor if needed (so aligned with floor for example)
        if(offsetZLocation != null){
            if(location != null){
                location.z += offsetZLocation;
            } else {
                location = new Vector3d(0, 0, offsetZLocation);
            }
            
            offsetZLocation = null;
        }
        
        if(rotation != null){
            // Rotation range changed between UE2 and UE3
            // for brushes no need that since they have been transformed permanently
            // Vertice data updated with rotation and rotation reset
            if(mapConverter.isFromUE1UE2ToUE3UE4()){
                rotation.x /= 360d;
                rotation.y /= 360d;
                rotation.z /= 360d;
            } 
            else if(mapConverter.isFromUE3UE4ToUE1UE2()){
                rotation.x *= 360d;
                rotation.y *= 360d;
                rotation.z *= 360d;
            }
        }
        
        // Notify actor was converted
        game = mapConverter.getOutputGame();
        
        // Converts all child actors as well
        children.stream().forEach((child) -> {
            if(child.needsConverting()){
                child.convert();
            }
        });
    }

    /**
     * We may not want to convert this t3d actor after analyzing data,
     * that's the purpose of this.
     * @return true is this t3d actor is allowed to be converted else not
     */
    public boolean isValidConverting(){
        return true;
    }
    
    /**
     *
     */
    protected void writeEndActor(){
        
        // means we did not even write "begin actor" so we skip ...
        if(sbf.length() == 0){
            return;
        }
        
        if(mapConverter.toUnrealEngine4()){
            if(drawScale != null){
                sbf.append(IDT).append("\tSpriteScale=").append(drawScale).append("\n");;
            }
            sbf.append(IDT).append("\tActorLabel=\"").append(name).append("\"\n");
        } 
        // Checked u1, ut99, ut2004, ut3
        else {
            if(drawScale != null){
                sbf.append(IDT).append("\tDrawScale=").append(drawScale).append("\"\n");;
            }
            sbf.append(IDT).append("\tName=\"").append(name).append("\n");
        }
        
        sbf.append(IDT).append("End Actor\n").toString();
    }

    /**
     * Says if this actor is valid to be written to t3d
     * Should be called always after isValidConverting
     * Some actors might be valid for convert but not for write.
     * E.g: sheet brushes not valid for write
     * but valid for convert(auto-create sheet static mesh)
     * isValidConverting() --> convert() -> isValidWritting() -> write(actor.toString())
     * @return
     */
    public boolean isValidWriting() {
        return validWriting;
    }

    public void setValidWriting(boolean validWriting) {
        this.validWriting = validWriting;
    }

    
    

    public String toString(){
        return sbf.toString();
    }
    
    
    /**
     * Null-safe property getter
     * @param name
     * @return 
     */
    protected String getProperty(String name){
        
        if(properties.containsKey(name)){
            return properties.get(name);
        } else {
            return null;
        }
    }
    
    /**
     * Indicates if this actor needs to be converted
     * @return <ocde>true</code>> If actor needs to be converted
     */
    private boolean needsConverting(){
        return game != mapConverter.getOutputGame();
    }
}

