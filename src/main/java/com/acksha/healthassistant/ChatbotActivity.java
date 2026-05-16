package com.acksha.healthassistant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class ChatbotActivity extends AppCompatActivity {

    private EditText inputMessage;
    private Button sendBtn;
    private ImageButton voiceBtn;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> messages = new ArrayList<>();
    private TextToSpeech tts;
    private static final int REQ_CODE_SPEECH_INPUT = 101;

    private static final String GEMINI_API_KEY = "AIzaSyCFbuRlFfmmCkKMtYthIKRJdnSdBcjhoTs";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        inputMessage = findViewById(R.id.inputMessage);
        sendBtn = findViewById(R.id.sendBtn);
        voiceBtn = findViewById(R.id.voiceBtn);
        chatListView = findViewById(R.id.chatListView);

        chatAdapter = new ChatAdapter(this, messages);
        chatListView.setAdapter(chatAdapter);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH);
                tts.setSpeechRate(0.9f);
            }
        });

        sendBtn.setOnClickListener(v -> {
            String text = inputMessage.getText().toString().trim();
            if (text.isEmpty()) return;

            addMessage(text, true);
            inputMessage.setText("");
            sendToModel(text);
        });

        voiceBtn.setOnClickListener(v -> startVoiceInput());
    }

    private void addMessage(String m, boolean isUser) {
        messages.add(new ChatMessage(m, isUser));
        chatAdapter.notifyDataSetChanged();
        chatListView.smoothScrollToPosition(messages.size() - 1);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> r = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (r != null && !r.isEmpty()) {
                String spoken = r.get(0);
                addMessage(spoken, true);
                sendToModel(spoken);
            }
        }
    }

    private void sendToModel(String userText) {
        addMessage("⏳ Thinking...", false);

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build();

                JSONObject req = new JSONObject();
                req.put("contents", new JSONArray().put(
                        new JSONObject().put("parts", new JSONArray().put(
                                new JSONObject().put("text", userText)
                        ))
                ));

                RequestBody body = RequestBody.create(
                        req.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(GEMINI_API_URL + GEMINI_API_KEY)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                String resp = response.body() != null ? response.body().string() : "";

                String reply = "Sorry, no reply.";
                try {
                    JSONObject json = new JSONObject(resp);
                    if (json.has("candidates")) {
                        reply = json.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");
                    } else if (json.has("generated_text")) {
                        reply = json.getString("generated_text");
                    } else {
                        reply = resp;
                    }
                } catch (Exception e) {
                    reply = resp.isEmpty() ? "No response" : resp;
                }

                String finalReply = reply;
                runOnUiThread(() -> {
                    if (!messages.isEmpty() &&
                            messages.get(messages.size() - 1).getMessage().contains("Thinking")) {
                        messages.remove(messages.size() - 1);
                    }
                    addMessage(finalReply, false);
                    tts.speak(finalReply, TextToSpeech.QUEUE_FLUSH, null, "tts1");
                });

            } catch (IOException | org.json.JSONException e) {
                runOnUiThread(() -> {
                    if (!messages.isEmpty() &&
                            messages.get(messages.size() - 1).getMessage().contains("Thinking")) {
                        messages.remove(messages.size() - 1);
                    }
                    addMessage("⚠️ Error: " + e.getMessage(), false);
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}