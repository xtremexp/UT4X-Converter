/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

import java.util.LinkedList;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.xtx.ut4converter.geom.Vertex;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DPolygon;

/**
 *
 * @author XtremeXp
 */
public class FBXModelObject extends FBXObject {

    final short MODEL_VERSION = 232;
    final short GEOMETRY_VERSION = 124;
    public static DecimalFormat df = new DecimalFormat("#0.000000", new DecimalFormatSymbols(Locale.US));
    
    short geometryVersion;
    
    LinkedList<FBXLayerElement> layerElements;
    
    LinkedList<Double> vertices;
    LinkedList<Double> normals;
    
    /**
     * Vertex index, last one of poly is always negated
     */
    LinkedList<Integer> polygonVertexIndices;
    
    LinkedList<Float> uvs;
    
    /**
     * Reference to brush
     */
    T3DBrush brush;
    
    public FBXModelObject(T3DBrush brush) {
        super(FBXObjectType.Model);
        
        version = MODEL_VERSION;
        geometryVersion = GEOMETRY_VERSION;
        this.brush = brush;
        this.subName = "Mesh";
        initialise();
    }
    
    private void initialise(){
        
        layerElements = new LinkedList<>();
        
        if(brush.getName() != null){
            this.name = brush.getName();
        } else {
            this.name = "SomeSM";
        }
        
        vertices = new LinkedList<>();
        normals = new LinkedList<>();
        polygonVertexIndices = new LinkedList<>();
        uvs = new LinkedList<>();
        
        load(brush.getPolyList());
        FBXLayerElementNormal layerNormal = new FBXLayerElementNormal(normals, FBXLayerElementNormal.MappingInformationType.ByPolygon);
        layerElements.add(layerNormal);
        layerElements.add(new FBXLayerElementMaterial());
    }

    private void load(LinkedList<T3DPolygon> polygons){
        
        for(T3DPolygon p : polygons){
            
            for(int idx = 0; idx < p.vertices.size(); idx ++){
                
                Vertex v = p.vertices.get(idx);
                
                vertices.add(v.getX());
                vertices.add(v.getY());
                vertices.add(v.getZ());

                uvs.add(v.getU());
                uvs.add(v.getV());
                
                // Last one always negates the index
                if( idx == p.vertices.size() - 1){
                    polygonVertexIndices.add(v.getBrushIdx() * -1);
                }
                
                else {
                    polygonVertexIndices.add(v.getBrushIdx());
                }
                
            }
            
            normals.add(p.normal.x);
            normals.add(p.normal.y);
            normals.add(p.normal.z);
        }
    }

    @Override
    public void writeFBX(StringBuilder sb) {
        
        sb.append("\tModel: \"Model::").append(this.name).append("\", \"").append(subName).append("\" {\n");
        sb.append("\t\tVersion: ").append(version).append("\n");
        sb.append("\t\tVertices: ");
        
        for(Double v : vertices){
            sb.append(df.format(v)).append(",");
        }
        
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        
        sb.append("\t\tPolygonVertexIndex: ");
        
        for(Integer idx : polygonVertexIndices){
            sb.append(idx).append(",");
        }
        
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        
        sb.append("\t\tUV: ");
        
        for(Float uv : uvs){
            sb.append(df.format(uv)).append(",");
        }
        
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        
        
        sb.append("\t\tGeometryVersion: ").append(geometryVersion).append("\n");
        
        for(FBXLayerElement layerElement : layerElements){
            layerElement.writeFBX(sb);
            sb.append("\n");
        }
        
        sb.append("\t\tLayer: 0 {\n");
        sb.append("\t\tVersion: 100 \n");
        
        for(FBXLayerElement layerElement : layerElements){
            
            sb.append("\t\tLayerElement:  {\n");
            sb.append("\t\t\tType: \"").append(layerElement.type.name()).append("\"\n");
            sb.append("\t\t\tTypedIndex: 0\n");
            sb.append("\t\t}\n");
        }
        
        sb.append("\t}\n\n");
        
        sb.append("}\n");
    }

}
