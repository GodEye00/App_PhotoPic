package andriod.bignerdranch.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class PhotoPageActivity extends SingleFragmentActivity{

    public static Intent newIntent(Context context, Uri photoPgaeUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPgaeUri);

        return i;

    }

    @Override
    protected Fragment createFragment() {
        return
                PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        Fragment fragment =  getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        PhotoPageFragment photoPageFragment = (PhotoPageFragment) fragment;
        if (!photoPageFragment.webViewGoBack()) {
            super.onBackPressed();
        }
    }
}
