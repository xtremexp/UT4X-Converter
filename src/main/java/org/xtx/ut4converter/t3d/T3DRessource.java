/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

/**
 * 
 * @author XtremeXp
 * @deprecated  Delete after move enum "Type"
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
    
}
