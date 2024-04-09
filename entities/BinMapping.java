package com.playtech.assignment.entities;

import com.playtech.assignment.enums.CardType;

public class BinMapping {
    public final String name;
    public final long rangeFrom;
    public final long rangeTo;
    public final CardType cardType;
    public final String country;

    public BinMapping(String name, long rangeFrom, long rangeTo, CardType cardType, String country) {
        this.name = name;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.cardType = cardType;
        this.country = country;
    }
}
