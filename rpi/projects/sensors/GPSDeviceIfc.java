public interface GPSDeviceIfc {

public void   snap() throws Exception;	// Snapshot of data

// NMEA Recommended Minimum GPS data
public String getDateStamp();		// MMDDYY
public String getTimeStamp();		// 24MMSS
public float  getLatitude();		// Signed degrees
public float  getLongitude();		// Signed degrees
public float  getSpeed();		// Ground speed in knots
public float  getTrackAngle();		// Track angle degrees true

}
