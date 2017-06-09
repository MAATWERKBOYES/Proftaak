package docentengo.fontys.nl.docentengo;


//Never forgetti
//http://145.93.96.177:8080/people
//http://145.93.96.177:8080/question

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import Business.APIConnection;
import Business.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String secureID;
    RestTemplate client;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.client = new RestTemplate();
        client.getMessageConverters().add(new StringHttpMessageConverter());
        Async async = new Async();
        async.execute();


        Button submitButton = (Button) findViewById(R.id.btnSaveName);
        EditText inputField = (EditText) findViewById(R.id.txtInput);
        initiateHomeScreen(submitButton, inputField);
    }

    //#ToDo Jeroen hier zorgen dat je als je al eens bent ingelogt gelijk doorgaat, in this case no input required
    private void initiateHomeScreen(Button submitButton, final EditText inputField) {
        //ifSignedIn call OpenTeacherDex with my ID/Username
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testInput(inputField.getText().toString())) {
                    //#getMyAndroidID
                    User newUser = new User(inputField.getText().toString(),Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    //TODO create user in db
                    AsyncSave save = new AsyncSave(newUser);
                    save.execute();
                    OpenTeacherDex(newUser);
                } else {
                    showAlertDialog("Missing input", "please enter a username");
                }
            }
        });
    }

    private void OpenTeacherDex(User currentUser) {
        Intent intent = new Intent(getApplicationContext(), TeacherDexActivity.class);
        intent.putExtra("CurrentUser", currentUser);
        startActivity(intent);
        finish();
    }

    private boolean testInput(String stringToTest) {
        if (stringToTest.equals(null) || "".equals(stringToTest)) {
            return false;
        }
        return true;
    }

    /**
     * Shows an alert dialog
     *
     * @param title   of the dialog
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
    }//user/save


    private class Async extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            String userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            User temp = client.getForObject(APIConnection.getAPIConnectionInformationURL() + "user/" + userID, User.class);
            return temp;
        }
        @Override
        protected void onPostExecute(User user) {

            if (user != null) {
                OpenTeacherDex(user);
            }
        }
    }

    private class AsyncSave extends AsyncTask<Void, Void, Void> {

        private User user;

        public AsyncSave(User user)
        {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ObjectMapper mapper= new ObjectMapper();
            String json="";
            try {
                json = mapper.writeValueAsString(user);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            client.setInterceptors(Arrays.asList((ClientHttpRequestInterceptor) new Fatoe()));
           client.postForObject(APIConnection.getAPIConnectionInformationURL() + "user",json,User.class);

            return null;
        }
    }

    public class Fatoe implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().add(HttpHeaders.ACCEPT, "application/json");
            request.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
            request.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");


            return execution.execute(request, body);
        }
    }
}


