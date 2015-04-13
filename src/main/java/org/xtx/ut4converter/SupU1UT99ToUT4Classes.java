/*'
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import java.util.HashMap;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DLight;
import org.xtx.ut4converter.t3d.T3DMatch.Match;
import org.xtx.ut4converter.t3d.T3DMover;
import org.xtx.ut4converter.t3d.T3DPlayerStart;
import org.xtx.ut4converter.t3d.T3DSound;
        
/**
 * FXML Controller class
 * @author XtremeXp
 */
public class SupU1UT99ToUT4Classes extends SupportedClasses {
    
    MapConverter mapConv;
    
    /**
     *
     * @param mc
     */
    public SupU1UT99ToUT4Classes(MapConverter mc) {
        super();
        this.mapConv = mc;
        
        putUtClass("Brush", T3DBrush.class);
        putUtClass("Mover", T3DMover.class);
        putUtClass("PlayerStart", T3DPlayerStart.class);
        
        putUtClass("Light", T3DLight.class);
        putUtClass("AmbientSound", T3DSound.class);
        
        //addPickups();
        
        addMatches(mc);
    }
    
    private void addMatches(MapConverter mc){
        HashMap<String, Match> hm = mc.getActorClassMatch();
        
        for(String c : hm.keySet()){
            putUtClass(c, hm.get(c).t3dClass);
        }
    }

    /**
     *
     */
    public SupU1UT99ToUT4Classes() {
        super();
    }
    
    /**
     * For testing purposes only.
     * Converts only one actor so it's easier
     * to import it in map being converted
     * @param actor
     * @param t3dClass 
     */
    public void setConvertOnly(String actor, Class t3dClass){
        classToUtActor.clear();
        classToUtActor.put(actor.toLowerCase(), t3dClass);
    }

}
