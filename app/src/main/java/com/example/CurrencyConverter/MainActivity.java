//Name: Sara Waldecker
//CTS4
//Matriculation Number: 3139841

package com.example.CurrencyConverter;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.CurrencyConverter.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int JOB_ID = 101;

    /**
     * The adapter, that is used to populate the spinners
     */
    private CurrencyAdapter adapter;

    /**
     * Share action provider for the share button
     */
    private ShareActionProvider shareActionProvider;

    private ExchangeRateDatabase database;

    /**
     * Initializes the {@link #adapter}
     * And sets it as the adapter for both of the spinners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //?????
        androidx.appcompat.widget.Toolbar mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        database = new ExchangeRateDatabase();

        adapter = new CurrencyAdapter(database);

        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = findViewById(R.id.spinnerTo);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerService();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("Daily update of currencies"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        EditText valueEditText = findViewById(R.id.valueEditText);
        String value = valueEditText.getText().toString();

        TextView resultTextView = findViewById(R.id.resultTextView);
        String result = resultTextView.getText().toString();

        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = findViewById(R.id.spinnerTo);

        int positionFrom = spinnerFrom.getSelectedItemPosition();
        int positionTo = spinnerTo.getSelectedItemPosition();

        for (String currency : database.getCurrencies()) {
            String currencyRate = Double.toString(database.getExchangeRate(currency));
            editor.putString(currency, currencyRate);
        }

        editor.putString("value", value);
        editor.putString("result", result);
        editor.putInt("positionFrom", positionFrom);
        editor.putInt("positionTo", positionTo);

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        String value = prefs.getString("value", "");
        String result = prefs.getString("result", "0.00");
        int positionFrom = prefs.getInt("positionFrom", 0);
        int positionTo = prefs.getInt("positionTo", 0);

        for (String currency : database.getCurrencies()) {
            String currencyRate = prefs.getString(currency, "0.00");

            if ("EUR".equals(currency)) {
                currencyRate = "1.00";
            }

            if (!("0.00".equals(currencyRate))) {
                database.setExchangeRate(currency, Double.parseDouble(currencyRate));
            }
        }

        EditText valueEditText = findViewById(R.id.valueEditText);

        TextView resultTextView = findViewById(R.id.resultTextView);

        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = findViewById(R.id.spinnerTo);

        valueEditText.setText(value);
        resultTextView.setText(result);
        spinnerFrom.setSelection(positionFrom);
        spinnerTo.setSelection(positionTo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.my_menu, menu);

        //MenuItem shareItem = menu.findItem(R.id.action_share);
        //shareActionProvider = (ShareActionProvider) menu.findItem(R.id.action_share);

        //setShareText(null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.currencyListMenuEntry:
                Intent intent = new Intent(MainActivity.this, CurrencyListActivity.class);
                MainActivity.this.startActivity(intent);
                return true;

            case R.id.refreshRatesMenuEntry:
                new Thread(new ExchangeRateUpdateRunnable(this)).start();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Calculates the conversion between two currencies.
     *
     * @param view - the view on which the click has happened
     */
    public void onCalculateClicked(View view) {
        TextView resultTextView = findViewById(R.id.resultTextView);
        EditText valueEditText = findViewById(R.id.valueEditText);

        if(valueEditText.getText().toString().length() == 0) {
            resultTextView.setText(String.format(Locale.getDefault(),"%.2f", 0.00));
            return;
        }

        double value = Double.parseDouble(valueEditText.getText().toString());

        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        String currencyFrom = (String) spinnerFrom.getSelectedItem();

        Spinner spinnerTo = findViewById(R.id.spinnerTo);
        String currencyTo = (String) spinnerTo.getSelectedItem();

        resultTextView.setText(String.format(Locale.getDefault(),
                "%.2f", adapter.getDatabase().convert(value, currencyFrom, currencyTo)));

        String text = "Currency Converter says: \n" +
                valueEditText.getText().toString() + " " + currencyFrom + " are " +
                resultTextView.getText().toString() + " " + currencyTo;

        setShareText(text);
    }

    /**
     * Sets the message for sharing the app with others
     * @param text message to set
     */
    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        if(text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }

        //shareActionProvider.setShareIntent(shareIntent);
    }

    /**
     * Registers the job service which will daily update currencies
     */
    public void registerService() {
        ComponentName serviceName = new ComponentName(this, RatesUpdateJobService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setPeriodic(86_400_000).build();

            JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

            if (scheduler.getPendingJob(JOB_ID) == null) {
                scheduler.schedule(jobInfo);
            }
        }

    }

    /**
     * Receives the message from the RatesUpdateJobService class once the updates have been finished
     */
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, "Rates updated successfully",
                    Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
        }
    };

    public ExchangeRateDatabase getDatabase() {
        return database;
    }

    public CurrencyAdapter getAdapter() {
        return adapter;
    }
}
