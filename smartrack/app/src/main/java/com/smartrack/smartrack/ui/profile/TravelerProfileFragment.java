package com.smartrack.smartrack.ui.profile;

import static androidx.core.content.ContextCompat.getSystemService;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.smartrack.smartrack.Model.Model;
import com.smartrack.smartrack.Model.PlacePlanning;
import com.smartrack.smartrack.Model.Traveler;
import com.smartrack.smartrack.R;
import com.smartrack.smartrack.ui.MyTrip.ListDayInTripFragment;


import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;


public class TravelerProfileFragment extends Fragment {
    TextView name, mail,categories;
    Button editBtn;
    ListView listCategory;
    MyAdapter adapter;
    String [] arrCategory;
    TextView category,logout;
    final String[] categoriesArray={
            "amusement park","aquarium","art gallery","bar","casino",
            "museum","night club","park","shopping mall","spa",
            "tourist attraction","zoo", "bowling alley","cafe",
            "church","city hall","library","mosque", "synagogue"
    };
    final String[] categoriesArraySaveInMongoDb={
            "amusement_park","aquarium","art_gallery","bar","casino",
            "museum","night_club","park","shopping_mall","spa",
            "tourist_attraction","zoo", "bowling_alley","cafe",
            "church","city_hall","library","mosque", "synagogue"
    };
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traveler_profile, container, false);
//        categories = view.findViewById(R.id.traveler_profile_categories);
        editBtn = view.findViewById(R.id.traveler_profile_edit_btn);
        mail = view.findViewById(R.id.traveler_profile_email);
        name = view.findViewById(R.id.traveler_profile_name);
        listCategory=view.findViewById(R.id.traveler_profile_list_category);
        Realm.init(getContext()); // context, usually an Activity or Application
        App app = new App(new AppConfiguration.Builder(getString(R.string.AppId)).build());
        User user = app.currentUser();
        logout=view.findViewById(R.id.traveler_profile_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(TravelerProfileFragmentDirections.actionNavProfileToLogoutActivity());
            }
        });
       Model.instance.getTravelerByEmailInDB(user.getProfile().getEmail(), getContext(), new Model.GetTravelerByEmailListener() {
            @Override
            public void onComplete(Traveler traveler, List<String> favoriteCategories) {
                name.setText("Hello "+traveler.getTravelerName());
                mail.setText(traveler.getTravelerMail());
                arrCategory= new String[favoriteCategories.size()];
                favoriteCategories.toArray(arrCategory);
                adapter=new MyAdapter();
                listCategory.setAdapter(adapter);
//                String c = favoriteCategories.stream()
//                                    .map(n -> String.valueOf(n))
//                                    .collect(Collectors.joining("\n", "", ""));
//                c=c.replace("_"," ");
//                categories.setText(c);
                editBtn= view.findViewById(R.id.traveler_profile_edit_btn);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isNetworkConnected()) {
                            String[] arrayCategories = new String[favoriteCategories.size()];
                            favoriteCategories.toArray(arrayCategories);
                            TravelerProfileFragmentDirections.ActionNavProfileToTravelerEditProfileFragment action = TravelerProfileFragmentDirections.actionNavProfileToTravelerEditProfileFragment(traveler, arrayCategories);
                            Navigation.findNavController(view).navigate(action);
                        }
                        else{
                            Toast.makeText(getContext(), "Error! Connect to Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (arrCategory== null) {
                return 0;
            } else {
                return arrCategory.length;
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
                view = inflater.inflate(R.layout.row_favorite_category, null);
            } else {

            }
            category = view.findViewById(R.id.row_favorite_category_text_view);
            String c = arrCategory[i];
            String a=c.replace('_',' ');
            category.setText(a);
            return view;
        }

    }

}