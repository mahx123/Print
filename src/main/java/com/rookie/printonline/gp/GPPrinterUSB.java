package com.rookie.printonline.gp;

import org.usb4java.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class GPPrinterUSB {
    // 佳博打印机的常见厂商 ID 和产品 ID（需根据实际型号修改）
    private static final short VENDOR_ID = (short) 0x0416;  // 佳博厂商 ID 示例
    private static final short PRODUCT_ID = (short) 0x5011; // 佳博产品 ID 示例

    // 端点地址（需根据实际打印机修改）
    private static final byte ENDPOINT_OUT = (byte) 0x02;  // 输出端点
    private static final byte ENDPOINT_IN = (byte) 0x81;   // 输入端点（用于接收响应）

    static {

    }
    public static void main(String[] args) {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new RuntimeException("USB 初始化失败: " + LibUsb.strError(result));
        }

        try {
            // 查找并打开打印机
            DeviceHandle handle = findPrinter(context);
            if (handle == null) {
                System.out.println("未找到佳博打印机，请检查连接或设备 ID");
                return;
            }

            try {
                // 声明接口
                claimInterface(handle);

                // 发送 GP 指令
                String gpCommand = buildGPCommand();
                sendCommand(handle, gpCommand);

                // 可选：接收打印机响应
                receiveResponse(handle);

            } finally {
                // 释放资源
                releaseInterface(handle);
                LibUsb.close(handle);
            }
        } finally {
            LibUsb.exit(context);
        }
    }

    private static DeviceHandle findPrinter(Context context) {
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0) {
            throw new RuntimeException("获取设备列表失败: " + LibUsb.strError(result));
        }

        try {
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result < 0) {
                    continue; // 忽略无法获取描述符的设备
                }

                if (descriptor.idVendor() == VENDOR_ID && descriptor.idProduct() == PRODUCT_ID) {
                    System.out.println("找到佳博打印机: " + descriptor.iProduct());
                    DeviceHandle handle = new DeviceHandle();
                    result = LibUsb.open(device, handle);
                    if (result < 0) {
                        System.err.println("无法打开设备: " + LibUsb.strError(result));
                    } else {
                        return handle;
                    }
                }
            }
        } finally {
            LibUsb.freeDeviceList(list, true);
        }
        return null;
    }

    private static void claimInterface(DeviceHandle handle) {
        int result = LibUsb.claimInterface(handle, 0); // 通常使用接口 0
        if (result < 0) {
            throw new RuntimeException("声明接口失败: " + LibUsb.strError(result));
        }
    }

    private static void releaseInterface(DeviceHandle handle) {
        int result = LibUsb.releaseInterface(handle, 0);
        if (result < 0) {
            System.err.println("释放接口失败: " + LibUsb.strError(result));
        }
    }

    private static String buildGPCommand() {
        // 构建 GP 指令示例（设置标签尺寸、打印文本、执行打印）
        StringBuilder command = new StringBuilder();
        command.append("SIZE 40 mm, 30 mm\n");    // 设置标签尺寸
        command.append("GAP 2 mm, 0 mm\n");       // 设置标签间隙
        command.append("CLS\n");                  // 清除缓冲区
        command.append("TEXT 100,100,\"TSS24.BF2\",\"0\",90,\"Hello, GP Printer!\"\n"); // 打印文本
        command.append("BARCODE 100,200,\"128\",100,1,0,2,2,\"ABC123456\"\n"); // 打印条码
        command.append("PRINT 1\n");              // 打印 1 份

        return command.toString();
    }

    private static void sendCommand(DeviceHandle handle, String command) {
        byte[] data = command.getBytes();
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.flip();

        IntBuffer transferred = IntBuffer.allocate(1);
        int result = LibUsb.bulkTransfer(handle, ENDPOINT_OUT, buffer, transferred, 5000);

        if (result == LibUsb.SUCCESS) {
            System.out.println("成功发送 " + transferred.get() + " 字节数据");
        } else {
            throw new RuntimeException("发送数据失败: " + LibUsb.strError(result));
        }
    }

    private static void receiveResponse(DeviceHandle handle) {
        // 创建接收缓冲区（根据打印机响应大小调整）
        ByteBuffer buffer = ByteBuffer.allocateDirect(64);
        IntBuffer transferred = IntBuffer.allocate(1);

        // 执行批量传输接收数据（超时设置为 1000 毫秒）
        int result = LibUsb.bulkTransfer(handle, ENDPOINT_IN, buffer, transferred, 1000);

        if (result == LibUsb.SUCCESS && transferred.get() > 0) {
            byte[] response = new byte[transferred.get()];
            buffer.get(response);
            System.out.println("收到响应: " + new String(response));
        } else if (result == LibUsb.ERROR_TIMEOUT) {
            System.out.println("接收超时，打印机可能没有返回数据");
        } else {
            System.err.println("接收响应失败: " + LibUsb.strError(result));
        }
    }
}
