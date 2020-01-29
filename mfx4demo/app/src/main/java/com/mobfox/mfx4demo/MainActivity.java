package com.mobfox.mfx4demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
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
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mobfox.adapter.MobFoxAdapter;
import com.mobfox.android.MobfoxSDK;
import com.mobfox.android.MobfoxSDK.*;

// added for MoPub
import com.mobfox.android.core.MFXStorage;
import com.mobfox.sdk.adapters.MoPubUtils;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideo;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;


public class MainActivity extends AppCompatActivity {

    private final Activity self = this;

    private LinearLayout relBanner;

    //creating variables for our layout
    private LinearLayout    linNative;
    private ImageView iconNative, mainNative;
    private TextView titleNative, descNative, ratingNative, sponsoredNative;
    private Button ctaNative;

    private CheckBox        btnUseLiveAds;
    private CheckBox        btnPicsartMode;

    private Button          btnBannerSmall;
    private Button          btnBannerLarge;
    private Button          btnBannerVideo;

    private Button          btnInterstitialHtml;
    private Button          btnInterstitialVideo;

    private Button          btnRewarded;

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

        initConfigButtons();

        initBannerButtons();

        initInterstitialButtons();

        initRewardedButtons();

        initNativeButtons();

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

    private void ShowToast(String text)
    {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
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
        clearAdMobRewarded();
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
                btnRewarded.setEnabled         (false);
                btnNative.setEnabled           (true);
                break;
            case ADAPTER_TYPE_MOPUB:
                btnBannerSmall.setEnabled      (true);
                btnBannerLarge.setEnabled      (false);
                btnBannerVideo.setEnabled      (false);
                btnInterstitialHtml.setEnabled (true);
                btnInterstitialVideo.setEnabled(true);
                btnRewarded.setEnabled         (true);
                btnNative.setEnabled           (true);
                break;
            case ADAPTER_TYPE_ADMOB:
                btnBannerSmall.setEnabled      (true);
                btnBannerLarge.setEnabled      (true);
                btnBannerVideo.setEnabled      (false);
                btnInterstitialHtml.setEnabled (true);
                btnInterstitialVideo.setEnabled(true);
                btnRewarded.setEnabled         (true);
                btnNative.setEnabled           (true);
                break;
        }
    }

    //===========================================================================================

    private void initConfigButtons()
    {
        btnUseLiveAds = (CheckBox)findViewById(R.id.btnUseLiveAds);
        btnUseLiveAds.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MFXStorage.sharedInstance(MainActivity.this).setPrefBool("MFX4Demo_use_live_ads",isChecked);
                clearAllAds();
                UpdateConfigButtons();
            }
        });

        btnPicsartMode = (CheckBox)findViewById(R.id.btnPicsartMode);
        btnPicsartMode.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MFXStorage.sharedInstance(MainActivity.this).setPrefBool("MFX4Demo_do_like_picsart",isChecked);
                clearAllAds();
                UpdateConfigButtons();
            }
        });
        UpdateConfigButtons();
    }

    private void UpdateConfigButtons()
    {
        btnUseLiveAds.setChecked(MFXStorage.sharedInstance(this).getPrefBool("MFX4Demo_use_live_ads",false));
        btnPicsartMode.setChecked(MFXStorage.sharedInstance(this).getPrefBool("MFX4Demo_do_like_picsart",false));
    }

    //===========================================================================================

    private void initBannerButtons()
    {
        btnBannerSmall = (Button)findViewById(R.id.btnBannerSmall);
        btnBannerSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast("Loading banner...");

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
                ShowToast("Loading banner...");

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
                ShowToast("Loading banner...");

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

    private void initInterstitialButtons()
    {
        btnInterstitialHtml = (Button)findViewById(R.id.btnInterstitialHtml);
        btnInterstitialHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast("Loading interstitial...");

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
                ShowToast("Loading interstitial...");

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

    private void initRewardedButtons()
    {
        btnRewarded = (Button)findViewById(R.id.btnRewarded);
        btnRewarded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast("Loading rewarded...");

                switch (mAdapterType)
                {
                    case ADAPTER_TYPE_MOBFOX:
                        // REWARDED_ROLLBACK startMobFoxRewarded();
                        break;
                    case ADAPTER_TYPE_MOPUB:
                        startMoPubRewarded(mopubRewardedInvh);
                        break;
                    case ADAPTER_TYPE_ADMOB:
                        startAdMobRewarded(admobRewardedInvh);
                        break;
                }
            }
        });
    }

    //===========================================================================================

    private void initNativeButtons()
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
                ShowToast("Loading native...");

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
    public static final String MOBFOX_HASH_INTER_HTML   = "855f0e6de92f5187d32ed2b86be45d4a";//"267d72ac3f77a3f447b32cf7ebf20673";
    public static final String MOBFOX_HASH_INTER_VIDEO  = "80187188f458cfde788d961b6882fd53";
    public static final String MOBFOX_HASH_NATIVE       = "d8bd50e4ba71a708ad224464bdcdc237";//"a764347547748896b84e0b8ccd90fd62";

    public static final String MOBFOX_LIVE_HASH_BANNER_HTML  = "fc57543a03d1a12362211f2a5771dbe6";
    public static final String MOBFOX_LIVE_HASH_BANNER_VIDEO = "e40be240fb5ae46b55ae0688242b5ff4";
    public static final String MOBFOX_LIVE_HASH_INTER_HTML   = "35eba51287132447b5a18a764ea3e678";
    public static final String MOBFOX_LIVE_HASH_INTER_VIDEO  = "741e400adbb54e13d772067aae1107fd";
    public static final String MOBFOX_LIVE_HASH_NATIVE       = "d22bf35c596809155ec8520d283a9b09";

    public static final String MOBFOX_LIVE2_HASH_BANNER_HTML  = "9d38e9038e217f927beb6e560d96fd45";
    public static final String MOBFOX_LIVE2_HASH_BANNER_VIDEO = "ed5b95941adf399c9a8cf3f296cfc64c";
    public static final String MOBFOX_LIVE2_HASH_INTER_HTML   = "35eba51287132447b5a18a764ea3e678";
    public static final String MOBFOX_LIVE2_HASH_INTER_VIDEO  = "75f994b45ca31b454addc8b808d59135";
    public static final String MOBFOX_LIVE2_HASH_NATIVE       = "d22bf35c596809155ec8520d283a9b09";

    private MFXBanner       mMFXBannerAd       = null;
    private MFXInterstitial mMFXInterstitialAd = null;
    private MFXNative       mMFXNativeAd       = null;

    //===========================================================================================

    private String MobfoxHashBannerHtml()
    {
        if (!MFXStorage.sharedInstance(this).getPrefBool("MFX4Demo_use_live_ads",false))
        {
            return MOBFOX_HASH_BANNER_HTML;
        } else {
            return MOBFOX_LIVE_HASH_BANNER_HTML;
        }
    }

    private String MobfoxHashBannerVideo()
    {
        if (!MFXStorage.sharedInstance(this).getPrefBool("MFX4Demo_use_live_ads",false))
        {
            return MOBFOX_HASH_BANNER_VIDEO;
        } else {
            return MOBFOX_LIVE_HASH_BANNER_VIDEO;
        }
    }

    private String MobfoxHashInterHtml()
    {
        if (!MFXStorage.sharedInstance(this).getPrefBool("MFX4Demo_use_live_ads",false))
        {
            return MOBFOX_HASH_INTER_HTML;
        } else {
            return MOBFOX_LIVE_HASH_INTER_HTML;
        }
    }

    private String MobfoxHashInterVideo()
    {
        if (!MFXStorage.sharedInstance(this).getPrefBool("MFX4Demo_use_live_ads",false))
        {
            return MOBFOX_HASH_INTER_VIDEO;
        } else {
            return MOBFOX_LIVE_HASH_INTER_VIDEO;
        }
    }

    private String MobfoxHashNative()
    {
        if (!MFXStorage.sharedInstance(this).getPrefBool("MFX4Demo_use_live_ads",false))
        {
            return MOBFOX_HASH_NATIVE;
        } else {
            return MOBFOX_LIVE_HASH_NATIVE;
        }
    }

    //===========================================================================================

    private void initMobFoxSDK()
    {
        MobfoxSDK.init(this);

        /*
        MobfoxSDK.setGDPR(true);
        MobfoxSDK.setGDPRConsentString(GDPRParams.GDPR_DEFAULT_MOBFOX_CONSENT_STRING);

        MobfoxSDK.setDemoAge("32");
        MobfoxSDK.setDemoGender("male");
        MobfoxSDK.setDemoKeywords("basketball,tennis");
        MobfoxSDK.setLatitude(32.455666);
        MobfoxSDK.setLongitude(32.455666);
        */
    }

    //===========================================================================================

    private void startMobFoxSmallBanner()
    {
        clearAllAds();

        mMFXBannerAd = MobfoxSDK.createBanner(MainActivity.this,320,50, MobfoxHashBannerHtml(),bannerListener);
        MobfoxSDK.setBannerFloorPrice(mMFXBannerAd,0.036f);
        MobfoxSDK.loadBanner(mMFXBannerAd);
    }

    private void startMobFoxLargeBanner()
    {
        clearAllAds();

        mMFXBannerAd = MobfoxSDK.createBanner(MainActivity.this,300,250, MobfoxHashBannerHtml(),bannerListener);
        MobfoxSDK.loadBanner(mMFXBannerAd);
    }

    private void startMobFoxVideoBanner()
    {
        clearAllAds();

        mMFXBannerAd = MobfoxSDK.createBanner(MainActivity.this,300,250, MobfoxHashBannerVideo(),bannerListener);
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
            ShowToast("onBannerLoadFailed: "+code);
        }

        @Override
        public void onBannerLoaded(MFXBanner banner) {
            ShowToast("onBannerLoaded");

            MobfoxSDK.addBannerViewTo(banner, relBanner);
        }

        @Override
        public void onBannerShown(MFXBanner banner) {
            ShowToast("onBannerShown");
        }

        @Override
        public void onBannerClosed(MFXBanner banner) {
            ShowToast("onBannerClosed");
        }

        @Override
        public void onBannerFinished(MFXBanner banner) {
            ShowToast("onBannerFinished");
        }

        @Override
        public void onBannerClicked(MFXBanner banner, String url) {
            ShowToast("onBannerClicked");
        }
    };

    //===========================================================================================

    private void startMobFoxHtmlInterstitial()
    {
        clearAllAds();

        mMFXInterstitialAd = MobfoxSDK.createInterstitial(MainActivity.this,
                MobfoxHashInterHtml(),
                interstitialListener);
        MobfoxSDK.loadInterstitial(mMFXInterstitialAd);
    }

    private void startMobFoxVideoInterstitial()
    {
        clearAllAds();

        mMFXInterstitialAd = MobfoxSDK.createInterstitial(MainActivity.this,
                MobfoxHashInterVideo(),
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
            ShowToast("onInterstitialLoaded");

            MobfoxSDK.showInterstitial(mMFXInterstitialAd);
        }

        @Override
        public void onInterstitialLoadFailed(MFXInterstitial interstitial, String code) {
            ShowToast("onInterstitialLoadFailed: "+code);
        }

        @Override
        public void onInterstitialClosed(MFXInterstitial interstitial) {
            ShowToast("onInterstitialClosed");

            MobfoxSDK.releaseInterstitial(mMFXInterstitialAd);
        }

        @Override
        public void onInterstitialClicked(MFXInterstitial interstitial, String url) {
            ShowToast("onInterstitialClicked");
        }

        @Override
        public void onInterstitialShown(MFXInterstitial interstitial) {
            ShowToast("onInterstitialShown");
        }

        @Override
        public void onInterstitialFinished(MFXInterstitial interstitial) {
            ShowToast("onInterstitialFinished");
        }
    };

    //===========================================================================================

    private void startMobFoxNative()
    {
        clearAllAds();

        mMFXNativeAd = MobfoxSDK.createNative(MainActivity.this,
                MobfoxHashNative(),
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
            ShowToast( "on native loaded");

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
            ShowToast( "on images ready");

            Map<String, Bitmap> imageItems = MobfoxSDK.getNativeImageBitmaps(mMFXNativeAd);

            updateNativeImage(iconNative, imageItems.get("icon"));
            updateNativeImage(mainNative, imageItems.get("main"));
        }

        @Override
        public void onNativeLoadFailed(MFXNative aNative, String code) {
            ShowToast( "MFXNative error: "+code);
        }

        @Override
        public void onNativeClicked(MFXNative aNative) {
            ShowToast( "MFXNative clicked");
        }
    };

    //###########################################################################################
    //###########################################################################################
    //#####                                                                                 #####
    //#####   M o P u b                                                                     #####
    //#####                                                                                 #####
    //###########################################################################################
    //###########################################################################################

    private static String mopubBannerInvh          = "4ad212b1d0104c5998b288e7a8e35967";    // Android MobFox Adapter / Test Hash Banner(DONT CHANGE)
    private static String mopubBannerLargeInvh     = "bf453fccdfe74af0ab8f6a944d6ae97a";    // Android MobFox Adapter /
    private static String mopubInterstitialInvh    = "3fd85a3e7a9d43ea993360a2536b7bbd";    // Android MobFox Adapter / Test Hash Interstitial(DONT CHANGE)
    private static String mopubInterVideoInvh      = "562f11d6b8f2499dbd0d1ebfe3c17968";    // Android MobFox Adapter / VIDEO_INTERSTITIAL_HASH (DONT CHANGE)
    private static String mopubNativeInvh          = "b146b367940a4c6da94e8143fb4b66e4";    // Android MobFox Adapter / AvocarrotNative       e2758ffdaf0d426aa19a633bab6bbc3a Test Hash Native (DONT CHANGE)

    //private static String mopubRewardedInvh        = "562f11d6b8f2499dbd0d1ebfe3c17968";    // Android MobFox Adapter / VIDEO_INTERSTITIAL_HASH (DONT CHANGE)
    private static String mopubRewardedInvh        = "005491feb31848a0ae7b9daf4a46c701";    // Android MobFox Adapter / MF Android Rewarded (DONT CHANGE)

    // Shimon ads:
    //private static String mopubBannerInvh          = "2bdfcf59d0f745bea63037b6d89b37d2";
    //private static String mopubBannerLargeInvh     = "949b36a9c6ac4a6799eb66f9f5dd41c1";
    //private static String mopubBannerVideoInvh     = "f63925f72d45427ea12e46d273b95d62";
    //private static String mopubInterstitialInvh    = "ce91a23450a74b2dab5af65ca6de51d1";
    //private static String mopubInterVideoInvh      = "99d11abd6e7a4b5297445df8d6f7371c";
    //private static String mopubNativeInvh          = "841e63ae8a3c42b6a4d692689ef33341";
    //private static String mopubRewardedInvh        = "cd6b6088edfd4e51971eb739e57c6b68";

    private MoPubView         mMoPubBannerAd       = null;
    private MoPubInterstitial mMoPubInterstitialAd = null;
    private MoPubRewardedVideo mMoPubRewardedAd    = null;
    private MoPubNative       mMoPubNativeAd       = null;
    private View              mMoPubNativeView     = null;

    //===========================================================================================

    private void startMoPubSmallBanner()
    {
        final Context c = this;

        MobfoxSDK.init(c);

        //MobfoxSDK.setCOPPA(true);

        mMoPubBannerAd = new MoPubView(this);
        relBanner.addView(mMoPubBannerAd);

        mMoPubBannerAd.setAdUnitId(mopubBannerInvh);
        mMoPubBannerAd.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {
                ShowToast( "MoPub Banner loaded");
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                ShowToast( "MoPub Banner failed");
                banner.destroy();
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                ShowToast( "MoPub Banner clicked");
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
                ShowToast( "MoPub Banner expanded");
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
                ShowToast( "MoPub Banner collapsed");
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
                ShowToast( "loaded");
                if (interstitial.isReady()) {
                    mMoPubInterstitialAd.show();
                } else {
                    ShowToast( "Interstitial is not ready yet !");
                }
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                ShowToast( "Interstitial load failed: " + errorCode);
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
                ShowToast( "shown");
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
                ShowToast( "clicked");
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                ShowToast( "dismissed");
                interstitial.destroy();
            }
        });
        mMoPubInterstitialAd.load();
    }

    private void startMoPubRewarded(String hashCode)
    {
        final Context c = this;

        MoPubRewardedVideos.setRewardedVideoListener(new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                ShowToast( "Rewarded loaded");
                MoPubRewardedVideos.showRewardedVideo(adUnitId);
            }

            @Override
            public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                ShowToast( "Rewarded load failed: " + errorCode.toString());
            }

            @Override
            public void onRewardedVideoStarted(@NonNull String adUnitId) {
                ShowToast( "Rewarded started");
            }

            @Override
            public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                ShowToast( "Rewarded playback failed: " + errorCode.toString());
            }

            @Override
            public void onRewardedVideoClicked(@NonNull String adUnitId) {
                ShowToast( "Rewarded clicked");
            }

            @Override
            public void onRewardedVideoClosed(@NonNull String adUnitId) {
                ShowToast( "Rewarded closed");
            }

            @Override
            public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                // Called when a rewarded video is completed and the user should be rewarded.
                // You can query the reward object with boolean isSuccessful(), String getLabel(), and int getAmount().
                if (reward.isSuccessful())
                {
                    ShowToast( "Rewarded completed, got "+reward.getAmount()+" "+reward.getLabel());
                } else {
                    ShowToast( "Rewarded not completed");
                }
            }
        });

        MoPubRewardedVideos.loadRewardedVideo(hashCode);
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
                ShowToast( "MoPub native loaded");

                nativeAd.prepare(findViewById(R.id.linNative));

                // Set the native event listeners (onImpression, and onClick).
                nativeAd.setMoPubNativeEventListener(new NativeAd.MoPubNativeEventListener() {
                    @Override
                    public void onImpression(View view) {
                        ShowToast( "MoPub native recorded an impression");
                    }

                    @Override
                    public void onClick(View view) {
                        ShowToast( "MoPub native recorded a click");
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
                ShowToast( "MoPub native error: "+errorCode);
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

                    if (MFXStorage.sharedInstance(c).getPrefBool("MFX4Demo_do_like_picsart",false))
                    {
                        DoLikePicsart();
                    }
                }
            });
        }
    }

    //###########################################################################################
    //###########################################################################################
    //#####                                                                                 #####
    //#####   P i c s a r t                                                                 #####
    //#####                                                                                 #####
    //###########################################################################################
    //###########################################################################################

    private static String mopubBannerPicsartInvh       = "49ace665424e41138d1ffe34780f6971";//"4ad212b1d0104c5998b288e7a8e35967";
    private static String mopubInterstitialPicsartInvh = "1932d720d19c4c2ca96fce403e373e46";//"3fd85a3e7a9d43ea993360a2536b7bbd";

    private MoPubView         mMoPubBannerAd1       = null;
    private MoPubInterstitial mMoPubInterstitialAd1 = null;
    private MoPubInterstitial mMoPubInterstitialAd2 = null;

    private void DoLikePicsart()
    {
        final Context c = this;

        // ===== first interstitial: =====================================
        mMoPubInterstitialAd1 = new MoPubInterstitial(self,mopubInterstitialPicsartInvh);
        mMoPubInterstitialAd1.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial loaded");
                if (interstitial.isReady()) {
                    //@@@mMoPubInterstitialAd1.show();
                } else {
                    ShowToast( "Picsart interstitial is not ready yet !");
                }
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                ShowToast( "Picsart interstitial load failed: " + errorCode);
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial shown");
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial clicked");
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial dismissed");
                interstitial.destroy();
            }
        });
        mMoPubInterstitialAd1.load();

        // ===== second interstitial: =====================================

        /*
        mMoPubInterstitialAd2 = new MoPubInterstitial(self,mopubInterstitialPicsartInvh);
        mMoPubInterstitialAd2.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial2 loaded");
                if (interstitial.isReady()) {
                    //@@@mMoPubInterstitialAd2.show();
                } else {
                    ShowToast( "Picsart interstitial2 is not ready yet !");
                }
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                ShowToast( "Picsart interstitial2 load failed: " + errorCode);
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial2 shown");
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial2 clicked");
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                ShowToast( "Picsart interstitial2 dismissed");
                interstitial.destroy();
            }
        });
        mMoPubInterstitialAd2.load();
        */
        // ===== first banner: =====================================

        mMoPubBannerAd1 = new MoPubView(this);
        //@@@relBanner.addView(mMoPubBannerAd1);

        mMoPubBannerAd1.setAdUnitId(mopubBannerPicsartInvh);
        mMoPubBannerAd1.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {
                ShowToast( "Picsart Banner loaded");
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                ShowToast( "Picsart Banner failed");
                banner.destroy();
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                ShowToast( "Picsart Banner clicked");
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
                ShowToast( "Picsart Banner expanded");
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
                ShowToast( "Picsart Banner collapsed");
                banner.destroy();
            }
        });

        /*
        Location locCurr = new Location(LocationManager.GPS_PROVIDER);
        locCurr.setLatitude (32.000000);
        locCurr.setLongitude(35.000000);
        mMoPubBannerAd1.setLocation(locCurr);

        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put("demo_age"   , "23");
        localExtras.put("demo_gender", "female");
        localExtras.put("r_floor"    , "0.03");
        localExtras.put("keywords"   , "soccer,baseball");

        mMoPubBannerAd1.setLocalExtras(localExtras);
        */

        if (MoPub.isSdkInitialized()) {
            mMoPubBannerAd1.loadAd();
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

    //private static String   admobRewardedInvh     = "ca-app-pub-3940256099942544/5224354917";   // AdMob test ad unit
    //private static String   admobRewardedInvh     = "ca-app-pub-8111915318550857/7271416015";   // sdk.mobfox.com.appcore
    private static String   admobRewardedInvh     = "ca-app-pub-6224828323195096/1152622735";   // "Shimon Rewarded" - com.lyrebirdstudio.colorizer.lite
    //private static String   admobRewardedInvh     = "ca-app-pub-6224828323195096/6739427693";   // "rewardedExample" - com.lyrebirdstudio.colorizer.lite
    //private static String   admobRewardedInvh     = "ca-app-pub-6224828323195096/7876284361";   // iOS
    //private static String   admobRewardedInvh   = "ca-app-pub-8111915318550857/7271416015";   // sdk.mobfox.com.appcore

    //private static String   admobNativeInvh       = "ca-app-pub-3940256099942544/2247696110";
    //private static String   admobNativeInvh       = "ca-app-pub-6224828323195096~6049137964";
    //private static String   admobNativeInvh       = "ca-app-pub-6224828323195096/3417473479";   // advancedNativeTest
    private static String   admobNativeInvh       = "ca-app-pub-6224828323195096/1268034150";   // Native Android For AdMob

    private AdView          mAdMobBannerView      = null;
    private InterstitialAd  mAdMobInterstitialAd  = null;
    private RewardedAd      mAdMobRewardedAd      = null;
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
                ShowToast( "AdMob Banner loaded");
                Log.d("MobfoxSDK", "AdMob Banner loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                switch (errorCode)
                {
                    case ERROR_CODE_INTERNAL_ERROR:
                        ShowToast( "AdMob Banner failed (INTERNAL_ERROR)");
                        break;
                    case ERROR_CODE_INVALID_REQUEST:
                        ShowToast( "AdMob Banner failed (INVALID_REQUEST)");
                        break;
                    case ERROR_CODE_NETWORK_ERROR:
                        ShowToast( "AdMob Banner failed (NETWORK_ERROR)");
                        break;
                    case ERROR_CODE_NO_FILL:
                        ShowToast( "AdMob Banner failed (NO_FILL)");
                        break;
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                ShowToast( "AdMob Banner opened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                ShowToast( "AdMob Banner clicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                ShowToast( "AdMob Banner left app");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                ShowToast( "AdMob Banner closed");
            }
        });


        Location locCurr = new Location(LocationManager.GPS_PROVIDER);
        locCurr.setLatitude (32.009876);
        locCurr.setLongitude(35.006789);

        Bundle bundle = new Bundle();
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

        mAdMobInterstitialAd = new InterstitialAd(self);
        mAdMobInterstitialAd.setAdUnitId(invh);

        mAdMobInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                ShowToast( "AdMob Interstitial onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                switch (errorCode)
                {
                    case ERROR_CODE_INTERNAL_ERROR:
                        ShowToast( "AdMob Interstitial failed (INTERNAL_ERROR)");
                        break;
                    case ERROR_CODE_INVALID_REQUEST:
                        ShowToast( "AdMob Interstitial failed (INVALID_REQUEST)");
                        break;
                    case ERROR_CODE_NETWORK_ERROR:
                        ShowToast( "AdMob Interstitial failed (NETWORK_ERROR)");
                        break;
                    case ERROR_CODE_NO_FILL:
                        ShowToast( "AdMob Interstitial failed (NO_FILL)");
                        break;
                }
            }

            @Override
            public void onAdLeftApplication() {
                ShowToast( "AdMob Interstitial onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                ShowToast( "AdMob Interstitial onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                ShowToast( "AdMob Interstitial onAdLoaded");
                mAdMobInterstitialAd.show();
            }
        });

        Location locCurr = new Location(LocationManager.GPS_PROVIDER);
        locCurr.setLatitude (32.009876);
        locCurr.setLongitude(35.006789);

        Bundle bundle = new Bundle();
        bundle.putString("demo_age","24");
        bundle.putString("demo_gender","male");
        bundle.putString("r_floor","0.04");

        AdRequest adRequestInterstitial = new AdRequest.Builder()
                //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .addNetworkExtrasBundle(MobFoxAdapter.class, bundle)
                .addKeyword("football,basketball")
                .setLocation(locCurr)
                .build();

        mAdMobInterstitialAd.loadAd(adRequestInterstitial);
    }

    private void startAdMobRewarded(String invh)
    {
        final Context c = this;

        clearAdMobRewarded();

        mAdMobRewardedAd = new RewardedAd(self, invh);

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                ShowToast( "AdMob onRewardedAdLoaded");

                if (mAdMobRewardedAd.isLoaded()) {
                    Activity activityContext = MainActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                            ShowToast( "AdMob onRewardedAdOpened");
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            ShowToast( "AdMob onRewardedAdClosed");
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            ShowToast( "AdMob onUserEarnedReward("+reward.getAmount()+", "+reward.getType()+")");
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            ShowToast( "AdMob onRewardedAdFailedToShow: "+errorCode);
                        }
                    };
                    mAdMobRewardedAd.show(activityContext, adCallback);
                } else {
                    ShowToast( "AdMob The rewarded ad wasn't loaded yet.");
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                switch (errorCode)
                {
                    case ERROR_CODE_INTERNAL_ERROR:
                        ShowToast( "AdMob Rewarded failed (INTERNAL_ERROR)");
                        break;
                    case ERROR_CODE_INVALID_REQUEST:
                        ShowToast( "AdMob Rewarded failed (INVALID_REQUEST)");
                        break;
                    case ERROR_CODE_NETWORK_ERROR:
                        ShowToast( "AdMob Rewarded failed (NETWORK_ERROR)");
                        break;
                    case ERROR_CODE_NO_FILL:
                        ShowToast( "AdMob Rewarded failed (NO_FILL)");
                        break;
                }
            }
        };

        Location locCurr = new Location(LocationManager.GPS_PROVIDER);
        locCurr.setLatitude (32.009876);
        locCurr.setLongitude(35.006789);

        Bundle bundle = new Bundle();
        bundle.putString("demo_age","24");
        bundle.putString("demo_gender","male");
        bundle.putString("r_floor","0.04");

        AdRequest adRequestRewarded = new AdRequest.Builder()
//                .addTestDevice("82109714761F90BAAD73679C21E34E56")
                .addNetworkExtrasBundle(MobFoxAdapter.class, bundle)
                .addKeyword("football,basketball")
                .setLocation(locCurr)
                .build();

        mAdMobRewardedAd.loadAd(adRequestRewarded, adLoadCallback);
    }

    private void startAdMobNative()
    {
        clearAdMobNative();

        final Context c = this;

        mAdMobNativeAdLoader = new AdLoader.Builder(this, admobNativeInvh)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                        // Show the ad.
                        if ((mAdMobNativeAdLoader!=null) && (mAdMobNativeAdLoader.isLoading())) {
                            // The AdLoader is still loading ads.
                            // Expect more adLoaded or onAdFailedToLoad callbacks.
                            ShowToast("AdMob Native loading...");
                        } else {
                            // The AdLoader has finished loading ads.
                            ShowToast("AdMob Native DONE loading !");

                            //=== populate unified native ad view:
                            FrameLayout frameLayout = findViewById(R.id.linAdMobNative);
                            UnifiedNativeAdView adView      = (UnifiedNativeAdView)getLayoutInflater().inflate(R.layout.admob_native_layout, null);

                            TextView titleNativeAdMob     = (TextView)adView.findViewById(R.id.titleNativeAdMob);
                            adView.setHeadlineView(titleNativeAdMob);

                            TextView descNativeAdMob      = (TextView)adView.findViewById(R.id.descNativeAdMob);
                            adView.setBodyView(descNativeAdMob);

                            TextView ratingNativeAdMob    = (TextView)adView.findViewById(R.id.ratingNativeAdMob);
                            adView.setStarRatingView(ratingNativeAdMob);

                            TextView sponsoredNativeAdMob = (TextView)adView.findViewById(R.id.sponsoredNativeAdMob);
                            adView.setAdvertiserView(sponsoredNativeAdMob);

                            TextView ctaNativeAdMob       = (TextView)adView.findViewById(R.id.ctaNativeAdMob);
                            adView.setCallToActionView(ctaNativeAdMob);

                            ImageView iconNativeAdMob     = (ImageView)adView.findViewById(R.id.iconNativeAdMob);
                            adView.setIconView(iconNativeAdMob);

                            ImageView mainNativeAdMob     = (ImageView)adView.findViewById(R.id.mainNativeAdMob);
                            adView.setImageView(mainNativeAdMob);

                            adView.setNativeAd(unifiedNativeAd);

                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);

                            //=== populate items:
                            updateNativeText(titleNativeAdMob    , unifiedNativeAd.getHeadline());//textItems.get("title"));
                            updateNativeText(descNativeAdMob     , unifiedNativeAd.getBody());//textItems.get("desc"));
                            updateNativeText(ratingNativeAdMob   , String.valueOf(unifiedNativeAd.getStarRating()));
                            updateNativeText(sponsoredNativeAdMob, unifiedNativeAd.getAdvertiser());
                            updateNativeText(ctaNativeAdMob      , unifiedNativeAd.getCallToAction());

                            com.google.android.gms.ads.formats.NativeAd.Image imgIcon = unifiedNativeAd.getIcon();
                            if (imgIcon!=null)
                            {
                                Drawable dIcon = imgIcon.getDrawable();
                                if (dIcon!=null)
                                {
                                    updateNativeImage(iconNativeAdMob, drawableToBitmap(dIcon));
                                }
                            }
                            List<com.google.android.gms.ads.formats.NativeAd.Image> lstImages = unifiedNativeAd.getImages();
                            if ((lstImages!=null) && (lstImages.size()>0))
                            {
                                Drawable dMain = lstImages.get(0).getDrawable();
                                if (dMain!=null)
                                {
                                    updateNativeImage(mainNativeAdMob, drawableToBitmap(dMain));
                                }
                            }
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                        switch (errorCode)
                        {
                            case ERROR_CODE_INTERNAL_ERROR:
                                ShowToast( "AdMob Native failed (INTERNAL_ERROR)");
                                break;
                            case ERROR_CODE_INVALID_REQUEST:
                                ShowToast( "AdMob Native failed (INVALID_REQUEST)");
                                break;
                            case ERROR_CODE_NETWORK_ERROR:
                                ShowToast( "AdMob Native failed (NETWORK_ERROR)");
                                break;
                            case ERROR_CODE_NO_FILL:
                                ShowToast( "AdMob Native failed (NO_FILL)");
                                break;
                        }
                    }

                    @Override
                    public void onAdLoaded()
                    {
                        ShowToast( "AdMob Native onAdLoaded");
                    }

                    @Override
                    public void onAdImpression()
                    {
                        ShowToast( "AdMob Native onAdImpression");
                    }

                    @Override
                    public void onAdOpened()
                    {
                        ShowToast( "AdMob Native onAdOpened");
                    }

                    @Override
                    public void onAdClicked()
                    {
                        ShowToast( "AdMob Native onAdClicked");
                    }

                    @Override
                    public void onAdLeftApplication()
                    {
                        ShowToast( "AdMob Native onAdLeftApplication");
                    }

                    @Override
                    public void onAdClosed()
                    {
                        ShowToast( "AdMob Native onAdClosed");
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        Location locCurr = new Location(LocationManager.GPS_PROVIDER);
        locCurr.setLatitude (32.009876);
        locCurr.setLongitude(35.006789);

        Bundle bundle = new Bundle();
        bundle.putString("demo_age","24");
        bundle.putString("demo_gender","male");
        bundle.putString("r_floor","0.04");

        AdRequest adRequestNtive = new AdRequest.Builder()
                .addTestDevice("82109714761F90BAAD73679C21E34E56")
                .addCustomEventExtrasBundle(MobFoxAdapter.class, bundle)
                .addKeyword("football,basketball")
                .setLocation(locCurr)
                .build();

        mAdMobNativeAdLoader.loadAd(adRequestNtive);
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

    private void clearAdMobRewarded()
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
