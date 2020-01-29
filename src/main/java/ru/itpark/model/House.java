package ru.itpark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class House {
    private int id;
    private int price;
    private String district;
    private String underground;
    private String date;
    public House(int id, int price, String district, String underground) {
        this.id = id;
        this.price = price;
        this.district = district;
        this.underground = underground;
    }
}

