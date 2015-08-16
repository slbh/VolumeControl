package io.github.slbh.volumecontrol.rules;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.util.Log;

import java.util.HashMap;

import io.github.slbh.volumecontrol.R;

/**
 * Created by florian on 28.07.15.
 * An object of this class represents a rule of the TRIGGER_HEADPHONE trigger type.
 * It needs one extra value with the key HeadphonesRule.EXTRA_PLUGGED which can either be '0' or '1'
 */
public class HeadphonesRule extends Rule{
    public final static String EXTRA_PLUGGED = "extra_plugged";

    boolean plugged;

    HeadphonesRule(String name, boolean musicVolume, byte changeTo, HashMap<String, String> extras){
        super(name, musicVolume, changeTo, extras);
        String plugged;

        //get the plugged value out of the extras map
        try {
            plugged = extras.get(EXTRA_PLUGGED);
        }catch (NullPointerException e) {
            throw new IllegalArgumentException("The extras map must contain a key " +
                    "HeadphonesRule.EXTRA_PLUGGED.");
        }

        //if the value of EXTRA_PLUGGED equals "1", set plugged to true
        this.plugged = plugged.equals("1") ? true : false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("debug", "onReceive");
        boolean plugged;

        //check if headphones are plugged in or out
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            plugged = state==0 ? false : true;
        }else{
            throw new IllegalArgumentException("The provided intent must have a" +
                    " ACTION_HEADSET_PLUG action");
        }

        //if the plugged state equals the one the user defined, apply the rule and change the volume
        if(this.plugged == plugged)
            apply(context);
    }

    /**
     * generates an if-clause that says when the respective rule is applied: Either when headphones
     * are plugged in or out.
     * @param res
     * @return
     */
    protected String getIfClause(Resources res){
        String str;
        String plugged;

        plugged = this.plugged ? res.getString(R.string.plugged_in) : res.getString(R.string.plugged_out);
        str = res.getString(R.string.headphone_descr, plugged);

        Log.e("debug", "If clause: "+str);
        return str;
    }

    @Override
    public byte getTriggerType() {
        return Rule.TRIGGER_HEADPHONE;
    }

    @Override
    /**
     * registers the Rule as Broadcastreceiver
     */
    public void register(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        context.registerReceiver(this, filter);

        Log.e("debug", "registered "+ name);
    }

    public void unregister(Context context){
        context.unregisterReceiver(this);

        Log.e("debug", "unregister "+name);
    }
}
