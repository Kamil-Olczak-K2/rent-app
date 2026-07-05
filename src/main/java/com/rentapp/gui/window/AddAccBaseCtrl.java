package com.rentapp.gui.window;

import com.rentapp.db_object.Accessory;
import com.rentapp.gui.scene.SceneCtrl;
import com.rentapp.table.AccessoryRow;
import com.rentapp.util.DBQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;


public class AddAccBaseCtrl implements Initializable {

    @FXML
    private VBox rootVBox;
    @FXML
    private Label title;
    @FXML
    private Button endButton;
    @FXML
    private TextField name;
    @FXML
    private TextField priceGross;
    @FXML
    private ChoiceBox<String> priceFor;

    private Function<Accessory, Integer> update;
    private AccessoryRow editedAcc = null;

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

        priceFor.getItems().add("brak opłaty");
        priceFor.getItems().add("zużycie 1 mm");
        priceFor.getItems().add("wypożyczenie");
        priceFor.getItems().add("dobę");

        priceFor.getSelectionModel().select(1);
        priceFor.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            if(t1.equals("brak opłaty")){
                priceGross.setText("0");
                priceGross.setDisable(true);
            } else {
                priceGross.setDisable(false);
            }
        });

    }

    @FXML
    void endAction(ActionEvent event) {
        if(!checkFieldsAreFilled()){
            return;
        }
        trimFields();

        if (editedAcc == null) {
            Accessory accessory = getAccessory();
            accessory.setInfo(priceFor.getSelectionModel().getSelectedItem());

            if (DBQuery.addAccessory(accessory)) {
                update.apply(accessory);
                SceneCtrl.closeWindow(event);
            }
        } else {
            Accessory accessory = getAccessory();
            accessory.setInfo(priceFor.getSelectionModel().getSelectedItem());
            accessory.setAccessoryID(editedAcc.getAccessoryID());
            if (DBQuery.updateAccessory(accessory)) {
                update.apply(accessory);
                SceneCtrl.closeWindow(event);
            }
        }
    }
    public void trimFields(){
        name.setText(name.getText().trim());
        priceGross.setText(priceGross.getText().trim());
    }


    private Accessory getAccessory() {
        String name = this.name.getText();
        double priceGross = 0d;
        if (!this.priceGross.isDisabled()) {
            priceGross = Double.parseDouble(this.priceGross.getText().trim().replace(" ", "").replaceAll("[^\\d.]", ""));
        }
        boolean usable = false;
        if (priceFor.getSelectionModel().getSelectedItem().equals("zużycie 1 mm")) {
            usable = true;
        }
        Accessory accessory = new Accessory(name, priceGross, usable);
        return accessory;
    }

    @FXML
    void closeWindow(ActionEvent event) {
        if(checkAnyFieldIsFilled()){
            Optional<ButtonType> res = SceneCtrl.showMessageWindow("Potwierdzenie", "Czy na pewno chcesz zamknąć okno? Wprowadzone dane zostaną utracone.", true);
            if(res.isPresent() && res.get().getButtonData() == ButtonBar.ButtonData.YES){
                SceneCtrl.closeWindow(event);
            }
        } else {
            SceneCtrl.closeWindow(event);
        }
    }

    public void setUpdate(Function<Accessory, Integer> function) {
        this.update = function;
    }

    public void setEditedAcc(AccessoryRow accessory){
        editedAcc = accessory;
        title.setText("Edytuj akcesorium");
        endButton.setText("Zapisz");
        name.setText(accessory.getName());
        priceGross.setText(accessory.getPriceGross());
        priceFor.getSelectionModel().select(accessory.getInfo());
    }

    public boolean checkAnyFieldIsFilled(){
        return  !name.getText().isEmpty() ||
                !priceGross.getText().isEmpty();
    }

    public VBox getRootVBox() {
        return rootVBox;
    }

    public boolean checkFieldsAreFilled(){
        if(name.getText().isEmpty() || priceGross.getText().isEmpty()){
            SceneCtrl.showMessageWindow("Błąd", "Wypełnij wszystkie pola.");
            return false;
        } else {
            return true;
        }
    }

}
