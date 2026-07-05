package com.rentapp.gui.window;

import com.rentapp.db_object.Accessory;
import com.rentapp.gui.scene.SceneCtrl;
import com.rentapp.table.AccessoryRow;
import com.rentapp.table.EquipRow;
import com.rentapp.util.Calculate;
import com.rentapp.util.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;


public class AddEquipBaseCtrl implements Initializable {
    @FXML
    private VBox rootVBox;
    @FXML
    private Label title;
    @FXML
    private TableView<AccessoryRow> accTable;
    @FXML
    private TableColumn<AccessoryRow, String> accName;
    @FXML
    private TableColumn<AccessoryRow, String> accPriceGross;
    @FXML
    private TableColumn<AccessoryRow, String> accPriceNett;
    @FXML
    private TableColumn<AccessoryRow, String> accQty;
    @FXML
    private TextField checkDate;
    @FXML
    private TextField checkMotohours;
    @FXML
    private TextField deposit;
    @FXML
    private TextField equipID;
    @FXML
    private TextField model;
    @FXML
    private TextField name;
    @FXML
    private TextField perDayGross;
    @FXML
    private TextField sn;
    @FXML
    private TextField value;
    @FXML
    private TextField year;
    @FXML
    private Button endButton;

    private Function<Integer, Integer> setAddedEquip;
    private EquipRow equipRow = new EquipRow();
    private boolean isEdited = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootVBox.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode().equals(javafx.scene.input.KeyCode.ESCAPE)){
                if(checkAnyFieldIsFilled()){
                    Optional<ButtonType> res = SceneCtrl.showMessageWindow("Potwierdzenie", "Czy na pewno chcesz zamknąć okno? Wprowadzone dane zostaną utracone.", true);
                    if(res.isPresent() && res.get().getButtonData() == ButtonBar.ButtonData.YES){
                        SceneCtrl.closeWindow(keyEvent);
                    }
                } else {
                    SceneCtrl.closeWindow(keyEvent);
            }
        }});
        equipID.setText(DBQuery.querryNewEquipID() + "");
        initializeAccTable();
    }

    private void initializeAccTable() {
        accName.setCellValueFactory(new PropertyValueFactory<>("name"));
        accPriceNett.setCellValueFactory(new PropertyValueFactory<>("priceNett"));
        accPriceGross.setCellValueFactory(new PropertyValueFactory<>("priceGross"));
        accQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
    }

    @FXML
    void endAction(ActionEvent event) throws SQLException {
        if(!checkFieldsAreFilled()){
            return;
        }
        if(isEdited){
            if(!gatherData()){
                return;
            }
            if (DBQuery.manageEquip(equipRow, true)) {
                setAddedEquip.apply(0);
                closeWindow(event);
            }
        } else {
            if(DBQuery.checkEquipConstraints(equipID.getText().trim().replaceAll("\\D", ""))){
                if(!gatherData()){
                    return;
                }
                if (DBQuery.manageEquip(equipRow, false)) {
                    setAddedEquip.apply(0);
                    closeWindow(event);
                }
            } else {
                SceneCtrl.showMessageWindow("Błąd", "Sprzęt o takim numerze wewn. lub numerze seryjnym istnieje.");
            }
        }
    }
    public void trimAllFields(){
        equipID.setText(equipID.getText().trim());
        model.setText(model.getText().trim());
        name.setText(name.getText().trim());
        deposit.setText(deposit.getText().trim());
        perDayGross.setText(perDayGross.getText().trim());
        year.setText(year.getText().trim());
        sn.setText(sn.getText().trim());
        checkDate.setText(checkDate.getText().trim());
        checkMotohours.setText(checkMotohours.getText().trim());
        value.setText(value.getText().trim());
    }

    public boolean gatherData() throws SQLException {
        trimAllFields();
        equipRow.setEquipmentID(equipID.getText().trim());
        equipRow.setSn(sn.getText().trim());
        equipRow.setModel(model.getText());
        equipRow.setName(name.getText());
        equipRow.setDeposit(deposit.getText().trim().replace(" ", "").replaceAll("[^\\d.]", ""));
        equipRow.setPerDayGross(Double.parseDouble(perDayGross.getText().trim().replace(" ", "").replaceAll("[^\\d.]", "")));
        equipRow.setEquipValueGross(Integer.parseInt(value.getText().trim().replace(" ", "").replaceAll("[^\\d.]", "")));

        if (!year.getText().isEmpty()) {
            try {
                Integer.parseInt(year.getText().trim().replace(" ", "").replaceAll("\\D", ""));
                equipRow.setManufactureYear(year.getText().trim().replace(" ", "").replaceAll("\\D", ""));
            } catch (NumberFormatException e) {
                SceneCtrl.showMessageWindow("Błąd", "Wprowadź rok produkcji w prawidłowym formacie.");
                return false;
            }
        }

        if (!checkDate.getText().isEmpty()) {
            if (isValidDateFormat(checkDate.getText().trim())) {
                equipRow.setReviewDate(checkDate.getText().trim());
            } else {
                SceneCtrl.showMessageWindow("Niepoprawny format daty", "Wprowadź datę w formacie dd.mm.yyyy");
                return false;
            }
        }

        if (!checkMotohours.getText().isEmpty()) {
            try {
                Integer.parseInt(checkMotohours.getText().trim());
                equipRow.setMotohours(checkMotohours.getText().trim());
            } catch (NumberFormatException e) {
                SceneCtrl.showMessageWindow("Błąd", "Wprowadź liczbę motogodzin w formacie liczbowym.");
                return false;
            }
        }
        return true;
    }


    @FXML
    void addAccessory(ActionEvent event) throws IOException {
        SceneCtrl.showAddAccessoryWindow(accessories -> {
            equipRow.addAccessories(accessories);
            refreshAccTable();
            return true;
        }, "Dodaj akcesoria");
    }

    @FXML
    void closeWindow(ActionEvent event) {
        SceneCtrl.closeWindow(event);
    }

    @FXML
    void deleteAccessory(ActionEvent event) {
        AccessoryRow accRow = accTable.getSelectionModel().getSelectedItem();
        equipRow.removeAccessory(accRow.getAccessoryID());
        refreshAccTable();
    }

    @FXML
    void editAccQty(ActionEvent event) {
        AccessoryRow accRow = accTable.getSelectionModel().getSelectedItem();
        TextInputDialog dialog = new TextInputDialog(accRow.getQty());
        dialog.setTitle("Edycja ilości");
        dialog.setHeaderText("Podaj nową ilość");
        dialog.setContentText("Ilość:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qty -> {
            equipRow.setAccessoryQty(accRow.getAccessoryID(), Integer.parseInt(qty));
            refreshAccTable();
        });
    }

    public void setSetAddedEquip(Function<Integer, Integer> function) {
        this.setAddedEquip = function;
    }
    public void setBaseEquip(EquipRow equipRow){
        this.equipRow = equipRow;
        model.setText(equipRow.getModel());
        name.setText(equipRow.getName());
        deposit.setText(equipRow.getDeposit());
        perDayGross.setText(equipRow.getPerDayGrossDouble() + "");

        checkDate.setText(equipRow.getReviewDate());
        checkMotohours.setText(equipRow.getMotohours());
        value.setText(equipRow.getEquipValueGrossInt() + "");
        refreshAccTable();
    }

    public void setEditedEquip(EquipRow equipRow){
        this.equipRow = equipRow;
        isEdited = true;
        title.setText("Edytuj sprzęt");
        endButton.setText("Zapisz");
        equipID.setText(equipRow.getEquipmentID());
        equipID.setDisable(true);
        model.setText(equipRow.getModel());
        name.setText(equipRow.getName());
        deposit.setText(equipRow.getDeposit());
        perDayGross.setText(equipRow.getPerDayGrossDouble() + "");
        year.setText(equipRow.getManufactureYear());
        if(equipRow.getSn() == null){
            sn.setText("");
        }else {
            sn.setText(equipRow.getSn());
        }

        checkDate.setText(equipRow.getReviewDate());
        checkMotohours.setText(equipRow.getMotohours());
        value.setText(equipRow.getEquipValueGrossInt() + "");
        refreshAccTable();
    }

    public void refreshAccTable() {
        ObservableList<AccessoryRow> data = FXCollections.observableArrayList();
        for (Accessory acc : equipRow.getIDAccessory().values()) {
            AccessoryRow accRow = new AccessoryRow();
            accRow.setAccessoryID(acc.getAccessoryID());
            accRow.setName(acc.getName());
            accRow.setPriceNett(acc.getPriceNettString());
            accRow.setPriceGross(Calculate.calculateGrossString(acc.getPriceNettDouble()));
            accRow.setQty(equipRow.getAccessoryIDQty().get(acc.getAccessoryID()) + "");
            accRow.setInfo(acc.getInfo());
            data.add(accRow);
        }
        accTable.getItems().clear();
        accTable.getItems().addAll(data);
    }

    public boolean checkAnyFieldIsFilled(){
        return  !equipID.getText().isEmpty() ||
                !model.getText().isEmpty() ||
                !name.getText().isEmpty() ||
                !deposit.getText().isEmpty() ||
                !perDayGross.getText().isEmpty() ||
                !year.getText().isEmpty() ||
                !sn.getText().isEmpty() ||
                !checkDate.getText().isEmpty() ||
                !checkMotohours.getText().isEmpty() ||
                !value.getText().isEmpty();
    }

    public VBox getRootVBox() {
        return rootVBox;
    }

    public static boolean isValidDateFormat(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean checkFieldsAreFilled(){
        if(equipID.getText().isEmpty() || model.getText().isEmpty() || name.getText().isEmpty() || deposit.getText().isEmpty() || perDayGross.getText().isEmpty() || value.getText().isEmpty()){
            SceneCtrl.showMessageWindow("Błąd", "Wypełnij wymagane pola. Pola wymagane są oznaczone gwiazdką.");
            return false;
        } else {
            return true;
        }
    }

}
