/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.tools;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.xtx.ut4converter.MainApp;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	public static boolean isInstalled(Class<MainApp> clazz) {
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
	 * <p>
	 * The install directory should be normally "C:\Program Files\UT4X-Converter\app"
	 * 
	 * @param clazz Class
	 *            class to check for installation
	 * @return the installation directory
	 */
	public static File getInstallDirectory(Class<MainApp> clazz) {
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
	private static URL getClassURL(Class<MainApp> clazz) {
		return clazz.getResource("/" + clazz.getName().replace('.', '/'));
	}

	/**
	 *
	 * @return Full path of UT4X converter program
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
	 * @return Full path of user documents folder
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
	 * @return Full path of umodel program
	 */
	public static File getUModelPath() {

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
	 * @return Full path of extract textures program
	 */
	public static File getExtractTextures() {
		File file = new File(Installation.getInstallDirectory(MainApp.class) + File.separator + APP_FOLDER + File.separator + "utxextractor" + File.separator + "ExtractTextures.exe");

		if (file.exists()) {
			return file;
		} else {
			return new File(Installation.getInstallDirectory(MainApp.class) +  File.separator + "utxextractor" + File.separator + "ExtractTextures.exe");
		}
	}


	/**
	 *
	 * @return Full path of utx analyser program
	 */
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
	 * @return <code>true</code> if current OS is windows else <code>false</code>
	 */
	public static boolean isWindows() {
		return (OS.contains("win"));
	}


	/**
	 * Means program running on linux
	 *
	 * @return <code>true</code> if current OS is linux else <code>false</code>
	 */
	public static boolean isLinux() {
		return (OS.contains("nux"));
	}


	public static synchronized int executeProcess(String command, List<String> logLines) throws InterruptedException, IOException {
		return executeProcess(command, logLines, null, null);
	}

	/**
	 * 
	 * @param command
	 *            Command line / path to exec file
	 * @param logLines
	 *            Store program lofs
	 * @param logger if not null will directly log output of process to logger
	 * @param logLevel Log level
	 * @return Program exit code (0 if everything went fine)
	 * @throws InterruptedException Exception thrown
	 * @throws IOException Exception thrown
	 */
	public static synchronized int executeProcess(String command, List<String> logLines, Logger logger, Level logLevel) throws InterruptedException, IOException {

		Runtime run;
		Process pp = null;

		try {
			run = Runtime.getRuntime();
			pp = run.exec(command);

			try (BufferedReader in = new BufferedReader(new InputStreamReader(pp.getInputStream()))) {

				String log;

				while ((log = in.readLine()) != null) {
					if (logger != null) {
						logger.log(logLevel, log);
					}
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


	public static GitHubReleaseJson getLatestRelease() throws IOException, InterruptedException {

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.github.com/repos/xtremexp/UT4X-Converter/releases/latest"))
				.build();

		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setPropertyNamingStrategy( PropertyNamingStrategies.SNAKE_CASE );


		final HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

		return objectMapper.readValue(response.body(), GitHubReleaseJson.class);
	}

	/**
	 * Check for update.
	 * Returns latest release information if there is an update available else <code>null</code>
	 *
	 * @return Latest release information if there is an update available else <code>null</code>
	 */
	public static GitHubReleaseJson checkForUpdate() throws IOException, InterruptedException {

		final GitHubReleaseJson latestRelease = getLatestRelease();

		final String[] remoteVersionSplit = latestRelease.getTagName().replace("v", "").split("\\.");
		final String[] currentVersionSplit = MainApp.VERSION.split("\\.");

		if (Integer.parseInt(remoteVersionSplit[0]) > Integer.parseInt(currentVersionSplit[0])) {
			return latestRelease;
		}

		if (Integer.parseInt(remoteVersionSplit[1]) > Integer.parseInt(currentVersionSplit[1])) {
			return latestRelease;
		}

		if (Integer.parseInt(remoteVersionSplit[2]) > Integer.parseInt(currentVersionSplit[2])) {
			return latestRelease;
		} else {
			return null;
		}
	}
}
