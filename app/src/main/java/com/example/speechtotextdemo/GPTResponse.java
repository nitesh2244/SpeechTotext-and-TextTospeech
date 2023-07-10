package com.example.speechtotextdemo;

import com.google.gson.annotations.SerializedName;

public class GPTResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("object")
    private String object;

    @SerializedName("created")
    private Long created;

    @SerializedName("model")
    private String model;

    @SerializedName("usage")
    private Usage usage;

    @SerializedName("choices")
    private Choice[] choices;

    public String getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public Long getCreated() {
        return created;
    }

    public String getModel() {
        return model;
    }

    public Usage getUsage() {
        return usage;
    }

    public Choice[] getChoices() {
        return choices;
    }

    public String getText() {
        if (choices != null && choices.length > 0) {
            return choices[0].getText();
        }
        return "";
    }

    public static class Usage {
        @SerializedName("prompt_tokens")
        private int promptTokens;

        @SerializedName("completion_tokens")
        private int completionTokens;

        @SerializedName("total_tokens")
        private int totalTokens;

        public int getPromptTokens() {
            return promptTokens;
        }

        public int getCompletionTokens() {
            return completionTokens;
        }

        public int getTotalTokens() {
            return totalTokens;
        }
    }

    public static class Choice {
        @SerializedName("message")
        private Message message;

        @SerializedName("finish_reason")
        private String finishReason;

        @SerializedName("index")
        private int index;

        @SerializedName("logprobs")
        private LogProbs logProbs;

        @SerializedName("text")
        private String text;

        public Message getMessage() {
            return message;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public int getIndex() {
            return index;
        }

        public LogProbs getLogProbs() {
            return logProbs;
        }

        public String getText() {
            return text;
        }
    }

    public static class Message {
        @SerializedName("role")
        private String role;

        @SerializedName("content")
        private String content;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public static class LogProbs {
        // Define log probability fields if required
        // ...
    }
}
