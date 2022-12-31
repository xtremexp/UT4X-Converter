/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.config.model;

import org.xtx.ut4converter.UTGames;

import java.io.File;

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
	 * @return Id of game
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
	 * @return Installation path of game
	 */
	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

}
