package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import com.bosswiin.device.bluetooth.blehandelr.BleNamesResolver;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapperUiCallbacks;

import java.util.List;

/**
 * Created by 9708023 on 2014/10/27.
 */
public class BLEActionSend extends BLEActionBase implements BleWrapperUiCallbacks {

    private final String logTag=this.getClass().getName();

    public BLEActionSend() {
    }

    @Override
    public boolean Execute(BLERequest request) {

        boolean result = false;

        if (request.actionEnum != BLEAcionEnum.Send) {
            result = this.successor.Execute(request);
        }
        else {

            String address=request.bluetoothDevice.getAddress().toString();

            if(request.bleWrapper.connect(address)){
                BluetoothGatt gatt=request.bleWrapper.getGatt();
               // BluetoothGattCharacteristic c=
                Log.v(this.getClass().getPackage().getName(), "Start to Send BLE device");
            }
            else{
                Log.e(this.getClass().getName(), "It cannot connect to :" + address);
            }
        }

        return result;
    }

    @Override
    public void uiAvailableServices(BluetoothGatt gatt,
                                    BluetoothDevice device,
                                    List<BluetoothGattService> services
    )
    {
        for (BluetoothGattService service : services)
        {
            String serviceName = BleNamesResolver.resolveUuid
                    (service.getUuid().toString());
            Log.d("DEBUG", serviceName);
        }
    }

    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt gatt,
                                            BluetoothDevice device,
                                            BluetoothGattService service,
                                            BluetoothGattCharacteristic ch,
                                            String strValue,
                                            int intValue,
                                            byte[] rawValue,
                                            String timestamp)
    {

        Log.d(this.logTag, "uiNewValueForCharacteristic");

        for (byte b:rawValue){
            Log.d(this.logTag, "Val: " + b);
        }
    }

    @Override
    public void uiSuccessfulWrite(final BluetoothGatt gatt,
                                  final BluetoothDevice device,
                                  final BluetoothGattService service,
                                  final BluetoothGattCharacteristic ch,
                                  final String description)
    {
    }

    @Override
    public void uiFailedWrite(final BluetoothGatt gatt,
                              final BluetoothDevice device,
                              final BluetoothGattService service,
                              final BluetoothGattCharacteristic ch,
                              final String description)
    {
    }

    @Override
    public void uiGotNotification(final BluetoothGatt gatt,
                                  final BluetoothDevice device,
                                  final BluetoothGattService service,
                                  final BluetoothGattCharacteristic ch)
    {
    }

    @Override
    public void uiDeviceConnected(final BluetoothGatt gatt,
                                  final BluetoothDevice device)
    {
    }

    @Override
    public void uiDeviceDisconnected(final BluetoothGatt gatt,
                                     final BluetoothDevice device)
    {
    }

    @Override
    public void uiNewRssiAvailable(final BluetoothGatt gatt,
                                   final BluetoothDevice device,
                                   final int rssi)
    {
    }

    @Override
    public void uiCharacteristicForService(final BluetoothGatt gatt,
                                           final BluetoothDevice device,
                                           final BluetoothGattService service,
                                           final List<BluetoothGattCharacteristic> chars)
    {
    }

    @Override
    public void uiCharacteristicsDetails(final BluetoothGatt gatt,
                                         final BluetoothDevice device,
                                         final BluetoothGattService service,
                                         final BluetoothGattCharacteristic characteristic)
    {
    }

    @Override
    public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
        // no need to handle that in this Activity (here, we are not scanning)
    }
}