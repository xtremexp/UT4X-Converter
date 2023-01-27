package org.xtx.ut4converter.tools;

import com.twelvemonkeys.imageio.plugins.tga.TGAImageReaderSpi;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

class ImageUtilsTest {

    @Test
    void testImageConversion() throws IOException {

        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new TGAImageReaderSpi());
        registry.registerServiceProvider(new net.nikr.dds.DDSImageReaderSpi());

        Path pngFile = Files.createTempFile("test", ".png");
        Path bmpFile = Files.createTempFile("test", ".bmp");

        try {
            // Test .pcx -> .png
            BufferedImage img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/warer3.pcx")));
            Assertions.assertTrue(ImageIO.write(img, "png", pngFile.toFile()));

            // Test .dds -> .png
            img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/UC2Texture.dds")));
            pngFile = Files.createTempFile("test", ".png");
            Assertions.assertTrue(ImageIO.write(img, "png", pngFile.toFile()));

            // Test .bmp -> resave as.bmp
            // unreal 2 exports in a weird bmp format that can't be imported in UT3 editor
            // resaving file with java makes import work
            img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/U2Texture.bmp")));
            bmpFile = Files.createTempFile("test", ".bmp");
            Assertions.assertTrue(ImageIO.write(img, "bmp", bmpFile.toFile()));
        } finally {
            FileUtils.deleteQuietly(pngFile.toFile());
            FileUtils.deleteQuietly(bmpFile.toFile());
        }
    }

    /**
     * Test read unreal 1 .tga texture file
     *
     * @throws IOException Error reading file
     */
    @Test
    void testReadTga() throws IOException {

        final IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new TGAImageReaderSpi());
        BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/gscr1.tga")));

        Assertions.assertNotNull(bufferedImage);
        Assertions.assertEquals(256, bufferedImage.getHeight());
        Assertions.assertEquals(256, bufferedImage.getWidth());
    }
}