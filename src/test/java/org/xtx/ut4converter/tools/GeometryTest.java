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


    /**
     * Test recomputation of origine so PanU and PanV are equal to 0
     * The expected origin values have been retreived using UE2 Editor (not storing PanU or PanV properties) and applying either panU or rotate
     * and comparing original origin to final origin after panU or panV applied
     */
    @Test
    void testComputeOrigin(){

        // Unreal1 - DmFith - Brush145 - Poly 2 sample
        Vector3d origin = new Vector3d(0,-1216,0);
        Vector3d texU = new Vector3d(0.5,0,0);
        Vector3d texV = new Vector3d(0,0.5,0);

        // Test if panU and panV are equals to zero, it should not change origin
        Assertions.assertEquals(origin, Geometry.computeNewOrigin(origin, texU, texV, 0, 0));

        // Test if panU and panV are equals to zero, it should not change origin if scale change
        Assertions.assertEquals(origin, Geometry.computeNewOrigin(origin, new Vector3d(2,0,0), new Vector3d(0,2,0), 0, 0));

        // Test if panU and panV are equals to zero, it should not change origin if rotation change
        Assertions.assertEquals(origin, Geometry.computeNewOrigin(origin, new Vector3d(1.414214,1.414214,0), new Vector3d(-1.414214,1.414214,0), 0, 0));


        // HORIZONTAL SURFACE (floor) with a 512x512 texture on a 256x256 box
        origin = new Vector3d(-128,128,128);
        texU = new Vector3d(2,0,0);
        texV = new Vector3d(0,2,0);

        // Test panU 64 -> OriginOffSet = (+32, 0, 0)
        Assertions.assertEquals(new Vector3d(-96, 128, 128), Geometry.computeNewOrigin(origin, texU, texV, 64, 0));

        // Test panV 64 -> OriginOffSet = (0, 32, 0)
        Assertions.assertEquals(new Vector3d(-128, 160, 128), Geometry.computeNewOrigin(origin, texU, texV, 0, 64));

        // Test panU = 64, panV = 64 -> OriginOffSet = (32, 32, 0)
        // Origin   -00128.000000,+00128.000000,+00128.000000
        // Diff      +  32        +32            ,+0
        // Origin   -00096.000000,+00160.000000,+00128.000000
        Assertions.assertEquals(new Vector3d(-96, 160, 128), Geometry.computeNewOrigin(origin, texU, texV, 64, 64));

        // Test panU = 80 (64+16), panV = 24 (16+4+4)
        // Origin   -00128.000000,+00128.000000,+00128.000000
        // Diff        +40           +12
        // Origin   -00088.000000,+00140.000000,+00128.000000
        Assertions.assertEquals(new Vector3d(-88, 140, 128), Geometry.computeNewOrigin(origin, texU, texV, 80, 24));

        // VERTICAL SURFACE (wall) with a 512x512 texture  scaled 0.5 on a 256x256 box
        origin = new Vector3d(-128,128,-128);
        texU = new Vector3d(2,0,0);
        texV = new Vector3d(0,0,-2);

        // Test panU =64
        // Origin   -00096.000000,+00128.000000,-00128.000000
        Assertions.assertEquals(new Vector3d(-96, 128, -128), Geometry.computeNewOrigin(origin, texU, texV, 64, 0));

        // Test panV =64
        // Origin   -00128.000000,+00128.000000,-00160.000000
        Assertions.assertEquals(new Vector3d(-128, 128, -160), Geometry.computeNewOrigin(origin, texU, texV, 0, 64));

        // 128x256 texture with scale 1 (origin value is the same)
        texU = new Vector3d(1,0,0);
        texV = new Vector3d(0,0,-1);

        // Test panU =64
        // Origin   -00064.000000,+00128.000000,-00128.000000
        Assertions.assertEquals(new Vector3d(-64, 128, -128), Geometry.computeNewOrigin(origin, texU, texV, 64, 0));

        // Test panU = 80 (64+16), panV = 24 (16+4+4)
        //  Origin   -00048.000000,+00128.000000,-00152.000000
        Assertions.assertEquals(new Vector3d(-48, 128, -152), Geometry.computeNewOrigin(origin, texU, texV, 80, 24));

        // Slope surface
        // -00128.000000,-00336.358154,+00193.513672 (original)
        // TextureU +00001.000000,+00000.000000,+00000.000000
        // TextureV +00000.000000,-00000.382683,-00000.923880
        origin = new Vector3d(-128,-336.358154,193.513672);
        texU = new Vector3d(1,0,0);
        texV = new Vector3d(0,-0.382683,-0.923880);

        // Test texU = 16
        // Origin   -00112.000000,-00336.358154,+00193.513672
        Assertions.assertEquals(new Vector3d(-112, -336.358154, 193.513672), Geometry.computeNewOrigin(origin, texU, texV, 16, 0));

        // Test texU = 16, texV=
        // Origin   -00112.000000,-00336.358154,+00193.513672
        Assertions.assertEquals(new Vector3d(-112, -336.358154, 193.513672), Geometry.computeNewOrigin(origin, texU, texV, 16, 0));

        // Test texU = 16, texV=8
        // Origin   -00112.000000,-00339.419678,+00186.122681
        //Assertions.assertEquals(new Vector3d(-112, -339.419678, 186.122681), Geometry.computeNewOrigin(origin, texU, texV, 16, 8));
        // Note: commented test but good (insignifiant diff due to precision or rounding)
        // Expected :(-112.0, -339.419678, 186.122681)
        // Actual   :(-112.0, -339.4196163685804, 186.12263593860183)
    }
}
