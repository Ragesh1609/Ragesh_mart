<div id="chatWidget" class="chat-widget">
    <button id="chatToggleBtn" class="chat-toggle-btn" aria-label="Open chat">💬</button>
    <div id="chatPanel" class="chat-panel hidden">
        <div class="chat-header">RageshMart Assistant</div>
        <div id="chatMessages" class="chat-messages"></div>
        <form id="chatForm" class="chat-form">
            <input type="text" id="chatInput" maxlength="500" placeholder="Ask about products or orders..." autocomplete="off">
            <button type="submit">Send</button>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
