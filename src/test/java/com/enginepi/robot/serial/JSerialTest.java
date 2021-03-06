package com.enginepi.robot.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

@Slf4j
public class JSerialTest {

    @Test
    public void testSerial() throws DecoderException, InterruptedException {
        SerialPort[] ports = SerialPort.getCommPorts();

        for (int i = 0; i < ports.length; i++) {
            SerialPort port = ports[i];

            log.info("port:{}",port.getSystemPortName());
        }


        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
//        comPort.setNumDataBits(8);
        comPort.setBaudRate(115200);
//
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
            @Override
            public void serialEvent(SerialPortEvent event)
            {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(newData, newData.length);
                //log.info("Read " + numRead + " bytes.");
                log.info("返回数据 byte:{}",newData);

                String chars = Hex.encodeHexString(newData).toUpperCase();

                log.info("返回数据 hex:{}",chars);
            }
        });


        byte[] lightValue = genLightValue(0);

        comPort.writeBytes(lightValue,lightValue.length);
        comPort.bytesAwaitingWrite();


        for (int i = 0; i < 1000000; i+=1) {
            String pad = StringUtils.leftPad(String.valueOf(i), 10, '0');

            byte[] bytes = genByte(pad);
            comPort.writeBytes(bytes,bytes.length);
            int bytesAwaitingWrite = comPort.bytesAwaitingWrite();
            //log.info("bytesAwaitingWrite:{}",bytesAwaitingWrite);
            Thread.sleep(1000);

        }




        //log.info("i:{}", i);

        Thread.sleep(10000);
        comPort.closePort();

    }

    @Test
    public void testGenCode() {
        for (int i = 0; i < 100; i++) {
            String pad = StringUtils.leftPad(String.valueOf(i), 10, '0');

            byte[] bytes = genByte(pad);

            //log.info("bytes:{}",bytes);
        }
    }

    /**
     * 根据显示字符串返回发送指令
     * @param timeCode
     *          0123456789
     * @return
     */
    public byte[] genByte(String timeCode) {

//        byte []buffer = new byte[]{
//                0x005A, 0x0000, 0x0000, 0x0001, (byte) 0x00F3, 0x0001, 0x0000, 0x001A, 0x0032, 0x0054, 0x0076,(byte) 0x0098, (byte)0x005D, (byte)0x00A5
//        };


        char[] chars = timeCode.toCharArray();



        for (int i = 0; i < chars.length; i++) {
            /**
             * 如果是 0 则替换为 A
             */
            if(chars[i] == '0') {
                chars[i] = 'A';
            }
            /**
             * 每两位调换位置
             */
            if(i % 2 != 0) {
                char temp =  chars[i];
                chars[i] = chars[i-1];
                chars[i-1] = temp;
            }
        }

        //chars[1] = chars[8];

        String newCode = new String(chars);




        StringBuffer sb = new StringBuffer("5A000001F30100").append(newCode)
                .append("FFA5");


        log.info("send code:{}",sb.toString());
        try {
            return Hex.decodeHex(sb.toString());
        } catch (DecoderException e) {
            return null;
        }
    }

    public byte[] genLightValue(int value) {
        //5A000001F303000800000000FFA5

//        byte [] buffer = new byte[]{
//                0x005A, 0x0000, 0x0000, 0x0001, (byte) 0x00F3, 0x0003, 0x0000, 0x0008,  0x0000, 0x0000, 0x0000,0x0000, (byte) 0x00FF, (byte)0x00A5
//        };

        String lightCode = "08";
        StringBuffer sb = new StringBuffer("5A000001F30300").append(lightCode)
                .append("00000000FFA5");


        try {
            return Hex.decodeHex(sb.toString());
        } catch (DecoderException e) {
            return null;
        }
    }
}
