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
public class FBXObjectMaterial extends FBXObject {

    enum ShadingModel {
        lambert
    }
    
    public FBXObjectMaterial(String materialName) {
        super(FBXObjectType.Material);
        this.name = materialName;
    }

    @Override
    public void writeFBX(StringBuilder sb) {
        sb.append("\tMaterial: \"Material::").append(name).append("\", \"\" {\n");
        
        sb.append("\t\tVersion: 102\n");
        sb.append("\t\tShadingModel: ").append(ShadingModel.lambert.name()).append("\n");
        sb.append("\t\tMultiLayer: 0\n");
        
        sb.append("}\n");
    }

}
