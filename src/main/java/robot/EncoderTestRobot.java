package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.input.Encoder;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
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

    String server = "172.16.203.32";
    int port = 12321;

    Socket client  = null;

    SerialPort serialPort = null;

    String portName = "ttyUSB0";
    @Override
    public void setup() {

        super.setup();

        SerialPort[] commPorts = SerialPort.getCommPorts();

        for (int i = 0; i < commPorts.length; i++) {
            SerialPort port = commPorts[i];
            log.info("port:{}",port.getSystemPortName());
             if(port.getSystemPortName().equals(portName)) {
                 serialPort = port;

                 break;
             }
        }


        if(serialPort == null) {
            log.warn("串口通讯错误");
        } else {
            serialPort.setBaudRate(9600);
            serialPort.openPort();
            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
                @Override
                public void serialEvent(SerialPortEvent event)
                {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    {
                        return;
                    }
                    byte[] newData = new byte[serialPort.bytesAvailable()];
                    int numRead = serialPort.readBytes(newData, newData.length);
                    // log.info("Read {} bytes.",numRead);
                    String content =  new String(newData);

                    // log.info("content:{}",content);

                    String[] split = content.split("\n");

                    if(split.length>0) {
                        String pos = split[0];
                        String[] posSplit = pos.split(",");
                        if(posSplit.length==2) {
                            // log.info("传感器1：{}",posSplit[0]);
                            // log.info("传感器2：{}",posSplit[1]);
                            sendData(pos);
                        }
                    }
                }
            });
        }
         try {
            client = new Socket(getByName(server),port);
            log.info("连接服务器成功");
        } catch (IOException e) {

            log.info("连接服务器错误");
            e.printStackTrace();
        }
    }

    @Override
    public void loop() throws InterruptedException {
        long millis = System.currentTimeMillis();


        if(millis % 1000 == 0) {
            //log.info("count:{}",encoder.read());

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
