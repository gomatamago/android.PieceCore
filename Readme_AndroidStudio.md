# [AndroidStudio]

### Piece�v���W�F�N�g�̃C���|�[�g
github����Piece�v���W�F�N�g���R�s�[���ĉ������B
Piece�v���W�F�N�g�̓I�g�i�S�R���A�v���̍ŏ��\���ƂȂ��Ă��܂��B
�����`���[�A�N�e�B�r�e�B�́A�ujp.co.jokerpiece.piece.MainActivity�v�ƂȂ�܂��B
AndroidStudio����Piece�v���W�F�N�g���N�����ĉ������B
![Sync Project](./syncProj.png)�{�^���������Ńr���h�����s���A![Run Project](./runProj.png)�{�^���������ŃA�v�������s���ĉ������B

## google-play-services_lib���C�u�����̃C���|�[�g
"Piece/app/build.gradle"����dependencies�Łucompile 'com.google.android.gms:play-services:+'�v�����s���邱�ƂŃ��C�u������ǂݍ���ł��܂��B

## android.PieceCore���C�u�����̃C���|�[�g
"Piece/app/build.gradle"����maven��url���w�肵�Adependencies�Łucompile 'jp.co.jokerpiece.android.piececore:android.piececore:0.0.+@aar'�v�����s���邱�ƂŃ��C�u������ǂݍ���ł��܂��B

### ���p���@

MainActivity�́A���C�u�����ɂ���ujp.co.jokerpiece.piecebase.MainBaseActivity�v��extends����K�v������܂��B
�ǉ�����^�u�́AsetConfig()���\�b�h��Override���邱�ƂŁA�������邱�Ƃ��ł��܂��B

setConfig()���\�b�h�ł́AArrayList<HashMap<String, Object>>��߂�l�Ƃ��ĕԂ��K�v������܂��B
���X�g�̐����^�u�̐��ƂȂ�A�^�u�̒��ɐݒ肷��ݒ���e��HashMap�Őݒ肷�邱�Ƃ��ł��܂��B
�ȉ���HashMap�Őݒ�ł���L�[�ƃo�����[�������܂��B

HashMap<String, Object>

| �L�[ | ���� | �o�����[ |
| --- | --- | --- |
| tabTitle | �^�u�ɕ\������^�C�g�� | getString(R.string.flyer1), getString(R.string.info1), getString(R.string.shopping1), getString(R.string.coupon1) |
| tabIcon | �^�u�ɐݒ肷��摜 | R.drawable.icon_flyer, R.drawable.icon_infomation, R.drawable.icon_shopping, R.drawable.icon_coupon |
| cls | �^�u�ɐݒ肷���� | FlyerFragment.class, InfomationFragment.class, ShoppingFragment.class, CouponFragment.class |

���L��MainActivity�̋L�q����L���܂��B

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
                    }
            ));
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
