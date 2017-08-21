package com.studentloan.white.net.data;

/**
 * Created by fu on 2017/8/8.
 */

public class Contacts {
    public String relationship;
    public String name;
    public String cellphones;//多个|间隔

    public Contacts(String relationship, String name, String cellphones) {
        this.relationship = relationship;
        this.name = name;
        this.cellphones = cellphones;
    }

    public Contacts(String name, String cellphones) {
        this.name = name;
        this.cellphones = cellphones;
    }
}
