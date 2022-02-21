package com.smartrack.smartrack.ui.futureTrip;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.smartrack.smartrack.Model.PlacePlanning;
import com.smartrack.smartrack.R;
import com.smartrack.smartrack.ui.planTrip.PlacesListFragment;
import com.smartrack.smartrack.ui.planTrip.PlacesListFragmentArgs;
import com.smartrack.smartrack.ui.planTrip.PlacesListFragmentDirections;
import com.squareup.picasso.Picasso;


public class ListTripInDayFragment extends Fragment {
    TextView name,location;
    ImageView imagev;
    RatingBar rating;
    PlacePlanning[] arrayPlaces;
    ListView listViewPlaces;
    MyAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_trip_in_day, container, false);
        listViewPlaces =view.findViewById(R.id.fragment_list_trip_in_day_list_view);
        arrayPlaces =  ListTripInDayFragmentArgs.fromBundle(getArguments()).getArrTrip();
        adapter=new MyAdapter();
        listViewPlaces.setAdapter(adapter);
        listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Log.d("TAG","post id"+i);
//                PlacesListFragmentDirections.ActionPlacesListFragmentToPlaceDetailsFragment action=PlacesListFragmentDirections.actionPlacesListFragmentToPlaceDetailsFragment(arrayPlaces[i]);
//                Navigation.findNavController(view).navigate( action);
            }
        });
        return view;
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (arrayPlaces== null) {
                return 0;
            } else {
                return arrayPlaces.length;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.trip_in_day_row, null);
            } else {

            }
            PlacePlanning place=arrayPlaces[i];

            name = view.findViewById(R.id.trip_in_day_row_name_trip);
            imagev = view.findViewById(R.id.trip_in_day_row_image);
            name.setText(place.getPlaceName());
            imagev.setTag(place.getPlaceImgUrl());
            rating=view.findViewById(R.id.trip_in_day_row_rating);
            rating.setRating(place.getPlaceRating());
            Drawable drawable = rating.getProgressDrawable();
            drawable.setColorFilter(Color.parseColor("#FDC313"), PorterDuff.Mode.SRC_ATOP);
            if (place.getPlaceImgUrl() != null && place.getPlaceImgUrl() != "") {
                if (place.getPlaceImgUrl() == imagev.getTag()) {
                    Picasso.get().load(place.getPlaceImgUrl()).into(imagev);
                }
            } else {


            }

            return view;
        }

    }
}