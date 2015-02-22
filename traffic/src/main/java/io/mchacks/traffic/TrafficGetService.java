package io.mchacks.traffic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Brian on 2/21/2015.
 */
public class TrafficGetService extends Service {

    public static final String BING_MAPS_API_KEY = "AhM1ZnquDvJRwKQc7Pj3nh1RadJiMLJmBGI49Caam6XThVxHi6c13N0nqfu2tG0B";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTrafficQuery();

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

    private void startTrafficQuery() {

        double latitude = 50;
        double longitude = 50;

        String coordinates =
                (latitude - 5) + ","
                        + (longitude - 5) + ","
                        + (latitude + 5) + ","
                        + (longitude + 5);

//        coordinates = "37,-105,45,-94";

        if (isNetworkAvailable()) {
            Log.d("Network", "Network available");

            Log.d("Logger", "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/"
                    + coordinates
                    + "?severity=4&key="
                    + BING_MAPS_API_KEY);
            new JsonAsyncTask(this).execute("http://dev.virtualearth.net/REST/v1/Traffic/Incidents/"
                    + coordinates
                    + "?severity=4&key="
                    + BING_MAPS_API_KEY);
        } else {
            Log.e("Network", "Network not available");
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class JsonAsyncTask extends AsyncTask<String, String, String> {

        TrafficGetService service;

        public JsonAsyncTask(TrafficGetService service) {
            this.service = service;
        }

        @Override
        protected void onPreExecute() {
            Log.d("JSON LOAD", "Loading!");

        }
        @Override
        protected String doInBackground(String... urls) {

            Log.e("Logger",urls[0]);

            InputStream input = null;
            String jsonString = "";
            try {

                HttpResponse response = new DefaultHttpClient().execute(new HttpGet(urls[0]));
                input = response.getEntity().getContent();
                if (input != null) {

                    Log.d("Json Post Execute 22", "Executing");
                    // read the input into a string
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line = "";
                    while ((line = reader.readLine()) != null)
                        jsonString += line;


                } else {
                    Log.e("JSON" , "Blank result.");
                }

            } catch (IOException e) {
                Log.e("JSON", "IOException");
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.d("Json Post Execute", "Executing");
                JSONObject json = new JSONObject(result);

                JSONObject resourceSets = ((JSONObject)(json.getJSONArray("resourceSets").get(0)));

                int count = resourceSets.getInt("estimatedTotal");
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                if (count == 0) {
                    names.add("Today's your lucky day. No traffic incidents! :)");
                    descriptions.add("");
                } else {
                    JSONArray resources = resourceSets.getJSONArray("resources");

                    // build the traffic data listing
//                    StringBuilder traffic = new StringBuilder();
//                    traffic.append(count + " known traffic incidents in your area.\n\n");



                    for (int i = 0; i < resources.length(); i++) {
                        JSONObject incident = (JSONObject) resources.get(i);


                        String[] types = {"Accident", "Congestion", "Disabled Vehicle", "Mass Transit", "Miscellaneous", "Other News", "Planned Event", "Road Hazard", "Construction", "Alert", "Weather" };
                        String name = types[incident.getInt("type") - 1];
//                        traffic.append(name + " \n");
                        names.add(name);

                        String closed = incident.getBoolean("roadClosed") ? "Road Closed" : "No Road Closure";
//                        traffic.append(closed + " \n");

                        if (incident.has("description")) { // not a required item
                            String description = incident.getString("description");
//                            traffic.append("Description: ").append(description);
                            descriptions.add(description);
                        } else {
                            descriptions.add(closed);
                        }

//                        traffic.append("\n\n");
                    }



//                    newsView.setText(traffic.toString());
//                newsView.setText(json.toString());
                }
                Intent sendIntent = new Intent();

                String spoken;
                if (count == 0)
                    spoken = "Looks like a smooth journey ahead";
                else if (count < 10)
                    spoken = "Your trip doesn't look too bad";
                else if (count < 20)
                    spoken = "Expect some minor delays today";
                else if (count < 30)
                    spoken = "Expect significant delays today";
                else
                    spoken = "Good luck. Might want to leave now if you intend to arrive on time.";

                sendIntent.setAction("com.hack.morningpal.FOUND_DATA");

                sendIntent.putExtra("title", names.remove(0));
                sendIntent.putExtra("sender_package", getPackageName());
                sendIntent.putExtra("description",descriptions.remove(0));
                sendIntent.putExtra("icon_name", "traffic");
                sendIntent.putExtra("spoken_phrase", spoken);

                String[] namesList = new String[names.size()];
                names.toArray(namesList);
                String[] descriptionsList = new String[descriptions.size()];
                descriptions.toArray(descriptionsList);

                sendIntent.putExtra("more_title", namesList);
                sendIntent.putExtra("more_description", descriptionsList);

                sendBroadcast(sendIntent);
                service.stopSelf();

            } catch (JSONException e) {
                Log.e("JSON" , "JSONException");
                e.printStackTrace();
            }
        }
    }
}
