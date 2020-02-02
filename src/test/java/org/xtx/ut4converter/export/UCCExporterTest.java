package org.xtx.ut4converter.export;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.BaseTest;

import java.io.File;
import java.util.Collection;

public class UCCExporterTest {

    public final static String T3D_EXPORT_FOLDER = "C:\\dev\\temp\\UT4X-UT";


    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllU1Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.U1);
    }

    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllUT99Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.UT99);
    }

    /**
     * Exports all U2 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllU2Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.U2);
    }

    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllUT2003Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.UT2003);
    }

    /**
     * Exports all UT2004 maps to .t3d level file
     *
     * @throws Exception
     */
    @Test
    public void testExportAllUT2004Levels() throws Exception {
        exportAllUTXLevels(UTGames.UTGame.UT2004);
    }

    private void exportAllUTXLevels(final UTGames.UTGame inputGame) throws Exception {
        final MapConverter mc = BaseTest.getMapConverterInstance(inputGame);


        final File ut2004BasePath = mc.getUserConfig().getGameConfigByGame(mc.getInputGame()).getPath();
        final File mapFolder = UTGames.getMapsFolder(ut2004BasePath, mc.getInputGame());

        final Collection<File> utxMapFiles = FileUtils.listFiles(mapFolder, new String[]{inputGame.mapExtension}, true);
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