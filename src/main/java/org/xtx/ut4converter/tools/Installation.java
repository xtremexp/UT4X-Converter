/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.tools;

/**
 *
 * @author hyperion
 */

import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Some core class that allows to know where program is running (path)
 * 
 * @author XtremeXp
 */
public class Installation {

	/**
	 * Where all "external" programs should be for converting/extracting stuff
	 */
	public static final String APP_FOLDER = "app";

	/**
	 * Test is the current running program is installed. <br>
	 * This method assumes that an installed java program is contained in a jar
	 * file.
	 * 
	 * @param clazz Class
	 * @return <code>true</code> if installed
	 */
	public static boolean isInstalled(Class clazz) {
		try {
			URL classUrl = getClassURL(clazz);
			
			if(classUrl == null){
				return false;
			}
			
			return "jar".equals(classUrl.getProtocol());
		} catch (Exception ex) {
			// fall through
		}
		return false;
	}

	/**
	 * Get the directory where the current running program is installed, i.e.
	 * the location of the jar file the given class is contained in. <br>
	 * If it is not installed (see {@link #isInstalled(Class)}}) the <em>current
	 * user working directory</em> (as denoted by the system property
	 * <code>user.dir<code>) is returned instead.
	 *
	 * The install directory should be normally "C:\Program Files\UT4X-Converter\app"
	 * 
	 * @param clazz Class
	 *            class to check for installation
	 * @return the installation directory
	 * @see
	 */
	public static File getInstallDirectory(Class clazz) {
		if (isInstalled(clazz)) {
			try {
				JarURLConnection jarCon = (JarURLConnection) getClassURL(clazz).openConnection();

				URL jarUrl = jarCon.getJarFileURL();

				File jarFile = new File(URLDecoder.decode(jarUrl.getPath(), StandardCharsets.UTF_8));

				return jarFile.getParentFile();
			} catch (Exception ex) {
				// fall through
			}
		}

		return new File(System.getProperty("user.dir"));
	}

	/**
	 * Get URL of the given class.
	 * 
	 * @param clazz
	 *            class to get URL for
	 * @return the URL this class was loaded from
	 */
	private static URL getClassURL(Class clazz) {
		return clazz.getResource("/" + clazz.getName().replace('.', '/'));
	}

	/**
	 *
	 * @return
	 */
	public static File getProgramFolder() {
		return Installation.getInstallDirectory(MainApp.class);
	}


	public static File getDocumentProgramFolder(){
		return new File(Installation.getDocumentUserFolder().getAbsolutePath() + File.separator + "UT4X-Converter");
	}

	public static File getContentFolder(){
		File file = new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "conf");

		if(file.exists()){
			return file;
		}
		// install directory is app folder if using the .exe UT4X converter file
		else {
			return new File(Installation.getInstallDirectory(MainApp.class)  + File.separator + "conf");
		}
	}

	public static File getG16ConvertFile() {
		File file = new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "g16convert" + File.separator + "g16convert.exe");

		if (file.exists()) {
			return file;
		} else {
			return new File(Installation.getInstallDirectory(MainApp.class) + File.separator + "g16convert" + File.separator + "g16convert.exe");
		}
	}

	/**
	 * Basically c:\Users\<user>\Documents
	 * @return
	 */
	public static File getDocumentUserFolder(){
		return new JFileChooser().getFileSystemView().getDefaultDirectory();
	}
	
	
	/**
	 * 
	 * @return Sox converter binary file
	 */
	public static File getSox() {
		File file = new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "sox" + File.separator + "sox.exe");

		if (file.exists()) {
			return file;
		} else {
			return new File(Installation.getInstallDirectory(MainApp.class) + File.separator  + "sox" + File.separator + "sox.exe");
		}
	}

	/**
	 * Return full path of texture converter
	 * 
	 * @param mapConverter
	 * @return
	 */
	public static File getUModelPath(MapConverter mapConverter) {

		File file = new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "umodel" + File.separator + "umodel_64.exe");

		if (file.exists()) {
			return file;
		} else {
			return new File(Installation.getInstallDirectory(MainApp.class) + File.separator  + "umodel" + File.separator + "umodel_64.exe");
		}
	}



	/**
	 * Return full path of texture extractor (basic extractor once done for UT3
	 * converter). The only one working for Unreal 2!
	 * 
	 * @return
	 */
	public static File getExtractTextures() {
		File file = new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "utxextractor" + File.separator + "ExtractTextures.exe");

		if (file.exists()) {
			return file;
		} else {
			return new File(Installation.getInstallDirectory(MainApp.class) +  File.separator + "utxextractor" + File.separator + "ExtractTextures.exe");
		}
	}

	public static File getUtxAnalyser() {
		File file = new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "utxextractor" + File.separator + "UtxAnalyser.exe");

		if (file.exists()) {
			return file;
		} else {
			return new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "utxextractor" + File.separator + "UtxAnalyser.exe");
		}
	}

	private static final String OS = System.getProperty("os.name").toLowerCase();

	/**
	 * Means program running on windows
	 * 
	 * @return
	 */
	public static boolean isWindows() {
		return (OS.contains("win"));
	}

	/**
	 * Means program running on mac
	 * 
	 * @return
	 */
	public static boolean isMac() {
		return (OS.contains("mac"));
	}

	/**
	 * Means program running on Unix
	 * 
	 * @return
	 */
	public static boolean isUnix() {
		return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
	}

	/**
	 * Means program running on linux
	 * 
	 * @return
	 */
	public static boolean isLinux() {
		return (OS.contains("nux"));
	}

	/**
	 * 
	 * @param command
	 *            Command line / path to exec file
	 * @param logLines
	 *            Store program lofs
	 * @return Program exit code (0 if everything went fine)
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static synchronized int executeProcess(String command, List<String> logLines) throws InterruptedException, IOException {

		Runtime run;
		Process pp = null;

		try {
			run = Runtime.getRuntime();
			pp = run.exec(command);

			try (BufferedReader in = new BufferedReader(new InputStreamReader(pp.getInputStream()))) {

				String log;

				while ((log = in.readLine()) != null) {
					logLines.add(log);
				}
			}

			pp.waitFor();

			return pp.exitValue();
		} finally {

			if (pp != null) {
				pp.destroy();
			}
		}
	}
}
