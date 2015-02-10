package de.feigl.warmupcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;

import java.util.ArrayList;

import de.feigl.warmupcalculator.Database.WeightsDatabase;

public class WeightsActivity extends ActionBarActivity {

    RecyclerView mRecyclerView;
    WeightsAdapter mAdapter;
    WeightsDatabase db;
    EditText barWeightInput;
    Button saveBarWeightButton;
    Button saveAndBackButton;
    TextView noWeightsHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);
        db = new WeightsDatabase(WeightsActivity.this);

        barWeightInput = (EditText) findViewById(R.id.et_bar_weight);
        noWeightsHint = (TextView) findViewById(R.id.tv_no_weights_hint);

        saveAndBackButton = (Button) findViewById(R.id.b_save_back);
        saveAndBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WeightsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        barWeightInput.setHint(removeFloatDigitIfPossible(db.getBarWeight()) + " kg");
        saveBarWeightButton = (Button) findViewById(R.id.b_save_bar_weight);
        saveBarWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!barWeightInput.getText().toString().equals("")) {
                    db.updateBarWeight(Double.parseDouble(barWeightInput.getText().toString()));
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new WeightsAdapter(db.getAllWeights(), R.layout.row_weights, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private String removeFloatDigitIfPossible(double weight) {
        String result;
        if (weight % 1 == 0) {
            result = String.valueOf((int) weight);
        } else {
            result = String.valueOf(weight);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weights, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_weight) {
            setDialogAddWeight();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        changeVisbilityOfHint();
    }

    public void changeVisbilityOfHint() {
        if (db.getAllWeights().size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            noWeightsHint.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noWeightsHint.setVisibility(View.GONE);
        }
    }

    private void setDialogAddWeight() {
        AlertDialogPro.Builder alertDialogBuilder = new AlertDialogPro.Builder(WeightsActivity.this)
                .setTitle(R.string.add_weight_title_string)
                .setMessage(R.string.add_weight_dialog_string);

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setTextColor(Color.BLACK);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton(R.string.done_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("")) {
                    Toast.makeText(WeightsActivity.this, R.string.no_weight_added_string, Toast.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    changeVisbilityOfHint();
                } else {
                    db.addWeight(Double.parseDouble(input.getText().toString()));
                    mAdapter.swapWeights(db.getAllWeights());

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    changeVisbilityOfHint();
                    dialog.dismiss();
                }
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                changeVisbilityOfHint();
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWeight;
        public ImageView dumbbellImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWeight = (TextView) itemView.findViewById(R.id.tvWeight);
            dumbbellImage = (ImageView) itemView.findViewById(R.id.ivDumbbell);
        }

    }
}
