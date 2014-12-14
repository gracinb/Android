//-----------------------------------------------------------------------------
//UI/Wire Frame: Manuel Anastsakis & Nabil Azam
//Back Code: Tien Dam & Brian Gracin
//Course: IST 402 - Android Development
//Instructor: Joe Oakes
//-----------------------------------------------------------------------------
package com.example.group1.mcburgertown;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.content.Intent;


public class PaymentActivity extends Activity {

    public String mainText;
    public String passLoc;
    private EditText Summary;
    private EditText TotalBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        //Gets intent that triggered this activity along with extra
        Intent intent = getIntent();
        mainText = intent.getStringExtra("tTot");

        //Assign EditText elements to variables
        Summary = (EditText) findViewById(R.id.orderPSummary);
        TotalBox = (EditText) findViewById(R.id.orderPTotal);

        //Splits extra string and assigns pieces
        String[] Splitter = mainText.split("\\*");
        Summary.setText(Splitter[1]);
        TotalBox.setText(Splitter[0]);
        passLoc = Splitter[2];

        //Assign btnListener to buttons
        ButtonListener btnListener = new ButtonListener();
        (findViewById(R.id.btnCredit)).setOnClickListener(btnListener);
        (findViewById(R.id.btnCash)).setOnClickListener(btnListener);
        (findViewById(R.id.btnBack)).setOnClickListener(btnListener);
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

    public void toMain(View view)
    {
        Intent intentMain = new Intent(this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentMain);
    }

    public void toEnd(View view)
    {
        Intent intentEnd = new Intent(this, MainActivity.class);
        intentEnd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentEnd.putExtra("End", TotalBox.getText().toString() + "*" + passLoc);
        startActivity(intentEnd);
    }

    public void toCard(View view)
    {
        Intent intentCard = new Intent(this, CardActivity.class);
        String total = TotalBox.getText().toString() + "*" + Summary.getText().toString() +
                "*" + passLoc;
        intentCard.putExtra("tTot",total);
        startActivity(intentCard);
    }

    public class ButtonListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            //Goes to credit page when Card clicked
            if (view.getId() == R.id.btnCredit)
            {
                toCard(view);
            }
            //Passes order info and then clears data when Cash clicked
            if (view.getId() == R.id.btnCash)
            {
                toEnd(view);
            }
            //Goes back to main page when Back clicked
            if (view.getId() == R.id.btnBack)
            {
                toMain(view);
            }
        }

    }
}
