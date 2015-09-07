import com.pi4j.io.i2c.*;

public class HelloSensors {

public static void main (String args[]) throws Exception {
	printTemperature();
	printPressure();
	printLocation();
	printAltitude();
}

public static void printLocation() throws Exception {
	GPSDeviceIfc gps = new UltimateGPS("/dev/ttyAMA0");	// my GPS device
	gps.snap();

	System.out.println("Location");
	System.out.println("  Date            = " + gps.getDateStamp());
	System.out.println("  Time            = " + gps.getTimeStamp());
	System.out.println("  Latitude        = " + String.format("%f",gps.getLatitude()));
	System.out.println("  Longitude       = " + String.format("%f",gps.getLongitude()));
	System.out.println("  Speed (knots)   = " + String.format("%f",gps.getSpeed()));
	System.out.println("  Bearing         = " + String.format("%f",gps.getTrackAngle()));
}

public static void printAltitude() throws Exception {
	// Get altitude from MPL3115A2 sensor
	AltimeterIfc mpl = new MPL3115A2(
			I2CFactory.getInstance(I2CBus.BUS_1));	// my bus
	float a = mpl.getAltitude();

	// Print the results
	System.out.println("Altimeter (feet)");
	System.out.println("  Altitude        = " + String.format("%f",a));
}

public static void printTemperature() throws Exception {
	// Get temperature from MPL3115A2 sensor
	ThermometerIfc mpl = new MPL3115A2(
			I2CFactory.getInstance(I2CBus.BUS_1));	// my bus
	float t1 = mpl.getTemperature();

	// Get temperature from DS18B20 sensor
	ThermometerIfc ds = new DS18B20("28-000005abe988");	// my device
	float t2 = ds.getTemperature();

	// Print the results
	System.out.println("Thermometer (deg F)");
	System.out.println("  Temp(MPL3115A2) = " + String.format("%f",t1));
	System.out.println("  Temp(DS18B20)   = " + String.format("%f",t2));
	System.out.println("  Temp(avg)       = " + String.format("%f",((t2+t1)/2.0)));
}

public static void printPressure() throws Exception {
	// Get altitude from MPL3115A2 sensor
	BarometerIfc mpl = new MPL3115A2(
			I2CFactory.getInstance(I2CBus.BUS_1));	// my bus
	float p = mpl.getPressure();

	// Print the results
	System.out.println("Barometer (inches Hg)");
	System.out.println("  Pressure        = " + String.format("%f",p));
}
}

