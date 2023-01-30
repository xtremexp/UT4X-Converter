/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import org.apache.commons.io.Charsets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author XtremeXp
 */
public class FileUtils {

	public static String getExtension(File file) {

		if (file == null) {
			return null;
		}

		String[] s = file.getName().split("\\.");

		return s[s.length - 1];
	}

	public static File changeExtension(File file, String newExtension) {

		int extIdx = file.getAbsolutePath().indexOf(getExtension(file));

		return new File(file.getAbsolutePath().substring(0, extIdx) + newExtension);
	}

	public static Charset detectEncoding(File file) throws IOException {

		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("File not specified or invalid file");
		}

		try (final BufferedReader bfr = new BufferedReader(new FileReader(file))) {

			String line = bfr.readLine();
			if (line.contains("\0")) {
				return StandardCharsets.UTF_16;
			} else {
				return StandardCharsets.UTF_8;
			}
		}
	}
}
