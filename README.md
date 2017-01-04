# MediaBrowserServiceCompat#onLoadItem() always sends an empty MediaItem on Android 7

https://code.google.com/p/android/issues/detail?id=221513

MediaBrowserServiceCompat#onLoadItem() returns a MediaItem with null media ID on Android 7. It is expected to return a MediaItem with non-null media ID and works as expected on Android 6.

```java
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
```

```java
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
```
