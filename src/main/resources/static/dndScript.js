let characterCreated = false;
let characterInfo = {};
let currentChoices = [];

async function createCharacter() {
    const log = document.getElementById('game-log');
    const userInput = document.getElementById('user-input');
    const sendButton = document.getElementById('send-button');
    const optionsContainer = document.getElementById('options-container');

    if (!sendButton || !userInput || !log || !optionsContainer) {
        console.error("Essential elements are missing from the DOM.");
        return;
    }

    appendToGameLog("🤖 Dungeon Master: Let's start by creating your character. What is your character's name?", true);
    userInput.focus();

    sendButton.onclick = async function () {
        const nameInput = userInput.value.trim();
        if (nameInput && !characterCreated) {
            characterInfo.name = nameInput;
            appendToGameLog(`🧝 Du: My name is ${nameInput}`, false);
            userInput.value = '';
            characterCreated = true;
            await chooseClass();
        } else {
            appendToGameLog("🤖 Dungeon Master: Please enter a valid name.", true);
        }
    };

    userInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            sendButton.click();
        }
    });
}

async function chooseClass() {
    const log = document.getElementById('game-log');
    const optionsContainer = document.getElementById('options-container');

    appendToGameLog(`🤖 Dungeon Master: Great, ${characterInfo.name}! Now, what kind of adventurer are you? Choose one of the following classes:`, true);

    // Hide buttons initially
    optionsContainer.style.display = 'none';

    // Wait for a moment to ensure the message is printed
    await new Promise(resolve => setTimeout(resolve, 500));

    // Now we can show the buttons after the AI finishes typing
    const classes = ['Warrior', 'Mage', 'Rogue', 'Healer'];
    classes.forEach(className => {
        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = className;
        button.onclick = () => {
            characterInfo.class = className;
            appendToGameLog(`🧝 Du: I choose to be a ${className}`, false);
            optionsContainer.innerHTML = '';
            document.getElementById('user-input').value = '';
            startAdventure();
        };
        optionsContainer.appendChild(button);
    });

    // Wait for typewriter to finish and then show options
    showOptions();  // This will handle showing the buttons and scrolling
}

async function startAdventure() {
    const log = document.getElementById('game-log');

    appendToGameLog(`🤖 Dungeon Master: Welcome, ${characterInfo.name} the ${characterInfo.class}! Your adventure begins now...`, true);

    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: 'Start the adventure' })
    });

    if (response.ok) {
        const data = await response.json();
        appendToGameLog(`🤖 Dungeon Master: ${data.message}`, true);
        const extractedChoices = extractChoicesFromMessage(data.message);
        currentChoices = extractedChoices;
        updateOptionsFromResponse(extractedChoices);
    } else {
        appendToGameLog("⚠️ Error: Could not retrieve adventure.", true);
    }

    // Ensure autoscrolling after the message is shown
    log.scrollTop = log.scrollHeight;
}

function extractChoicesFromMessage(message) {
    const lines = message.split('\n');
    return lines
        .filter(line => /^\d+\./.test(line))
        .map(line => {
            const text = line.replace(/^\d+\.\s*/, '').trim();
            return { text };
        });
}

function updateOptionsFromResponse(choices) {
    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = '';

    if (choices.length === 0) return;

    choices.forEach((choice, index) => {
        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = `${index + 1}. ${choice.text}`;
        button.onclick = () => handleUserChoice(index + 1, choice.text);
        optionsContainer.appendChild(button);
    });
}

async function handleUserChoice(choiceIndex, choiceText) {
    const log = document.getElementById('game-log');
    const optionsContainer = document.getElementById('options-container');

    appendToGameLog(`🧝 Du: I choose to ${choiceText}`, false);

    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: `I choose option ${choiceIndex}` })
    });

    if (response.ok) {
        const data = await response.json();
        appendToGameLog(`🤖 Dungeon Master: ${data.message}`, true);
        const extractedChoices = extractChoicesFromMessage(data.message);
        currentChoices = extractedChoices;
        updateOptionsFromResponse(extractedChoices);
    } else {
        appendToGameLog("⚠️ Error: Could not retrieve next part of adventure.", true);
    }

    // Ensure autoscrolling after the message is shown
    log.scrollTop = log.scrollHeight;
}

// Function to append messages to game log
function appendToGameLog(text, isAI = true) {
    const log = document.getElementById('game-log');
    if (isAI) {
        typeWriterEffect(text, log);
    } else {
        const div = document.createElement("div");
        div.innerHTML = text;
        log.appendChild(div);
        log.scrollTop = log.scrollHeight; // Autoscroll after each user message
    }
}

// Function to simulate the typewriter effect
function typeWriterEffect(text, container, delay = 30) {
    let i = 0;
    const div = document.createElement("div");

    function type() {
        if (i < text.length) {
            div.innerHTML += text.charAt(i);
            i++;
            setTimeout(type, delay);
        } else {
            // Once AI is done typing, scroll to the bottom
            container.scrollTop = container.scrollHeight;

            // Now show the options after typing is complete
            showOptions();
        }
    }

    container.appendChild(div);
    type();
}

// Function to display the options after typing is done
function showOptions() {
    const optionsContainer = document.getElementById('options-container');
    const log = document.getElementById('game-log');

    // Ensure optionsContainer is visible after AI finishes typing
    optionsContainer.style.display = 'flex';

    // Autoscroll to bottom after options are shown
    log.scrollTop = log.scrollHeight;
}


window.addEventListener('load', () => {
    const log = document.getElementById('game-log');
    appendToGameLog("🤖 Dungeon Master: Welcome, adventurer! Let’s begin your journey...", true);
    createCharacter();
});
