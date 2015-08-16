package io.github.slbh.volumecontrol.rules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.util.Log;

import java.util.HashMap;

import io.github.slbh.volumecontrol.R;

/**
 * Created by florian on 25.07.15.
 * This class represents a rule set by the user. It changes the volume when onReceive is called. It
 * is registered by the RuleManager class
 */
public abstract class Rule extends BroadcastReceiver{
    public final static byte TRIGGER_HEADPHONE = 0;
    public final static byte TRIGGER_TIME = 1;
    public final static byte TRIGGER_APPLICATION = 2;

    protected String name;
    protected boolean musicVolume;
    protected byte changeTo;
    protected HashMap<String, String> extras;

    /**
     *
     * @param name
     * @param musicVolume if true, media volume is changed instead of ring/notification volume
     * @param changeTo -1 = silent; 0 = vibrate; 1 = 1% ... 100 = 100%
     */
    Rule(String name, boolean musicVolume, byte changeTo, HashMap<String, String> extras){
        //changeTo has to be a value between -1 and 100
        if(changeTo < -1 || changeTo > 100)
            throw new IllegalArgumentException("changeTo has to be between -1 and 100");
        //if changeTo is 0 (=vibrate), the phoneVolume has to be changed
        if(changeTo == 0 && musicVolume)
            throw new IllegalArgumentException("changeTo cannot be 0 (=vibrate) when musicVolume " +
                    "is to be changed");

        this.name = name;
        this.musicVolume = musicVolume;
        this.changeTo = changeTo;
        this.extras = extras;
        Log.e("Debug", "Rule constructor ende");
    }

    /**
     * this method is called by the onReceive method of a child object after it handled the intent
     * in order to change the volume in the way the user wanted to
     * @param context
     */
    protected void apply(Context context){
        AudioManager audiomgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        Log.e("debug", "appling rule");

        if (musicVolume) {
            setStreamVolume(AudioManager.STREAM_MUSIC, audiomgr);
        }else if(changeTo == 0){
            audiomgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }else if(changeTo == -1){
            audiomgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }else{
            audiomgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            setStreamVolume(AudioManager.STREAM_RING, audiomgr);
            setStreamVolume(AudioManager.STREAM_NOTIFICATION, audiomgr);
        }
    }

    /**
     * calculates the volume the user wants to set based upon the percentage the user gave and the
     * streamMaxVolume
     * @param streamMaxVolume
     * @return the percentage of the StreamMaxVolume that is contained in changeTo
     */
    private int getVolume(int streamMaxVolume){
        if(changeTo == -1)
            return 0;

        return streamMaxVolume * changeTo / 100;
    }

    /**
     * sets the volume of the given stream to the percentage provided by changeTo
     * @param stream
     * @param audiomgr
     */
    private void setStreamVolume(int stream, AudioManager audiomgr){
        int index = getVolume(audiomgr.getStreamMaxVolume(stream));

        audiomgr.setStreamVolume(stream, index, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * returns a string that describes the rule and what it does
     * @param res
     * @return
     */
    public String getDescription(Resources res){
        String str;
        String plugged;
        String phoneVol;
        String percent;

        //changeTo = 0 -> "vibrate", changeTo = -1 -> "muted", otherwise -> "set to changeTo %"

        switch (changeTo){
            case 0:
                percent = res.getString(R.string.vibrate);
                break;
            case -1:
                percent = res.getString(R.string.muted);
                break;
            default:
                percent = res.getString(R.string.set_to, changeTo);
                break;
        }

        phoneVol = !musicVolume ?
                res.getString(R.string.phone_vol) :
                res.getString(R.string.media_vol);

        str = getIfClause(res)+ " " + res.getString(R.string.main_clause, phoneVol, percent);

        return str;
    }

    public String toString() {
        return name;
    }

    public boolean isMusicVolume() {
        return musicVolume;
    }

    public byte getChangeTo() {
        return changeTo;
    }

    protected abstract String getIfClause(Resources res);
    public abstract byte getTriggerType();
    public abstract void register(Context context);
    public abstract void unregister(Context context);
}
