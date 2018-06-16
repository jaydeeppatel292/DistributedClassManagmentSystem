package com.concordia.dsd.utils;

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
}
