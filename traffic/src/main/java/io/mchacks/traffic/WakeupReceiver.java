package io.mchacks.traffic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Brian on 2/21/2015.
 */
public class WakeupReceiver extends BroadcastReceiver {
    public WakeupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, TrafficGetService.class));
    }
}