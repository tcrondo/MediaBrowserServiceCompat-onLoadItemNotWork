package com.example.tcrondo.mediabrowserservicecompatonloaditemnotwork;

import android.content.ComponentName;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private MediaBrowserCompat mBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBrowser = new MediaBrowserCompat(
                this,
                new ComponentName(this, MainService.class),
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        loadChildren();
                        loadItem("item3");
                    }
                }, null);
        mBrowser.connect();
    }

    private void loadChildren() {
        mBrowser.subscribe("parent", new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
                for (MediaBrowserCompat.MediaItem child : children) {
                    Log.d(TAG, "child: " + child.getMediaId());
                }
            }
        });
    }

    private void loadItem(String itemId) {
        mBrowser.getItem(itemId, new MediaBrowserCompat.ItemCallback() {
            @Override
            public void onItemLoaded(MediaBrowserCompat.MediaItem item) {
                // The log function below prints:
                // `item: item3` on Android 6
                // `item: null` on Android 7
                Log.d(TAG, "item: " + item.getMediaId());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBrowser.disconnect();
    }
}
