package io.github.slbh.volumecontrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import io.github.slbh.volumecontrol.rules.RuleManager;


public class RulesListActivity extends ActionBarActivity {
    private Resources res;
    private RuleManager ruleMgr;
    private boolean boundToRuleMgr;
    private ServiceConnection connection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_list);

        //start the RuleManager
        Intent intent = new Intent(this, RuleManager.class);
        ComponentName serviceName = startService(intent);

        res = this.getResources();
        ListView rules = (ListView) findViewById(R.id.rules);

        //add the @+id/footer_layout view as footer view so that the btn is always on the bottom
        //of the list of views
        View footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_layout, null, false);
        rules.addFooterView(footerView);

        listRules(rules);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO bind to service
        //TODO get saved Rulelist
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO save service and Rulelist
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO unbind service
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rules_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the new_rule_btn button is clicked (it attached to this method in the layout file
     * via the android:onClick attribute). This method starts the NewRuleActivity activity
     * @param view
     */
    public void newRule(View view){
        //TODO service im Intent übergeben
        Intent intent = new Intent(RulesListActivity.this, NewRuleActivity.class);
        startActivity(intent);
    }

    /**
     * puts the name of all the user's rules in the list or a message about how to create the first
     * one
     * @param rules the ListView that should show the rules
     */
    private void listRules(ListView rules){
        ArrayList<String> datalist = new ArrayList<String>();

        //if list is empty, show a message that tells the user to press the new_rule_btn button
        if(datalist.isEmpty()) {
            String text = String.format(res.getString(R.string.empty_list),
                    res.getString(R.string.new_rule));
            datalist.add(text);
        }

        //create a simple list adapter
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                datalist);
        rules.setAdapter(adapter);
    }
}
