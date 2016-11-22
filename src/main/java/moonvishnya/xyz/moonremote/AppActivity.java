package moonvishnya.xyz.moonremote;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.HttpURLConnection;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import moonvishnya.xyz.moonremote.net.server.ServerInformation;



public class AppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        final Button send_signal = (Button) findViewById(R.id.main_app_btn_send_signal);
        final Button remove_signal = (Button) findViewById(R.id.main_app_btn_remove_signal);

            send_signal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignalSender signalSender = new SignalSender();
                    signalSender.execute(ServerInformation.SERVER_NAME
                            + ServerInformation.MOON_REMOTE_SIGNAL_GETTER_SCRIPT
                            + ServerInformation.SERVER_ON_ADD_SIGNAL_OPERATION );
                }
            });

            remove_signal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskRemover remover = new TaskRemover();
                    remover.execute();
                }
            });
    }

    public class SignalSender extends AsyncTask<String, Void, Boolean> {

        private  boolean success=false;
        private  Integer server_answer;
        private  HttpURLConnection connection;
        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AppActivity.this);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... post_url) {
            URL url = null;
            try {
                url = new URL(post_url[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
                server_answer = connection.getResponseCode();

                if (server_answer == HttpsURLConnection.HTTP_OK) {
                    connection.disconnect();
                    success = true;
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (success) {
                Toast.makeText(AppActivity.this, "OK..", Toast.LENGTH_SHORT).show();
//                TaskRemover remover = new TaskRemover();
//                remover.execute();
                dialog.dismiss();
            } else {
                Toast.makeText(AppActivity.this, "Проблемы с интернетом?", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        }
    }
    public class TaskRemover extends AsyncTask<Void, Void, Integer> {

        private  Integer server_answer;
        private  HttpURLConnection connection;
        private static final String remove_signal_url = ServerInformation.SERVER_NAME
                + ServerInformation.MOON_REMOTE_SIGNAL_GETTER_SCRIPT
                + ServerInformation.ON_DELETE_SIGNAL_OPERATION;


        @Override
        protected Integer doInBackground(Void... params) {

            URL url = null;

            try {
                url = new URL(remove_signal_url);
                Log.i("s", remove_signal_url);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
                server_answer = connection.getResponseCode();

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return server_answer;
        }
        @Override
        protected void onPostExecute(Integer param) {
            Toast.makeText(AppActivity.this, "Сигнал удален.", Toast.LENGTH_SHORT).show();
        }
    }

}
