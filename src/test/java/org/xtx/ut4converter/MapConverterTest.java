package org.xtx.ut4converter;

import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.T3DTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Class to test conversion of maps in batch
 */
class MapConverterTest {


    /**
     * Test conversion of all maps from input game to output game
     *
     * @param inputGame  Input game
     * @param outputGame Output game
     * @throws IOException Error loading map
     */
    public static void testConvertAllMapsOfGameToGame(UTGames.UTGame inputGame, UTGames.UTGame outputGame) throws IOException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(inputGame, outputGame);
        final File mapsFolder = UTGames.getMapsFolder(mc.getUserConfig().getGameConfigByGame(mc.getInputGame()).getPath(), mc.getInputGame());
        mc.setNoUi(true);


        int i = 0;

        for (File mapFile : Objects.requireNonNull(mapsFolder.listFiles())) {
            // try every 4 maps
            if ((i % 4 == 0) && mapFile.getName().endsWith(UTGames.getPackageFileExtensionByGameAndType(inputGame, T3DRessource.Type.LEVEL)) && mapFile.length() > 100000) {
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
                    mc.setInT3d(null);
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