package io.mchacks.sports;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WakeupReceiver extends BroadcastReceiver {
    public WakeupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RetrieveRSS.class));
    }
}
