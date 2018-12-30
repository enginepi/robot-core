package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.input.Encoder;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.extern.slf4j.Slf4j;
import sun.net.util.IPAddressUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.net.InetAddress.*;

@Slf4j
@Robot
public class EncoderTestRobot extends AbstractRobot implements IRobot {

    Encoder encoder;

    String server = "172.16.200.85";
    int port = 12321;

    Socket client  = null;

    @Override
    public void setup() {

        super.setup();

        encoder = new Encoder(RaspiPin.GPIO_15,RaspiPin.GPIO_16);


        encoder.init();


//        try {
//            client = new Socket(getByName(server),port);
//            log.info("连接服务器成功");
//        } catch (IOException e) {
//
//            log.info("连接服务器错误");
//            e.printStackTrace();
//        }
    }

    @Override
    public void loop() throws InterruptedException {
        long millis = System.currentTimeMillis();


        if(millis % 1000 == 0) {
            log.info("count:{}",encoder.read());

        }

        //sendData(String.valueOf(abs/100));
    }

    private void sendData(String data) {
        if(this.client!= null) {
            try {

                data += "\n";
                log.info("send:{}",data);
                OutputStream output = this.client.getOutputStream();
                output.write(data.getBytes("UTF-8"));
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
