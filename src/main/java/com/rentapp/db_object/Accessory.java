package com.rentapp.db_object;

import com.rentapp.util.Calculate;

public class Accessory{

    private int accessoryID;
    private String name;
    private String priceNett;
    private double priceNettDouble;
    private double priceGross;
    private boolean isUsable;
    private String usage;
    private String info = "";
    private int quantity;

    public Accessory(int accessoryID) {
        this.accessoryID = accessoryID;
        this.name = "";
        this.priceNett = "";
        this.priceGross = 0;
        this.isUsable = false;
        this.usage = "";

    }

    public Accessory(int accessoryID, String name, String priceNett, boolean isUsable, String info) {
        this.accessoryID = accessoryID;
        this.name = name;
        this.priceNett = priceNett;
        this.priceNettDouble = Double.parseDouble(priceNett);
        this.priceGross = Calculate.calculateGross(priceNettDouble);
        this.isUsable = isUsable;
        this.usage = "";
        this.info = info;
    }

    public Accessory(String name, double priceGross, boolean isUsable) {
        this.name = name;
        setPriceGross(priceGross);
        this.isUsable = isUsable;
        this.usage = "";
    }

    public void setAccessoryID(int accessoryID) {
        this.accessoryID = accessoryID;
    }
    public void setAccessoryID(String accessoryID) {
        this.accessoryID = Integer.parseInt(accessoryID);
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPriceNett(double priceNett) {
        this.priceNett = String.valueOf(priceNett);
        this.priceGross = Calculate.calculateGross(priceNett);
    }
    public void setPriceGross(double priceGross) {
        this.priceGross = priceGross;
        this.priceNett = Calculate.calculateNett(priceGross) + "";
    }
    public void setIsUsable(boolean usable) {
        this.isUsable = usable;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }


    public String getName() {
        return name;
    }

    public double getPriceNettDouble() {
        return Double.parseDouble(priceNett);
    }

    public String getPriceNettString(){
        return String.valueOf(priceNett);
    }

    public double getPriceGrossDouble() {
        return priceGross;
    }

    public boolean isUsable() {
        return isUsable;
    }

    public int getAccessoryID() {
        return accessoryID;
    }
    public String getAccessoryIDString(){
        return String.valueOf(accessoryID);
    }

    public String getUsage() {
        return usage;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
