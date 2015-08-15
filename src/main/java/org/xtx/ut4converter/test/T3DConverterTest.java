/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;

/**
 * Test class for testing all conversion of all UT99 stock maps.
 * 
 * @author XtremeXp
 */
public class T3DConverterTest {

	UserConfig uc;
	UserGameConfig ugc;
	List<File> failedConvMaps;

	public T3DConverterTest() {

	}

	public void test() {

		failedConvMaps = new ArrayList<>();

		try {
			uc = UserConfig.load();
			ugc = uc.getGameConfigByGame(UTGames.UTGame.UT99);

			if (ugc != null) {
				File mapFolder = new File(ugc.getPath().getAbsolutePath() + File.separator + "Maps");

				for (File f : mapFolder.listFiles()) {
					if (f.isDirectory() || !f.getName().endsWith(".unr")) {

					} else {
						try {
							MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, f, "Z:\\TEMP\\MASSTEST");
							mc.setScale(2d);
							mc.convert();
						} catch (Exception e) {
							failedConvMaps.add(f);
						}
					}
				}
			}

			for (File mapFile : failedConvMaps) {
				System.out.println(mapFile.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
}
