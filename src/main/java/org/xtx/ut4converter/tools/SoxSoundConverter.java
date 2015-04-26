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

/**
 * Uses sox program to convert sound ressources
 * @author XtremeXp
 */
public  class SoxSoundConverter {
    
    final Logger logger;
    
    /**
     * 
     * @param logger Logger used in map converter
     */
    public SoxSoundConverter(Logger logger){
        this.logger = logger;
    }
    
    /**
     * Converts .wav file to 44k wav file
     * @param inWaveFile Sound file to be converted
     * @param outWaveFile Converted sound file
     */
    public synchronized void convertTo44k(File inWaveFile, File outWaveFile){
        
        String command = Installation.getSoxSoundConverter().getAbsolutePath() +  " \"" + inWaveFile.getAbsolutePath() + "\" -r 44100 \"" + outWaveFile.getAbsolutePath() + "\"";
        
        logger.info(command);
        List<String> logLines = new ArrayList<>();
        
        try {
            Installation.executeProcess(command, logLines);
        } catch (InterruptedException | IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        for(String logLine : logLines){
            logger.info(logLine);
        }
    }
}
