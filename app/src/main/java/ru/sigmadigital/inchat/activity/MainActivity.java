package ru.sigmadigital.inchat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.newW.chats.ChatsNavigationFragment;
import ru.sigmadigital.inchat.fragments.newW.news.NewsNavigationFragment;
import ru.sigmadigital.inchat.fragments.newW.orders.OrdersNavigationFragment;
import ru.sigmadigital.inchat.fragments.newW.menu.MenuNavigationFragment;
import ru.sigmadigital.inchat.models.realm.ChatMessageRealm;

public class MainActivity extends BaseActivity {

    private AHBottomNavigation bottomNavBar;

    private Handler mBackPressedHandler = new Handler();
    private boolean doubleBackToExitPressedOnce;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bottom navigation
        int position = 0;
        initBottomBar();
        if (savedInstanceState != null && savedInstanceState.containsKey("stateNavBar")) {
            position = savedInstanceState.getInt("stateNavBar");
            /*bottomNavBar.setCurrentItem(position);*/
        } else {
            /*bottomNavBar.setCurrentItem(position);
            setCurrentFragment(position);*/
        }
        bottomNavBar.setCurrentItem(position);
        setCurrentFragment(position);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    //bottom navigation
    void initBottomBar() {
        bottomNavBar = findViewById(R.id.bb_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getResources().getString(R.string.home),
                R.drawable.ic_home);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getResources().getString(R.string.chats),
                R.drawable.ic_chats);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getResources().getString(R.string.news),
                R.drawable.ic_news);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getResources().getString(R.string.menu),
                R.drawable.ic_menu);

        bottomNavBar.addItem(item1);
        bottomNavBar.addItem(item2);
        bottomNavBar.addItem(item3);
        bottomNavBar.addItem(item4);



        bottomNavBar.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavBar.setTitleTextSizeInSp(13, 11);

        bottomNavBar.setDefaultBackgroundColor(getResources().getColor(R.color.white));
        bottomNavBar.setAccentColor(getResources().getColor(R.color.blue));
        bottomNavBar.setInactiveColor(getResources().getColor(R.color.navBarInactive));

        bottomNavBar.setBehaviorTranslationEnabled(true);
        bottomNavBar.setTranslucentNavigationEnabled(true);
        //bottomNavBar.

        bottomNavBar.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected) {
                    setCurrentFragment(position);
                }
                return true;
            }
        });
    }

    private void setCurrentFragment(int position) {
        switch (position) {
            case 0: {
                showNavigatorFragment(OrdersNavigationFragment.newInstance());
                break;
            }
            case 1: {
                showNavigatorFragment(ChatsNavigationFragment.newInstance());
                break;
            }
            case 2: {
                showNavigatorFragment(NewsNavigationFragment.newInstance());
                break;
            }
            case 3: {
                showNavigatorFragment(MenuNavigationFragment.newInstance());
                break;
            }
        }
    }

    private void showNavigatorFragment(Fragment fragment) {
        replaceCurrentFragmentWith(getSupportFragmentManager(),
                R.id.fragment_container,
                fragment,
                false);
    }


    //realm clear
    private void clearRealm() {
        App.getRealm().beginTransaction();
        App.getRealm().delete(ChatMessageRealm.class);
        App.getRealm().commitTransaction();
    }


    //back press file manager
    OnBackPressedClick onBackPressedClick;

    public interface OnBackPressedClick {
        void OnBackPressClick();
    }

    public void setOnBackPressedClick(OnBackPressedClick onBackPressedClick) {
        this.onBackPressedClick = onBackPressedClick;
    }

    public void deleteOnBackPressedClick() {
        this.onBackPressedClick = null;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedClick != null) {
            onBackPressedClick.OnBackPressClick();
        } else {
            if (getSupportFragmentManager()
                    .getBackStackEntryCount() > 0) {
                this.doubleBackToExitPressedOnce = true;
            }
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                doubleBackToExitPressedOnce = false;
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "нажмите еще раз чтобы выйти", Toast.LENGTH_SHORT).show();
            mBackPressedHandler.postDelayed(mRunnable, 2000);
        }
    }

    public void hideBottomNavigationBar(){
        bottomNavBar.setVisibility(View.GONE);
    }

    public void showBottomNavigationBar(){
        bottomNavBar.setVisibility(View.VISIBLE);
    }
}
