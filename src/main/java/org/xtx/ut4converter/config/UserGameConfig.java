/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.config;

import java.io.File;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.xtx.ut4converter.UTGames;

/**
 * 
 * @author XtremeXp
 */
@XmlRootElement
public class UserGameConfig {

    
    public UserGameConfig() {
        
    }
    
    public UserGameConfig(UTGames.UTGame id, File path) {
        this.id = id;
        this.path = path;
    }
    
    

    UTGames.UTGame id;
    File path;
    File lastConverted;
 
    /**
     * Short name of UT game
     * @return 
     */
    public UTGames.UTGame getId() {
        return id;
    }

    @XmlAttribute(required = true)
    public void setId(UTGames.UTGame id) {
        this.id = id;
    }

    /**
     * Where this game is installed
     * @return 
     */
    public File getPath() {
            return path;
    }

    @XmlElement
    public void setPath(File path) {
        this.path = path;
    }

    /**
     * Last converted map
     * @return 
     */
    @XmlElement
    public File getLastConverted() {
        return lastConverted;
    }

    public void setLastConverted(File lastConverted) {
        this.lastConverted = lastConverted;
    }
    
    
    
}
