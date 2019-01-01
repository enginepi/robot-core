package com.enginepi.vision;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@Slf4j
public class WebcamWebSocketsController {

    static {

        ;
        //Webcam.setDriver(new IpCamDriver(new IpCamStorage("src/main/resources/cameras.xml")));
    }


    public static void main(String[] args) throws Exception {

        for (String name : WebcamCache.getWebcamNames()) {
            log.info("Will read webcam {}", name);
        }

        Server server = new Server(8123);
        WebSocketHandler wsHandler = new WebSocketHandler() {

            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WebcamWebSocketHandler.class);
            }
        };

        server.setHandler(wsHandler);
        server.start();
        server.join();
    }
}
