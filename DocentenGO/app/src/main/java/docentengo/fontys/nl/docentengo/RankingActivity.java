package docentengo.fontys.nl.docentengo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import api.ApiController;
import Business.User;

public class RankingActivity extends AppCompatActivity {
    private User signedUser;
    private ListView lvRankings;
    private ApiController apiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        retrieveUser();
        createReturnDexButtonEvent();
        this.apiController = new ApiController();
        lvRankings = (ListView) findViewById(R.id.lvRankings);

        FillRankingList FillRankingList = new FillRankingList();
        FillRankingList.execute();
    }

    private void retrieveUser(){
        if(getIntent().hasExtra("CurrentUser")){
            signedUser = (User)getIntent().getExtras().getSerializable("CurrentUser");
        }else if(!getIntent().hasExtra("CurrentUser")){
            AlertHandler.showAlertDialog(this,
                    "No user",
                    "There was no logged-in User found.");
        }
    }

    private void createReturnDexButtonEvent(){
        Button returnButton = (Button)findViewById(R.id.btnReturnDex);
        returnButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TeacherDexActivity.class);
                intent.putExtra("CurrentUser", signedUser);
                startActivity(intent);
                finish();
            }
        });
    }

    public void setAdapter(List<User> users) {
        if(users !=null) {
            ArrayAdapter<User> adapter = new ArrayAdapter<>(this
                    , android.R.layout.simple_list_item_1
                    , android.R.id.text1
                    , users);
            lvRankings.setAdapter(adapter);
        }
    }

    private class FillRankingList extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... params) {
            try
            {
                return apiController.getAllUser();
            }
            catch(Exception ex)
            {
                AlertHandler.showErrorDialog(RankingActivity.this,
                        ex,
                        "Server connection error",
                        "The application was unable to connect to the server");
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<User> users) {
            setAdapter(users);
        }
    }
}
