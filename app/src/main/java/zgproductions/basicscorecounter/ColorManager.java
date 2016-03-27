package zgproductions.basicscorecounter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by owner on 2/17/2016.
 *
 * Class to handle management of background colors for individual counters
 * Colors are pseudo-randomly chosen and not repeated in the scroll view
 * Colors are stored as Integers corresponding to their R.id values
 *
 */

public class ColorManager {

    final static private String TAG = "basicscorecounter.COLOR";
    private ArrayList<Integer> colors;

    public ColorManager() {
        colors = new ArrayList<>();
    }

    protected static ArrayList<Integer> getAllColors(int[] colorIds) {
        ArrayList<Integer> allColors = new ArrayList<>();
        int numOfColors = colorIds.length;
        for (int i=0; i<MainActivity.MAX_COUNTER_LIMIT; i++) {
            allColors.add(colorIds[i % numOfColors]);
        }
        return allColors;
    }

    public void setColors(int[] colorIds) {
        //set colors from list of R colorIds
        colors.clear();
        int numOfColors = colorIds.length;
        for (int i=0; i<MainActivity.MAX_COUNTER_LIMIT; i++) {
            colors.add(colorIds[i % numOfColors]);
        }
        //Collections.shuffle(colors);
    }

    @Override
    public String toString() {
        return "num colors = " + colors.size() + " " + colors.toString();
    }

    public void addColor(int color) {
        if (MainActivity.MAX_COUNTER_LIMIT <= 10 && !colors.contains(color)) {
            colors.add(color);
            //Log.d(TAG, "colors size = " + colors.size() + " " + this.toString());
        } else {
            colors.add(color);
        }
    }

    public void removeColor(Integer colorToBeRemoved) {
        colors.remove(colorToBeRemoved);
    }

    public int getColor() {
        //Log.d(TAG, "Getting color: " + this.toString());
        return colors.remove(0);
    }


}
