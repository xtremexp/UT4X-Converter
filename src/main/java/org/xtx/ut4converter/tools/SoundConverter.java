/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import com.sun.media.sound.WaveFileWriter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * 
 * @author XtremeXp
 */
public  class SoundConverter {
    
    final Logger logger;
    
    /**
     * 
     * @param logger Logger used in map converter
     */
    public SoundConverter(Logger logger){
        this.logger = logger;
    }
    
    /**
     * Get possible format allowed to convert to 16 bits
     * @param srcAudioFormat Audio format of source wave file that need to be converted to 16 bits
     * @return 
     */
    private synchronized AudioFormat getAudioFormat16bit(AudioFormat srcAudioFormat){
        
        for(Encoding encoding : AudioSystem.getTargetEncodings(srcAudioFormat.getEncoding())){
                
            for(AudioFormat audioFormat : AudioSystem.getTargetFormats(encoding, srcAudioFormat)){
                if(audioFormat.getSampleSizeInBits() == 16){
                    return audioFormat;
                } 
            }
        }
        
        return null;
    }
    
    /**
     * Converts .wav file to 16 bit sample size file.
     * UE4 editor does not support 8 bit sample size sounds unlike UE1, UE2, UE3 (?)
     * @param inWaveFile Sound file to be converted
     * @param outWaveFile Converted sound file
     */
    public synchronized void convertTo16BitSampleSize(File inWaveFile, File outWaveFile){
        
        AudioInputStream audioInputStream = null;
        AudioFormat srcAudioFormat = null;
        
        logger.info("Converting "+inWaveFile.getName()+" sound to 44.1 Khz / 16 bit");
        
        try {
            audioInputStream = AudioSystem.getAudioInputStream(inWaveFile);
            srcAudioFormat = audioInputStream.getFormat();
            

            AudioFormat dstAudioFormat = getAudioFormat16bit(srcAudioFormat);
            
            if(dstAudioFormat == null){
                logger.log(Level.WARNING, "No sound conversion available for "+inWaveFile.getName());
                return;
            }
            
            AudioInputStream convertedIn = AudioSystem.getAudioInputStream(dstAudioFormat, audioInputStream);
            
            logger.info("Converting "+inWaveFile.getName()+" sound to 44.1 Khz / 16 bit");
            
            WaveFileWriter writer = new WaveFileWriter();
            writer.write(convertedIn, Type.WAVE, outWaveFile);

        } catch (Exception e){
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error while converting sound file "+inWaveFile.getName());
        }
            
    }
}
