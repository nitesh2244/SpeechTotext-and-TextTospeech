package com.example.speechtotextdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.speechtotextdemo.databinding.ActivityNewBinding;
import com.salesforce.android.chat.core.ChatConfiguration;
import com.salesforce.android.chat.ui.ChatUI;
import com.salesforce.android.chat.ui.ChatUIClient;
import com.salesforce.android.chat.ui.ChatUIConfiguration;
import com.salesforce.android.service.common.utilities.control.Async;

public class NewActivity extends AppCompatActivity {


    ActivityNewBinding binding;
    public static final String ORG_ID = "00DB00000003Rxz";
    public static final String DEPLOYMENT_ID = "573B00000005KXz";
    public static final String BUTTON_ID = "575C00000004h3m";
    public static final String LIVE_AGENT_POD = "d.gla5.gus.salesforce.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new);

        ChatConfiguration chatConfiguration =
                new ChatConfiguration.Builder(ORG_ID, BUTTON_ID,
                        DEPLOYMENT_ID, LIVE_AGENT_POD)
                        .build();

        binding.btnChat.setOnClickListener(view -> {
            ChatUI.configure(ChatUIConfiguration.create(chatConfiguration))
                    .createClient(getApplicationContext())
                    .onResult(new Async.ResultHandler<ChatUIClient>() {
                        @Override
                        public void handleResult(Async<?> operation,
                                                 ChatUIClient chatUIClient) {

                            // Once configured, let’s start a chat session
                            chatUIClient.startChatSession(NewActivity.this);
                        }
                    });

        });
    }
}