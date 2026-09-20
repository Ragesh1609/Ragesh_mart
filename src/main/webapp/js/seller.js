const productForm = document.getElementById("productForm");
const myListings = document.getElementById("myListings");

async function loadMyListings() {
    if (!myListings) return;
    try {
        const products = await apiFetch("/api/v1/products?mine=true");
        renderListings(products);
    } catch (err) {
        myListings.innerHTML = `<p class="form-error">${escapeHtml(err.message)}</p>`;
    }
}

function renderListings(products) {
    if (!products.length) {
        myListings.innerHTML = "<p>No listings yet.</p>";
        return;
    }
    myListings.innerHTML = products.map((p) => `
        <div class="product-card">
            <h3>${escapeHtml(p.name)}</h3>
            <p class="price">${formatCurrency(p.price)}</p>
            <p>Stock: ${escapeHtml(p.stockQty)}</p>
            <button class="editBtn" data-id="${p.id}">Edit</button>
            <button class="deleteBtn" data-id="${p.id}">Delete</button>
        </div>
    `).join("");

    document.querySelectorAll(".editBtn").forEach((btn) => {
        btn.addEventListener("click", async () => {
            const product = await apiFetch("/api/v1/products/" + btn.dataset.id);
            document.getElementById("productId").value = product.id;
            document.getElementById("pName").value = product.name;
            document.getElementById("pDescription").value = product.description || "";
            document.getElementById("pPrice").value = product.price;
            document.getElementById("pStock").value = product.stockQty;
            document.getElementById("pCategory").value = product.category || "";
            document.getElementById("pImageUrl").value = product.imageUrl || "";
        });
    });

    document.querySelectorAll(".deleteBtn").forEach((btn) => {
        btn.addEventListener("click", async () => {
            if (!confirm("Delete this listing?")) return;
            try {
                await apiFetch("/api/v1/products/" + btn.dataset.id, { method: "DELETE" });
                loadMyListings();
            } catch (err) {
                alert(err.message);
            }
        });
    });
}

productForm?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("productId").value;
    const payload = {
        name: document.getElementById("pName").value,
        description: document.getElementById("pDescription").value,
        price: Number(document.getElementById("pPrice").value),
        stockQty: Number(document.getElementById("pStock").value),
        category: document.getElementById("pCategory").value,
        imageUrl: document.getElementById("pImageUrl").value,
    };
    try {
        if (id) {
            await apiFetch("/api/v1/products/" + id, { method: "PUT", body: JSON.stringify(payload) });
        } else {
            await apiFetch("/api/v1/products", { method: "POST", body: JSON.stringify(payload) });
        }
        productForm.reset();
        document.getElementById("productId").value = "";
        loadMyListings();
    } catch (err) {
        alert(err.message);
    }
});

loadMyListings();
