const cartList = document.getElementById("cartList");
const cartTotalEl = document.getElementById("cartTotal");
const checkoutModal = document.getElementById("checkoutModal");

async function loadCart() {
    if (!cartList) return;
    try {
        const { items, total } = await apiFetch("/api/v1/cart");
        renderCart(items, total);
    } catch (err) {
        cartList.innerHTML = `<p class="form-error">${escapeHtml(err.message)}</p>`;
    }
}

function renderCart(items, total) {
    if (!items.length) {
        cartList.innerHTML = "<p>Your cart is empty.</p>";
        cartTotalEl.textContent = "";
        return;
    }
    cartList.innerHTML = items.map((item) => `
        <div class="cart-item">
            <div>
                <strong>${escapeHtml(item.productName)}</strong><br>
                ${formatCurrency(item.unitPrice)} x
                <input type="number" min="1" value="${item.quantity}" data-id="${item.id}" class="qtyInput" style="width:50px">
            </div>
            <div>
                ${formatCurrency(item.lineTotal ?? (item.unitPrice * item.quantity))}
                <button data-id="${item.id}" class="removeBtn">Remove</button>
            </div>
        </div>
    `).join("");
    cartTotalEl.textContent = "Total: " + formatCurrency(total);

    document.querySelectorAll(".qtyInput").forEach((input) => {
        input.addEventListener("change", async () => {
            try {
                await apiFetch("/api/v1/cart/" + input.dataset.id, {
                    method: "PUT",
                    body: JSON.stringify({ quantity: Number(input.value) }),
                });
                loadCart();
            } catch (err) {
                alert(err.message);
            }
        });
    });

    document.querySelectorAll(".removeBtn").forEach((btn) => {
        btn.addEventListener("click", async () => {
            try {
                await apiFetch("/api/v1/cart/" + btn.dataset.id, { method: "DELETE" });
                loadCart();
            } catch (err) {
                alert(err.message);
            }
        });
    });
}

document.getElementById("checkoutBtn")?.addEventListener("click", () => {
    checkoutModal.classList.remove("hidden");
});

document.getElementById("cancelPaymentBtn")?.addEventListener("click", () => {
    checkoutModal.classList.add("hidden");
});

document.getElementById("confirmPaymentBtn")?.addEventListener("click", async () => {
    try {
        await apiFetch("/api/v1/checkout", {
            method: "POST",
            body: JSON.stringify({ mockPaymentConfirmed: true }),
        });
        checkoutModal.classList.add("hidden");
        alert("Order placed successfully!");
        window.location.href = "orders.jsp";
    } catch (err) {
        alert(err.message);
    }
});

loadCart();
