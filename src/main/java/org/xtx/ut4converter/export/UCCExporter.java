/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import com.sun.istack.internal.logging.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.util.List;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;

/**
 * Export ressources from map
 * such as sounds to .wav,
 * textures to .bmp and so on ...
 * using the original 'ucc.exe program' ...
 * @author XtremeXp
 */
public class UCCExporter extends UTPackageExtractor {

    private enum ExportType{
        LEVEL_T3D("Level t3d"),
        SOUND_WAV("Sound wav"),
        STATICMESH_T3D("StaticMesh t3d"),
        CLASS_UC("Class uc");

        String option;

        ExportType(String command){
            this.option = command;
        }

        public String getOption() {
            return option;
        }
    }
    
    public UCCExporter(MapConverter mapConverter, T3DRessource ressource) {
        super(mapConverter, ressource);
    }

    @Override
    public List<File> extract() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public static boolean exportLevelToT3d(File unrealMap){
        
        if(unrealMap == null || !unrealMap.exists()){
            Logger.getLogger(UCCExporter.class).warning("Impossible to export");
        }
        
        return false;
    }
    
   
    /**
     * Creates a batch script to export ressources
     * @return 
     */
    private File createBatchExportFile()
    {
        BufferedWriter bwr = null;

        /*
        try {
            File fbat = File.createTempFile("LevelExporter", ".bat");
            bwr = new BufferedWriter(new FileWriter(fbat));
            
            // Copy file to the /Binaries folder (for UT3) or /System folder (for UT UE1/2 games)
            bwr.write("copy \""+utmap.getAbsolutePath()+"\" \""+uccfilepath.getParent()+"\"\n");
            bwr.write(uccfilepath.getAbsolutePath().split("\\\\")[0]+"\n");
            bwr.write("cd \""+uccfilepath.getParent()+"\"\n");
            
            if(mapConverter.getInputGame() == UTGames.UTGame.UT3){
                bwr.write(BATCH_PROGRAM_UT3+" batchexport \""+utmap.getAbsolutePath()+"\" Level t3d \n");
            } else {
                bwr.write(BATCH_PROGRAM+" batchexport "+utmap.getName()+" Level t3d \""+outputfolder.getAbsolutePath()+"\"\n");
            }
            
            bwr.write("del "+utmap.getName());
            bwr.close();
            return fbat;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(LevelExporter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            if(bwr != null){
                try {
                    bwr.close();
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(LevelExporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }*/
        return null;
    }
    
    public File getExporterPath(){
        /*
        File gamePath = mapConverter.getConfig().getUTxRootFolder(mapConverter.getInputGame());
        
        if(mapConverter.getInputGame() == UTGames.UTGame.UT3){
            return new File(gamePath.getAbsolutePath() + File.separator + "Binaries" + File.separator + "ut3.com");
        } else {
            return new File(gamePath.getAbsolutePath() + File.separator + "System" + File.separator + "ucc.exe");
        }*/
        return null;
    }
    
}
