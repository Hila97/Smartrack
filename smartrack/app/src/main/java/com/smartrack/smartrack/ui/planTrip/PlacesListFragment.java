package com.smartrack.smartrack.ui.planTrip;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartrack.smartrack.HTTP.HttpCall;
import com.smartrack.smartrack.HTTP.HttpRequest;
import com.smartrack.smartrack.LoginActivity;
import com.smartrack.smartrack.Model.Model;
import com.smartrack.smartrack.Model.PlaceDetails;
import com.smartrack.smartrack.Model.PlacePlanning;
import com.smartrack.smartrack.Model.Trip;
import com.smartrack.smartrack.R;
import com.squareup.picasso.Picasso;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlacesListFragment extends Fragment {
    ListView listViewPlaces;
    MyAdapter adapter;
    TextView name;
    ImageView imagev;
    RatingBar rating;
    PlacePlanning[] arrayPlaces;
    ArrayList<PlacePlanning> chosenPlaces;
    Button button;
    TextView planBtn;
    Integer tripDays,placesNum=0;
    TextView amountUserPlace,location;
    ProgressDialog myLoadingDialog;
    String tripName,tripLocation;
    int[] colorArray;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_list, container, false);
        listViewPlaces =view.findViewById(R.id.fragment_places_list_listview);

        arrayPlaces =PlacesListFragmentArgs.fromBundle(getArguments()).getArrayPlaces();
        tripDays=PlacesListFragmentArgs.fromBundle(getArguments()).getTripDays();
        String destination = PlacesListFragmentArgs.fromBundle(getArguments()).getLocationTrip();
        adapter=new MyAdapter();
        listViewPlaces.setAdapter(adapter);
        listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                PlacesListFragmentDirections.ActionPlacesListFragmentToPlaceDetailsFragment action=PlacesListFragmentDirections.actionPlacesListFragmentToPlaceDetailsFragment(arrayPlaces[i]);
                Navigation.findNavController(view).navigate( action);
            }
        });
        Realm.init(getContext()); // context, usually an Activity or Application
        App app = new App(new AppConfiguration.Builder(getString(R.string.AppId)).build());
        user = app.currentUser();
        tripName= PlacesListFragmentArgs.fromBundle(getArguments()).getTripName();
        tripLocation = PlacesListFragmentArgs.fromBundle(getArguments()).getLocationTrip();
        chosenNumber(arrayPlaces);
        amountUserPlace=view.findViewById(R.id.fragment_places_list_count_place_user);
        amountUserPlace.setText(String.valueOf(placesNum));
        location = view.findViewById(R.id.fragment_places_list_location);
        location.setText(destination);
        planBtn=view.findViewById(R.id.fragment_places_list_planBtn);
        planBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placesNum==0)
                    Toast.makeText(getContext(),"no places selected ",Toast.LENGTH_SHORT).show();
                else if (placesNum<tripDays)
                    Toast.makeText(getContext(),"Too few places selected ",Toast.LENGTH_SHORT).show();
                else if (placesNum > tripDays*3)
                    Toast.makeText(getContext(),"Too many places selected ",Toast.LENGTH_SHORT).show();
                else
                    CreateListForPlanning();
            }
        });
        myLoadingDialog=new ProgressDialog(getContext());
        colorArray= getContext().getResources().getIntArray(R.array.array_name);
        return view;
    }

    private void chosenNumber(PlacePlanning[] arrayPlaces) {
        placesNum=0;
        for(int i=0; i<arrayPlaces.length;++i)
            if(arrayPlaces[i].getStatus()==true)
                placesNum++;
    }
    int count;
    int j;
    private void CreateListForPlanning() {
        myLoadingDialog.setTitle("Planing Trip");
        myLoadingDialog.setMessage("Please Wait, planing your trip");
        myLoadingDialog.setCanceledOnTouchOutside(false);
        myLoadingDialog.show();
        chosenPlaces=new ArrayList<PlacePlanning>();
        for(int i=0;i<arrayPlaces.length;i++)
        {
            if(arrayPlaces[i].getStatus()==true) {
                PlacePlanning place = getPlaceDetailsById(arrayPlaces[i].getPlaceID());
                chosenPlaces.add(place);
            }
        }


        Model.instance.planTrip(chosenPlaces, tripDays, new Model.PlanTripListener() {
            @Override
            public void onComplete(ArrayList<PlacePlanning> chosenPlaces1) {

                Model.instance.addTrip(tripName, tripLocation, user.getProfile().getEmail(), tripDays,getContext(), new Model.AddTripListener() {
                    @Override
                    public void onComplete(String  tripId) {
                        addPlaces(chosenPlaces1,0,tripId);
                    }
                });
            }
        });
    }

    JSONObject jsonData=null;
    String jsonStringPlace;
    private  PlacePlanning getPlaceDetailsById(String placeId) {
        PlacePlanning p=new PlacePlanning();
        Thread placeThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url="https://maps.googleapis.com/maps/api/place/details/json?place_id=";
                    url+=placeId+"&key="+getString(R.string.places_api_key);
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .method("GET", null)
                            .build();
                    Response response = client.newCall(request).execute();
                    jsonStringPlace= response.body().string();
                    jsonData = new JSONObject(jsonStringPlace);
                }catch (IOException | JSONException f){
                }
            }
        });
        placeThread.start();
        try {
            placeThread.join();
            if(jsonData!=null)
            {
                JSONObject result=(JSONObject)jsonData.get("result");
                p=PlacesList.JsonToPlace(result);
            }
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return p;
    }
    public void addPlaces(ArrayList<PlacePlanning> chosenPlaces,int index,String tripId){
        if(index==chosenPlaces.size()) {
            PlacePlanning[] arrayPlaces = new PlacePlanning[chosenPlaces.size()];
            chosenPlaces.toArray(arrayPlaces);
            myLoadingDialog.dismiss();
            getParentFragmentManager().popBackStack();
            PlacesListFragmentDirections.ActionPlacesListFragmentToListDayInTripFragment action=PlacesListFragmentDirections.actionPlacesListFragmentToListDayInTripFragment(tripName,tripLocation,arrayPlaces ,tripDays);
            Navigation.findNavController(getView()).navigate( action);
        }
        else {
            Model.instance.addPlace(chosenPlaces.get(index), tripLocation, user.getProfile().getEmail(), tripId, getContext(),new Model.AddPlaceListener() {
                @Override
                public void onComplete(boolean isSuccess) {
                    addPlaces(chosenPlaces, index + 1, tripId);
                }

            });
        }
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
                view = inflater.inflate(R.layout.place_list_row, null);
            } else {

            }
            PlacePlanning place=arrayPlaces[i];

            name = view.findViewById(R.id.place_list_row_name_trip);
            imagev = view.findViewById(R.id.place_list_row_image);
            button=view.findViewById(R.id.place_list_row_button);
            name.setText(place.getPlaceName());
            imagev.setTag(place.getPlaceImgUrl());
            rating=view.findViewById(R.id.place_list_row_rating);
            rating.setRating(place.getPlaceRating());
            Drawable drawable = rating.getProgressDrawable();
            drawable.setColorFilter(Color.parseColor("#FDC313"), PorterDuff.Mode.SRC_ATOP);
            if (place.getPlaceImgUrl() != null && place.getPlaceImgUrl() != "") {
                if (place.getPlaceImgUrl() == imagev.getTag()) {
                    Picasso.get().load(place.getPlaceImgUrl()).into(imagev);
                }
            } else {


            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(place.getStatus()==false){
                        place.setStatus(true);
                        placesNum++;
                        arrayPlaces[i].setStatus(true);
                        amountUserPlace.setText(String.valueOf(placesNum));

                    }
                    else{
                        place.setStatus(false);
                        placesNum--;
                        arrayPlaces[i].setStatus(false);
                        amountUserPlace.setText(String.valueOf(placesNum));
                    }
                   notifyDataSetChanged();
                }
            });

            button.setTag(place.getStatus());
            if(place.getStatus()==false && String.valueOf(button.getTag())=="false"){
                button.setText("Add");
                button.setBackgroundColor(colorArray[1]);
            }
            else{
                button.setText("Remove");
                button.setBackgroundColor(colorArray[0]);
            }
            return view;
        }

    }

}