package com.example.hives;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.view.MenuItem;

public class joinedHivesActivity extends AppCompatActivity {
    List<Object> hivelist = new ArrayList<Object>();
    RecyclerView recyclerView;
    String currenthive;
    FirebaseUser currentuser;
    private BottomNavigationView bottomnav;
    DatabaseReference hivesref;
    View nothing;


    private TextView explore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_hives);
        nothing=findViewById(R.id.view2);
        nothing.setVisibility(View.INVISIBLE);
        bottomnav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.nav_jhives);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });
        explore = findViewById(R.id.textView11);
        explore.setVisibility(View.INVISIBLE);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //forgot password page
                startActivity(new Intent(joinedHivesActivity.this, searchActivity.class));
            }
        });
        recyclerView = findViewById(R.id.rec);
        // recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentuser.getUid()).child("Joined Hives");//NOT USED
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // NOT USED EXCEPT TO CALL FILL RECYCLER
                hivelist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    hivelist.add(snapshot.getValue());
                    System.out.println(dataSnapshot.getChildrenCount());}
                if (!hivelist.isEmpty()) ;
                fillRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fillRecycler() {

            hivesref = FirebaseDatabase.getInstance().getReference().child("HIVES");
            FirebaseRecyclerAdapter<HivesRetrieve, HiveViewHolder> firebaseRecyclerAdapter = null;
            // for (int i = 0; i<hives.length; i++) {
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HivesRetrieve, HiveViewHolder>(
                    HivesRetrieve.class,
                    R.layout.item_row,
                    HiveViewHolder.class,
                    hivesref.orderByChild(currentuser.getUid()).startAt(currentuser.getUid()).endAt(currentuser.getUid())) {
                @Override
                protected void populateViewHolder(HiveViewHolder hiveViewHolder, HivesRetrieve hivesRetrieve, final int k) {
                    hiveViewHolder.setHiveName(hivesRetrieve.getTitle());
                    hiveViewHolder.setHiveImage(getApplicationContext(), hivesRetrieve.getImage());
                    hiveViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String hivename = getRef(k).getKey();
                            Intent profielintent = new Intent(joinedHivesActivity.this, ViewHive.class);
                            profielintent.putExtra("HiveName", hivename);
                            startActivity(profielintent);
                        }
                    });
                }

            };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            if (firebaseRecyclerAdapter==null){
                nothing.setVisibility(View.VISIBLE);
            }




    }

    public static class HiveViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public HiveViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setHiveName(String hiveName) {
            TextView hivesname = mView.findViewById(R.id.post_title);
            hivesname.setText(hiveName);
            System.out.println("here3");
        }

        public void setHiveImage(Context cnx, String hiveImage) {

            ImageView hivesimage = mView.findViewById(R.id.post_image);
            Picasso.get().load(hiveImage).into(hivesimage);
        }
    }
    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_search:
                Intent intentsearch=new Intent(joinedHivesActivity.this, searchActivity.class);
                startActivity(intentsearch);
                break;

            case R.id.nav_home:

                Intent intenthome=new Intent(joinedHivesActivity.this, MainActivity.class);
                startActivity(intenthome);
                break;
            case R.id.nav_notf:

                Intent intentnot=new Intent(joinedHivesActivity.this, NotificationActivity.class);
                startActivity(intentnot);
                break;


            case R.id.nav_profile:

                Intent intentprofile=new Intent(joinedHivesActivity.this, ProfileActivity.class);
                startActivity(intentprofile);
                break;}}
}