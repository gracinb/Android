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

//Paypal imports
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;

import android.util.Log;

import org.json.JSONException;

import java.math.BigDecimal;
//End Paypal imports

public class MainActivity extends Activity {

    private String theEnd;
    private Spinner Food;
    private Spinner Drink;
    private Spinner Location;
    private EditText Summary;
    private EditText TotalBox;
    private ArrayList<String> entrees = null;
    private ArrayList<String> drinks = null;

    //Paypal object
    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("AfFXrxCQed8pD7PSNrRirV4q252SpwUaZv2Eq11hftDVO8PHNPU5oK5f6mVT");
    //End Paypal object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Paypal intent
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        //End Paypal intent

        //Declare InputStream that will be used
        InputStream in_s;

        //Attempt to read in XML file
        XmlPullParserFactory pullParserFactory;
        try {
            //Creates parser factory instance
            pullParserFactory = XmlPullParserFactory.newInstance();
            //Creates parser from factory instance
            XmlPullParser parser = pullParserFactory.newPullParser();

            //Open xml to be read
            //xml file is in the assets folder
            in_s = getApplicationContext().getAssets().open("menu_values.xml");
            //Set to not process namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            //Assign InputStream with xml document to parser
            parser.setInput(in_s, null);

            //Calls method to parse XML file given parser
            parseXML(parser);

            //Close the xml file, from literature not required but good practice
            in_s.close();

        //Catch error with parser
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        //Catch error opening/reading xml file
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Get intent that returned to this page
        Intent intentEnd = getIntent();
        theEnd = intentEnd.getStringExtra("End");

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

    //Added for Paypal
    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        //Int to track parser's event type
        //getEventType is (START_TAG, END_TAG, TEXT, etc.) returned as an int
        int eventType = parser.getEventType();

        //String used to hold items as they are read in from xml
        String entreeCurrent;
        String drinkCurrent;

        //Reads in data while data remains
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                //Creates string arrays to hold on input if eventType is START_DOCUMENT
                case XmlPullParser.START_DOCUMENT:
                    entrees = new ArrayList<String>();
                    drinks = new ArrayList<String>();
                    break;
                //If eventType is START_TAG it gets and checks the tag name and performs an action
                //If name the name matches it gets field information and stores it in the correct
                //  string ArrayList
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
            //Tells parser to move on by setting new eventType
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
                //toPayment(view);
                // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
                // Change PAYMENT_INTENT_SALE to
                //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
                //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
                //     later via calls from your server.

                PayPalPayment payment = new PayPalPayment(new BigDecimal("1.75"), "USD", "hipster jeans",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                startActivityForResult(intent, 0);
            }
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }
}

