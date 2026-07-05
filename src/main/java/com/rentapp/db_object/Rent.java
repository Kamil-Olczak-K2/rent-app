package com.rentapp.db_object;

import com.rentapp.table.ClientRow;
import com.rentapp.table.EquipRow;
import com.rentapp.util.Calculate;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Rent{
    private int rentalID;
    private ClientRow client;

    private Map<Integer, EquipRow> iDEquip;
    private Map<Integer, Accessory> iDAccessory;
    private Map<Integer, Integer> accessoryIDQty;
    private LocalDateTime fromWhen;
    private LocalDate probableToWhen;

    private int clientID;
    private double totPerDayNett;
    private double totPerDayGross;
    private int depositGross;
    private int clientRentID;
    private String clientRentIDPretty;
    private String note;
    private String placeOfWork;
    private String depositPayment;
    private boolean longTerm = false;
    private boolean isNewClient = false;


    public Rent(int rentalID){
        this.rentalID = rentalID;
        this.iDEquip = new HashMap<>();
        this.iDAccessory = new HashMap<>();
        this.accessoryIDQty = new HashMap<>();
        this.fromWhen = LocalDateTime.now();
        this.probableToWhen = LocalDate.parse(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.clientID = 0;
        this.totPerDayNett = 0;
        this.totPerDayGross = 0;
        this.depositGross = 0;
        this.clientRentID = 0;
        this.clientRentIDPretty = "";
        note = "";
        placeOfWork = "";
    }

    public String[] getRentInfoPDF(){
        String [] info = new String[]{"","","","","","","","","","","","","","","","",""};
        if (iDEquip.size() == 1) {
            int value= 0;
            for (EquipRow equipRow : iDEquip.values()) {
                info[0] += equipRow.getName();
                info[1] += equipRow.getModel();
                info[2] += equipRow.getSn();
                info[3] += equipRow.getManufactureYear();
                value += equipRow.getEquipValueGrossInt();
                info[5] += equipRow.getEquipmentID();
                if(equipRow.getReviewDate().isEmpty()){
                    info[6] = "aktualny";
                } else {
                    info[6] = equipRow.getReviewDate();
                }
            }
            info[4] = value + " zł";
        } else {
            int value= 0;
            for (EquipRow equipRow : iDEquip.values()) {
                info[0] += equipRow.getShortName() + ", ";
                info[1] += equipRow.getModel() + ", ";
                info[2] += equipRow.getSn() + ", ";
                info[3] += equipRow.getManufactureYear() + ", ";
                value += equipRow.getEquipValueGrossInt();
                info[5] += equipRow.getEquipmentID() + ", ";
                if(equipRow.getReviewDate().isEmpty()){
                    info[6] = "aktualny";
                } else {
                    info[6] = equipRow.getReviewDate();
                }
            }
            info[4] = value + " zł";
        }
        info[7] = "";
        for(Accessory accessory : iDAccessory.values()){
            if(accessoryIDQty.get(accessory.getAccessoryID()) == 1){
                info[8] += accessory.getName() + ", ";
            } else {
                info[8] += accessory.getName() + " x" + accessoryIDQty.get(accessory.getAccessoryID()) + ", ";
            }
            if(!accessory.getInfo().equals("brak opłaty") || accessory.getPriceGrossDouble() < 1.0d){
                if(!accessory.getInfo().equals("zużycie 1 mm")){
                    info[9] += accessory.getName() + " - " + (int) accessory.getPriceGrossDouble() + " zł za " + accessory.getInfo() + ", ";
                } else {
                    info[9] += accessory.getName() + " - " + (int) accessory.getPriceGrossDouble() + " zł za 1 mm";
                }
            } else {
                info[9] += "";
            }
        }
        if(longTerm){
            info[10] = Calculate.addZeroIfTenthsEqual(totPerDayGross) + " zł za miesiąc";
        } else {
            info[10] = Calculate.addZeroIfTenthsEqual(totPerDayGross) + " zł";
        }
        if(depositPayment.equals("gotówka")){
            info[11] = depositGross + " zł";
        } else if(depositPayment.equals("przelew") && depositGross == 0){
            info[11] = "przelew";
        } else if(depositPayment.equals("przelew") && depositGross > 0){
            info[11] = depositGross + " zł przelew";
        } else {
            info[11] = depositGross + " zł "+ depositPayment;
        }

        info[12] = probableToWhen.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        info[13] = "";
        info[14] = placeOfWork;
        info[15] = fromWhen.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        info[16] = fromWhen.format(DateTimeFormatter.ofPattern("HH:mm"));
        return info;
    }


    public int getRentalID() {
        return rentalID;
    }

    public ClientRow getClient() {
        return client;
    }

    public Map<Integer, EquipRow> getIDEquip() {
        return iDEquip;
    }

    public List<EquipRow> getEquip() {
        return new ArrayList<>(iDEquip.values());
    }

    public Map<Integer, Accessory> getIDAccessory() {
        return iDAccessory;
    }

    public Map<Integer, Integer> getAccessoryIDQty() {
        return accessoryIDQty;
    }

    public Timestamp getFromWhen() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
        return Timestamp.valueOf(fromWhen.format(formatter));
    }

    public String getFromWhenPretty() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return fromWhen.format(formatter);
    }

    public String getFromWhenTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return fromWhen.format(formatter);
    }

    public Date getProbableToWhen() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return Date.valueOf(probableToWhen.format(formatter));
    }

    public String getProbableToWhenPretty() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return probableToWhen.format(formatter);
    }

    public int getClientID() {
        return clientID;
    }

    public double getTotPerDayNett() {
        return totPerDayNett;
    }

    public double getTotPerDayGross() {
        return totPerDayGross;
    }

    public int getDepositGross() {
        return depositGross;
    }

    public int getClientRentID() {
        return clientRentID;
    }
    public String getClientRentIDPretty (){
        return clientRentIDPretty;
    }
    public String getNote(){
        return note;
    }
    public List<String> getNoteList() {
        String [] splited = note.split("\n");
        return Arrays.asList(splited);
    }

    public void setRentalID(int rentalID) {
        this.rentalID = rentalID;
    }

    public void setiDEquip(Map<Integer, EquipRow> iDEquip) {
        this.iDEquip = iDEquip;
    }

    public void setEquipmentPricePDayNet(int id, double newPrice){
        if(iDEquip.containsKey(id)){
            iDEquip.get(id).setPerDayNet(newPrice);
        }
    }
    public void addEquipment(List<EquipRow> equipmentList){
        for(EquipRow equip : equipmentList){
            if(!iDEquip.containsKey(equip.getEquipmentIDInt())){
                this.iDEquip.put(equip.getEquipmentIDInt(), equip);
            }
        }
    }

    public void setAccessoryQty(int id, int qty){
        if(accessoryIDQty.containsKey(id)){
            accessoryIDQty.put(id, qty);
        }
    }
    public void setAccessoryPriceNett(int id, double newPrice){
        if(iDAccessory.containsKey(id)){
            iDAccessory.get(id).setPriceNett(newPrice);
        }
    }

    public void addAccessory(Accessory accessory, int qty) {
        int accessoryID = accessory.getAccessoryID();
        if(this.iDAccessory.isEmpty()){
            this.iDAccessory.put(accessoryID, accessory);
            this.accessoryIDQty.put(accessoryID, qty);
        } else {
            if(iDAccessory.containsKey(accessoryID)){
                qty += this.accessoryIDQty.get(accessoryID);
                this.accessoryIDQty.put(accessoryID, qty);
            } else {
                this.iDAccessory.put(accessoryID, accessory);
                this.accessoryIDQty.put(accessoryID, qty);
            }
        }
    }
    public void addAccessory(Map<Integer, Accessory> iDAccessory, Map<Integer, Integer> accessoryIDQty){
        if(this.iDAccessory.isEmpty()){
            this.iDAccessory = iDAccessory;
            this.accessoryIDQty = accessoryIDQty;
        } else {
            for (int iD : iDAccessory.keySet()) {
                if (this.iDAccessory.containsKey(iD)) {
                    int qty = accessoryIDQty.get(iD);
                    qty += this.accessoryIDQty.get(iD);
                    this.accessoryIDQty.put(iD, qty);
                } else {
                    this.iDAccessory.put(iD, iDAccessory.get(iD));
                    this.accessoryIDQty.put(iD, accessoryIDQty.get(iD));
                }
            }
        }
    }
    public void setFromWhen(String fromWhen) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.fromWhen = LocalDateTime.parse(fromWhen, formatter);
    }

    public void setFromWhen(LocalDateTime fromWhen) {
        this.fromWhen = fromWhen;
    }

    public void setClient(ClientRow client) {
        this.client = client;
        this.clientID =  Integer.parseInt(client.getClientID());
    }

    public void setProbableToWhen(LocalDate probableToWhen) {
        this.probableToWhen = probableToWhen;
    }

    public void setProbableToWhen(String probableToWhen) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.probableToWhen = LocalDate.parse(probableToWhen, formatter);
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public void setTotPerDayNett(double totPerDayNett) {
        this.totPerDayNett = totPerDayNett;
    }

    public void updateTotPerDayNett(){
        double totPerDayNett = 0;
        for(EquipRow equip : iDEquip.values()){
            totPerDayNett += equip.getPerDayNetDouble();
        }
//        int qty = 0;
//        for(Accessory accessory : iDAccessory.values()){
//            qty = accessoryIDQty.get(accessory.getAccessoryID());
//            totPerDayNett += accessory.getPriceNettDouble() * qty;
//        }
        this.totPerDayNett = totPerDayNett;
    }

    public void setTotPerDayGross(double totPerDayGross) {
        this.totPerDayGross = totPerDayGross;
    }

    public void updateTotPerDayGross() {
        double totPerDayGross = 0;
        for(EquipRow equip : iDEquip.values()){
            totPerDayGross += equip.getPerDayGrossDouble();
        }
//        int qty = 0;
//        for(Accessory accessory : iDAccessory.values()){
//            qty = accessoryIDQty.get(accessory.getAccessoryID());
//            totPerDayGross += accessory.getPriceGrossDouble() * qty;
//        }
        this.totPerDayGross = totPerDayGross;
    }

    public void updateNotes(){
        for(Accessory accessory : iDAccessory.values()){
            if(accessory.isUsable()){
                if(note.isEmpty()){
                    note = accessory.getName() + " - podany został koszt " + accessory.getInfo().replace("e", "a") + ", aktualna wysokość segmentu: ";
                } else {
                    if(!note.contains(accessory.getName())){
                        note += "\n" + accessory.getName() + " - podany został koszt " + accessory.getInfo().replace("e", "a") + ", aktualna wysokość segmentu: ";
                    }
                }
            }
        }
    }

    public void setDepositGross(int depositGross) {
        this.depositGross = depositGross;
    }
    public void updateDepositGross(){
        depositGross = 0;
        for(EquipRow equip : iDEquip.values()){
            depositGross += equip.getDepositGrossInt();
        }
    }


    public void setNote(String note) {
        this.note = note;
    }

    public void setClientRentID(int clientRentID) {
        this.clientRentID = clientRentID;
        String year = Year.now().toString();
        this.clientRentIDPretty = clientRentID + "/" + year.charAt(2) + year.charAt(3);
    }
    public void setClientRentID(int clientRentID, String clientRentIDYear){
        this.clientRentID = clientRentID;
        this.clientRentIDPretty = clientRentID + "/" + clientRentIDYear.charAt(2) + clientRentIDYear.charAt(3);
    }

    public void setIsNewClient(boolean newClient) {
        isNewClient = newClient;
    }

    public boolean isNewClient() {
        return isNewClient;
    }

    public void deleteEquipment(int id){
        iDEquip.remove(id);
    }
    public void deleteAccessory(int id){
        if(note.contains(iDAccessory.get(id).getName())){
            String [] splited = note.split("\n");
            note = "";
            for (int i = 0; i < splited.length; i++) {
                if(!splited[i].contains(iDAccessory.get(id).getName())){
                    note += splited[i] + "\n";
                }
            }
        }
        iDAccessory.remove(id);
        accessoryIDQty.remove(id);
    }

    public String getPlaceOfWork() {
        return placeOfWork;
    }

    public void setPlaceOfWork(String placeOfWork) {
        this.placeOfWork = placeOfWork;
    }

    public boolean isLongTerm() {
        return longTerm;
    }

    public void setLongTerm(boolean longTerm) {
        this.longTerm = longTerm;
    }

    public String getDepositPayment() {
        return depositPayment;
    }

    public void setDepositPayment(String depostitPayment) {
        this.depositPayment = depostitPayment;
    }
}
