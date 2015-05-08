/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author XtremeXp
 */
public class ConversionViewController implements Initializable {
    
    @FXML
    private TableView<TableRowLog> convLogTableView;
    @FXML
    private TableColumn<TableRowLog, String> logTime;
    @FXML
    private TableColumn<TableRowLog, String> logLevel;
    @FXML
    private TableColumn<TableRowLog, String> logMsg;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logTime.setCellValueFactory(new PropertyValueFactory<TableRowLog, String>("time"));
        logLevel.setCellValueFactory(new PropertyValueFactory<TableRowLog, String>("level"));
        logMsg.setCellValueFactory(new PropertyValueFactory<TableRowLog, String>("message"));
    }    
    
    public TableView<TableRowLog> getConvLogTableView() {
        return convLogTableView;
    }
    
}
