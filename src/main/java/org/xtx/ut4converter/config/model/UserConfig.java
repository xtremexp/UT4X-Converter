/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.config.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.xtx.ut4converter.ui.SettingsSceneController;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.Installation;

/**
 * 
 * @author XtremeXp
 */
public class UserConfig {

	private static ObjectMapper objectMapper = new ObjectMapper();

	public final static String USER_CONFIG_JSON_FILE = "UserConfig.json";

	/**
	 * umodel.exe path set by user in settings
	 */
	private File uModelPath;

	/**
	 * Nconvert path
	 */
	private File nConvertPath;

	/**
	 * true if program running for the first time. If so should display some
	 * pop-up information to redirect user to settings panel
	 */
	private Boolean isFirstRun;

	List<UserGameConfig> games = new ArrayList<>();

	public File getUModelPath() {
		return uModelPath;
	}

	public void setUModelPath(File uModelPath) {
		this.uModelPath = uModelPath;
	}

	public Boolean getIsFirstRun() {
		return isFirstRun;
	}

	public void setIsFirstRun(Boolean isFirstRun) {
		this.isFirstRun = isFirstRun;
	}

	public File getNConvertPath() {
		return nConvertPath;
	}

	public void setNConvertPath(File nConvertPath) {
		this.nConvertPath = nConvertPath;
	}

	public List<UserGameConfig> getGame() {
		return games;
	}

	public void setGame(List<UserGameConfig> games) {
		this.games = games;
	}

	public static File getUserConfigFile() {
		return new File(Installation.getDocumentProgramFolder().getAbsolutePath() + File.separator + UserConfig.USER_CONFIG_JSON_FILE);
	}

	/**
	 * Tells if use have specifies the path of the game
	 * 
	 * @param game
	 *            UT Game
	 * @return <code>true</code> if UT game path is set in settings
	 */
	public boolean hasGamePathSet(UTGames.UTGame game) {
		UserGameConfig gameConfig = getGameConfigByGame(game);

		if (gameConfig != null) {
			return gameConfig.getPath() != null && gameConfig.getPath().exists();
		} else {
			return false;
		}
	}

	/**
	 * Get the game config for some UT game
	 * 
	 * @param game
	 *            UT game
	 * @return User game configuration for the ut game
	 */
	public UserGameConfig getGameConfigByGame(UTGames.UTGame game) {

		for (UserGameConfig gameConfig : games) {
			if (gameConfig.id == game) {
				return gameConfig;
			}
		}

		return null;
	}

	/**
	 * Save user configuration to XML file
	 * 
	 */
	public void saveFile() throws IOException {
		File configFile = getUserConfigFile();;
		configFile.getParentFile().mkdirs();

		try (final FileWriter fw = new FileWriter(configFile)) {
			objectMapper.writeValue(fw, this);
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static UserConfig load() throws  IOException {
		try {
			File file = UserConfig.getUserConfigFile();

			// auto-create config file
			if (!file.exists()) {
				UserConfig userConfig = new UserConfig();
				userConfig.saveFile();
				return userConfig;
			}

			return objectMapper.readValue(file, UserConfig.class);
		} catch (IOException ex) {
			Logger.getLogger(SettingsSceneController.class.getName()).log(Level.SEVERE, null, ex);
			throw ex;
		}
	}

}
