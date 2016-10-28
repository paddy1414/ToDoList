package pdesigns.com.todolist;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import pdesigns.com.todolist.DbStuffs.TaskDbHelper;
import pdesigns.com.todolist.DbStuffs.TaskPreferences;


public class DateListView extends ListActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private ListAdapter listAdapter;
    private TaskDbHelper helper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new TaskDbHelper(this);
        helper.open();
        updateUI();
        FloatingActionButton fab1 = (FloatingActionButton) this.findViewById(R.id.fab);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createReminder();
            }
        });
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_insert:
                createReminder();
                return true;
            case R.id.menu_settings:
                Intent i = new Intent(this, TaskPreferences.class);
                startActivity(i);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void createReminder() {
        Intent i = new Intent(this, ReminderEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void updateUI() {
        Cursor cursor = helper.fetchAllReminders();
        startManagingCursor(cursor);
        String[] from = new String[]{TaskDbHelper.TASK};

        int[] to = new int[]{R.id.taskTextView};

        SimpleCursorAdapter reminders  = new SimpleCursorAdapter(
                this,
                R.layout.task_view,
                cursor,
                from,
                to
        );

        this.setListAdapter(reminders);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_menu_item_longpress, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                helper.deleteReminder(info.id);
                updateUI();
                return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.d("list item selected was ", position + "");
        Toast.makeText(DateListView.this, getString(R.string.itemPicked) + position, Toast.LENGTH_SHORT).show();

         Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(TaskDbHelper._ID, id);
         startActivityForResult(i, ACTIVITY_EDIT);
    }




}
