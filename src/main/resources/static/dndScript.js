let characterCreated = false;
let characterInfo = {};
let currentChoices = [];

async function appendToGameLog(text, isDM = false, useTyping = true) {
    const log = document.getElementById('game-log');
    const div = document.createElement("div");
    div.className = isDM ? 'dm-line' : 'chat-line';
    log.appendChild(div);

    if (useTyping) {
        await typeWriterEffect(text, div, 20);
    } else {
        div.innerHTML = text.replace(/\n/g, "<br>");
        log.scrollTo({
            top: log.scrollHeight,
            behavior: 'smooth'
        });
    }
}

function typeWriterEffect(text, element, delay = 20) {
    return new Promise(resolve => {
        let i = 0;

        function type() {
            if (i < text.length) {
                const char = text.charAt(i);
                element.innerHTML += char === "\n" ? "<br>" : char;
                i++;

                const log = document.getElementById('game-log');

                // 🔽 Kun scroll hvis brugeren er tæt på bunden (fx mindre end 100px fra)
                const isAtBottom = log.scrollHeight - log.scrollTop - log.clientHeight < 100;
                if (isAtBottom) {
                    log.scrollTo({
                        top: log.scrollHeight,
                        behavior: 'smooth'
                    });
                }

                setTimeout(type, delay);
            } else {
                resolve();
            }
        }

        type();
    });
}

function showThinking() {
    const log = document.getElementById('game-log');
    const thinkingDiv = document.createElement("div");
    thinkingDiv.id = "thinking";
    thinkingDiv.className = 'dm-line italic text-yellow-300';
    thinkingDiv.innerHTML = `🧙‍♂️ <span class="dots">Thinking</span>`;
    log.appendChild(thinkingDiv);
    log.scrollTo({ top: log.scrollHeight, behavior: 'smooth' });
}

function removeThinking() {
    const existing = document.getElementById("thinking");
    if (existing) existing.remove();
}

async function createCharacter() {
    const userInput = document.getElementById('user-input');
    const sendButton = document.getElementById('send-button');

    appendToGameLog("🧙‍♂️ So.. traveller, who are thy? What is thy name?", true);
    userInput.focus();

    sendButton.onclick = async function () {
        const nameInput = userInput.value.trim();
        if (nameInput && !characterCreated) {
            characterInfo.name = nameInput;
            appendToGameLog(`🧝 ${nameInput}`);
            userInput.value = '';
            characterCreated = true;

            // 👇 HIDE the input bar here
            document.getElementById('input-container').style.display = 'none';

            await chooseClass();
        }
    };

    userInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            sendButton.click();
        }
    });
}

async function chooseClass() {
    appendToGameLog(`🧙‍♂️ Great, ${characterInfo.name}! Choose your class:`, true);
    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = '';

    const classes = ['Warrior', 'Mage', 'Rogue', 'Healer'];
    classes.forEach((className, idx) => {
        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = `${idx + 1}. ${className}`;
        button.onclick = () => {
            characterInfo.class = className;
            appendToGameLog(`🧝 ${className}`);
            optionsContainer.innerHTML = '';
            startAdventure();
        };
        optionsContainer.appendChild(button);
    });
}

async function startAdventure() {
    appendToGameLog(`🧙‍♂️ Welcome, ${characterInfo.name} the ${characterInfo.class}! Your adventure begins...`, true);

    showThinking();
    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: 'Start the adventure' })
    });
    removeThinking();

    if (response.ok) {
        const data = await response.json();
        const cleanedMessage = data.message
            .split('\n')
            .filter(line => !/^\d+\.\s/.test(line))
            .join('\n');

        await appendToGameLog(`🧙‍♂️ ${cleanedMessage}`, true);

        const choices = extractChoicesFromMessage(data.message);

        currentChoices = choices;
        updateOptionsFromResponse(choices);
    } else {
        appendToGameLog("⚠️ Error: Could not retrieve adventure.", true);
    }
}

function extractChoicesFromMessage(message) {
    return message.split('\n')
        .filter(line => /^\d+\./.test(line))
        .map(line => {
            const text = line.replace(/^\d+\.\s*/, '').trim();
            return { text };
        });
}

function updateOptionsFromResponse(choices) {
    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = ''; // Fjern gamle valgmuligheder

    // Tilføj nye valgmuligheder (og deaktivér gamle knapper, hvis du ønsker det)
    choices.forEach((choice, index) => {
        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = `${index + 1}. ${choice.text}`;
        button.onclick = () => handleUserChoice(index + 1, choice.text);

        // Deaktiver knapperne, der er valgt tidligere
        button.disabled = false; // Hvis du ønsker at deaktivere, kan du gøre det her
        optionsContainer.appendChild(button);
    });
}

async function handleUserChoice(choiceIndex, choiceText) {
    // Vis valget og fjern derefter options
    await appendToGameLog(`🧝 ${choiceText}`, false, false);

    // Fjern valgmulighederne (deaktiver knapper)
    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = ''; // Fjern alle knapper

    showThinking();
    const response = await fetch('http://localhost:8080/chat/adventure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: `I choose option ${choiceIndex}` })
    });
    removeThinking();

    if (response.ok) {
        const data = await response.json();
        const cleanedMessage = data.message
            .split('\n')
            .filter(line => !/^\d+\.\s/.test(line))
            .join('\n');

        await appendToGameLog(`🧙‍♂️ ${cleanedMessage}`, true); // 🧙‍♂️ skal have typewriter-effekt
        const choices = extractChoicesFromMessage(data.message);
        currentChoices = choices;
        updateOptionsFromResponse(choices);
    } else {
        appendToGameLog("⚠️ Error: Could not retrieve next part of adventure.", true);
    }
}

window.addEventListener('load', () => {
    appendToGameLog("🧙‍♂️ Ohh, hello traveller! Let’s begin thy journey...", true);
    createCharacter();
});