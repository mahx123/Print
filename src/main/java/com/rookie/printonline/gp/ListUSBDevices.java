package com.rookie.printonline.gp;
import org.usb4java.*;

public class ListUSBDevices {

    static {
        // 加载名为 "mylibrary" 的 DLL（无需扩展名）
        System.loadLibrary("LibusbJava-1_2");
    }
    public static void main(String[] args) {
        Context context = new Context();
        LibUsb.init(context);

        try {
            DeviceList list = new DeviceList();
            LibUsb.getDeviceList(context, list);

            try {
                for (Device device : list) {
                    DeviceDescriptor descriptor = new DeviceDescriptor();
                    LibUsb.getDeviceDescriptor(device, descriptor);

                    System.out.printf("Bus %03d Device %03d: ID %04x:%04x - %s%n",
                            LibUsb.getBusNumber(device),
                            LibUsb.getDeviceAddress(device),
                            descriptor.idVendor(),
                            descriptor.idProduct(),
                            getProductString(device, descriptor));
                }
            } finally {
                LibUsb.freeDeviceList(list, true);
            }
        } finally {
            LibUsb.exit(context);
        }
    }

    private static String getProductString(Device device, DeviceDescriptor descriptor) {
        try {
            DeviceHandle handle = new DeviceHandle();
            LibUsb.open(device, handle);
            try {
                return LibUsb.getStringDescriptor(handle, descriptor.iProduct());
            } finally {
                LibUsb.close(handle);
            }
        } catch (Exception e) {
            return "[无法获取设备名称]";
        }
    }
}
