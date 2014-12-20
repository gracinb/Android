//-----------------------------------------------------------------------------
//UI/Wire Frame: Manuel Anastsakis & Nabil Azam
//Back Code: Tien Dam & Brian Gracin
//Course: IST 402 - Android Development
//Instructor: Joe Oakes
//-----------------------------------------------------------------------------
package com.example.group1.mcburgertown;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.content.Intent;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;


public class LocalPaymentActivity extends Activity {

    private String mainText;
    private String passLoc;
    private EditText Summary;
    private EditText TotalBox;
    private Double Total = 0.00;
    private static final String TAG = "paymentActivity";

    //PayPal object
    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("AfFXrxCQed8pD7PSNrRirV4q252SpwUaZv2Eq11hftDVO8PHNPU5oK5f6mVT");
    //End PayPal object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        final NumberFormat enUS = NumberFormat.getCurrencyInstance(Locale.US);

        //PayPal intent
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        //End PayPal intent

        //Gets intent that triggered this activity along with extra
        Intent intentFromMain = getIntent();
        mainText = intentFromMain.getStringExtra("tTot");

        //Assign EditText elements to variables
        Summary = (EditText) findViewById(R.id.orderPSummary);
        TotalBox = (EditText) findViewById(R.id.orderPTotal);

        //Splits extra string and assigns pieces
        String[] Splitter = mainText.split("\\*");
        Summary.setText(Splitter[1]);
        Total = Double.parseDouble(Splitter[0]);
        TotalBox.setText(enUS.format(Total));
        passLoc = Splitter[2];

        //Assign btnListener to buttons
        ButtonListener btnListener = new ButtonListener();
        (findViewById(R.id.btnCredit)).setOnClickListener(btnListener);
        (findViewById(R.id.btnCash)).setOnClickListener(btnListener);
        (findViewById(R.id.btnBack)).setOnClickListener(btnListener);
    }

    //Added for PayPal
    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toMain()
    {
        Intent intentMain = new Intent(this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentMain);
    }

    public void toEnd()
    {
        Intent intentEnd = new Intent(this, MainActivity.class);
        intentEnd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentEnd.putExtra("End", TotalBox.getText().toString() + "*" + passLoc);
        startActivity(intentEnd);
    }

    public void toEndPayPal()
    {
        Intent intentEnd = new Intent(this, MainActivity.class);
        intentEnd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentEnd.putExtra("End", TotalBox.getText().toString() + "*" + passLoc + "*" + "Transaction Complete");
        startActivity(intentEnd);
    }

    public class ButtonListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            //Goes to PayPal when clicked
            if (view.getId() == R.id.btnCredit)
            {
                //toCard(view);
                // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
                // Change PAYMENT_INTENT_SALE to
                //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
                //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
                //     later via calls from your server.

                PayPalPayment payment = new PayPalPayment(new BigDecimal(Total.toString()), "USD", "Meal at McBurgerTown",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(LocalPaymentActivity.this, com.paypal.android.sdk.payments.PaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);

                startActivityForResult(intent, 0);

            }
            //Passes order info and then clears data when Cash clicked
            if (view.getId() == R.id.btnCash)
            {
                toEnd();
            }
            //Goes back to main page when Back clicked
            if (view.getId() == R.id.btnBack)
            {
                toMain();
            }
        }

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i(TAG, confirm.toJSONObject().toString(4));
                    Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                    toEndPayPal();

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
        else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }
}
