/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.xtx.ut4converter.ui.SettingsSceneController;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.Installation;

/**
 * 
 * @author XtremeXp
 */
@XmlRootElement
public class UserConfig {
    
    public final static String USER_CONFIG_XML_FILE = "UserConfig.xml";
    
    /**
     * umodel.exe path set by user in settings
     */
    File uModelPath;
    
    /**
     * Nconvert path
     */
    File nConvertPath;
    
    List<UserGameConfig> games = new ArrayList<>();
    
    @XmlElement
    public File getUModelPath(){
        return uModelPath;
    }
    
    public void setUModelPath(File uModelPath){
        this.uModelPath = uModelPath;
    }
    
    @XmlElement
    public File getNConvertPath(){
        return uModelPath;
    }
    
    public void setNConvertPath(File nConvertPath){
        this.nConvertPath = nConvertPath;
    }

    @XmlElement
    public List<UserGameConfig> getGame() {
        return games;
    }

    public void setGame(List<UserGameConfig> games) {
        this.games = games;
    }

    public static File getUserConfigFile(){
        return new File(Installation.getProgramFolder().getAbsolutePath() + File.separator + UserConfig.USER_CONFIG_XML_FILE );
    }
    
    /**
     * Tells if use have specifies the path of the game
     * @param game UT Game
     * @return <code>true</code> if UT game path is set in settings
     */
    public boolean hasGamePathSet(UTGames.UTGame game){
        UserGameConfig gameConfig = getGameConfigByGame(game);
        
        if(gameConfig != null){
            return gameConfig.getPath() != null && gameConfig.getPath().exists();
        } else {
            return false;
        }
    }
    
    /**
     * Get the game config for some UT game
     * @param game UT game
     * @return User game configuration for the ut game
     */
    public UserGameConfig getGameConfigByGame(UTGames.UTGame game){
        
        for(UserGameConfig gameConfig : games){
            if(gameConfig.id == game){
                return gameConfig;
            }
        }
        
        return null;
    }
    
    /**
     * Save user configuration to XML file
     * @throws JAXBException 
     */
    public void saveFile() throws JAXBException{
        File configFile;
        
        try {
            configFile = getUserConfigFile();
            JAXBContext jaxbContext = JAXBContext.newInstance(UserConfig.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(this, configFile);
            jaxbMarshaller.marshal(this, System.out);
        } catch (JAXBException e) {
            throw e;
        }
    }
    
    /**
     * 
     * @return
     * @throws JAXBException 
     */
    public static UserConfig load() throws JAXBException{
        try {
            File file = UserConfig.getUserConfigFile();
            
            // auto-create config file
            if(!file.exists()){
                UserConfig userConfig = new UserConfig();
                userConfig.saveFile();
                return userConfig;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(UserConfig.class);
            
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            UserConfig userConfig = (UserConfig) jaxbUnmarshaller.unmarshal(UserConfig.getUserConfigFile());
            
            userConfig.checkGameConfigPaths();
            
            return userConfig;
        } catch (JAXBException ex) {
            Logger.getLogger(SettingsSceneController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    
    /**
     * Checks that user game configuration paths does exits.
     * If not existing, the paths are set to "null" and the user config file is saved.
     * @throws javax.xml.bind.JAXBException
     */
    public void checkGameConfigPaths() throws JAXBException{
        
        boolean saveConfig = false;
        
        for(UserGameConfig userGameConfig : games){
            
            if(userGameConfig.getPath() != null && !userGameConfig.getPath().exists()){
                userGameConfig.setPath(null);
                saveConfig = true;
            }
        }
        
        if(saveConfig){
            saveFile();
        }
    }
    
}

