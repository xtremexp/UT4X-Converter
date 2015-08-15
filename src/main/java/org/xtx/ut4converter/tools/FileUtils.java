/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import java.io.File;

/**
 *
 * @author XtremeXp
 */
public class FileUtils {

	public static String getExtension(File file) {

		if (file == null) {
			return null;
		}

		String s[] = file.getName().split("\\.");

		return s[s.length - 1];
	}

	public static File changeExtension(File file, String newExtension) {

		int extIdx = file.getAbsolutePath().indexOf(getExtension(file));

		return new File(file.getAbsolutePath().substring(0, extIdx) + newExtension);
	}
}
