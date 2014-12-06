//-----------------------------------------------------------------------------
//UI/Wire Frame: Manuel Anastsakis & Nabil Azam
//Back Code: Tien Dam & Brian Gracin
//Course: IST 402 - Android Development
//Instructor: Joe Oakes
//-----------------------------------------------------------------------------
package com.example.group1.mcburgertown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.Intent;

import java.util.Random;


public class MainActivity extends Activity {

    private String theEnd;
    private Spinner Food;
    private Spinner Drink;
    private Spinner Location;
    private EditText Summary;
    private EditText TotalBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        theEnd = intent.getStringExtra("End");

        Food = (Spinner) findViewById(R.id.entreeSpinner);
        Drink = (Spinner) findViewById(R.id.drinkSpinner);
        Location = (Spinner) findViewById(R.id.locationSpinner);
        Summary = (EditText) findViewById(R.id.orderSummary);
        TotalBox = (EditText) findViewById(R.id.orderTotal);

        ButtonListener btnListener = new ButtonListener();
        (findViewById(R.id.btnOrderAdd)).setOnClickListener(btnListener);
        (findViewById(R.id.btnOrderClear)).setOnClickListener(btnListener);

        if (theEnd != null)
        {
            popUp();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toPayment(View view)
    {
        Intent intentPayment = new Intent(this, PaymentActivity.class);
        String total = TotalBox.getText().toString() + "*" + Summary.getText().toString()
                + "*" + Location.getSelectedItem().toString();
        intentPayment.putExtra("tTot",total);
        startActivity(intentPayment);
    }

    public void popUp ()
    {
        final Random rand = new Random();
        String[] Splitter = theEnd.split("\\*");
        final int min = 100000;
        final int max = 900000;
        final int random = rand.nextInt((max - min) + 1) + min;

        new AlertDialog.Builder(this)
                .setTitle("YourOrder")
                .setMessage("Your order was: " + Splitter[0] + "\n" + "You picked: " + Splitter[1]
                        + "\n" + "Order number: " + random )
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public class ButtonListener implements OnClickListener {

        String selectedFood = null;
        String selectedDrink = null;
        Integer orderNumber = 0;
        Double Total = 0.00;
        Double foodPrice = 0.00;
        Double drinkPrice = 0.00;

        @Override
        public void onClick(View view) {
            //activity_main
            if (view.getId() == R.id.btnOrderAdd) {
                selectedFood = Food.getSelectedItem().toString();
                selectedDrink = Drink.getSelectedItem().toString();

                if (selectedFood.equals("Select Entree")) {
                    selectedFood = null;
                    //return;
                } else {
                    Summary.append(selectedFood + "\n");
                    String[] SplitPrice = selectedFood.split("\\$");
                    foodPrice = Double.parseDouble(SplitPrice[1]);
                }
                if (selectedDrink.equals("Select Drink")) {
                    selectedDrink = null;

                } else {
                    Summary.append(selectedDrink + "\n");
                    String[] SplitDrinkPrice = selectedDrink.split("\\$");
                    drinkPrice = Double.parseDouble(SplitDrinkPrice[1]);
                }
                Total += foodPrice + drinkPrice;
                TotalBox.setText("$ " + Total.toString());

                Food.setSelection(0);
                Drink.setSelection(0);
                foodPrice = 0.00;
                drinkPrice = 0.00;

            }
            if (view.getId() == R.id.btnOrderClear) {
                Location.setSelection(0);
                Food.setSelection(0);
                Drink.setSelection(0);
                Summary.setText(null);
                orderNumber = 0;
                TotalBox.setText(" ");
                Total = 0.00;
            }
        }
    }
}

