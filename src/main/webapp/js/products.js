const productGrid = document.getElementById("productGrid");

async function loadProducts() {
    if (!productGrid) return;
    const keyword = document.getElementById("searchInput")?.value || "";
    const category = document.getElementById("categoryFilter")?.value || "";
    const params = new URLSearchParams();
    if (keyword) params.set("keyword", keyword);
    if (category) params.set("category", category);

    try {
        const products = await apiFetch("/api/v1/products?" + params.toString());
        renderProducts(products);
    } catch (err) {
        productGrid.innerHTML = `<p class="form-error">${escapeHtml(err.message)}</p>`;
    }
}

function renderProducts(products) {
    if (!products.length) {
        productGrid.innerHTML = "<p>No products found.</p>";
        return;
    }
    productGrid.innerHTML = products.map((p) => `
        <div class="product-card">
            <img src="${escapeHtml(p.imageUrl || 'https://placehold.co/300x200?text=No+Image')}" alt="${escapeHtml(p.name)}">
            <h3>${escapeHtml(p.name)}</h3>
            <p>${escapeHtml(p.description || "")}</p>
            <p class="price">${formatCurrency(p.price)}</p>
            <p>Stock: ${escapeHtml(p.stockQty)}</p>
            <button data-id="${p.id}" class="addToCartBtn primary-btn">Add to Cart</button>
        </div>
    `).join("");

    document.querySelectorAll(".addToCartBtn").forEach((btn) => {
        btn.addEventListener("click", async () => {
            try {
                await apiFetch("/api/v1/cart", {
                    method: "POST",
                    body: JSON.stringify({ productId: Number(btn.dataset.id), quantity: 1 }),
                });
                alert("Added to cart!");
            } catch (err) {
                alert(err.message);
            }
        });
    });
}

document.getElementById("searchBtn")?.addEventListener("click", loadProducts);
loadProducts();
