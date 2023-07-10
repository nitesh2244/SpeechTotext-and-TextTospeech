package com.example.speechtotextdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.speechtotextdemo.databinding.ActivityMainBinding;
import com.example.speechtotextdemo.util.ProgressDialog;

import android.Manifest;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private ProgressBar progressBar;
    private SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;

    private OpenAIService openAIService;

    String fromText = "";

    ProgressDialog progressDialog;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        openAIService = new OpenAIService();
        progressDialog = new ProgressDialog(this);
        SpeechRecognition();
        initClick();
        setTextToSpeech();
    }

    private void initClick() {


        binding.mic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isRecordAudioPermissionGranted()) {
                    startSpeechRecognition();
                } else {
                    requestRecordAudioPermission();
                }
                return true;
            }
        });
        binding.mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Press & Hold", Toast.LENGTH_SHORT).show();
            }
        });

        binding.sendButton.setOnClickListener(view -> {
            fromText = "input";
            String userInput = binding.userInputEdittext.getText().toString();
            if (!userInput.isEmpty()) {
                addSenderMessage(userInput);
                generateResponse(userInput);
                binding.userInputEdittext.setHint("Ask your question");
                binding.userInputEdittext.setText("");
            } else {
                Toast.makeText(this, "please enter question's", Toast.LENGTH_SHORT).show();
            }


        });

    }

    @SuppressLint("StaticFieldLeak")
    private void generateResponse(String prompt) {

        progressDialog.show();

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    return openAIService.generateResponse(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception appropriately
                }
                return null;
            }

            @Override
            protected void onPostExecute(String response) {
                // Clear the searching text
                progressDialog.hide();
                if (response != null) {
                    if (fromText.equalsIgnoreCase("input")) {
                        addReceiverMessage(response);
                    } else if (fromText.equalsIgnoreCase("voiceInput")) {
                        textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null);
                        addReceiverMessage(response);
                    }
                } else {
                    addReceiverMessage("Error occurred while generating response");
                }
            }
        }.execute(prompt);
    }


    private void addSenderMessage(String message) {
        TextView senderTextView = new TextView(this);
        senderTextView.setText(message);
        senderTextView.setTextSize(18);
        senderTextView.setBackgroundResource(R.drawable.sender_bubble);
        senderTextView.setPadding(16, 8, 16, 8);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.END;
        layoutParams.setMargins(16, 8, 16, 8);
        senderTextView.setLayoutParams(layoutParams);
        layoutParams.bottomMargin = 20;


        binding.chatContainer.addView(senderTextView);

        binding.scrollView.post(new Runnable() {
            @Override
            public void run() {
                binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void addReceiverMessage(String message) {
        LinearLayout containerLayout = new LinearLayout(this);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setBackgroundResource(R.drawable.receiver_bubble);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        containerLayout.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.baseline_volume_up_24); // Replace with your image resource
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageParams.gravity = Gravity.START;
        imageParams.setMargins(16, 20, 0, 0);
        imageView.setLayoutParams(imageParams);

        TextView receiverTextView = new TextView(this);
        receiverTextView.setText(message);
        receiverTextView.setTextColor(Color.WHITE);
        receiverTextView.setTextSize(18);

        receiverTextView.setPadding(8, 0, 8, 8);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.START;
        receiverTextView.setLayoutParams(textParams);

        containerLayout.addView(imageView);
        containerLayout.addView(receiverTextView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = message;
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        layoutParams.bottomMargin = 20;
        binding.chatContainer.addView(containerLayout);
        binding.scrollView.post(new Runnable() {
            @Override
            public void run() {
                binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    private void setTextToSpeech() {

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if (i != TextToSpeech.ERROR) {

                    //textToSpeech.setLanguage(Locale.ENGLISH);
                    setDesiredVoice();
                }
            }
        });
    }

    private void setDesiredVoice() {

        Set<Voice> voices = textToSpeech.getVoices();

        for (Voice voice : voices) {
            if (voice.getName().equals("en-us-x-sfg#male_1-local")) {
                textToSpeech.setVoice(voice);
                break;
            }
            Log.d("TTS", "Voice: " + voice.getName());
        }
    }


    private void SpeechRecognition() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //binding.mic.setColorFilter(Color.RED);
                        binding.userInputEdittext.setHint("I'm listening...");

                    }
                });
            }

            @Override
            public void onBeginningOfSpeech() {
                binding.userInputEdittext.setHint("I'm listening...");

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                stopSpeechRecognition();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //binding.mic.setColorFilter(Color.BLACK);
                    }
                });


            }

            @Override
            public void onError(int i) {
                stopSpeechRecognition();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // binding.mic.setColorFilter(Color.BLACK);
                    }
                });
            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    fromText = "voiceInput";
                    String recognizedText = matches.get(0);
                    addSenderMessage(recognizedText);
                    generateResponse(recognizedText);
                    binding.userInputEdittext.setHint("Ask your questions");
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

    }


    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if (speechRecognizer != null) {
            speechRecognizer.startListening(intent);
        }
    }

    private void stopSpeechRecognition() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }


    private void requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_AUDIO_PERMISSION_CODE);
    }

    // Check if microphone permission is granted
    private boolean isRecordAudioPermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechRecognition();
            } else {
                requestRecordAudioPermission();
            }
        }
    }

}