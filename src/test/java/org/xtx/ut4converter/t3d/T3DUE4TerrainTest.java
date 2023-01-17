package org.xtx.ut4converter.t3d;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import javax.vecmath.Point2d;


public class T3DUE4TerrainTest {


    /**
     * Give a global index (starting at 0), test computation of coordinates
     * (assuming X axis is from left to right
     * and y axis is from top to bottom)
     */
    @Disabled("Dependant of .exe files")
    void testGetCoordinatesForIndexInSquareSize(){

        // index 28 means it's 29th value (0,1, .... 28)
        // -- X ->
        // - - - - - - - - - - (9)
        // - - - - - - - - - - (19)
        // - - - - - - - - * - (29)
        // - - - - - - - - - - (39)
        // - - - - - - - - - - (49)
        // |
        // Y
        // |
        // V

        final Point2d result = T3DUE4Terrain.getCoordinatesForIndexInSquareSize(28, 10, 5);

        // should return (x=8, y = 2)
        Assertions.assertEquals(8d, result.getX(), 0.01d);
        Assertions.assertEquals(2d, result.getY(), 0.01d);
    }

}
