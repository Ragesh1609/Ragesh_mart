const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const errorEl = document.getElementById("loginError");
        errorEl.textContent = "";
        try {
            await apiFetch("/api/v1/auth/login", {
                method: "POST",
                body: JSON.stringify({ email, password }),
            });
            window.location.href = "index.jsp";
        } catch (err) {
            errorEl.textContent = err.message;
        }
    });
}

const registerForm = document.getElementById("registerForm");
if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const role = document.getElementById("role").value;
        const errorEl = document.getElementById("registerError");
        errorEl.textContent = "";
        try {
            await apiFetch("/api/v1/auth/register", {
                method: "POST",
                body: JSON.stringify({ name, email, password, role }),
            });
            window.location.href = "login.jsp";
        } catch (err) {
            errorEl.textContent = err.message;
        }
    });
}
