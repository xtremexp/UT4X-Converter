/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
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
    private TableColumn<TableRowLog, Level> logLevel;
    @FXML
    private TableColumn<TableRowLog, String> logMsg;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        logLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        logMsg.setCellValueFactory(new PropertyValueFactory<>("message"));
        
        logLevel.setCellFactory(column -> {
                return new TableCell<TableRowLog, Level>(){
                    
                    @Override
                    protected void updateItem(Level item, boolean empty) {

                        if(item != null){
                            setText(item.getName());

                            if(item == Level.WARNING){
                                setStyle("-fx-background-color: #F5DA81"); // light orange
                            } 
                            else if(item == Level.SEVERE){
                                setStyle("-fx-background-color: #F5A9A9"); // light red
                            }
                            else if(item == Level.FINE){
                                setStyle("-fx-background-color: #E6E6E6"); // light red
                            }
                            else {
                                setStyle(null);
                            }
                        }
                    }
                };
        });
    }    
    
    public TableView<TableRowLog> getConvLogTableView() {
        return convLogTableView;
    }
    
}
