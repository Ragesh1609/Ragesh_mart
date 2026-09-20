const adminContent = document.getElementById("adminContent");
const tabUsers = document.getElementById("tabUsers");
const tabOrders = document.getElementById("tabOrders");
let adminView = "users";

async function loadAdmin() {
    if (!adminContent) return;
    try {
        if (adminView === "users") {
            const users = await apiFetch("/api/v1/admin/users");
            renderUsers(users);
        } else {
            const orders = await apiFetch("/api/v1/admin/orders");
            renderOrders(orders);
        }
    } catch (err) {
        adminContent.innerHTML = `<p class="form-error">${escapeHtml(err.message)}</p>`;
    }
}

function renderUsers(users) {
    adminContent.innerHTML = `
        <table style="width:100%; border-collapse:collapse">
            <tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th></tr>
            ${users.map(u => `
                <tr>
                    <td>${u.id}</td>
                    <td>${escapeHtml(u.name)}</td>
                    <td>${escapeHtml(u.email)}</td>
                    <td>${escapeHtml(u.role)}</td>
                </tr>`).join("")}
        </table>`;
}

function renderOrders(orders) {
    if (!orders.length) {
        adminContent.innerHTML = "<p>No orders yet.</p>";
        return;
    }
    adminContent.innerHTML = orders.map((o) => `
        <div class="form-card" style="margin-bottom:1rem">
            <div><strong>Order #${o.id}</strong> — Buyer ${o.buyerId} — ${formatCurrency(o.totalAmount)} — ${escapeHtml(o.status)}</div>
            <ul>${o.items.map(i => `<li>${escapeHtml(i.productName)} x ${i.quantity}
                <button class="removeListingBtn" data-id="${i.productId}">Remove listing</button></li>`).join("")}</ul>
        </div>
    `).join("");

    document.querySelectorAll(".removeListingBtn").forEach((btn) => {
        btn.addEventListener("click", async () => {
            if (!confirm("Remove this product listing site-wide?")) return;
            try {
                await apiFetch("/api/v1/admin/products/" + btn.dataset.id, { method: "DELETE" });
                alert("Listing removed.");
            } catch (err) {
                alert(err.message);
            }
        });
    });
}

tabUsers?.addEventListener("click", () => {
    adminView = "users";
    tabUsers.classList.add("active");
    tabOrders.classList.remove("active");
    loadAdmin();
});

tabOrders?.addEventListener("click", () => {
    adminView = "orders";
    tabOrders.classList.add("active");
    tabUsers.classList.remove("active");
    loadAdmin();
});

loadAdmin();
