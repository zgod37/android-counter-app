package zgproductions.basicscorecounter;

/**
 * Created by zgod on 2/17/2016.
 *
 * Class to store information for individual counters
 * Two constructors -> one for new counters, one for saved counters
 * Stores a unique id for each counter that corresponds to position on screen
 * e.g. "counter2" = third counter in scroll view
 * Counters are accessed through ActiveCounterList
 */

public class CounterData {

    private String id = "";
    private String label = "";
    private int backgroundColor;
    private int value;
    private int stepUp;
    private int stepDown;

    public CounterData(String id, String label, int backGroundColor) {
        //default constructor for new counters

        this.id = id;
        this.label = label;
        this.backgroundColor = backGroundColor;
        this.value = 0;
        this.stepUp = 1;
        this.stepDown = 1;
    }

    public CounterData(String id, String label, String value, String backgroundColor, String stepUp, String stepDown) {
        //constructor for saved counters

        this.id = id;
        this.label = label;
        this.value = Integer.parseInt(value);
        this.backgroundColor = Integer.parseInt(backgroundColor);
        this.stepUp = Integer.parseInt(stepUp);
        this.stepDown = Integer.parseInt(stepDown);
    }

    @Override
    public String toString() {
        return this.label + " " +
                this.value + " " +
                this.backgroundColor + " " +
                this.stepUp + " " +
                this.stepDown;
    }

    public boolean equalsString(String counterDataString) {
        return this.toString().equals(counterDataString);
    }

    public void setAllValuesFromString(String newCounterDataString) {
        String[] newCounterDataSplit = newCounterDataString.split(" ");
        label = newCounterDataSplit[0];
        value = Integer.parseInt(newCounterDataSplit[1]);
        backgroundColor = Integer.parseInt(newCounterDataSplit[2]);
        stepUp = Integer.parseInt(newCounterDataSplit[3]);
        stepDown = Integer.parseInt(newCounterDataSplit[4]);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getStringValue() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getStepUp() {
        return stepUp;
    }

    public void setStepUp(int stepUp) {
        this.stepUp = stepUp;
    }

    public int getStepDown() {
        return stepDown;
    }

    public void setStepDown(int stepDown) {
        this.stepDown = stepDown;
    }


}
