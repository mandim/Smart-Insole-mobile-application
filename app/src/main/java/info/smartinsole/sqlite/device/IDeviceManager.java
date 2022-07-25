package info.smartinsole.sqlite.device;

import java.util.List;

/**
 * Main Interface for the device Manager
 * The implementation should handle all the communication with the
 * Smart Insole device
 */
public interface IDeviceManager {

    /**
     * Get List of all available devices
     * @return List of available devices
     */
    List<Device> getDevices();


    /**
     * Connect to device
     * @param device Device to connect to
     * @return True if connection succeeded
     */
    boolean connect(Device device);

    /**
     * Disconnect from device
     * @param device Device to disconnect
     */
    void disconnect(Device device);

    /**
     * Register a data reader which will be used to write data to the local database
     *
     * @param dataReader Data Reader implementation class
     */
    void registerDataReader(IDeviceDataReader dataReader);

    /**
     * Unregister data readers
     * This be always called after disconnection to avoid errors
     * @param dataReader Data Reader implementation class
     */
    void unregisterDataReader(IDeviceDataReader dataReader);




}
