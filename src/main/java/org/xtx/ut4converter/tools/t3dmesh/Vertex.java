package org.xtx.ut4converter.tools.t3dmesh;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 *
 */
public class Vertex {

    private Vector3d xyz;

    private Vector2d uv;


    /**
     * Vertex 2 -160.000000 96.000000 8.000000 0.000000 0.003906
     * @param line T3d line to parse
     */
    public void parseLine(final String line){
        final String[] s = line.split(" ");

        this.xyz = new Vector3d(Double.parseDouble(s[2]), Double.parseDouble(s[3]), Double.parseDouble(s[4]));
        this.uv = new Vector2d(Double.parseDouble(s[5]), Double.parseDouble(s[6]));
    }

    public Vector3d getXyz() {
        return xyz;
    }

    public Vector2d getUv() {
        return uv;
    }
}
