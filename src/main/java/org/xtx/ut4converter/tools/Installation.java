/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.tools;


/**
 *
 * @author hyperion
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.MapConverter;

/**
 * Some core class that allows to know where program is running (path)
 * @author XtremeXp
 */
public class Installation {
 
    /**
     * Where all "external" programs should be for converting/extracting stuff
     */
    private static final String BINARIES_FOLDER = "bin";
    
  /**
   * Test is the current running program is installed.
   * <br>
   * This method assumes that an installed java program is contained
   * in a jar file.
   * 
     * @param clazz
   * @return    <code>true</code> if installed
   */
  public static boolean isInstalled(Class clazz) {
    try {
      URL classUrl = getClassURL(clazz);
    
      return "jar".equals(classUrl.getProtocol());
    } catch (Exception ex) {
      // fall through
    }
    return false;   
  }
 
  /**
   * Get the directory where the current running program is installed,
   * i.e. the location of the jar file the given class is contained in. 
   * <br>
   * If it is not installed (see {@link #isInstalled()}) the <em>current
   * user working directory</em> (as denoted by the system property
   * <code>user.dir<code>) is returned instead.
   * 
   * @param  clazz  class to check for installation
   * @return        the installation directory
   * @see
   */
  public static File getInstallDirectory(Class clazz) {
    if (isInstalled(clazz)) {
      try {
        JarURLConnection jarCon = (JarURLConnection)getClassURL(clazz).openConnection();
 
        URL jarUrl = jarCon.getJarFileURL();
    
        File jarFile = new File(URLDecoder.decode(jarUrl.getPath(), "UTF-8"));
    
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
   * @param  clazz  class to get URL for
   * @return the URL this class was loaded from
   */
  private static URL getClassURL(Class clazz) {
    return clazz.getResource("/" + clazz.getName().replace('.', '/'));
  }

    /**
     *
     * @return
     */
    public static File getProgramFolder()
    {
        return Installation.getInstallDirectory(MainApp.class);
    }
    
    public static File getG16ConvertFile()
    {
        return new File(Installation.getInstallDirectory(MainApp.class) + File.separator + BINARIES_FOLDER + File.separator + "g16convert" + File.separator + "g16convert.exe");
    }
    
    /**
     * Return full path of texture converter
     * @return 
     */
    public static File getNConvert()
    {
        return new File(Installation.getInstallDirectory(MainApp.class) + File.separator + BINARIES_FOLDER + File.separator + "nconvert" + File.separator + "nconvert.exe");
    }
    
    /**
     * Return full path of texture converter
     * @param mapConverter
     * @return 
     */
    public static File getUModelPath(MapConverter mapConverter)
    {
        if(mapConverter.getUserConfig() != null){
            return mapConverter.getUserConfig().getUModelPath();
        }
        
        return null;
    }
    
    /**
     * Return full path of texture converter
     * @param mapConverter
     * @return 
     */
    public static File getNConvertPath(MapConverter mapConverter)
    {
        if(mapConverter.getUserConfig() != null){
            return mapConverter.getUserConfig().getNConvertPath();
        }
        
        return null;
    }
    
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Means program running on windows
     * @return 
     */
    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    /**
     * Means program running on mac
     * @return 
     */
    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    /**
     * Means program running on Unix
     * @return 
     */
    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 );
    }

    /**
     * Means program running on linux
     * @return 
     */
    public static boolean isLinux() {
        return (OS.contains("nux") );
    }
    
    /**
     * 
     * @param command Command line / path to exec file
     * @param logLines Store program lofs
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
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(pp.getInputStream()))){

                String log;

                while ((log = in.readLine()) != null) {
                    logLines.add(log);
                }
            } 

            pp.waitFor();
            int exitVal = pp.exitValue();
            
            return exitVal;
        } finally {

            if( pp != null ){
                pp.destroy();
            }
        }
    }
}


