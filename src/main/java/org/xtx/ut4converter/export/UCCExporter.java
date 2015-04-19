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
import org.xtx.ut4converter.ucore.UPackage;

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
        UNKNOWN("UNKNOWN"), // fake option so will make crash export
        LEVEL_T3D("Level t3d"),
        SOUND_WAV("Sound wav"),
        TEXTURE_TGA("Texture tga"), // TODO check UE support other type like dds, tga
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
    
    /**
     * Get the right command line option for ucc.exe
     * for exporting ressources
     * @param type Type of ressource
     * @return ucc command line options
     */
    private UccOptions getUccOptions(T3DRessource.Type type){
        
        if(type == T3DRessource.Type.SOUND){
            return UccOptions.SOUND_WAV;
        } 
        
        else if (type == T3DRessource.Type.MUSIC){
            return UccOptions.UNKNOWN;
        }
        
        else if (type == T3DRessource.Type.TEXTURE){
            return UccOptions.TEXTURE_TGA;
        }
        
        else if (type == T3DRessource.Type.LEVEL){
            return UccOptions.LEVEL_T3D;
        }
        
        return UccOptions.UNKNOWN;
    }
    
    public UCCExporter(MapConverter mapConverter) {
        super(mapConverter);
        
        userGameConfig = mapConverter.getUserConfig().getGameConfigByGame(mapConverter.getInputGame());
        uccExporterPath = getExporterPath();
    }

    @Override
    public List<File> extract(T3DRessource ressource) throws Exception {
                
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
                logger.log(Level.SEVERE, "Impossible to find {0} unreal package extractor", uccExporterPath.getAbsolutePath());
            }
            
            return null;
        }
        
        
        List<File> exportedFiles = exportT3DMap(ressource);
        // notify map converter of exported files
        UPackage uPackage = new UPackage(mapConverter.getInputGame().engine, uccExporterPath, ressource.type);

        if(!mapConverter.exportedPackages.containsKey(uPackage)){
            mapConverter.exportedPackages.put(uPackage, exportedFiles);
        }

        if(!mapConverter.exportedRessources.containsKey(ressource.uPacRessource)){
            mapConverter.exportedRessources.put(ressource.uPacRessource, ressource.getFileContainer());
        }

        
        return exportedFiles;
    }
    
    
    /**
     * Exports unreal map (.unr, .ut2, ...) to Unreal Text level file (.t3d)
     * @param mapConverter MapConverter options, used to determined which game 
     * @param unrealMap Map to export to .t3d
     * @return t3d file map exported
     */
    public static File exportLevelToT3d(MapConverter mapConverter, File unrealMap) throws Exception{
        
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
     * @param type Type of ressource to export (Textures, Sounds, ...)
     * @param unrealPackage Unreal package file to export (can be a map (.unr) file or single package (.uax sound file package)
     * @return Batch file created
     */
    private File createExportFileBatch(File unrealPackage, T3DRessource.Type type) throws IOException
    {

        File fbat = File.createTempFile("UCCExportPackage", ".bat");
        
        try ( FileWriter fw = new FileWriter(fbat); BufferedWriter bwr = new BufferedWriter(fw)) {
      
            String drive = uccExporterPath.getAbsolutePath().substring(0, 2);
            bwr.write(drive+"\n"); //switch to good drive (e.g, executing UT4 converter from Z:\\ drive but map is in C:\\ drive
            bwr.write("cd \""+uccExporterPath.getParent()+"\"\n");
            bwr.write(getCommandLine(unrealPackage.getName(), type));
            
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
        
        // UT4 TODO CHECK if exporter does exists in command line
        else {
            throw new UnsupportedOperationException("Unsupported UCC exporter for Unreal Engine "+mapConverter.getInputGame().engine.name());
        }
    }
    
    /**
     * Get command line for exporting Unreal Package ressources
     * @param fileName File name or full path filename of Unreal Package to extract
     * @return Full command line for extracting stuff from unreal packages (including maps)
     */
    private String getCommandLine(String fileName, T3DRessource.Type type){
        
        if( mapConverter.getInputGame().engine.version == UTGames.UnrealEngine.UE1.version ){
            return uccExporterPath.getName() + " batchexport  "+ fileName+ " " + getUccOptions(type) + " " + getExportFolder();
        } 
        
        else {
            return "\"" + uccExporterPath.getAbsolutePath() + "\" batchexport  "+ fileName+ " " + getUccOptions(type);
        }
    }
    
    /**
     * 
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    private List<File> exportT3DMap(T3DRessource ressource) throws IOException, InterruptedException
    {
        List<File> exportedFiles = new ArrayList<>();
        
        File unrealMapCopy = null;
        File u1Batch = null;
        
        try {
            logger.log(Level.INFO, "Exporting "+ressource.getFileContainer().getName()+" package");

            // Copy of unreal package to folder of ucc.exe (/System) for U1/U2
            unrealMapCopy = new File(uccExporterPath.getParent() + File.separator + ressource.getFileContainer().getName());
            
            logger.log(Level.INFO, "Creating " + unrealMapCopy.getAbsolutePath());
            unrealMapCopy.createNewFile();
            Files.copy(ressource.getFileContainer().toPath(), unrealMapCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);

            List<String> logLines = new ArrayList<>();

            String command;
            
            // For unreal 1 or ut99 we do need to create a batch file
            // because ucc.exe don't work if executing itself with parent folders with whitespaces in name
            // TODO use only if whitespaces in ucc.exe or map folder name
            if(mapConverter.getInputGame().engine.version < UTGames.UnrealEngine.UE2.version){
                u1Batch = createExportFileBatch(unrealMapCopy, ressource.type);
                command = u1Batch.getAbsolutePath();
            } else {
                command = getCommandLine(unrealMapCopy.getName(), ressource.type);
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
                // Exported Texture GenWarp.Sun128a to Z:\\TEMP\Sun128a.bmp
                else if(logLine.contains("Exported ")){
                    File ressourceFile = new File(logLine.split(" to ")[1]);
                    exportedFiles.add(ressourceFile);
                }
            }


        } finally {
            if(unrealMapCopy != null && unrealMapCopy.exists()){
                if(unrealMapCopy.delete()){
                    logger.info(unrealMapCopy+" unreal package file copy deleted");
                }
            }
            
            if(u1Batch != null && u1Batch.exists()){
                
                if(u1Batch.delete()){
                    logger.info(u1Batch+" batch file deleted");
                }
            }
        }
        
        
        return exportedFiles;
    }
    
    
    public static void test (){
        
        File unrealMap = new File("Z:\\TEMP\\UT99Maps\\AS-Mazon.unr");
        
        MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, unrealMap, Double.NaN);
        
        try {
            UCCExporter.exportLevelToT3d(mc, unrealMap);
        } catch (Exception e){
            System.exit(-1);
        }
        
        System.exit(0);
    }
}
