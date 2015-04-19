/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.export;

import com.sun.istack.internal.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.t3d.T3DRessource;

/**
 * Export ressources from map
 * such as sounds to .wav,
 * textures to .bmp and so on ...
 * using the original 'ucc.exe program' ...
 * TODO test exporter when UT4 Converter as parent folder with spaces
 * @author XtremeXp
 */
public final class UCCExporter extends UTPackageExtractor {

    /**
     * User game configuration
     * used to get UT game path
     * and so Unreal Package extractor as well
     */
    protected UserGameConfig userGameConfig;
    
    /**
     * File path of ucc.exe or ut3.com program.
     * Depends of user game settings
     */
    File uccExporterPath;
    
    private enum Name{
        UCC_EXE("ucc.exe"),
        UT3_COM("ut3.com");
        
        String programName;
        
        Name(String programName){
            this.programName = programName;
        }
        
        public String toString(){
            return this.programName;
        }
    }
    
    /**
     * Exporter options of embedded UT extractor
     * for Unreal Packages
     */
    private enum UccOptions{
        LEVEL_T3D("Level t3d"),
        SOUND_WAV("Sound wav"),
        STATICMESH_T3D("StaticMesh t3d"),
        CLASS_UC("Class uc");

        String option;

        UccOptions(String command){
            this.option = command;
        }

        public String getOption() {
            return option;
        }
        
        public String toString(){
            return this.option;
        }
    }
    
    public UCCExporter(MapConverter mapConverter) {
        super(mapConverter);
        
        userGameConfig = mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame());
        uccExporterPath = getExporterPath();
    }

    @Override
    public List<File> extract(T3DRessource ressource) {
                
        if(userGameConfig.getPath() == null || !userGameConfig.getPath().exists()){
            logger.log(Level.SEVERE, "Game path not set or does not exists in user settings for game "+ mapConverter.getInputGame().name);
            return null;
        }
        
        
        if(!uccExporterPath.exists()){
            
            // For Unreal 1, by default ucc.exe program is not embedded, need download latest patch from www.oldunreal.com !
            if(mapConverter.getOutputGame() == UTGames.UTGame.U1){
                logger.log(Level.SEVERE, "{0} program does not exist. Download and install latest {1} patch at www.oldunreal.com", new Object[]{uccExporterPath.getName(), UTGames.UTGame.U1.name});
            } 
            else {
                logger.log(Level.SEVERE, "Impossible to find {0} t3d level extractor", uccExporterPath.getAbsolutePath());
            }
            
            return null;
        }
        
        if(ressource.type == T3DRessource.Type.LEVEL){
            try {
                File t3dMap = exportT3DMap(ressource);
                
                if(t3dMap != null){
                    List<File> t3dFiles = new ArrayList<>();
                    t3dFiles.add(t3dMap);
                    return t3dFiles;
                } else {
                    return null;
                }
            } catch (IOException | InterruptedException ex) {
                java.util.logging.Logger.getLogger(UCCExporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        else {
            throw new UnsupportedOperationException("Extraction of "+ressource.type.getName()+" not implemented yet");
        }
        
        return null;
    }
    
    
    /**
     * Exports unreal map (.unr, .ut2, ...) to Unreal Text level file (.t3d)
     * @param mapConverter MapConverter options, used to determined which game 
     * @param unrealMap Map to export to .t3d
     * @return t3d file map exported
     */
    public static File exportLevelToT3d(MapConverter mapConverter, File unrealMap){
        
        if(unrealMap == null || !unrealMap.exists()){
            Logger.getLogger(UCCExporter.class).warning("Impossible to export");
            return null;
        }
        
        T3DRessource t3dRessource = new T3DRessource(unrealMap.getAbsolutePath(), T3DRessource.Type.LEVEL, mapConverter);
        UCCExporter ucE = new UCCExporter(mapConverter);
        
        List<File> files = ucE.extract(t3dRessource);
        
        return !files.isEmpty()?files.get(0):null;
    }
    
   
    /**
     * Creates a batch script to export ressources
     * that creates a windows .bat script that will
     * go to ucc.exe program and execute it.
     * Used for Unreal Engine 1 based games because
     * ucc.exe t3d exporter map program does not support whitespaces in folder name
     * @return Batch file created
     */
    private File createExportFileBatch(File unrealMapCopy) throws IOException
    {

        File fbat = File.createTempFile("LevelExporter", ".bat");
        
        try ( FileWriter fw = new FileWriter(fbat); BufferedWriter bwr = new BufferedWriter(fw)) {
      
            String drive = uccExporterPath.getAbsolutePath().substring(0, 2);
            bwr.write(drive+"\n"); //switch to good drive (e.g, executing UT4 converter from Z:\\ drive but map is in C:\\ drive
            bwr.write("cd \""+uccExporterPath.getParent()+"\"\n");
            bwr.write(getCommandLine(unrealMapCopy.getName()));
            
            bwr.close();
        }
        
        return fbat;
    }
    
    @Override
    public File getExporterPath(){
        
        // U1, UT, U2(TODO CHECK), UT2003(TODO CHECK), UT2004
        if(mapConverter.getInputGame().engine.version < UTGames.UnrealEngine.UE3.version){
            return new File(userGameConfig.getPath() + File.separator + "System" + File.separator + Name.UCC_EXE);
        } 
        
        // UT3
        else if(mapConverter.getInputGame() == UTGames.UTGame.UT3){
            return new File(userGameConfig.getPath() + File.separator + "Binaries" + File.separator + Name.UT3_COM);
        } 
        
        // UT4 TODO CHECK
        else {
            throw new UnsupportedOperationException("Unsupported UCC exporter for Unreal Engine "+mapConverter.getInputGame().engine.name());
        }
    }
    
    /**
     * Get command line for exporting Unreal Package ressources
     * TODO handle other type of exports
     * @param fileName File name or full path filename of Unreal Package to extract
     * @return 
     */
    private String getCommandLine(String fileName){
        
        if( mapConverter.getInputGame().engine.version == UTGames.UnrealEngine.UE1.version ){
            return uccExporterPath.getName() + " batchexport  "+ fileName+ " " + UccOptions.LEVEL_T3D + " " + getExportFolder();
        } 
        
        else {
            return "\"" + uccExporterPath.getAbsolutePath() + "\" batchexport  "+ fileName+ " " + UccOptions.LEVEL_T3D;
        }
    }
    
    /**
     * 
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    private File exportT3DMap(T3DRessource ressource) throws IOException, InterruptedException
    {

        File unrealMapCopy = null;
        File u1Batch = null;
        
        try {
            logger.log(Level.INFO, "Exporting "+ressource.getInName()+" to Unreal Text File (.t3d)");

            // Copy Unreal Map to folder of ucc.exe (/System) for U1/U2
            // or ut3.com (/Binaries) for UT3
            unrealMapCopy = new File(uccExporterPath.getParent() + File.separator + "MapCopy.unr");
            logger.log(Level.INFO, "Creating " + unrealMapCopy.getAbsolutePath());
            unrealMapCopy.createNewFile();
            Files.copy(new File(ressource.getInName()).toPath(), unrealMapCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);

            List<String> logLines = new ArrayList<>();

            String command;
            
            // For unreal 1 or ut99 we do need to create a batch file
            // because ucc.exe don't work if executing itself with parent folders with whitespaces in name
            // TODO use only if whitespaces in ucc.exe or map folder name
            if(mapConverter.getInputGame().engine.version < UTGames.UnrealEngine.UE2.version){
                u1Batch = createExportFileBatch(unrealMapCopy);
                command = u1Batch.getAbsolutePath();
            } else {
                command = getCommandLine(unrealMapCopy.getName());
            }

            logger.log(Level.INFO, command);

            Runtime run = Runtime.getRuntime();
            Process pp = run.exec(command);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(pp.getInputStream()))){
                String line;

                while ((line = in.readLine()) != null) {
                    logLines.add(line);
                }
            } 

            // TODO handle exit value
            pp.waitFor();
            pp.exitValue();
            pp.destroy();

            for (String logLine : logLines) {

                logger.info(logLine);

                if(logLine.contains("Failed")) {
                    String missingpackage = logLine.split("\\'")[2];
                    logger.log(Level.SEVERE, "Impossible to export. Unreal Package "+missingpackage+" missing");
                    return null;
                } 

                else if (logLine.contains("Commandlet batchexport not found")) {
                    logger.log(Level.SEVERE, logLine);
                    return null;
                }
                
                // Exported Level MapCopy.MyLevel to Z:\TEMP\UT4Converter\Conversion\UT99\MyLevel.t3d
                else if(logLine.contains("Exported Level")){
                    File t3dMap = new File(logLine.split(" to ")[1]);
                    logger.log(Level.INFO, "Map exported to "+ t3dMap.getAbsolutePath());
                    return t3dMap;
                }
            }


        } finally {
            if(unrealMapCopy != null && unrealMapCopy.exists()){
                unrealMapCopy.delete();
            }
            
            if(u1Batch != null && u1Batch.exists()){
                u1Batch.delete();
            }
        }
        
        
        return null;
    }
    
    
    public static void test (){
        
        File unrealMap = new File("Z:\\TEMP\\UT99Maps\\AS-Mazon.unr");
        
        MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, unrealMap, Double.NaN);
        File t3dLevelFile = UCCExporter.exportLevelToT3d(mc, unrealMap);
        System.exit(0);
    }
}
