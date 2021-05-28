package andriod.bignerdranch.photogallery;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PhotoGalleryActivity extends
       SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, PhotoGalleryActivity.class);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return MyPhotoGalleryFragment.newInstance();
    }


    @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode,
                    REQUEST_ERROR,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });

            errorDialog.show();
        }
    }
}