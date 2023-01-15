package org.xtx.ut4converter.tools.t3dmesh;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DBrush;
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
    void testReadAndConvertToObj() throws IOException {
        final File t3dFile = new File("C:\\dev\\Temp\\crane_cab.t3d");
        final StaticMesh t3dStaticMesh = new StaticMesh(t3dFile);

        final File mtlFile = new File("E:\\TEMP\\crane_cab.mtl");
        final File objFile = new File("E:\\TEMP\\crane_cab.obj");

        final ObjStaticMesh objStaticMesh = new ObjStaticMesh(t3dStaticMesh);
        objStaticMesh.export(mtlFile, objFile);
    }

    /**
     * Test converting a T3D brush to .obj wavefront staticmesh
     *
     * @throws IOException Error read/write obj file
     * @throws ReflectiveOperationException Error
     */
    @Test
    void testReadBrushAndConvertToObj() throws IOException, ReflectiveOperationException {


        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DBrush brush = (T3DBrush) T3DTestUtils.parseFromT3d(mc, "Brush", T3DBrush.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/Brush.t3d")).getPath());

        final File mtlFile = new File("C:\\dev2\\brush.mtl");
        final File objFile = new File("C:\\dev2\\brush.obj");
        final File texFile = new File("C:\\dev\\TexA256x256.png");

        Files.deleteIfExists(mtlFile.toPath());
        Files.deleteIfExists(objFile.toPath());

        // apply this texture to each poly
        final BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(StaticMeshTest.class.getResource("/textures/TexA256x256.png")));
        final Dimension texDim =  new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());

        // simulate texture was exported (ucc.exe not available in github test env)
        brush.getPolyList().forEach(p -> {
            p.getTexture().setTextureDimensions(texDim);
            p.getTexture().getExportedFiles().add(0, texFile);
        });

        ObjStaticMesh.writeMtlObjFile(brush, mtlFile);
        ObjStaticMesh.writeObj(brush, objFile, mtlFile);
    }

}