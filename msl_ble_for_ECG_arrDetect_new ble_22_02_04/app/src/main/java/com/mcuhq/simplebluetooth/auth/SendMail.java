package com.mcuhq.simplebluetooth.auth;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

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
            //Toast.makeText(context, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("sendSecurityCode", String.valueOf(result));

        return result;
    }
}

