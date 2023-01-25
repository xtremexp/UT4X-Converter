package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.t3dmesh.StaticMeshTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


class T3DMoverTest {

    /**
     * Test converting unreal UE1 mover brush
     *
     * @throws IOException                  Error read/write obj file
     * @throws ReflectiveOperationException Error
     */
    @Test
    void testConvertU1MoverToUT4() throws IOException, ReflectiveOperationException {

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

        final T3DMover moverBrush = (T3DMover) T3DTestUtils.parseFromT3d(mc, "Mover", T3DMover.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-Mover.t3d")).getPath());

        Assertions.assertEquals(4, moverBrush.getMoverProperties().moveTime);
        Assertions.assertEquals(6, moverBrush.getMoverProperties().stayOpenTime);
        Assertions.assertEquals("DoorsAnc.Stone.sdoorL1", moverBrush.getMoverProperties().moveAmbientSound.toString());
        Assertions.assertEquals("DoorsAnc.GenericThumps.adend61", moverBrush.getMoverProperties().closedSound.toString());
        Assertions.assertEquals("DoorsAnc.GenericThumps.adend61", moverBrush.getMoverProperties().openedSound.toString());
        Assertions.assertEquals(1, moverBrush.getMoverProperties().getPositions().size());
        Assertions.assertEquals(MoverProperties.MoverEncroachType.ME_CrushWhenEncroach, moverBrush.getMoverProperties().moverEncroachType);
        Assertions.assertEquals(MoverProperties.MoverGlideType.MV_GlideByTime, moverBrush.getMoverProperties().moverGlideType);
        // apply this texture to each poly

        // simulate texture was exported (ucc.exe not available in github test env)
        int idx = 1;

        for (T3DPolygon p : moverBrush.getPolyList()) {

            final BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(StaticMeshTest.class.getResource("/textures/Tex256x256-" + idx + ".png")));
            final Dimension texDim = new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
            final Path texPath = Files.createTempFile("Tex256x256-" + idx, ".png");

            ImageIO.write(bufferedImage, "png", texPath.toFile());
            p.getTexture().setTextureDimensions(texDim);
            p.getTexture().getExportedFiles().add(0, texPath.toFile());

            idx++;
        }

        final String convT3D = moverBrush.convertScaleAndToT3D(2d);
        System.out.println(convT3D);

        // move time converted properly
        Assertions.assertTrue(convT3D.contains("Lift Time=4.0"));
        Assertions.assertTrue(convT3D.contains("Wait at top time=6.0"));

        // Destination -> Destination * ScaleFactor = -196 * 2
        Assertions.assertTrue(convT3D.contains("Lift Destination=(X=0.000000,Y=0.000000,Z=-392.000000)"));
    }

}