package com.venned.simplerealm.realm;


import java.util.ArrayList;
import java.util.HashMap;

public class RealmLevel {
    public static HashMap<Integer,RealmLevel> realmlevel = new HashMap<>();
    private int price;
    private int bordersize;
    private int maxplayer;
    private int number;
    public RealmLevel(int number, int price, int bordersize, int maxplayer) {
       this.price = price;
       this.bordersize = bordersize;
       this.maxplayer = maxplayer;
       this.number = number;
       realmlevel.put(number,this);
    }

    public int getPrice() {
        return price;
    }

    public int getBordersize() {
        return bordersize;
    }

    public int getMaxplayer() {
        return maxplayer;
    }
    public int getNumber(){
        return number;
    }
    public static RealmLevel getLevel(int level){
      return realmlevel.get(level);
    }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(System.out::println);
    }
}
