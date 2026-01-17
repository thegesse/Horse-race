// Updates the UI. 'currentLength' ensures the percentage is calculated correctly
function renderHorses(horses, currentLength = 440) {
    const container = document.getElementById("horse-container");
    const betDropdown = document.getElementById("betHorseId");

    if (betDropdown && betDropdown.options.length <= 1) {
        horses.forEach(horse => {
            const opt = document.createElement("option");
            opt.value = horse.id;
            opt.text = horse.name;
            betDropdown.add(opt);
        });
    }

    container.innerHTML = "";

    horses.forEach(horse => {
        const div = document.createElement("div");
        div.className = "horse-card";
        const progress = horse.lengthRan || 0;
        const percentage = Math.min((progress / currentLength) * 100, 100);

        div.innerHTML = `
            <div class="horse-info">
                <strong>${horse.name}</strong> 
                <span>Speed: ${horse.speed}</span>
            </div>
            <div class="progress-bar">
                <div class="progress-fill" style="width: ${percentage}%"></div>
            </div>
        `;
        container.appendChild(div);
    });
}

async function displayRacers() {
    try {
        const response = await fetch(`/racers`, { method: "GET" });
        const data = await response.json();
        // Get length from input to keep percentage accurate
        const length = document.getElementById("raceDist").value || 440;
        renderHorses(data, length);
    } catch (error) {
        console.error("Fetch error: " + error);
    }
}

async function renderRace(length) {
    try {
        // 1. Force all bars to 0% immediately
        const responseInitial = await fetch(`/racers`, { method: "GET" });
        const initialData = await responseInitial.json();
        const resetData = initialData.map(h => ({ ...h, lengthRan: 0 }));
        renderHorses(resetData, length);

        // 2. We use 'requestAnimationFrame' to ensure the browser
        // actually renders the horses at 0% before we start the race.
        window.requestAnimationFrame(() => {
            window.requestAnimationFrame(async () => {

                // 3. Trigger the actual race calculation in Java
                const response = await fetch(`/race/${length}`, { method: "POST" });
                await response.json();

                // 4. Update to the finished positions
                // The CSS transition (5s) will now take over and slide them slowly
                await displayRacers();

                // 5. Wait for the 5-second animation to finish before showing the result
                setTimeout(() => {
                    checkBetStatus();
                }, 5100);
            });
        });

    } catch (error) {
        console.error("Race error: " + error);
    }
}

async function checkBetStatus() {
    try {
        const response = await fetch('/bet-status');
        const bet = await response.json();
        if (bet) {
            if (bet.won) {
                alert(`WINNER! Horse ID ${bet.horseId} took 1st place!`);
            } else {
                alert("You lost! Better luck next time.");
            }
        }
    } catch (error) {
        console.log("No active bet.");
    }
}

async function handleStartRace() {
    const length = document.getElementById("raceDist").value || 440;
    await renderRace(length);
}

async function placeBet() {
    const select = document.getElementById("betHorseId");
    const id = select.value;
    const amount = document.getElementById("betAmount").value;

    if (!id || !amount) {
        alert("Please select a horse and amount!");
        return;
    }

    const response = await fetch(`/gamble/${id}/${amount}`, { method: "POST" });
    const message = await response.text();
    alert(message);
}

async function resetGame() {
    await fetch('/reset', { method: "POST" });
    const select = document.getElementById("betHorseId");
    if (select) select.innerHTML = '<option value="">Select a Horse</option>';
    displayRacers();
}

// Initial Load
displayRacers();