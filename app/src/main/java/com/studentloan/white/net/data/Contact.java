package com.studentloan.white.net.data;

/**
 * Created by fu on 2017/6/13.
 */

public class Contact {
    public String relationship;
    public String name;
    public String cellphone;

    public Contact(String name,String cellphones){
        this.name = name;
        this.cellphone = cellphones;
    }
    public Contact(String name,String cellphones,String relationship){
        this.name = name;
        this.cellphone = cellphones;
        this.relationship = relationship;
    }
}
