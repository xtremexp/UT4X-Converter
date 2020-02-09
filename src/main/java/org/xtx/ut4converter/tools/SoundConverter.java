/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

// openjdk visibility limation
//import com.sun.media.sound.WaveFileWriter;

/**
 * 
 * @author XtremeXp
 */
public class SoundConverter {

	private File soxConverter;
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
	 * Get possible format allowed to convert to 16 bits
	 * 
	 * @param srcAudioFormat
	 *            Audio format of source wave file that need to be converted to
	 *            16 bits
	 * @return
	 */
	private synchronized AudioFormat getAudioFormat16bit(AudioFormat srcAudioFormat) {

		for (Encoding encoding : AudioSystem.getTargetEncodings(srcAudioFormat.getEncoding())) {

			for (AudioFormat audioFormat : AudioSystem.getTargetFormats(encoding, srcAudioFormat)) {
				if (audioFormat.getSampleSizeInBits() == 16) {
					return audioFormat;
				}
			}
		}

		return null;
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

		// convert sound using sox program
		if(this.soxConverter.exists()){
			convertTo16BitSampleSizeUsingSox(inWaveFile, outWaveFile);
		} 
		// convert sound using java core api
		else {
			logger.warning("Sox sound converter not found in " + this.soxConverter + " Using core java converter.");
			convertTo16BitSampleSizeUsingCoreApi(inWaveFile, outWaveFile);
		}

	}
	
	/**
	 * 
	 * @param inWaveFile
	 * @param outWaveFile
	 */
	private synchronized void convertTo16BitSampleSizeUsingSox(File inWaveFile, File outWaveFile){
		
		final String cmd = soxConverter.getAbsolutePath() + " \"" + inWaveFile.getAbsolutePath() + "\" -r 44100 -b 16 \"" + outWaveFile.getAbsolutePath() + "\"";
		
		logger.info("Converting "+inWaveFile.getName()+" sound to 44.1 Khz / 16 bit");
		List<String> logLines  = new ArrayList<>();
		
		try {
			Installation.executeProcess(cmd, logLines);
		} catch (InterruptedException | IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	/**
	 * Convert sound wave file to 16 bit sample size (UE4 does not support 8 bits sample sound files)
	 * Using the core conversion is crap since some sound might not be correctly imported by UE4
	 * @deprecated might be deleted in some near future ...
	 * @param inWaveFile Sound file
	 * @param outWaveFile Converted 16 bit sound file
	 */
	private synchronized void convertTo16BitSampleSizeUsingCoreApi(File inWaveFile, File outWaveFile){
		// TODO use other lib (if existing) since OPENJDK can't use WaveFileWriter
		throw new UnsupportedOperationException("OPENJDK 11 does not support this operation");

		/*
		AudioInputStream audioInputStream = null;
		AudioInputStream convertedIn = null;

		AudioFormat srcAudioFormat;

		try {
			audioInputStream = AudioSystem.getAudioInputStream(inWaveFile);
			srcAudioFormat = audioInputStream.getFormat();

			// SampleSize ever 16 bits, just do a file copy
			if (srcAudioFormat.getSampleSizeInBits() == 16) {
				Files.copy(inWaveFile.toPath(), outWaveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				return;
			}

			AudioFormat dstAudioFormat = getAudioFormat16bit(srcAudioFormat);

			if (dstAudioFormat == null) {
				logger.log(Level.WARNING, "No sound conversion available for " + inWaveFile.getName());
				return;
			}

			convertedIn = AudioSystem.getAudioInputStream(dstAudioFormat, audioInputStream);

			logger.info("Converting " + inWaveFile.getName() + " sound to 44.1 Khz / 16 bit");

			WaveFileWriter writer = new WaveFileWriter();
			writer.write(convertedIn, Type.WAVE, outWaveFile);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while converting sound file " + inWaveFile.getName(), e);
		}

		finally {

			if (audioInputStream != null) {
				try {
					audioInputStream.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error while converting sound file " + inWaveFile.getName(), e);
				}
			}

			if (convertedIn != null) {
				try {
					convertedIn.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error while converting sound file " + inWaveFile.getName(), e);
				}
			}
		}*/
	}
}
