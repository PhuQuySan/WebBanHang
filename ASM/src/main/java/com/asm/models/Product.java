package com.asm.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    private int quantity;

    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

//    @OneToMany ( mappedBy = orphanRemoval = true)
//    @Tostring
}


//package com.asm.models;
//
//import java.util.List;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//@Table(name = "Product")
//public class Product {
////    public Integer getId() {
////		return id;
////	}
////
////	public void setId(Integer id) {
////		this.id = id;
////	}
////
////	public String getName() {
////		return name;
////	}
////
////	public void setName(String name) {
////		this.name = name;
////	}
////
////	public double getPrice() {
////		return price;
////	}
////
////	public void setPrice(double price) {
////		this.price = price;
////	}
////
////	public int getQuantity() {
////		return quantity;
////	}
////
////	public void setQuantity(int quantity) {
////		this.quantity = quantity;
////	}
////
////	public String getImage() {
////		return image;
////	}
////
////	public void setImage(String image) {
////		this.image = image;
////	}
////
////	public Category getCategory() {
////		return category;
////	}
////
////	public void setCategory(Category category) {
////		this.category = category;
////	}
//
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(nullable = false)
//    private double price;
//
//    private int quantity;
//
//    private String image;
//
//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category category;
//}
