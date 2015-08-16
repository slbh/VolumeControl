package io.github.slbh.volumecontrol.rules;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by florian on 28.07.15.
 */
public class RuleManager extends Service{
    private final IBinder ruleMgrBinder = new RuleMgrBinder();
    HashMap<String, Rule> rules;

    public void onCreate(){
        //TODO XML auslesen
        //TODO Regeln registrieren
    }

    public void onDestroy(){
        //TODO Regeln unregistrieren
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO startForeground()

        //if the service gets killed after returning it is restarted
        return START_STICKY;
    }

    public IBinder onBind(Intent intent){
        return ruleMgrBinder;
    }


    /**
     * Class used for the cliend Binder
     */
    public class RuleMgrBinder extends Binder{
        RuleManager getService() {
            return RuleManager.this;
        }
    }

    /**
     * Creates a Rule object of the given type with the given parameters
     * @param name
     * @param musicVolume
     * @param changeTo
     * @param extras
     * @param triggerType
     */
    private Rule buildRule(String name, boolean musicVolume, byte changeTo,
                           HashMap<String, Object> extras, byte triggerType) {
        Rule rule;

        //create Rule based upon trigger type
        switch(triggerType){
            case Rule.TRIGGER_HEADPHONE:
                rule = new HeadphonesRule(name, musicVolume ,changeTo, extras);
                break;
            default:
                throw new IllegalArgumentException("Trigger type does not exist.");
        }

        return rule;
    }

    /**
     * Returns a list of all rules that are currently active
     * @return
     */
    public ArrayList<Rule> getRules(){
        return new ArrayList<Rule>(rules.values());
    }

    public void newRule(String name, boolean musicVolume, byte changeTo,
                        HashMap<String, String> extras, byte triggerType){
        //TODO code um regel zu erstellen
    }

    public void deleteRule(String name){
        Rule rule;

        rule = rules.get(name);

        //TODO löschen
    }
}
