package com.rentapp.gui.window;

import com.rentapp.gui.scene.SceneCtrl;
import com.rentapp.table.RentedRow;
import com.rentapp.table.Table;
import com.rentapp.util.DBQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RentHistoryCtrl implements Initializable {
    @FXML
    private VBox rootVBox;
    @FXML
    private TableView<RentedRow> rentalTable;
    @FXML
    private TableColumn<RentedRow, String> client;
    @FXML
    private TableColumn<RentedRow, String> company;
    @FXML
    private TableColumn<RentedRow, String> peselNip;
    @FXML
    private TableColumn<RentedRow, String> equipment;
    @FXML
    private TableColumn<RentedRow, String> equipmentID;
    @FXML
    private TableColumn<RentedRow, String> accessories;
    @FXML
    private TableColumn<RentedRow, String> notes;
    @FXML
    private TableColumn<RentedRow, String> priceGross;
    @FXML
    private TableColumn<RentedRow, String> depositGross;
    @FXML
    private TableColumn<RentedRow, String> depositPayment;
    @FXML
    private TableColumn<RentedRow, String> fromWhen;
    @FXML
    private TableColumn<RentedRow, String> toWhen;
    @FXML
    private TableColumn<RentedRow, String> byWho;
    @FXML
    private TableColumn<RentedRow, String> longTerm;

    @FXML
    private TextField search;
    @FXML
    private ChoiceBox<String> searchColumnChose;

    private Map<String, String> columnsNames = new HashMap<>();
    private TableView<RentedRow> initializedRentalTable = new TableView<>();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootVBox.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ESCAPE){
                SceneCtrl.closeWindow(keyEvent);
            }
        });


        initializeEquipTable();
        rentalTable.getItems().addAll(DBQuery.queryAndMakeHistoryRentalTable());
        initializedRentalTable.getItems().addAll(rentalTable.getItems());

        searchColumnChose.getSelectionModel().select(0);
        search.requestFocus();
    }

    private void initializeEquipTable() {
        client.setCellValueFactory(new PropertyValueFactory<>("client"));
        company.setCellValueFactory(new PropertyValueFactory<>("company"));
        peselNip.setCellValueFactory(new PropertyValueFactory<>("peselNip"));
        equipment.setCellValueFactory(new PropertyValueFactory<>("equipment"));
        equipmentID.setCellValueFactory(new PropertyValueFactory<>("equipmentID"));
        accessories.setCellValueFactory(new PropertyValueFactory<>("accessories"));
        notes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        priceGross.setCellValueFactory(new PropertyValueFactory<>("priceGross"));
        depositGross.setCellValueFactory(new PropertyValueFactory<>("depositGross"));
        depositPayment.setCellValueFactory(new PropertyValueFactory<>("depositPayment"));
        fromWhen.setCellValueFactory(new PropertyValueFactory<>("fromWhen"));
        toWhen.setCellValueFactory(new PropertyValueFactory<>("toWhen"));
        byWho.setCellValueFactory(new PropertyValueFactory<>("byWhoColumn"));
        longTerm.setCellValueFactory(new PropertyValueFactory<>("longTerm"));

        columnsNames.put("Klient", "client");
        searchColumnChose.getItems().add("Klient");
        columnsNames.put("Firma", "company");
        searchColumnChose.getItems().add("Firma");
        columnsNames.put("Pesel/NIP", "peselNip");
        searchColumnChose.getItems().add("Pesel/NIP");
        columnsNames.put("Sprzęt", "equipment");
        searchColumnChose.getItems().add("Sprzęt");
        columnsNames.put("Nr wewnętrzny", "equipmentID");
        searchColumnChose.getItems().add("Nr wewnętrzny");
        columnsNames.put("Akcesoria", "accessories");
        searchColumnChose.getItems().add("Akcesoria");
        columnsNames.put("Uwagi", "notes");
        searchColumnChose.getItems().add("Uwagi");
        columnsNames.put("Cena brutto", "priceGross");
        searchColumnChose.getItems().add("Cena brutto");
        columnsNames.put("Kaucja brutto", "depositGross");
        searchColumnChose.getItems().add("Kaucja brutto");
        columnsNames.put("Data godz wypożyczenia", "fromWhen");
        searchColumnChose.getItems().add("Data godz wypożyczenia");
        columnsNames.put("Data zwrotu", "toWhen");
        searchColumnChose.getItems().add("Data zwrotu");
        columnsNames.put("Przez kogo", "byWhoColumn");
        searchColumnChose.getItems().add("Przez kogo");
        columnsNames.put("Długoterminowy", "longTerm");
        searchColumnChose.getItems().add("Długoterminowy");

        searchColumnChose.getItems().addAll(columnsNames.keySet());
    }


    @FXML
    void search(KeyEvent event) {
        if(search.getText().isEmpty()){
            rentalTable.getItems().clear();
            rentalTable.getItems().addAll(initializedRentalTable.getItems());
        } else {
            rentalTable.getItems().clear();
            rentalTable.getItems().addAll(initializedRentalTable.getItems());
            rentalTable = Table.searchHistoryRentalTable(rentalTable, search.getText(),columnsNames.get(searchColumnChose.getValue()));
        }
    }

    @FXML
    public void closeWindow(ActionEvent event) {
        SceneCtrl.closeWindow(event);
    }

    public VBox getRootVBox() {
        return rootVBox;
    }
}




