package andriod.bignerdranch.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

public class CardViewActivity extends SingleFragmentActivity{

    private static final String EXTRA_OWNER = "photoGalleryOwner";

    public static Intent newIntent(Context context, String owner) {
        Intent i = new Intent(context, CardViewActivity.class);
        i.putExtra(EXTRA_OWNER, owner);
        return i;

    }

    @Override
    protected Fragment createFragment() {
        return CardViewFragment.newInstance(getIntent().getDataString());
    }

}
