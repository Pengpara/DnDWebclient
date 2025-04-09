package com.example.dungeonsanddragonswebclient;

import com.example.dungeonsanddragonswebclient.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class DungeonsAndDragonsWebclientApplication implements CommandLineRunner {

    @Value("${api.key}")  // API key injected from application.properties
    private String key;

    private final ChatService chatService;

    public DungeonsAndDragonsWebclientApplication(ChatService chatService) {
        this.chatService = chatService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DungeonsAndDragonsWebclientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        // Start the adventure with an initial user input
        System.out.println("Welcome to the Dungeons & Dragons adventure!");
        System.out.print("Enter your action to begin the adventure: ");
        String userInput = scanner.nextLine().trim();

        // Generate the adventure scenario based on the user's action
        String adventureScenario = chatService.generateAdventureScenario(userInput);

        // Output the AI-generated scenario
        System.out.println("\n🤖 Dungeon Master:");
        System.out.println(adventureScenario);

        // Adventure loop
        boolean keepPlaying = true;
        while (keepPlaying) {
            System.out.print("\n🧝 Your move ('exit' to quit): ");
            userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                keepPlaying = false;
                System.out.println("🛑 The adventure ends here. Thanks for playing!");
            } else if (userInput.toLowerCase().startsWith("roll")) {
                handleDiceRoll(userInput);
            } else {
                // Generate new scenario based on the user's new action
                String newScenario = chatService.generateAdventureScenario(userInput);

                // Output the new AI-generated scenario
                System.out.println("\n🤖 Dungeon Master:");
                System.out.println(newScenario);
            }
        }
    }

    // Dice roll handler
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

            // Handle multiple dice rolls
            List<Integer> rolls = new ArrayList<>();
            int total = 0;
            for (int i = 0; i < count; i++) {
                int roll = (int) (Math.random() * sides) + 1;
                rolls.add(roll);
                total += roll;
            }

            // Describe the result of the dice roll
            String rollDescription = getRollDescription(total);

            System.out.println("🎲 You rolled " + count + "d" + sides + ": " + rolls + " = Total: " + total);
            System.out.println(rollDescription);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Couldn't read dice format from: " + dicePart);
        }
    }

    // Function to determine the description of the dice roll
    private String getRollDescription(int rollTotal) {
        if (rollTotal == 20) {
            return "✨ Critical Success: Unbelievable! You're a legend. Riches rain on you.";
        } else if (rollTotal >= 18 && rollTotal <= 19) {
            return "🏅 Strong Success: You pull it off impressively. People applaud you.";
        } else if (rollTotal >= 15 && rollTotal <= 17) {
            return "✅ Success: Well done! You complete your action effectively.";
        } else if (rollTotal >= 12 && rollTotal <= 14) {
            return "😌 Mild Success: You manage it, but it wasn’t pretty.";
        } else if (rollTotal >= 9 && rollTotal <= 11) {
            return "😐 Mixed Result: It works... kind of. There are some consequences.";
        } else if (rollTotal >= 6 && rollTotal <= 8) {
            return "😬 Weak Failure: You fail, but nothing too bad happens.";
        } else if (rollTotal >= 3 && rollTotal <= 5) {
            return "❌ Fail: You mess up. Things start to go wrong.";
        } else if (rollTotal == 2) {
            return "☠️ Big Mistake: Oof. That's bad. People laugh. You’re embarrassed.";
        } else if (rollTotal == 1) {
            return "💀 Critical Fail: Total disaster. Something terrible happens!";
        }
        return "⚖️ Unknown Roll: Something strange occurred!";
    }
}