<template>
    <div class="ai-chat-container container py-4">
        <h2 class="chat-title mb-4">
            <i class="fas fa-map-marked-alt me-2"></i>
            AI 여행 추천
        </h2>

        <div class="chat-box shadow rounded">
            <div v-if="messages.length === 0" class="empty-state">
                <div class="empty-icon">
                    <i class="fas fa-plane"></i>
                </div>
                <h4>어디로 떠나고 싶으신가요?</h4>
                <p class="text-muted">여행지에 대해 궁금한 점을 입력해 보세요!</p>
                <div class="suggestion-chips">
                    <span class="chip" @click="setSuggestion('서울 근교 당일치기 여행지')">서울 근교 당일치기</span>
                    <span class="chip" @click="setSuggestion('제주도 3박4일 코스')">제주도 3박4일</span>
                    <span class="chip" @click="setSuggestion('부산 맛집 추천')">부산 맛집</span>
                </div>
            </div>

            <div v-for="(msg, index) in messages" :key="index" class="message-wrapper">
                <div v-if="msg.role === 'user'" class="message user-message">
                    <div class="message-content user-content">
                        {{ msg.content }}
                    </div>
                    <div class="message-avatar user-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                </div>
                <div v-else class="message ai-message">
                    <div class="message-avatar ai-avatar">
                        <i class="fas fa-robot"></i>
                    </div>
                    <div class="message-content ai-content">
                        <div v-html="formatMessage(msg.content)"></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="input-section">
            <div class="input-wrapper">
                <input
                    v-model="userInput"
                    @keyup.enter="sendMessage"
                    type="text"
                    class="chat-input"
                    placeholder="예: 서울 근교에서 당일치기 자연 여행지 알려줘"
                />
                <button
                    class="send-button"
                    @click="sendMessage"
                    :disabled="!userInput.trim()"
                >
                    <i class="fas fa-paper-plane"></i>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { getSimpleChat } from '@/lib/api/aichatAPI'
import { marked } from 'marked'

const userInput = ref('')
const messages = ref([])

const formatMessage = (text) => {
    const withLineBreaks = text.replace(/\n/g, '  \n')
    return marked.parse(withLineBreaks)
}

const setSuggestion = (suggestion) => {
    userInput.value = suggestion
}

const sendMessage = async () => {
    const message = userInput.value.trim()
    if (!message) return

    messages.value.push({ role: 'user', content: message })

    try {
        const aiMessage = await getSimpleChat(message)
        messages.value.push({ role: 'ai', content: aiMessage || 'AI의 응답이 없습니다.' })
    } catch (err) {
        console.error('AI 요청 오류:', err)
        messages.value.push({
            role: 'ai',
            content: '죄송합니다. 서버 오류가 발생했어요 😥',
        })
    }

    userInput.value = ''
}
</script>

<style scoped>
.ai-chat-container {
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem 1rem;
}

.chat-title {
    text-align: center;
    color: #2c3e50;
    font-weight: 700;
    font-size: 2rem;
    margin-bottom: 2rem;
}

.chat-box {
    min-height: 500px;
    max-height: 600px;
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-radius: 20px;
    padding: 2rem;
    overflow-y: auto;
    margin-bottom: 2rem;
    border: 1px solid #dee2e6;
}

/* 빈 상태 스타일 */
.empty-state {
    text-align: center;
    padding: 3rem 1rem;
}

.empty-icon {
    font-size: 4rem;
    color: #007bff;
    margin-bottom: 1.5rem;
}

.empty-state h4 {
    color: #2c3e50;
    margin-bottom: 0.5rem;
}

.suggestion-chips {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.5rem;
    margin-top: 1.5rem;
}

.chip {
    background: #007bff;
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 25px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: all 0.3s ease;
}

.chip:hover {
    background: #0056b3;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3);
}

/* 메시지 스타일 */
.message-wrapper {
    margin-bottom: 1.5rem;
}

.message {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
}

.user-message {
    justify-content: flex-end;
    flex-direction: row-reverse;
}

.ai-message {
    justify-content: flex-start;
}

.message-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    flex-shrink: 0;
}

.user-avatar {
    background: linear-gradient(135deg, #007bff, #0056b3);
    color: white;
}

.ai-avatar {
    background: linear-gradient(135deg, #28a745, #20c997);
    color: white;
}

.message-content {
    max-width: 70%;
    padding: 1rem 1.5rem;
    border-radius: 20px;
    position: relative;
}

.user-content {
    background: linear-gradient(135deg, #007bff, #0056b3);
    color: white;
    border-bottom-right-radius: 8px;
}

.ai-content {
    background: white;
    color: #2c3e50;
    border: 1px solid #e9ecef;
    border-bottom-left-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 입력 섹션 */
.input-section {
    position: sticky;
    bottom: 0;
    background: white;
    padding: 1rem 1rem;
    border-radius: 40px;
    box-shadow: 0 -2px 20px rgba(0, 0, 0, 0.1);
}

.input-wrapper {
    display: flex;
    gap: 1rem;
    align-items: center;
    background: white;
    border: 2px solid #e9ecef;
    border-radius: 50px;
    padding: 0.5rem;
    transition: all 0.3s ease;
}

.input-wrapper:focus-within {
    border-color: #007bff;
    box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.chat-input {
    flex: 1;
    border: none;
    outline: none;
    padding: 1rem 1.5rem;
    font-size: 1rem;
    background: transparent;
}

.chat-input::placeholder {
    color: #6c757d;
}

.send-button {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    border: none;
    background: linear-gradient(135deg, #007bff, #0056b3);
    color: white;
    font-size: 1.2rem;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
}

.send-button:hover:not(:disabled) {
    background: linear-gradient(135deg, #0056b3, #003d82);
    transform: scale(1.05);
    box-shadow: 0 4px 15px rgba(0, 123, 255, 0.4);
}

.send-button:disabled {
    background: #6c757d;
    cursor: not-allowed;
    transform: none;
}

/* AI 메시지 내부 스타일링 */
.ai-content :deep(p) {
    margin: 0 0 0.5rem 0;
    line-height: 1.6;
}

.ai-content :deep(ul),
.ai-content :deep(ol) {
    margin: 0.5rem 0;
    padding-left: 1.5rem;
}

.ai-content :deep(li) {
    margin-bottom: 0.3rem;
}

.ai-content :deep(strong) {
    color: #007bff;
}

.ai-content :deep(h1),
.ai-content :deep(h2),
.ai-content :deep(h3) {
    color: #2c3e50;
    margin: 1rem 0 0.5rem 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
    .ai-chat-container {
        padding: 1rem 0.5rem;
    }

    .chat-title {
        font-size: 1.5rem;
    }

    .message-content {
        max-width: 85%;
    }

    .suggestion-chips {
        flex-direction: column;
        align-items: center;
    }

    .chip {
        min-width: 200px;
        text-align: center;
    }
}

/* 스크롤바 스타일링 */
.chat-box::-webkit-scrollbar {
    width: 6px;
}

.chat-box::-webkit-scrollbar-track {
    background: transparent;
}

.chat-box::-webkit-scrollbar-thumb {
    background: #dee2e6;
    border-radius: 3px;
}

.chat-box::-webkit-scrollbar-thumb:hover {
    background: #adb5bd;
}
</style>
