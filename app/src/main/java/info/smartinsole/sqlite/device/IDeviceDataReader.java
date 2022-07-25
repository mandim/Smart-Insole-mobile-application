package info.smartinsole.sqlite.device;

public interface IDeviceDataReader {

    /**
     * This method should be called when new data are received from the device
     * @param data Device data to be stored locally
     */
    void write(DeviceData data);
}
