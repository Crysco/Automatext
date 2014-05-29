package com.automatext.crysco.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import static com.automatext.crysco.app.GlobalConstants.PREFS_NAME;
import static com.automatext.crysco.app.GlobalConstants.SWITCH_STATE;
import static com.automatext.crysco.app.GlobalConstants.TAG;

/**
 * Created by Crys on 5/23/14.
 */
public class IncomingSmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            Object[] pdus =(Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for(int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            Log.d(GlobalConstants.TAG, "Text Received");

            DBAdapter.getMainDBInstance().open();
            Cursor cursor = DatabaseReplies.getInstance().getMostRecentRecord();
            final boolean checkOne = (cursor.getInt(cursor.getColumnIndex(DatabaseReplies.KEY_ACTIVE)) == GlobalConstants.ActiveState.ACTIVE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            final boolean checkThree = prefs.getBoolean(SWITCH_STATE, false);
            final String replyMessage = cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_CONTENT));

            Log.d(TAG, Boolean.toString(checkThree));
            if(checkOne && checkThree) {
                Log.d(GlobalConstants.TAG, "Text Replied");
                SmsSender.sendText(messages[0].getDisplayOriginatingAddress(), replyMessage);
            }
        }
    }
}
