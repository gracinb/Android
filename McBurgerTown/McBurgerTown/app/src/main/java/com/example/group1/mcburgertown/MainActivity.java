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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.Intent;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    private String theEnd;
    private Spinner Food;
    private Spinner Drink;
    private Spinner Location;
    private EditText Summary;
    private EditText TotalBox;
    private ArrayList<String> entrees = null;
    private ArrayList<String> drinks = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream in_s;

        //Attempt to read in XML file
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            in_s = getApplicationContext().getAssets().open("menu_values.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            //Calls method to parse XML file given parser
            parseXML(parser);

            in_s.close();

        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Get intent that returned to this page
        Intent intent = getIntent();
        theEnd = intent.getStringExtra("End");

        //Assign Spinner and EditText elements to variables
        Food = (Spinner) findViewById(R.id.entreeSpinner);
        Drink = (Spinner) findViewById(R.id.drinkSpinner);
        Location = (Spinner) findViewById(R.id.locationSpinner);
        Summary = (EditText) findViewById(R.id.orderSummary);
        TotalBox = (EditText) findViewById(R.id.orderTotal);

        //Create ArrayAdapter to populate spinner
        ArrayAdapter<String> entreeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,entrees);
        ArrayAdapter<String> drinkAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,drinks);

        //Set dropdown resource type
        entreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Assign btnListener to buttons
        ButtonListener btnListener = new ButtonListener();
        (findViewById(R.id.btnOrderAdd)).setOnClickListener(btnListener);
        (findViewById(R.id.btnOrderClear)).setOnClickListener(btnListener);
        (findViewById(R.id.btnOrderSubmit)).setOnClickListener(btnListener);

        //Set adapters to spinners
        Food.setAdapter(entreeAdapter);
        Drink.setAdapter(drinkAdapter);

        //Check if theEnd string was passed
        if (theEnd != null)
        {
            popUp();
        }
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        String entreeCurrent;
        String drinkCurrent;

        //Reads in data while data remains
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    entrees = new ArrayList<String>();
                    drinks = new ArrayList<String>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    if (name.equals("entree")) {
                        entreeCurrent = parser.nextText();
                        entrees.add(entreeCurrent);
                    }
                    if (name.equals("drink")) {
                        drinkCurrent = parser.nextText();
                        drinks.add(drinkCurrent);
                    }
            }
            eventType = parser.next();
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
            if (view.getId() == R.id.btnOrderAdd) {
                selectedFood = Food.getSelectedItem().toString();
                selectedDrink = Drink.getSelectedItem().toString();

                //Checks if default state
                if (selectedFood.equals("Select Entree")) {
                    selectedFood = null;
                }
                //If not default state
                else {
                    Summary.append(selectedFood + "\n");
                    String[] SplitPrice = selectedFood.split("\\$");
                    foodPrice = Double.parseDouble(SplitPrice[1]);
                }

                //Checks if default state
                if (selectedDrink.equals("Select Drink")) {
                    selectedDrink = null;

                }
                //If not default state
                else {
                    Summary.append(selectedDrink + "\n");
                    String[] SplitDrinkPrice = selectedDrink.split("\\$");
                    drinkPrice = Double.parseDouble(SplitDrinkPrice[1]);
                }

                //Updates and displays running total
                Total += foodPrice + drinkPrice;
                TotalBox.setText("$ " + Total.toString());

                //Clears information for next order
                Food.setSelection(0);
                Drink.setSelection(0);
                foodPrice = 0.00;
                drinkPrice = 0.00;

            }
            //Clears spinners and EditTexts when Clear button clicked
            if (view.getId() == R.id.btnOrderClear) {
                Location.setSelection(0);
                Food.setSelection(0);
                Drink.setSelection(0);
                Summary.setText(null);
                orderNumber = 0;
                TotalBox.setText(" ");
                Total = 0.00;
            }
            //Goes to payment page with intent when Pay Now clicked
            if (view.getId() == R.id.btnOrderSubmit)
            {
                toPayment(view);
            }
        }
    }
}

