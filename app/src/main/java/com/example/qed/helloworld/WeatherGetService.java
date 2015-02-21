package com.example.qed.helloworld;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class WeatherGetService extends Service implements YahooWeatherInfoListener {
    public static final String TAG = "WeatherGetService";

    public WeatherGetService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        YahooWeather.getInstance().queryYahooWeatherByGPS(this, this);

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, i, 0);

        Notification n = new Notification.Builder(this)
                .setContentTitle("Processing Input")
                .setContentText("Loading...")
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(500, n);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        if(weatherInfo != null) {
            // Add your code here
            // weatherInfo object contains all information returned by Yahoo Weather apis
            //Yahoo! Weather - Montreal, CA
            Log.d(TAG, "City info: " + weatherInfo.getLocationCity());
            Log.d(TAG, "Country info: "+ weatherInfo.getLocationCountry());

            //Conditions for Montreal, CA at 1:59 pm EST, windspeed
            Log.d(TAG, "Conditions: " + weatherInfo.getConditionTitle());
            Log.d(TAG, "Wind Speed: " + weatherInfo.getWindSpeed());
            //THE DAY
            Log.d(TAG, "Day: " + weatherInfo.getForecastInfo1().getForecastDay());


            //CURRENT TEMPERATURE
            Log.d(TAG, "Current Temperature: " + weatherInfo.getCurrentTempC());

            int forecastCode= weatherInfo.getForecastInfo1().getForecastCode();
            String drawable = "rain";

            switch(forecastCode)
            {
                case 0:
                    drawable="tornado";
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 45:
                    drawable="tstorm";
                    break;

                case 5:
                case 6:
                case 7:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 41:
                case 42:
                case 43:
                case 46:
                    drawable="snow";
                    break;

                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 35:
                    drawable="rain";

                    break;
                case 17:
                    drawable="hail";
                    break;
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    drawable="fog";
                    break;
                case 24:
                    drawable="wind";
                    break;
                case 25:
                    drawable="cold";
                    break;
                case 26:
                case 44:
                    drawable="cloud";
                    break;
                case 27:
                case 29:
                    drawable="cloudnight";
                    break;
                case 28:
                case 30:
                    drawable="cloudday";
                    break;
                case 31:
                case 33:
                    drawable="night";
                    break;
                case 32:
                case 34:
                    drawable="sun";
                    break;
                case 36:
                    drawable="hot";
                    break;

                case 37:
                case 38:
                case 39:
                case 47:
                    drawable="scatteredthunder";
                    break;
                case 40:
                    drawable="scatteredrain";
                    break;

                default: drawable="error";
                    break;
            }



//WEATHER STATUS TITLE PLUS THE CODE
            Log.d(TAG, "Weather Status: " + weatherInfo.getForecastInfo1().getForecastText());

            Log.d(TAG, "Weather Code: " + weatherInfo.getForecastInfo1().getForecastCode());

//HIGH LOW TEMP IN C
            Log.d(TAG, "High Temp (C): " + weatherInfo.getForecastInfo1().getForecastTempHighC());
            Log.d(TAG, "Low Temp (C): " + weatherInfo.getForecastInfo1().getForecastTempLowC());

//weather status,  degrees


            String title= weatherInfo.getConditionTitle();
            String description=", Weather Status: "+weatherInfo.getForecastInfo1().getForecastText()+", Temp (C): "+
                    weatherInfo.getCurrentTempC()+" High Temp: "+weatherInfo.getForecastInfo1().getForecastTempHighC()
                    +", Low Temp: "+weatherInfo.getForecastInfo1().getForecastTempLowC();
            String spoken="The expected weather is "+weatherInfo.getForecastInfo1().getForecastText()+
                    " and the current temperature is "+ weatherInfo.getCurrentTempC();

            Intent sendIntent = new Intent();
            sendIntent.setAction("com.hack.morningpal.FOUND_DATA");
            sendIntent.putExtra("title", title);
            sendIntent.putExtra("sender_package", getPackageName());
            sendIntent.putExtra("description",description );
            sendIntent.putExtra("icon_name", drawable);
            sendIntent.putExtra("spoken_phrase", spoken);
            sendBroadcast(sendIntent);
            this.stopSelf();
        }
    }
}
