package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.telephony.SmsManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Crys on 5/27/14.
 */
public class SmsSender {

    private static boolean sent;

    public static void sendText(String incomingNumber, String replyMessage) {
        if(!sent) {
            SmsManager.getDefault().sendTextMessage(incomingNumber, null, replyMessage, null, null);
            sent = true;
            Log.d(TAG, "Text sent to " + incomingNumber);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    sent = false;
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 5000);
        }
    }
}
