package io.mchacks.rss;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

/**
 * Created by Brian on 2/22/2015.
 */
public class RetrieveRSS extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        retrieveRSS("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");

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

    private void retrieveRSS(String rss) {

        new JsonAsyncTask(this).execute(rss);
    }

    public class JsonAsyncTask extends AsyncTask<String, String, String> {

        RetrieveRSS service;

        public JsonAsyncTask(RetrieveRSS service) {
            this.service = service;
        }

        @Override
        protected void onPreExecute() {
            Log.d("RSS LOAD", "Loading!");

        }
        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            RssFeed feed = null;
            try {
                url = new URL(urls[0]);
                feed = RssReader.read(url);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<RssItem> rssItems = feed.getRssItems();
            StringBuilder list = new StringBuilder();
            for(RssItem rssItem : rssItems) {
                Log.i("RSS Reader", rssItem.getTitle());
                list.append(rssItem.getTitle()).append("\n");
            }
            return list.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Intent sendIntent = new Intent();
            sendIntent.setAction("com.hack.morningpal.FOUND_DATA");

            sendIntent.putExtra("title", "Morning News");
            sendIntent.putExtra("sender_package", getPackageName());
            sendIntent.putExtra("description", result);
            sendIntent.putExtra("icon_name", "news");
            sendIntent.putExtra("spoken_phrase", "Good morning! Here are this morning's headlines. " + result);

            sendBroadcast(sendIntent);
        }
    }
}
