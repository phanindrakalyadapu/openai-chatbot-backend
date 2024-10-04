package com.example.chatGPT.service;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import kong.unirest.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class ChatGPTService {
    @Value("${chatGPT.apikey}")
    private String apiKey;
    private static final String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public String getAnswer(String question) {
        // Create the messages array for the conversation
        JSONArray messages = new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                .put(new JSONObject().put("role", "user").put("content", question));

        // Construct the request body with the model, messages, and max_tokens
        JSONObject requestBody = new JSONObject()
                .put("model", "gpt-3.5-turbo")  // Specify the model
                .put("messages", messages)
                .put("max_tokens", 1000);  // You can adjust this based on your needs

        // Log the prompt for debugging purposes
        System.out.println("Prompt: " + question);

        // Make the POST request to OpenAI's API
        HttpResponse<JsonNode> response = Unirest.post(API_ENDPOINT)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .asJson();

        // Log the response for debugging purposes
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            // Parse the response to get the assistant's reply from 'choices'
            return response.getBody()
                    .getObject()
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        } else {
            // Throw an error in case the API call fails
            throw new RuntimeException("Failed to call ChatGPT API: " + response.getStatusText());
        }
    }
}
