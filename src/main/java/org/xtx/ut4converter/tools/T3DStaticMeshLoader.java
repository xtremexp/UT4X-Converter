/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DPolygon;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.T3DStaticMesh;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Converts t3d static mesh files (generated with "ucc.exe batchexport staticmesh" command)
 * to brush.
 * 
 * Example of some .t3d staticmesh file
 * "Begin StaticMesh Name=phobosradar
        Version=2.000000 BoundingBox.Min.X=-31.097290 BoundingBox.Min.Y=-53.882950 BoundingBox.Min.Z=-53.234379 BoundingBox.Max.X=39.252708 BoundingBox.Max.Y=55.240028 BoundingBox.Max.Z=53.234379
        Begin Triangle
            Texture Epic_Phoboswing.Shaders.cf_RadarShader
            SmoothingMask 1
            Vertex 0 -7.909100 -53.882950 -17.342461 0.006540 0.200720
            Vertex 1 -5.630610 -50.535172 -0.801760 0.042930 0.098930
            Vertex 2 1.082450 -51.566898 -5.899310 0.114000 0.132040
        End Triangle
        ...
 * "
 * @author XtremeXp
 */
public class T3DStaticMeshLoader {
    
    public static enum ExportFormat {
        ASE, // Ascii Scene (can be imported by UT3 not UT4)
        FBX, // Flimbox format (can be imported by UT4 not UT3)
        T3D // Unreal Engine 2 internal format (this one)
    }
    
    File t3dStaticMeshFile;
    MapConverter mapConverter;
    LinkedList<T3DPolygon> polygons;
    ExportFormat exportFormat;
    
    /**
     *
     * @param mapConverter Map converter
     * @param t3dStaticMeshFile T3d static mesh file
     */
    public T3DStaticMeshLoader(MapConverter mapConverter, File t3dStaticMeshFile){
        this.t3dStaticMeshFile = t3dStaticMeshFile;
        this.polygons = new LinkedList<>();
        
        if(mapConverter.toUT4()){
            exportFormat = ExportFormat.FBX;
        }
        
        else if(mapConverter.toUE3()){
            exportFormat = ExportFormat.ASE;
        }
    }
    
    /**
     * Converts the t3d static mesh file to t3d brush.
     * This might be usefull to later convert to .fbx (flimbox) files
     * @return Brush
     * @throws IOException 
     */
    public T3DBrush convertToBrush() throws IOException{
        
        T3DBrush brush = new T3DBrush(mapConverter, null);
        brush.initialise();
        
        loadPolygons();
        brush.setPolyList(polygons);
        
        return brush;
    }
    
    /**
     * Converts .t3d staticmesh file into a t3d brush
     * then convert it into a .fbx file that can be imported into unreal editor 4
     * @return
     * @throws IOException 
     */
    public T3DStaticMesh convertToStaticMesh() throws IOException{
        
        T3DBrush brush = convertToBrush();
        
        // TODO brush to .fbx converter
        if(exportFormat == ExportFormat.FBX){
            
        }
        
        // TODO put "refactored" ut3 converter code here
        else if(exportFormat == ExportFormat.ASE){
            
        }
        
        return null;
    }
    
    /**
     * Loads polygon data from t3d line
     * @return List of polygons
     */
    private void loadPolygons() throws FileNotFoundException, IOException {
        
        
        try (FileReader fr = new FileReader(t3dStaticMeshFile); BufferedReader bfr = new BufferedReader(fr)){
            
            String line;
            
            while( (line = bfr.readLine()) != null){
                
                T3DPolygon p = null;
                line = line.trim();
                
                if(line.startsWith("Begin Triangle")){
                    p = new T3DPolygon();
                    p.setMapConverter(mapConverter);
                }
                
                // E.G: "Texture Epic_Phoboswing.Shaders.cf_RadarShader"
                else if(line.startsWith("Texture")){
                    
                    String texture = line.split("Texture ")[1];
                    
                    UPackageRessource textureRessource = mapConverter.getUPackageRessource(texture,  T3DRessource.Type.TEXTURE);
                    
                    if(p != null){
                        p.setTexture(textureRessource);
                    }
                }
                
                // TODO check what is smoothing mask
                else if(line.startsWith("SmoothingMask")){
                    
                }
                
                // e.g: "Vertex 0 -2.313340 -48.702381 16.483009 -0.004290 0.000590"
                else if(line.startsWith("Vertex")){
                    Vector3d v = new Vector3d();
                    String s[] = line.split("\\ ");
                    
                    v.x = Float.valueOf(s[2]);
                    v.y = Float.valueOf(s[3]);
                    v.z = Float.valueOf(s[4]);
                    
                    // TODO check other values seems related to UV ...
                    
                    if(p != null){
                        p.addVertex(v);
                    }
                }
                
                else if(line.startsWith("End Triangle")){
                    
                    polygons.add(p);
                }
            }
        }
    }

}
