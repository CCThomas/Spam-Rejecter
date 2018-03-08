package ccthomas.com.spamrejecter.object;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.lang.reflect.Method;

import ccthomas.com.spamrejecter.object.Subset;

/**
 * Created by CCThomasMac on 3/1/18.
 */

public class IncomingCallReceiver extends BroadcastReceiver {
    static ArrayList<Subset> subsets = new ArrayList<>();

    public IncomingCallReceiver() {}

    @Override
    public void onReceive(final Context context, Intent intent) {
        // ITelephony telephonyService;
        Object iTelephony;
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            boolean isNumberAnException = false;
            for (Subset subset: subsets) {
                if (subset.isException(number)) isNumberAnException = true;
            }

            if (!isNumberAnException) {

                boolean blockNumber = false;
                for (Subset subset : subsets) {
                    if (subset.blockNumber(number)) {blockNumber = true; break;}
                }

                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING) && blockNumber) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        Method m1 = tm.getClass().getDeclaredMethod("getITelephony");
                        m1.setAccessible(true);
                        iTelephony = m1.invoke(tm);

                        if ((number != null)) {
                            Method m2 = iTelephony.getClass().getDeclaredMethod("silenceRinger");
                            Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");
                            m2.invoke(iTelephony);
                            m3.invoke(iTelephony);

                            Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(context, "Ring " + number, Toast.LENGTH_SHORT).show();

                }
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Toast.makeText(context, "Answered " + number, Toast.LENGTH_SHORT).show();
                }
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Toast.makeText(context, "Idle " + number, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addSubset(Subset subset) {
        subsets.add(subset);
    }
    public static Subset getSubsetAtIndex(int index) {return subsets.get(index);}

    public static ArrayList<Subset> getSubsets() {return subsets;}

    public static void removeSubsetWithID(int id) {
        subsets.remove(id);
    }
    public static void replaceSubsetAtIndex(int index, Subset subset) {
        subsets.remove(index);
        subsets.add(index, subset);
    }

}
