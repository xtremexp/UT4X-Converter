/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.xtx.ut4converter.controller.MainSceneController;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author XtremeXp
 */
public class UIUtils {

	/**
	 * Opens explorer to specific folder
	 * 
	 * @param dirToOpen Folder to open with explorer
	 */
	public static void openExplorer(File dirToOpen) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(dirToOpen);
		} catch (IOException ex) {
			Logger.getLogger(UIUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Opens url in web browser
	 *
	 * @param url
	 *            Url to open with web browser
	 */
	public static void openUrl(String url, boolean confirmBeforeOpen, String message) {

		if (url == null) {
			return;
		}

		if (confirmBeforeOpen) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Web browser access");

			message = message != null ? (message + " \n") : "";
			message += "Do you want to open web browser to this url ?\n" + url;

			alert.setContentText(message);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get() != ButtonType.OK) {
				return;
			}
		} else if (message != null) {

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Information");
			alert.setHeaderText("Message");

			alert.setContentText(message);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get() != ButtonType.OK) {
				return;
			}
		}

		if (Desktop.isDesktopSupported()) {
			try {
				Desktop desktop = Desktop.getDesktop();

				desktop.browse(new URI(url));
			} catch (URISyntaxException | IOException ex) {
				Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Impossible to open web browser");
			alert.setContentText("Your system is not or does not support desktop. \n Manually go to:" + url);

			alert.showAndWait();
		}
	}
}
