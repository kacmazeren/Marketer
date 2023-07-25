package com.example.marketer10;

import java.util.Date;

public class Product {

    private int ID;
    private Date date;
    private String supermarket;
    private String kind;
    private String goods;
    private String productType;
    private String brand;
    private String weight;
    private double price;
    private double unitPrice;

    public Product(int ID, Date date, String supermarket, String kind, String product, String productType, String brand, String weight, double price, double unitPrice) {
        this.ID = ID;
        this.date = date;
        this.supermarket = supermarket;
        this.kind = kind;
        this.goods = product;
        this.productType = productType;
        this.brand = brand;
        this.weight = weight;
        this.price = price;
        this.unitPrice = unitPrice;
    }

    public Product() {}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSupermarket() {
        return supermarket;
    }

    public void setSupermarket(String supermarket) {
        this.supermarket = supermarket;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    @Override
    public String toString() {
        return  ID + "Date: " + date + ", Supermarket: " + supermarket + ", Goods: " + goods + ", Price: " + price + " , UnitPrice:"+ unitPrice;
    }


}
