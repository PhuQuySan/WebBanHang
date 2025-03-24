package com.asm.models;




public class Cart {
	
    private int id;
    private String name;
    private String image;
    private double price;
    private int qty;

    public Cart(int id, String name, String image, double price, int qty) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.qty = qty;
    }

    // Getters và Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public double getPrice() { return price; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}
