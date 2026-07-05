package com.rentapp.gui.window;

import com.rentapp.db_object.Rent;
import com.rentapp.gui.scene.SceneCtrl;
import com.rentapp.table.EquipRow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import static com.rentapp.util.Calculate.*;

public class EditRentedEquipCtrl implements Initializable {

    @FXML
    private Button cancel;
    @FXML
    private Label nameModel;
    @FXML
    private Label labelPriceNett;
    @FXML
    private Label label0;
    @FXML
    private Label labelPriceGross;
    @FXML
    private Label label1;
    @FXML
    private Label labelQty;

    @FXML
    private TextField priceGross;

    @FXML
    private TextField priceNett;

    @FXML
    private TextField quantity;

    @FXML
    private VBox rootVBox;

    @FXML
    private Button updateButton;

    private TextField lastEditedTextField;
    private String selectedType = "";
    private int editedEquipID;
    private Rent editedRent = null;
    private EquipRow editedEquipRow = null;
    private List<?> selected;
    private Function<Integer, Integer> invokeUpdate;
    private double oldPriceNett;
    private int oldQty;
    private double newPriceNett;
    private int newQty;

    public void initialize(URL location, ResourceBundle resources) {
        rootVBox.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                SceneCtrl.closeWindow(keyEvent);
            }
        });

        priceNett.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                updatePriceNett();
            }
        });
        priceGross.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                updatePriceGross();
            }
        });
        quantity.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                updateQuantity();
            }
        });

        priceGross.textProperty().addListener((observable, oldValue, newValue) -> {
            lastEditedTextField = priceGross;
        });

        priceNett.textProperty().addListener((observable, oldValue, newValue) -> {
            lastEditedTextField = priceNett;
        });

        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            lastEditedTextField = quantity;
        });

        rootVBox.setOnMouseClicked(mouseEvent -> {
            updateFields();
        });
    }

    public void updateFields(){
        if(lastEditedTextField == priceGross){
            updatePriceGross();
        } else if(lastEditedTextField == priceNett){
            updatePriceNett();
        } else if(lastEditedTextField == quantity){
            updateQuantity();
        }
    }

    public void updatePriceGross(){
        if (!priceGross.getText().equals(addZeroIfTenthsEqual(calculateGrossString(newPriceNett)))) {
            if (priceGross.getText().contains(",")) {
                priceGross.setText(priceGross.getText().replace(",", "."));
            }
            if (priceGross.getText().isEmpty() || Double.parseDouble(priceGross.getText()) < 0) {
                priceGross.setText(calculateGrossString(oldPriceNett));
            } else if (priceGross.getText().matches(".[^A-z]*")) {
                newPriceNett = calculateNett(parseDoubleRound2Tenths(priceGross.getText()));
                priceGross.setText(addZeroIfTenthsEqual(calculateGrossString(newPriceNett)));
                priceNett.setText(addZeroIfTenthsEqual(String.valueOf(newPriceNett)));
            } else {
                priceGross.setText(calculateGrossString(oldPriceNett));
            }
        }
    }

    public void updatePriceNett(){
        if (!priceNett.getText().equals(addZeroIfTenthsEqual(newPriceNett + ""))) {
            if (priceNett.getText().contains(",")) {
                priceNett.setText(priceNett.getText().replace(",", "."));
            }
            if (priceNett.getText().isEmpty() || Double.parseDouble(priceNett.getText()) < 0) {
                priceNett.setText(String.valueOf(oldPriceNett));
            } else if (priceNett.getText().matches(".[^A-z]*")) {
                newPriceNett = parseDoubleRound2Tenths(priceNett.getText());
                priceNett.setText(addZeroIfTenthsEqual(String.valueOf(newPriceNett)));
                priceGross.setText(addZeroIfTenthsEqual(calculateGrossString(newPriceNett)));
            } else {
                priceNett.setText(String.valueOf(oldPriceNett));
            }
        }

    }

    public void updateQuantity(){


        String quantityText = quantity.getText();
        if (!quantityText.equals(newQty + "")){
            if (quantityText.matches(".[^A-z]*")) {
                newQty = Integer.parseInt(quantityText);
                quantity.setText(String.valueOf(newQty));
            } else {
                quantity.setText(String.valueOf(oldQty));
            }
        }
    }

    @FXML
    void closeWindow(ActionEvent event) {
        SceneCtrl.closeWindow(event);
    }

    @FXML
    void update(ActionEvent event) {
        updateFields();
        if(editedRent != null){
            if(oldPriceNett != newPriceNett || oldQty != newQty){
                if(selectedType.equals("accessory")){
                    if(newQty == 0){
                        editedRent.deleteAccessory(editedEquipID);
                    } else {
                        editedRent.setAccessoryPriceNett(editedEquipID, newPriceNett);
                        editedRent.setAccessoryQty(editedEquipID, newQty);
                    }
                }
                else {
                    editedRent.setEquipmentPricePDayNet(editedEquipID, newPriceNett);
                }
                int x = 0;
                invokeUpdate.apply(x);
                SceneCtrl.closeWindow(event);
            } else {
                SceneCtrl.closeWindow(event);
            }
        } else {
            if(oldQty != newQty){
                editedEquipRow.setAccessoryQty(editedEquipID, newQty);
                int x = 0;
                invokeUpdate.apply(x);
                SceneCtrl.closeWindow(event);
            } else {
                SceneCtrl.closeWindow(event);
            }

        }
    }

    public void setEditedEquip(Function<Integer, Integer> invokeUpdate, Rent rent, String type, int id){
        this.selectedType = type;
        this.editedEquipID = id;
        this.editedRent = rent;
        this.invokeUpdate = invokeUpdate;
        if(selectedType.equals("accessory")){
            nameModel.setText(rent.getIDAccessory().get(editedEquipID).getName());
            oldPriceNett = rent.getIDAccessory().get(editedEquipID).getPriceNettDouble();
            oldQty = rent.getAccessoryIDQty().get(editedEquipID);
            newPriceNett = oldPriceNett;
            newQty = oldQty;
            priceNett.setText(addZeroIfTenthsEqual(oldPriceNett));
            priceGross.setText(calculateGrossString(oldPriceNett));
            quantity.setText(String.valueOf(oldQty));
        }
        else {
            nameModel.setText(rent.getIDEquip().get(editedEquipID).getModel());
            oldPriceNett = rent.getIDEquip().get(editedEquipID).getPerDayNetDouble();
            newPriceNett = oldPriceNett;
            disableFields(false,false,true);
            priceNett.setText(addZeroIfTenthsEqual(oldPriceNett));
            priceGross.setText(calculateGrossString(oldPriceNett));
        }
    }

    public void setEditedEquip(Function<Integer, Integer> invokeUpdate, EquipRow equipRow, int accID){
        this.editedEquipID = accID;
        this.editedEquipRow = equipRow;
        this.invokeUpdate = invokeUpdate;
        nameModel.setText(equipRow.getIDAccessory().get(accID).getName());
        oldQty = equipRow.getAccessoryIDQty().get(accID);
        newQty = oldQty;
        quantity.setText(oldQty + "");
        disableFields(true, true, false);
    }


    public VBox getRootVBox(){
        return rootVBox;
    }

    public void disableFields(boolean priceNett, boolean priceGross, boolean quantity){
        if(priceNett){
            this.priceNett.setDisable(true);
            this.priceNett.setVisible(false);
            labelPriceNett.setVisible(false);
            label0.setVisible(false);
        }
        if(priceGross){
            this.priceGross.setDisable(true);
            this.priceGross.setVisible(false);
            labelPriceGross.setVisible(false);
            label1.setVisible(false);
        }
        if(quantity){
            this.quantity.setDisable(true);
            this.quantity.setVisible(false);
            labelQty.setVisible(false);
        }
    }

}

