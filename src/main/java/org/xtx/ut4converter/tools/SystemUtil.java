package org.xtx.ut4converter.tools;


public class SystemUtil {

    public static boolean is32BitOS(){
        return "i386".equals(System.getProperties().getProperty("os.arch"));
    }
}
