package ru.sigmadigital.inchat.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class SubsFragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentsList = new ArrayList<>();
    List<String>titleList = new ArrayList<>();

    public SubsFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return  titleList.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        fragmentsList.add(fragment);
        titleList.add(title);
    }

}
