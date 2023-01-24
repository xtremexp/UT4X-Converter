package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.t3dmesh.StaticMeshTest;

import java.io.IOException;
import java.util.Objects;


class T3DStaticMeshTest {


    /**
     * Test converting staticmesh with skin, culldistance from UT2004 to UT4
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing t3dstaticmesh actor
     */
    @Test
    void testStaticMeshConversionUT2004toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);

        final T3DStaticMesh sm = (T3DStaticMesh) T3DTestUtils.parseFromT3d(mc, "StaticMeshActor", T3DStaticMesh.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-StaticMesh.t3d")).getPath());

        Assertions.assertEquals("AnubisStatic.All.pharoh", sm.staticMesh.toString());
        Assertions.assertEquals(5000, sm.cullDistance);
        Assertions.assertEquals(1, sm.skins.size());

        final String convT3d = sm.convertScaleAndToT3D(1d);
        Assertions.assertFalse(convT3d.contains("NoCollision"));
        Assertions.assertTrue(convT3d.contains("CachedMaxDrawDistance=5000.0"));
        Assertions.assertTrue(convT3d.contains("StaticMesh=StaticMesh'/Game/Converted/mymap-UT2004/AnubisStatic_All_pharoh.AnubisStatic_All_pharoh'"));
        Assertions.assertTrue(convT3d.contains("OverrideMaterials(0)=Material'/Game/Converted/mymap-UT2004/AnubisTextures_All_Pharoh_t_Mat.AnubisTextures_All_Pharoh_t_Mat'"));
        System.out.println(convT3d);
    }

    /**
     * Test converting staticmesh with no collision
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing t3dstaticmesh actor
     */
    @Test
    void testStaticMeshNoColliConversionUT2004toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);

        final T3DStaticMesh sm = (T3DStaticMesh) T3DTestUtils.parseFromT3d(mc, "StaticMeshActor", T3DStaticMesh.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-StaticMesh-NoColli.t3d")).getPath());

        Assertions.assertEquals("AnubisStatic.All.pharoh", sm.staticMesh.toString());
        Assertions.assertEquals(5000, sm.cullDistance);
        Assertions.assertEquals(false, sm.collideActors);

        final String convT3d = sm.convertScaleAndToT3D(1d);
        Assertions.assertTrue(convT3d.contains("bUseDefaultCollision=false"));
        Assertions.assertTrue(convT3d.contains("NoCollision"));
        System.out.println(convT3d);
    }

    @Test
    void testStaticMeshConversionUT2004toUT3() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT3);

        final T3DStaticMesh ut2004Sm = (T3DStaticMesh) T3DTestUtils.parseFromT3d(mc, "StaticMeshActor", T3DStaticMesh.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-StaticMesh.t3d")).getPath());
        System.out.println(ut2004Sm.convertScaleAndToT3D(1d));
    }
}