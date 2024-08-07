package com.example.CurrencyConverter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CurrencyAdapter extends BaseAdapter {

    private ExchangeRateDatabase database;

    public CurrencyAdapter(ExchangeRateDatabase database) {
        this.database = database;
    }

    @Override
    public int getCount() {
        return database.getCurrencies().length;
    }

    @Override
    public Object getItem(int i) {
        return database.getCurrencies()[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_currency_list, null, false);
        }

        String currencyName = database.getCurrencies()[i];

        ImageView currencyFlagImageView = view.findViewById(R.id.currencyFlagImageView);
        int imageId = context.getResources().getIdentifier("flag_" + currencyName.toLowerCase(),
                "drawable", context.getPackageName());
        currencyFlagImageView.setImageResource(imageId);

        TextView currencyNameTextView = view.findViewById(R.id.currencyNameTextView);
        currencyNameTextView.setText(currencyName);

        TextView exchangeRateTextView = view.findViewById(R.id.exchangeRateTextView);
        double exchangeRate = database.getExchangeRate(currencyName);
        String exchangeRateText = Double.toString(exchangeRate);
        exchangeRateTextView.setText(exchangeRateText);

        return view;
    }

    /**
     * Getter method for {@link #database}
     * @return {@link #database}
     */
    public ExchangeRateDatabase getDatabase() {
        return database;
    }
}
