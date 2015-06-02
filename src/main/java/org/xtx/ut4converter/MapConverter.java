package org.xtx.ut4converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import javafx.scene.control.TableView;

import javax.imageio.spi.IIORegistry;
import javax.xml.bind.JAXBException;

import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.export.CopyExporter;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.export.UModelExporter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.t3d.T3DLevelConvertor;
import org.xtx.ut4converter.t3d.T3DMatch;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackage;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ui.ConversionViewController;
import org.xtx.ut4converter.ui.TableRowLog;

/**
 * 
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class MapConverter extends Task<T3DLevelConvertor> {
    
    /**
     * UT Game the map will be converted from
     */
    UTGame inputGame;
    
    /**
     * UT Game the map will be converted to
     */
    UTGame outputGame;
    
    public static final String CONV_PATH = File.separator + "Converted";
    
    /**
     * Input map. Can be either a map (.unr, ...) or unreal text map (.t3d)
     */
    File inMap;
    
    /**
     * Final map name, might differ from original one.
     * E.G: AS-Mazon (UT99) -> AS-Mazon-Original (for UT4 for exemple)
     */
    String outMapName;
    
    File inT3d, outT3d;
    
    /**
     * Where all converted stuff will be converted
     */
    Path outPath;
    
    /**
     * Scale factor applied to converted level.
     */
    Double scale;
    
    /**
     * Quick converter of actor name.
     * E.G:
     * "Mover" (U1/UT99) -> "InterpActor" (UT3)
     */
    T3DMatch tm;
    
    /**
     * Tells whether or not map is team based
     */
    private Boolean isTeamGameType;
    
    /**
     * TODO move this to T3D Level converter
     */
    SupportedClasses supportedActorClasses;
    
    /**
     * T3d level converter
     */
    T3DLevelConvertor t3dLvlConvertor;
    
    /**
     * If <code>true</code> textures of the map
     * will be exported and converted.
     */
    private boolean convertTextures = true;
    
    /**
     * If <code>true</code> sounds of the map
     * will be exported and converted
     */
    private boolean convertSounds = true;
    
    /**
     * Changes sound volume of sound actors.
     * For exemple, if soundVolumeRatio < 100%, volume will be decreased
     */
    public Float soundVolumeFactor;
    
    /**
     * Changes sound volume of sound actors.
     * For exemple, if soundVolumeRatio < 100%, volume will be decreased
     */
    public Float brightnessFactor;
    
    /**
     * If <code>true</code> staticmeshes of the map
     * will be exported and converted
     */
    private boolean convertStaticMeshes = true;
    
    /**
     * If <code>true</code> music of the map
     * will be exported and converted
     */
    private boolean convertMusic = true;
    
    /**
     * Allow to extract packages.
     * There should be always only one instanced
     */
    public List<UTPackageExtractor> packageExtractors;
    
    


    /**
     * Unreal packages used in map
     * Can be sounds, textures, ...
     */
    public Map<String, UPackage> mapPackages = new HashMap<>();
    
    /**
     * User configuration which allows to know
     * where UT games are installed for exemple
     */
    UserConfig userConfig;

    /**
     * Reference to user interface
     */
    ConversionViewController conversionViewController;
    
    /**
     * Global logger
     */
    static final Logger logger = Logger.getLogger("MapConverter");
    
    /**
     * Original UT game the map comes from
     * @return
     */
    public UTGame getInputGame() {
        return inputGame;
    }

    /**
     * UT game the map will be converted to
     * @return
     */
    public UTGame getOutputGame() {
        return outputGame;
    }

    /**
     * Input map that will be converted 
     * (Unreal Map (.unr, .ut2) or Unreal Text Map file (.t3d)
     * @return
     */
    public File getInMap() {
        return inMap;
    }

    /**
     * Scale factor applied when converting
     * @return
     */
    public Double getScale() {
        return scale;
    }

    /**
     * 
     * @param inputGame Input UT Game
     * @param outputGame Output UT Game
     */
    public MapConverter(UTGame inputGame, UTGame outputGame){
        this.inputGame = inputGame;
        this.outputGame = outputGame;
        initialise();
    }
    
    /**
     * 
     * @param inputGame Input game the map originally comes from
     * @param outputGame Output game the map will be converted to
     * @param inpMap Map to be converted (either a t3d file or map)
     * @param path
     */
    public MapConverter(UTGame inputGame, UTGame outputGame, File inpMap, String path) {
        this.inputGame = inputGame;
        this.outputGame = outputGame;
        this.inMap = inpMap;
        this.outPath = Paths.get(path);
        initialise();
    }
    
    public MapConverter(UTGame inputGame, UTGame outputGame, File inpMap) {
        this.inputGame = inputGame;
        this.outputGame = outputGame;
        initialise();
    }
    
    /**
     * Indicates that gametype is team based
     * @return true is gametype is team based
     */
    public Boolean isTeamGameType() {
        return isTeamGameType;
    }

    /**
     *
     * @param isTeamGameType
     */
    public void setIsTeamGameType(Boolean isTeamGameType) {
        this.isTeamGameType = isTeamGameType;
    }
    
    /**
     * Tried to find property converted to some other game ...
     * @param name
     * @param withT3dClass
     * @param properties
     * @return 
     */
    public T3DMatch.Match getMatchFor(String name, boolean withT3dClass, Map<String, String> properties){
        return tm.getMatchFor(name, inputGame, outputGame, withT3dClass, properties);
    }
    
    /**
     *
     * @return
     */
    public HashMap<String, T3DMatch.Match> getActorClassMatch(){
        return tm.getActorClassMatch(inputGame, outputGame);
    }
    
    
    private void initOutMapName(){
        if(outMapName==null){
            // TODO being able to set it manually (chosen by user)
            outMapName = inMap.getName().split("\\.")[0] + "-" + inputGame.shortName;

            // Remove bad chars from name (e.g: DM-Cybrosis][ ->  DM-Cybrosis)
            // else ue4 editor won't be able to set sounds or textures to actors
            outMapName = T3DUtils.filterName(outMapName);
        }
    }
    
    private void initialise(){
        
        if(this.outPath == null && inMap != null){
            this.outPath = Paths.get(this.getMapConvertFolder().toURI());
        }
        
        // support for reading targa files
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new com.realityinteractive.imageio.tga.TGAImageReaderSpi());


        scale = 1d;
        
        try {
            
            tm = new T3DMatch(inputGame);
                      
            if(inMap != null ){
            	if(inMap.getName().endsWith(".t3d")){
            		inT3d = inMap;
            	}
            	
            	if(isTeamGameType == null){
                    isTeamGameType = UTGameTypes.isTeamBasedFromMapName(inT3d != null ? inT3d.getName() : inMap.getName());
                }
            	
            	getTempExportFolder().mkdirs();
            	
            	initOutMapName();
            }
            
            
            supportedActorClasses = new SupportedClasses(this);
            
            // Unreal Text map level files (.t3d)
            // do not export full info about textures on polygon but only name
            // (not package)
            // E.g: "Begin Polygon Texture=Ebwl Link=1""
            // so need to load the name to package db
            // not for Unreal 1 no need that since latest Unreal 1 patch
            // from oldunreal.com got better version of unreal engine with full info export
            // E.G: "Begin Polygon Texture=Skaarj.Wall.Ebwl Link=1"
            // TODO test if Unreal 1 path set try export t3d level with Unreal 1 for ut99 map
            // so we have always package info at all circumstances
            if(inputGame == UTGame.UT99){
                loadNameToPackage();
            }
            
            userConfig = UserConfig.load();
        } catch (JAXBException | IOException ex) {
            Logger.getLogger(MapConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // init available extractors
        packageExtractors = new ArrayList<>();
        packageExtractors.add(new UCCExporter(this));
        packageExtractors.add(new CopyExporter(this));
        
        if(userConfig.getUModelPath() != null && userConfig.getUModelPath().exists()){
            packageExtractors.add(new UModelExporter(this));
        }
    }
    
    /**
     * All logs redirect to user interface thought table
     * TODO write log file
     */
    private void addLoggerHandlers(){
        
        if(conversionViewController == null || conversionViewController.getConvLogTableView() == null){
            return;
        }
        
        conversionViewController.getProgressBar().progressProperty().bind(progressProperty());
        conversionViewController.getProgressIndicator().progressProperty().bind(progressProperty());
        conversionViewController.getProgressMessage().textProperty().bind(messageProperty());
        
        final TableView<TableRowLog> t = conversionViewController.getConvLogTableView();
        t.getItems().clear();
        
        
        logger.addHandler(new Handler() {

            @Override
            public void publish(LogRecord record) {
                t.getItems().add(new TableRowLog(record));
            }

            @Override
            public void flush() {
                t.getItems().clear();
            }

            @Override
            public void close() throws SecurityException {
                // nothing to do
            }
        });
    }
    
    /**
     * Converts level
     * @throws Exception 
     */
    public void convert() throws Exception {
        
        logger.log(Level.INFO, "*****************************************");
        logger.log(Level.INFO, "Conversion of " + inMap.getName() + " to " + outputGame.name);
        logger.log(Level.INFO, "Scale Factor: " + scale);
        
        updateProgress(0, 100);

        if(!outPath.toFile().exists()){
            outPath.toFile().mkdirs();
        }
        
        // Export unreal map to Unreal Text map
        if(inT3d == null){
            updateProgress(10, 100);
            updateMessage("Exporting map to unreal text file");
            inT3d = UCCExporter.exportLevelToT3d(this, inMap);
            updateProgress(20, 100);
        }
        
        outT3d = new File(outPath.toFile().getAbsolutePath() + File.separator + outMapName + ".t3d");
        
        // t3d ever exported or directly converting from t3d file, then skip export of it 
        // and directly convert it
        t3dLvlConvertor = new T3DLevelConvertor(inT3d, outT3d, this);
        updateMessage("Converting "+inT3d.getName()+" to "+outT3d.getName());
        t3dLvlConvertor.convert();
        updateProgress(80, 100);
        
        cleanAndConvertRessources();
        
        updateProgress(100, 100);
        updateMessage("All done!");
        logger.log(Level.INFO, "Map was succesfully converted to "+getOutT3d().getAbsolutePath());
    }
    
    
    /**
     * Delete unused files
     * and convert them to good format if needed.
     * (e.g: convert staticmeshes to .ase or .fbx format for import in UE4)
     * @throws IOException 
     */
    private void cleanAndConvertRessources() throws IOException {
        
        updateMessage("Converting ressource files");
        boolean wasConverted;
        
        // remove unecessary exported files
        // convert them to some new file format if needed
        // and rename them to fit with "naming" standards
        for(UPackage unrealPackage : mapPackages.values()){
            
            for(UPackageRessource ressource : unrealPackage.getRessources()){
                
                wasConverted = false;
                File exportedFile = ressource.getExportedFile();
                
                if(exportedFile != null){
                    
                    if(!ressource.isUsedInMap()){
                        if(exportedFile.delete()){
                            logger.fine("Deleted unused file "+exportedFile);
                        }
                    } 
                    
                    // Renaming exported files (e.g: Stream2.wav -> AmbOutside_Looping_Stream2.wav)
                    else  {

                        // Some sounds and/or textures might need to be converted for correct import in UE4
                        if(ressource.needsConversion(this)){
                            exportedFile = ressource.convert(logger, userConfig);
                            ressource.setExportedFile(exportedFile);
                            wasConverted = true;
                        }
                        
                        File newFile = new File(getMapConvertFolder().getAbsolutePath() + File.separator + ressource.getType().getName() + File.separator + ressource.getConvertedFileName());
                        newFile.mkdirs();
                        newFile.createNewFile();
                        
                        // sometimes it does not find the exported texture (? ... weird)
                        if(exportedFile.exists() && exportedFile.isFile()){
                            Files.copy(exportedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                        
                        if(exportedFile.delete()){
                            logger.fine("Deleted "+exportedFile);
                        }
                        
                        exportedFile = newFile;
                        
                        if(wasConverted){
                            logger.fine("Converted "+ressource.getType().name()+" :"+newFile.getName());
                        }
                    }
                }
            }
        }
        
        updateMessage("Deleting temporary files");
        
        // DELETE ALL IN TEMP FOLDER
        int i = 1;
        
        File[] files = getTempExportFolder().listFiles();
        
        for(File f : files){
        	
        	
            if(f.delete()){
            	logger.log(Level.FINE, i + "/" + files.length +" - Deleted " + f);
            } else {
            	f.deleteOnExit();
            	logger.log(Level.FINE, i + "/" + files.length + " - Could not delete " + f);
            }
            
            i ++;
        }
        
        logger.log(Level.FINE, "Deleting folder"+getTempExportFolder());
        getTempExportFolder().delete();
        
        
        // Create a folder for this map in UE4Editor
        // and copy a simple existing .uasset file so we can see the folder created in UT4 editor ...
        if(toUT4()){
            UserGameConfig userGameConfig = userConfig.getGameConfigByGame(UTGame.UT4);
            
            if(userGameConfig.getPath() == null || !userGameConfig.getPath().exists()){
            	logger.log(Level.WARNING, "UT4 Editor path not set in settings!");
            	return;
            }
            
            File restrictedAssetsFolder = new File(userGameConfig.getPath() + File.separator + "UnrealTournament" + File.separator + "Content" + File.separator + "RestrictedAssets");
            File wipFolder = new File(restrictedAssetsFolder + File.separator + "Maps" + File.separator + "WIP");
            File wipConvertedMapFolder = new File(wipFolder + File.separator + getOutMapName());
            wipConvertedMapFolder.mkdirs();
            
            logger.log(Level.FINE, "Creating "+wipConvertedMapFolder);
            
            // copy small .uasset file so the folder will appear in UT4 editor ....
            File uassetFile = new File(restrictedAssetsFolder + File.separator + "Blueprints" + File.separator + "Lift" + File.separator + "Curves" + File.separator + "EaseIn-Out.uasset");
            File uassetCopy = new File(wipConvertedMapFolder + File.separator + "dummyfile.uasset");
            
            if(!uassetCopy.exists()){
            	Files.copy(uassetFile.toPath(), uassetCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     *
     * @return
     */
    public SupportedClasses getSupportedActorClasses() {
        return supportedActorClasses;
    }

    /**
     *
     * @return
     */
    public T3DLevelConvertor getT3dLvlConvertor() {
        return t3dLvlConvertor;
    }

    /**
     *
     * @param t3dLvlConvertor
     */
    public void setT3dLvlConvertor(T3DLevelConvertor t3dLvlConvertor) {
        this.t3dLvlConvertor = t3dLvlConvertor;
    }

    public String getOutMapName() {
        return outMapName;
    }

    
    /**
     * Current user configuration such as program path for UT99 and so on ...
     * @return 
     */
    public UserConfig getUserConfig() {
        return userConfig;
    }
    
    /**
     * 
     * @return <code>true</code> if the output game is using unreal engine 4
     */
    public boolean toUnrealEngine4(){
        return UTGames.isUnrealEngine4(this.getOutputGame());
    }
    
    /**
     *
     * @return
     */
    public UTGames.UnrealEngine getUnrealEngineTo(){
        return this.getOutputGame().engine;
    }
    
    /**
     * Indicated if converting from UT using Unreal Engine 1 or Unreal Engine 2
     * (basically Unreal1, UT99, Unreal 2, UT2003 and UT2004)
     * @return true if converting from Unreal Engine 1 or 2 UTx game
     * @deprecated Use isFrom
     */
    public boolean fromUE1OrUE2(){
        return UTGames.isUnrealEngine1(this.getInputGame()) || UTGames.isUnrealEngine2(this.getInputGame());
    }
    
    public boolean isFrom(UnrealEngine... engines){
        
        if(engines.length == 0){
            return false;
        }
        
        for(UnrealEngine engine : engines){
            if(engine == this.getInputGame().engine){
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isTo(UnrealEngine... engines){
        
        if(engines.length == 0){
            return false;
        }
        
        for(UnrealEngine engine : engines){
            if(engine == this.getOutputGame().engine){
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Indicated if converting to UT using Unreal Engine 1 or Unreal Engine 2
     * (basically Unreal1, UT99, Unreal 2, UT2003 and UT2004)
     * @return true if converting to Unreal Engine 1 or 2 UTx game
     * @deprecated Use isFrom
     */
    public boolean toUE1OrUE2(){
        return UTGames.isUnrealEngine1(this.getOutputGame()) || UTGames.isUnrealEngine2(this.getOutputGame());
    }
    
    /**
     * Indicates games being converted from some UT game in unreal engine 3 or 4
     * @return 
     */
    public boolean fromUE1orUE2OrUE3(){
        return UTGames.isUnrealEngine1(this.getInputGame()) 
                || UTGames.isUnrealEngine2(this.getInputGame()) 
                    || UTGames.isUnrealEngine3(this.getInputGame());
    }
    
    /**
     * Indicates games being converted from some UT game in unreal engine 3 or 4
     * @return 
     */
    public boolean fromUE3OrUE4(){
        return UTGames.isUnrealEngine3(this.getInputGame()) || UTGames.isUnrealEngine4(this.getInputGame());
    }
    
    /**
     * Indicates games being converted to some UT game in unreal engine 3 or 4
     * @return 
     */
    public boolean toUE3OrUE4(){
        return UTGames.isUnrealEngine3(this.getOutputGame()) || UTGames.isUnrealEngine4(this.getOutputGame());
    }
    
    /**
     * Indicated game being converted to Unreal Engine 3 game (basically only UT3)
     * @return 
     * @deprecated Use isTo
     */
    public boolean toUE3(){
        return UTGames.isUnrealEngine3(this.getOutputGame());
    }
    
    /**
     * Tells if converting UT game using Unreal Engine 1 or 2
     * is being converted to some other UT game using Unreal Engine 3 or 4.
     * 
     * @return true if converting UT game using Unreal Engine 1 or 2 to UT game using Unreal Engine 3 or 4
     */
    public boolean isFromUE1UE2ToUE3UE4(){
        return fromUE1OrUE2() && toUE3OrUE4();
    }
    
    public boolean fromUE123ToUE4(){
        return fromUE1orUE2OrUE3() && toUnrealEngine4();
    }
    
    public boolean toUT4(){
        return outputGame == UTGame.UT4;
    }
    
    /**
     * @deprecated Use isTo
     * @return 
     */
    public boolean toUE4(){
        return UTGames.isUnrealEngine4(this.getOutputGame());
    }
    
    /**
     * Tells if converting UT game using Unreal Engine 1 or 2
     * is being converted to some other UT game using Unreal Engine 3 or 4.
     * 
     * @return true if converting UT game using Unreal Engine 1 or 2 to UT game using Unreal Engine 3 or 4
     */
    public boolean isFromUE3UE4ToUE1UE2(){
        return toUE1OrUE2() && fromUE3OrUE4();
    }

    public File getOutT3d() {
        return outT3d;
    }

    public void setConversionViewController(ConversionViewController conversionViewController) {
        this.conversionViewController = conversionViewController;
        addLoggerHandlers();
    }
    
    public Logger getLogger(){
        return logger;
    }

    /**
     * <UT4ConverterFolder>/Converted
     * @return 
     */
    private static File getBaseConvertFolder(){
        return new File(Installation.getProgramFolder().getAbsolutePath() + File.separator + CONV_PATH);
    }
    
    /**
     * <UT4ConverterFolder>/Converted/<MapName>
     * @return 
     */
    public File getMapConvertFolder(){
        return new File(getBaseConvertFolder()+ File.separator + getInMap().getName().split("\\.")[0]);
    }
    
    /**
     * <UT4ConverterFolder>/Converted/<MapName>/Temp
     * @return 
     */
    public  File getTempExportFolder(){
        return new File(getMapConvertFolder() + File.separator + "Temp");
    }
    
    Map<String, String> nameToPackage = new HashMap<>();
    
    
    private void loadNameToPackage() throws FileNotFoundException, IOException {
        
        File dbFile = new File(Installation.getProgramFolder() + File.separator + "conf" + File.separator + inputGame.shortName+"TexNameToPackage.txt");
        
        try (FileReader fr = new FileReader(dbFile); BufferedReader bfr = new BufferedReader(fr);) {
            
            String line;
            
            while((line=bfr.readLine())!=null){
                String[] sp = line.split("\\:");
                nameToPackage.put(sp[0], sp[1]);
            }
        }
    }
    
     /**
     * T3D actor properties which are ressources (basically sounds, music, textures, ...)
     * 
     * @param fullRessourceName Full name of ressource (e.g: AmbModern.Looping.comp1 )
     * @param type Type of ressource (sound, staticmesh, texture, ...)
     * @return 
     */
    public UPackageRessource getUPackageRessource(String fullRessourceName, T3DRessource.Type type){
        
        if(fullRessourceName == null){
            return null;
        }
        
        String[] split = fullRessourceName.split("\\.");
        
        String packageName;
        
        // having only name of ressource not which package it belongs to
        // happens for UE1/2 where polygon t3d data only store name
        // so we using the old "ut3 converter" name to package db until finding a better way ...
        if(split.length <= 1 && type == T3DRessource.Type.TEXTURE){
            String name = split[0];
            packageName = nameToPackage.get(name.toLowerCase());
            fullRessourceName = packageName + "." + name;
        } else {
            packageName = fullRessourceName.split("\\.")[0];
        }

        
        // Ressource ever created while parsing previous t3d lines
        // we return it
        if(mapPackages.containsKey(packageName)){
            
            UPackage unrealPackage = mapPackages.get(packageName);
            UPackageRessource uPackageRessource = unrealPackage.findRessource(fullRessourceName);
                    
            if(uPackageRessource != null){
                uPackageRessource.setIsUsedInMap(true);
                return uPackageRessource;
            }
            // Need to create one
            else {
                return new UPackageRessource(fullRessourceName, type, getInputGame(), unrealPackage, true);
            }
        } 
        
        else {
            
            // need to create one (unreal package info is auto-created)
            UPackageRessource upRessource =  new UPackageRessource(fullRessourceName, type, getInputGame(), true);
            mapPackages.put(packageName, upRessource.getUnrealPackage());
            return upRessource;
        }
    }
    
    /**
     * Force converter not to convert any binary ressources
     */
    public void noConvertRessources(){
        this.convertSounds = false;
        this.convertStaticMeshes = false;
        this.convertTextures = false;
        this.convertMusic = false;
    }

    @Override
    protected T3DLevelConvertor call() throws Exception {
        convert();
        return t3dLvlConvertor;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public ConversionViewController getConversionViewController() {
        return conversionViewController;
    }

    public void setInMap(File inMap) {
        this.inMap = inMap;
        initialise();
    }

    public Path getOutPath() {
        return outPath;
    }

    public boolean convertTextures() {
        return convertTextures;
    }
    

    /**
     * Says if program can convert/export textures.
     * For unreal engine <= 2 we can still user the stock "ucc.exe" one
     * except for Unreal 2 which always produces "0" bytes file size
     * but for UUE3/4 need umodel program
     * @return <code>true</code> if it's possible to convert texture otherwise false
     */
    public boolean canConvertTextures(){
    	
    	if(userConfig.getUModelPath() != null && userConfig.getUModelPath().exists()){
    		return true;
    	}
    	
    	// todo handle/test ucc_bin for Unreal 1 for linux with www.oldunreal.com patch
    	if(inputGame.engine.version <= UnrealEngine.UE2.version && inputGame != UTGame.U2){
    		return userConfig.hasGamePathSet(inputGame) && Installation.isWindows();
    	}
    	
    	return false;
    }
    
    /**
     * Says if program can convert/export sounds.
     * @return
     */
    public boolean canConvertSounds(){
    	
    	if(userConfig.getUModelPath() != null && userConfig.getUModelPath().exists()){
    		return true;
    	}
    	
    	if(inputGame.engine.version <= UnrealEngine.UE2.version){
    		// todo handle/test ucc_bin for Unreal 1 for linux with www.oldunreal.com patch
    		return userConfig.hasGamePathSet(inputGame) && Installation.isWindows();
    	}
    	
    	return false;
    }
    
    public boolean canConvertMusic(){
    	// just a file copy for UT2004 (.ogg files ...)
    	if(inputGame.engine == UnrealEngine.UE2){
    		return true;
    	} else {
    		return canConvertSounds();
    	}
    }

    public boolean convertSounds() {
        return convertSounds;
    }

    public boolean convertStaticMeshes() {
        return convertStaticMeshes;
    }
    
    /**
     * Says if program can export staticmeshes
     * @return
     */
    public boolean canConvertStaticMeshes(){
    	
    	// no staticmeshes for UE1 (UT99 + Unreal 1)
    	if(inputGame.engine == UnrealEngine.UE1){
    		return false;
    	}
    	
    	// note: can export with ucc for UT2003/UT2004 but .t3d mesh format
    	// not working for import with UE4
    	// conversion only partial for umodel
    	// since pskx cannot be used by UE4 but can be imported with blender
    	// and exported to fbx or obj format file allowed by UE4
    	return userConfig.getUModelPath() != null && userConfig.getUModelPath().exists();
    }

    public boolean convertMusic() {
        return convertMusic;
    }

    public void setConvertTextures(boolean convertTextures) {
        this.convertTextures = convertTextures;
    }

    public void setConvertSounds(boolean convertSounds) {
        this.convertSounds = convertSounds;
    }

    public void setConvertStaticMeshes(boolean convertStaticMeshes) {
        this.convertStaticMeshes = convertStaticMeshes;
    }

    public void setConvertMusic(boolean convertMusic) {
        this.convertMusic = convertMusic;
    }
    
    
    
}

