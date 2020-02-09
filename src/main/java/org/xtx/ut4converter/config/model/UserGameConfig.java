/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.config.model;

import java.io.File;
import org.xtx.ut4converter.UTGames;

/**
 * 
 * @author XtremeXp
 */
public class UserGameConfig {

	private UserGameConfig() {

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
	 * 
	 * @return
	 */
	public UTGames.UTGame getId() {
		return id;
	}

	public void setId(UTGames.UTGame id) {
		this.id = id;
	}

	/**
	 * Where this game is installed
	 * 
	 * @return
	 */
	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	/**
	 * Last converted map
	 * 
	 * @return
	 */
	public File getLastConverted() {
		return lastConverted;
	}

	public void setLastConverted(File lastConverted) {
		this.lastConverted = lastConverted;
	}

}
