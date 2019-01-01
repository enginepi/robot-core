package com.enginepi.vision;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@WebSocket
@Slf4j
public class WebcamWebSocketHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Session session;

    private void teardown() {
        try {
            session.close();
            session = null;
        } finally {
            WebcamCache.unsubscribe(this);
        }
    }

    private void setup(Session session) {

        this.session = session;

        Map<String, Object> message = new HashMap<String, Object>();
        message.put("type", "list");
        message.put("webcams", WebcamCache.getWebcamNames());

        send(message);

        WebcamCache.subscribe(this);
    }

    @OnWebSocketClose
    public void onClose(int status, String reason) {
        log.info("WebSocket closed, status = {}, reason = {}", status, reason);
        teardown();
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        log.error("WebSocket error", t);
        teardown();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.info("WebSocket connect, from = {}", session.getRemoteAddress().getAddress());
        setup(session);
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        log.info("WebSocket message, text = {}", message);
    }

    public void newImage(Webcam webcam, BufferedImage image) {

        // log.info("New image from {}", webcam);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPG", baos);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        String base64 = null;
        try {
            base64 = new String(Base64.getEncoder().encode(baos.toByteArray()), "UTF8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }

        Map<String, Object> message = new HashMap<String, Object>();
        message.put("type", "image");
        message.put("webcam", webcam.getName());
        message.put("image", base64);

        send(message);
    }

    private void send(String message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendStringByFuture(message);
            } catch (Exception e) {
                log.error("Exception when sending string", e);
            }
        }
    }

    private void send(Object object) {
        try {
            send(MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
