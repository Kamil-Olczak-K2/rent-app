package com.rentapp.table;


import com.rentapp.util.Calculate;
import com.rentapp.db_object.Accessory;
import com.rentapp.util.DBQuery;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EquipRow {
    private String model;
    private String equipmentID;
    private String name;
    private String deposit;
    private String perDayNet;
    private double perDayNetDouble;
    private String perDayGross;
    private double perDayGrossDouble;
    private String manufactureYear;
    private String sn;
    private String reviewDate;
    private String motohours;
    private String sold;

    private String shortName;
    private int equipValueGross;


    private Boolean available;
    private Map<Integer, Accessory> iDAccessory;
    private Map<Integer, Integer> accessoryIDQty;

    public EquipRow() {
        model = "";
        equipmentID = "";
        name = "";
        deposit = "";
        perDayNet = "";
        manufactureYear = "";
        sn = "";
        reviewDate = "";
        motohours = "";
        equipValueGross = 1000;
        sold = "";
        available = true;
        this.iDAccessory = new HashMap<>();
        this.accessoryIDQty = new HashMap<>();
    }

    public String get(String columnName) {
        return switch (columnName) {
            case "model" -> model;
            case "equipmentID" -> equipmentID;
            case "name" -> name;
            case "deposit" -> deposit;
            case "perDayNet" -> perDayNet;
            case "manufactureYear" -> manufactureYear;
            case "sn" -> sn;
            case "reviewDate" -> reviewDate;
            case "motohours" -> motohours;
            case "equipValueGross" -> Integer.toString(equipValueGross);
            case "sold" -> sold;
            case "available" -> available.toString();
            default -> "";
        };
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setEquipmentID(String equipmentID) throws SQLException {
        this.equipmentID = equipmentID;
//        setAccessories();
    }

    public void setName(String name) {
        this.name = name;
        this.shortName = name.split(" ")[0];
    }

    public void setDeposit(String deposit) {
        this.deposit = Objects.requireNonNullElse(deposit, "");;
    }

    public void setPerDayNet(String perDayNet) {
        this.perDayNet = perDayNet;
        this.perDayNetDouble = Double.parseDouble(perDayNet);
        setPerDayGross(Calculate.calculateGross(Double.parseDouble(perDayNet)));
        setPerDayGross(Calculate.calculateGrossString(Double.parseDouble(perDayNet)));
    }
    public void setPerDayNet(double perDayNet){
        this.perDayNet = String.valueOf(perDayNet);
        this.perDayNetDouble = perDayNet;
        setPerDayGross(Calculate.calculateGross(perDayNet));
        setPerDayGross(Calculate.calculateGrossString(perDayNet));
    }

    public void setPerDayGross(double perDayGrossDouble){
        this.perDayGrossDouble = perDayGrossDouble;
        setPerDayGross(perDayGrossDouble + "");
        this.perDayNetDouble = Calculate.calculateNett(perDayGrossDouble);
        this.perDayNet = Calculate.calculateNettString(perDayGrossDouble);

    }

    public void setManufactureYear(String manufactureYear) {
        this.manufactureYear = Objects.requireNonNullElse(manufactureYear, "");;
    }

    public void setSn(String sn) {
        this.sn = Objects.requireNonNullElse(sn, "");
    }

    public void setReviewDate(String reviewDate) {
        try {
            if(reviewDate == null || reviewDate.isEmpty()){
                this.reviewDate = "";
            } else {
                this.reviewDate = convertDateFormatSQL(reviewDate);
            }
        } catch (DateTimeParseException e) {
            this.reviewDate = reviewDate;
        }
    }

    public void setMotohours(String motohours) {
        this.motohours = Objects.requireNonNullElse(motohours, "");
    }

    public void setEquipValueGross(int equipValueGross) {
        this.equipValueGross = equipValueGross;
    }

    public void setSold(String sold) {
        this.sold = Objects.requireNonNullElse(sold, "");
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setAccessories() throws SQLException {
        if(equipmentID.isEmpty()){
            return;
        } else {
            this.iDAccessory = DBQuery.queryAccessories(equipmentID);
            if (this.iDAccessory != null) {
                this.accessoryIDQty = new HashMap<>();
                this.iDAccessory.forEach((k, v) -> {
                    this.accessoryIDQty.put(k, this.iDAccessory.get(k).getQuantity());
                });
            }
        }
    }

    public void setAccessoryQty(Map<Integer, Integer> accessoryIDQty) {
        this.accessoryIDQty = accessoryIDQty;
    }

    public void setIDAccessory(Map<Integer, Accessory> iDAccessory) {
        this.iDAccessory = iDAccessory;
    }

    public void addAccessories(List<AccessoryRow> accessories){
        for(AccessoryRow accRow : accessories){
            this.iDAccessory.put(accRow.getAccessoryID(),
                    new Accessory(accRow.getAccessoryID(),
                            accRow.getName(),
                            accRow.getPriceNett(),
                            accRow.isUsable(),
                            accRow.getInfo()));
            this.accessoryIDQty.put(accRow.getAccessoryID(), 1);
        }
    }

    public void removeAccessory(int accessoryID){
        this.iDAccessory.remove(accessoryID);
        this.accessoryIDQty.remove(accessoryID);
    }

    public void setAccessoryQty(int accessoryID, int qty){
        this.accessoryIDQty.put(accessoryID, qty);
    }

    public String getModel() {
        return model;
    }

    public String getEquipmentID() {
        return equipmentID;
    }
    public int getEquipmentIDInt(){
        return Integer.parseInt(equipmentID);
    }

    public String getName() {
        return name;
    }
    public String getShortName () {
        return shortName;
    }

    public String getDeposit() {
        return deposit;
    }
    public int getDepositGrossInt() {
        return Integer.parseInt(deposit);
    }

    public String getPerDayNet() {
        return perDayNet;
    }

    public double getPerDayNetDouble() {
        return Double.parseDouble(perDayNet);
    }

    public double getPerDayGrossDouble() {
        return perDayGrossDouble;
    }

    public String getManufactureYear() {
        return manufactureYear;
    }

    public String getSn() {
        return sn;
    }

    public Date getReviewDateSQL() {
        return Date.valueOf(reviewDate);
    }

    public String getReviewDate() {
        if(reviewDate == null || reviewDate.isEmpty()){
            return "";
        }
        return convertDateFormatPretty(reviewDate);
    }

    public String getMotohours() {
        return motohours;
    }

    public int getEquipValueGrossInt() {
        return equipValueGross;
    }

    public String getSold() {
        if(sold == null || sold.isEmpty()){
            return "";
        }
        return convertDateFormatPretty(sold);
    }

    public Boolean getAvailable() {
        return available;
    }

    public Map<Integer, Accessory> getIDAccessory() {
        return this.iDAccessory;
    }

    public Map<Integer, Integer> getAccessoryIDQty() {
        return this.accessoryIDQty;
    }

    public String convertDateFormatSQL(String dateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, inputFormatter);
        return date.format(outputFormatter);
    }

    public String convertDateFormatPretty(String dateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(dateString, inputFormatter);
        return date.format(outputFormatter);
    }

    public String getPerDayGross() {
        return perDayGross;
    }

    public void setPerDayGross(String perDayGross) {
        this.perDayGross = perDayGross;
    }
}
