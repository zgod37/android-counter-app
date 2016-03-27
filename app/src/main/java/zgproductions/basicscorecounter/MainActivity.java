package zgproductions.basicscorecounter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Main activity
 *
 * Holds scroll view which contains the list of active counters
 * Users can add, remove and configure counters
 * Each counter is represented as a table row in the layout
 *
 */

public class MainActivity extends AppCompatActivity {

    final static int MAX_COUNTER_LIMIT = 10;
    final private static String TAG = "basicscorecounter.MAIN";

    private ColorManager colorManager = new ColorManager();
    private ActiveCounterList activeCounterList = new ActiveCounterList();
    private TableLayout scrollViewTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scrollViewTable = (TableLayout) findViewById(R.id.scrollViewTable);

        setCounterColors();
        restoreAllCounters();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveAllCounters();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //called from counter config activity
        //intent contains new counter info

        if (resultCode == RESULT_OK) {
            changeCounterData(intent.getStringExtra("counterId"), intent.getStringExtra("counterDataString"));
        }

        if (resultCode == 2) {
            removeCounter(intent.getStringExtra("counterId"));
        }

    }

    private void setCounterColors() {
        colorManager.setColors(getResources().getIntArray(R.array.counter_colors));
        //Log.d(TAG, "colors = " + colorManager);
    }

    private int getCounterCount() {
        return scrollViewTable.getChildCount();
    }

    public void addNewCounter(View view) {
        //add a new counter to the row
        //enforce max counter limit

        int counterCount = getCounterCount();
        if (counterCount < MAX_COUNTER_LIMIT) {
            CounterData newCounterData = activeCounterList.addNewCounter(
                    "counter"+counterCount, "counter"+(counterCount+1), colorManager.getColor());
            addCounterToScrollView(newCounterData);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Counter limit reached!")
                    .setMessage("Error! Number of counters limited to: " + MAX_COUNTER_LIMIT)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void incrementCounter(View view) {
        TableRow counterRow = (TableRow) view.getParent();
        TextView counterId = (TextView) counterRow.findViewById(R.id.counterId);
        CounterData counterData = activeCounterList.getCounterData(counterId.getText().toString());
        changeCounterValue((TextView) counterRow.findViewById(R.id.counterValue), counterData, counterData.getStepUp());
    }

    public void decrementCounter(View view) {
        TableRow counterRow = (TableRow) view.getParent();
        TextView counterId = (TextView) counterRow.findViewById(R.id.counterId);
        CounterData counterData = activeCounterList.getCounterData(counterId.getText().toString());
        changeCounterValue((TextView) counterRow.findViewById(R.id.counterValue), counterData, counterData.getStepDown() * -1);
    }

    private void changeCounterValue(TextView counterValue, CounterData counterData, int delta) {
        int newValue = Integer.valueOf(counterValue.getText().toString()) + delta;
        counterValue.setText(String.valueOf(newValue));
        counterData.setValue(newValue);
    }

    private void changeCounterData (String counterId, String newCounterDataString) {
        //change stored counter data with relevant values
        //also update view in scrollview

        CounterData counterData = activeCounterList.getCounterData(counterId);
        if (!counterData.equalsString(newCounterDataString)) {
            //Log.i(TAG, "Counter data was changed by user");

            //add old color to available color list
            colorManager.addColor(counterData.getBackgroundColor());

            //change data
            counterData.setAllValuesFromString(newCounterDataString);

            //change view, accessed by slicing numeral in counterId
            int counterPosition = Integer.valueOf(counterId.substring(7));
            TableRow counterRow = (TableRow) scrollViewTable.getChildAt(counterPosition);
            TextView counterLabel = (TextView) counterRow.findViewById(R.id.counterLabel);
            TextView counterValue = (TextView) counterRow.findViewById(R.id.counterValue);
            counterLabel.setText(counterData.getLabel());
            counterValue.setText(counterData.getStringValue());
            counterRow.setBackgroundColor(counterData.getBackgroundColor());
        }
    }

    private void addCounterToScrollView(CounterData counterData) {
        //insert counter into the scroll view

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View counterRow = inflater.inflate(R.layout.single_counter, null);

        TextView counterId = (TextView) counterRow.findViewById(R.id.counterId);
        TextView counterLabel = (TextView) counterRow.findViewById(R.id.counterLabel);
        TextView counterValue = (TextView) counterRow.findViewById(R.id.counterValue);

        counterId.setText(counterData.getId());
        counterLabel.setText(counterData.getLabel());
        counterValue.setText(counterData.getStringValue());

        counterRow.setBackgroundColor(counterData.getBackgroundColor());

        scrollViewTable.addView(counterRow);
    }

    public void openConfigWindow(View view) {
        //start counter config activity popup window
        //put counter data for counter being edited

        TableRow counterRow = (TableRow) view.getParent();
        TextView counterId = (TextView) counterRow.findViewById(R.id.counterId);
        CounterData counterData = activeCounterList.getCounterData(counterId.getText().toString());

        Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
        intent.putExtra("counterId", counterId.getText().toString());
        intent.putExtra("counterDataString", counterData.toString());
        startActivityForResult(intent, 1);
    }

    private void removeCounter(String idToRemove) {
        //remove counter from active counters and scroll view
        //adjust counterIds of remaining counters to reflect new positions

        //remove view, accessed by slicing numeral in counterId
        scrollViewTable.removeView(scrollViewTable.getChildAt(Integer.valueOf(idToRemove.substring(7))));
        colorManager.addColor(activeCounterList.getCounterData(idToRemove).getBackgroundColor());
        activeCounterList.removeCounter(idToRemove);

        int currentIndex = 0;
        while (currentIndex < getCounterCount()) {
            TableRow counterRow = (TableRow) scrollViewTable.getChildAt(currentIndex);
            TextView counterId = (TextView) counterRow.findViewById(R.id.counterId);

            //update id
            counterId.setText(getString(R.string.counter_label,currentIndex));

            CounterData counterData = activeCounterList.get(currentIndex);
            //Log.i(TAG, "changing current counter's id from -> " + counterData.getId() + " to -> counter" + currentIndex);
            counterData.setId("counter"+currentIndex);
            currentIndex++;
        }
    }

    private void saveAllCounters() {
        //save all counters in saved preferences

        SharedPreferences.Editor editor = getSharedPreferences("counters", MODE_PRIVATE).edit();
        editor.clear();
        int i = 0;
        while (i < getCounterCount()) {
            String id = "counter" + i;
            String counterDataString = activeCounterList.getCounterData(id).toString();
            editor.putString(id, counterDataString);
            i++;
        }
        editor.apply();
    }

    private void restoreAllCounters() {
        //restore all counters from saved preferences

        SharedPreferences savedCounters = getSharedPreferences("counters", MODE_PRIVATE);

        //use index to preserve order in scrollview
        for (int i=0; i<savedCounters.getAll().keySet().size(); i++) {

            String counterDataString = savedCounters.getString("counter" + i, null);
            if (counterDataString != null) {

                String[] counterDataSplit = counterDataString.split(" ");
                CounterData restoredCounter = activeCounterList.restoreCounter("counter"+i,
                        counterDataSplit[0], counterDataSplit[1], counterDataSplit[2],
                        counterDataSplit[3], counterDataSplit[4]);

                addCounterToScrollView(restoredCounter);

                //remove color so it is not repeated in new counters
                colorManager.removeColor(restoredCounter.getBackgroundColor());
            }
        }
    }

    private void resetAllCounters() {
        //reset all counters to zero
        //change in both scrollview and activeCounterList

        for (int i=0; i<getCounterCount(); i++) {
            TableRow counterRow = (TableRow) scrollViewTable.getChildAt(i);
            TextView counterValue = (TextView) counterRow.findViewById(R.id.counterValue);
            counterValue.setText("0");
        }

        activeCounterList.resetAll();
    }

    private void removeAllCounters() {
        //remove all counters and reset colors
        getSharedPreferences("counters", MODE_PRIVATE).edit().clear().apply();
        setCounterColors();
        scrollViewTable.removeAllViews();
        activeCounterList.clear();
    }

    private void openResetAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Reset all counters?")
                .setMessage("Are you sure? This will set all counters to zero and cannot be undone")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetAllCounters();
                    }
                })
                .show();
    }

    private void openRemoveAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Remove all counters?")
                .setMessage("Are you sure? This will permanently remove all active counters")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllCounters();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.remove_all_counters:
                openRemoveAllDialog();
                break;
            case R.id.reset_all_counters:
                openResetAllDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
