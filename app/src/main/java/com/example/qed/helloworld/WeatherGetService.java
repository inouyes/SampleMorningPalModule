package com.example.qed.helloworld;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
            Log.d(TAG, "Day2: " + weatherInfo.getForecastInfo2().getForecastDate());

            //CURRENT TEMPERATURE
            Log.d(TAG, "Current Temperature: " + weatherInfo.getCurrentTempC());

            int forecastCode = weatherInfo.getForecastInfo1().getForecastCode();
            String drawable = "rain";
            int c = Color.argb(255, 255, 255, 255);
            String background_image=null;
            switch(forecastCode)
            {
                case 0:
                    drawable="tornado";
                    c = Color.parseColor("#FF9E80");
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 45:
                    drawable="tstorm";
                    c = Color.parseColor("#9E9E9E");
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
                   // c = Color.parseColor("#B0BEC5");
                    background_image="http://upload.wikimedia.org/wikipedia/commons/0/0b/Shadows_on_snow.jpg";
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 35:
                    drawable="rain";
                    c = Color.parseColor("#4DB6AC");
                    break;
                case 17:
                    drawable="hail";
                    c = Color.parseColor("#B0BEC5");
                    break;
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    drawable="fog";
                    c = Color.parseColor("#9E9E9E");
                    break;
                case 24:
                    drawable="wind";
                    c = Color.parseColor("#F48FB1");
                    break;
                case 25:
                    drawable="cold";
                    c = Color.parseColor("#F48FB1");
                    break;
                case 26:
                case 44:
                    drawable="cloud";//updated
                   // c = Color.parseColor("#BDBDBD");
                    background_image="http://upload.wikimedia.org/wikipedia/commons/6/63/Cloud_over_mountain_in_Switzerland.JPG";
                    break;
                case 27:
                case 29:
                    drawable="cloudnight";//Replaced
                    //c = Color.parseColor("#757575");
                    background_image="http://upload.wikimedia.org/wikipedia/commons/6/63/Cloud_over_mountain_in_Switzerland.JPG";
                    break;
                case 28:
                case 30:
                    drawable="cloudday";//replaced
                    //c = Color.parseColor("#757575");
                    background_image="http://upload.wikimedia.org/wikipedia/commons/6/63/Cloud_over_mountain_in_Switzerland.JPG";
                    break;
                case 31:
                case 33:
                    drawable="night";
                    c = Color.parseColor("#616161");
                    break;
                case 32:
                case 34:
                    drawable="sun";
                    c = Color.parseColor("#7986CB");

                    break;
                case 36:
                    drawable="hot";
                    c = Color.parseColor("#7986CB");
                    break;

                case 37:
                case 38:
                case 39:
                case 47:
                    drawable = "scatteredthunder";
                    c = Color.parseColor("#EF9A9A");
                    break;
                case 40:
                    drawable = "scatteredrain";
                    c = Color.parseColor("#B388FF");
                    break;

                default:
                    drawable = "error";
                    c = Color.argb(255, 255, 255, 255);
                    break;
            }



//WEATHER STATUS TITLE PLUS THE CODE
            Log.d(TAG, "Weather Status: " + weatherInfo.getForecastInfo1().getForecastText());

            Log.d(TAG, "Weather Code: " + weatherInfo.getForecastInfo1().getForecastCode());

//HIGH LOW TEMP IN C
            Log.d(TAG, "High Temp (C): " + weatherInfo.getForecastInfo1().getForecastTempHighC());
            Log.d(TAG, "Low Temp (C): " + weatherInfo.getForecastInfo1().getForecastTempLowC());

//weather status,  degrees


            String title = weatherInfo.getLocationCity() + ", "+ weatherInfo.getCurrentTempC() + "C " +"\n" + weatherInfo.getForecastInfo1().getForecastText();
            String description = weatherInfo.getForecastInfo1().getForecastTempHighC() + "C high, "
                    + weatherInfo.getForecastInfo1().getForecastTempLowC() + "C low";



            String spoken = "Today's weather for " + weatherInfo.getLocationCity() + " is " +weatherInfo.getForecastInfo1().getForecastText() +
                    ". The temperature is " + weatherInfo.getCurrentTempC() + "degrees, with a high of " + weatherInfo.getForecastInfo1().getForecastTempHighC() + "degrees, and a "
                    + "low of " + weatherInfo.getForecastInfo1().getForecastTempLowC() + "degrees.";
            String more_title[] = new String[]{ "Wind Chill", "Wind Speed"};
            String more_description[] = new String[]{ weatherInfo.getWindChill() + "C", weatherInfo.getWindSpeed() + "km/h" };

            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Description: " + description);
            Log.d(TAG, "Spoken: " + spoken);
            Log.d(TAG, "More title: " + more_title);
            Log.d(TAG, "More description: " + more_description);
            Log.d(TAG, "Drawable: " + drawable);
            Log.d(TAG, "Background color: " + c);

            Intent sendIntent = new Intent();
            sendIntent.setAction("com.hack.morningpal.FOUND_DATA");
            sendIntent.putExtra("title", title);
            sendIntent.putExtra("sender_package", getPackageName());
            sendIntent.putExtra("description", description);
            sendIntent.putExtra("icon_name", drawable);
            sendIntent.putExtra("more_title", more_title);
            sendIntent.putExtra("more_description", more_description);
            sendIntent.putExtra("spoken_phrase", spoken);

            if(background_image==null) {
                sendIntent.putExtra("background_color", c);
            }
            sendIntent.putExtra("background_name", background_image);
            sendBroadcast(sendIntent);
            this.stopSelf();
        }
    }
}
