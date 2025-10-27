package srangeldev.funkoapi.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class WebSockethandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {
    private final String entidad;

    // Lista con todas las sesiones activas sin repetirse
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public WebSockethandler(String entidad) {
        this.entidad = entidad;
    }

    @Override // Cuando se crea una conexion nueva, lanza un mensaje de bienvenida
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WEBSOCKET: Conexión establecida con el servidor");
        log.info("WEBSOCKET: Sesión: " + session);
        sessions.add(session);
        TextMessage message = new TextMessage("Bienvenido al sistema de notificaciones de " + entidad + " por FunkoApi");
        log.info("WEBSOCKET: Servidor envía: {}", message);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WEBSOCKET: Conexión cerrada con el servidor: " + status);
        sessions.remove(session);
    }

    @Override
    public void sendMessage(String message) throws IOException {
        log.info("WEBSOCKET: Enviar mensaje de cambios en la entidad: " + entidad + " : " + message);
        // Enviamos el mensaje a todos los clientes conectados
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) { // Si teiene la sesion abierta le enviamos el mensaje
                log.info("WEBSOCKET: Servidor WS envía: " + message);
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WEBSOCKET: Error de transporte con el servidor: " + exception.getMessage()); // Para guardar el logger de error
    }

    @Override
    public List<String> getSubProtocols() { // Le decimos a Spring los "Subprotocolos" que aceptamos en nuestro WebSocket
        return List.of("categories.websocket");
    }
}
