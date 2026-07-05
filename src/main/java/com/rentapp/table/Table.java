package com.rentapp.table;

import com.rentapp.db_object.Accessory;
import com.rentapp.util.Calculate;
import com.rentapp.util.Credentials;
import com.rentapp.util.DBQuery;
import com.rentapp.db_object.Rent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Table {

    public static ObservableList<RentedRow> makeRentalTable(ResultSet resultSet) throws SQLException {

        if (resultSet != null) {
            ObservableList<RentedRow> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                RentedRow row = new RentedRow();
//                SELECT k.imie_nazwisko, IFNULL(k.nazwa_firma, "") , s.model, s.id_sprzet, IFNULL(w.uwagi, ""),
//                w.od, w.przew_data_zw, w.przez_kogo, w.id_klient, w.id_wypozyczenie, w.dlugoterminowe, w.za_d_netto, w.kaucja_brutto, k.peselNip, w.kaucja_platnosc
                row.setClient(resultSet.getString(1));
                row.setCompany(resultSet.getString(2));
                row.setEquipment(resultSet.getString(3));
                row.setEquipmentID(resultSet.getString(4));
                row.setFromWhen(resultSet.getString(6));
                row.setProbableToWhen(resultSet.getString(7));
                row.setByWhoColumn(resultSet.getString(8));
                row.setClientID(resultSet.getString(9));
                row.setRentalID(resultSet.getString(10));
                if(resultSet.getBoolean(11)){
                    row.setLongTerm("Tak");
                    row.setPriceGross(Calculate.calculateGrossString(resultSet.getString(12)) + " zł / miesiąc");
                } else{
                    row.setLongTerm("Nie");
                    row.setPriceGross(Calculate.calculateGrossString(resultSet.getString(12)) + " zł / dzień");
                }
                row.setDepositGross(resultSet.getString(13) + " zł");
                row.setPeselNip(resultSet.getString(14));
                row.setAccessories(Objects.requireNonNull(DBQuery.queryAccessoriesNameAndUsage(row.getRentalID())));
                row.setNotes(resultSet.getString(5));
                row.setDepositPayment(resultSet.getString(15));
                row.setClientRentalID(resultSet.getInt(16));
                row.setClientRentalIDYear(resultSet.getInt(17));
                data.add(row);
            }
            return data;
        } else {
            return null;
        }
    }

    public static ObservableList<RentedRow> makeHistoryRentalTable(ResultSet resultSet) throws SQLException {

        if (resultSet != null) {
            ObservableList<RentedRow> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                RentedRow row = new RentedRow();
                row.setClient(resultSet.getString(1));
                row.setCompany(resultSet.getString(2));
                row.setEquipment(resultSet.getString(3));
                row.setEquipmentID(resultSet.getString(4));
                row.setFromWhen(resultSet.getString(6));
                row.setToWhen(resultSet.getString(7));
                row.setByWhoColumn(resultSet.getString(8));
                row.setClientID(resultSet.getString(9));
                row.setRentalID(resultSet.getString(10));
                if(resultSet.getBoolean(11)){
                    row.setLongTerm("Tak");
                    row.setPriceGross(Calculate.calculateGrossString(resultSet.getString(12)) + " zł / miesiąc");
                } else {
                    row.setLongTerm("Nie");
                    row.setPriceGross(Calculate.calculateGrossString(resultSet.getString(12)) + " zł / dzień");
                }
                row.setDepositGross(resultSet.getString(13) + " zł");
                row.setPeselNip(resultSet.getString(14));
                row.setAccessories(Objects.requireNonNull(DBQuery.queryAccessoriesNameAndUsage(row.getRentalID())));
                row.setNotes(resultSet.getString(5));
                row.setDepositPayment(resultSet.getString(15));
                row.setClientRentalID(resultSet.getInt(16));
                row.setClientRentalIDYear(resultSet.getInt(17));
                data.add(row);
            }
            return data;
        } else {
            return null;
        }
    }

    public static ObservableList<ClientRow> makeClientTable(ResultSet resultSet) throws SQLException {

        if (resultSet != null){
            ObservableList<ClientRow> data = FXCollections.observableArrayList();
            while (resultSet.next()){
                ClientRow row = new ClientRow();
                row.setName(resultSet.getString(1));
                row.setCompany(resultSet.getString(2));
                row.setPeselNip(resultSet.getString(3));
                row.setAdress(resultSet.getString(4));
                row.setIdentityCard(resultSet.getString(5));
                row.setPhoneNumber(resultSet.getString(6));
                row.setFromWhen(resultSet.getString(7));
                row.setContractNumber(resultSet.getString(8), resultSet.getString(9));
                row.setClientID(resultSet.getString(10));
                row.setDepositFree(resultSet.getBoolean(11));
                data.add(row);
            }
            return data;
        } else {
            return null;
        }

    }

    public static ObservableList<EquipRow> makeEquipTable(ResultSet resultSet) throws SQLException {
        if (resultSet != null){
            ObservableList<EquipRow> data = FXCollections.observableArrayList();
            while (resultSet.next()){
                EquipRow row = new EquipRow();
                row.setModel(resultSet.getString(1));
                row.setEquipmentID(resultSet.getString(2));
                row.setName(resultSet.getString(3));
                row.setDeposit(resultSet.getString(4));
                row.setPerDayNet(resultSet.getString(5));
                row.setManufactureYear(resultSet.getString(6));
                row.setSn(resultSet.getString(7));
                row.setReviewDate(resultSet.getString(8));
                row.setMotohours(resultSet.getString(9));
                row.setEquipValueGross(resultSet.getInt(10));
                row.setSold(resultSet.getString(11));
                row.setAvailable(resultSet.getBoolean(12));
                row.setAccessories();
                data.add(row);
            }
            return data;
        } else {
            return null;
        }
    }

    public static ObservableList<AccessoryRow> makeAccessoryTable(ResultSet resultSet) throws SQLException {
        if (resultSet != null){
            ObservableList<AccessoryRow> data = FXCollections.observableArrayList();
            while (resultSet.next()){
//                "SELECT nazwa, cena_netto, zuzywalne, id_akcesoria, " +
                AccessoryRow row = new AccessoryRow();
                row.setName(resultSet.getString(1));
                row.setPriceNett(resultSet.getString(2));
                if(resultSet.getBoolean(3)){
                    row.setUsable("Tak");
                }
                row.setAccessoryID(resultSet.getInt(4));
                row.setInfo(resultSet.getString(5));
                data.add(row);
            }
            return data;
        } else {
            return null;
        }
    }

    public static ObservableList<ToRentRow> updateRentedEquipTable(Rent rent){
        ObservableList<ToRentRow> data = FXCollections.observableArrayList();
        ToRentRow row = null;
        List<EquipRow> equipment = rent.getEquip();
        Map<Integer, Accessory> iDAccessory = new HashMap<>();
        Map<Integer, Integer> accessoryIDQty = new HashMap<>();
        for(EquipRow equip : equipment){
            row = new ToRentRow();
            row.setModel(equip.getModel());
            row.setEquipID(equip.getEquipmentID());
            row.setName(equip.getName());
            row.setPerDayNett(Calculate.addZeroIfTenthsEqual(equip.getPerDayNet()));
            row.setPerDayGross(Calculate.calculateGrossString(equip.getPerDayNetDouble()));
            row.setType("equip");
            data.add(row);

//            iDAccessory = equip.getIDAccessory();
//            accessoryIDQty = rent.getAccessoryIDQty();
//            for(Accessory accessory : iDAccessory.values()){
//                row = new ToRentRow();
//                row.setName(accessory.getName());
//                row.setAccessoryID(accessory.getAccessoryIDString());
//                row.setPerDayNett(Calculate.addZeroIfTenthsEqual(accessory.getPriceNettString()));
//                row.setPerDayGross(Calculate.calculateGrossString(accessory.getPriceNettDouble()));
//                if(accessoryIDQty.containsKey(accessory.getAccessoryID())){
//                     row.setQuantity(accessoryIDQty.get(accessory.getAccessoryID()));
//                     row.setType("accessory");
//                     data.add(row);
//                }
//            }
        }
        iDAccessory = rent.getIDAccessory();
        accessoryIDQty = rent.getAccessoryIDQty();
        for(Accessory accessory : iDAccessory.values()){
            row = new ToRentRow();
            row.setAccessoryID(accessory.getAccessoryIDString());
            row.setType("accessory");
            if(!data.contains(row)){
                row.setName(accessory.getName());
                row.setPerDayNett(Calculate.addZeroIfTenthsEqual(accessory.getPriceNettString()));
                row.setPerDayGross(Calculate.calculateGrossString(accessory.getPriceNettDouble()));
                if(accessoryIDQty.containsKey(accessory.getAccessoryID())){
                    row.setQuantity(accessoryIDQty.get(accessory.getAccessoryID()));
                    data.add(row);
                }
            }
        }
        return data;
    }

    public static ObservableList<UserRow> updateUserTable(ObservableList<UserRow> obList){
        ObservableList<UserRow> data = FXCollections.observableArrayList();
        for(UserRow row : obList){
            row.setUserRole(Credentials.getUserRole(row.getUserName().getBytes()));
            data.add(row);
        }
        return data;
    }

    public static TableView<RentedRow> searchHistoryRentalTable(TableView<RentedRow> table, String search, String column){
        ObservableList<RentedRow> tableToSearch = table.getItems();
        ObservableList<RentedRow> filteredTable = FXCollections.observableArrayList();
        for (RentedRow row : tableToSearch){
            if (row.get(column).toLowerCase().contains(search.toLowerCase())){
                filteredTable.add(row);
            }
        }
        table.getItems().clear();
        table.getItems().addAll(filteredTable);
        return table;
    }

    public static TableView<ClientRow> searchClientTable(TableView<ClientRow> table, String search, String column){
        ObservableList<ClientRow> tableToSearch = table.getItems();
        ObservableList<ClientRow> filteredTable = FXCollections.observableArrayList();
        for (ClientRow row : tableToSearch){
            if (row.get(column).toLowerCase().contains(search.toLowerCase())){
                filteredTable.add(row);
            }
        }
        table.getItems().clear();
        table.getItems().addAll(filteredTable);
        return table;
    }
    public static TableView<ClientRow> searchClientTable(TableView<ClientRow> table, String search, List<String> columns){
        ObservableList<ClientRow> tableToSearch = table.getItems();
        ObservableList<ClientRow> filteredTable = FXCollections.observableArrayList();
        for (ClientRow row : tableToSearch){
            for (String column : columns){
                if (row.get(column).toLowerCase().contains(search.toLowerCase())) {
                    filteredTable.add(row);
                    break;
                }
            }
        }
        table.getItems().clear();
        table.getItems().addAll(filteredTable);
        return table;
    }


    public static TableView<EquipRow> searchEquipTable(TableView<EquipRow> table, String search, String column){
        ObservableList<EquipRow> tableToSearch = table.getItems();
        ObservableList<EquipRow> filteredTable = FXCollections.observableArrayList();
        for (EquipRow row : tableToSearch){
            if (row.get(column).toLowerCase().contains(search.toLowerCase())){
                filteredTable.add(row);
            }
        }
        table.getItems().clear();
        table.getItems().addAll(filteredTable);
        return table;
    }

    public static TableView<EquipRow> searchEquipTable(TableView<EquipRow> table, String search, List<String> columns){
        ObservableList<EquipRow> tableToSearch = table.getItems();
        ObservableList<EquipRow> filteredTable = FXCollections.observableArrayList();
        for (EquipRow row : tableToSearch) {
            for (String column : columns) {
                if (row.get(column).toLowerCase().contains(search.toLowerCase())) {
                    filteredTable.add(row);
                    break;
                }
            }
        }
        table.getItems().clear();
        table.getItems().addAll(filteredTable);
        return table;
    }

    public static TableView<AccessoryRow> searchAccessoryTable(TableView<AccessoryRow> table, String search, String column){
        ObservableList<AccessoryRow> tableToSearch = table.getItems();
        ObservableList<AccessoryRow> filteredTable = FXCollections.observableArrayList();
        for (AccessoryRow row : tableToSearch){
            if (row.get(column).toLowerCase().contains(search.toLowerCase())){
                filteredTable.add(row);
            }
        }
        table.getItems().clear();
        table.getItems().addAll(filteredTable);
        return table;
    }
}
