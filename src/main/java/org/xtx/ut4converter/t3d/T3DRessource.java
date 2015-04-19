/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.io.File;
import java.util.logging.Level;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * 
 * @author XtremeXp
 */
public class T3DRessource {
    
    public static enum Type{
        UNKNOWN("Unknown"),
        LEVEL("Level"),
        TEXTURE("Texture"),
        MESH("Mesh"),
        STATICMESH("StaticMesh"),
        MUSIC("Music"),
        SCRIPT("Script"),
        SOUND("Sound");
        
        String name;
        
        Type(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    
    /**
     * Original name of ressource
     * Could be for exemple:
     * U1 Brush: Texture=rClfClg3x -> rClfClg3x 
     * U1 Sound: AmbientSound=Sound'AmbModern.Looping.comp1' -> AmbModern.Looping.comp1
     */
    String inName;
    
    
    /**
     * New name for converted ressource.
     * Might differ from original one.
     * E.G for texture:
     * UE1: inName = rClfClg3x
     * UE2: inName = <packageName>.rClfClg3x
     * UT3: outName = '<mapname>.<inName>_Mat'
     * UT4: /Game/RestrictedAsset/Maps/WIP/<mapname>/<inName>.uasset
     */
    String outName;
    
    public UPackageRessource uPacRessource;
    
    public Type type;
    
    MapConverter mapConverter;
    
    
    /**
     * Tells if ressource has been or ever been exported
     */
    boolean exported = false;
    
    /**
     * Tells if ressource has been or ever been converted
     */
    boolean converted = false;
    
    /**
     * Default extractor for this ressource.
     * We might use the standard embedded one such as UCC export (UCCExporter)
     * or the UModel one
     */
    UTPackageExtractor extractor;

    /**
     * 
     * @param inName Original ressource name
     * @param type
     * @param mapConverter 
     */
    public T3DRessource(String inName, Type type, MapConverter mapConverter) {
        this.inName = inName;
        this.type = type;
        this.mapConverter = mapConverter;
        initialise();
    }
    
  
    final String UE4_BASEPATH = "/Game/RestrictedAsset/Maps/WIP";
    final String UE4_RES_EXTENSION = ".uasset";
    final String UE12_PACKAGE_MYLEVEL = "myLevel";
    
    /**
     * Guess what will be the converted file name
     * @return 
     */
    private void initialise(){
        
        
        uPacRessource = new UPackageRessource(inName, mapConverter.getInputGame(), type);
        uPacRessource.setPackageFile(getFileContainer());
        
        
        if(mapConverter.getOutputGame().engine == UTGames.UnrealEngine.UE4){
            //  /Game/RestrictedAsset/Maps/WIP/<mapname>/<inName>.uasset
            // TODO use some subfolder for original packages
            // E.G: /Game/RestrictedAsset/Maps/WIP/<mapname>/<oldpackage>/<inName>.uasset
            // /Game/RestrictedAssets/Audio/Ambient/Exteriors/air_wind01.air_wind01
            // 'AmbModern.Looping.comp1' -> /Game/RestrictedAsset/Maps/WIP/<mapname>/<oldpackagename>_<oldpckgroupename>/<inName>
            // AmbientSoundAmbientSoundAmbientSound
            outName = UE4_BASEPATH + "/" + mapConverter.getOutMapName() + "/" + type.name + "/" + inName;
            
            // in UT4 editor click on ressource file -> Create Cue adds a "_Cue" suffix
            if(type == Type.SOUND){
                outName += "_Cue";
            } 
            
            else if(type == Type.TEXTURE){
                outName += "_Mat";
            }
        } 
        
        // UE3: <outMapName>.<typeName>.<inName>_Map
        else if(mapConverter.getOutputGame().engine == UTGames.UnrealEngine.UE3){
            if(type == Type.TEXTURE){
                // when importing in UE3 the auto create material when importing textures
                // adds the "_Mat" suffix
                outName = mapConverter.getOutMapName() + "." + inName + "_Mat";
            }
        }
        
        else if(mapConverter.getOutputGame().engine == UTGames.UnrealEngine.UE2 || mapConverter.getOutputGame().engine == UTGames.UnrealEngine.UE1){
            outName = UE12_PACKAGE_MYLEVEL + File.separator + inName;
        }
    }
    
    
    
    /**
     * Export ressource
     */
    public void export(){
    
        try {
            extractor = UTPackageExtractor.getExtractor(mapConverter, this);
            extractor.extract(this);
        } catch (Exception ex) {
            mapConverter.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public String getInName() {
        return inName;
    }

    public String getOutName() {
        return outName;
    }
    
    /**
     * From ressource name and type
     * guess the original file container of the ressource.
     * E.G: 'DoorsMod.General.mlift6end' in UT99 -> <UT99PATH>/Sounds/DoorsMod.uax
     * @return 
     */
    public File getFileContainer(){
        
        if(type == Type.LEVEL){
            return new File(inName);
        }
        
        // format is 
        if(mapConverter.getInputGame() == UTGames.UTGame.UT4){
            // <UT4Folder>/Content/<NAME>
            //return new File(mapConverter.getConfig().getUT4RootFolder() + File.separator + getInName());
            // TODO
            throw new UnsupportedOperationException("Unsupported operation");
        } 
        
        else if(mapConverter.getInputGame() == UTGames.UTGame.UT3){
            // <UT3Folder>/UTGame/CookedPC/? (hard to say ...) -> do file search on UT3 package)
            throw new UnsupportedOperationException("Unsupported operation");
        }
        
        // U1/UT/U2/UT2003/UT2004
        else if(mapConverter.getInputGame().engine.version <  UTGames.UnrealEngine.UE3.version){
            
            UserConfig uc = mapConverter.getUserConfig();
            
            if(uc != null){
                UserGameConfig ugc = uc.getGameConfigByGame(mapConverter.getInputGame());
                
                if(ugc != null && ugc.getPath() != null){
                    return new File(ugc.getPath().getAbsolutePath() + File.separator + getFolderForPackageType() + File.separator + uPacRessource.getUnrealPackage().getName() + getUnrealPackageFileExtension());
                }
            }
        }

        return null;
    }
    
    /**
     * Return path where unreal packages are stored depending
     * on type of ressource
     * @return Relative folder from UT path where the unreal package file should be
     */
    private String getFolderForPackageType(){
        
        if(type == Type.MUSIC){
            return "Music";
        } 
        
        else if (type == Type.SOUND){
            return "Sounds";
        }
        
        else if (type == Type.TEXTURE){
            return "Textures";
        }
        
        else if (type == Type.STATICMESH){
            return "StaticMeshes";
        }
        
        else if (type == Type.LEVEL){
            return "Maps";
        }
        
        else if (type == Type.SCRIPT){
            return "System";
        }
        
        return null;
    }
    
    /**
     * Return relative path
     * @return 
     */
    private String getUnrealPackageFileExtension(){
        
        if(type == Type.MUSIC){
            return ".umx";
        } 
        
        else if (type == Type.SOUND){
            return ".uax";
        }
        
        else if (type == Type.TEXTURE){
            return ".utx";
        }
        
        else if (type == Type.STATICMESH){
            return ".usx";
        }
        
        else if (type == Type.SCRIPT){
            return ".u";
        }
        
        return null;
    }
    
    
}
