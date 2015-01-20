package de.feigl.warmupcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.feigl.warmupcalculator.Database.WeightsDatabase;


public class MainActivity extends ActionBarActivity {
    private EditText startingWeight;
    private EditText endingWeight;
    private TextView increment;
    private TextView tvIncrementNumber;
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
    private float starting;
    ArrayList<Double> plates;

    WeightsDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new WeightsDatabase(this);

        instantiateWeights();
        increment = (TextView) findViewById(R.id.tv_increment);
        tvIncrementNumber = (TextView) findViewById(R.id.tv_increment_number);
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
                if (db.getAllWeights().size() != 0) {
                    calculateLastSetsAndSetText();
                }
                changeStartingWeightValue();
            }
        });

        endingWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                setIncrementText();
                calculateFirstTwoSets();
                if (db.getAllWeights().size() != 0) {
                    calculateLastSetsAndSetText();
                }

                if (!hasFocus) {
                    hideKeyboard();
                }
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

        changeStartingWeightValue();

        incrementNumber = calculateIncrement();
    }

    private void calculateLastSetsAndSetText() {
        calculateThirdSet();
        calculateForthSet();
        calculateFifthSet();
        setFifthSetPlates();
        setForthSetPlates();
        setThirdSetPlates();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void checkForEmptyPlates() {
        if (db.getAllWeights().size() == 0) {
            setDialogNoWeights();
        }

        plates = db.getAllWeights();
    }

    private void changeStartingWeightValue() {
        if (startingWeight.getText().toString().equals("")) {
            starting = 0;
        } else {
            starting = Float.parseFloat(startingWeight.getText().toString());
        }
    }

    private void setPlates() {
        db.getAllWeights();
    }

    private void setIncrementText() {
        increment.setText(R.string.increment_string);
        tvIncrementNumber.setText(incrementNumber + "kg");
    }

    //calculating the set weights/////////////////////////////////////////////////////////////////////////
    private void calculateFifthSet() {
        incrementNumber = calculateIncrement();
        float step = incrementNumber;
        double weight = starting + (step * 3) - ((starting + (step * 3)) % new Double(plates.get(plates.size() - 1).toString()));
        fifthSet.setText("2 x " + weight);
    }

    private void calculateForthSet() {
        float step = calculateIncrement();
        double weight = starting + (step * 2) - ((starting + (step * 3)) % new Double(plates.get(plates.size() - 1).toString()));
        forthSet.setText("3 x " + weight);

    }

    private void calculateThirdSet() {
        float step = incrementNumber;
        double weight = starting + step - ((starting + (step * 3)) % new Double(plates.get(plates.size() - 1).toString()));
        thirdSet.setText("5 x " + weight);
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
        double lightestPlate = 0;
        if (plates.size() != 0) {
            lightestPlate = plates.get(plates.size() - 1);
        }

        ArrayList<Double> result = new ArrayList<Double>();

        while (oneSide >= lightestPlate) {
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
        float step = calculateIncrement();
        double weight = starting + (step * 3) - ((starting + (step * 3)) % new Double(plates.get(plates.size() - 1).toString()));
        ArrayList<Double> platesList = getPlates(weight, db.getAllWeights());
        String platesString = "";
        for (int i = 0; i < platesList.size(); i++) {
            platesString = platesString.concat(platesList.get(i).toString() + ", ");
        }
        if (platesString.length() > 0)
            platesString = platesString.substring(0, platesString.length() - 2);
        fifthSetPlates.setText(platesString);
    }

    private void setForthSetPlates() {
        float step = calculateIncrement();
        double weight = starting + (step * 2) - ((starting + (step * 3)) % new Double(plates.get(plates.size() - 1).toString()));
        ArrayList<Double> platesList = getPlates(weight, db.getAllWeights());
        String platesString = "";
        for (int i = 0; i < platesList.size(); i++) {
            platesString = platesString.concat(platesList.get(i).toString() + ", ");
        }
        if (platesString.length() > 0)
            platesString = platesString.substring(0, platesString.length() - 2);
        forthSetPlates.setText(platesString);
    }

    private void setThirdSetPlates() {
        float step = calculateIncrement();
        double weight = starting + (step) - ((starting + (step * 3)) % new Double(plates.get(plates.size() - 1).toString()));
        ArrayList<Double> platesList = getPlates(weight, db.getAllWeights());
        String platesString = "";
        for (int i = 0; i < platesList.size(); i++) {
            platesString = platesString.concat(platesList.get(i).toString() + ", ");
        }
        if (platesString.length() > 0)
            platesString = platesString.substring(0, platesString.length() - 2);
        thirdSetPlates.setText(platesString);
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
        if (id == R.id.action_weights) {
            Intent i = new Intent(MainActivity.this, WeightsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDialogNoWeights() {
        AlertDialogPro.Builder alertDialogBuilder = new AlertDialogPro.Builder(MainActivity.this);
        // set dialog message
        alertDialogBuilder
                .setTitle(R.string.no_weights_string)
                .setMessage(R.string.no_weights_dialog_string)
                .setCancelable(false)
                .setPositiveButton(R.string.add_weight_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(MainActivity.this, WeightsActivity.class);
                        dialog.dismiss();
                        startActivity(i);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForEmptyPlates();
        calculateLastSetsAndSetText();
    }

    //    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//    }

    /**
     * This class makes the ad request and loads the ad.
     */
    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /**
         * Called when leaving the activity
         */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /**
         * Called when returning to the activity
         */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /**
         * Called before the activity is destroyed
         */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }
}