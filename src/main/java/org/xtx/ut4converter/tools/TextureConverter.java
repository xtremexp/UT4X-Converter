/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xtx.ut4converter.config.UserConfig;

/**
 * Interface to use nconvert.exe external program to convert textures
 * 
 * @author XtremeXp
 */
public class TextureConverter {

	Logger logger;
	File nConvertBin;

	public TextureConverter(Logger logger, UserConfig userConfig) {
		this.logger = logger;
		this.nConvertBin = userConfig.getNConvertPath();
	}

	public boolean isNConvertAvailable() {
		return nConvertBin != null && nConvertBin.exists();
	}

	/**
	 * Convert image file to another format
	 * 
	 * @param inputTexture
	 * @param textureFormat
	 */
	public File convert(File inputTexture, TextureFormat textureFormat) {

		if (inputTexture == null) {
			return null;
		}

		String command = nConvertBin.getAbsolutePath() + " -32bits -out " + textureFormat.name() + " \"" + inputTexture.getAbsolutePath() + "\"";

		List<String> logLines = new ArrayList<>();

		try {
			logger.log(Level.INFO, "Converting " + inputTexture.getName() + " to " + textureFormat);
			Installation.executeProcess(command, logLines);

			for (String log : logLines) {
				System.out.println(log);
			}

		} catch (InterruptedException | IOException ex) {
			logger.log(Level.SEVERE, "Error while converting texture", ex);
		}

		return FileUtils.changeExtension(inputTexture, textureFormat.name());
	}
}
