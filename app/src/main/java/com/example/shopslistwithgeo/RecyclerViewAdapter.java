package com.example.shopslistwithgeo;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Shop> myShopsList;
    private android.support.v7.app.AlertDialog.Builder alertDialogBuilder;
    private android.support.v7.app.AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Shop> groceryItems) {
        this.context = context;
        this.myShopsList = groceryItems;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        Shop grocery = myShopsList.get(position);
        holder.groceryItemName.setText(grocery.getName());
        holder.location.setText(" long: " + grocery.getLongitude() + " lat: " + grocery.getLatitude());
        holder.description.setText(grocery.getDescription());
        holder.range.setText(grocery.getRange());


    }

    @Override
    public int getItemCount() {
        return myShopsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groceryItemName;
        public TextView location;
        public TextView description;
        public TextView range;
        public Button editButton;
        public Button deleteButton;
        public int id;


        public ViewHolder(View view, Context ctx) {
            super(view);

            context = ctx;
            groceryItemName = (TextView) view.findViewById(R.id.name);
            location = (TextView) view.findViewById(R.id.location);
            description = (TextView) view.findViewById(R.id.description);
            range = (TextView) view.findViewById(R.id.range);
            editButton = (Button) view.findViewById(R.id.editButton);
            deleteButton = (Button) view.findViewById(R.id.deleteButton);
            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next screen/ DetailsActivity
                    int position = getAdapterPosition();

                    Shop shop = myShopsList.get(position);

                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.editButton:
                    int position = getAdapterPosition();
                    Shop shop = myShopsList.get(position);
                    editItem(shop);

                    break;
                case R.id.deleteButton:
                    position = getAdapterPosition();
                    shop = myShopsList.get(position);
                    Log.d("Where ", "Position: " + position + "Shop id: " + shop.getId());
                     deleteItem(shop.getId());
                    break;

                }
        }



        public void deleteItem(final int id) {

            //create an AlertDialog
            alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_dialog, null);

            Button noButton = (Button) view.findViewById(R.id.noButton);
            Button yesButton = (Button) view.findViewById(R.id.yesButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();


            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete the item.
                    DatabaseHandler db = new DatabaseHandler(context);
                    //delete item
                    db.deleteShop(id);
                    myShopsList.remove(getAdapterPosition()); //Pozycja adaptera i id nie jest takie samo!
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();


                }
            });

        }


        public void editItem(final Shop grocery) {

            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);

            final EditText shopItem = (EditText) view.findViewById(R.id.shopItem);
            final EditText description = (EditText) view.findViewById(R.id.shopDescription);
            final EditText range = (EditText) view.findViewById(R.id.shopRange);

            // title.setText("Edit Grocery");
            Button saveButton = (Button) view.findViewById(R.id.saveButton);


            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler db = new DatabaseHandler(context);

                  //  Update item
                    grocery.setName(shopItem.getText().toString());
                    grocery.setDescription(description.getText().toString());
                    grocery.setRange(range.getText().toString());

                    if (!shopItem.getText().toString().isEmpty()
                            && !description.getText().toString().isEmpty()
                            && !range.getText().toString().isEmpty()) {

                        db.updateShop(grocery);
                        notifyItemChanged(getAdapterPosition(),grocery);
                    }else {
                        //Snackbar.make(view, "Add Product and Quantity", Snackbar.LENGTH_LONG).show();
                    }

                    dialog.dismiss();
                }
            });

        }
    }
}