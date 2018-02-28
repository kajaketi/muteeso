package com.nisaidie.nisaidie1.helper;

public class EmDetailsTwo {

    String fName, lName, primaryPhone, otherPhone, address, rship;
    private String pushId;

    //default constructor
    public EmDetailsTwo(){

    }

    public String getfName(){
        return fName;
    }

    public void setfName(String first_name){
        this.fName = first_name;
    }

    public String getlName(){
        return lName;
    }

    public void setlName(String last_name){
        this.lName = last_name;
    }

    public String getPrimaryPhone(){
        return primaryPhone;
    }

    public void setPrimaryPhone(String prim_phone){
        this.primaryPhone = prim_phone;
    }

    public String getOtherPhone(){
        return otherPhone;
    }

    public void setOtherPhone(String other_phone){
        this.otherPhone = other_phone;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress (String address) {
        this.address = address;
    }

    public String getRship (){
        return rship;
    }

    public void setRship (String r_ship) {
        this.rship = r_ship;
    }

    public String getPushId(){
        return pushId;
    }

    public void setPushId(String pushId){
        this.pushId = pushId;
    }
}
