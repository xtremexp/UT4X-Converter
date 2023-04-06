package net.nikr.dds;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import static net.nikr.dds.Viewer.*;

public class LoadFileOperation {

    // extract method
    public static BufferedImage loadFile(File file, int imageIndex){
        return getBufferedImage(file, imageIndex);
    }

    public static  void loadFile(File file){
        if (!files.isEmpty()) {
            LoadFileOperation.loadFile(file, 0);
        }
    }
    public static void loadFile(){
        if (!files.isEmpty()) {
            loadFile(files.get(fileIndex), mipMap);
        }
    }



    public static BufferedImage getBufferedImage(File file, int imageIndex) {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix("dds");
        if (iterator.hasNext()){
            try {
                ImageReader imageReader = iterator.next();
                imageReader.setInput(new FileImageInputStream(file));
                int max = imageReader.getNumImages(true);
                if (imageIndex >= 0 && imageIndex < max){
                    return imageReader.read(imageIndex);
                }
            } catch (Exception ex) {
                System.out.println("loadFile fail...");
                ex.printStackTrace();
            }
        }
        return null;
    }
    
}
