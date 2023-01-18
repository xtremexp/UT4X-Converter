package org.xtx.ut4converter.t3d;

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

        final T3DZoneInfo ut2004ZoneInfo = (T3DZoneInfo) T3DTestUtils.parseFromT3d(mc, "ZoneInfo", T3DZoneInfo.class, Objects.requireNonNull(T3DZoneInfoTest.class.getResource("/t3d/ue2/UT2004-ZoneInfo.t3d")).getPath());
        System.out.println(ut2004ZoneInfo.convertScaleAndToT3D(2d));
    }
}