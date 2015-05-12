### Piece�v���W�F�N�g�̍쐬
AndroidStudio��Piece�v���W�F�N�g(�ȉ��A�{�v���W�F�N�g)���쐬���ĉ������B
�����`���[�A�N�e�B�r�e�B�́A�ujp.co.jokerpiece.piece.MainActivity�v�ƂȂ�܂��B
���C�u�����Əd�����郊�\�[�X�́A�{�v���W�F�N�g���D�悳��܂��B
android.PieceCore���C�u�����Ɂudrawable/ic_launcher.png�v�͗p�ӂ��Ă���̂Ŗ{�v���W�F�N�g��ic_launcher�t�@�C���͂��ׂč폜���ĉ������B

#### google-play-services_lib���C�u�����̃C���|�[�g
"Piece/app/build.gradle"����dependencies�Łucompile 'com.google.android.gms:play-services:+'�v�����s���邱�ƂŃ��C�u������ǂݍ���ł��܂��B

#### android.PieceCore���C�u�����̃C���|�[�g
"Piece/app/build.gradle"����maven��url���w�肵�Adependencies�Łucompile 'jp.co.jokerpiece.android.piececore:android.piececore:0.0.+@aar'�v�����s���邱�ƂŃ��C�u������ǂݍ���ł��܂��B

![Sync Project](./mdImage/syncProj.png)�{�^���������Ńr���h�����s���A![Run Project](./mdImage/runProj.png)�{�^���������ŃA�v�������s���ĉ������B

### app_id�̐ݒ�

"Piece/app/src/main/assets"�t�H���_��settingFile.txt�ɖ{�A�v���ݒ���e���L�q���ĉ������B
���L��settingFile.txt�̋L�q����L���܂��B

settingFile.txt

    app_id=pieceSample
    app_key=jokerpiece_appKey
    splash_time=3
    project_id=367759414941
    beacon_isEnabled=false

### ���p���@

MainActivity�́A���C�u�����ɂ���ujp.co.jokerpiece.piecebase.MainBaseActivity�v��extends����K�v������܂��B
�ǉ�����^�u�́AsetConfig()���\�b�h��Override���邱�ƂŁA�������邱�Ƃ��ł��܂��B

setConfig()���\�b�h�ł́AArrayList<HashMap<String, Object>>��߂�l�Ƃ��ĕԂ��K�v������܂��B
���X�g�̐����^�u�̐��ƂȂ�A�^�u�̒��ɐݒ肷��ݒ���e��HashMap�Őݒ肷�邱�Ƃ��ł��܂��B
�ȉ���HashMap�Őݒ�ł���L�[�ƃo�����[�������܂��B

HashMap<String, Object>

| �L�[ | ���� | �o�����[ |
| --- | --- | --- |
| tabTitle | �^�u�ɕ\������^�C�g�� | getString(R.string.flyer1), getString(R.string.info1), getString(R.string.shopping1), getString(R.string.coupon1), getString(R.string.fitting1),  getString(R.string.map1)|
| tabIcon | �^�u�ɐݒ肷��摜 | R.drawable.icon_flyer, R.drawable.icon_infomation, R.drawable.icon_shopping, R.drawable.icon_coupon, R.drawable.icon_fitting, R.drawable.icon_map |
| cls | �^�u�ɐݒ肷���� | FlyerFragment.class, InfomationFragment.class, ShoppingFragment.class, CouponFragment.class, FittingFragment.class, MapViewFragment.class |

���L��build.gradle�AAndroidManifest.xml�Astyles.xml�AMainActivity�AGcmBroadcastReceiver�AGcmIntentService�̋L�q����L���܂��B

build.gradle

    apply plugin: 'com.android.application'

    android {
        compileSdkVersion 21
        buildToolsVersion "21.1.2"

        defaultConfig {
            applicationId "jp.co.jokerpiece.piece"
            minSdkVersion 14
            targetSdkVersion 21
            versionCode 1
            versionName "1.0.0"
        }
        buildTypes {
            release {
                minifyEnabled false
                proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            }
        }
    }

    repositories {
        mavenCentral()
        maven {
            url 'https://raw.github.com/jokerpiece/android.PieceCore/master/android.PieceCore/app/repository'
        }
    }

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.android.support:appcompat-v7:21.0.0'
        compile 'com.google.android.gms:play-services:+'
        compile 'jp.co.jokerpiece.android.piececore:android.piececore:0.0.16@aar'
    }

AndroidManifest.xml

    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="jp.co.jokerpiece.piece"
        android:versionCode="1"
        android:versionName="1.0" >

        <uses-sdk
            android:minSdkVersion="14"
            android:targetSdkVersion="21" />

        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

        <!-- �v�b�V���ʒm�̐ݒ�(��������) -->
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
        <uses-permission android:name="android.permission.WAKE_LOCK" />
        <uses-permission android:name="android.permission.GET_ACCOUNTS" />
        <uses-permission
            android:name="${applicationId}.permission.RECEIVE" />
        <permission
            android:name="${applicationId}.permission.C2D_MESSAGE"
            android:protectionLevel="signature" />
        <uses-permission
            android:name="${applicationId}.permission.C2D_MESSAGE" />
        <uses-permission android:name="android.permission.WAKE_LOCK" />
        <!-- �v�b�V���ʒm�̐ݒ�(�����܂�) -->

        <!-- ���C�u�����̃A�v���P�[�V�������g�p���邽�� -->
        <!-- "jp.co.jokerpiece.piecebase.util.App"��ݒ肷��K�v������܂� -->
        <application
            android:allowBackup="true"
            android:icon="@drawable/ic_launcher"
            android:name="jp.co.jokerpiece.piecebase.util.App"
            android:theme="@style/AppTheme"
            android:label="@string/app_name"
            android:launchMode="singleTop">
            
             <meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="@string/map_key"/>
            <activity
                android:name=".MainActivity"
                android:screenOrientation="portrait"
                android:launchMode="singleTask">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />
                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>

            <!-- �v�b�V���ʒm�ɕK�v�ȃA�N�e�B�r�e�B(��������) -->
            <meta-data
                android:name="com.google.android.gms.version"
                android:value="@integer/google_play_services_version"/>
            <service android:name=".GcmIntentService" android:enabled="true"/>
            <receiver
                android:name=".GcmBroadcastReceiver"
                android:permission="com.google.android.c2dm.permission.SEND" >
                <intent-filter>
                    <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                    <category android:name="${applicationId}" />
                </intent-filter>
            </receiver>
            <!-- �v�b�V���ʒm�ɕK�v�ȃA�N�e�B�r�e�B(�����܂�) -->

        </application>

    </manifest>

styles.xml

    <resources>

        <!-- Base application theme. -->
        <style name="AppTheme" parent="android:Theme.WithActionBar">
            <!-- Customize your theme here. -->
        </style>

    </resources>


MainActivity.java

    package jp.co.jokerpiece.piece;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.HashMap;

    import jp.co.jokerpiece.piecebase.CouponFragment;
    import jp.co.jokerpiece.piecebase.FlyerFragment;
    import jp.co.jokerpiece.piecebase.InfomationFragment;
    import jp.co.jokerpiece.piecebase.MainBaseActivity;
    import jp.co.jokerpiece.piecebase.R;
    import jp.co.jokerpiece.piecebase.ShoppingFragment;
    import android.os.Bundle;

    public class MainActivity extends MainBaseActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public ArrayList<HashMap<String, Object>> setConfig() {
            return new ArrayList<HashMap<String,Object>>(Arrays.asList(
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.flyer1)); }
                            { put("tabIcon", R.drawable.icon_flyer); }
                            { put("cls", FlyerFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.info1)); }
                            { put("tabIcon", R.drawable.icon_infomation); }
                            { put("cls", InfomationFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.shopping1)); }
                            { put("tabIcon", R.drawable.icon_shopping); }
                            { put("cls", ShoppingFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.coupon1)); }
                            { put("tabIcon", R.drawable.icon_coupon); }
                            { put("cls", CouponFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.fitting1)); }
                            { put("tabIcon", R.drawable.icon_fitting); }
                            { put("cls", FittingFragment.class); }
                    }
            ));
        }

    }

GcmBroadcastReceiver.java

    package jp.co.jokerpiece.piece;
    
    import android.app.Activity;
    import android.content.ComponentName;
    import android.content.Context;
    import android.content.Intent;
    import android.support.v4.content.WakefulBroadcastReceiver;
    
    public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    
        @Override
        public void onReceive(Context context, Intent intent) {
    
            // Explicitly specify that GcmMessageHandler will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmIntentService.class.getName());
    
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
    }

GcmIntentService.java

    package jp.co.jokerpiece.piece;
    
    import android.content.Intent;
    import android.util.Log;
    
    import java.util.HashMap;
    
    import jp.co.jokerpiece.piecebase.data.NewsListData;
    
    public class GcmIntentService extends jp.co.jokerpiece.piecebase.GcmIntentService {
        public static final String TAG = GcmIntentService.class.getSimpleName();
    
        @Override
        public Intent getSelectedIntent(HashMap<String, String> map) {
            Intent intent = null;
            switch (getBlankIfNull(map.get("type"))) {
                case NewsListData.NEWS_DATA_TYPE_INFOMATION + "":
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", getBlankIfNull(map.get("type")));
                    intent.putExtra("newsId", getBlankIfNull(map.get("id")));
                    Log.d(TAG, "type: Infomation, newsId: " + getBlankIfNull(map.get("id")));
                    break;
                case NewsListData.NEWS_DATA_TYPE_FLYER + "":
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", getBlankIfNull(map.get("type")));
                    intent.putExtra("flyer_ID", getBlankIfNull(map.get("id")));
                    Log.d(TAG, "type: Flyer, flyer_ID: " + getBlankIfNull(map.get("id")));
                    break;
                case NewsListData.NEWS_DATA_TYPE_COUPON + "":
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", getBlankIfNull(map.get("type")));
                    intent.putExtra("coupon_code", getBlankIfNull(map.get("id")));
                    Log.d(TAG, "type: Coupon, coupon_code: " + getBlankIfNull(map.get("id")));
                    break;
                default:
                    intent = new Intent(this, MainActivity.class);
                    break;
            }
            return intent;
        }
    }

### �@�\�N���X

Piece�Œ񋟂��Ă���@�\�ƕR�Â��N���X���͉��L�̒ʂ�ł��B

|���O | ���� |
| --- | --- |
|FlyerFragment.class | �t���C���[ |
|InfomationFragment.class | ���m�点�ꗗ |
|ShoppingFragment.class | �V���b�s���O |
|CouponFragment.class | �N�[�|�� |
|FittingFragment.class | �t�B�b�e�B���O |
|MapViewFragment.class | �}�b�v |


### PieceSample�v���W�F�N�g

github(<https://github.com/jokerpiece/android.PieceCore>)����PieceSample�v���W�F�N�g���R�s�[���ĉ������B
PieceSampe�v���W�F�N�g��Piece�A�v���̍ŏ��\���ƂȂ��Ă��܂��B
�����`���[�A�N�e�B�r�e�B�́A�ujp.co.jokerpiece.piece.MainActivity�v�ƂȂ�܂��B
AndroidStudio����PieceSample�v���W�F�N�g���N�����ĉ������B
![Sync Project](./mdImage/syncProj.png)�{�^���������Ńr���h�����s���A![Run Project](./mdImage/runProj.png)�{�^���������ŃA�v�������s���ĉ������B

### �v�b�V���ʒm�̃A�C�R���ݒ�

drawable-ldpi�Adrawable-mdpi�Adrawable-hdpi�Adrawable-xhdpi��status_icon.png�Ƃ������O�̉摜�t�@�C����ݒ肵�ĉ������B
�摜�T�C�Y�͉��LURL���Q�l�ɂ��ĉ������B

* 4.1.4 �X�e�[�^�X�o�[ �A�C�R��<br>(<http://www.techdoctranslator.com/android/practices/ui_guidelines/icon_design/icon_design_status_bar>)
