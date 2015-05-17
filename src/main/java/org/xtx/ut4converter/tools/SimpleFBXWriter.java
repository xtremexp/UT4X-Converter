/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DPolygon;
import org.xtx.ut4converter.tools.fbx.FBXDefinitions;
import org.xtx.ut4converter.tools.fbx.FBXHeaderExtension;
import org.xtx.ut4converter.tools.fbx.FBXObject;

/**
 * Simple utility class to write
 * brushes from UT to ASCII .fbx staticmesh files
 * @author XtremeXp
 */
public class SimpleFBXWriter {
    
    static final String CREATOR = MainApp.PROGRAM_NAME + "-" + MainApp.VERSION;
    
    
    FBXHeaderExtension headerExtension;
    FBXDefinitions definitions;
    List<FBXObject> objects;
    
    /**
     * Output fbx file
     */
    File fbxFile;
    List<T3DPolygon> polygons;
    
    /**
     * 
     * @param polygons 
     */
    public SimpleFBXWriter(List<T3DPolygon> polygons){
        this.polygons = polygons;
        
        initialise();
    }
    
    private void initialise(){
        headerExtension = FBXHeaderExtension.getInstance(CREATOR);
        
        definitions = FBXDefinitions.getInstance(objects);
    }
    

    
    public void write(File fbxFile) throws IOException{
        
        StringBuilder sb = new StringBuilder("");
        
        writeHeader(sb);
        writeBody(sb);
        writeFooter(sb);
        
        try (FileWriter fw = new FileWriter(fbxFile); BufferedWriter bwr = new BufferedWriter(fw);){
            bwr.write(sb.toString());
        }
    }
    
    
    private void writeHeader(StringBuilder sb){
        
        sb.append("; FBX ").append(headerExtension.FBXVersion).append(" project file\n");
        sb.append("; Created by ").append(CREATOR).append("\n");
        sb.append("; ----------------------------------------------------\n");
        
        headerExtension.writeFBX(sb);
        
        sb.append("; Object definitions\n");
        sb.append("; ----------------------------------------------------\n");
        
        definitions.writeFBX(sb);
    }
    
    private void writeBody(StringBuilder sb){
        
        sb.append("; Object properties\n");
        sb.append(";------------------------------------------------------------------\n\n");
        
        sb.append("Objects:  {\n\n");
        
        for(FBXObject object : objects){
            object.writeFBX(sb);
            sb.append("\n");
        }
        
        sb.append("} \n");
    }
    
    private void writeFooter(StringBuilder sb){
        
    }
    
    public static void test(){
        
        File t3dSmFile = new File("Y:\\UT4Converter\\Converted\\DM-Phobos2\\StaticMesh\\epic_phobos_Meshes_phobosradar.t3d");
        File fbxSmFile = new File("C:\\Temp\\epic_phobos_Meshes_phobosradar.fbx");
        fbxSmFile.delete();
        
        MapConverter mc = new MapConverter(UTGames.UTGame.UT2003, UTGames.UTGame.UT4, new File("fakemap.t3d"), 1d);

        try {
            // load poly data from .t3d sm file
            T3DStaticMeshFileLoader smLoader = new T3DStaticMeshFileLoader(mc, t3dSmFile);
            SimpleFBXWriter fbwWriter = new SimpleFBXWriter(smLoader.brush.getPolyList());
            fbwWriter.write(fbxSmFile);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
