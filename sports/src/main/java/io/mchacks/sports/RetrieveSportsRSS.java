package io.mchacks.sports;

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
public class RetrieveSportsRSS extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //retrieveRSS("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml", "news");
        retrieveRSS("http://api.foxsports.com/v1/rss?partnerKey=zBaFxRyGKCfxBagJG9b8pqLyndmvo7UU", "sports");



        return START_NOT_STICKY;


    }

    private void retrieveRSS(String rss, String category) {

        String[] params = {rss, category};
        new JsonAsyncTask(this).execute(params);

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, i, 0);

        Notification n = new Notification.Builder(this)
                .setContentTitle("Processing Input")
                .setContentText("Loading...")
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(500, n);
    }

    /**
     * Each news item from the feed
     */
    public class NewsItem {
        private String title;
        private String content;

        public NewsItem(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public String getTitle() {
            return title;
        }
    }

    public class JsonAsyncTask extends AsyncTask<String, String, NewsItem[]> {

        RetrieveSportsRSS service;
        String category;

        public JsonAsyncTask(RetrieveSportsRSS service) {
            this.service = service;
        }

        @Override
        protected void onPreExecute() {
            Log.d("RSS LOAD", "Loading!");

        }
        @Override
        protected NewsItem[] doInBackground(String... params) {
            category = params[1];
            URL url = null;
            RssFeed feed = null;
            try {
                url = new URL(params[0]);
                feed = RssReader.read(url);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<RssItem> rssItems = feed.getRssItems();

            // Show no more than 5 headlines
            int size = rssItems.size() > 5 ? 5 : rssItems.size();

            NewsItem[] list = new NewsItem[size];
            for(int i = 0; i < size; i++) {
                NewsItem news = new NewsItem(rssItems.get(i).getTitle(), rssItems.get(i).getDescription());
                list[i] = news;
            }
            return list;
        }

        @Override
        protected void onPostExecute(NewsItem[] result) {
            Intent sendIntent = new Intent();
            sendIntent.setAction("com.hack.morningpal.FOUND_DATA");

            sendIntent.putExtra("title", result[0].getTitle());
            sendIntent.putExtra("sender_package", getPackageName());
            sendIntent.putExtra("description", result[0].getContent());
            sendIntent.putExtra("icon_name", this.category);
            sendIntent.putExtra("background_name", "http://corrupteddevelopment.com/wp-content/uploads/2013/08/blue-globe-icon.jpg");
            sendIntent.putExtra("spoken_phrase",
                    this.category == "news" ?
                    "Good morning! Here are this morning's headlines. " + result[0].getTitle()
                    : "Good morning! Here are the latest sports updates. " + result[0].getTitle()
            );

            // new array removing the first item
            String[] names = new String[result.length - 1];
            String[] descriptions = new String[result.length - 1];
            for (int i = 1; i < result.length; i++) {
                names[i - 1] = result[i].getTitle();
                descriptions[i - 1] = result[i].getContent();
            }

            sendIntent.putExtra("more_title", names);
            sendIntent.putExtra("more_description", descriptions);

            sendBroadcast(sendIntent);

            service.stopSelf();
        }
    }
}
