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

        Intent intent = getIntent();
        mainText = intent.getStringExtra("tTot");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        String[] Splitter = mainText.split("\\*");
        Summary = (EditText) findViewById(R.id.orderPSummary);
        TotalBox = (EditText) findViewById(R.id.orderPTotal);
        passLoc = Splitter[2];

        Summary.setText(Splitter[1]);
        TotalBox.setText(Splitter[0]);

        ButtonListener btnListener = new ButtonListener();
        (findViewById(R.id.btnCash)).setOnClickListener(btnListener);
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
        String total = TotalBox.getText().toString() + "*" + Summary.getText().toString() + "*" + passLoc;
        intentCard.putExtra("tTot",total);
        startActivity(intentCard);
    }

    public class ButtonListener implements OnClickListener {


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btnCash)
            {
                toEnd(view);
            }
        }

    }
}
