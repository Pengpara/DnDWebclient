async function sendMessage() {
    const input = document.getElementById('user-input');
    const message = input.value.trim();
    const log = document.getElementById('game-log');

    if (!message) return;

    log.innerHTML += `<div><strong>🧝 Du:</strong> ${message}</div>`;
    input.value = '';

    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: message })
    });

    const data = await response.json();

    log.innerHTML += `<div><strong>🤖 Dungeon Master:</strong> ${data.message}</div>`;
    log.scrollTop = log.scrollHeight;

    updateOptionsFromResponse(data.message);
}




async function rollDice() {
    const response = await fetch('http://localhost:8080/chat/roll-dice', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    });

    const data = await response.json();
    const gameLog = document.getElementById('game-log');
    gameLog.innerHTML += `<p>${data.message}</p>`;
}

document.getElementById('send-button').addEventListener('click', async function() {
    const button = this;
    button.disabled = true;
    try {
        const userInput = document.getElementById('user-input').value;
        const response = await fetch('/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ prompt: userInput })
        });
        const data = await response.json();
        document.getElementById('response').innerText = data.response;
    } catch (error) {
        console.error('Error:', error);
    } finally {
        button.disabled = false;
    }
});


function updateOptionsFromResponse(responseText) {
    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = ''; // Fjern gamle knapper

    const optionRegex = /^\d+\.\s+(.*)$/gm;
    let match;
    const options = [];

    while ((match = optionRegex.exec(responseText)) !== null) {
        const fullText = match[1]; // Hele valget, fx: "Do you follow the sound..."
        // Vi forkorter ved at fjerne "Do you" og punktum til sidst
        const shortened = fullText
            .replace(/^Do you\s*/i, '')
            .replace(/\?$/, '')
            .trim();

        options.push(shortened);
    }

    options.forEach(option => {
        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = option;
        button.onclick = () => sendMessage(option);
        optionsContainer.appendChild(button);
    });
}

// Automatisk startbesked når spillet loader
window.addEventListener('load', async () => {
    const log = document.getElementById('game-log');
    log.innerHTML += `<div><strong>🤖 Dungeon Master:</strong> Velkommen, eventyrer! Lad os begynde dit eventyr...</div>`;

    // Vi sender en simpel besked for at teste om serveren svarer
    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: 'Start the adventure' }) // Fast prompt
    });

    if (response.ok) {
        const data = await response.json();
        log.innerHTML += `<div><strong>🤖 Dungeon Master:</strong> ${data.message}</div>`;
        updateOptionsFromResponse(data.message);  // Update knapperne med valg
    } else {
        log.innerHTML += `<div><strong>⚠️ Error:</strong> Kunne ikke hente eventyr.</div>`;
    }
    log.scrollTop = log.scrollHeight;
});


