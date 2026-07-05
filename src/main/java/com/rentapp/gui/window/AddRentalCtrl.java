package com.rentapp.gui.window;

import com.rentapp.util.Calculate;
import com.rentapp.util.DBQuery;
import com.rentapp.db_object.Accessory;
import com.rentapp.db_object.Rent;
import com.rentapp.table.*;
import com.rentapp.gui.scene.SceneCtrl;
import com.rentapp.util.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.DateTimeException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

public class AddRentalCtrl implements Initializable {

    @FXML
    private TextField clientInfo;
    @FXML
    private TextField depositGross;
    @FXML
    private TableView<ToRentRow> rentedEquipTable;
    @FXML
    private TableColumn<ToRentRow, String> equipID;
    @FXML
    private TableColumn<ToRentRow, String> model;
    @FXML
    private TableColumn<ToRentRow, String> name;
    @FXML
    private TableColumn<ToRentRow, String> perDayNett;
    @FXML
    private TableColumn<ToRentRow, String> perDayGross;
    @FXML
    private TableColumn<ToRentRow, String> quantity;
    @FXML
    private TextField totPerDayGross;
    @FXML
    private TextField totPerDayNet;
    @FXML
    private VBox rootVBox;
    @FXML
    private TextArea notes;
    @FXML
    private Label totalNettLabel;
    @FXML
    private Label totalGrossLabel;
    @FXML
    private TextField fromWhen;
    @FXML
    private TextField fromWhenTime;
    @FXML
    private TextField probableToWhen;
    @FXML
    private CheckBox longTermRental;
    @FXML
    private TextField placeOfWork;
    @FXML
    private ChoiceBox depositPayment;

    private Rent rent;
    private Function<Integer, Integer> mainScreenCtrl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootVBox.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                closeWindowKey(keyEvent);
            }
        }));
        initializeRentedEquipTable();

        rent = new Rent(DBQuery.queryNewRentalID());
        fromWhen.setText(rent.getFromWhenPretty());
        fromWhenTime.setText(rent.getFromWhenTime());
        probableToWhen.setText(rent.getProbableToWhenPretty());
        depositPayment.getItems().addAll("gotówka", "karta", "przelew");
        depositPayment.getSelectionModel().select(0);

        if(rent.getRentalID() == -1){
            SceneCtrl.showMessageWindow("Błąd", "Brak połączenia z bazą danych. \nSprawdź połączenie z serwerem bazy danych i spróbuj ponownie.");
            rootVBox.addEventHandler(KeyEvent.KEY_PRESSED, (SceneCtrl::closeWindow));
        }

        clientInfo.setText("Wybierz klienta");


    }

    public void initializeRentedEquipTable(){
        equipID.setCellValueFactory(new PropertyValueFactory<>("equipID"));
        model.setCellValueFactory(new PropertyValueFactory<>("model"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        perDayNett.setCellValueFactory(new PropertyValueFactory<>("perDayNett"));
        perDayGross.setCellValueFactory(new PropertyValueFactory<>("perDayGross"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    public void updateRentedEquipTable(){
        rentedEquipTable.getItems().clear();
        rentedEquipTable.getItems().addAll(Table.updateRentedEquipTable(rent));
    }

    public void updateRentedEquipScreen(){
        totPerDayNet.setText(Calculate.addZeroIfTenthsEqual(rent.getTotPerDayNett()));
        totPerDayGross.setText(Calculate.addZeroIfTenthsEqual(rent.getTotPerDayGross()));
        depositGross.setText(String.valueOf(rent.getDepositGross()));
        notes.setText(rent.getNote());
    }

    @FXML
    public void addEquipment(ActionEvent event) throws IOException {
        if(rent.getClient() != null){
            SceneCtrl.showAddEquipmentToRentalWindow((equip) -> {
                this.setSelectedEquip(equip);
                return true;
            });
        } else {
            SceneCtrl.showMessageWindow("Błąd", "Wybierz klienta.");
        }
    }

    @FXML
    public void changeClient(ActionEvent event) throws IOException {
        SceneCtrl.showChangeClientWindow((client) -> {
            this.setSelectedClient(client);
            return true;
        });
    }


    @FXML
    public void addAccessory(ActionEvent event) throws IOException {
        if(rent.getClient() != null){
            SceneCtrl.showAddAccessoryWindow((accessory) -> {
                this.setSelectedAccessory(accessory);
                return true;
            }, "Dodaj do wypożyczenia");
        } else {
            SceneCtrl.showMessageWindow("Błąd", "Wybierz klienta.");
        }
    }

    @FXML
    public void closeWindow(ActionEvent event) {
        if(closeWindowConfirmation()){
            SceneCtrl.closeWindow(event);
        }
    }
    public void closeWindowKey(KeyEvent event) {
        if(closeWindowConfirmation()){
            SceneCtrl.closeWindow(event);
        }
    }
    public boolean closeWindowConfirmation(){
        if(rent.getClient() != null){
            Optional<ButtonType> result = SceneCtrl.showMessageWindow("Zamknij okno",
                                                                  "Czy na pewno chcesz zamknąć okno? \n" +
                                                                          "Wszystkie niezapisane zmiany zostaną utracone.",
                                                                true);
            if(result.get().getButtonData() == ButtonBar.ButtonData.YES){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @FXML
    public void delete(ActionEvent event) {
        ToRentRow selectedRow = rentedEquipTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            if(selectedRow.getType().equals("accessory")){
                rent.setNote(notes.getText());
                rent.deleteAccessory(selectedRow.getAccessoryIDInt());
                rent.updateTotPerDayNett();
                rent.updateTotPerDayGross();
                updateRentedEquipTable();
                updateRentedEquipScreen();
            } else {
                rent.deleteEquipment(selectedRow.getEquipIDInt());
                rent.updateTotPerDayNett();
                rent.updateTotPerDayGross();
                rent.updateDepositGross();
                updateRentedEquipTable();
                updateRentedEquipScreen();
            }
        } else {
            SceneCtrl.showMessageWindow("Błąd", "Wybierz sprzęt do usunięcia.");
        }

    }

    @FXML
    public void finalizeRent(ActionEvent event) throws IOException {
        if (notes.getText().length() > 255) {
            SceneCtrl.showMessageWindow("Błąd", "Maksymalna ilośc znaków w uwagach to 255");
            return;
        }
        if (rent.getClient() == null) {
            SceneCtrl.showMessageWindow("Błąd", "Wybierz klienta.");
            return;
        }
        if (rentedEquipTable.getItems().isEmpty()) {
            SceneCtrl.showMessageWindow("Błąd", "Dodaj sprzęt do wypożyczenia.");
            return;
        }

        rent.setNote(notes.getText().replaceAll("\t", " "));
        rent.setPlaceOfWork(placeOfWork.getText().trim().replaceAll("\t", " "));
        rent.setLongTerm(longTermRental.isSelected());
        rent.setDepositGross(Integer.parseInt(depositGross.getText()));
        String selectedPayment = depositPayment.getSelectionModel().getSelectedItem().toString();
        if (selectedPayment.equals("gotówka") && rent.getDepositGross() == 0) {
            rent.setDepositPayment("");
        } else {
            rent.setDepositPayment(selectedPayment);
        }
        try {
            rent.setFromWhen(fromWhen.getText() + " " + fromWhenTime.getText());
            rent.setProbableToWhen(probableToWhen.getText());
        } catch (DateTimeException e) {
            SceneCtrl.showMessageWindow("Błąd", "Niepoprawny format daty lub godziny. Prawidłowy format to: \ndd.mm.yyyy \nhh:mm");
            e.printStackTrace(); Logger.logExToFile(e);
            return;
        }
        rent.setIsNewClient(DBQuery.checkClientStatus(rent.getClient().getClientIDInt()));

        SceneCtrl.showPrintWindowWithInvoke(rent.isNewClient(), true, rent, (x) -> {
            if (DBQuery.addRent(rent)) {
                mainScreenCtrl.apply(0);
                SceneCtrl.closeWindow(event);
                return 0;
            } else {
                SceneCtrl.showMessageWindow("Błąd", "Wystąpił błąd podczas dodawania wypożyczenia do bazy danych.");
                return 1;
            }
        });

    }

    @FXML
    public void editRentedEqiup(ActionEvent event) throws IOException {
        ToRentRow selectedRow = rentedEquipTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            if (selectedRow.getType().equals("accessory")) {
                SceneCtrl.showEditRentedEquipWindow((x) -> {
                    rent.updateTotPerDayNett();
                    rent.updateTotPerDayGross();
                    updateRentedEquipTable();
                    updateRentedEquipScreen();
                    return 0;
                }, rent, selectedRow.getType(), selectedRow.getAccessoryIDInt());
            } else {
                SceneCtrl.showEditRentedEquipWindow((x) -> {
                    rent.updateTotPerDayNett();
                    rent.updateTotPerDayGross();
                    updateRentedEquipTable();
                    updateRentedEquipScreen();
                    return 0;
                }, rent, selectedRow.getType(), selectedRow.getEquipIDInt());
            }
        } else {
            SceneCtrl.showMessageWindow("Błąd", "Wybierz sprzęt do edycji.");
        }
    }

    @FXML
    public void selectLongTermRental(ActionEvent event){
        if(longTermRental.isSelected()){
            totalNettLabel.setText("Stawka miesięczna sprzętu netto:");
            totalGrossLabel.setText("Stawka miesięczna sprzętu brutto:");
        } else {
            totalNettLabel.setText("Stawka dobowa sprzętu netto:");
            totalGrossLabel.setText("Stawka dobowa sprzętu brutto:");
        }
    }

    public void setSelectedClient(ClientRow client) {
        rent.setClient(client);
        rent.setClientRentID(DBQuery.queryClientNewRentID(client.getClientID()));
        clientInfo.setText(client.getName() + ";  " + (client.getCompany().isEmpty() ? "" : client.getCompany() + ";  ") + client.getPhoneNumber());
        if(client.isDepositFree()){
            rent.setDepositGross(0);
            depositGross.setText("0");
            depositGross.setDisable(true);
            depositPayment.setDisable(true);
        }
    }

    public void setSelectedAccessory(List<AccessoryRow> selectedAccessory){
        for(AccessoryRow accessoryRow : selectedAccessory){
            Accessory accessory = new Accessory(accessoryRow.getAccessoryID(), accessoryRow.getName(), accessoryRow.getPriceNett(), accessoryRow.isUsable(), accessoryRow.getInfo());
            rent.addAccessory(accessory, 1);
        }
        updateRentedEquipTable();
        rent.updateTotPerDayNett();
        rent.updateTotPerDayGross();
        rent.updateNotes();
        updateRentedEquipScreen();
    }


    public void setSelectedEquip(List<EquipRow> selectedEquip) {
        rent.addEquipment(selectedEquip);
        for(EquipRow equip : selectedEquip){
            rent.addAccessory(equip.getIDAccessory(), equip.getAccessoryIDQty());
        }
        updateRentedEquipTable();
        rent.updateTotPerDayNett();
        rent.updateTotPerDayGross();
        rent.updateNotes();
        if(!rent.getClient().isDepositFree()){
            rent.updateDepositGross();
        } else {
            rent.setDepositGross(0);
        }
        updateRentedEquipScreen();

    }

    public void setMainScreenCtrl(Function<Integer, Integer> function){
        this.mainScreenCtrl = function;
    }


    public VBox getRootVBox() {
        return rootVBox;
    }

}
