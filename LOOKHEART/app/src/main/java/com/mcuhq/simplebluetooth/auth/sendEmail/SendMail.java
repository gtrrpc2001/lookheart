package com.mcuhq.simplebluetooth.auth.sendEmail;


import android.content.Context;
import android.util.Log;

import com.mcuhq.simplebluetooth.auth.sendEmail.GMailSender;

public class SendMail {

    String getEmail;
    public SendMail(String getEmail){
        this.getEmail = getEmail;
    }
    GMailSender gMailSender = new GMailSender();

    String emailCode = "";

    public String getCode(){
        return emailCode;
    }

    public boolean sendSecurityCode(Context context, String tile, String content) {
        boolean result = false;
        try {
            emailCode = gMailSender.getEmailCode();
            gMailSender.sendMail(tile, content + "\n" + "인증번호 : " + emailCode, getEmail);
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("sendSecurityCode", String.valueOf(result));

        return result;
    }
}

