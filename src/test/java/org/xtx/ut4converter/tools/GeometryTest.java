package org.xtx.ut4converter.tools;

import org.junit.Assert;
import org.junit.Test;

import javax.vecmath.Vector3d;

public class GeometryTest {

    @Test
    public void testUE123ToUE4Rotation(){

        final Vector3d ut99Rotation = new Vector3d(65536d,16384d, 0d);
        Assert.assertEquals(new Vector3d(360d, 90d, 0d), Geometry.UE123ToUE4Rotation(ut99Rotation));
    }

}