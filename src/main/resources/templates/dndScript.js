async function sendMessage() {
    const userInput = document.getElementById('user-input').value;

    if (userInput.trim() !== '') {
        // Send brugerens input til backend for at få et eventyr
        const response = await fetch('/chat/adventure', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userMessage: userInput })
        });

        const data = await response.json();
        const gameLog = document.getElementById('game-log');
        gameLog.textContent += "\n🤖 Dungeon Master: " + data.message;
        document.getElementById('user-input').value = '';
    }
}

async function rollDice() {
    const response = await fetch('/chat/roll-dice', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const data = await response.json();
    const gameLog = document.getElementById('game-log');
    gameLog.textContent += "\n🎲 Du rullede en: " + data.message;
}
