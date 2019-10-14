package ru.sigmadigital.inchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BaseActivity extends AppCompatActivity {


    private static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getClass().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public void replaceCurrentFragmentWith(FragmentManager manager, int container, Fragment fragment, boolean addToBackStack) {
        final String tag = fragment.getClass().getSimpleName();
        //Log.e("isFragmentInBackstack", tag + "   " + isFragmentInBackstack(manager, tag));
        if (isFragmentInBackstack(manager, tag)) {
            try {
                manager.popBackStackImmediate(tag, 0);

            } catch (IllegalStateException ignored) {
                // There's no way to avoid getting this if saveInstanceState has already been called.
            }
        } else {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(container, fragment, tag);
            if (addToBackStack) transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        }
    }

}
