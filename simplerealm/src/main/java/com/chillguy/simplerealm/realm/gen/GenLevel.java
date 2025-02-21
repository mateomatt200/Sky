package com.chillguy.simplerealm.realm.gen;

import java.util.List;

public class GenLevel {

    int nivel;
    List<GenChances> listBlocks;
    int price;

    public GenLevel(int nivel, List<GenChances> listBlocks, int price) {
        this.nivel = nivel;
        this.listBlocks = listBlocks;
        this.price = price;
    }

    public int getNivel() {
        return nivel;
    }

    public int getPrice() {
        return price;
    }

    public List<GenChances> getListBlocks() {
        return listBlocks;
    }
}
