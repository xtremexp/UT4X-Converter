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
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Optional;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.tools.fbx.FBXDefinitions;
import org.xtx.ut4converter.tools.fbx.FBXHeaderExtension;
import org.xtx.ut4converter.tools.fbx.FBXModelObject;
import org.xtx.ut4converter.tools.fbx.FBXObject;
import org.xtx.ut4converter.tools.fbx.FBXObjectType;

/**
 * Simple utility class to write
 * brushes from UT to ASCII .fbx staticmesh files
 * @author XtremeXp
 */
public class SimpleFBXWriter {
    
    static final String CREATOR = MainApp.PROGRAM_NAME + "-" + MainApp.VERSION;
    
    
    
    FBXHeaderExtension headerExtension;
    FBXDefinitions definitions;
    LinkedList<FBXObject> objects;
    
    /**
     * Output fbx file
     */
    File fbxFile;
    T3DBrush brush;
    
    /**
     * 
     * @param brush
     */
    public SimpleFBXWriter(T3DBrush brush){
        this.brush = brush;
        
        initialise();
    }
    
    private void initialise(){
        
        brush.calcVerticeIndices(); // need compute vertex index (needed for fbx)
        
        objects = new LinkedList<>();
        headerExtension = FBXHeaderExtension.getInstance(CREATOR);
        objects.add(new FBXModelObject(brush));
        
        definitions = FBXDefinitions.getInstance(objects);
        
        // TODO add FBXObjectMaterial
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
        
        sb.append("; FBX 6.1.0 project file\n");
        sb.append("; Created by ").append(CREATOR).append("\n");
        sb.append("; ----------------------------------------------------\n");
        sb.append("\n");
        
        headerExtension.writeFBX(sb);

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");
        sb.append("CreationTime: \"").append(sdf.format(headerExtension.creationTimeStamp.time)).append("\"\n");
        sb.append("Creator: \"").append(CREATOR).append("\"\n");
        sb.append("\n");
        
        sb.append("; Object definitions\n");
        sb.append("; ----------------------------------------------------\n");
        
        definitions.writeFBX(sb);
        sb.append("\n");

    }
    
    private FBXObject getFBXObjectByType(FBXObjectType objectType){
        
        Optional<FBXObject> object = objects.stream().filter(w -> w.getObjectType() == objectType).findFirst();

        return object.isPresent()? object.get() : null;
    }
    
    private void writeBody(StringBuilder sb){
        
        sb.append("; Object properties\n");
        sb.append(";-----------------------------------------------------\n\n");
        
        if(objects != null && !objects.isEmpty()){
            sb.append("Objects:  {\n\n");

            for(FBXObject object : objects){
                object.writeFBX(sb);
                sb.append("\n");
            }

            sb.append("} \n");
        }
        
        sb.append("\n");
    }
    
    private void writeFooter(StringBuilder sb){
        
        sb.append("; Object relations\n");
        sb.append(";-----------------------------------------------------\n\n");
        
        
        sb.append("Relations:  {\n");
	
        for(FBXObject object : objects){
            sb.append("\t").append(object.getObjectType().name()).append(" \"").append(object.getObjectType().name()).append("::").append(object.getName()).append("\", \"").append(object.getSubName()).append("\" {\n");
            sb.append("\t}\n");
        }
        
        sb.append("}\n");
        sb.append("\n");
        
        sb.append("; Object connections\n");
        sb.append("----------------------------------------------------\n\n");

        FBXObject model = getFBXObjectByType(FBXObjectType.Model);
        FBXObject material = getFBXObjectByType(FBXObjectType.Material);
        
        sb.append("Connections:  {\n");
        
        sb.append("\tConnect: \"OO\", \"Model::").append(model.getName()).append("\", \"Model::Scene\"\n");
        
        if(material != null){
            sb.append("\tConnect: \"OO\", \"Material::").append(material).append("\", \"Model::MonCube\"\n");
        }
        
         sb.append("}\n");
    }
    
    public static void test(String args[]){
        
        //File t3dSmFile = new File("Y:\\UT4Converter\\Converted\\DM-Phobos2\\StaticMesh\\epic_phobos_Meshes_phobosradar");
        File fbxSmFile = new File("C:\\Temp\\epic_phobos_Meshes_phobosradar.g");
        fbxSmFile.delete();
        
        MapConverter mc = new MapConverter(UTGames.UTGame.UT2003, UTGames.UTGame.UT4, new File("fakemap.t3d"), 1d);

        try {
            // load poly data from .t3d sm file
            for(File t3dSmFile : new File("Y:\\UT4Converter\\Converted\\DM-Phobos2\\StaticMesh\\").listFiles()){
                
                if(t3dSmFile.getName().endsWith(".fbx")){
                    t3dSmFile.delete();
                    continue;
                }
                
                T3DStaticMeshFileLoader smLoader = new T3DStaticMeshFileLoader(mc, t3dSmFile);
                SimpleFBXWriter fbwWriter = new SimpleFBXWriter(smLoader.brush);
                File fbx = new File(t3dSmFile.getAbsolutePath().split("\\.")[0]+".fbx");
                fbx.delete();
                System.out.println("Writting "+fbx);
                fbwWriter.write(fbx);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
