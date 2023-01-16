package org.xtx.ut4converter.tools.t3dmesh;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DMover;
import org.xtx.ut4converter.t3d.T3DPolygon;
import org.xtx.ut4converter.t3d.T3DTestUtils;
import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;


public class StaticMeshTest {


    /**
     * Test read staticmesh from .t3d file and conversion to Wavefront (.obj) staticmesh file
     */
    @Test
    void testConvertT3DStaticMeshToObj() throws IOException {
        final File t3dFile = new File("C:\\dev\\Temp\\crane_cab.t3d");
        final StaticMesh t3dStaticMesh = new StaticMesh(t3dFile);

        final File mtlFile = new File("E:\\TEMP\\crane_cab.mtl");
        final File objFile = new File("E:\\TEMP\\crane_cab.obj");

        final ObjStaticMesh objStaticMesh = new ObjStaticMesh(t3dStaticMesh);
        objStaticMesh.export(mtlFile, objFile);
    }

    /**
     * Test converting unreal UE1 mover brush
     *
     * @throws IOException Error read/write obj file
     * @throws ReflectiveOperationException Error
     */
    @Test
    void testConvertUE1Mover() throws IOException, ReflectiveOperationException {

        // see /resources/t3d/ue1/Brush.t3d
        // Import a 256x256x256 brush cube
        // Face 1 has texture scale = 1 - Front
        // Face 2 has texture scale = 0.5 - Right side of Face 1
        // Face 3 has PanU = 64 - Right side of Face 2
        // Face 4 has PanV = 32 - Right side of Face 3
        // Face 5 has texture scale = 1 - Top
        // Face 6 has texture scale = 1 - Bottom
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        mc.setConvertSounds(false);

        final T3DMover moverBrush = (T3DMover) T3DTestUtils.parseFromT3d(mc, "Mover", T3DMover.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/Brush.t3d")).getPath());

        final File mtlFile = new File("C:\\dev2\\brush.mtl");
        final File objFile = new File("C:\\dev2\\brush.obj");

        Files.deleteIfExists(mtlFile.toPath());
        Files.deleteIfExists(objFile.toPath());

        // apply this texture to each poly

        // simulate texture was exported (ucc.exe not available in github test env)
        int idx = 1;

        for (T3DPolygon p : moverBrush.getPolyList()) {

            final BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(StaticMeshTest.class.getResource("/textures/Tex256x256-" + idx + ".png")));
            final Dimension texDim = new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
            final File texFile = new File("C:\\dev\\Temp\\Tex256x256-" + idx + ".png");

            ImageIO.write(bufferedImage, "png", texFile);
            p.getTexture().setTextureDimensions(texDim);
            p.getTexture().getExportedFiles().add(0, texFile);

            idx ++;
        }

        moverBrush.convert();
    }

}