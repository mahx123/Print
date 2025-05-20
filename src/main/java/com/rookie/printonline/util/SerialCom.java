package com.rookie.printonline.util;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SerialCom {
    public static void main(String[] args) throws Exception {

       // serialBean.testMethod();
        listPortChoices();
    }
    public static List<String> listPortChoices() {
        List<String> portList = new ArrayList<>();
        CommPortIdentifier portId;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
        // iterate through the ports.
        while (en.hasMoreElements()) {
            portId = (CommPortIdentifier) en.nextElement();
            //    log.info("当前可用串口：");
            System.out.println(portId);
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                String portName = portId.getName();

                portList.add(portName);
            }
        }
        return portList;
    }



}



