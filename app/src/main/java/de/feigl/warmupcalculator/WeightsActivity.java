package de.feigl.warmupcalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;

import java.util.ArrayList;

import de.feigl.warmupcalculator.Database.WeightsDatabase;

public class WeightsActivity extends ActionBarActivity {

    RecyclerView mRecyclerView;
    WeightsAdapter mAdapter;
    WeightsDatabase db;
    ArrayList<Double> weightsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);

        db = new WeightsDatabase(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new WeightsAdapter(db.getAllWeights(), R.layout.row_weights, this);
        mRecyclerView.setAdapter(mAdapter);
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

    private void setDialogAddWeight() {
        AlertDialogPro.Builder alertDialogBuilder = new AlertDialogPro.Builder(WeightsActivity.this)
                .setTitle(R.string.add_weight_title_string)
                .setMessage(R.string.add_weight_dialog_string);

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setTextColor(Color.BLACK);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton(R.string.done_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.addWeight(Double.parseDouble(input.getText().toString()));
                mAdapter.swapWeights(db.getAllWeights());
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.show();
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
