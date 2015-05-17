/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

/**
 *
 * @author XtremeXp
 */
public abstract class FBXObject implements FBXWriter {
    
    FBXObjectType objectType;
    String name;
    short version;

    public FBXObject(FBXObjectType objectType) {
        this.objectType = objectType;
    }

}
