// Funktion til at sende besked og opdatere loggen
async function sendMessage(userMessage = null) {
    const input = document.getElementById('user-input');
    const message = userMessage || input.value.trim();
    const log = document.getElementById('game-log');

    if (!message) return;

    log.innerHTML += `<div><strong>🧝 Du:</strong> ${message}</div>`;
    input.value = '';

    // Send beskeden til serveren og få et svar
    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: message })
    });

    const data = await response.json();

    // Vis DM's svar og opdater knapperne
    log.innerHTML += `<div><strong>🤖 Dungeon Master:</strong> ${data.message}</div>`;
    log.scrollTop = log.scrollHeight;

    updateOptionsFromResponse(data.message);
}

// Funktion til at opdatere knapperne baseret på serverens svar
function updateOptionsFromResponse(responseText) {
    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = ''; // Fjern gamle knapper

    // Regex til at finde valgmulighederne i svaret
    const optionRegex = /^\d+\.\s+(.*)$/gm;
    let match;
    const options = [];

    while ((match = optionRegex.exec(responseText)) !== null) {
        const fullText = match[1]; // Hele valget, fx: "Do you follow the sound..."
        // Forkorter valget ved at fjerne "Do you" og punktum til sidst
        const shortened = fullText
            .replace(/^Do you\s*/i, '')
            .replace(/\?$/, '')
            .trim();

        options.push(shortened);
    }

    // Opret knapper for hver mulighed
    options.forEach(option => {
        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = option;
        button.onclick = () => sendMessage(option); // Send valget som en besked
        optionsContainer.appendChild(button);
    });
}

// Automatisk startbesked når spillet loader
window.addEventListener('load', async () => {
    const log = document.getElementById('game-log');
    log.innerHTML += `<div><strong>🤖 Dungeon Master:</strong> Welcome, adventurer! Let’s begin your journey...</div>`;

    // Start eventyret ved at sende en simpel besked til serveren
    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: 'Start the adventure' }) // Fast prompt
    });

    if (response.ok) {
        const data = await response.json();
        log.innerHTML += `<div><strong>🤖 Dungeon Master:</strong> ${data.message}</div>`;
        updateOptionsFromResponse(data.message);  // Opdater knapperne med valgmuligheder
    } else {
        log.innerHTML += `<div><strong>⚠️ Error:</strong> Kunne ikke hente eventyr.</div>`;
    }
    log.scrollTop = log.scrollHeight;
});
