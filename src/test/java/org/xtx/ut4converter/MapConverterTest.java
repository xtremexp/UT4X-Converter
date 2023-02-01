package org.xtx.ut4converter;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.t3d.T3DTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * These tests should be run manually before releasing a new version
 */
@Disabled("Only enable when environement has unreal games installed")
class MapConverterTest {

    /**
     * Test UT2004 to UT4 map conversion
     * @throws Exception
     */
    @Test
    void testUt2004toUT4MapConversion() throws Exception {
        final File uMap = new File("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Unreal Tournament 2004\\maps\\ONS-Dria.ut2");
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4, uMap, true, true);

        final File outputFolder = mc.getMapConvertFolder();

        System.out.print("Cleaning " + mc.getMapConvertFolder() + "... ");
        FileUtils.deleteDirectory(mc.getMapConvertFolder());
        System.out.println("OK");

        System.out.print("Converting " + uMap + " ... ");
        mc.convert();
        System.out.println("OK");

        // Testing some key ressources were exported
        // Sounds exported
        Assertions.assertTrue(new File(outputFolder + "/Sounds/OutdoorAmbience_BThunder_wind1.wav").exists());

        // Textures exported either from map package (with terrain ones as .bmp) or other package
        Assertions.assertTrue(new File(outputFolder + "/Texture/ONS-Dria_flare614.tga").exists());
        Assertions.assertTrue(new File(outputFolder + "/Texture/SkyRenders_BarrenPlanet_ICEFLOWroof.tga").exists());
        Assertions.assertTrue(new File(outputFolder + "/Texture/ONS-Dria_Terraintexalphalayerdetailsand.bmp").exists());

        // Staticmeshes exported either from map package or other package
        Assertions.assertTrue(new File(outputFolder + "/StaticMesh/cp_wasteland_mesh_DecoLayers_cp_rubble1_deco.obj").exists());
        Assertions.assertTrue(new File(outputFolder + "/StaticMesh/ONS-Dria_pssneeuwvleklang.obj").exists());
        Assertions.assertTrue(new File(outputFolder + "/StaticMesh/ONS-Dria_pssneeuwvleklang.mtl").exists());
    }


    /**
     * Test conversion of all maps from input game to output game
     *
     * @param inputGame  Input game
     * @param outputGame Output game
     * @throws IOException Error loading map
     */
    public static void testConvertAllMapsOfGameToGame(UTGames.UTGame inputGame, UTGames.UTGame outputGame) throws IOException {


        final MapConverter mc = T3DTestUtils.getMapConverterInstance(inputGame, outputGame);
        final File mapsFolder = new File(mc.getInputGame().getPath() + File.separator + mc.getInputGame().getMapFolder());
        mc.setNoUi(true);


        int i = 0;

        for (File mapFile : Objects.requireNonNull(mapsFolder.listFiles())) {
            // try every 4 maps
            if ((i % 4 == 0) && mapFile.getName().endsWith("." + mc.getInputGame().getMapExt()) && mapFile.length() > 100000) {
                try {
                    T3DTestUtils.setMapFile(mc, mapFile.getName());
                    mc.convert();
                    System.out.println(mapFile.getAbsolutePath() + " OK");
                } catch (Exception e) {
                    System.out.println("ERROR");
                    e.printStackTrace();
                    System.out.println("Error while converting " + mapFile.getName());
                } finally {
                    // need to reset null to force map export to t3d
                    mc.getConversionSettings().setInputT3DMapFile(null);
                    i++;
                }
            } else {
                i++;
            }
        }
    }


    public static void main(String[] args) throws IOException {
        //testConvertAllMapsOfGameToGame(UTGames.UTGame.U1, UTGames.UTGame.UT3);
        testConvertAllMapsOfGameToGame(UTGames.UTGame.DNF, UTGames.UTGame.UT3);
    }
}