/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

import java.util.LinkedList;

/**
 *
 * @author XtremeXp
 */
public class FBXLayerElementNormal extends FBXLayerElement {

    final short DEFAULT_VERSION = 101;
    
    MappingInformationType mappingInformationType;
    ReferenceInformationType referenceInformationType;
    LinkedList<Double> normals;
    
    public static enum MappingInformationType {
        ByPolygon,
        ByPolygonVertex,
        ByVertex,
        ByEdge,
        AllSame
    }
    

    public FBXLayerElementNormal(LinkedList<Double> normals, MappingInformationType mappingInformationType) {
        
        super(Type.LayerElementNormal);
        
        this.version = DEFAULT_VERSION;
        this.normals = normals;
        this.mappingInformationType = mappingInformationType;
        referenceInformationType = ReferenceInformationType.Direct;
    }
    
    
    

    @Override
    public void writeFBX(StringBuilder sb) {
        
        sb.append("\t\tLayerElementNormal: ").append(index).append(" {\n");
        
        sb.append("\t\t\tVersion: ").append(version).append("\n");
        sb.append("\t\t\tName: \"").append(name).append("\"\n");
        sb.append("\t\t\tMappingInformationType: \"").append(mappingInformationType.name()).append("\"\n");
        sb.append("\t\t\tReferenceInformationType: \"").append(referenceInformationType.name()).append("\"\n");
        
        sb.append("\t\t\tNormals: ");
        

        for(Double normal : normals){
            sb.append(df.format(normal)).append(",");
        }
        
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        
        sb.append("\t\t}");
    }
}
