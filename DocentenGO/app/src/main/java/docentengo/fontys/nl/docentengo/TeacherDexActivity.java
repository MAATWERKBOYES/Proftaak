package docentengo.fontys.nl.docentengo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import Business.PersonEntry;
import api.APIConnection;
import Business.Person;
import Business.User;
import api.ApiController;

public class TeacherDexActivity extends AppCompatActivity {
    private User signedUser;
    private ApiController apiController;
    private ListView lvTeacherDex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dex);

        lvTeacherDex = (ListView)findViewById(R.id.lvTeacherDex);

        System.out.println("Loaded page");
        retrieveUser();
        System.out.println("got the user");
        setPersonalDexName();
        System.out.println("set the dex name");

        setAdapter(signedUser.getTeachers());

        createEnterCodeButtonEvent();
        createRankingsButtonEvent();
    }

    public void setAdapter(List<PersonEntry> teacherList)
    {
        ArrayAdapter<PersonEntry> adapter = new ArrayAdapter<>(this
                ,android.R.layout.simple_list_item_1
                ,android.R.id.text1
                ,teacherList);
        lvTeacherDex.setAdapter(adapter);

        lvTeacherDex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonEntry entry = (PersonEntry) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(),TeacherInfoActivity.class);
                intent.putExtra("selectedTeacher",entry.getTeacher());
                intent.putExtra("CurrentUser", signedUser);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveUser(){
        if(getIntent().hasExtra("CurrentUser")){
            signedUser = (User)getIntent().getExtras().getSerializable("CurrentUser");
        }else if(!getIntent().hasExtra("CurrentUser")){
            showAlertDialog("No user", "There was no logged in User found.");
        }
    }

    private void setPersonalDexName(){
        TextView devName = (TextView)findViewById(R.id.tvDexName);
        String username = signedUser.getName();
        if("sS".indexOf(username.substring(username.length() - 1)) > -1 ){
            devName.setText(signedUser.getName() + "' Dex");
        }else{
            devName.setText(signedUser.getName() + "'s Dex");
        }
    }

    private void createEnterCodeButtonEvent(){
        Button navigateBattleCodeScreen = (Button)findViewById(R.id.btnCode);
        navigateBattleCodeScreen.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EncounterActivity.class);
                intent.putExtra("CurrentUser", signedUser);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createRankingsButtonEvent(){
        Button btnNavigateRanking = (Button)findViewById(R.id.btnRanking);
        btnNavigateRanking.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RankingActivity.class);
                intent.putExtra("CurrentUser", signedUser);
                startActivity(intent);
                finish();
            }
        });
    }
    /**
     * Shows an alert dialog
     * @param title of the dialog
     * @param message of the dialog
     */
    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
