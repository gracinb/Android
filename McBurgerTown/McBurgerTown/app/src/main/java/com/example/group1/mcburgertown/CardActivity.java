package com.example.group1.mcburgertown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class CardActivity extends Activity {

    public String mainText;
    private EditText Summary;
    private EditText TotalBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        mainText = intent.getStringExtra("tTot");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit);

        String[] Splitter = mainText.split("\\*");
        Summary = (EditText) findViewById(R.id.txtOrder);
        TotalBox = (EditText) findViewById(R.id.orderCTotal);

        Summary.setText(Splitter[1]);
        TotalBox.setText(Splitter[0]);

        ButtonListener btnListener = new ButtonListener();
        (findViewById(R.id.btnCCSubmit)).setOnClickListener(btnListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void toPay(View view)
    {
        Intent intentPay = new Intent(this, MainActivity.class);
        intentPay.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentPay);
    }
    public class ButtonListener implements OnClickListener {


        @Override
        public void onClick(View view) {


        }

    }

}