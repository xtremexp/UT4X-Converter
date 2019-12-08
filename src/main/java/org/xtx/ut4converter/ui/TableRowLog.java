/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import javafx.beans.property.SimpleStringProperty;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class for handling logs that will be redirected to table log in user
 * interface
 * 
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class TableRowLog {

	/**
	 * Log level
	 */
	private Level level;

	/**
	 * Log message
	 */
	private final SimpleStringProperty message;

	/**
	 * Time of log in mm:ss:SSS format
	 */
	private final SimpleStringProperty time;

	/**
	 * Date formatter
	 */
	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");

	public TableRowLog(LogRecord logRecord) {
		this.level = logRecord.getLevel();

		if(logRecord.getMessage() != null){
			this.message = new SimpleStringProperty(TableRowLog.getMessageFormatted(logRecord));
		} else {
			// weird sometimes message is null ... (?)
			this.message = new SimpleStringProperty("");
		}

		this.time = new SimpleStringProperty(sdf.format(new Date(logRecord.getMillis())));
	}

	public static String getMessageFormatted(LogRecord logRecord) {
		if (logRecord.getMessage() != null) {
			if (logRecord.getThrown() != null) {
				return MessageFormat.format(logRecord.getMessage(), logRecord.getParameters()) + " " + logRecord.getThrown().getMessage();
			} else {
				return MessageFormat.format(logRecord.getMessage(), logRecord.getParameters());
			}
		} else {
			return "";
		}
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level logLevel) {
		level = logLevel;
	}

	public String getMessage() {
		return message.get();
	}

	public void setMessage(String fName) {
		message.set(fName);
	}

	public String getTime() {
		return time.get();
	}

	public void setTime(String fName) {
		time.set(fName);
	}
}
