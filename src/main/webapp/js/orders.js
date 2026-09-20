const ordersList = document.getElementById("ordersList");
const tabBuyer = document.getElementById("tabBuyer");
const tabSeller = document.getElementById("tabSeller");
let currentView = "buyer";

async function loadOrders() {
    if (!ordersList) return;
    const qs = currentView === "seller" ? "?as=seller" : "";
    try {
        const orders = await apiFetch("/api/v1/orders" + qs);
        renderOrders(orders);
    } catch (err) {
        ordersList.innerHTML = `<p class="form-error">${escapeHtml(err.message)}</p>`;
    }
}

function renderOrders(orders) {
    if (!orders.length) {
        ordersList.innerHTML = "<p>No orders found.</p>";
        return;
    }
    ordersList.innerHTML = orders.map((o) => `
        <div class="form-card" style="margin-bottom:1rem">
            <div><strong>Order #${o.id}</strong> — ${formatCurrency(o.totalAmount)}</div>
            <div>Status:
                ${currentView === "seller"
                    ? `<select class="statusSelect" data-id="${o.id}">
                        ${["PENDING","CONFIRMED","SHIPPED","DELIVERED","CANCELLED"]
                            .map(s => `<option value="${s}" ${s === o.status ? "selected" : ""}>${s}</option>`).join("")}
                       </select>`
                    : escapeHtml(o.status)}
            </div>
            <ul>
                ${o.items.map(i => `<li>${escapeHtml(i.productName)} x ${i.quantity} @ ${formatCurrency(i.unitPrice)}</li>`).join("")}
            </ul>
        </div>
    `).join("");

    document.querySelectorAll(".statusSelect").forEach((sel) => {
        sel.addEventListener("change", async () => {
            try {
                await apiFetch(`/api/v1/orders/${sel.dataset.id}/status`, {
                    method: "PUT",
                    body: JSON.stringify({ status: sel.value }),
                });
            } catch (err) {
                alert(err.message);
                loadOrders();
            }
        });
    });
}

tabBuyer?.addEventListener("click", () => {
    currentView = "buyer";
    tabBuyer.classList.add("active");
    tabSeller.classList.remove("active");
    loadOrders();
});

tabSeller?.addEventListener("click", () => {
    currentView = "seller";
    tabSeller.classList.add("active");
    tabBuyer.classList.remove("active");
    loadOrders();
});

loadOrders();
