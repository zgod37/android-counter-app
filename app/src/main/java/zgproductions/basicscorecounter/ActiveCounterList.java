package zgproductions.basicscorecounter;

import java.util.ArrayList;

/**
 * Created by zgod on 2/17/2016.
 *
 * Customized ArrayList to store the information of all active counters
 * Handles code for creating and updating CounterData objects
 *
 */
public class ActiveCounterList extends ArrayList<CounterData> {

    public CounterData addNewCounter(String id, String label, int backgroundColor) {
        CounterData newCounterData = new CounterData(id, label, backgroundColor);
        add(newCounterData);
        return newCounterData;
    }

    public void removeCounter(String id) {
        remove(getCounterData(id));
    }

    public CounterData getCounterData(String id) {
        for (CounterData counter : this) {
            if (counter.getId().equals(id)) {
                return counter;
            }
        }
        return null;
    }

    public CounterData restoreCounter(String id, String label, String value, String backgroundColor,
                                      String stepUp, String stepDown) {
        CounterData restoredCounterData =  new CounterData(id, label, value, backgroundColor, stepUp, stepDown);
        add(restoredCounterData);
        return restoredCounterData;
    }

    public void resetAll() {
        for (CounterData counter : this) {
            counter.setValue(0);
        }
    }
}
