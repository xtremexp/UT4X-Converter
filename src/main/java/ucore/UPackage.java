/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucore;

import java.io.File;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DRessource.Type;

/**
 * Very basic implementation of unreal package
 * @author XtremeXp
 */
public class UPackage {
    
    private final UTGames.UnrealEngine engine;
    
    File file;
    
    /**
     * Type of package (level, sound, textures, ...)
     */
    Type type;

    public UPackage(UTGames.UnrealEngine engine, File file, Type type) {
        this.engine = engine;
        this.file = file;
        this.type = type;
    }
    
    
}
