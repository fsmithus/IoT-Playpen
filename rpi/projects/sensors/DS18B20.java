import java.io.*;

public class DS18B20 implements ThermometerIfc {

private static boolean verbose = false;

public static void main (String args[]) throws Exception {
	String deviceName = null;
	if (args.length < 1) {
		deviceName = "28-000005abe988";		// my device
	}
	else {
		deviceName = args[0];
	}
	DS18B20 dev = new DS18B20(deviceName);

	System.out.print("Temperature (deg F) = ");
	System.out.println(dev.getTemperature());
}


private String filename = null;

public DS18B20(String id) throws Exception {
	Runtime.getRuntime().exec("modprobe w1-gpio");
	Runtime.getRuntime().exec("modprobe w1-therm");
	filename = "/sys/bus/w1/devices/" + id + "/w1_slave";
}

private String getReading() throws Exception {
	StringBuffer sbuf = null;
	int indexyes = 0;
	for(boolean doitagain = true; doitagain;) {
		InputStream is = new FileInputStream(filename);
		sbuf = new StringBuffer(is.available());
		for (int j = 0; j>=0;) {
			j = is.read();
			sbuf.append((char) j);
		}
		if (verbose)	System.out.println(sbuf.toString());
		is.close();

		indexyes = sbuf.indexOf("YES");
		if (indexyes > 0)
			doitagain = false;
	}

	int offset = sbuf.lastIndexOf("t=") + 2;
	return sbuf.substring(offset,offset+5);
}

public float getTemperature() throws Exception {
	float t = (float) 0.0;
	String ts = getReading();
	if (verbose)	System.out.println(ts);
	t = Float.parseFloat(ts) / (float) 1000.0;
	t *= (180.0/100.0);	// Convert degrees C to degrees F
	t += 32.0;
	return t;
}
}

