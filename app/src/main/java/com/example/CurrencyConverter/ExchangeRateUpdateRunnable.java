package com.example.CurrencyConverter;




import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

    /**
     * A class that implements Runnable interface and updates the rates when the user requests
     */
    public class ExchangeRateUpdateRunnable implements Runnable {

        private MainActivity mainActivity;
        private RatesUpdateNotifier notifier;

        public ExchangeRateUpdateRunnable(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
            this.notifier = new RatesUpdateNotifier(mainActivity);
        }

        @Override
        public void run() {
            final String queryString = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

            try {
                URL url = new URL(queryString);

                URLConnection connection = url.openConnection();

                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(connection.getInputStream(), connection.getContentEncoding());

                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if ("Cube".equals(parser.getName()) && parser.getAttributeCount() == 2) {
                            String currency = parser.getAttributeValue(null, "currency");
                            String sRate = parser.getAttributeValue(null, "rate");

                            double rate = Double.parseDouble(sRate);
                            mainActivity.getDatabase().setExchangeRate(currency, rate);
                        }
                    }

                    eventType = parser.next();
                }
            } catch (MalformedURLException e) {
                Log.e("MalformedURLException", "MalformedURLException");
            } catch (IOException e) {
                Log.e("IOException", "IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("XmlPullParserException", "XmlPullParserException");
            }

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainActivity.getApplicationContext(),
                            "Rates updated successfully", Toast.LENGTH_LONG).show();
                    mainActivity.getAdapter().notifyDataSetChanged();
                    notifier.showOrUpdateNotification();
                }
            });
        }
    }


