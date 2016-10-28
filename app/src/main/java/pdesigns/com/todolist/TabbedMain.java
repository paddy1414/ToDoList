package pdesigns.com.todolist;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by Patrick on 22/10/2015.
 */
public class TabbedMain extends TabActivity {

    private static final String Date_SPEC = "Date";
    private static final String DONE_SPEC = "Done";
    private static final String Priority_SPEC = "Priortiy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbedmain);

        TabHost tabHost = getTabHost();

        // Inbox Tab
        TabHost.TabSpec dateSpec = tabHost.newTabSpec(Date_SPEC);
        // Tab Icon
        dateSpec.setIndicator(Date_SPEC, getResources().getDrawable(R.drawable.ic_action_time));
        Intent dateIntent = new Intent(this, DateListView.class);
        // Tab Content
        dateSpec.setContent(dateIntent);

        // Outbox Tab
        TabHost.TabSpec doneSpec = tabHost.newTabSpec(DONE_SPEC);
        doneSpec.setIndicator(DONE_SPEC, getResources().getDrawable(R.drawable.ic_action_time));
        Intent doneIntent = new Intent(this, DoneListView.class);
        doneSpec.setContent(doneIntent);



        // Adding all TabSpec to TabHost
        tabHost.addTab(dateSpec); // Adding Inbox tab
        tabHost.addTab(doneSpec); // Adding Outbox tab
    }
}
