package com.example.dungeonsanddragonswebclient;

import com.example.dungeonsanddragonswebclient.model.ChatGPTRequest;
import com.example.dungeonsanddragonswebclient.model.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class DungeonsAndDragonsWebclientApplication implements CommandLineRunner {
    @Value("${api.key}")
    private String key;

    public static void main(String[] args) {
        SpringApplication.run(DungeonsAndDragonsWebclientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        WebClient webClient = WebClient.create("https://api.mistral.ai");

        List<ChatGPTRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatGPTRequest.Message("system", "You are a creative and engaging Dungeon Master for a Dungeons & Dragons adventure. Set the scene and respond to the player's actions."));
        messages.add(new ChatGPTRequest.Message("user", "Start an exciting adventure. End with: 'What do you want to do next?'"));

        boolean keepPlaying = true;

        while (keepPlaying) {
            ChatGPTRequest request = new ChatGPTRequest("mistral-medium", messages, 0.9, 1.0);

            String responseContent = webClient.post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer " + key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatGPTResponse.class)
                    .map(response -> response.getChoices().get(0).getMessage().getContent())
                    .block();

            System.out.println("\n🤖 Dungeon Master:");
            System.out.println(responseContent);

            messages.add(new ChatGPTRequest.Message("assistant", responseContent));

            System.out.print("\n🧝 Your move ('exit' to quit): ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                keepPlaying = false;
                System.out.println("🛑 The adventure ends here. Thanks for playing!");
            } else if (userInput.toLowerCase().startsWith("roll")) {
                handleDiceRoll(userInput);
            } else {
                messages.add(new ChatGPTRequest.Message("user", userInput));
            }
        }
    }

    private void handleDiceRoll(String input) {
        String[] parts = input.toLowerCase().split(" ");
        if (parts.length < 2) {
            System.out.println("⚠️ Use the format like 'roll d20' or 'roll 2d6'");
            return;
        }

        String dicePart = parts[1];
        int count = 1;
        int sides;

        if (!dicePart.contains("d")) {
            System.out.println("⚠️ Format should be like 'd20' or '2d6'");
            return;
        }

        String[] diceParts = dicePart.split("d");
        try {
            if (!diceParts[0].isEmpty()) {
                count = Integer.parseInt(diceParts[0]);
            }
            sides = Integer.parseInt(diceParts[1]);

            if (count <= 0 || sides <= 0) {
                System.out.println("⚠️ Both number of dice and sides must be greater than 0.");
                return;
            }

            List<Integer> rolls = new ArrayList<>();
            int total = 0;
            for (int i = 0; i < count; i++) {
                int roll = (int) (Math.random() * sides) + 1;
                rolls.add(roll);
                total += roll;
            }

            System.out.println("🎲 You rolled " + count + "d" + sides + ": " + rolls + " = Total: " + total);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Couldn't read dice format from: " + dicePart);
        }
    }
}
