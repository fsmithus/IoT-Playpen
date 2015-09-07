import java.io.*;
import java.util.StringTokenizer;

public class UltimateGPS implements GPSDeviceIfc {

private static boolean verbose = false;

public static void main (String args[]) throws Exception {
	// Get location information from GPS sensor
	GPSDeviceIfc gps = new UltimateGPS("/dev/ttyAMA0");	// my GPS device
	gps.snap();

	// Print the results
	System.out.println("GPS Data");
	System.out.println("  Date    = " + gps.getDateStamp());
	System.out.println("  Time    = " + gps.getTimeStamp());
	System.out.println("  Lat     = " + String.format("%f",gps.getLatitude()));
	System.out.println("  Long    = " + String.format("%f",gps.getLongitude()));
	System.out.println("  Speed   = " + String.format("%f",gps.getSpeed()));
	System.out.println("  Bearing = " + String.format("%f",gps.getTrackAngle()));
}

private BufferedReader gpsin;
private String dateStamp	= "Today";
private String timeStamp	= "Now";
private float latitude		= (float) 0.0;
private float longitude		= (float) 0.0;
private float speed		= (float) 0.0;
private float trackAngle	= (float) 0.0;

public UltimateGPS(String streamPath) throws Exception {
	gpsin = new BufferedReader(new FileReader(streamPath));
}

public void snap() throws Exception {
	boolean doitagain = true;
	while (doitagain) {
		try {
			String line = gpsin.readLine();
			if (verbose) System.out.println(line);
			StringTokenizer st = new StringTokenizer(line,",");
			String key = st.nextToken();
			if (key.equals("$GPRMC")) {
				harvestGPRMC(st);
				doitagain = false;
			}
		}
		catch (Exception e) {
			Thread.sleep(10);
		}
	}
}

//$GPRMC,010250.000,A,2935.1916,N,09539.6558,W,0.01,173.51,030714,,,D*74
private void harvestGPRMC(StringTokenizer st) {
	timeStamp = convertTimeStamp(st.nextToken());
	st.nextToken();			// Skip status = Active/Void
	String lats = st.nextToken();
	String latd = st.nextToken();
	latitude = convertLL(lats,latd);
	String longs = st.nextToken();
	String longd = st.nextToken();
	longitude = convertLL(longs,longd);
	String speeds = st.nextToken();
	speed = Float.parseFloat(speeds);
	String angles = st.nextToken();
	trackAngle = Float.parseFloat(angles);
	dateStamp  = st.nextToken();	// ignore the rest
}

public String getDateStamp()	{ return dateStamp; }
public String getTimeStamp()	{ return timeStamp; }
public float  getLatitude()	{ return latitude; }
public float  getLongitude()	{ return longitude; }
public float  getSpeed()	{ return speed; }
public float  getTrackAngle()	{ return trackAngle; }

private String convertTimeStamp(String s) {
	return 	s.substring(0,2) + ":" +	// GMT Hours
		s.substring(2,4) + ":" +	// GMT Minutes
		s.substring(4);			// GMT Seconds.Milliseconds
}

private float convertLL(String num,String dir) {
	float d = 0.0f;
	float m = 0.0f;
	if (num.indexOf(".") == 4)  {				// Latitude
		d = Float.parseFloat(num.substring(0,2));	// two digits
		m = Float.parseFloat(num.substring(2));
	}
	else {							// Longitude
		d = Float.parseFloat(num.substring(0,3));	// three digits
		m = Float.parseFloat(num.substring(3));
	}
	// System.out.println(String.format("%f",m));
	d += m / 60.0f;			// convert minutes to degrees
	if (dir.equals("S") || dir.equals("W")) {	// N & E are positive
		d = -d;
	}
	return d;
}
}

