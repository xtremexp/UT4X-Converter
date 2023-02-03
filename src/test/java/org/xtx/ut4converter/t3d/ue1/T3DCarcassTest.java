package org.xtx.ut4converter.t3d.ue1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.ConversionSettings;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


class T3DCarcassTest {

    /**
     * Carcass U1 to UT4 conversion
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testCarcassConversionTest() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DCarcass carcass = (T3DCarcass) T3DTestUtils.parseFromT3d(mc, "MaleBodyTwo", T3DCarcass.class, Objects.requireNonNull(T3DCarcassTest.class.getResource("/t3d/ue1/U1-Carcass.t3d")).getPath());

        Assertions.assertNotNull(carcass);
        final String convT3D = carcass.convertScaleAndToT3D(2d);
        System.out.println(convT3D);
    }


    /**
     * Using version with sound ressources conversion (need unreal game installed)
     *
     * @throws Exception Exception thrown
     */
    @Disabled
    @Test
    void testCarcassWithRessourceConversion() throws Exception {

        final File t3d = new File(Objects.requireNonNull(T3DCarcassTest.class.getResource("/t3d/ue1/U1-Carcass.t3d")).toURI());
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4, t3d, true, true);
        mc.getConversionSettings().setExportOption(ConversionSettings.ExportOption.BY_PACKAGE);
        mc.getConversionSettings().setUt4ReferenceBaseFolder("/Game/Converted/Unreal1");
        mc.getConversionSettings().setScaleFactor(2.5d);
        mc.setUseUbClasses(true);
        mc.convert();
    }

}