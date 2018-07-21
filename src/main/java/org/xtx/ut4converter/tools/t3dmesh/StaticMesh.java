package org.xtx.ut4converter.tools.t3dmesh;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class StaticMesh {

    private final File t3dFile;

    private final List<Triangle> triangles;

    public StaticMesh(final File t3dFile) {
        this.t3dFile = t3dFile;
        this.triangles = new LinkedList<>();

        read();
    }

    private void read(){

        if(this.t3dFile == null || !this.t3dFile.exists()){
            return;
        }

        try(final BufferedReader bfr = new BufferedReader(new FileReader(this.t3dFile))){

            String line;
            Triangle triangle = null;

            while((line = bfr.readLine()) != null){
                line = line.trim();

                if(line.startsWith("Begin Triangle")){
                    triangle = new Triangle();
                } else if(triangle != null && !line.startsWith("Begin StaticMesh") && !line.startsWith("End StaticMesh") && !line.startsWith("End Triangle")) {
                    triangle.parseLine(line);
                } else if(line.startsWith("End Triangle")){
                    triangles.add(triangle);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getT3dFile() {
        return t3dFile;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public static void main(final String args[]){
        final File t3dFile = new File("E:\\TEMP\\UT4X-Converter\\Converted\\DM-OSR-Cheops\\StaticMesh\\DM-OSR-Cheops_Floor_lift.t3d");
        final StaticMesh staticMesh = new StaticMesh(t3dFile);
        staticMesh.getTriangles();
    }
}
