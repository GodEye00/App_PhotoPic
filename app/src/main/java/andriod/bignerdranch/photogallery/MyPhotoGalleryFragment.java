package andriod.bignerdranch.photogallery;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyPhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private boolean UrlToggled = true;
    private GoogleApiClient mClient;

    private List<GalleryItem> mItems = new ArrayList<>();


    public static MyPhotoGalleryFragment newInstance() {
        return new MyPhotoGalleryFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItem();

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
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

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
                menuInflater.inflate(R.menu.fragment_photo_gallery, menu);


            MenuItem MapItem = menu.findItem(R.id.area_locate);
            MapItem.setEnabled(mClient.isConnected());

            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
            final SearchView searchView = (SearchView)
                    searchItem.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d(TAG, "QueryTextSubmit: " + s);

                    QueryPreferences.setStoredQuery(getActivity(), s);

                    searchView.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                    updateItem();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Log.d(TAG, "QueryTextChange: " + s);
                    return false;
                }
            });

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String query = QueryPreferences.getStoredQuery(getActivity());
                    searchView.setQuery(query, false);
                }
            });


            MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
            if (PollService.isServiceAlarmOn(getActivity())) {
                toggleItem.setTitle(R.string.stop_polling);
            } else {
                toggleItem.setTitle(R.string.start_polling);
            }


            MenuItem toggleViewItem = menu.findItem(R.id.view_image);

            if (UrlToggled) {
                toggleViewItem.setTitle(R.string.view_in_web);
            } else {
                toggleViewItem.setTitle(R.string.view_image);
            }




        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItem();
                return true;

            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService
                        .isServiceAlarmOn(getActivity());

                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.area_locate:
                Intent i = LocatrActivity.newIntent(getActivity());
                startActivity(i);
                return true;

            case R.id.view_image:
                UrlToggled = !UrlToggled;
                getActivity().invalidateOptionsMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



        private void updateItem() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
        }


        private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter((mItems)));
        }

    }




    private class PhotoHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        private ImageView mItemImageView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.
                    findViewById(R.id.item_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            Picasso.get()
                    .load(galleryItem.getUrl())
                    .placeholder(R.drawable.bill_up_close)
                    .into(mItemImageView);

        }

        public void bindGalleryUriItem(GalleryItem galleryItem) {
            mGalleryItem = galleryItem;
        }

        @Override
        public void onClick(View view) {
            Intent i;
            if (UrlToggled) {
                i = CardViewActivity
                        .newIntent(getActivity(), mGalleryItem.getOwner());
            } else {
                i = PhotoPageActivity
                        .newIntent(getActivity(), mGalleryItem.getPhotoPageUri());
            }

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

            View view = layoutInflater.inflate(R.layout.list_item_gallery, parent, false);

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




    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }


        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }

        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
    

}