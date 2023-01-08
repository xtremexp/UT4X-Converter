
/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.config.model;


import java.io.File;

/**
 * 
 * @author XtremeXp
 */
public class UserGameConfig {


	/**
	 * Unreal game id (e.g: 'UT99')
	 */
	private String id;
	/**
	 * Path where game is installed.
	 * For UE4+, its the editor install path
	 */
	private File path;

	private UserGameConfig() {

	}

	public UserGameConfig(String id, File path) {
		this.id = id;
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

}
