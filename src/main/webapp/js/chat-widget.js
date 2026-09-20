(function () {
    const toggleBtn = document.getElementById("chatToggleBtn");
    const panel = document.getElementById("chatPanel");
    const form = document.getElementById("chatForm");
    const input = document.getElementById("chatInput");
    const messages = document.getElementById("chatMessages");

    if (!toggleBtn) return;

    toggleBtn.addEventListener("click", () => panel.classList.toggle("hidden"));

    function addMessage(text, who) {
        const div = document.createElement("div");
        div.className = who === "user" ? "chat-msg-user" : "chat-msg-bot";
        div.textContent = text; // textContent, never innerHTML - avoids XSS from chat content
        messages.appendChild(div);
        messages.scrollTop = messages.scrollHeight;
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const message = input.value.trim();
        if (!message) return;
        addMessage(message, "user");
        input.value = "";

        try {
            const resp = await fetch("api/chat", {
                method: "POST",
                credentials: "same-origin",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ message }),
            });
            const body = await resp.json();
            if (body.success) {
                addMessage(body.data.reply, "bot");
            } else {
                addMessage(body.error?.message || "Sorry, something went wrong.", "bot");
            }
        } catch (err) {
            addMessage("Sorry, the assistant is temporarily unavailable.", "bot");
        }
    });
})();
