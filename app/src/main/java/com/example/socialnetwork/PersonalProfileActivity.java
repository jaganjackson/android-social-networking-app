package com.example.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfImage;
    private Button SendFriendReqbtn, DeclineFriendReqbtn;
    private DatabaseReference FriendRequestRef,UsersRef,FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserID, receiverUserID, CURRENT_STATE;
    private String saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);

        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();
        receiverUserID=getIntent().getExtras().get("visit_user_id").toString();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef= FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef= FirebaseDatabase.getInstance().getReference().child("Friends");

        userName=(TextView)findViewById(R.id.person_username);
        userProfName=(TextView)findViewById(R.id.person_profile_full_name);
        userStatus=(TextView)findViewById(R.id.person_profile_status);
        userCountry=(TextView)findViewById(R.id.person_country);
        userGender=(TextView)findViewById(R.id.person_gender);
        userRelation=(TextView)findViewById(R.id.person_relationship_status);
        userDOB=(TextView)findViewById(R.id.person_dob);
        userProfImage=(CircleImageView)findViewById(R.id.person_profile_pic);

        SendFriendReqbtn=(Button)findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendReqbtn=(Button)findViewById(R.id.person_decline_friend_request_btn);
        CURRENT_STATE="not_friends";

        UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
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

                    Picasso.with(PersonalProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText("@"+myUsername);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userCountry.setText("Country: "+myCountry);
                    userGender.setText("Gender: "+myGender);
                    userRelation.setText("Relationship Status: "+myRelationshipStatus);
                    userDOB.setText("DOB: "+myDOB);

                    MaintenanceButtons();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendReqbtn.setVisibility(View.INVISIBLE);
        DeclineFriendReqbtn.setEnabled(false);

        if(senderUserID.equals(receiverUserID)){
            SendFriendReqbtn.setVisibility(View.INVISIBLE);
        }else{
            SendFriendReqbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendFriendReqbtn.setEnabled(false);
                    if(CURRENT_STATE.equals("not_friends")){
                        SendFriendReqtoPerson();
                    }else if(CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }else if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }else if(CURRENT_STATE.equals("request_received")){
                        UnFriend();
                    }
                }
            });
        }
    }

    private void UnFriend() {
        FriendsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqbtn.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                SendFriendReqbtn.setText("Send Friend Request");
                                                DeclineFriendReqbtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqbtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        FriendsRef.child(senderUserID).child(receiverUserID).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserID).child(senderUserID).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        SendFriendReqbtn.setEnabled(true);
                                                                                        CURRENT_STATE="friends";
                                                                                        SendFriendReqbtn.setText("Unfriend");
                                                                                        DeclineFriendReqbtn.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendReqbtn.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqbtn.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                SendFriendReqbtn.setText("Send Friend Request");
                                                DeclineFriendReqbtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqbtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintenanceButtons() {
        FriendRequestRef.child(senderUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserID)){
                            String request_type=dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                CURRENT_STATE="request_sent";
                                SendFriendReqbtn.setText("Cancel Friend Request");
                                DeclineFriendReqbtn.setVisibility(View.INVISIBLE);
                                DeclineFriendReqbtn.setEnabled(false);
                            }else if(request_type.equals("received")){
                                CURRENT_STATE="request_received";
                                SendFriendReqbtn.setText("Accept Friend Request");
                                DeclineFriendReqbtn.setVisibility(View.VISIBLE);
                                DeclineFriendReqbtn.setEnabled(true);

                                DeclineFriendReqbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else{
                            FriendsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserID)){
                                                CURRENT_STATE="friends";
                                                SendFriendReqbtn.setText("Unfriend");
                                                DeclineFriendReqbtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqbtn.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendReqtoPerson() {
        FriendRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqbtn.setEnabled(true);
                                                CURRENT_STATE="request_sent";
                                                SendFriendReqbtn.setText("Cancel Friend Request");
                                                DeclineFriendReqbtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqbtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
