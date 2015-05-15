/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import static org.xtx.ut4converter.t3d.T3DActor.getActorClass;

/**
 * Converts T3D Unreal 1 / Unreal Tournament
 * to Unreal Tournament "4" t3d file
 * @author XtremeXp
 */
public class T3DLevelConvertor  {
    
    /**
     * Current map converter
     */
    MapConverter mapConverter;
    
    /**
     * Input t3d file that need conversion
     */
    File inT3dFile;
    
    /**
     * Converted input t3d file
     */
    File outT3dFile;
    
    /**
     * Reader for input t3d file
     */
    BufferedReader bfr;
    
    /**
     * Writer for converted t3d file
     */
    BufferedWriter bwr;
    
    
    /**
     * String cache for converted t3d actors.
     * Converted actor is sent to the writer.
     */
    StringBuilder sbf;
    
    /**
     * Actors that were not converted.
     */
    public SortedSet<String> unconvertedActors = new TreeSet<>();
    
    LinkedList<T3DActor> convertedActors = new LinkedList<>();
    
    boolean createNoteWhenUnconverted = true;
    
    Logger logger;
    
    Vector3d levelDimension;
    
    Vector3d boundBoxLocalisation;
    
    /**
     * 
     * @param originalT3d Original t3d ut3 file
     * @param convertedT3d New t3d file converted in UT4 t3d format
     * @param mc MapConverter options
     */
    public T3DLevelConvertor(File originalT3d, File convertedT3d, MapConverter mc){
        
        this.inT3dFile = originalT3d;
        this.outT3dFile = convertedT3d;
        this.mapConverter = mc;
        this.mapConverter.setT3dLvlConvertor(this);
        this.logger = mc.getLogger();
    }
    
    /**
     * Converts t3d file for final game
     * @throws Exception 
     */
    public void convert() throws Exception
    {
        if(inT3dFile == null || !inT3dFile.exists()){
            throw new Exception("File "+inT3dFile.getAbsolutePath()+" does not exists!");
        }

        logger.info("Converting t3d map "+inT3dFile.getName()+" to "+mapConverter.getOutputGame().name+" t3d level");
        
        try {
            
            bfr = new BufferedReader(new FileReader(inT3dFile));
            bwr = new BufferedWriter(new FileWriter(outT3dFile));
            
            /**
             * Current line of T3D File being analyzed
             */
            int linenumber=1;
            String line;

            
            // Read input t3d file and convert actors
            while((line=bfr.readLine())!=null)
            {
                try {
                    analyzeLine(line);
                    linenumber ++;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error while parsing data from T3D Level File "+inT3dFile.getName());
                    logger.log(Level.SEVERE, "Line #"+linenumber+" Original Line String:"+line);
                    logger.log(Level.SEVERE, "Current Actor Class: "+uta.t3dClass);
                    logger.log(Level.SEVERE, "ERROR:", e);
                    throw e;
                }
            }

            // Write T3D converted file
            write(bwr);
        } finally {
            bwr.close();
            bfr.close();
        }
    }
    
    /**
     * 
     * @param bw
     * @throws IOException 
     */
    private void write(BufferedWriter bw) throws IOException {
        
        writeHeader();
            
        for(T3DActor actor : convertedActors){
            
            // write parent actor
            if(actor.isValidWriting()){
                bw.write(actor.toString());
            }
            
            // write replacement actors
            for(T3DActor repActor : actor.children){
                bw.write(repActor.toString());
            }
        }

        writeFooter();
    }

    /**
     * If true means we don't anaylise t3d lines
     * of current actor being parsed
     */
    boolean banalyseline = false;
    String currentClass="";
    
    /**
     * Current actor class
     */
    Class utActorClass = null;
    T3DActor uta = null;
    
    private final int LEVEL_OBJECT_LEVEL = 1;
    
    /**
     * Begin Object Class=Level
     */
    private int deepObjectLevel = LEVEL_OBJECT_LEVEL - 1;
    
    /**
     * Says if current line is data for new actor
     * @param line
     * @return 
     */
    private boolean isBeginActor(String line){
        
        if(mapConverter.getInputGame().engine.version <= UnrealEngine.UE2.version){
            return line.contains("Begin Actor");
        }
        
        // Any actor/sub-class begins with "Begin Object"
        else if (mapConverter.getInputGame().engine.version == UnrealEngine.UE3.version){
            
            if(line.trim().startsWith("Begin Object")){
                
                deepObjectLevel ++;
                return (deepObjectLevel == (LEVEL_OBJECT_LEVEL + 1));
            }
        }
        
        return false;
    }
    
    private boolean isEndActor(String line){
        
        if(mapConverter.getInputGame().engine.version <= UnrealEngine.UE2.version){
            return line.contains("End Actor");
        }
        
        // Any actor begin with "Begin Object"
        else if (mapConverter.getInputGame().engine.version == UnrealEngine.UE3.version){
            
            if(line.trim().startsWith("End Object")){
                deepObjectLevel --;
                return (deepObjectLevel == LEVEL_OBJECT_LEVEL);
            }
        }
        
        return false;
    }
    
    /**
     * Analyze T3D line to get and convert UT data
     * @param line current T3D line being read
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    private void analyzeLine(String line) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        if (isBeginActor(line)) {
            currentClass = getActorClass(line);
            
            if (mapConverter.getSupportedActorClasses().canBeConverted(currentClass)) {
                utActorClass = mapConverter.getSupportedActorClasses().getConvertActorClass(currentClass);
                banalyseline = true;

                if (utActorClass != null) {
                    Constructor cons = utActorClass.getConstructor(MapConverter.class, String.class);
                    uta = (T3DActor) cons.newInstance(mapConverter, getActorClass(line));
                    convertedActors.add(uta);
                    uta.analyseT3DData(line);
                } 
            } else {
                // skips some useless/uneeded actors to notify unconverted (e.g: pathnodes for UE4/UT4)
                if(!mapConverter.getSupportedActorClasses().noNotifyUnconverted(currentClass)){
                    unconvertedActors.add(currentClass);


                    if(createNoteWhenUnconverted) {
                        banalyseline = true;
                        utActorClass = T3DNote.class;
                        uta = new T3DNote(mapConverter, "Unconverted: "+currentClass, true);
                        uta.analyseT3DData(line);
                        convertedActors.add(uta);
                    } else {
                        logger.warning("Unconverted "+currentClass);
                        banalyseline = false;
                    }
                }
            }
        } 
        
        // Actor End - We write converted data to t3d file
        else if (isEndActor(line)) {

            if (banalyseline) {
                if (uta != null) {
                    
                    if(uta.isValidConverting()){
                        // we might want to only re-scale map
                        if(uta.getMapConverter().getOutputGame() != uta.getMapConverter().getInputGame()){
                            uta.convert();
                        }
                        // rescale if needed 
                        // must always be done after convert
                        uta.scale(mapConverter.getScale()); 
                    } else {
                        convertedActors.removeLast();
                    }
                } 
            }

            // Reset
            banalyseline = false;
            utActorClass = null;
            uta = null;
        }
        
        // Actor data being analyzed
        else {
            if (banalyseline) {
                if (uta != null) {
                    uta.analyseT3DData(line.trim());
                }
            }
        }
    }

    /**
     * Returns current actor class
     * from t3d line defining actor
     * @param line t3d line
     * @return Actor class
     */
    private String getActorClass(String line) {
        return (line.split("=")[1]).split(" ")[0];
    }

    /**
     * Write header of T3D file
     * TODO check for UE1/UE2
     * @throws IOException 
     */
    private void writeHeader() throws IOException{
        bwr.write("Begin Map\n");
        bwr.write("\tBegin Level Name=/Game/RestrictedAssets/Maps/WIP/"+mapConverter.getOutMapName()+"\n");
        
        // Auto creates a big additive brush surrounding level
        // to simulate creating a level in subtract mode (not existing in UE4 ...)
        if(mapConverter.isFromUE1UE2ToUE3UE4()){
            
            Double offset = 200d;
            Vector3d boundBox = getLevelDimensions();
            
            T3DBrush additiveBrush = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
            additiveBrush.location = boundBoxLocalisation;
            additiveBrush.name = "BigAdditiveBrush";
            additiveBrush.brushClass = T3DBrush.BrushClass.Brush;
            
            // no need accurate light map resolution on this brush since it will be never seen by player
            for(T3DPolygon p : additiveBrush.polyList){
                p.lightMapScale = 2048d;
            }
            
            bwr.write(additiveBrush.toString());
        }
    }
    
    /**
     * 
     * @return 
     */
    private Vector3d getLevelDimensions(){
        
        if(levelDimension == null){
            Vector3d max = new Vector3d(0d, 0d, 0d);
            Vector3d min = new Vector3d(0d, 0d, 0d);
            
            // get the max/min boundaries of brush vertices on whole level
            for(T3DActor actor : convertedActors){
                
                if(actor instanceof T3DBrush){
                    
                    T3DBrush brush = (T3DBrush) actor;
                    
                    Vector3d maxA = brush.getMaxVertexPos();
                    Vector3d minA = brush.getMinVertexPos();

                    max.x = Math.max(max.x, maxA.x);
                    max.y = Math.max(max.y, maxA.y);
                    max.z = Math.max(max.z, maxA.z);
                    
                    min.x = Math.min(min.x, minA.x);
                    min.y = Math.min(min.y, minA.y);
                    min.z = Math.min(min.z, minA.z);
                }
            }
            
            Double offset = 100d;
            
            // box dimensions that would fit perfectly the level in
            levelDimension = new Vector3d();
            levelDimension.x = Math.abs(max.x) + Math.abs(min.x);
            levelDimension.y = Math.abs(max.y) + Math.abs(min.y);
            levelDimension.z = Math.abs(max.z) + Math.abs(min.z);
            
            Vector3d loc = new Vector3d();
            loc.x = (max.x + min.x) / 2;
            loc.y = (max.y + min.y) / 2;
            loc.z = (max.z + min.z) / 2;
            
            boundBoxLocalisation = loc;
        }
        
        return levelDimension;
    }
    
    /**
     * Write footer of converted t3d file
     * // Begin Map Name=/Game/RestrictedAssets/Maps/WIP/DM-SolarTest
     * // Begin Level NAME=PersistentLevel
     * TODO check for UE1/UE2
     * @throws IOException 
     */
    private void writeFooter() throws IOException{
        
        
        if(mapConverter.toUnrealEngine4()){
            

            Vector3d boundBox = getLevelDimensions();
            Double offset = 100d;

            // Automatically add a lightMassVolume around the whole level
            T3DBrush lightMassVolume = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
            lightMassVolume.location = boundBoxLocalisation;
            lightMassVolume.name = "LightMassImpVolume";
            lightMassVolume.brushClass = T3DBrush.BrushClass.LightmassImportanceVolume;
            bwr.write(lightMassVolume.toString());
            
            offset = 150d;
            
            // Automatically add a navigation volume
            // FIXME UED4 editor crashes for unknown reason
            /*
            T3DBrush navMeshBoundsVolume = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
            navMeshBoundsVolume.location = loc;
            navMeshBoundsVolume.name = "NavMeshBndsVolume";
            navMeshBoundsVolume.brushClass = T3DBrush.BrushClass.NavMeshBoundsVolume;
            bwr.write(navMeshBoundsVolume.toString());
            */
        }
        
        
        bwr.write("\tEnd Level\n");
        bwr.write("\tBegin Surface\n");
        bwr.write("\tEnd Surface\n");
        bwr.write("End Map\n");
    }

    /**
     *
     * @param createNoteWhenUnconverted
     */
    public void setCreateNoteWhenUnconverted(boolean createNoteWhenUnconverted) {
        this.createNoteWhenUnconverted = createNoteWhenUnconverted;
    }
    
    
    
    /**
     * Test map conversion
     */
    public static void test(){
        
        try {
            
            File t3dFile = new File("Z:\\TEMP\\UT99Maps\\Test\\DM-UT99-MoverTest.t3d");
            MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, t3dFile, 2.5d);
            
            //((SupU1UT99ToUT4Classes) mc.getSupportedActorClasses()).setConvertOnly("AmbientSound", T3DSound.class);
            mc.convertTo("Z:\\Temp");
            
            System.out.println("Unconverted classes:");
            
            for(String className : mc.getT3dLvlConvertor().unconvertedActors){
                System.out.println(className);
            }
        } catch (Exception ex) {
            Logger.getLogger(T3DLevelConvertor.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        System.exit(0);
    }
    
}
