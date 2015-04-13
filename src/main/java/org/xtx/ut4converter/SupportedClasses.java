/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import java.util.HashMap;

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

}
