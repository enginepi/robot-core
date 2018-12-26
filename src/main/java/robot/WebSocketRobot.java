package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


@Slf4j
@Robot
public class WebSocketRobot extends AbstractRobot implements IRobot {

    String url = "ws://localhost:8888";
    @Override
    public void setup() {
        super.setup();



    }

    @Override
    public void loop() throws InterruptedException {

    }


    public static class  GodotWebSocketClient extends WebSocketClient {

        public GodotWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            log.info("websocket open");
        }

        @Override
        public void onMessage(String s) {

        }

        @Override
        public void onClose(int i, String s, boolean b) {
            log.info("websocket close");
        }

        @Override
        public void onError(Exception e) {

        }
    }
}
