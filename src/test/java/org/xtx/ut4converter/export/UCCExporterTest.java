package org.xtx.ut4converter.export;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.ApplicationConfig;
import org.xtx.ut4converter.t3d.T3DTestUtils;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class UCCExporterTest {

    public final static String T3D_EXPORT_FOLDER = "C:\\dev\\temp\\UT4X-UT";


    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllU1LevelsToUT4() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.U1, UTGames.UTGame.UT4);
    }

    /**
     * Test convert all U1 maps to UT3
     * @throws Exception
     */
    @Test
    public void testExportAllU1LevelsToUT3() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.U1, UTGames.UTGame.UT3);
    }

    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllUT99Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.UT99, UTGames.UTGame.UT4);
    }

    /**
     * Exports all U2 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllU2Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.U2, UTGames.UTGame.UT4);
    }

    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllUT2003Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.UT2003, UTGames.UTGame.UT4);
    }

    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllUT2004Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);
    }

    /**
     * Exports all UT3 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllUT3Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.UT3, UTGames.UTGame.UT4);
    }

    @Test
    public void testExportAllDNFLevels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.DNF, UTGames.UTGame.UT3);
    }

    private void exportAllUTXLevels(final UTGames.UTGame inputGame, final UTGames.UTGame outputGame) throws Exception {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(inputGame, outputGame);


        final List<UnrealGame> games = ApplicationConfig.getBaseGames();
        final UnrealGame inputGame2 = games.stream().filter(g -> g.getShortName().equals(inputGame.shortName)).findFirst().orElse(null);


        final File mapFolder = new File(inputGame2.getPath() + File.separator + mc.getInputGame().getMapFolder());

        final Collection<File> utxMapFiles = FileUtils.listFiles(mapFolder, new String[]{inputGame.mapExtension}, true);

        FileUtils.deleteDirectory(new File(T3D_EXPORT_FOLDER));
        final File exportFolderBase = new File(T3D_EXPORT_FOLDER + "\\t3d\\" + inputGame.shortName);

        if (exportFolderBase.exists() || exportFolderBase.mkdirs()) {

            int idx = 0;

            for (final File map : utxMapFiles) {

                // U1 and Unreal 2 maps have no prefixes
                // Try to convert regular stock maps
                if (!map.getName().startsWith("UB") && map.getName().contains("-") || (inputGame == UTGames.UTGame.U1 || inputGame == UTGames.UTGame.U2)) {
                    final File t3dFile = UCCExporter.exportLevelToT3d(mc, map);
                    final File newT3dFile = new File(exportFolderBase + "/" + map.getName() + ".t3d");

                    // might have been ever exported
                    newT3dFile.delete();

                    FileUtils.moveFile(t3dFile, newT3dFile);
                    System.out.println(newT3dFile + " - " + idx + "/" + utxMapFiles.size());
                    idx++;
                }
            }
        }
    }

}