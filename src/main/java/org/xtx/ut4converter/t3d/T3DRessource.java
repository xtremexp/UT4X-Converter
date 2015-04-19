/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.io.File;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;

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
        init();
    }
    
    /**
     * 
     */
    private void init(){
        initOutName();
    }
    
    final String UE4_BASEPATH = "/Game/RestrictedAsset/Maps/WIP";
    final String UE4_RES_EXTENSION = ".uasset";
    final String UE12_PACKAGE_MYLEVEL = "myLevel";
    
    /**
     * 
     * @return 
     */
    private void initOutName(){
        
        if(mapConverter.getOutputGame().engine == UTGames.UnrealEngine.UE4){
            //  /Game/RestrictedAsset/Maps/WIP/<mapname>/<inName>.uasset
            // TODO use some subfolder for original packages
            // E.G: /Game/RestrictedAsset/Maps/WIP/<mapname>/<oldpackage>/<inName>.uasset
            // /Game/RestrictedAssets/Audio/Ambient/Exteriors/air_wind01.air_wind01
            // 'AmbModern.Looping.comp1' -> /Game/RestrictedAsset/Maps/WIP/<mapname>/<oldpackagename>_<oldpckgroupename>/<inName>
            // AmbientSoundAmbientSoundAmbientSound
            outName = UE4_BASEPATH + "/" + mapConverter.getOutMapName() + "/" + type.name + "/" + inName;
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
    
        extractor = UTPackageExtractor.getExtractor(mapConverter, this);
        extractor.extract(this);
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
        } 
        
        else if(mapConverter.getInputGame() == UTGames.UTGame.UT3){
            // <UT3Folder>/UTGame/CookedPC/? (hard to say ...)
            // TODO getFileContainer UT3
            throw new UnsupportedOperationException("Impossible to find linked container file from ressource "+getInName());
        }
        
        else if(mapConverter.getInputGame().engine == UTGames.UnrealEngine.UE2 || mapConverter.getInputGame().engine == UTGames.UnrealEngine.UE1){
            //return new File(mapConverter.getConfig().getUTxRootFolder(mapConverter.getInputGame()) + File.separator + getRelativePathForUE12Ressource() + File.separator + getInName());
        }

        return null;
    }
    
    private String getRelativePathForUE12Ressource(){
        
        if(type == Type.MUSIC){
            return File.separator + "Music";
        } 
        else if (type == Type.SOUND){
            return File.separator + "Sounds";
        }
        
        else if (type == Type.TEXTURE){
            return File.separator + "Textures";
        }
        
        else if (type == Type.STATICMESH){
            return File.separator + "StaticMeshes";
        }
        
        return null;
    }
    
    
}
