// dndScript.js - Merged version with API key modal + your custom features

// ============================================================================
// MODAL ELEMENTS AND API KEY MANAGEMENT
// ============================================================================
const apiKeyModal = document.getElementById("api-key-modal");
const gameContainer = document.getElementById("game-container");
const apiKeyInput = document.getElementById("api-key-input");
const startGameBtn = document.getElementById("start-game-button");
const demoModeBtn = document.getElementById("demo-mode-button");
const showKeyCheckbox = document.getElementById("show-key-checkbox");
const modalErrorMessage = document.getElementById("modal-error-message");
const modalSuccessMessage = document.getElementById("modal-success-message");

let apiKey = null;
let sessionId = null;
let demoMode = false;

// ============================================================================
// YOUR CUSTOM GAME VARIABLES
// ============================================================================
let characterCreated = false;
let characterInfo = {};
let currentChoices = [];
let currentBackground = 'cave'; // Default initial background

const dmThinkingLines = [
    "The DM strokes his long, silver beard, eyes flickering like candlelight in deep thought",
    "The DM taps his gnarled staff rhythmically against the stone floor, lost in ancient contemplation",
    "The DM leans back in his worn oak chair, gaze wandering through invisible realms of possibility",
    "The DM narrows his eyes, as though reading fate's hidden script upon the air itself",
    "The DM mutters something unintelligible, consulting memories older than the kingdom's founding",
    "The DM tilts his head, as if listening to a whisper carried from the other side of the veil",
    "The DM adjusts the brim of his wide hat, shadows dancing across his thoughtful face",
    "The DM gazes into a swirling crystal orb, seeking wisdom in its ever-changing depths",
    "The DM runs a hand through his beard, each strand a tangled tale yet untold",
    "The DM closes his eyes for a moment, breathing in the scent of old parchment and prophecy",
    "The DM's fingers hover over an invisible tome, flipping pages in the library of his mind",
    "The DM offers a knowing smile, lost for a breath in some secret scheme or half-formed plan",
    "The DM's eyes flash like lightning behind storm clouds, as if struck by a sudden insight",
    "The DM casts his gaze toward the unseen horizon, watching destinies weave and unravel",
    "The DM whispers a forgotten word, and for a moment, the world seems to wait with him",
    "The DM raises an eyebrow, a soft chuckle escaping as he considers your curious fate",
    "The DM lifts a finger to the sky, as if plucking ideas from the constellations themselves",
    "The DM sighs—a sound like pages turning in a forgotten tome—as he weighs your path ahead",
    "The DM traces glowing symbols in the air, each one a fragment of thought crystallizing",
    "The DM consults a raven perched on his shoulder, nodding slowly at its silent counsel",
    "The DM squints into the ether, as if watching time unravel and reweave before his eyes",
    "The DM strokes his beard with deliberate care, each movement steeped in deliberate thought",
    "The DM whispers to the shadows behind his throne, considering what they whisper in return",
    "The DM twirls a quill between his fingers, sketching thoughts across the fabric of fate",
    "The DM cups his hands around an unseen flame, meditating on the warmth of potential futures",
    "The DM hums an old, arcane tune—a melody only the wise and weary remember",
    "The DM turns slowly toward the fire, watching the embers rise like thoughts made manifest",
    "The DM adjusts the rings on his fingers, each one glowing faintly with possibilities",
    "The DM stares into nothing, where perhaps only he sees the spinning gears of destiny",
    "The DM closes a massive tome with a soft thud, decision glinting in his ancient eyes"
];

// ============================================================================
// API KEY MODAL FUNCTIONS
// ============================================================================

// Generate a unique session ID
function generateSessionId() {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

// Validate API key format
function validateApiKey(key) {
    if (!key || key.trim() === '') {
        return { valid: false, message: 'Please enter an API key' };
    }
    if (key.length < 20) {
        return { valid: false, message: 'API key seems too short. Please check and try again.' };
    }
    return { valid: true };
}

// Hide modal and show game
function hideModalAndShowGame() {
    apiKeyModal.classList.add('hidden');
    gameContainer.classList.remove('hidden');
}

// Check for existing API key on page load
function checkExistingApiKey() {
    const existingKey = sessionStorage.getItem('mistral_api_key');
    const existingDemoMode = sessionStorage.getItem('demo_mode') === 'true';

    if (existingKey && !existingDemoMode) {
        apiKey = existingKey;
        apiKeyInput.value = existingKey;
        modalSuccessMessage.textContent = '✓ API Key found in session';
        modalSuccessMessage.classList.remove('hidden');
        modalErrorMessage.classList.add('hidden');
    } else if (existingDemoMode) {
        demoMode = true;
        hideModalAndShowGame();
        initializeGame();
    }
}

// Modal: Toggle password visibility
showKeyCheckbox.addEventListener('change', (e) => {
    apiKeyInput.type = e.target.checked ? 'text' : 'password';
});

// Modal: Start game with API key
startGameBtn.addEventListener('click', () => {
    const key = apiKeyInput.value.trim();
    const validation = validateApiKey(key);

    if (!validation.valid) {
        modalErrorMessage.textContent = validation.message;
        modalErrorMessage.classList.remove('hidden');
        modalSuccessMessage.classList.add('hidden');
        return;
    }

    // Store API key in sessionStorage
    sessionStorage.setItem('mistral_api_key', key);
    sessionStorage.removeItem('demo_mode');
    apiKey = key;
    demoMode = false;

    // Show success message
    modalSuccessMessage.textContent = '✓ API Key saved! Starting adventure...';
    modalSuccessMessage.classList.remove('hidden');
    modalErrorMessage.classList.add('hidden');
    startGameBtn.disabled = true;

    // Hide modal and show game
    setTimeout(() => {
        hideModalAndShowGame();
        initializeGame();
        startGameBtn.disabled = false;
    }, 800);
});

// Modal: Demo mode
demoModeBtn.addEventListener('click', () => {
    sessionStorage.setItem('demo_mode', 'true');
    sessionStorage.removeItem('mistral_api_key');
    demoMode = true;
    apiKey = null;

    hideModalAndShowGame();
    initializeGame();
});

// Modal: Allow Enter key to submit
apiKeyInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        startGameBtn.click();
    }
});

// ============================================================================
// GAME INITIALIZATION
// ============================================================================

function initializeGame() {
    // Generate session ID
    sessionId = generateSessionId();
    console.log('Session initialized:', sessionId);

    if (demoMode) {
        appendToGameLog("⚠️ DEMO MODE: You're in demo mode. Set up your API key to unlock the full experience!", true, false);
    }

    // Start your custom character creation flow
    appendToGameLog("🧙‍♂️ Ohh, hello traveller! Let's begin thy journey...", true);
    createCharacter();
}

// ============================================================================
// YOUR CUSTOM GAME FUNCTIONS (with API key integration)
// ============================================================================

function getRandomDmThinkingLine() {
    return dmThinkingLines[Math.floor(Math.random() * dmThinkingLines.length)];
}

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

                // Only scroll if user is near the bottom (less than 100px from bottom)
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

    const randomLine = getRandomDmThinkingLine();
    thinkingDiv.innerHTML = `<span class="dots">🧙‍♂️ ${randomLine}</span>`;

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

            // Hide the input bar
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

    const classes = ['Warrior', 'Mage', 'Rogue', 'Cleric'];
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

    // Check for demo mode
    if (demoMode) {
        appendToGameLog("⚠️ Demo mode is active. Please set up your API key to continue your adventure!", true, false);

        const optionsContainer = document.getElementById('options-container');
        optionsContainer.innerHTML = '';
        const setupButton = document.createElement("button");
        setupButton.className = "button";
        setupButton.textContent = "Set Up API Key";
        setupButton.onclick = () => {
            sessionStorage.removeItem('demo_mode');
            location.reload();
        };
        optionsContainer.appendChild(setupButton);
        return;
    }

    showThinking();

    try {
        const response = await fetch('http://localhost:8080/chat/send', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userMessage: 'Start the adventure',
                apiKey: apiKey,
                sessionId: sessionId
            })
        });

        removeThinking();

        if (response.ok) {
            const data = await response.json();
            console.log("✅ Received message:", data.message);

            updateBackgroundFromMessage(data.message);

            const cleanedMessage = data.message
                .split('\n')
                .filter(line => !/^\d+\.\s/.test(line))
                .join('\n');

            await appendToGameLog(`🧙‍♂️ ${cleanedMessage}`, true);

            const choices = extractChoicesFromMessage(data.message);
            currentChoices = choices;
            updateOptionsFromResponse(choices);
        } else {
            const data = await response.json();
            appendToGameLog("⚠️ Error: " + (data.message || "Could not retrieve adventure."), true, false);

            // If API key error, offer to reconfigure
            if (data.message && data.message.includes("API key")) {
                const optionsContainer = document.getElementById('options-container');
                const reconfigButton = document.createElement("button");
                reconfigButton.className = "button";
                reconfigButton.textContent = "Update API Key";
                reconfigButton.onclick = () => {
                    sessionStorage.removeItem('mistral_api_key');
                    location.reload();
                };
                optionsContainer.appendChild(reconfigButton);
            }
        }
    } catch (error) {
        removeThinking();
        console.error("Error:", error);
        appendToGameLog("⚠️ Connection error. Please check your internet connection and try again.", true, false);
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
    optionsContainer.innerHTML = '';

    // Limit to a maximum of 4 choices
    const limitedChoices = choices.slice(0, 4);

    limitedChoices.forEach((choice, index) => {
        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = `${index + 1}. ${choice.text}`;
        button.onclick = () => handleUserChoice(index + 1, choice.text);
        optionsContainer.appendChild(button);
    });
}

function updateBackgroundFromMessage(message) {
    const trimmed = message.trim();
    const match = trimmed.match(/#(tavern|castle|cave|alley|forest|inn|market|shop|blacksmith|dungeon|ruins|plains|clearing|lake|altar)\b/i);
    if (!match) {
        console.log("🚫 No valid background tag found.");
        return;
    }

    const scene = match[1].toLowerCase();

    // Check if the scene is already the current one
    if (scene === currentBackground) {
        console.log(`🛑 Background already set to "${scene}", skipping transition.`);
        return;
    }

    currentBackground = scene; // Update the current scene

    const newBg = `url('/${scene}.gif')`;
    const fadeTime = 2000;
    const pauseBetween = 1000;
    const bgEl = document.getElementById("background-layer");

    console.log("🔹 Step 1: Start fade out");
    bgEl.classList.add("fade-out");

    setTimeout(() => {
        console.log("🔹 Step 2: Switch image");
        bgEl.style.backgroundImage = newBg;

        setTimeout(() => {
            console.log("🔹 Step 3: Fade back in");
            bgEl.classList.remove("fade-out");

            setTimeout(() => {
                console.log("✅ Step 4: Done!");
            }, fadeTime);

        }, pauseBetween);

    }, fadeTime);
}

async function handleUserChoice(choiceIndex, choiceText) {
    // Show choice and remove options
    await appendToGameLog(`🧝 ${choiceText}`, false, false);

    // Remove all buttons
    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = '';

    // Check for demo mode
    if (demoMode) {
        appendToGameLog("⚠️ Demo mode is active. Please set up your API key to continue!", true, false);

        const setupButton = document.createElement("button");
        setupButton.className = "button";
        setupButton.textContent = "Set Up API Key";
        setupButton.onclick = () => {
            sessionStorage.removeItem('demo_mode');
            location.reload();
        };
        optionsContainer.appendChild(setupButton);
        return;
    }

    showThinking();

    try {
        const response = await fetch('http://localhost:8080/chat/send', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userMessage: `I choose option ${choiceIndex}`,
                apiKey: apiKey,
                sessionId: sessionId
            })
        });

        removeThinking();

        if (response.ok) {
            const data = await response.json();
            console.log("✅ Received message:", data.message);

            updateBackgroundFromMessage(data.message);

            const cleanedMessage = data.message
                .split('\n')
                .filter(line => !/^\d+\.\s/.test(line))
                .join('\n');

            await appendToGameLog(`🧙‍♂️ ${cleanedMessage}`, true);

            const choices = extractChoicesFromMessage(data.message);
            currentChoices = choices;
            updateOptionsFromResponse(choices);
        } else {
            const data = await response.json();
            appendToGameLog("⚠️ Error: " + (data.message || "Could not retrieve next part of adventure."), true, false);

            // If API key error, offer to reconfigure
            if (data.message && data.message.includes("API key")) {
                const reconfigButton = document.createElement("button");
                reconfigButton.className = "button";
                reconfigButton.textContent = "Update API Key";
                reconfigButton.onclick = () => {
                    sessionStorage.removeItem('mistral_api_key');
                    location.reload();
                };
                optionsContainer.appendChild(reconfigButton);
            }
        }
    } catch (error) {
        removeThinking();
        console.error("Error:", error);
        appendToGameLog("⚠️ Connection error. Please check your internet connection and try again.", true, false);
    }
}

// Add reset button functionality
function addResetButton() {
    const resetBtn = document.createElement("button");
    resetBtn.className = "button";
    resetBtn.textContent = "🔄 Reset";
    resetBtn.style.position = "absolute";
    resetBtn.style.top = "10px";
    resetBtn.style.right = "10px";
    resetBtn.style.fontSize = "0.65rem";
    resetBtn.style.padding = "0.4rem 0.6rem";
    resetBtn.onclick = async () => {
        if (confirm("Reset your adventure?")) {
            try {
                await fetch("http://localhost:8080/chat/reset", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        sessionId: sessionId
                    }),
                });
            } catch (error) {
                console.error("Error resetting session:", error);
            }
            location.reload();
        }
    };
    gameContainer.appendChild(resetBtn);
}

// ============================================================================
// PAGE LOAD INITIALIZATION
// ============================================================================

window.addEventListener('load', () => {
    console.log("🧪 JS is running");
    checkExistingApiKey();
    addResetButton();
});