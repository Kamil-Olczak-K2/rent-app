package com.rentapp.util;

import com.rentapp.db_object.Accessory;
import com.rentapp.db_object.Rent;
import com.rentapp.gui.scene.LoginCtrl;
import com.rentapp.gui.scene.SceneCtrl;
import com.rentapp.table.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Year;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//TODO logging

public class DBQuery {
    public static ObservableList<RentedRow> queryAndMakeRentalTable() throws SQLException {
        String query = "SELECT k.imie_nazwisko, IFNULL(k.nazwa_firma, \"\") , s.model, s.id_sprzet, IFNULL(w.uwagi, \"\"), w.od, w.przew_data_zw, w.przez_kogo, w.id_klient, w.id_wypozyczenie, w.dlugoterminowe, w.za_d_netto, IFNULL(w.kaucja_brutto, \"0\"), k.pesel_nip, w.platnosc_kaucji, w.nr_wypozycz_klient, w.nr_wypozycz_klient_rok " +
                "FROM Wypozyczenie w " +
                "JOIN Klient k ON w.id_klient = k.id_klient " +
                "JOIN Wypozyczenie_sprzet ws ON w.id_wypozyczenie = ws.id_wypozyczenie " +
                "JOIN Sprzet s ON ws.id_sprzet = s.id_sprzet " +
                "WHERE ws.do IS NULL";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            return Table.makeRentalTable(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static ObservableList<RentedRow> queryAndMakeHistoryRentalTable() {
        String query = "SELECT k.imie_nazwisko, IFNULL(k.nazwa_firma, \"\") , s.model, s.id_sprzet, IFNULL(w.uwagi, \"\"), w.od, ws.do, w.przez_kogo, w.id_klient, w.id_wypozyczenie, w.dlugoterminowe, w.za_d_netto, IFNULL(w.kaucja_brutto, \"0\"), k.pesel_nip, w.platnosc_kaucji, w.nr_wypozycz_klient, w.nr_wypozycz_klient_rok " +
                "FROM Wypozyczenie w " +
                "JOIN Klient k ON w.id_klient = k.id_klient " +
                "JOIN Wypozyczenie_sprzet ws ON w.id_wypozyczenie = ws.id_wypozyczenie " +
                "JOIN Sprzet s ON ws.id_sprzet = s.id_sprzet " +
                "WHERE ws.do IS NOT NULL";
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            return Table.makeHistoryRentalTable(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static HashMap<String, String> queryAccessoriesNameAndUsage(String rentalID) {
        String query = "SELECT a.nazwa, IFNULL(wa.stopien_zuzycia,\"\") " +
                "FROM Akcesoria a " +
                "JOIN Wypozyczenie_akcesoria wa ON wa.id_akcesoria = a.id_akcesoria " +
                "WHERE wa.id_wypozyczenie = ?";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, rentalID);
            ResultSet resultSet = statement.executeQuery();
            HashMap<String, String> accessoriesNamesAndUsage = new HashMap<>();
            while (resultSet.next()) {
                accessoriesNamesAndUsage.put(resultSet.getString(1), resultSet.getString(2));
            }
            return accessoriesNamesAndUsage;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static ObservableList<ClientRow> queryAndMakeClientTable() throws SQLException {
        String query = "SELECT imie_nazwisko, IFNULL(nazwa_firma, \"\"), pesel_nip, adres, dw_osob, telefon, od, nr_umowy, nr_umowy_rok, id_klient, bez_kaucji " +
                "FROM Klient";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            return Table.makeClientTable(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static ObservableList<EquipRow> queryAndMakeEquipTable() throws SQLException {
        String query = "SELECT model, id_sprzet, nazwa, kaucja_brutto, za_d_netto, rok_produkcji, nr_seryjny, data_przegladu, przeglad_ilosc_motogodzin, wartosc_brutto, sprzedany, dostepny " +
                "FROM Sprzet";
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            return Table.makeEquipTable(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static ObservableList<EquipRow> queryAndMakeAvailableEquipTable() throws SQLException {
        String query = "SELECT model, id_sprzet, nazwa, kaucja_brutto, za_d_netto, rok_produkcji, nr_seryjny, data_przegladu, przeglad_ilosc_motogodzin, wartosc_brutto, sprzedany, dostepny " +
                "FROM Sprzet " +
                "WHERE dostepny = true AND sprzedany IS NULL";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            return Table.makeEquipTable(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static ObservableList<AccessoryRow> queryAndMakeAccessoryTable() throws SQLException {
        String query = "SELECT nazwa, cena_netto, zuzywalne, id_akcesoria, info " +
                "FROM Akcesoria";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            return Table.makeAccessoryTable(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static Map<Integer, Accessory> queryAccessories(String equipID) {
        String query = "SELECT a.id_akcesoria, a.nazwa, a.cena_netto, a.zuzywalne, sa.ilosc_akcesoria, a.info " +
                "FROM Akcesoria a " +
                "JOIN Sprzet_akcesoria sa ON sa.id_akcesoria = a.id_akcesoria " +
                "WHERE sa.id_sprzet = ?";
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, equipID);
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Accessory> accessories = new HashMap<>();
            while (resultSet.next()) {
                Accessory accessory = new Accessory(resultSet.getInt(1));
                accessory.setName(resultSet.getString(2));
                accessory.setPriceNett(resultSet.getDouble(3));
                accessory.setIsUsable(resultSet.getBoolean(4));
                accessory.setQuantity(resultSet.getInt(5));
                accessory.setInfo(resultSet.getString(6));
                accessories.put(accessory.getAccessoryID(), accessory);
            }
            return accessories;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static ClientRow queryClientData(String clientID) {
        String query = "SELECT imie_nazwisko, IFNULL(nazwa_firma, \"\"), pesel_nip, adres, dw_osob, telefon, od, nr_umowy, nr_umowy_rok, bez_kaucji " +
                "FROM Klient " +
                "WHERE id_klient = ?";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(clientID));
            ResultSet resultSet = statement.executeQuery();
            ClientRow clientRow = new ClientRow();
            resultSet.next();
            clientRow.setClientID(clientID);
            clientRow.setName(resultSet.getString(1));
            clientRow.setCompany(resultSet.getString(2));
            clientRow.setPeselNip(resultSet.getString(3));
            clientRow.setAdress(resultSet.getString(4));
            clientRow.setIdentityCard(resultSet.getString(5));
            clientRow.setPhoneNumber(resultSet.getString(6));
            clientRow.setFromWhen(resultSet.getString(7));
            clientRow.setContractNumber(resultSet.getString(8) + "/" + resultSet.getString(9).charAt(2) + resultSet.getString(9).charAt(3));
            clientRow.setDepositFree(resultSet.getBoolean(10));

            return clientRow;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            return null;
        }
    }

    public static Map<Integer, EquipRow> queryRentedEquip(String rentalID) {
        String query = "SELECT s.model, s.id_sprzet, s.nazwa, s.kaucja_brutto, s.za_d_netto, s.rok_produkcji, s.nr_seryjny, s.data_przegladu, s.przeglad_ilosc_motogodzin, s.wartosc_brutto " +
                "FROM Sprzet s " +
                "JOIN Wypozyczenie_sprzet ws ON ws.id_sprzet = s.id_sprzet " +
                "WHERE ws.id_wypozyczenie = ?";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(rentalID));
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, EquipRow> rentedIDEquip = new HashMap<>();
            while (resultSet.next()) {
                EquipRow equipRow = new EquipRow();
                equipRow.setModel(resultSet.getString(1));
                equipRow.setEquipmentID(resultSet.getString(2));
                equipRow.setName(resultSet.getString(3));
                equipRow.setDeposit(resultSet.getString(4));
                equipRow.setPerDayNet(resultSet.getString(5));
                equipRow.setManufactureYear(resultSet.getString(6));
                equipRow.setSn(resultSet.getString(7));
                equipRow.setReviewDate(resultSet.getString(8));
                equipRow.setMotohours(resultSet.getString(9));
                equipRow.setEquipValueGross(resultSet.getInt(10));
                rentedIDEquip.put(Integer.parseInt(equipRow.getEquipmentID()), equipRow);
            }
            return rentedIDEquip;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return null;
    }

    public static Map<Integer, Accessory> queryRentedAccessory(String rentalID) {
        String query = "SELECT a.id_akcesoria, a.nazwa, IFNULL(wa.cena_netto, a.cena_netto), a.zuzywalne, a.info " +
                "FROM Akcesoria a " +
                "JOIN Wypozyczenie_akcesoria wa ON wa.id_akcesoria = a.id_akcesoria " +
                "WHERE wa.id_wypozyczenie = ?";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(rentalID));
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Accessory> rentedIDAccessory = new HashMap<>();
            while (resultSet.next()) {
                Accessory accessory = new Accessory(resultSet.getInt(1));
                accessory.setName(resultSet.getString(2));
                accessory.setPriceNett(resultSet.getDouble(3));
                accessory.setIsUsable(resultSet.getBoolean(4));
                accessory.setInfo(resultSet.getString(5));
                rentedIDAccessory.put(accessory.getAccessoryID(), accessory);
            }
            return rentedIDAccessory;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return null;
    }

    public static String queryRentedEquipPlaceOfWork(String rentalID) {
        String query = "SELECT miejsce_pracy " +
                "FROM Wypozyczenie " +
                "WHERE id_wypozyczenie = ?";
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(rentalID));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return null;
    }


    public static Map<Integer, Integer> queryRentedAccessoryQty(String rentalID) {
        String query = "SELECT id_akcesoria, ilosc " +
                "FROM Wypozyczenie_akcesoria " +
                "WHERE id_wypozyczenie = ?";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(rentalID));
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Integer> rentedIDAccessoryCount = new HashMap<>();
            while (resultSet.next()) {
                rentedIDAccessoryCount.put(resultSet.getInt(1), resultSet.getInt(2));
            }
            return rentedIDAccessoryCount;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return null;
    }


    public static String queryNewContractNumber() {

        if (checkForNewYear()) {
            return "1";
        } else {
            try {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                String querry = "SELECT MAX(nr_umowy) " +
                        "FROM Klient " +
                        "WHERE nr_umowy_rok = ?";
                statement = DBConnection.getConnection().prepareStatement(querry);

                statement.setString(1, Year.now().toString());
                resultSet = statement.executeQuery();
                resultSet.next();
                return resultSet.getInt(1) + 1 + "";
            } catch (SQLException e) {
                e.printStackTrace();
                Logger.logExToFile(e);
            }
        }
        return null;
    }

    public static int queryNewRentalID() {
        try {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            String querry = "SELECT MAX(id_wypozyczenie) " +
                    "FROM Wypozyczenie";
            statement = DBConnection.getConnection().prepareStatement(querry);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) + 1;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return -1;
    }

    public static int queryClientNewRentID(String clientID) {
        String querry = "SELECT COUNT(1)" +
                "FROM Wypozyczenie " +
                "WHERE id_klient = ? AND nr_wypozycz_klient_rok = ?";
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(querry)) {
            ResultSet resultSet = null;
            String year = Year.now().toString();
            statement.setString(1, clientID);
            statement.setInt(2, Integer.parseInt(year));
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) + 1;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return -1;
    }

    public static boolean checkEquipConstraints(String equipID) {
        String query = "SELECT COUNT(1) " +
                "FROM Sprzet " +
                "WHERE id_sprzet = ?";
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, equipID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return false;
    }

    public static int querryNewEquipID() {
        String query = "SELECT t1.id_sprzet + 1 AS missing_id " +
                "FROM Sprzet t1 " +
                "LEFT JOIN Sprzet t2 ON t1.id_sprzet + 1 = t2.id_sprzet " +
                "WHERE t2.id_sprzet IS NULL " +
                "  AND t1.id_sprzet < (SELECT MAX(id_sprzet) FROM Sprzet) " +
                "ORDER BY t1.id_sprzet";

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                query = "SELECT MAX(id_sprzet) " +
                        "FROM Sprzet";
                try (PreparedStatement statement0 = DBConnection.getConnection().prepareStatement(query)) {
                    resultSet = statement0.executeQuery();
                    resultSet.next();
                    return resultSet.getInt(1) + 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return -1;
    }

    public static boolean checkForNewYear() {
        String querry = "SELECT COUNT(1) " +
                "FROM Klient " +
                "WHERE nr_umowy_rok = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            try {
                statement = DBConnection.getConnection().prepareStatement(querry);

                statement.setString(1, Year.now().toString());

                resultSet = statement.executeQuery();
                resultSet.next();
                if (resultSet.getInt(1) == 0) {
                    return true;
                }
            } finally {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }

        return false;
    }

    public static boolean checkClientStatus(int clientID) {
        try {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            String query = "SELECT id_klient " +
                    "FROM Klient " +
                    "WHERE id_klient NOT IN ( " +
                    "SELECT id_klient FROM Wypozyczenie)";
            statement = DBConnection.getConnection().prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) == (clientID)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return false;
    }


    public static String addClient(ClientRow client) {
        String query = "INSERT INTO Klient (imie_nazwisko, pesel_nip, adres, dw_osob, telefon, nazwa_firma, od, nr_umowy_rok, nr_umowy, bez_kaucji) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            statement.setString(1, client.getName());
            statement.setString(2, client.getPeselNip());
            statement.setString(3, client.getAdress());
            statement.setString(4, client.getIdentityCard());
            statement.setString(5, client.getPhoneNumber());
            statement.setString(6, client.getCompany());
            statement.setDate(7, Date.valueOf(client.getFromWhen()));
            statement.setInt(8, Year.now().getValue());
            statement.setInt(9, client.getContractNumberInt());
            statement.setBoolean(10, client.isDepositFree());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    connection.commit();
                    return generatedKeys.getString(1);
                } else {
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return null;
    }

    public static boolean deleteClient(ClientRow clientRow) throws SQLException {
        String[] queries = {"DELETE FROM Wypozyczenie_sprzet " +
                "WHERE id_wypozyczenie IN (SELECT id_wypozyczenie " +
                "FROM Wypozyczenie " +
                "WHERE id_klient = ?)",

                "DELETE FROM Wypozyczenie_akcesoria " +
                        "WHERE id_wypozyczenie IN (SELECT id_wypozyczenie " +
                        "FROM Wypozyczenie " +
                        "WHERE id_klient = ?)",

                "DELETE FROM Wypozyczenie " +
                        "WHERE id_klient = ?",

                "DELETE FROM Klient " +
                        "WHERE id_klient = ?"};

        Connection connection = DBConnection.getConnection();
        connection.setAutoCommit(false);

        for (String query : queries) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, clientRow.getClientIDInt());
                statement.executeUpdate();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    SceneCtrl.showMessageWindow("Błąd", "Nie udało się usunąć klienta. Błąd edycji tabeli klientów, spróbuj ponownie.");
                }
                e.printStackTrace();
                Logger.logExToFile(e);
                Logger.logExToFile(e);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się usunąć klienta. Błąd połączenia z bazą danych, spróbuj ponownie.");
                return false;
            }
        }
        connection.commit();
        return true;
    }

    public static boolean manageEquip(EquipRow equipRow, boolean update) throws SQLException {
        String equipQuery;
        if (update) {
            equipQuery = "UPDATE Sprzet " +
                    "SET nazwa = ?, model = ?, za_d_netto = ?, kaucja_brutto = ?, rok_produkcji = ?, nr_seryjny = ?, wartosc_brutto = ?, data_przegladu = ?, przeglad_ilosc_motogodzin = ?, sprzedany = ?, dostepny = ?, w_serwisie = ? " +
                    "WHERE id_sprzet = ?";
        } else {
            equipQuery = "INSERT INTO Sprzet (id_sprzet, nazwa, model, za_d_netto, kaucja_brutto, rok_produkcji, nr_seryjny, wartosc_brutto, data_przegladu, przeglad_ilosc_motogodzin, sprzedany, dostepny, w_serwisie) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        String accQuery = "INSERT INTO Sprzet_akcesoria (id_sprzet, id_akcesoria, ilosc_akcesoria) " +
                "VALUES (?, ?, ?)";
        String deleteAccQuery = "DELETE FROM Sprzet_akcesoria " +
                "WHERE id_sprzet = ?";
        Connection connection = DBConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(equipQuery)) {
            connection.setAutoCommit(false);
            statement.setInt(1, equipRow.getEquipmentIDInt());
            statement.setString(2, equipRow.getName());
            statement.setString(3, equipRow.getModel());
            statement.setDouble(4, equipRow.getPerDayNetDouble());
            statement.setInt(5, equipRow.getDepositGrossInt());
            statement.setObject(6, equipRow.getManufactureYear().isEmpty() ? null : Integer.parseInt(equipRow.getManufactureYear()), Types.INTEGER);
            statement.setString(7, equipRow.getSn().isEmpty() ? null : equipRow.getSn());
            statement.setInt(8, equipRow.getEquipValueGrossInt());
            statement.setDate(9, equipRow.getReviewDate().isEmpty() ? null : equipRow.getReviewDateSQL());
            statement.setObject(10, equipRow.getMotohours().isEmpty() ? null : Integer.parseInt(equipRow.getMotohours()), Types.INTEGER);
            statement.setDate(11, null);
            statement.setBoolean(12, true);
            statement.setBoolean(13, false);
            statement.executeUpdate();

            if (update) {
                try (PreparedStatement statement0 = connection.prepareStatement(deleteAccQuery)) {
                    statement0.setInt(1, equipRow.getEquipmentIDInt());
                    statement0.executeUpdate();
                }
            }

            try (PreparedStatement statement0 = connection.prepareStatement(accQuery)) {
                Map<Integer, Accessory> iDAccessory = equipRow.getIDAccessory();
                Map<Integer, Integer> iDAccQty = equipRow.getAccessoryIDQty();
                for (int accID : iDAccessory.keySet()) {
                    statement0.setInt(1, equipRow.getEquipmentIDInt());
                    statement0.setInt(2, accID);
                    statement0.setInt(3, iDAccQty.get(accID));
                    statement0.executeUpdate();
                }
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                Logger.logExToFile(e1);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się dodać sprzętu. Błąd modyfikacji bazy sprzętu, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się zmodyfikować sprzętu. Błąd połączenia z bazą danych, spóbuj ponownie.");
        }
        return false;
    }

    public static boolean editClient(ClientRow clientRow) throws SQLException {
        String querry = "UPDATE Klient " +
                "SET imie_nazwisko = ?, pesel_nip = ?, adres = ?, dw_osob = ?, telefon = ?, nazwa_firma = ?, od = ?, nr_umowy = ?, bez_kaucji = ? " +
                "WHERE id_klient = ?";

        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(querry)) {
            connection.setAutoCommit(false);
            statement.setString(1, clientRow.getName());
            statement.setString(2, clientRow.getPeselNip());
            statement.setString(3, clientRow.getAdress());
            statement.setString(4, clientRow.getIdentityCard());
            statement.setString(5, clientRow.getPhoneNumber());
            statement.setString(6, clientRow.getCompany());
            statement.setString(7, clientRow.getFromWhen());
            statement.setInt(8, clientRow.getContractNumberInt());
            statement.setBoolean(9, clientRow.isDepositFree());
            statement.setInt(10, clientRow.getClientIDInt());
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            Logger.logExToFile(e);
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się zaktualizować klienta. Błąd połączenia z bazą danych");
        }
        return false;
    }

    public static boolean deleteEquip(EquipRow equipRow) {
        String query0 = "DELETE FROM Wypozyczenie_sprzet " +
                "WHERE id_sprzet = ?";
        String query1 = "DELETE FROM Sprzet_akcesoria " +
                "WHERE id_sprzet = ?";
        String query2 = "DELETE FROM Sprzet " +
                "WHERE id_sprzet = ?";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement0 = connection.prepareStatement(query0)) {
            statement0.setInt(1, equipRow.getEquipmentIDInt());
            statement0.executeUpdate();
            try (PreparedStatement statement1 = connection.prepareStatement(query1)) {
                statement1.setInt(1, equipRow.getEquipmentIDInt());
                statement1.executeUpdate();
                try (PreparedStatement statement2 = connection.prepareStatement(query2)) {
                    statement2.setInt(1, equipRow.getEquipmentIDInt());
                    statement2.executeUpdate();
                    connection.commit();
                    return true;
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                Logger.logExToFile(e1);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się usunąć sprzętu. Błąd modyfikacji bazy sprzętu, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się usunąć sprzętu. Błąd połączenia z bazą danych, spróbuj ponownie.");
        }
        return false;
    }

    public static boolean markSold(EquipRow equipRow) throws SQLException {
        String query = "UPDATE Sprzet " +
                "SET sprzedany = NOW(), dostepny = false " +
                "WHERE id_sprzet = ?";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            statement.setInt(1, equipRow.getEquipmentIDInt());
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                Logger.logExToFile(e1);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się oznaczyć sprzętu jako sprzedany. Błąd modyfikacji tabeli sprzętu, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się zaktualizować sprzętu. Błąd połączenia z bazą danych, spróbuj ponownie.");
        }
        return false;
    }

    public static boolean addAccessory(Accessory accessory) {
        String query = "INSERT INTO Akcesoria (nazwa, cena_netto, zuzywalne, info) " +
                "VALUES (?, ?, ?, ?)";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, accessory.getName());
            statement.setDouble(2, accessory.getPriceNettDouble());
            statement.setBoolean(3, accessory.isUsable());
            statement.setString(4, accessory.getInfo());
            statement.executeUpdate();

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.logExToFile(ex);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się dodać akcesorium. Błąd modyfikacji tabeli akcesoriów, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się dodać akcesorium. Błąd połączenia z bazą danych");
        }
        return false;
    }

    public static boolean updateAccessory(Accessory accessory) {
        String query = "UPDATE Akcesoria " +
                "SET nazwa = ?, cena_netto = ?, zuzywalne = ?, info = ? " +
                "WHERE id_akcesoria = ?";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            statement.setString(1, accessory.getName());
            statement.setDouble(2, accessory.getPriceNettDouble());
            statement.setBoolean(3, accessory.isUsable());
            statement.setString(4, accessory.getInfo());
            statement.setInt(5, accessory.getAccessoryID());
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.logExToFile(ex);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się zaktualizować akcesorium. Błąd modyfikacji tabeli akcesoriów, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się zaktualizować akcesorium. Błąd połączenia z bazą danych");
        }
        return false;
    }

    public static boolean deleteAccessory(int accID) {
        String query0 = "DELETE FROM Wypozyczenie_akcesoria " +
                "WHERE id_akcesoria = ?";
        String query1 = "DELETE FROM Akcesoria " +
                "WHERE id_akcesoria = ?";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query0)) {
            connection.setAutoCommit(false);
            statement.setString(1, accID + "");
            statement.executeUpdate();
            try (PreparedStatement statement0 = DBConnection.getConnection().prepareStatement(query1)) {
                statement0.setString(1, accID + "");
                statement0.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.logExToFile(ex);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się usunąć akcesorium. Błąd modyfikacji tabeli akcesoriów, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się usunąć akcesorium. Błąd połączenia z bazą danych");
        }
        return false;
    }

    public static boolean addRent(Rent rent) {
        String updateRentTable = "INSERT INTO Wypozyczenie (od, Id_klient, uwagi, przez_kogo, przew_data_zw, kaucja_brutto, za_d_netto, nr_wypozycz_klient, nr_wypozycz_klient_rok, dlugoterminowe, miejsce_pracy, platnosc_kaucji) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = DBConnection.getConnection();
        try {
            try (PreparedStatement statement = connection.prepareStatement(updateRentTable);) {
                connection.setAutoCommit(false);

                statement.setTimestamp(1, rent.getFromWhen());
                statement.setInt(2, rent.getClientID());
                statement.setString(3, rent.getNote());
                statement.setString(4, LoginCtrl.getLoggedUser());
                statement.setDate(5, rent.getProbableToWhen());
                statement.setInt(6, rent.getDepositGross());
                statement.setDouble(7, rent.getTotPerDayNett());
                statement.setInt(8, rent.getClientRentID());
                statement.setInt(9, Integer.parseInt(Year.now().toString()));
                statement.setBoolean(10, rent.isLongTerm());
                statement.setString(11, rent.getPlaceOfWork());
                statement.setString(12, rent.getDepositPayment());
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                Logger.logExToFile(e);
                return false;
            }

            Set<Integer> equipIDs = rent.getIDEquip().keySet();
            for (int iD : equipIDs) {
                String addToRent = "UPDATE Sprzet " +
                        "SET dostepny = false " +
                        "WHERE id_sprzet = ?";
                try (PreparedStatement statement = connection.prepareStatement(addToRent);) {
                    statement.setInt(1, iD);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Logger.logExToFile(e);
                    return false;
                }

                addToRent = "INSERT INTO Wypozyczenie_sprzet (id_wypozyczenie, id_sprzet, do) " +
                        "VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(addToRent);) {
                    statement.setInt(1, rent.getRentalID());
                    statement.setInt(2, iD);
                    statement.setTimestamp(3, null);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Logger.logExToFile(e);
                    return false;
                }
            }

            Map<Integer, Accessory> iDAccessory = rent.getIDAccessory();
            Map<Integer, Integer> iDAccQty = rent.getAccessoryIDQty();
            String addToRent = "INSERT INTO Wypozyczenie_akcesoria " +
                    "(id_wypozyczenie, id_akcesoria, stopien_zuzycia, ilosc) " +
                    "VALUES (?, ?, ?, ?)";
            for (int iD : iDAccessory.keySet()) {
                try (PreparedStatement statement = connection.prepareStatement(addToRent);) {
                    statement.setInt(1, rent.getRentalID());
                    statement.setInt(2, iD);
                    if (iDAccessory.get(iD).isUsable()) {
                        statement.setString(3, iDAccessory.get(iD).getUsage());
                    } else {
                        statement.setString(3, null);
                    }
                    statement.setInt(4, iDAccQty.get(iD));
                    statement.executeUpdate();


                } catch (SQLException e) {
                    e.printStackTrace();
                    Logger.logExToFile(e);
                    return false;
                }
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.logExToFile(ex);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się dodać wypożyczenia. Błąd modyfikacji tabeli wypożyczeń, spróbuj ponownie.");
                return false;
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się dodać wypożyczenia. Błąd połączenia z bazą danych");
            return false;
        }


    }

    public static void setReturned(String equipmentID) {

        String[] queries = {"UPDATE Wypozyczenie_sprzet SET do = NOW() WHERE id_sprzet = ?",
                "UPDATE Sprzet SET dostepny = true WHERE id_sprzet = ?"};

        Connection connection = DBConnection.getConnection();
        try {
            connection.setAutoCommit(false);
            for (String query : queries) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, equipmentID);
                    statement.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.logExToFile(ex);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się zwrócić sprzętu. Błąd modyfikacji tabeli wypożyczeń, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się zwrócić sprzętu. Błąd połączenia z bazą danych, spróbuj ponownie.");
        }
    }

    public static boolean updateDatabaseUser(byte[] login, byte[] password) {
        String query = "ALTER USER ?@'%' IDENTIFIED BY ?";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            statement.setString(1, new String(login));
            statement.setString(2, new String(password));
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.logExToFile(ex);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się zaktualizować użytkownika. Błąd modyfikacji użytkowników, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się zaktualizować użytkownika. Błąd połączenia z bazą danych");
        } finally {
            Arrays.fill(login, (byte) 0);
            Arrays.fill(password, (byte) 0);
        }
        return false;
    }

    public static boolean addUser(byte[] userName, byte[] password, String role) {
        String query = "CREATE USER ?@'%' IDENTIFIED BY ?";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            statement.setString(1, new String(userName));
            statement.setString(2, new String(password));
            statement.executeUpdate();
            if (role.equals("admin")) {
                query = "GRANT ALTER, DELETE, DROP, GRANT OPTION, INSERT, SELECT, UPDATE ON rent.* TO ?@'%'";
                try (PreparedStatement statement0 = connection.prepareStatement(query)) {
                    statement0.setString(1, new String(userName));
                    statement0.executeUpdate();
                }
                query = "GRANT CREATE USER ON *.* TO ?@'%'";
                try (PreparedStatement statement0 = connection.prepareStatement(query)) {
                    statement0.setString(1, new String(userName));
                    statement0.executeUpdate();
                }
            } else {
                query = "GRANT INSERT, SELECT, UPDATE ON rent.* TO ?@'%'";
                try (PreparedStatement statement0 = connection.prepareStatement(query)) {
                    statement0.setString(1, new String(userName));
                    statement0.executeUpdate();
                }
            }
            query = "INSERT INTO Uzytkownik (nazwa) VALUES (?)";
            try (PreparedStatement statement0 = connection.prepareStatement(query)) {
                statement0.setString(1, new String(userName));
                statement0.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.logExToFile(ex);
                SceneCtrl.showMessageWindow("Błąd", "Nie udało się dodać użytkownika. Błąd modyfikacji użytkowników, spróbuj ponownie.");
            }
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się dodać użytkownika. Błąd połączenia z bazą danych");
        } finally {
            Arrays.fill(userName, (byte) 0);
            Arrays.fill(password, (byte) 0);
        }
        return false;
    }

    public static boolean deleteUser(byte[] userName) {
        String query = "DROP USER ?@'%'";
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, new String(userName));
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się usunąć użytkownika. Błąd połączenia z bazą danych");
        }
        return false;
    }

    public static ObservableList<UserRow> queryUserNames() {
        String query = "SELECT nazwa FROM Uzytkownik";
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            ObservableList<UserRow> userNames = FXCollections.observableArrayList();
            while (resultSet.next()) {
                userNames.add(new UserRow(resultSet.getString(1)));
            }
            return userNames;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.logExToFile(e);
        }
        return null;
    }


}
