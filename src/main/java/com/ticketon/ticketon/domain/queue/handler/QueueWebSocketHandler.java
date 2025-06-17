package com.ticketon.ticketon.domain.queue.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class QueueWebSocketHandler extends TextWebSocketHandler {

    // 접속된 세션을 저장할 스레드 안전한 Set
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    /**
     * 클라이언트가 웹소켓 연결 성공했을때 자동 실행
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){
        // 클라이언트가 보낸 메시지 처리
    }

    /**
     * 예약 서버 입장 후 웹소켓 종료시 자동 연결 해제
    */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    /**
     * 서버가 대기열 상태 변경 같은 이벤트가 발생했을 때, 모든 접속된 클라이언트에게 실시간으로 알림을 보낼 때 호출.
     */
    public void sendMessageToAll(String message) {
        TextMessage msg = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}