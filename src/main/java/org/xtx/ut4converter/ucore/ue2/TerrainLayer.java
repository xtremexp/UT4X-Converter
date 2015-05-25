/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * @author XtremeXp
 */
public class TerrainLayer implements T3D {

    
    
    enum TextureMapAxis {
        TEXMAPAXIS_XY,
        TEXMAPAXIS_XS,
        TEXMAPAXIS_YZ,
    }
    
    UPackageRessource alphaMapTexture;
    
    /**
     * List of alpha map values if alphaMapTexture set
     * and values read from texture file
     */
    List<Integer> alphaMap;
    
    Float kFriction;
    Float kRestitution;
    Rotator layerRotation;
    UPackageRessource texture;
    TextureMapAxis textureMapAxis;
    Float textureRotation;
    Float uPan, vPan;
    Float uScale, vScale;
    
    MapConverter mapConverter;

    public TerrainLayer(MapConverter mapConverter){
        this.mapConverter = mapConverter;
        initialise();
    }

    private void initialise(){
        textureMapAxis = TextureMapAxis.TEXMAPAXIS_XY;
        alphaMap = new ArrayList<>();
    }
    
    @Override
    public void convert() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Double newScale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean analyseT3DData(String line) {
        
        if(line.contains("AlphaMap=")){
            alphaMapTexture = mapConverter.getUPackageRessource(line.split("AlphaMap=")[1].split("\\'")[1], T3DRessource.Type.TEXTURE);
        }
        
        else if(line.contains("Texture=")){
            texture = mapConverter.getUPackageRessource(line.split("Texture=")[1].split("\\'")[1], T3DRessource.Type.TEXTURE);
        }
        
        else if(line.contains("UScale")){
            uScale = Float.valueOf(line.split("UScale=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("VScale")){
            vScale = Float.valueOf(line.split("VScale=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("UPan")){
            uPan = Float.valueOf(line.split("UPan=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("VPan")){
            vPan = Float.valueOf(line.split("VPan=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("TextureRotation")){
            textureRotation = Float.valueOf(line.split("TextureRotation=")[1].split("\\,")[0]);
        }
        
        else {
            return false;
        }
        
        return true;
    }
    
    public void load() throws IOException{
        
        if(alphaMapTexture != null){
            loadAlphaTextureMap();
        }
        
    }
    
    /**
     * Extract alpha texture map
     * and load values.
     * Code refactored from UT3 converter
     */
    private void loadAlphaTextureMap() throws IOException{
        
        // Export heightmap texture to .tga
        UCCExporter uccExporter = new UCCExporter(mapConverter);
        uccExporter.setForcedUccOption(UCCExporter.UccOptions.TEXTURE_TGA);
        File exportFolder = new File(mapConverter.getTempExportFolder() + File.separator + "TerrainTga" + alphaMapTexture.getUnrealPackage().getName() + File.separator);
        uccExporter.setForcedExportFolder(exportFolder);
        
        alphaMapTexture.export(uccExporter);
        
        Color color;
        int value;
        final int DEFAULT_ALPHA = 128;
        
        BufferedImage img = ImageIO.read(alphaMapTexture.getExportedFile());
        
        for(int y= (img.getWidth()-1); y >= 0; y--)
        {
            for(int x=0; x < img.getWidth(); x++)
            {
                value = img.getRGB(x, y);
                color = new Color(value,true);
                value = color.getAlpha();
                alphaMap.add(value);

                if(x == (img.getWidth()-1) ){
                    alphaMap.add(DEFAULT_ALPHA);
                }
            }
        }
        

        for(int x=0; x < (img.getWidth() + 1); x++)
        {
            alphaMap.add(DEFAULT_ALPHA);
        }
        
        System.out.println(alphaMap.size());
    }
    
    
    
}
