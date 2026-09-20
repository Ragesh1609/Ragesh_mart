// Shared helpers used by every page. All requests go through this wrapper so
// cookies (session) are always sent and the fixed API envelope is unwrapped consistently.
const API_BASE = ""; // same-origin

async function apiFetch(path, options = {}) {
    const resp = await fetch(API_BASE + path, {
        credentials: "same-origin",
        headers: { "Content-Type": "application/json", ...(options.headers || {}) },
        ...options,
    });
    let body;
    try {
        body = await resp.json();
    } catch (e) {
        body = { success: false, error: { message: "Unexpected server response" } };
    }
    if (!resp.ok || body.success === false) {
        const message = body.error ? body.error.message : "Request failed";
        throw new Error(message);
    }
    return body.data;
}

function escapeHtml(str) {
    const div = document.createElement("div");
    div.textContent = str == null ? "" : String(str);
    return div.innerHTML;
}

function formatCurrency(amount) {
    return "₹" + Number(amount).toFixed(2);
}
