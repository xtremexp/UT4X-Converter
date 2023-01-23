package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DZoneInfoTest {


    @Test
    void testZoneInfoConversionUT2004ToUT3() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT3);

        final T3DZoneInfo ut2004ZoneInfo = (T3DZoneInfo) T3DTestUtils.parseFromT3d(mc, "ZoneInfo", T3DZoneInfo.class, Objects.requireNonNull(T3DZoneInfoTest.class.getResource("/t3d/ue2/UT2004-ZoneInfo.t3d")).getPath());
        System.out.println(ut2004ZoneInfo.convertScaleAndToT3D(2d));
    }

    @Test
    void testZoneInfoConversionUT2004ToUT4() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);

        final T3DZoneInfo zi = (T3DZoneInfo) T3DTestUtils.parseFromT3d(mc, "ZoneInfo", T3DZoneInfo.class, Objects.requireNonNull(T3DZoneInfoTest.class.getResource("/t3d/ue2/UT2004-ZoneInfo.t3d")).getPath());
        Assertions.assertEquals(15000, zi.distanceFogEnd);
        Assertions.assertEquals(0, zi.distanceFogEndMin);
        Assertions.assertEquals(80, zi.distanceFogColor.R);
        Assertions.assertEquals(60, zi.distanceFogColor.G);
        Assertions.assertEquals(30, zi.distanceFogColor.B);
        Assertions.assertNotNull(zi);


        String convT3d = zi.convertScaleAndToT3D(2d);

        Assertions.assertTrue(convT3d.contains("Begin Actor Class=AtmosphericFog"));
        Assertions.assertTrue(convT3d.contains("DefaultLightColor=(B=30,G=60,R=80,A=0)"));

        Assertions.assertTrue(convT3d.contains("Begin Actor Class=PostProcessVolume"));
        Assertions.assertTrue(convT3d.contains("AmbientCubemapTint=(B=0.047058824,G=0.046244655,R=0.033217993,A=1.0)"));
    }
}