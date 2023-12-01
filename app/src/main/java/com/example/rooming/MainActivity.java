package com.example.rooming;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rooming.Activities.LoginActivity;
import com.example.rooming.Entity.Message;
import com.example.rooming.MessegeAdaptors.MessageAdaptor;
import com.example.rooming.Session.SessionManager;
import com.example.rooming.ui.MyAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdaptor messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList = new ArrayList<>();


        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_ET);
        sendButton = findViewById(R.id.send_btn);

        //recycle view
        messageAdapter = new MessageAdaptor(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //Toolbar initialization
        Toolbar toolbar=findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);


        //Click Listener
        sendButton.setOnClickListener((v -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message.Sent_By_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu,menu);

        MenuItem navBackItem=menu.findItem(R.id.nav_back);
        navBackItem.setVisible(false) ;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent intent=new Intent(MainActivity.this, MyAccount.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_logout) {
            logoutUser();
            Toast.makeText(MainActivity.this,"USERS LOGOUT",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
    void addResponse(String response) {

        removeTypeMessage();
        addToChat(response, Message.Sent_By_Bot);
    }
    private void removeTypeMessage() {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            messageList.remove(i);
            runOnUiThread(() -> {
                messageAdapter.notifyItemRemoved(i);
            });
            break;
        }
    }
    void callAPI(String question) {
        //OKHTTP
        messageList.add(new Message("Typing... ", Message.Sent_By_Bot));
        String apiKey=BuildConfig.OPENAI_API_KEY;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", question);
            messageArr.put(obj);

            jsonBody.put("messages", messageArr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {

                    String responseBody = response.body().string();
                    JSONObject jsonOb = null;
                    try {
                        jsonOb = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonOb.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    addResponse("Failed to load response " + response.body().string());

                }
            }
        });
    }
   private void logoutUser(){
       SessionManager sessionManager=new SessionManager(getApplicationContext());
       sessionManager.removeSession();
       Intent intent=new Intent(MainActivity.this, LoginActivity.class);
       startActivity(intent);
       finish();
   }
}

