package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.t3dmesh.StaticMeshTest;

import java.io.IOException;
import java.util.Objects;


class T3DStaticMeshTest {


    @Test
    void testStaticMeshConversionUT2004toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);

        final T3DStaticMesh ut2004Sm = (T3DStaticMesh) T3DTestUtils.parseFromT3d(mc, "StaticMeshActor", T3DStaticMesh.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-StaticMesh.t3d")).getPath());
        ut2004Sm.convertScaleAndToT3D(2d);
    }

    @Test
    void testStaticMeshConversionUT2004toUT3() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT3);

        final T3DStaticMesh ut2004Sm = (T3DStaticMesh) T3DTestUtils.parseFromT3d(mc, "StaticMeshActor", T3DStaticMesh.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-StaticMesh.t3d")).getPath());
        ut2004Sm.convertScaleAndToT3D(2d);
    }
}