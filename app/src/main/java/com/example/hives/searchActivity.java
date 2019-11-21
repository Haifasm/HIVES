package com.example.hives;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

       import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Context;
        import android.os.Bundle;
        import android.text.Html;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.inputmethod.EditorInfo;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;
        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.squareup.picasso.Picasso;

        import javax.xml.transform.Result;

        import de.hdodenhof.circleimageview.CircleImageView;

public class searchActivity extends AppCompatActivity  {


    private DatabaseReference allHivesdatabaseRef;
    private RecyclerView result;
    private ImageButton searchbtn ;
    private EditText searchInpuText;
    private Button hivecreate;
    private BottomNavigationView bottomnav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_search);
        bottomnav= (BottomNavigationView) findViewById(R.id.bottom_navigationsearch);

        bottomnav.setSelectedItemId(R.id.nav_search);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });


        hivecreate=(Button)findViewById(R.id.createHive);

        hivecreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hivecreation=new Intent(searchActivity.this,CreateHiveActivity.class);
                startActivity(hivecreation);
            }
        });

        allHivesdatabaseRef= FirebaseDatabase.getInstance().getReference().child("HIVES");
        // search btn
        searchbtn=(ImageButton)findViewById(R.id.searchbutton);


        // SearchInput
        searchInpuText= (EditText)findViewById(R.id.SearchInput);
        searchInpuText.setOnEditorActionListener(editorListener);


        // RecyclerView
        result= (RecyclerView)findViewById(R.id.Searchresult);
        result.setHasFixedSize(true);

        result.setLayoutManager(new LinearLayoutManager(this));
        Browse();

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SearchBoxInput=searchInpuText.getText().toString();
                SearchMethod(SearchBoxInput);
            }
        });
    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            String SearchBoxInput=searchInpuText.getText().toString();
            SearchMethod(SearchBoxInput);

            return true;
        }
    };

    public void SearchMethod(String SearchBoxInput) {


        Toast.makeText(this,"جاري البحث..",Toast.LENGTH_LONG).show();
        Query searchHivesAndInfiQuere = allHivesdatabaseRef.orderByChild("title").startAt(SearchBoxInput).endAt(SearchBoxInput +"\uf8ff");
        FirebaseRecyclerAdapter<SearchResult,SearchViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<SearchResult,SearchViweHolder>
                (
                        SearchResult.class,
                        R.layout.item_row,
                        SearchViweHolder.class,
                        searchHivesAndInfiQuere
                ){
            @Override
            protected void populateViewHolder(SearchViweHolder searchViweHolder, SearchResult module,final int i)
            {
                searchViweHolder.setHivename(module.getTitle());
                //searchViweHolder.setHiveinfo(module.getHiveinfo());
                searchViweHolder.setImage(getApplicationContext(),module.getimage());

                searchViweHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String hivename=getRef(i).getKey();
                        Intent profielintent=new Intent(searchActivity.this,ViewHive.class);
                        profielintent.putExtra("HiveName",hivename);
                        startActivity(profielintent);
                    }
                });

            }

        };
        result.setAdapter(FirebaseRecycleAdapter); }
    public void Browse() {

        Query searchHivesAndInfiQuere = allHivesdatabaseRef.orderByChild("title");//.startAt(SearchBoxInput).endAt(SearchBoxInput +"\uf8ff");
        FirebaseRecyclerAdapter<SearchResult,SearchViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<SearchResult,SearchViweHolder>
                (
                        SearchResult.class,
                        R.layout.item_row,
                        SearchViweHolder.class,
                        searchHivesAndInfiQuere
                ){
            @Override
            protected void populateViewHolder(SearchViweHolder searchViweHolder, SearchResult module, final int i)
            {
                searchViweHolder.setHivename(module.getTitle());
                //searchViweHolder.setHiveinfo(module.getHiveinfo());
                searchViweHolder.setImage(getApplicationContext(),module.getimage());

                searchViweHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String hivename=getRef(i).getKey();
                        Intent profielintent=new Intent(searchActivity.this,ViewHive.class);
                        profielintent.putExtra("HiveName",hivename);
                        startActivity(profielintent);
                    }
                });

            }

        };
        result.setAdapter(FirebaseRecycleAdapter); }


    public static class SearchViweHolder extends RecyclerView.ViewHolder{
        View mViwe;
        public SearchViweHolder(@NonNull View itemView) {
            super(itemView);
            mViwe= itemView;
        }
        public void setImage(Context ctx, String img) {
     /* CircleImageView MyImage= (CircleImageView) mViwe.findViewById(R.id.all_haive_profileImg);
        Picasso.with(ctx).load(img).placeholder(R.drawable.beeehive.Into(MyImage);
*/
            ImageView post_image = (ImageView) mViwe.findViewById(R.id.post_image);
            //  Picasso.get().load(img).into(post_image);
            Picasso.get().load(img).into(post_image);
            Glide.with(ctx).load(img).into(post_image);
        }

        public void setHivename(String hivename){
            TextView myName=(TextView)mViwe.findViewById(R.id.post_title);
            // result was all user names (retreved from database)
            myName.setText(hivename);
        }

       /* public void setHiveinfo(String hiveinfo) {{
            TextView myHiveInfo=(TextView) mViwe.findViewById(R.id.all_haive_profile_bio);
            myHiveInfo.setText(hiveinfo);
        }
        }*/
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_jhives:
                Intent intentjhives=new Intent(searchActivity.this, joinedHivesActivity.class);
                startActivity(intentjhives);
                break;

            case R.id.nav_home:
                Intent intentsearch=new Intent(searchActivity.this, MainActivity.class);
                startActivity(intentsearch);
                break;

            case R.id.nav_profile:

                Intent intentprofile=new Intent(searchActivity.this, ProfileActivity.class);
                startActivity(intentprofile);
                break;
            case R.id.nav_notf:

                Intent intentnot=new Intent(searchActivity.this, NotificationActivity.class);
                startActivity(intentnot);
                break;
        }

    }
}