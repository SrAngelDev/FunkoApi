package srangeldev.funkoapi.notifications.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface WebSocketSender {
    // Cuando trermina la conexion por cualquier motivo se elimina de la lista de notificaciones
    void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception;

    void sendMessage(String message) throws IOException;
}