package andriod.bignerdranch.photogallery;

import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LocatrActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, LocatrActivity.class);

        return i;

    }

    @Override
    protected Fragment createFragment() {
        return LocatrMapFragment.newInstance();
    }



}