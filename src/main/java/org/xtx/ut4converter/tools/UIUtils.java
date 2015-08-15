/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author XtremeXp
 */
public class UIUtils {

	/**
	 * Opens explorer to specific folder
	 * 
	 * @param dirToOpen
	 */
	public static void openExplorer(File dirToOpen) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(dirToOpen);
		} catch (IOException ex) {
			Logger.getLogger(UIUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static boolean confirm(String header, String text) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(header);
		alert.setContentText(text);

		Optional<ButtonType> result = alert.showAndWait();

		return result.get() != ButtonType.OK;
	}
}
