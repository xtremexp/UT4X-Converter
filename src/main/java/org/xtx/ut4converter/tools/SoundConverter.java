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

// openjdk visibility limation
//import com.sun.media.sound.WaveFileWriter;

/**
 * 
 * @author XtremeXp
 */
public class SoundConverter {

	private final File soxConverter;
	final Logger logger;

	/**
	 * 
	 * @param logger
	 *            Logger used in map converter
	 */
	public SoundConverter(Logger logger) {
		this.logger = logger;
		this.soxConverter = Installation.getSox();
	}

	/**
	 * Converts .wav file to 16 bit sample size file. UE4 editor does not
	 * support 8 bit sample size sounds unlike UE1, UE2, UE3 (?)
	 * 
	 * @param inWaveFile
	 *            Sound file to be converted
	 * @param outWaveFile
	 *            Converted sound file
	 */
	public synchronized void convertTo16BitSampleSize(File inWaveFile, File outWaveFile) {

		convertTo16BitSampleSizeUsingSox(inWaveFile, outWaveFile);
	}
	
	/**
	 * 
	 * @param inWaveFile Input wave file
	 * @param outWaveFile Output converted wave file
	 */
	private synchronized void convertTo16BitSampleSizeUsingSox(File inWaveFile, File outWaveFile){
		
		final String cmd = "\"" + soxConverter.getAbsolutePath() + "\" \"" + inWaveFile.getAbsolutePath() + "\" -r 44100 -b 16 \"" + outWaveFile.getAbsolutePath() + "\"";
		
		logger.info("Converting "+inWaveFile.getName()+" sound to 44.1 Khz / 16 bit");
		List<String> logLines  = new ArrayList<>();
		
		try {
			Installation.executeProcess(cmd, logLines);
		} catch (InterruptedException | IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	

}
