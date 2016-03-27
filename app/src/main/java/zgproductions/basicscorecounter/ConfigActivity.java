package zgproductions.basicscorecounter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by zgod on 2/20/2016.
 *
 * TODO:
 * clean up layouts - something like make border around value section for more clear delineation
 * animating the opening/closing of the popup window would be cool
 *
 *
 * Pop up activity window that allows user to configure counter settings
 * Users can change counter labels, manually edit counter's value,
 * increment/decrement values, and background color of counter row
 *
 */

public class ConfigActivity extends Activity {

    private EditText changeLabel;
    private EditText changeValue;
    private EditText changeIncrement;
    private EditText changeDecrement;

    private Spinner colorSpinner;

    private String counterId;
    private String background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        changeLabel = (EditText) findViewById(R.id.changeLabel);
        changeValue = (EditText) findViewById(R.id.changeValue);
        changeIncrement = (EditText) findViewById(R.id.changeIncrement);
        changeDecrement = (EditText) findViewById(R.id.changeDecrement);

        Intent intent = getIntent();
        String[] counterDataSplit = intent.getStringExtra("counterDataString").split(" ");
        counterId = intent.getStringExtra("counterId");

        changeLabel.setText(counterDataSplit[0]);
        changeValue.setText(counterDataSplit[1]);
        changeIncrement.setText(counterDataSplit[3]);
        changeDecrement.setText(counterDataSplit[4]);

        background = counterDataSplit[2];

        colorSpinner = (Spinner) findViewById(R.id.colorSpinner);
        setUpColorSpinner();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Window theWindow = getWindow();
        theWindow.setLayout((int)(displayMetrics.widthPixels*.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        theWindow.setGravity(Gravity.CENTER);
    }

    private void setUpColorSpinner() {
        //initialize color spinner with custom adapter (defined below)

        ArrayList<Integer> colors = ColorManager.getAllColors(getResources().getIntArray(R.array.counter_colors));
        String[] names = getResources().getStringArray(R.array.color_names);
        ColorArrayAdapter adapter = new ColorArrayAdapter(this, names, colors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setSelection(adapter.getPosition(background));

        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //******** BUG FIX ********
                //if user rotates screen for some reason this is called
                //and view will be null, so check first to avoid NPE
                if (view != null) {
                    ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                    background = Integer.toString(colorDrawable.getColor());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class ColorArrayAdapter extends ArrayAdapter<String> {
        //custom adapter for spinner, displays generic color names
        //with associated background color

        private ArrayList<Integer> mColors;

        public ColorArrayAdapter(Context context, String[] names, ArrayList<Integer> colors) {
            super(context, android.R.layout.simple_spinner_item, names);
            this.mColors = colors;
        }

        @Override
        public int getPosition(String item) {
            //override to position in mColors array
            //for setting default item of spinner

            return mColors.indexOf(Integer.parseInt(item));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setBackgroundColor(mColors.get(position));
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            view.setBackgroundColor(mColors.get(position));
            return view;
        }

    }

    public void updateCounterData(View view) {
        //send new counter values back to main activity

        Intent intent = new Intent();
        String counterDataString = changeLabel.getText().toString() + " " +
                changeValue.getText().toString() + " " +
                background + " " +
                changeIncrement.getText().toString() + " " +
                changeDecrement.getText().toString();

        intent.putExtra("counterId", counterId);
        intent.putExtra("counterDataString", counterDataString);

        setResult(RESULT_OK, intent);
        finish();

    }

    public void deleteCounter(View view) {
        //send counterId to be removed back to main activity

        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
        builder.setTitle("Delete counter?")
                .setMessage("Are you sure you want to delete this counter?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("counterId", counterId);
                        setResult(2, intent);
                        finish();
                    }
                }).show();
    }

    public void resetValues(View view) {
        //reset counter value + steps to default

        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
        builder.setTitle("Reset counter?")
                .setMessage("Are you sure? This will set the counter value to 0 and the step values to 1.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeValue.setText("0");
                        changeIncrement.setText("1");
                        changeDecrement.setText("1");
                    }
                }).show();
    }
}
