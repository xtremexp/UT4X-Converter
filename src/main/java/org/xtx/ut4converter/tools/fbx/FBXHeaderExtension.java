/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

import java.util.Calendar;

/**
 *
 * @author XtremeXp
 */
public class FBXHeaderExtension implements FBXWriter {

    /**
     * 6.1 version
     */
    static final short DEFAULT_FBX_VERSION = 6100;
    static final short DEFAULT_FBX_HEADER_VERSION = 1003;

    short FBXHeaderVersion;
    public short FBXVersion;
    public CreationTimeStamp creationTimeStamp;
    static String creator;

    public static class CreationTimeStamp {

        short version;
        public long time;
        
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
        int millisecond;
    }

    public static FBXHeaderExtension getInstance(String creator) {

        FBXHeaderExtension fbxHeader = new FBXHeaderExtension();

        fbxHeader.FBXHeaderVersion = DEFAULT_FBX_HEADER_VERSION;
        fbxHeader.FBXVersion = DEFAULT_FBX_VERSION;
        FBXHeaderExtension.creator = creator;
        Calendar cal = Calendar.getInstance();

        CreationTimeStamp cts = new CreationTimeStamp();
        cts.year = cal.get(Calendar.YEAR);
        cts.month = cal.get(Calendar.MONTH);
        cts.day = cal.get(Calendar.DAY_OF_MONTH) + 1;
        cts.hour = cal.get(Calendar.HOUR_OF_DAY);
        cts.minute = cal.get(Calendar.MINUTE);
        cts.second = cal.get(Calendar.SECOND);
        cts.millisecond = cal.get(Calendar.MILLISECOND);
        cts.time = cal.getTimeInMillis();
        
        fbxHeader.creationTimeStamp = cts;

        return fbxHeader;
    }

    @Override
    public void writeFBX(StringBuilder sb) {

        sb.append("FBXHeaderExtension:  {\n");
        sb.append("\tFBXHeaderVersion: ").append(FBXHeaderVersion).append("\n");
        sb.append("\tFBXVersion: ").append(DEFAULT_FBX_VERSION).append("\n");

        sb.append("\tCreationTimeStamp:  {\n");
        sb.append("\t\tVersion: ").append(creationTimeStamp.version).append("\n");
        sb.append("\t\tYear: ").append(creationTimeStamp.year).append("\n");
        sb.append("\t\tMonth: ").append(creationTimeStamp.month).append("\n");
        sb.append("\t\tDay: ").append(creationTimeStamp.day).append("\n");
        sb.append("\t\tHour: ").append(creationTimeStamp.hour).append("\n");
        sb.append("\t\tMinute: ").append(creationTimeStamp.minute).append("\n");
        sb.append("\t\tSecond: ").append(creationTimeStamp.second).append("\n");
        sb.append("\t\tMillisecond: ").append(creationTimeStamp.millisecond).append("\n");
        sb.append("\t}\n");

        sb.append("\tCreator: \"").append(creator).append("\"\n");
        sb.append("}\n");
    }
}
