/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Tells which UT actor classes will be converted
 * @author XtremeXp
 */
public class SupportedClasses {
    
    
    /**
     * If in t3d
     */
    protected HashMap<String, Class> classToUtActor = new HashMap();


    /**
     * Actors that are no longer needed for output game.
     * E.G: PathNodes in Unreal Engine 4 which no longer exists (using some navigation volume
     * to border the "navigation area")
     * Useful to avoid some spammy log message 
     * and create notes in editor for unconverted actors info
     */
    protected List<String> uneededActors = new ArrayList<>();

    /**
     *
     */
    public SupportedClasses() {
    }



    /**
     *
     * @param utxclass
     */
    public void addClass(String utxclass)
    {
        classToUtActor.put(utxclass.toLowerCase(), null);
    }
    
    /**
     *
     * @param className
     * @param utActorClass
     */
    public void putUtClass(String className, Class utActorClass){
        if(className == null){
            return;
        }
        classToUtActor.put(className.toLowerCase(), utActorClass);
    }



    /**
     * Tells 
     * @param utClassName
     * @return 
     */
    public boolean canBeConverted(String utClassName){
        return classToUtActor.containsKey(utClassName.toLowerCase());
    }
    
    
    /**
     * Get UT Actor class from t3d class name.
     * Might return null if not special convert class
     * @param utClassName
     * @return 
     */
    public Class getConvertActorClass(String utClassName){
        return classToUtActor.get(utClassName.toLowerCase());
    }
    
    /**
     * 
     * @param utClassName
     * @return 
     */
    public boolean noNotifyUnconverted(String utClassName){
        return uneededActors.contains(utClassName);
    }

}
