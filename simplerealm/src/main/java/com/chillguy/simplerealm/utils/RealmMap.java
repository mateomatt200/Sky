package com.chillguy.simplerealm.utils;

import com.chillguy.simplerealm.realm.RealmLevel;
import com.chillguy.simplerealm.realm.gen.GenLevel;

import java.util.HashMap;
import java.util.Map;

public class RealmMap {

    private HashMap<String, HashMap<Integer, RealmLevel>> realmLevels = new HashMap<>();
    private Map<Integer, GenLevel> genLevels = new HashMap<>();

    public RealmMap(){
        realmLevels = new HashMap<>();
        genLevels = new HashMap<>();
    }

    public Map<Integer, GenLevel> getGenLevels() {
        return genLevels;
    }

    public HashMap<String, HashMap<Integer, RealmLevel>> getRealmLevels() {
        return realmLevels;
    }
}
