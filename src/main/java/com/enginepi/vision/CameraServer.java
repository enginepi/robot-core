package com.enginepi.vision;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * @author liangdi
 */
@Slf4j
public class CameraServer {
    Server server = null;

    /**
     * 启动摄像头服务
     */
    public void start() {
        WebSocketHandler wsHandler = new WebSocketHandler() {

            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WebcamWebSocketHandler.class);
            }
        };

        try {
            server = new Server(8123);
            server.setHandler(wsHandler);
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if(server != null) {
            if(server.isStarted()) {
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

