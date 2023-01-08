/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.config.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.Installation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 22/12/2022: removed uModelPath field
 * @author XtremeXp
 */
public class UserConfig {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public final static String USER_CONFIG_JSON_FILE = "UserConfig.json";

	/**
	 * Version of userconfig file
	 */
	private int version = 1;

	/**
	 * If true, program will check for updates at start
	 */
	private boolean checkForUpdates = true;

	/**
	 * true if program running for the first time. If so should display some
	 * pop-up information to redirect user to settings panel
	 */
	private Boolean isFirstRun;

	private List<UserGameConfig> games = new ArrayList<>();


	public boolean isCheckForUpdates() {
		return checkForUpdates;
	}

	public void setCheckForUpdates(boolean checkForUpdates) {
		this.checkForUpdates = checkForUpdates;
	}

	public Boolean getIsFirstRun() {
		return isFirstRun;
	}

	public void setIsFirstRun(Boolean isFirstRun) {
		this.isFirstRun = isFirstRun;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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
			if (game.shortName.equals(gameConfig.getId())) {
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
		File configFile = getUserConfigFile();

		if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
			throw new IOException("Could not create directory " + configFile.getParentFile());
		}

		try (final FileWriter fw = new FileWriter(configFile)) {
			objectMapper.writeValue(fw, this);
		}
	}

	/**
	 *
	 * @return User configuration
	 */
	public static UserConfig load() throws  IOException {
		File file = UserConfig.getUserConfigFile();

		// auto-create config file
		if (!file.exists()) {
			UserConfig userConfig = new UserConfig();
			userConfig.saveFile();
			return userConfig;
		}

		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper.readValue(file, UserConfig.class);
	}

}
