/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;
import org.xtx.ut4converter.ConversionSettings;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class ConversionViewController implements Initializable {

	@FXML
	@Getter
	private TableView<TableRowLog> convLogTableView;

	@FXML
	@Getter
	private TableColumn<TableRowLog, String> logTime;

	@FXML
	@Getter
	private TableColumn<TableRowLog, Level> logLevel;

	@FXML
	@Getter
	private TableColumn<TableRowLog, String> logMsg;

	@FXML
	@Getter
	private ProgressBar progressBar;

	@FXML
	@Getter
	private ProgressBar progressBarDetail;
	@FXML
	@Getter
	private ProgressIndicator progressIndicator;

	@FXML
	@Getter
	private ProgressIndicator progressIndicatorDetail;

	@FXML
	@Getter
	private Label progressMessageDetail;

	@FXML
	@Getter
	private Label progressMessage;

	@Getter
	@Setter
	private ConversionSettings conversionSettings;


	/**
	 * Initializes the controller class.
	 *
	 * @param url Url
	 * @param rb  Resource Bundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		logTime.setCellValueFactory(new PropertyValueFactory<>("time"));
		logLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
		logMsg.setCellValueFactory(new PropertyValueFactory<>("message"));

		logLevel.setCellFactory(column -> new TableCell<>() {

			@Override
			protected void updateItem(Level item, boolean empty) {

				if (item != null) {
					setText(item.getName());

					if (item == Level.WARNING) {
						setStyle("-fx-background-color: #ff9213"); // orange
					} else if (item == Level.SEVERE) {
						setStyle("-fx-background-color: #ff3333"); // red
					} else if (item == Level.FINE) {
						setStyle("-fx-background-color: #e8e8e8"); // white
					} else {
						setStyle(null);
					}
				}
			}
		});
	}
}
