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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.MapConverter;

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
    
    LinkedList<T3DActor> convertedActors = new LinkedList<T3DActor>();
    
    boolean createNoteWhenUnconverted = true;
    
    Logger logger;
    
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

            writeHeader();

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

            writeFooter();
            
        } finally {
            bwr.close();
            bfr.close();
        }
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
    List<T3DActor> linkedActors = new ArrayList<>();
    
    
    /**
     * Analyze T3D line to get and convert UT data
     * @param line current T3D line being read
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    private void analyzeLine(String line) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        if (line.contains("Begin Actor")) {
            currentClass = getActorClass(line);
            
            if (mapConverter.getSupportedActorClasses().canBeConverted(currentClass)) {
                utActorClass = mapConverter.getSupportedActorClasses().getConvertActorClass(currentClass);
                banalyseline = true;

                if (utActorClass != null) {
                    Constructor cons = utActorClass.getConstructor(MapConverter.class);
                    uta = (T3DActor) cons.newInstance(mapConverter);
                    convertedActors.add(uta);
                    uta.analyseT3DData(line);
                    
                    if(uta.isLinked){
                        linkedActors.add(uta);
                    }
                } 
                // Means we copy line without any "conversion"
                else {
                    bwr.write(line + "\n");
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
                    } else {
                        logger.warning("Unconverted "+currentClass);
                        banalyseline = false;
                    }
                }
            }
        } 
        
        // Actor End - We write converted data to t3d file
        else if (line.contains("End Actor")) {

            if (banalyseline) {
                if (uta != null && uta.isValid() && uta.isValidWriting()) {
                    // we might want to only re-scale map
                    if(uta.getMapConverter().getOutputGame() != uta.getMapConverter().getInputGame()){
                        uta.convert();
                    }
                    // rescale if needed 
                    // must always be done after convert
                    uta.scale(mapConverter.getScale()); 
                    bwr.write(uta.toString());
                } 
                // Means we copy line without any "conversion"
                // not recommended since can crash editor on import sometimes
                else {
                    bwr.write(line + "\n");
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
                    uta.analyseT3DData(line);
                } else {
                    bwr.write(line + "\n");
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
        bwr.write("\tBegin Level NAME=PersistentLevel\n");
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
            
            Vector3d max = new Vector3d(0d, 0d, 0d);
            Vector3d min = new Vector3d(0d, 0d, 0d);
            
            // get the max/min boundaries of brush vertices on whole level
            for(T3DActor actor : convertedActors){
                
                if(actor instanceof T3DBrush){
                    Vector3d maxA = ((T3DBrush) actor).getMaxVertexPos();
                    Vector3d minA = ((T3DBrush) actor).getMinVertexPos();

                    max.x = Math.max(max.x, maxA.x);
                    max.y = Math.max(max.y, maxA.y);
                    max.z = Math.max(max.z, maxA.z);
                    
                    min.x = Math.min(min.x, minA.x);
                    min.y = Math.min(min.y, minA.y);
                    min.z = Math.min(min.z, minA.z);
                }
            }
            
            final Double offset = 150d;
            
            // box dimensions that would fit perfectly the level in
            Vector3d boundBox = new Vector3d();
            boundBox.x = Math.abs(max.x) + Math.abs(min.x);
            boundBox.y = Math.abs(max.y) + Math.abs(min.y);
            boundBox.z = Math.abs(max.z) + Math.abs(min.z);
            
            // Automatically add a lightMassVolume around the whole level
            T3DBrush lightMassVolume = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
            Vector3d loc = new Vector3d(0d, 0d, 0d);
            
            loc.x = (max.x + min.x) / 2;
            loc.y = (max.y + min.y) / 2;
            loc.z = (max.z + min.z) / 2;
            
            lightMassVolume.location = loc;
            lightMassVolume.name = "LightMassImpVolume";
            lightMassVolume.brushClass = T3DBrush.BrushClass.LightmassImportanceVolume;
            bwr.write(lightMassVolume.toString());
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

    /**
     * Get list of actors that are set as linked with other one
     * This helps converting actor to set properties
     * dependant of other linked actor 
     * (e.g: teleporter, property "URL" in UT99 linked with other actor
     * with tag property
     * @return List of linked actors
     */
    public List<T3DActor> getLinkedActors() {
        return linkedActors;
    }
    
    
    
}
