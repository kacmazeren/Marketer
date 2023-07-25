package com.example.marketer10;

public class ShoppingItem {
    private String date;
    private String supermarket;
    private String productType;
    private double price;
    private String memberNumber;

    public ShoppingItem(String date, String supermarket, String productType, double price, String memberNumber) {
        this.date = date;
        this.supermarket = supermarket;
        this.productType = productType;
        this.price = price;
        this.memberNumber = memberNumber;
    }

    public String getDate() {
        return date;
    }

    public String getSupermarket() {
        return supermarket;
    }

    public String getProductType() {
        return productType;
    }

    public double getPrice() {
        return price;
    }
    public String getMemberNumber() { // new getter
        return memberNumber;
    }
}
