package com.example.speechtotextdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.speechtotextdemo.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        SpeechRecognition();
        initClick();
        setTextToSpeech();
    }

    private void initClick() {

        binding.image.setOnClickListener(view -> {
            startSpeechRecognition();
        });

        binding.btnListen.setOnClickListener(view -> {
            if (binding.edtText.getText().toString().isEmpty()) {
                Toast.makeText(this, "please enter text", Toast.LENGTH_SHORT).show();
            } else {
                textToSpeech.speak(binding.edtText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
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

            }

            @Override
            public void onBeginningOfSpeech() {
                binding.text.setText("");
                binding.text.setHint("Listening...");

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
            }

            @Override
            public void onError(int i) {
                stopSpeechRecognition();
            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    binding.text.setText(recognizedText);
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

    private void circularAnimation() {
        // Create the animation
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(binding.image, "scaleX", 1f, 1.5f, 1f);
        scaleXAnimator.setDuration(1000); // Set the duration of the animation
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE); // Repeat the animation infinitely
        scaleXAnimator.setRepeatMode(ValueAnimator.RESTART); // Restart the animation when it reaches the end

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(binding.image, "scaleY", 1f, 1.5f, 1f);
        scaleYAnimator.setDuration(1000);
        scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ValueAnimator.RESTART);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator); // Play both scale animations together

// Start the animation
        animatorSet.start();
    }

}