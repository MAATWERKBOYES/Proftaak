package docentengo.fontys.nl.docentengo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import Business.APIConnection;
import Business.Question;

public class BattleActivity extends AppCompatActivity {
    RestTemplate client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        this.client = new RestTemplate();
        client.getMessageConverters().add(new StringHttpMessageConverter());
        BattleActivity.Async async = new BattleActivity.Async();
        async.execute();
    }



    private class Async extends AsyncTask<Void, Void, List<Question>> {
        @Override
        protected List<Question> doInBackground(Void... params) {
            //TODO put to textboxes (Future)
            System.out.println("wat.");
            List<Question> temp = Arrays.asList(client.getForObject(APIConnection.getAPIConnectionInformationURL() + "user", Question[].class));
            //TODO Select correct question
            return temp;
        }

        @Override
        protected void onPostExecute(List<Question> questions) {
            super.onPostExecute(questions);
        }
    }
}
