package de.feigl.warmupcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends Activity {
    private EditText startingWeight;
    private EditText endingWeight;
    private TextView increment;
    private TextView firstSet;
    private TextView secondSet;
    private TextView thirdSet;
    private TextView forthSet;
    private TextView fifthSet;

    private TextView firstSetPlates;
    private TextView secondSetPlates;
    private TextView thirdSetPlates;
    private TextView forthSetPlates;
    private TextView fifthSetPlates;
    private float incrementNumber;
    ArrayList<Double> plates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instantiateWeights();
        increment = (TextView) findViewById(R.id.tv_increment);
        instantiateSetStrings();
        instantiatePlatesString();

        plates = new ArrayList<Double>();
        setPlates();
        setIncrementText();
        calculateFirstTwoSets();
        startingWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setIncrementText();
                calculateFirstTwoSets();
                calculateThirdSet();
                calculateThirdSet();
                calculateForthSet();
                calculateFifthSet();
                setFifthSetPlates();
                setForthSetPlates();
                setThirdSetPlates();
            }
        });

        endingWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setIncrementText();
                calculateFirstTwoSets();
                calculateThirdSet();
                calculateForthSet();
                calculateFifthSet();
                setFifthSetPlates();
                setForthSetPlates();
                setThirdSetPlates();
            }
        });

        endingWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE)
                    endingWeight.clearFocus();
                return false;
            }
        });
    }

    private void setPlates() {
        plates.add(1.25);
        plates.add(2.5);
        plates.add(5.0);
        plates.add(10.0);
        plates.add(15.0);
        plates.add(20.0);
        plates.add(25.0);
    }

    private void setIncrementText() {
        incrementNumber = calculateIncrement();
        increment.setText("Increment: " + incrementNumber);
    }

    //calculating the set weights/////////////////////////////////////////////////////////////////////////
    private void calculateFifthSet() {
        if (!startingWeight.getText().toString().equals("") && !endingWeight.getText().toString().equals("")) {
            float starting = Float.parseFloat(startingWeight.getText().toString());
            float step = calculateIncrement();
            double weight = starting + (step * 3) - ((starting + (step * 3)) % new Double(plates.get(6).toString()));
            fifthSet.setText("2 x " + weight);
        }
    }

    private void calculateForthSet() {
        if (!startingWeight.getText().toString().equals("") && !endingWeight.getText().toString().equals("")) {
            float starting = Float.parseFloat(startingWeight.getText().toString());
            float step = calculateIncrement();
            double weight = starting + (step * 2) - ((starting + (step * 3)) % new Double(plates.get(6).toString()));
            forthSet.setText("3 x " + weight);
        }
    }

    private void calculateThirdSet() {
        if (!startingWeight.getText().toString().equals("") && !endingWeight.getText().toString().equals("")) {
            float starting = Float.parseFloat(startingWeight.getText().toString());
            float step = calculateIncrement();
            double weight = starting + step - ((starting + (step * 3)) % new Double(plates.get(6).toString()));
            thirdSet.setText("5 x " + weight);
        }
    }

    private void calculateFirstTwoSets() {
        firstSet.setText("5 x " + startingWeight.getText().toString());
        secondSet.setText("5 x " + startingWeight.getText().toString());
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

    //calculates the increment of the single sets an sets it in the textView
    private float calculateIncrement() {
        float incrementValue = 0;
        if (!startingWeight.getText().toString().equals("") && !endingWeight.getText().toString().equals("")) {
            float startWeight = Float.parseFloat(startingWeight.getText().toString());
            float endWeight = Float.parseFloat(endingWeight.getText().toString());
            incrementValue = (endWeight - startWeight) / 4;
        }
        return incrementValue;
    }

    private void instantiateWeights() {
        startingWeight = (EditText) findViewById(R.id.et_starting_weight);
        endingWeight = (EditText) findViewById(R.id.et_end_weight);
    }

    private void instantiateSetStrings() {
        firstSet = (TextView) findViewById(R.id.tv_first_set);
        secondSet = (TextView) findViewById(R.id.tv_second_set);
        thirdSet = (TextView) findViewById(R.id.tv_third_set);
        forthSet = (TextView) findViewById(R.id.tv_forth_set);
        fifthSet = (TextView) findViewById(R.id.tv_fifth_set);
    }

    private void instantiatePlatesString() {
        firstSetPlates = (TextView) findViewById(R.id.tv_first_set_plates);
        secondSetPlates = (TextView) findViewById(R.id.tv_second_set_plates);
        thirdSetPlates = (TextView) findViewById(R.id.tv_third_set_plates);
        forthSetPlates = (TextView) findViewById(R.id.tv_forth_set_plates);
        fifthSetPlates = (TextView) findViewById(R.id.tv_fifth_set_plates);
    }

    private ArrayList<Double> getPlates(double trainingWeight, ArrayList<Double> plates) {

        double oneSide = (trainingWeight - 20) / 2;
        double lightest_plate = plates.get(6);

        Collections.sort(plates, new Comparator<Double>() {
            @Override
            public int compare(Double c1, Double c2) {
                return Double.compare(c2, c1);
            }
        });

        ArrayList<Double> result = new ArrayList<Double>();

        while (oneSide >= lightest_plate) {
            Boolean found = false;
            for (int i = 0; i < plates.size(); i++) {
                double plate = plates.get(i);
                if (oneSide - plate >= 0) {
                    result.add(plate);
                    oneSide = oneSide - plate;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        if (oneSide > 0) {
            return null;
        }
        return result;
    }

    private void setFifthSetPlates() {
        if (endingWeight.getText().toString().equals("")) {
            endingWeight.setText("0");
        }
        ArrayList<Double> platesList = getPlates(Double.valueOf(endingWeight.getText().toString()), plates);
        String platesString = "";
        for (int i = 0; i < platesList.size(); i++) {
            platesString = platesString.concat(platesList.get(i).toString() + ", ");
        }
        fifthSetPlates.setText(platesString);
    }

    private void setForthSetPlates() {
        if (endingWeight.getText().toString().equals("")) {
            endingWeight.setText("0");
        }
        ArrayList<Double> platesList = getPlates(Double.valueOf(endingWeight.getText().toString()), plates);
        String platesString = "";
        for (int i = 0; i < platesList.size(); i++) {
            platesString = platesString.concat(platesList.get(i).toString() + ", ");
        }
        fifthSetPlates.setText(platesString);
    }

    private void setThirdSetPlates() {
        if (endingWeight.getText().toString().equals("")) {
            endingWeight.setText("0");
        }
        ArrayList<Double> platesList = getPlates(Double.valueOf(endingWeight.getText().toString()), plates);
        String platesString = "";
        for (int i = 0; i < platesList.size(); i++) {
            platesString = platesString.concat(platesList.get(i).toString() + ", ");
        }
        fifthSetPlates.setText(platesString);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
