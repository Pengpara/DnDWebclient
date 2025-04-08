const chatBox = document.getElementById("chatBox");

function appendMessage(sender, text) {
    const p = document.createElement("p");
    p.innerHTML = `<strong>${sender}:</strong> ${text}`;
    chatBox.appendChild(p);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function sendMessage() {
    const input = document.getElementById("userInput");
    const text = input.value.trim();
    if (!text) return;

    appendMessage("🧝 You", text);
    input.value = "";

    // Send user input to Spring Boot backend and get the response from DM
    fetch('/api/chat/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(text)
    })
        .then(response => response.json())
        .then(data => {
            appendMessage("🧙 DM", data); // Append DM's response to the chatbox
        })
        .catch(error => {
            console.error("Error fetching chat response:", error);
            appendMessage("🧙 DM", "Sorry, there was an error processing your request.");
        });
}

function rollDice() {
    const roll = Math.floor(Math.random() * 20) + 1;
    appendMessage("🎲 Dice", `You rolled a ${roll}`);

    let dmResponse = "";

    if (roll === 20) {
        dmResponse = "✨ Critical success! You impress everyone and earn a bag of gold!";
    } else if (roll === 1) {
        dmResponse = "💀 Critical fail! You fall flat on your face and someone calls their goons to beat you up.";
    } else if (roll >= 15) {
        dmResponse = "⚔️ Great success! Your move goes well.";
    } else if (roll >= 10) {
        dmResponse = "😐 Mixed result. It kind of works... but there's a complication.";
    } else {
        dmResponse = "😬 Not great. Your move fails awkwardly.";
    }

    setTimeout(() => {
        appendMessage("🧙 DM", dmResponse);
    }, 700);
}
