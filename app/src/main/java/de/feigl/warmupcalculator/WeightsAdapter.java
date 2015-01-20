package de.feigl.warmupcalculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;

import java.util.ArrayList;
import java.util.List;

import de.feigl.warmupcalculator.Database.WeightsDatabase;

public class WeightsAdapter extends RecyclerView.Adapter<WeightsAdapter.ViewHolder>{

    private ArrayList<Double> weights;
    private int rowLayout;
    private Context mContext;

    public WeightsAdapter(ArrayList<Double> weight, int rowLayout, Context context) {
        this.weights = weight;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    public void swapWeights(ArrayList<Double> weights){
        this.weights = weights;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Double weight = weights.get(i);
        viewHolder.tvWeight.setText(weight.toString() + " kg");
        viewHolder.ivDumbbell.setImageResource(R.drawable.dumbbell);
        viewHolder.ivDelete.setImageResource((R.drawable.delete));

        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialogDeleteWeight(weight);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weights == null ? 0 : weights.size();
    }

    private void setDialogDeleteWeight(final double weight){
        AlertDialogPro.Builder alertDialogBuilder = new AlertDialogPro.Builder(mContext);
        // set dialog message
        alertDialogBuilder
                .setTitle(R.string.delete_weight_title_string)
                .setMessage(R.string.delete_weight_dialog_string)
                .setCancelable(false)
                .setPositiveButton(R.string.delete_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WeightsDatabase db = new WeightsDatabase(mContext);
                        db.deleteWeight(weight);
                        swapWeights(db.getAllWeights());
                    }
                })
                .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWeight;
        public ImageView ivDumbbell;
        public ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWeight = (TextView) itemView.findViewById(R.id.tvWeight);
            ivDumbbell = (ImageView)itemView.findViewById(R.id.ivDumbbell);
            ivDelete = (ImageView)itemView.findViewById(R.id.ivDelete);
        }

    }
}
