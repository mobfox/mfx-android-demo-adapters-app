package com.mobfox.mfx4demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// added for MobfoxSDK
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.mobfox.adapter.MobFoxAdapter;
import com.mobfox.android.MobfoxSDK;
import com.mobfox.android.MobfoxSDK.*;

// added for MoPub
import com.mobfox.android.core.gdpr.GDPRParams;
import com.mobfox.sdk.adapters.MoPubUtils;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;


public class MainActivity extends AppCompatActivity {

    private final Activity self = this;

    private LinearLayout relBanner;

    //creating variables for our layout
    private LinearLayout    linNative;
    private ImageView iconNative, mainNative;
    private TextView titleNative, descNative, ratingNative, sponsoredNative;
    private Button ctaNative;

    private Button          btnBannerSmall;
    private Button          btnBannerLarge;
    private Button          btnBannerVideo;

    private Button          btnInterstitialHtml;
    private Button          btnInterstitialVideo;

    private Button          btnNative;

    //===========================================================================================

    private static final int ADAPTER_TYPE_MOBFOX = 0;
    private static final int ADAPTER_TYPE_MOPUB  = 1;
    private static final int ADAPTER_TYPE_ADMOB  = 2;
    private int             mAdapterType = ADAPTER_TYPE_MOBFOX;

    //###########################################################################################
    //###########################################################################################
    //#####                                                                                 #####
    //#####   A p p   U I   s t u f f                                                       #####
    //#####                                                                                 #####
    //###########################################################################################
    //###########################################################################################

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        relBanner = (LinearLayout)findViewById(R.id.banner);

        // MobFox SDK init:
        initMobFoxSDK();

        // MoPub SDK init:
        initMoPubSDK();

        // AdMob SDK init:
        initAdMobSDK();

        initBannerButtons();

        initInterstitial();

        initNative();

        initTabs();


        //startMobFoxLargeBanner();
        //startMobFoxHtmlInterstitial();
        //startMobFoxNative();
    }

    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                // MoPub SDK initialized
                Log.d("mopub", "init");
            }
        };
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        MobfoxSDK.onPause(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobfoxSDK.onResume(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        clearAllAds();

        MobfoxSDK.onDestroy(this);
    }

    //===========================================================================================

    private void initTabs()
    {
        RelativeLayout rel;

        rel = (RelativeLayout)findViewById(R.id.btnTabMobFox);
        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapterType!=ADAPTER_TYPE_MOBFOX)
                {
                    mAdapterType = ADAPTER_TYPE_MOBFOX;
                    UpdateAdapters();
                }
            }
        });

        rel = (RelativeLayout)findViewById(R.id.btnTabMoPub);
        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapterType!=ADAPTER_TYPE_MOPUB)
                {
                    mAdapterType = ADAPTER_TYPE_MOPUB;
                    UpdateAdapters();
                }
            }
        });

        rel = (RelativeLayout)findViewById(R.id.btnTabAdMob);
        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapterType!=ADAPTER_TYPE_ADMOB)
                {
                    mAdapterType = ADAPTER_TYPE_ADMOB;
                    UpdateAdapters();
                }
            }
        });

        UpdateAdapters();
    }

    private void clearAllAds()
    {
        clearMobFoxBanners();
        clearMobFoxInterstitials();
        clearMobFoxNatives();

        clearMoPubBanner();
        clearMoPubInterstitial();
        clearMoPubNative();

        clearAdMobBanner();
        clearAdMobInterstitial();
        clearAdMobNative();
    }

    private void UpdateAdapters()
    {
        ImageView iv;

        clearAllAds();

        ((ImageView)findViewById(R.id.imgMobFox)).setBackgroundResource((mAdapterType==ADAPTER_TYPE_MOBFOX)?R.drawable.mobfox_logo:R.drawable.mobfox_logo_grey);
        ((ImageView)findViewById(R.id.imgMoPub )).setBackgroundResource((mAdapterType==ADAPTER_TYPE_MOPUB )?R.drawable.mopub_logo :R.drawable.mopub_logo_grey);
        ((ImageView)findViewById(R.id.imgAdMob )).setBackgroundResource((mAdapterType==ADAPTER_TYPE_ADMOB )?R.drawable.admob_logo :R.drawable.admob_logo_grey);

        switch (mAdapterType)
        {
            case ADAPTER_TYPE_MOBFOX:
                btnBannerSmall.setEnabled      (true);
                btnBannerLarge.setEnabled      (true);
                btnBannerVideo.setEnabled      (true);
                btnInterstitialHtml.setEnabled (true);
                btnInterstitialVideo.setEnabled(true);
                btnNative.setEnabled           (true);
                break;
            case ADAPTER_TYPE_MOPUB:
                btnBannerSmall.setEnabled      (true);
                btnBannerLarge.setEnabled      (false);
                btnBannerVideo.setEnabled      (false);
                btnInterstitialHtml.setEnabled (true);
                btnInterstitialVideo.setEnabled(true);
                btnNative.setEnabled           (true);
                break;
            case ADAPTER_TYPE_ADMOB:
                btnBannerSmall.setEnabled      (true);
                btnBannerLarge.setEnabled      (true);
                btnBannerVideo.setEnabled      (false);
                btnInterstitialHtml.setEnabled (true);
                btnInterstitialVideo.setEnabled(true);
                btnNative.setEnabled           (false);
                break;
        }
    }

    //===========================================================================================

    private void initBannerButtons()
    {
        btnBannerSmall = (Button)findViewById(R.id.btnBannerSmall);
        btnBannerSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Loading banner...",Toast.LENGTH_SHORT).show();

                switch (mAdapterType)
                {
                    case ADAPTER_TYPE_MOBFOX:
                        startMobFoxSmallBanner();
                        break;
                    case ADAPTER_TYPE_MOPUB:
                        startMoPubSmallBanner();
                        break;
                    case ADAPTER_TYPE_ADMOB:
                        startAdMobSmallBanner();
                        break;
                }
            }
        });

        btnBannerLarge = (Button)findViewById(R.id.btnBannerLarge);
        btnBannerLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Loading banner...",Toast.LENGTH_SHORT).show();

                switch (mAdapterType)
                {
                    case ADAPTER_TYPE_MOBFOX:
                        startMobFoxLargeBanner();
                        break;
                    case ADAPTER_TYPE_MOPUB:
                        // NOP
                        break;
                    case ADAPTER_TYPE_ADMOB:
                        startAdMobLargeBanner();
                        break;
                }
            }
        });

        btnBannerVideo = (Button)findViewById(R.id.btnBannerVideo);
        btnBannerVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Loading banner...",Toast.LENGTH_SHORT).show();

                switch (mAdapterType)
                {
                    case ADAPTER_TYPE_MOBFOX:
                        startMobFoxVideoBanner();
                        break;
                    case ADAPTER_TYPE_MOPUB:
                        // NOP
                        break;
                    case ADAPTER_TYPE_ADMOB:
                        // NOP
                        break;
                }
            }
        });
    }

    //===========================================================================================

    private void initInterstitial()
    {
        btnInterstitialHtml = (Button)findViewById(R.id.btnInterstitialHtml);
        btnInterstitialHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Loading interstitial...",Toast.LENGTH_SHORT).show();

                switch (mAdapterType)
                {
                    case ADAPTER_TYPE_MOBFOX:
                        startMobFoxHtmlInterstitial();
                        break;
                    case ADAPTER_TYPE_MOPUB:
                        startMoPubInterstitial(mopubInterstitialInvh);
                        break;
                    case ADAPTER_TYPE_ADMOB:
                        startAdMobInterstitial(admobInterstitialInvh);
                        break;
                }
            }
        });

        btnInterstitialVideo = (Button)findViewById(R.id.btnInterstitialVideo);
        btnInterstitialVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Loading interstitial...",Toast.LENGTH_SHORT).show();

                switch (mAdapterType)
                {
                    case ADAPTER_TYPE_MOBFOX:
                        startMobFoxVideoInterstitial();
                        break;
                    case ADAPTER_TYPE_MOPUB:
                        startMoPubInterstitial(mopubInterVideoInvh);
                        break;
                    case ADAPTER_TYPE_ADMOB:
                        startAdMobInterstitial(admobInterVideoInvh);
                        break;
                }
            }
        });
    }

    //===========================================================================================

    private void initNative()
    {
        linNative         = findViewById(R.id.linNative);
        iconNative        = findViewById(R.id.iconNative);
        mainNative        = findViewById(R.id.mainNative);

        titleNative       = findViewById(R.id.titleNative);
        descNative        = findViewById(R.id.descNative);
        ratingNative      = findViewById(R.id.ratingNative);
        sponsoredNative   = findViewById(R.id.sponsoredNative);

        ctaNative         = findViewById(R.id.ctaNative);

        titleNative.setVisibility(View.GONE);
        descNative.setVisibility(View.GONE);
        ratingNative.setVisibility(View.GONE);
        sponsoredNative.setVisibility(View.GONE);
        ctaNative.setVisibility(View.GONE);
        iconNative.setVisibility(View.GONE);
        mainNative.setVisibility(View.GONE);

        btnNative = (Button)findViewById(R.id.btnNative);
        btnNative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Loading native...",Toast.LENGTH_SHORT).show();

                switch (mAdapterType)
                {
                    case ADAPTER_TYPE_MOBFOX:
                        startMobFoxNative();
                        break;
                    case ADAPTER_TYPE_MOPUB:
                        startMoPubNative();
                        break;
                    case ADAPTER_TYPE_ADMOB:
                        startAdMobNative();
                        break;
                }
            }
        });
    }

    //###########################################################################################
    //###########################################################################################
    //#####                                                                                 #####
    //#####   M o b f o x                                                                   #####
    //#####                                                                                 #####
    //###########################################################################################
    //###########################################################################################

    public static final String MOBFOX_HASH_BANNER_HTML  = "fe96717d9875b9da4339ea5367eff1ec";
    public static final String MOBFOX_HASH_BANNER_VIDEO = "80187188f458cfde788d961b6882fd53";
    public static final String MOBFOX_HASH_INTER_HTML   = "267d72ac3f77a3f447b32cf7ebf20673";
    public static final String MOBFOX_HASH_INTER_VIDEO  = "80187188f458cfde788d961b6882fd53";
    public static final String MOBFOX_HASH_NATIVE       = "a764347547748896b84e0b8ccd90fd62";

    private MFXBanner       mMFXBannerAd       = null;
    private MFXInterstitial mMFXInterstitialAd = null;
    private MFXNative       mMFXNativeAd       = null;

    //===========================================================================================

    private void initMobFoxSDK()
    {
        MobfoxSDK.init(this);

        MobfoxSDK.setGDPR(true);
        MobfoxSDK.setGDPRConsentString(GDPRParams.GDPR_DEFAULT_MOBFOX_CONSENT_STRING);

        MobfoxSDK.setDemoAge("32");
        MobfoxSDK.setDemoGender("male");
        MobfoxSDK.setDemoKeywords("basketball,tennis");
        MobfoxSDK.setLatitude(32.455666);
        MobfoxSDK.setLongitude(32.455666);
    }

    //===========================================================================================

    private void startMobFoxSmallBanner()
    {
        clearAllAds();

        mMFXBannerAd = MobfoxSDK.createBanner(MainActivity.this,320,50, MOBFOX_HASH_BANNER_HTML,bannerListener);
        MobfoxSDK.setBannerFloorPrice(mMFXBannerAd,0.036f);
        MobfoxSDK.loadBanner(mMFXBannerAd);
    }

    private void startMobFoxLargeBanner()
    {
        clearAllAds();

        mMFXBannerAd = MobfoxSDK.createBanner(MainActivity.this,300,250, MOBFOX_HASH_BANNER_HTML,bannerListener);
        MobfoxSDK.loadBanner(mMFXBannerAd);
    }

    private void startMobFoxVideoBanner()
    {
        clearAllAds();

        mMFXBannerAd = MobfoxSDK.createBanner(MainActivity.this,300,250, MOBFOX_HASH_BANNER_VIDEO,bannerListener);
        MobfoxSDK.loadBanner(mMFXBannerAd);
    }

    private void clearMobFoxBanners()
    {
        if (mMFXBannerAd!=null)
        {
            MobfoxSDK.releaseBanner(mMFXBannerAd);
            mMFXBannerAd = null;
        }

        relBanner = (LinearLayout)findViewById(R.id.banner);
        relBanner.removeAllViews();
    }

    //-------------------------------------------------------------------------------------------

    private MFXBannerListener bannerListener = new MFXBannerListener() {

        @Override
        public void onBannerLoadFailed(MFXBanner banner, String code) {
            Toast.makeText(MainActivity.this,"onBannerLoadFailed: "+code,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerLoaded(MFXBanner banner) {
            Toast.makeText(MainActivity.this,"onBannerLoaded",Toast.LENGTH_SHORT).show();

            MobfoxSDK.addBannerViewTo(banner, relBanner);
        }

        @Override
        public void onBannerShown(MFXBanner banner) {
            Toast.makeText(MainActivity.this,"onBannerShown",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerClosed(MFXBanner banner) {
            Toast.makeText(MainActivity.this,"onBannerClosed",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerFinished(MFXBanner banner) {
            Toast.makeText(MainActivity.this,"onBannerFinished",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerClicked(MFXBanner banner, String url) {
            Toast.makeText(MainActivity.this,"onBannerClicked",Toast.LENGTH_SHORT).show();
        }
    };

    //===========================================================================================

    private void startMobFoxHtmlInterstitial()
    {
        clearAllAds();

        mMFXInterstitialAd = MobfoxSDK.createInterstitial(MainActivity.this,
                MOBFOX_HASH_INTER_HTML,
                interstitialListener);
        MobfoxSDK.loadInterstitial(mMFXInterstitialAd);
    }

    private void startMobFoxVideoInterstitial()
    {
        clearAllAds();

        mMFXInterstitialAd = MobfoxSDK.createInterstitial(MainActivity.this,
                MOBFOX_HASH_INTER_VIDEO,
                interstitialListener);
        MobfoxSDK.loadInterstitial(mMFXInterstitialAd);
    }

    private void clearMobFoxInterstitials()
    {
        if (mMFXInterstitialAd!=null)
        {
            MobfoxSDK.releaseInterstitial(mMFXInterstitialAd);
            mMFXInterstitialAd = null;
        }
    }

    //-------------------------------------------------------------------------------------------

    private MFXInterstitialListener interstitialListener = new MFXInterstitialListener() {
        @Override
        public void onInterstitialLoaded(MFXInterstitial interstitial) {
            Toast.makeText(MainActivity.this,"onInterstitialLoaded",Toast.LENGTH_SHORT).show();

            MobfoxSDK.showInterstitial(mMFXInterstitialAd);
        }

        @Override
        public void onInterstitialLoadFailed(MFXInterstitial interstitial, String code) {
            Toast.makeText(MainActivity.this,"onInterstitialLoadFailed: "+code,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInterstitialClosed(MFXInterstitial interstitial) {
            Toast.makeText(MainActivity.this,"onInterstitialClosed",Toast.LENGTH_SHORT).show();

            MobfoxSDK.releaseInterstitial(mMFXInterstitialAd);
        }

        @Override
        public void onInterstitialClicked(MFXInterstitial interstitial, String url) {
            Toast.makeText(MainActivity.this,"onInterstitialClicked",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInterstitialShown(MFXInterstitial interstitial) {
            Toast.makeText(MainActivity.this,"onInterstitialShown",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInterstitialFinished(MFXInterstitial interstitial) {
            Toast.makeText(MainActivity.this,"onInterstitialFinished",Toast.LENGTH_SHORT).show();
        }
    };

    //===========================================================================================

    private void startMobFoxNative()
    {
        clearAllAds();

        mMFXNativeAd = MobfoxSDK.createNative(MainActivity.this,
                MOBFOX_HASH_NATIVE,
                nativeListener);
        MobfoxSDK.loadNative(mMFXNativeAd);
    }

    private void clearMobFoxNatives()
    {
        if (mMFXNativeAd!=null)
        {
            updateNativeText(titleNative    , null);
            updateNativeText(descNative     , null);
            updateNativeText(ratingNative   , null);
            updateNativeText(sponsoredNative, null);
            updateNativeText(ctaNative      , null);

            updateNativeImage(iconNative, null);
            updateNativeImage(mainNative, null);

            MobfoxSDK.releaseNative(mMFXNativeAd);
            mMFXNativeAd = null;
        }
    }

    //-------------------------------------------------------------------------------------------

    private void updateNativeText(TextView tv, String value)
    {
        if ((value==null) || (value.length()==0))
        {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(value);
        }
    }

    private void updateNativeImage(ImageView iv, Bitmap value)
    {
        if (value==null)
        {
            iv.setVisibility(View.GONE);
        } else {
            iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap(value);
        }
    }

    private MFXNativeListener nativeListener = new MFXNativeListener() {
        @Override
        public void onNativeLoaded(MFXNative aNative) {
            Toast.makeText(self, "on native loaded", Toast.LENGTH_SHORT).show();

            Map<String, String> textItems = MobfoxSDK.getNativeTexts(mMFXNativeAd);

            updateNativeText(titleNative    , textItems.get("title"));
            updateNativeText(descNative     , textItems.get("desc"));
            updateNativeText(ratingNative   , textItems.get("rating"));
            updateNativeText(sponsoredNative, textItems.get("sponsored"));
            updateNativeText(ctaNative      , textItems.get("ctatext"));

            MobfoxSDK.loadNativeImages(mMFXNativeAd);

            MobfoxSDK.registerNativeForInteraction(self, mMFXNativeAd, linNative);

            ctaNative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobfoxSDK.callToActionClicked(mMFXNativeAd);
                }
            });
        }

        @Override
        public void onNativeImagesReady(MFXNative aNative) {
            Toast.makeText(self, "on images ready", Toast.LENGTH_SHORT).show();

            Map<String, Bitmap> imageItems = MobfoxSDK.getNativeImageBitmaps(mMFXNativeAd);

            updateNativeImage(iconNative, imageItems.get("icon"));
            updateNativeImage(mainNative, imageItems.get("main"));
        }

        @Override
        public void onNativeLoadFailed(MFXNative aNative, String code) {
            Toast.makeText(self, "MFXNative error: "+code, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNativeClicked(MFXNative aNative) {
            Toast.makeText(self, "MFXNative clicked", Toast.LENGTH_SHORT).show();
        }
    };

    //###########################################################################################
    //###########################################################################################
    //#####                                                                                 #####
    //#####   M o P u b                                                                     #####
    //#####                                                                                 #####
    //###########################################################################################
    //###########################################################################################

    private static String mopubBannerInvh          = "4ad212b1d0104c5998b288e7a8e35967";
    //private static String mopubBannerInvh        = "c2ec34f19e82433787a120aab00f9732";
    private static String mopubBannerLargeInvh     = "bf453fccdfe74af0ab8f6a944d6ae97a";

    private static String mopubInterstitialInvh    = "3fd85a3e7a9d43ea993360a2536b7bbd";
    //private static String mopubInterstitialInvh  = "28c3e68fc45a4276beaae95d32d21fb8";

    private static String mopubInterVideoInvh      = "562f11d6b8f2499dbd0d1ebfe3c17968";
    //private static String mopubInterVideoInvh    = "e7f2729a9ff54e17aa39f515f9a85eaa";

    //private static String mopubNativeInvh        = "11a17b188668469fb0412708c3d16813";
    private static String mopubNativeInvh        = "b146b367940a4c6da94e8143fb4b66e4";
    //private static String mopubNativeInvh        = "13cb8dbf4203433e8004bd34ed86406a";

    private MoPubView         mMoPubBannerAd       = null;
    private MoPubInterstitial mMoPubInterstitialAd = null;
    private MoPubNative       mMoPubNativeAd       = null;
    private View              mMoPubNativeView     = null;

    //===========================================================================================

    private void startMoPubSmallBanner()
    {
        final Context c = this;

        mMoPubBannerAd = new MoPubView(this);
        relBanner.addView(mMoPubBannerAd);

        mMoPubBannerAd.setAdUnitId(mopubBannerInvh);
        mMoPubBannerAd.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {
                Toast.makeText(c, "MoPub Banner loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                Toast.makeText(c, "MoPub Banner failed", Toast.LENGTH_SHORT).show();
                banner.destroy();
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                Toast.makeText(c, "MoPub Banner clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
                Toast.makeText(c, "MoPub Banner expanded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
                Toast.makeText(c, "MoPub Banner collapsed", Toast.LENGTH_SHORT).show();
                banner.destroy();
            }
        });

        Location locCurr = new Location(LocationManager.GPS_PROVIDER);
        locCurr.setLatitude (32.000000);
        locCurr.setLongitude(35.000000);
        mMoPubBannerAd.setLocation(locCurr);

        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put("demo_age"   , "23");
        localExtras.put("demo_gender", "female");
        localExtras.put("r_floor"    , "0.03");
        localExtras.put("keywords"   , "soccer,baseball");

        mMoPubBannerAd.setLocalExtras(localExtras);

        if (MoPub.isSdkInitialized()) {
            mMoPubBannerAd.loadAd();
        }
    }

    //===========================================================================================

    private void startMoPubInterstitial(String hashCode)
    {
        final Context c = this;

        mMoPubInterstitialAd = new MoPubInterstitial(self,hashCode);
        mMoPubInterstitialAd.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                Toast.makeText(c, "loaded", Toast.LENGTH_SHORT).show();
                if (interstitial.isReady()) {
                    mMoPubInterstitialAd.show();
                } else {
                    Toast.makeText(c, "Interstitial is not ready yet !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                Toast.makeText(c, "Interstitial load failed: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
                Toast.makeText(c, "shown", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
                Toast.makeText(c, "clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                Toast.makeText(c, "dismissed", Toast.LENGTH_SHORT).show();
                interstitial.destroy();
            }
        });
        mMoPubInterstitialAd.load();
    }

    private void startMoPubNative()
    {
        clearMoPubNative();

        ViewBinder mMoPubViewBinder = new ViewBinder.Builder(R.layout.mopub_native_layout)
                .mainImageId(R.id.mainNative)
                .iconImageId(R.id.iconNative)
                .titleId(R.id.titleNative)
                .textId(R.id.descNative)
                .callToActionId(R.id.ctaNative)
                .addExtra("sponsored", R.id.sponsoredNative)
                .addExtra("rating", R.id.ratingNative)
                .build();

        mMoPubNativeAd = new MoPubNative(this, mopubNativeInvh, new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAd nativeAd) {
                Toast.makeText(self, "MoPub native loaded", Toast.LENGTH_SHORT).show();

                nativeAd.prepare(findViewById(R.id.linNative));

                // Set the native event listeners (onImpression, and onClick).
                nativeAd.setMoPubNativeEventListener(new NativeAd.MoPubNativeEventListener() {
                    @Override
                    public void onImpression(View view) {
                        Toast.makeText(self, "MoPub native recorded an impression", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClick(View view) {
                        Toast.makeText(self, "MoPub native recorded a click", Toast.LENGTH_SHORT).show();
                    }
                });

                // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                AdapterHelper adapterHelper = new AdapterHelper(MainActivity.this, 0, 3); // When standalone, any range will be fine.
                mMoPubNativeView = adapterHelper.getAdView(null, null, nativeAd, new ViewBinder.Builder(0).build());

                // Add the ad view to our view hierarchy
                linNative.addView(mMoPubNativeView);
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Toast.makeText(self, "MoPub native error: "+errorCode, Toast.LENGTH_SHORT).show();
            }
        });

        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(mMoPubViewBinder);
        mMoPubNativeAd.registerAdRenderer(moPubStaticNativeAdRenderer);

        mMoPubNativeAd.makeRequest();
    }

    //===========================================================================================

    private void clearMoPubBanner()
    {
        if (mMoPubBannerAd!=null)
        {
            relBanner.removeView(mMoPubBannerAd);
            mMoPubBannerAd.destroy();
            mMoPubBannerAd = null;
        }
    }

    private void clearMoPubInterstitial()
    {
        if (mMoPubInterstitialAd!=null)
        {
            mMoPubInterstitialAd.destroy();
            mMoPubInterstitialAd = null;
        }
    }

    private void clearMoPubNative()
    {
        if (mMoPubNativeAd!=null)
        {
            linNative.removeView(mMoPubNativeView);
            mMoPubNativeAd.destroy();
            mMoPubNativeAd = null;
        }
    }

    //===========================================================================================

    private void initMoPubSDK()
    {
        final Context c = this;

        if (!MoPub.isSdkInitialized())
        {
            SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(mopubBannerInvh).build();
            MoPub.initializeSdk(this, sdkConfiguration, new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    Log.d("MobfoxSDK", "MoPub SDK init");
                }
            });
        }
    }


    //###########################################################################################
    //###########################################################################################
    //#####                                                                                 #####
    //#####   A d M o b                                                                     #####
    //#####                                                                                 #####
    //###########################################################################################
    //###########################################################################################

    private static String   admobBannerInvh       = "ca-app-pub-8111915318550857/5234422920";   // sdk.mobfox.com.appcore
    //private static String   admobBannerInvh       = "ca-app-pub-6224828323195096/4350674761";
    //private static String   admobBannerInvh       = "ca-app-pub-6224828323195096/4723665370";
    //private static String   admobBannerInvh       = "ca-app-pub-6224828323195096/7573529306";   // com.lyrebirdstudio.colorizer.lite

    private static String   admobInterstitialInvh = "ca-app-pub-8111915318550857/9385420926";   // sdk.mobfox.com.appcore
    //private static String   admobInterstitialInvh = "ca-app-pub-6224828323195096/1031427961";
    //private static String   admobInterstitialInvh = "ca-app-pub-6224828323195096/3716389562";
    //private static String   admobInterstitialInvh = "ca-app-pub-6224828323195096/5075358473";   // com.lyrebirdstudio.colorizer.lite

    private static String   admobInterVideoInvh   = "ca-app-pub-8111915318550857/7271416015";   // sdk.mobfox.com.appcore
    //private static String   admobInterVideoInvh   = "ca-app-pub-6224828323195096/5018083420";
    //private static String   admobInterVideoInvh   = "ca-app-pub-6224828323195096/6293496404";
    //private static String   admobInterVideoInvh   = "ca-app-pub-6224828323195096/3340427870";
    //private static String   admobInterVideoInvh   = "ca-app-pub-6224828323195096/6293496404";   // com.lyrebirdstudio.colorizer.lite

    private static String   admobNativeInvh       = "ca-app-pub-3940256099942544/2247696110";
    //private static String   admobNativeInvh       = "ca-app-pub-6224828323195096~6049137964";

    private AdView          mAdMobBannerView      = null;
    private InterstitialAd  mAdMobInterstitialAd  = null;
    private AdLoader        mAdMobNativeAdLoader  = null;

    //===========================================================================================

    private void startAdMobSmallBanner()
    {
        startAdMobBannerWithSize(AdSize.BANNER);
    }

    private void startAdMobLargeBanner()
    {
        startAdMobBannerWithSize(AdSize.MEDIUM_RECTANGLE);
    }

    private void startAdMobBannerWithSize(AdSize adSize)
    {
        final Context c = this;

        clearAdMobBanner();

        mAdMobBannerView = new AdView(this);
        mAdMobBannerView.setAdSize(adSize);
        mAdMobBannerView.setAdUnitId(admobBannerInvh);

        mAdMobBannerView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(c, "AdMob Banner loaded", Toast.LENGTH_SHORT).show();
                Log.d("MobfoxSDK", "AdMob Banner loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(c, "AdMob Banner failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Toast.makeText(c, "AdMob Banner opened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Toast.makeText(c, "AdMob Banner clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Toast.makeText(c, "AdMob Banner left app", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Toast.makeText(c, "AdMob Banner closed", Toast.LENGTH_SHORT).show();
            }
        });


        Location locCurr = new Location(LocationManager.GPS_PROVIDER);
        locCurr.setLatitude (32.009876);
        locCurr.setLongitude(35.006789);

        Bundle bundle = new Bundle();
        bundle.putBoolean("gdpr", true);
        bundle.putString("gdpr_consent", "YES");

        bundle.putString("demo_age","24");
        bundle.putString("demo_gender","male");
        bundle.putString("r_floor","0.04");


        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(MobFoxAdapter.class, bundle)
                .addKeyword("football,basketball")
                .setLocation(locCurr)
                .build();
        mAdMobBannerView.loadAd(adRequest);

        relBanner.addView(mAdMobBannerView);
    }

    private void startAdMobInterstitial(String invh)
    {
        final Context c = this;

        clearAdMobInterstitial();

        Bundle bundle = new Bundle();
        bundle.putBoolean("gdpr", true);
        bundle.putString("gdpr_consent", "YES");

        mAdMobInterstitialAd = new InterstitialAd(self);
        mAdMobInterstitialAd.setAdUnitId(invh);

        mAdMobInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Toast.makeText(c, "AdMob Interstitial onAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(c, "AdMob Interstitial onAdFailedToLoad: "+errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(c, "AdMob Interstitial onAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                Toast.makeText(c, "AdMob Interstitial onAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                Toast.makeText(c, "AdMob Interstitial onAdLoaded", Toast.LENGTH_SHORT).show();
                mAdMobInterstitialAd.show();
            }
        });

        AdRequest adRequestInterstitial = new AdRequest.Builder()
                //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .addNetworkExtrasBundle(MobFoxAdapter.class, bundle)
                //.tagForChildDirectedTreatment(true)
                .build();

        mAdMobInterstitialAd.loadAd(adRequestInterstitial);
    }

    private void startAdMobNative()
    {
        clearAdMobNative();

        final Context c = this;

        Bundle bundle = new Bundle();
        bundle.putBoolean("gdpr", true);
        bundle.putString("gdpr_consent", "YES");

        mAdMobNativeAdLoader = new AdLoader.Builder(this, admobNativeInvh)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.
                        if ((mAdMobNativeAdLoader!=null) && (mAdMobNativeAdLoader.isLoading())) {
                            // The AdLoader is still loading ads.
                            // Expect more adLoaded or onAdFailedToLoad callbacks.
                            Toast.makeText(c,"AdMob Native loading...",Toast.LENGTH_SHORT).show();
                        } else {
                            // The AdLoader has finished loading ads.
                            Toast.makeText(c,"AdMob Native DONE loading !",Toast.LENGTH_SHORT).show();

                            updateNativeText(titleNative    , unifiedNativeAd.getHeadline());//textItems.get("title"));
                            updateNativeText(descNative     , unifiedNativeAd.getBody());//textItems.get("desc"));
                            updateNativeText(ratingNative   , String.valueOf(unifiedNativeAd.getStarRating()));
                            updateNativeText(sponsoredNative, unifiedNativeAd.getAdvertiser());
                            updateNativeText(ctaNative      , unifiedNativeAd.getCallToAction());

                            com.google.android.gms.ads.formats.NativeAd.Image imgIcon = unifiedNativeAd.getIcon();
                            if (imgIcon!=null)
                            {
                                Drawable dIcon = imgIcon.getDrawable();
                                if (dIcon!=null)
                                {
                                    updateNativeImage(iconNative, drawableToBitmap(dIcon));
                                }
                            }
                            List<com.google.android.gms.ads.formats.NativeAd.Image> lstImages = unifiedNativeAd.getImages();
                            if ((lstImages!=null) && (lstImages.size()>0))
                            {
                                Drawable dMain = lstImages.get(0).getDrawable();
                                if (dMain!=null)
                                {
                                    updateNativeImage(mainNative, drawableToBitmap(dMain));
                                }
                            }
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Toast.makeText(c,"AdMob Native Failed: "+errorCode,Toast.LENGTH_SHORT).show();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        mAdMobNativeAdLoader.loadAd(new AdRequest.Builder()
                .addNetworkExtrasBundle(MobFoxAdapter.class, bundle)
                .build());
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //===========================================================================================

    private void clearAdMobBanner()
    {
        relBanner = (LinearLayout)findViewById(R.id.banner);
        relBanner.removeAllViews();
    }

    private void clearAdMobInterstitial()
    {
        // mytodo:
    }

    private void clearAdMobNative()
    {
        // mytodo:
    }

    //===========================================================================================

    private void initAdMobSDK()
    {
        //@@@MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");
    }
}
