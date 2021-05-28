package andriod.bignerdranch.photogallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardViewFragment extends VisibleFragment {

    private static final String TAG = "PhotoGalleryFragment";
    private static final String ARG_OWNER = "photo_page_owner";

    private GoogleApiClient mClient;

    private RecyclerView mPhotoRecyclerView;

    private List<GalleryItem> mItems = new ArrayList<>();

    public static CardViewFragment newInstance(String owner) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_OWNER, owner);

        CardViewFragment fragment = new CardViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        String mOnwer = (String) getArguments().getSerializable(ARG_OWNER);


          new FetchReverseItemsTask(mOnwer).execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallary, container,
                false);


        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        mPhotoRecyclerView = (RecyclerView)
                v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return v;
    }













    @Override
    public void onStart() {
        super.onStart();

        getActivity().invalidateOptionsMenu();
        mClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.cardview_menu, menu);


        MenuItem MapItem = menu.findItem(R.id.area_locate);
        MapItem.setEnabled(mClient.isConnected());


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.area_locate:
                Intent i = LocatrActivity.newIntent(getActivity());
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }






    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter((mItems)));
        }

    }


    private class PhotoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView mItemImageView;
        private TextView mCaptionView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.
                    findViewById(R.id.image);
            itemView.setOnClickListener(this);

            mCaptionView = (TextView) itemView.
                    findViewById(R.id.caption);
            itemView.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            Picasso.get()
                    .load(galleryItem.getUrl())
                    .placeholder(R.drawable.bill_up_close)
                    .into(mItemImageView);
            mCaptionView.setText(galleryItem.toString());

        }

        public void bindGalleryUriItem(GalleryItem galleryItem) {
            mGalleryItem = galleryItem;
        }

        @Override
        public void onClick(View view) {
                            Intent i = PhotoPageActivity
                        .newIntent(getActivity(), mGalleryItem.getPhotoPageUri());
                startActivity(i);
        }

    }



    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItemsList;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItemsList = galleryItems;
        }


        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            View view = layoutInflater.inflate(R.layout.fragment_card_view, parent, false);

            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItemsList.get(position);
            holder.bindGalleryItem(galleryItem);
            holder.bindGalleryUriItem(galleryItem);

        }

        @Override
        public int getItemCount() {
            return mGalleryItemsList.size();
        }
    }




    public class FetchReverseItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private String mString;
        public FetchReverseItemsTask(String imageurl) {mString = imageurl;}


        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().searchPhotos(mString);
        }


        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }

    }


}






