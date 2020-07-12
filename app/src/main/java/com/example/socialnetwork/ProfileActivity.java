package com.example.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfImage;
    private DatabaseReference profileuserRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        profileuserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        userName=(TextView)findViewById(R.id.my_username);
        userProfName=(TextView)findViewById(R.id.my_profile_full_name);
        userStatus=(TextView)findViewById(R.id.my_profile_status);
        userCountry=(TextView)findViewById(R.id.my_country);
        userGender=(TextView)findViewById(R.id.my_gender);
        userRelation=(TextView)findViewById(R.id.my_relationship_status);
        userDOB=(TextView)findViewById(R.id.my_dob);
        userProfImage=(CircleImageView)findViewById(R.id.my_profile_pic);

        profileuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage=dataSnapshot.child("profileimage").getValue().toString();
                    String myUsername=dataSnapshot.child("username").getValue().toString();
                    String myProfileName=dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus=dataSnapshot.child("status").getValue().toString();
                    String myDOB=dataSnapshot.child("dob").getValue().toString();
                    String myGender=dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus=dataSnapshot.child("relationshipstatus").getValue().toString();
                    String myCountry=dataSnapshot.child("country").getValue().toString();

                    Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText("@"+myUsername);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userCountry.setText("Country: "+myCountry);
                    userGender.setText("Gender: "+myGender);
                    userRelation.setText("Relationship Status: "+myRelationshipStatus);
                    userDOB.setText("DOB: "+myDOB);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
