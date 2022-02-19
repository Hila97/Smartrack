package com.smartrack.smartrack;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.smartrack.smartrack.Model.Model;
import com.smartrack.smartrack.Model.Traveler;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.UserProfile;
import io.realm.mongodb.sync.SyncConfiguration;


public class UserDetailsActivity extends AppCompatActivity {
    TextInputLayout InputsName;
    Spinner genderSpinner, birthYearSpinner;
    TextView textViewCategory,errorCategory;
    boolean[] selectedCategory;
    Button saveBtn;
    ArrayList<Integer> categoriesList = new ArrayList<>();
    String travelerName,travelerGender;
    int travelerBirthYear;
    final String[] categoriesArray={
            "amusement park","aquarium","art gallery","bar","casino",
            "museum","night club","park","shopping mall","spa",
            "tourist attraction","zoo", "bowling alley","cafe",
            "church","city hall","library","mosque", "synagogue"
    };
    final String[] categoriesArraySaveInMongoDb={
            "amusement_park","aquarium","art_gallery","bar","casino",
            "museum","night club","park","shopping_mall","spa",
            "tourist_attraction","zoo", "bowling_alley","cafe",
            "church","city_hall","library","mosque", "synagogue"
    };
    User user;
    UserProfile userProfile;
    RealmList<String> travelerFavoriteCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Realm.init(this); // context, usually an Activity or Application
        App app = new App(new AppConfiguration.Builder(getString(R.string.AppId)).build());
        user = app.currentUser();
        userProfile = user.getProfile();

        birthYearSpinner = findViewById(R.id.activity_user_details_spinner_birthyear);
        InputsName =findViewById(R.id.activity_user_details_userName);
        genderSpinner = findViewById(R.id.activity_user_details_spinner_gender);
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);
        errorCategory=findViewById(R.id.activity_user_details_textView_error_category);
        ArrayAdapter<CharSequence> adapterBirthYear = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getYears());
        adapterBirthYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        birthYearSpinner.setAdapter(adapterBirthYear);

        textViewCategory = findViewById(R.id.activity_user_details_category_textView);
        selectedCategory = new boolean[categoriesArray.length];
        saveBtn=findViewById(R.id.activity_user_details_btm);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertUser();
            }
        });
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                travelerGender=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        birthYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Gender",parent.getItemAtPosition(position).toString());
                travelerBirthYear=Integer.valueOf( parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildCategoryList();
            }});

    }

    private void saveTraveler() {
        String partitionValue = userProfile.getEmail();
        travelerName=InputsName.getEditText().getText().toString();
        ObjectId _id=new ObjectId(user.getId());
        Traveler traveler=new Traveler(_id,partitionValue, travelerName,travelerBirthYear,travelerGender,travelerFavoriteCategories);
        Model.instance.addTraveler(traveler, new Model.AddTravelerListener() {
            @Override
            public void onComplete(String isSuccess) {
                if (isSuccess.equals("true")) {
                    Toast.makeText(UserDetailsActivity.this, "saved", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(UserDetailsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UserDetailsActivity.this, "Error! Traveler is not Created", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        SyncConfiguration config = new SyncConfiguration.Builder(user, partitionValue)
//                .allowQueriesOnUiThread(true)
//                .allowWritesOnUiThread(true)
//                .build();
//        Realm backgroundThreadRealm = Realm.getInstance(config);
//        Realm.getInstanceAsync(config, new Realm.Callback() {
//            @Override
//            public void onSuccess(Realm realm) {
//                ObjectId _id=new ObjectId(user.getId());
//                Traveler traveler=new Traveler(_id,partitionValue, travelerName,travelerBirthYear,travelerGender,travelerFavoriteCategories);
//
//                realm.executeTransaction (transactionRealm -> {
//                    transactionRealm.insert(traveler);
//                    Toast.makeText(UserDetailsActivity.this, "saved", Toast.LENGTH_LONG).show();
//                    Intent intent=new Intent(UserDetailsActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                });
//            }
//        });
    }

    public String[] getYears() {
        String[] years = new String[120];
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int s;
        for (int index = 0; index < years.length; ) {
            s = year - index;
            years[index++] = String.valueOf(s);
            Log.d("TAG", years[index - 1]);
        }
        return years;
    }
    private void BuildCategoryList(){
            // Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);

            // set title
            builder.setTitle("Select Categories:");

            // set dialog non cancelable
            builder.setCancelable(false);

            builder.setMultiChoiceItems(categoriesArray, selectedCategory, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    // check condition
                    if (b) {
                        // when checkbox selected
                        // Add position  in lang list
                        categoriesList.add(i);
                        // Sort array list
                        Collections.sort(categoriesList);
                    } else {
                        // when checkbox unselected
                        // Remove position from langList
                        categoriesList.remove(i);
                    }
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    travelerFavoriteCategories=new RealmList<>();
                    errorCategory.setText("");
                    // Initialize string builder
                    StringBuilder stringBuilder = new StringBuilder();
                    // use for loop
                    for (int j = 0; j < categoriesList.size(); j++) {

                        travelerFavoriteCategories.add(categoriesArraySaveInMongoDb[categoriesList.get(j)]);
                        // concat array value
                        stringBuilder.append(categoriesArray[categoriesList.get(j)]);
                        // check condition
                        if (j != categoriesList.size() - 1) {
                            // When j value  not equal
                            // to lang list size - 1
                            // add comma
                            stringBuilder.append(", ");
                        }
                    }
                    // set text on textView
                    textViewCategory.setText(stringBuilder.toString());
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // dismiss dialog
                    dialogInterface.dismiss();
                }
            });
            builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // use for loop
                    for (int j = 0; j < selectedCategory.length; j++) {
                        // remove all selection
                        selectedCategory[j] = false;
                        // clear language list

                    }
                    categoriesList.clear();
                    travelerFavoriteCategories.clear();
                    // clear text view value
                    textViewCategory.setText("");
                }
            });
            // show dialog
            builder.show();
    }
    private  void insertUser(){
        String username= InputsName.getEditText().getText().toString();
        if(username.isEmpty() || username.length()<3)  {
            showError(InputsName,"UserName is not valid");
        }
        else if (textViewCategory.getText()==""){
            errorCategory.setText("Select at least one category ");
        }
        else{
            saveTraveler();
        }
    }


    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }
}