package com.example.tcrondo.mediabrowserservicecompatonloaditemnotwork;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.ArrayList;
import java.util.List;

public class MainService extends MediaBrowserServiceCompat {
    private MediaSessionCompat mSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mSession = new MediaSessionCompat(this, MainService.class.getSimpleName());
        setSessionToken(mSession.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new MediaBrowserServiceCompat.BrowserRoot("__ROOT__", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<MediaBrowserCompat.MediaItem> children = new ArrayList<>();
                children.add(new MediaBrowserCompat.MediaItem(
                        new MediaDescriptionCompat.Builder().setMediaId("item1").build(),
                        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                children.add(new MediaBrowserCompat.MediaItem(
                        new MediaDescriptionCompat.Builder().setMediaId("item2").build(),
                        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                result.sendResult(children);
            }
        }, 100);
    }

    @Override
    public void onLoadItem(final String itemId, final Result<MediaBrowserCompat.MediaItem> result) {
        result.detach();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MediaBrowserCompat.MediaItem item = new MediaBrowserCompat.MediaItem(
                        new MediaDescriptionCompat.Builder().setMediaId(itemId).build(),
                        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE | MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
                result.sendResult(item);
            }
        }, 100);
    }
}
