package jp.co.jokerpiece.piecebase;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebViewFragment;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.data.SaveData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.BeaconUtil;

/**
 * (注意)
 * MainActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
 * で設定している箇所はタブの遷移のみを行っています。
 * データの受け渡しに関しては未実装です。
 * "Shopping"の部分はタブに設定しているクラスのクラス名を含む文字列を設定すればOK。
 * AppUtil.getPositionメソッドが-1で返ってくる場合は何も起こりません。
 */
public class MainBaseActivity extends FragmentActivity implements OnTabChangeListener {
    private static final String TAG = MainBaseActivity.class.getSimpleName();

    private Context context;
    public static FragmentTabHost tabHost;
    public String myTheme = "";
    public TabColorState tabColorState;
    public ArrayList<HashMap<String, Object>> settingData = new ArrayList<HashMap<String, Object>>();
    public static ArrayList<TabInfo> tabInfoList;
    public static HashMap<String, Integer> titleOfActionBar;
    public static boolean startFromSchemeFlg = false;
    public static boolean firstTimeStart = true;
    ImageView splashView;
    boolean onTabChange;
    boolean onTab = false;
    long timer;
    public ArrayList<BaseFragment> list = new ArrayList<BaseFragment>();
    public static String intentClassName = null;

    private final MainBaseActivity self = this;
    public FirebaseAnalytics mAnalytics;

    SharedPreferences systemData;
    SharedPreferences.Editor systemDataEditor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnalytics = FirebaseAnalytics.getInstance(self);

        // ユーザープロパティの設定
        //mAnalytics.setUserProperty("key", "value");

        //mAnalytics.setUserProperty("favorite_food", "mFavoriteFood");

        //Bundle params = new Bundle();
        //params.putString("image_name", "name");
        //params.putString("full_text", "text");
        //mAnalytics.logEvent("share_image", params);

        if(Config.ANALYTICS_MODE.equals("true")){
            App app = (App)this.getApplication();
            Tracker t = app.getTracker(App.TrackerName.APP_TRACKER);
            t.setScreenName("MAIN ACTIVITY");
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }

        //Set the line pay switch's status
        systemData = this.getSharedPreferences("SystemDataSave", Context.MODE_PRIVATE);
        String status = systemData.getString("linepay_switch","");

        //Check the switch status which last used
        if(status != null)
        {
            if(status.equals("true"))
            {
                Config.PAY_SELECT_KBN = "1";

                Config.CARTURLENABLE=false;
            }
            else
            {
                Config.PAY_SELECT_KBN = "0";

                Config.CARTURLENABLE=true;
            }
        }

        context = this;
        Config.PACKAGE_NAME = getApplicationContext().getPackageName();
        FlyerFragment Fy = new FlyerFragment();
        CouponFragment Cf = new CouponFragment();
        ShoppingFragment Sf = new ShoppingFragment();
        setTheme();
        getActionBar().hide();
        setContentView(R.layout.activity_main);
        AppUtil.setPrefString(context, "FLYERID", "0");
        splashView = (ImageView) findViewById(R.id.splashView);
        timer = Config.SPLASH_TIME * 1000;
        list.add(Fy);
        list.add(Cf);
        //list.add(Sf);

        for (int i = 0; i < list.size(); i++) {
            if (!SaveData.SplashIsFinished) {
                list.get(i).doInSplash(this);
            }
        }
        // 10秒カウントダウンする
        new CountDownTimer(timer, 1000) {
            // カウントダウン処理
            public void onTick(long millisUntilFinished) {
            }

            // カウントが0になった時の処理
            public void onFinish() {
                splashView.setVisibility(View.GONE);
                getActionBar().show();
            }
        }.start();


        //ローカルプッシュ通知の設定
        NotificationManager mManager;
        mManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.cancelAll();

        settingData = setConfig();
        titleOfActionBar = setTitleOfActionBar();
        tabInfoList = setTabInfoList();
        tabColorState = setTabColorState();

        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setup(this, getSupportFragmentManager(), R.id.real_content);

        for (int i = 0; i < tabInfoList.size(); i++) {
            addTab(tabInfoList.get(i));
        }

        tabHost.setOnTabChangedListener(this);

        // クリックイベントを設定する
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Config.Backflg = false;
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (onTab) {
                            return false;
                        }
                        onTab = true;
                        new CountDownTimer(1000, 1000) {
                            // カウントダウン処理
                            public void onTick(long millisUntilFinished) {
                            }

                            // カウントが0になった時の処理
                            public void onFinish() {
                                onTab = false;
                            }
                        }.start();
                        AppUtil.setPrefString(context, "FLYERID", "0");
                        if (v.equals(tabHost.getCurrentTabView())) {
                            onTabChange = false;
                        }
//                        getCurrentRootFragment().getChildFragmentManager()
//                                .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                        for (int i = 0; i < backStackCount; i++) {
                            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        if (!Config.haveUrlFlg) {
                            if (tabHost.getCurrentTab() != Config.CouponFragmentNum) {
                                if (v.equals(tabHost.getCurrentTabView()) && !onTabChange) {
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.fragment, Fragment.instantiate(
                                            context,
                                            tabInfoList.get(tabHost.getCurrentTab()).cls.getName()));
                                    ft.commit();
                                    return true;
                                }
                            }
                        } else {
                            if (v.equals(tabHost.getCurrentTabView()) && !onTabChange) {
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment, Fragment.instantiate(
                                        context,
                                        tabInfoList.get(tabHost.getCurrentTab()).cls.getName()));
                                ft.commit();
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
        }

        /**
         * IS_BEACON_ENABLEDがtrueの場合はビーコン処理を実行する
         */
        if (Config.IS_BEACON_ENABLED) {
            // ビーコン処理の初期化
            BeaconUtil.init(this);
            // ビーコン検索処理
            BeaconUtil.startScan();
        }
        //最初のフラグメントを記録
        Config.Savelist.add(0);
        setFragmentNum();
//        if(GcmIntentService.notification_flg){
//            GcmIntentService.notification_flg = false;
//            MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Infomation"));
//        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        runIfGetIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runIfGetIntent(getIntent());
        // プッシュ通知処理の初期化
        Common.setupGcm(context, (Activity) context, Config.loaderCnt++);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Config.Backflg = true;
                //スタックを戻る。
                int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
                if (backStackCnt != 0) {
                    getSupportFragmentManager().popBackStack();
                }
                else
                {
                    if (Config.Backflg) {
                        if (Config.FragmentCurrentNum > 0) {
                            MainBaseActivity.tabHost.setCurrentTab((Integer) Config.Savelist.get(Config.FragmentCurrentNum - 1));
                        }
                        if (Config.Savelist.size() > 1) {
                            Config.Savelist.remove(Config.FragmentCurrentNum);
                        }
                        if (Config.FragmentCurrentNum > 0) {
                            Config.FragmentCurrentNum -= 1;
                        }
                    }
                    return false;
                }
        }

        return false;
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // KeyEvent.ACTION_DOWN以外のイベントを無視する
//        // （これがないとKeyEvent.ACTION_UPもフックしてしまう）
//        if (event.getAction() != KeyEvent.ACTION_DOWN) {
//            return false;
//        }
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                if (!getCurrentRootFragment().popBackStack()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        return false;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BeaconUtil.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetoothの自動ONが成功した場合呼ばれる
                    AppUtil.debugLog(TAG, "Bluetoothの自動ONに成功しました。");
                    BeaconUtil.isGetBluetoothAdapter = true;
                    BeaconUtil.startScan();
                }
                break;
        }
    }

    /**
     * アプリのテーマを設定する。
     * overrideする場合は、以下の通りに実装する。
     * 1.myTheme = "[default/cute]";
     * 2.super.setTheme();
     * super.setThemeはmyThemeを設定してから呼んでください。
     */
    public void setTheme() {
        // Theme setting
        switch (myTheme) {
            case "cute":
                setTheme(R.style.AppTheme_cute);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }

    /**
     * タブに設定する内容を宣言する。
     */
    public ArrayList<HashMap<String, Object>> setConfig() {
        return new ArrayList<HashMap<String, Object>>(Arrays.asList(
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.flyer1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_flyer);
                    }

                    {
                        put("cls", FlyerFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.info1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_infomation);
                    }

                    {
                        put("cls", InfomationFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.shopping1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_shopping);
                    }

                    {
                        put("cls", ShoppingFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.coupon1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_coupon);
                    }

                    {
                        put("cls", CouponFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.fitting1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_fitting);
                    }

                    {
                        put("cls", FittingFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.map1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_map);
                    }

                    {
                        put("cls", MapViewFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.barcode1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_barcode);
                    }

                    {
                        put("cls", QRCodeCaptFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.stamp1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_stamp);
                    }

                    {
                        put("cls", StampFragment.class);
                    }
                },
//                new HashMap<String, Object>() {
//                    {
//                        put("tabTitle", getString(R.string.sns1));
//                    }
//
//                    {
//                        put("tabIcon", R.drawable.icon_sns);
//                    }
//
//                    {
//                        put("cls", SnsFragment.class);
//                    }
//                },
//                new HashMap<String, Object>() {
//                    {
//                        put("tabTitle", getString(R.string.twitter1));
//                    }
//
//                    {
//                        put("tabIcon", R.drawable.i_twitter);
//                    }
//
//                    {
//                        put("cls", TwitterFragment.class);
//                    }
//                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.reminder1));
                    }

                    {
                        put("tabIcon", R.drawable.icon_birthday_2x_360);
                    }

                    {
                        put("cls", ReminderFragment.class);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("tabTitle", getString(R.string.setting1));
                    }

                    {
                        put("tabIcon", R.drawable.ic_action_settings);
                    }

                    {
                        put("cls", SettingFragment.class);
                    }
                }
        ));
    }

    /**
     * 画面のアクションバータイトルに対応するリソースを設定する。
     */
    public HashMap<String, Integer> setTitleOfActionBar() {
        return new HashMap<String, Integer>() {
            {
                put(FlyerFragment.class.getSimpleName(), R.string.flyer0);
            }

            {
                put(InfomationFragment.class.getSimpleName(), R.string.info0);
            }

            {
                put(InfomationSyosaiFragment.class.getSimpleName(), R.string.info0);
            }

            {
                put(ShoppingFragment.class.getSimpleName(), R.string.shopping0);
            }

            {
                put(ShoppingGoodsFragment.class.getSimpleName(), R.string.item_list);
            }

            {
                put(CouponFragment.class.getSimpleName(), R.string.coupon_get0);
            }

            {
                put(CouponUseFragment.class.getSimpleName(), R.string.coupon_use0);
            }

            {
                put(WebViewFragment.class.getSimpleName(), R.string.webview);
            }

            {
                put(FittingFragment.class.getSimpleName(), R.string.fitting0);
            }

            {
                put(MapViewFragment.class.getSimpleName(), R.string.map0);
            }

            {
                put(QRCodeCaptFragment.class.getSimpleName(), R.string.barcode0);
            }

            {
                put(StampFragment.class.getSimpleName(), R.string.stamp0);
            }

//            {
//                put(SnsFragment.class.getSimpleName(), R.string.sns0);
//            }
//
//            {
//                put(TwitterFragment.class.getSimpleName(), R.string.twitter0);
//            }

            {
                put(ReminderFragment.class.getSimpleName(), R.string.reminder0);
            }
            {
                put(SettingFragment.class.getSimpleName(),R.string.setting0);
            }

        };
    }


    public void setFragmentNum() {
        //Tebalの順番に合わせて番号を設定
        Config.FlyerFragmentNum = 0;
        Config.InfoFragmentNum = 1;
        Config.ShoppingFragmentNum = 2;
        Config.CouponFragmentNum = 3;
        Config.FittingFragmentNum = 4;
        Config.MapFragmentNum = 5;
        Config.BarcodeFragmentNum = 6;
        Config.StampFragmentNum = 7;
        Config.SnsFragmentNum = 8;
        Config.TwitterFragmentNum = 9;
        Config.WebViewFragmentNum = 10;
        Config.ReminederFragmentNum = 11;
        Config.SettingFragmentNum = 12;
    }

    /**
     * tabに設定する情報を設定する。
     */
    @SuppressWarnings("unchecked")
    public ArrayList<TabInfo> setTabInfoList() {
        ArrayList<TabInfo> tabInfoList = new ArrayList<TabInfo>();
        for (int i = 0; i < settingData.size(); i++) {
            HashMap<String, Object> data = settingData.get(i);
            tabInfoList.add(new TabInfo(
                    "TAB" + i,
                    (String) data.get("tabTitle"),
                    (Integer) data.get("tabIcon"),
                    (Class<? extends Fragment>) data.get("cls")));
        }
        return tabInfoList;
    }

    /**
     * タブに設定するTabColorStateを取得する。<br>
     * (TabColorStateのコンストラクタ)<br>
     * (1)bgSelected: 選択時の背景<br>
     * (2)bgUnselected: 非選択時の背景<br>
     * (3)ftSelected: 選択時のフォント色<br>
     * (4)ftUnselected: 非選択時のフォント色<br>
     * (例)<br>
     * return new TabColorState(<br>
     * Color.argb(0xff, 0xcc, 0xcc, 0xcc),<br>
     * Color.argb(0xff, 0xff, 0xff, 0xff),<br>
     * Color.argb(0xff, 0x66, 0x66, 0x66),<br>
     * Color.argb(0xff, 0x66, 0x66, 0x66));<br>
     * (注意)<br>
     * 設定しない場合は-1を設定する。<br>
     * 但し、「(1)と(2)」「(3)と(4)」はセットです。
     *
     * @return タブに設定するTabColorState
     */
    public TabColorState setTabColorState() {
        return new TabColorState(
                -1,
                -1,
                -1,
                -1);
    }

    /**
     * タブを追加する。
     **/
    public void addTab(TabInfo tabInfo) {
        View childView = new CustomTabContentView(this, tabInfo.title, tabInfo.resId);
        TabSpec tabSpec = tabHost.newTabSpec(tabInfo.tag).setIndicator(childView);
        Bundle args = new Bundle();
        args.putString("root", tabInfo.cls.getName());
        tabHost.addTab(tabSpec, RootFragment.class, args);
    }

    /**
     * タブ情報を保持しておくクラス。
     */
    public class TabInfo {
        public String tag;
        public String title;
        public int resId;
        public Class<? extends Fragment> cls;

        public TabInfo(String tag, String title, int resId, Class<? extends Fragment> cls) {
            this.tag = tag;
            this.title = title;
            this.resId = resId;
            this.cls = cls;
        }
    }

    /**
     * 現在タブに設定されているフラグメントを取得する。
     **/
    public RootFragment getCurrentRootFragment() {
        return (RootFragment) getSupportFragmentManager().findFragmentById(R.id.real_content);
    }

    @Override
    public void onTabChanged(String tabId) {
//            AppUtil.debugLog("tag", "" + tabId);
        AppUtil.debugLog("mAdapter", "0.5");
        onTabChange = true;
//            String s = String.valueOf(Config.CouponFragmentNum);
//                if (!tabId.equals("TAB" + s)) {    //couponFragmentだけは初期化しない
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        // 初期化するため
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, Fragment.instantiate(
                context,
                tabInfoList.get(tabHost.getCurrentTab()).cls.getName()));
        ft.commit();
//                }

    }

    /**
     * TabWidget用の独自Viewを作ります。
     */
    public class CustomTabContentView extends FrameLayout {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public CustomTabContentView(Context context) {
            super(context);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public CustomTabContentView(Context context, String title, int resId) {
            this(context);
            View childview = inflater.inflate(R.layout.tab_widget, null);
            LinearLayout ll = (LinearLayout) childview.findViewById(R.id.ll_tab);
            ImageView iv = (ImageView) childview.findViewById(R.id.iv_tab);
            iv.setImageResource(resId);
            TextView tv = (TextView) childview.findViewById(R.id.tv_tab);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            tv.setText(title);

            // Theme setting
            switch (myTheme) {
                case "cute":
                    ll.setBackgroundResource(R.drawable.shape_tabbtn_cute);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_color_cute));
                    break;
                default:
                    ll.setBackgroundResource(R.drawable.shape_tabbtn);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_color_cute));
                    break;
            }

            // Tab colorstate
            if (tabColorState != null) {
                // 背景
                if (tabColorState.bgSelected != -1 && tabColorState.bgUnselected != -1) {
                    Drawable pressed = new ColorDrawable(Color.argb(0x90, 0x8e, 0xb7, 0xff));
                    Drawable selected = new ColorDrawable(tabColorState.bgSelected);
                    Drawable unselected = new ColorDrawable(tabColorState.bgUnselected);

                    StateListDrawable d = new StateListDrawable();
                    d.addState(new int[]{android.R.attr.state_pressed}, pressed);
                    d.addState(new int[]{android.R.attr.state_selected}, selected);
                    d.addState(new int[]{-android.R.attr.state_focused}, unselected);

                    int sdkVersion = Build.VERSION.SDK_INT;
                    if (sdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                        ll.setBackgroundDrawable(d);
                    } else {
                        ll.setBackground(d);
                    }
                }

                // フォント色
                if (tabColorState.ftSelected != -1 && tabColorState.ftUnselected != -1) {
                    ColorStateList c = new ColorStateList(
                            new int[][]{
                                    new int[]{android.R.attr.state_selected},
                                    new int[]{-android.R.attr.state_focused}
                            },
                            new int[]{
                                    tabColorState.ftSelected,
                                    tabColorState.ftUnselected
                            });
                    tv.setTextColor(c);
                }
            }
            addView(childview);
        }
    }

    /**
     * タブに設定するcolorstateを保持する。<br>
     * (1)bgSelected: 選択時の背景<br>
     * (2)bgUnselected: 非選択時の背景<br>
     * (3)ftSelected: 選択時のフォント色<br>
     * (4)ftUnselected: 非選択時のフォント色<br>
     */
    public class TabColorState {
        /**
         * 選択時の背景
         */
        public int bgSelected;
        /**
         * 非選択時の背景
         */
        public int bgUnselected;
        /**
         * 選択時のフォント色
         */
        public int ftSelected;
        /**
         * 非選択時のフォント色
         */
        public int ftUnselected;

        /**
         * コンストラクタ
         */
        public TabColorState(int bgSelected, int bgUnselected,
                             int ftSelected, int ftUnselected) {
            this.bgSelected = bgSelected;
            this.bgUnselected = bgUnselected;
            this.ftSelected = ftSelected;
            this.ftUnselected = ftUnselected;
        }
    }


    /**
     * getIntent()で取得できた場合の処理を記述します。
     * 本メソッドをonResume()で呼んでいるのは、launchModeがsingleTaskの場合、
     * メインアクティビティを起動中はonCreate()は呼ばれずonResume()から呼ばれるため。
     */
    public void runIfGetIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.getString("type") != null) {
            switch (bundle.getString("type")) {
                case NewsListData.NEWS_DATA_TYPE_INFOMATION + "":
                    intentClassName = "Infomation";
                    //MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Infomation"));
//    			FragmentManager fmInfo = getSupportFragmentManager();
//    			FragmentTransaction ftInfo = fmInfo.beginTransaction();
//    			ftInfo.addToBackStack(null);
//    			InfomationSyosaiFragment fragmentInfo = new InfomationSyosaiFragment();
//    			Bundle bundleInfo = new Bundle();
//    			bundleInfo.putString("newsId", bundle.getString("newsId"));
//    			fragmentInfo.setArguments(bundleInfo);
//    			ftInfo.replace(R.id.fragment, fragmentInfo);
//    			ftInfo.commit();
                    break;
                case NewsListData.NEWS_DATA_TYPE_FLYER + "":
                    intentClassName = "Flyer";
                    //MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Flyer"));
//    			FragmentManager fmNews = getSupportFragmentManager();
//    			FragmentTransaction ftNews = fmNews.beginTransaction();
//    			ftNews.addToBackStack(null);
//    			FlyerFragment fragmentNews = new FlyerFragment();
//    			Bundle bundleNews= new Bundle();
//    			bundleNews.putString("flyer_ID", bundle.getString("flyer_ID"));
//    			fragmentNews.setArguments(bundleNews);
//    			ftNews.replace(R.id.fragment, fragmentNews);
//    			ftNews.commit();
                    break;
                case NewsListData.NEWS_DATA_TYPE_COUPON + "":
                    intentClassName = "Coupon";
                    //MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Coupon"));
//    			FragmentManager fmCoupon = getSupportFragmentManager();
//    			FragmentTransaction ftCoupon = fmCoupon.beginTransaction();
//    			ftCoupon.addToBackStack(null);
//    			CouponFragment fragmentCoupon = new CouponFragment();
//    			Bundle bundleCoupon= new Bundle();
//    			bundleCoupon.putString("coupon_code", bundle.getString("coupon_code"));
//    			fragmentCoupon.setArguments(bundleCoupon);
//    			ftCoupon.replace(R.id.fragment, fragmentCoupon);
//    			ftCoupon.commit();
                    break;
                default:
                    break;
            }
        }
    }

}
