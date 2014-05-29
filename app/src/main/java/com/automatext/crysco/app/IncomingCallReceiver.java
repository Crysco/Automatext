package com.automatext.crysco.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import static com.automatext.crysco.app.GlobalConstants.PREFS_NAME;
import static com.automatext.crysco.app.GlobalConstants.SWITCH_STATE;
import static com.automatext.crysco.app.GlobalConstants.*;

/**
 * Created by Crys on 5/27/14.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private static MyPhoneStateListener phoneStateListener;

    @Override
    public void onReceive(Context context, Intent intent) {
            TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(phoneStateListener == null) {
                Log.d(TAG, "phoneStateListener created");
                phoneStateListener = new MyPhoneStateListener(context);
                tmgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
    }

    private class MyPhoneStateListener extends PhoneStateListener {

        private Context context;
        private AudioManager audioManager;
        private int ringerMode;
        private boolean sendMessage;

        public MyPhoneStateListener(Context c) {
            this.context = c;
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            ringerMode = audioManager.getRingerMode();
            sendMessage = false;
        }

        public void onCallStateChanged(int state, String incomingNumber) {

            Log.d(TAG, "Call state changed");
            DBAdapter.getMainDBInstance().open();
            Cursor cursor = DatabaseReplies.getInstance().getMostRecentRecord();
            if(cursor != null && cursor.moveToFirst()) {
                if(state == TelephonyManager.CALL_STATE_RINGING){
                    final boolean checkOne = (cursor.getInt(cursor.getColumnIndex(DatabaseReplies.KEY_ACTIVE)) == ActiveState.ACTIVE);
                    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
                    final boolean checkThree = prefs.getBoolean(SWITCH_STATE, false);
                    final int silence = cursor.getInt(cursor.getColumnIndex(DatabaseReplies.KEY_SILENCE));

                    if(checkOne && checkThree) {
                        if(silence == SilenceState.SILENCED) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                        sendMessage = true;
                    }
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    audioManager.setRingerMode(ringerMode);
                    if(sendMessage) {
                        final String replyMessage = cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_CONTENT));
                        SmsSender.sendText(incomingNumber, replyMessage);
                    }
                }
                cursor.close();
            }
            DBAdapter.getMainDBInstance().close();
        }
    }
}
