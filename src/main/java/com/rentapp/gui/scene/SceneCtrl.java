package com.rentapp.gui.scene;

import com.rentapp.db_object.Accessory;
import com.rentapp.db_object.Rent;
import com.rentapp.gui.window.*;
import com.rentapp.table.*;
import com.rentapp.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SceneCtrl {
    public static Stage stage;
    private static Scene scene;
    private static Parent root;

    private static boolean printed;

    public static void switchToMainScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("main_screen.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainCtrl ctrl = loader.getController();
        scene = new Scene(root, ctrl.getRootVBox().getPrefWidth(), ctrl.getRootVBox().getPrefHeight());
        stage.setScene(scene);
        stage.show();
    }

    public static void showRentHistoryWindow() throws IOException{
        FXMLLoader loader = new FXMLLoader(App.class.getResource("rent_history_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        RentHistoryCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(),controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        newWindow.show();
    }

    public static void showAddRentalWindow(Function<Integer, Integer> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_rental_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        AddRentalCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(),controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        controller.setMainScreenCtrl(function);
        newWindow.show();
    }

    public static void showEditRentedEquipWindow(Function<Integer, Integer> function, Rent rent, String type, int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("edit_rented_equip_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        EditRentedEquipCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        controller.setEditedEquip(function, rent, type, id);
        newWindow.show();
    }

    public static void showAddClientWindow(Function<ClientRow, Boolean> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_client_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, 750, 500);
        newWindow.setScene(scene);
        AddClientCtrl controller = loader.getController();
        controller.setInvokeUpdate(function);
        newWindow.show();
    }


    public static void  showEditClientWindow(ClientRow clientRow, Function<ClientRow, Boolean> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_client_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, 750, 500);
        newWindow.setScene(scene);
        AddClientCtrl controller = loader.getController();
        controller.setInvokeUpdate(function);
        controller.setEditedClient(clientRow);
        newWindow.show();
    }

    public static void showAddEquipWindow(Function<Integer, Integer> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_equip_base_window.fxml"));
        root = loader.load();
        AddEquipBaseCtrl controller = loader.getController();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        controller.setSetAddedEquip(function);
        newWindow.show();
    }

    public static void showAddEquipWindow(EquipRow equipRow, Function<Integer, Integer> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_equip_base_window.fxml"));
        root = loader.load();
        AddEquipBaseCtrl controller = loader.getController();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        controller.setSetAddedEquip(function);
        controller.setBaseEquip(equipRow);
        newWindow.show();
    }


    public static void showEditEquipWindow(EquipRow equipRow, Function<Integer, Integer> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_equip_base_window.fxml"));
        root = loader.load();
        AddEquipBaseCtrl controller = loader.getController();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        controller.setSetAddedEquip(function);
        controller.setEditedEquip(equipRow);
        newWindow.show();
    }


    public static void showChangeClientWindow(Function<ClientRow, Boolean> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("change_client_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, 1146, 617);
        newWindow.setScene(scene);
        ChangeClientCtrl controller = loader.getController();
        controller.setSetSelectedClient(function);
        newWindow.show();
    }

    public static void showAddEquipmentToRentalWindow(Function<List<EquipRow>, Boolean> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_equipment_to_rental_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, 1146, 617);
        newWindow.setScene(scene);
        AddEquipRentalCtrl controller = loader.getController();
        controller.setSetSelectedEquip(function);
        newWindow.show();
    }

    public static void showAddAccessoryWindow(Function<List<AccessoryRow>, Boolean> function, String addButtonLabel) throws IOException{
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_accessory_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        scene = new Scene(root, 1146, 617);
        newWindow.setScene(scene);
        AddAccessoryCtrl controller = loader.getController();
        controller.setSetSelectedAccesssory(function);
        controller.setAddButtonLabel(addButtonLabel);
        newWindow.show();
    }

    public static void showAddAccBaseWindow(Function<Accessory, Integer> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_acc_base_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        AddAccBaseCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        controller.setUpdate(function);
        newWindow.setScene(scene);

        newWindow.show();
    }

    public static void showEditAccBaseWindow(AccessoryRow row, Function<Accessory, Integer> function) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_acc_base_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        AddAccBaseCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        controller.setUpdate(function);
        controller.setEditedAcc(row);
        newWindow.setScene(scene);
        newWindow.show();
    }

    public static void showChangePasswdWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("change_passwd_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        ChangePasswdCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        newWindow.show();
    }

    public static void showAddUserWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("add_user_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        AddUserCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        newWindow.show();
    }

    public static void selectUserToDeleteWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("chose_user_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        ChoseUserCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        newWindow.setScene(scene);
        newWindow.show();
    }
    public static void selectUserToResetWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("chose_user_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        ChoseUserCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        controller.setResetPasswd();
        newWindow.setScene(scene);
        newWindow.show();
    }

    public static void showResetPasswdWindow(UserRow user) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("reset_passwd_window.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        ResetPasswdCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        controller.setUser(user);
        newWindow.setScene(scene);
        newWindow.show();
    }




    public static void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static void closeWindow(KeyEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static void showMessageWindow(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(scene.getWindow());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showMessageWindow(String title, String message, boolean userChoice) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(scene.getWindow());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType buttonTypeYes = new ButtonType("Tak", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("Nie", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        return alert.showAndWait();
    }

    public static void showPrintWindowWithInvoke(boolean contractSelected, boolean protocolSelected, Rent rent, Function<Integer, Integer> invokeUpdate) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("print_window_invoke.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        PrintWindowCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        controller.setUpdate(invokeUpdate);
        controller.setSelection(contractSelected, protocolSelected);
        controller.setRent(rent);
        newWindow.setScene(scene);
        newWindow.show();
    }

    public static void showPrintWindow(boolean contractSelected, boolean protocolSelected, RentedRow rentedRow) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("print_window_invoke.fxml"));
        root = loader.load();
        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.setResizable(false);
        PrintWindowCtrl controller = loader.getController();
        scene = new Scene(root, controller.getRootVBox().getPrefWidth(), controller.getRootVBox().getPrefHeight());
        controller.setSelection(contractSelected, protocolSelected);
        controller.setRent(rentedRow);
        newWindow.setScene(scene);
        newWindow.show();
    }

}
