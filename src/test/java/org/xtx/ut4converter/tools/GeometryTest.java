package org.xtx.ut4converter.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.t3d.T3DUtils;

import javax.vecmath.Vector3d;

public class GeometryTest {

    @Test
    void testUE123ToUE4Rotation(){

        final Vector3d ut99Rotation = new Vector3d(65536d,16384d, 0d);
        Assertions.assertEquals(new Vector3d(360d, 90d, 0d), Geometry.UE123ToUE4Rotation(ut99Rotation));
    }


    @Test
    void testGetRotator(){

        // testing parser
        final Rotator r1 = T3DUtils.parseRotator("BaseRot=(Pitch=32768,Yaw=-16384,Roll=65536)");
        Assertions.assertEquals(32768, r1.getPitch(), 0.01d);
        Assertions.assertEquals(-16384, r1.getYaw(), 0.01d);
        Assertions.assertEquals(65536, r1.getRoll(), 0.01d);
        // test default space is UE123 space (not UE4 space)
        Assertions.assertFalse(r1.isUE4Space());

        // test switching from UE123 space to UE4 space (meaning changing range and rotator value)
        r1.switchSpace(true);
        Assertions.assertEquals(180, r1.getPitch(), 0.01d);
        Assertions.assertEquals(-90, r1.getYaw(), 0.01d);
        Assertions.assertEquals(360, r1.getRoll(), 0.01d);
        Assertions.assertTrue(r1.isUE4Space());

        final Rotator r2 = T3DUtils.parseRotator("BaseRot=(Yaw=-16384,Roll=65536)");
        Assertions.assertEquals(0, r2.getPitch(), 0.01d);
        Assertions.assertEquals(-16384, r2.getYaw(), 0.01d);
        Assertions.assertEquals(65536, r2.getRoll(), 0.01d);

        final Rotator r3 = T3DUtils.parseRotator("BaseRot=(Pitch=32768,Yaw=-16384)");
        Assertions.assertEquals(32768, r3.getPitch(), 0.01d);
        Assertions.assertEquals(-16384, r3.getYaw(), 0.01d);
        Assertions.assertEquals(0, r3.getRoll(), 0.01d);

        final Rotator r4 = T3DUtils.parseRotator("BaseRot=(Pitch=32768,Roll=65536)");
        Assertions.assertEquals(32768, r4.getPitch(), 0.01d);
        Assertions.assertEquals(0, r4.getYaw(), 0.01d);
        Assertions.assertEquals(65536, r4.getRoll(), 0.01d);

        // some real test from IsvKran32 Unreal 1 map
        final Rotator r = T3DUtils.parseRotator("=(Roll=-19456)");
        System.out.println(r);
        r.switchSpace(true);
        System.out.println(r);
        System.out.println(r.toUEString());
    }
}
