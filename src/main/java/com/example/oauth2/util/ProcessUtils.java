package com.example.oauth2.util;

import java.util.Date;
import java.util.Random;

public class ProcessUtils {
    public static String generateTempPwd(int length) {
        String numbers = "012345678";
        char otp[] = new char[length];
        Random getOtpNum = new Random();
        for (int i = 0; i < length; i++) {
            otp[i] = numbers.charAt(getOtpNum.nextInt(numbers.length()));
        }
        String optCode = "";
        for (int i = 0; i < otp.length; i++) {
            optCode += otp[i];
        }
        return optCode;
    }

    public static Date getCurrentDay() {
        Date currentDate = new Date();
        return new Date(currentDate.getTime());
    }
}
