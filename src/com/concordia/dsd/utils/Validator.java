package com.concordia.dsd.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Validator {
    private static Validator instance;
    private Validator(){};
    public static Validator getInstance(){
        if(instance==null){
            instance = new Validator();
        }
        return instance;
    }

    public boolean isValidLastName(String lastName) {
        boolean isValid = false;
        if (lastName != null && !lastName.isEmpty()) {
            char key = lastName.charAt(0);
            if (Character.isLetter(key)) {
                isValid = true;
            }
        }
        return isValid;
    }
    public boolean isValidDate(String dateToValidate, String dateFromat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);

        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
