/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * StaticMesh movers.
 * Mover class for Unreal Engine  >= 2
 * Unreal Engine 1 is using mover brush (see T3DMover class)
 * @author XtremeXp
 */
public class T3DMoverSM extends T3DStaticMesh  {

    MoverProperties moverProperties;
    
    public T3DMoverSM(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
        moverProperties = new MoverProperties(this);
    }


    @Override
    public boolean analyseT3DData(String line) {
        
        if(moverProperties.analyseT3DData(line, mapConverter)) {


        } else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    @Override
    public void scale(Double newScale){
        
        moverProperties.scale(newScale);
        
        super.scale(newScale);
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        
        moverProperties.toString(sbf);
        return sbf.toString();
    }
    
    @Override
    public void convert(){
        
        if(mapConverter.convertSounds){
            moverProperties.convert();
        }
        
        super.convert();
    }
    
}
