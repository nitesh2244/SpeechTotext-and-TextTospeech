package com.example.mirarai.Audio.view;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mirarai.Audio.model.AudioResponse;
import com.example.mirarai.Audio.model.TextToAudioResponse;
import com.example.mirarai.Audio.viewmodel.AudioViewModel;
import com.example.mirarai.MySingleton;
import com.example.mirarai.OpenAIService;
import com.example.mirarai.R;
import com.example.mirarai.databinding.ActivityMainBinding;
import com.example.mirarai.util.ProgressDialog;
import com.example.mirarai.util.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener {

    ActivityMainBinding binding;
    private SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;

    private LottieAnimationView lottieAnimationView;

    private OpenAIService openAIService;

    String fromText = "";

    private Dialog recordingDialog;

    ProgressDialog progressDialog;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;
    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;

    AudioViewModel audioViewModel;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;

    String data = "";

    File recordedAudioFile;
    File audioFile;

    File avatar;
    private static final int REQUEST_PICK_AUDIO = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_PICK_IMAGE = 1;

    private boolean isRecording = false;
    private Handler handler = new Handler();


    private String apiUrl = "https://api.openai.com/v1/completions";
    private String accessToken = "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";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        /*  Window window = getWindow();
        int statusBarColor = ContextCompat.getColor(this, R.color.sky_blue);
        window.setStatusBarColor(statusBarColor);*/
        openAIService = new OpenAIService();
        progressDialog = new ProgressDialog(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        audioViewModel = new AudioViewModel();
        audioViewModel.init(this);


        SpeechRecognition();
        initClick();
        setTextToSpeech();
        observer();

        //loadMessagesFromSharedPreferences();

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
                HashMap<String, String> map = new HashMap<>();
                map.put("text", userInput);
                audioViewModel.textToAudio(map);
                //callAPI(userInput);
                addReceiverMessage("", true);
               /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
                binding.userInputEdittext.setHint("Ask your question");
                binding.userInputEdittext.setText("");
                closeKeyboard();
            } else {
                Toast.makeText(this, "please enter question's", Toast.LENGTH_SHORT).show();
            }

        });

        binding.title.setOnClickListener(view -> {
            //playRecording();
        });


        binding.audioFile.setOnClickListener(view -> {
           /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_PICK_AUDIO);*/
            showRecordingDialog();

        });

        binding.UploadImage.setOnClickListener(view -> {
            showImageSourceDialog();
        });

    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        String[] options = {"Camera", "Gallery"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Camera option selected
                    openCamera();
                } else if (which == 1) {
                    // Gallery option selected
                    openGallery();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
    }


    private void showRecordingDialog() {
        recordingDialog = new Dialog(this);
        recordingDialog.setContentView(R.layout.dialog_start_recording);
        recordingDialog.setCanceledOnTouchOutside(false);

        Button startRecordingButton = recordingDialog.findViewById(R.id.buttonStartRecording);
        Button stopRecordingButton = recordingDialog.findViewById(R.id.buttonStopRecording);
        ImageView close = recordingDialog.findViewById(R.id.close);

        close.setOnClickListener(view -> {
            recordingDialog.cancel();
        });

        startRecordingButton.setOnClickListener(v -> {
            if (isRecordAudioPermissionGranted()) {
                startRecordingButton.setVisibility(View.GONE);
                stopRecordingButton.setVisibility(View.VISIBLE);
                //dialogTitle.setText("Recording...");
                LottieAnimationView lottieAnimationView1 = recordingDialog.findViewById(R.id.micRecording);
                lottieAnimationView1.setVisibility(View.VISIBLE);
                lottieAnimationView1.setAnimation("mic_animation.json");
                lottieAnimationView1.loop(true);
                lottieAnimationView1.playAnimation();

                startRecording();
            } else {
                requestRecordAudioPermission();
            }

        });

        stopRecordingButton.setOnClickListener(v -> {
            recordingDialog.dismiss();
            stopRecording();

        });

        recordingDialog.show();
    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

        //saveMessagesToSharedPreferences();

        View rootView = findViewById(android.R.id.content);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keyboardHeight = screenHeight - r.bottom;

                if (keyboardHeight > screenHeight * 0.15) {
                    binding.scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }, 200);
                }
            }
        });
    }

    private void addReceiverMessage(String message, boolean showLottieAnimation) {
        LinearLayout containerLayout = new LinearLayout(this);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setBackgroundResource(R.drawable.receiver_bubble);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        containerLayout.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.baseline_volume_up_24); // Replace with your image resource
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageParams.gravity = Gravity.START;
        imageParams.setMargins(16, 20, 0, 0);
        imageView.setLayoutParams(imageParams);


        TextView receiverTextView = new TextView(this);

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (charIndex < message.length()) {
                    receiverTextView.setText(message.substring(0, charIndex++));
                    handler.postDelayed(this, 50);
                }
            }
        }, 50);*/

        receiverTextView.setText(message);
        receiverTextView.setTextColor(Color.WHITE);
        receiverTextView.setTextSize(18);

        receiverTextView.setPadding(8, 0, 8, 8);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.START;
        textParams.setMargins(16, 8, 16, 8);
        receiverTextView.setLayoutParams(textParams);


        lottieAnimationView = new LottieAnimationView(this);
        lottieAnimationView.setAnimation("dot_process.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();

        LinearLayout.LayoutParams lottieParams = new LinearLayout.LayoutParams(
                100, 100);
        lottieParams.gravity = Gravity.CENTER_HORIZONTAL;
        lottieParams.setMargins(8, 0, 8, 0);
        lottieAnimationView.setLayoutParams(lottieParams);

        containerLayout.addView(imageView);

        //saveMessagesToSharedPreferences();


        if (showLottieAnimation) {
            containerLayout.addView(lottieAnimationView);
        } else {
            containerLayout.addView(receiverTextView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = message;
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                /*String audioUrl = "http://192.168.1.122:3000/Airtel.mp3";

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(audioUrl);
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error playing audio", Toast.LENGTH_SHORT).show();
                    }
                }*/
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
                        binding.userInputEdittext.setHint("Ask your questions");
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
                        binding.userInputEdittext.setHint("Ask your questions");
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
                    addReceiverMessage("", true);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //callAPI(recognizedText);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("text", recognizedText);
                    audioViewModel.textToAudio(map);
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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
                //startSpeechRecognition();
            } else {
                requestRecordAudioPermission();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



            /*if (requestCode == REQUEST_PICK_AUDIO && data != null) {

                Uri audioUri = data.getData();

                try {
                    audioFile = Utils.getFile(MainActivity.this, audioUri);
                    Log.d("audioFile", "onActivityResult: " + audioFile);
                    progressDialog.show();
                    audioViewModel.uploadAudio(audioFile);


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else*/

            if (requestCode == REQUEST_IMAGE_CAPTURE &&  resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap camera = (Bitmap) extras.get("data");
                Uri uri = getImageUri(camera);
                Log.d("FromCamera", "onActivityResult: " + uri);
                try {
                    avatar = Utils.getFile(MainActivity.this, uri);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else if (requestCode == REQUEST_PICK_IMAGE &&  resultCode == RESULT_OK) {
                Uri gallery = data.getData();
                Log.d("FromGallery", "onActivityResult: " + gallery);
                try {
                    avatar = Utils.getFile(MainActivity.this, gallery);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        if (path != null) {
            return Uri.parse(path);
        } else {
            return null;
        }
    }

    private void callAPI(String text) {

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", text);
            requestBody.put("max_tokens", 100);
            requestBody.put("temperature", 1);
            requestBody.put("top_p", 1);
            requestBody.put("frequency_penalty", 0.0);
            requestBody.put("presence_penalty", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray choicesArray = response.getJSONArray("choices");
                    JSONObject choiceObject = choicesArray.getJSONObject(0);
                    String text = choiceObject.getString("text");
                    data = text.replaceFirst("\n", "").replaceFirst("\n", "");
                    Log.d("API Response", text.replaceFirst("\n", "").replaceFirst("\n", ""));
                    HashMap<String, String> map = new HashMap<>();
                    map.put("textToAudio", data);
                    /* addReceiverMessage("",true);*/
                   /* binding.mic.setClickable(false);
                    binding.mic.setFocusable(false);
                    binding.mic.setOnLongClickListener(null);

                    binding.sendButton.setClickable(false);
                    binding.sendButton.setFocusable(false);

                    binding.mic.setCardBackgroundColor(getResources().getColor(R.color.disable_button));
                    binding.sendButton.setCardBackgroundColor(getResources().getColor(R.color.disable_button));*/


                    audioViewModel.textToAudio(map);
                    //Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API Error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        int timeoutMs = 25000;
        RetryPolicy policy = new DefaultRetryPolicy(timeoutMs, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void observer() {

        audioViewModel.getIsFailed().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressDialog.hide();

                if (lottieAnimationView != null) {

                    if (lottieAnimationView.getParent() instanceof ViewGroup) {
                        ViewGroup parent = (ViewGroup) lottieAnimationView.getParent();
                        parent.removeView(lottieAnimationView);
                    }
                }
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
        audioViewModel.observeUploadAudioResponse().observe(this, new Observer<AudioResponse>() {
            @Override
            public void onChanged(AudioResponse audioResponse) {
                progressDialog.hide();
                Toast.makeText(MainActivity.this, audioResponse.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("ApiResponseAudioResponse", "onResponse: " + new Gson().toJson(audioResponse));
            }
        });

        audioViewModel.observeTextToAudioResponse().observe(this, new Observer<TextToAudioResponse>() {
            @Override
            public void onChanged(TextToAudioResponse textToAudioResponse) {

                Log.d("API response", "onChanged: " + textToAudioResponse.getAudioUrl());

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(textToAudioResponse.getAudioUrl());
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                            progressDialog.hide();

                            if (lottieAnimationView.getParent() instanceof ViewGroup) {
                                ViewGroup parent = (ViewGroup) lottieAnimationView.getParent();
                                parent.removeView(lottieAnimationView);
                            }
                            // Set data when media player starts playing
                            //addReceiverMessage(data, false);

                            /*Add text response*/

                            addReceiverMessage(textToAudioResponse.getText(), false);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                              /*  binding.mic.setClickable(true);
                                binding.mic.setFocusable(true);

                                binding.sendButton.setClickable(true);
                                binding.sendButton.setFocusable(true);

                                binding.mic.setCardBackgroundColor(getResources().getColor(R.color.sky_blue));
                                binding.sendButton.setCardBackgroundColor(getResources().getColor(R.color.sky_blue));*/
                        }
                    });
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.hide();
                    Toast.makeText(MainActivity.this, "Error playing audio", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void downloadAudioFile(String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle(fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }


    private void startRecording() {

        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir == null) {
            Log.e("Recording", "External storage not available.");
            return;
        }

        audioFilePath = externalFilesDir.getAbsolutePath() + "/recorded_audio.m4a";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void playRecording() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(recordedAudioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            recordedAudioFile = new File(audioFilePath);

            progressDialog.show();
            audioViewModel.uploadAudio(recordedAudioFile);


        }
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        return false;
    }

   /* @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }*/


}