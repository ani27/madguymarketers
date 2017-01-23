package com.vp6.anish.madguysmarketers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by anish on 22-12-2016.
 */

public class SmsListener extends BroadcastReceiver {

    public OnSmsReceivedListener listener = null;
    public Context context;

    public SmsListener() {

    }

    public void setOnSmsReceivedListener(Context context) {
        Log.i("Listener", "SET");
        this.listener = (OnSmsReceivedListener) context;
        Log.i("Listener", "SET SUCCESS");
    }

    public interface OnSmsReceivedListener {
        public void onSmsReceived(String otp);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e("TAG", "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains("madguy")) {
                        return;
                    }

                    // verification code from sms
                    String verificationCode = message.substring(18,18+4                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             );

                    Log.e("TAG", "OTP received: " + verificationCode);

                    if (listener != null) {
                        listener.onSmsReceived(verificationCode);
                    }
                    else
                    {
                        Log.d("Listener", "Its null");
                    }
//                    Intent hhtpIntent = new Intent(context, HttpService.class);
//                    hhtpIntent.putExtra("otp", verificationCode);
//                    context.startService(hhtpIntent);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception: " + e.getMessage());
        }
    }

}
