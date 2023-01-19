package org.xtx.ut4converter.tools.t3dmesh;

import java.util.LinkedList;
import java.util.List;

public class Triangle {

    private String texture;

    private int smoothingMask;

    private final List<Vertex> vertices;


    public Triangle(){
        this.vertices = new LinkedList<>();
    }

    /**
     * Texture BarrensArchitecture.Borders.bdr01BA
     SmoothingMask 354715648
     Vertex 0 144.000000 80.000000 4.000000 -0.296875 0.000000
     Vertex 1 -144.000000 80.000000 4.000000 -0.015625 0.000000
     Vertex 2 -160.000000 96.000000 8.000000 0.000000 0.003906
     * @param line Vertex t3d line to parse
     */
    public void parseLine(final String line){

        if(line.startsWith("Texture")){
            this.texture = line.split(" ")[1];
        } else if(line.startsWith("Vertex")){
            final Vertex vertex = new Vertex();
            vertex.parseLine(line);
            this.vertices.add(vertex);
        } else if(line.startsWith("SmoothingMask")){
            this.smoothingMask = Integer.parseInt(line.split(" ")[1]);
        }
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public int getSmoothingMask() {
        return smoothingMask;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
