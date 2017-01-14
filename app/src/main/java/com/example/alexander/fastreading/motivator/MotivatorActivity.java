package com.example.alexander.fastreading.motivator;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class MotivatorActivity extends AppCompatActivity {

    private AdView adView;
    private boolean isPremiumUser;

    private static Random random = new Random();

    private static final String ARGUMENT_PAGE_NUMBER = "drawable_id";
    private static final int[] IMAGES =
            {
                    R.drawable.mot1, R.drawable.mot2, R.drawable.mot3,
                    R.drawable.mot4, R.drawable.mot5, R.drawable.mot6,
                    R.drawable.mot7, R.drawable.mot8, R.drawable.mot9,
                    R.drawable.mot10, R.drawable.mot11, R.drawable.mot12,
                    R.drawable.mot13, R.drawable.mot14, R.drawable.mot15,
                    R.drawable.mot16, R.drawable.mot17, R.drawable.mot18,
                    R.drawable.mot19,
                    R.drawable.mot20, R.drawable.mot21, R.drawable.mot22,
                    R.drawable.mot23, R.drawable.mot24, R.drawable.mot25,
                    R.drawable.mot26, R.drawable.mot27, R.drawable.mot28,
                    R.drawable.mot29, R.drawable.mot30,

                    R.drawable.mot31, R.drawable.mot32, R.drawable.mot33,
                    R.drawable.mot34, R.drawable.mot35, R.drawable.mot36,
                    R.drawable.mot37, R.drawable.mot38, R.drawable.mot39,
                    R.drawable.mot40, R.drawable.mot41, R.drawable.mot42,
                    R.drawable.mot43, R.drawable.mot44, R.drawable.mot45,
                    R.drawable.mot46, R.drawable.mot47, R.drawable.mot48,
                    R.drawable.mot49, R.drawable.mot50, R.drawable.mot51,
                    R.drawable.mot52, R.drawable.mot53, R.drawable.mot54,
                    R.drawable.mot55, R.drawable.mot56, R.drawable.mot57,
                    R.drawable.mot58, R.drawable.mot59, R.drawable.mot60,
                    R.drawable.mot61, R.drawable.mot62, R.drawable.mot63
            };

    private static final int NUM_PAGES = 100;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motivator_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.motivators);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        adView = (AdView) findViewById(R.id.adView);

        isPremiumUser = SettingsManager.isPremiumUser();
        if (isPremiumUser) {
            adView.setVisibility(View.GONE);
        } else {
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    adView.setVisibility(View.GONE);
                }
            });

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        parentView = findViewById(android.R.id.content);
        viewPager = (ViewPager) findViewById(R.id.motivator_activity_view_pager);

        PagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                MotivatorsPageFragment pageFragment = new MotivatorsPageFragment();

                Bundle arguments = new Bundle();
                arguments.putInt(ARGUMENT_PAGE_NUMBER, IMAGES[random.nextInt(IMAGES.length)]);
                pageFragment.setArguments(arguments);

                return pageFragment;
            }

            @Override
            public int getCount() {
                return NUM_PAGES;
            }
        };

        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);

        viewPager.setAdapter(wrappedAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), IMAGES[viewPager.getCurrentItem()]);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/png");

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "title");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


                    OutputStream outputStream = null;
                    try {
                        outputStream = getContentResolver().openOutputStream(uri);
                        icon.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    } catch (Exception e) {
                        Log.d("Exception", e.getMessage());
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share Image"));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Bitmap icon = BitmapFactory.decodeResource(getResources(), IMAGES[viewPager.getCurrentItem()]);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "title");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


                OutputStream outputStream = null;
                try {
                    outputStream = getContentResolver().openOutputStream(uri);
                    icon.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image"));

            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Snackbar.make(parentView, R.string.permission_error, Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(parentView, R.string.permission_error, Snackbar.LENGTH_LONG).setAction(R.string.settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                            startActivity(appSettingsIntent);
                        }
                    }).show();

                    //Snackbar.make(parentView, R.string.permission_error, Snackbar.LENGTH_LONG).show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.motivators_toolbar, menu);
        return true;
    }
/*
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MotivatorsPageFragment pageFragment = new MotivatorsPageFragment();

            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PAGE_NUMBER, IMAGES[random.nextInt(NUM_PAGES)]);
            pageFragment.setArguments(arguments);

            return pageFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
*/
    public class MinFragmentPagerAdapter extends FragmentPagerAdapter {

        private FragmentPagerAdapter adapter;

        public MinFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setAdapter(FragmentPagerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getCount() {
            int realCount = adapter.getCount();
            if (realCount == 1) {
                return 4;
            } else if (realCount == 2 || realCount == 3) {
                return realCount * 2;
            } else {
                return realCount;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return adapter.isViewFromObject(view, object);
        }

        /**
         * Warning: If you only have 1-3 real pages, this method will create multiple, duplicate
         * instances of your Fragments to ensure wrap-around is possible. This may be a problem if you
         * have editable fields or transient state (they will not be in sync).
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            int realCount = adapter.getCount();
            if (realCount == 1) {
                return adapter.getItem(0);
            } else if (realCount == 2 || realCount == 3) {
                return adapter.getItem(position % realCount);
            } else {
                return adapter.getItem(position);
            }
        }



    }

    public class InfinitePagerAdapter extends PagerAdapter {

        private static final String TAG = "InfinitePagerAdapter";
        private static final boolean DEBUG = false;

        private PagerAdapter adapter;

        public InfinitePagerAdapter(PagerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getCount() {
            if (getRealCount() == 0) {
                return 0;
            }
            // warning: scrolling to very high values (1,000,000+) results in
            // strange drawing behaviour
            return Integer.MAX_VALUE;
        }

        /**
         * @return the {@link #getCount()} result of the wrapped adapter
         */
        public int getRealCount() {
            return adapter.getCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int virtualPosition = position % getRealCount();
            debug("instantiateItem: real position: " + position);
            debug("instantiateItem: virtual position: " + virtualPosition);

            // only expose virtual position to the inner adapter
            return adapter.instantiateItem(container, virtualPosition);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int virtualPosition = position % getRealCount();
            debug("destroyItem: real position: " + position);
            debug("destroyItem: virtual position: " + virtualPosition);

            // only expose virtual position to the inner adapter
            adapter.destroyItem(container, virtualPosition, object);
        }

    /*
     * Delegate rest of methods directly to the inner adapter.
     */

        @Override
        public void finishUpdate(ViewGroup container) {
            adapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return adapter.isViewFromObject(view, object);
        }

        @Override
        public void restoreState(Parcelable bundle, ClassLoader classLoader) {
            adapter.restoreState(bundle, classLoader);
        }

        @Override
        public Parcelable saveState() {
            return adapter.saveState();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            adapter.startUpdate(container);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int virtualPosition = position % getRealCount();
            return adapter.getPageTitle(virtualPosition);
        }

        @Override
        public float getPageWidth(int position) {
            return adapter.getPageWidth(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            adapter.setPrimaryItem(container, position, object);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            adapter.unregisterDataSetObserver(observer);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            adapter.registerDataSetObserver(observer);
        }

        @Override
        public void notifyDataSetChanged() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return adapter.getItemPosition(object);
        }

    /*
     * End delegation
     */

        private void debug(String message) {
            if (DEBUG) {
                Log.d(TAG, message);
            }
        }
    }

}
