import java.lang.*;
import java.io.IOException;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

public class MPL3115A2 implements I2CDevice,
				ThermometerIfc,
				AltimeterIfc,
				BarometerIfc {

/*
See Table 9 of the data sheet.
http://adafruit.com/datasheets/1893_datasheet.pdf

These are the codes required for polling the sensor.  Interupt/FIFO codes
are also provided in the datasheet.

To develop this class, I started by translating the Adafruit MPL3115A2 C++
library (https://github.com/adafruit/Adafruit_MPL3113A2_Library) and
substituted pi4j (pi4j.com) classes for the Wire library, which I did not
have for Java.  The data output seems unstable, so I replaced the sampling
algorithm for a snapshot algorithm, which seemed more appropriate for my
purposes anyway.

The instabilities ended up being my own errors in performing bit operations
with Java's signed integers.

In the end, the data stabilized to values tht I expected.
*/

public static final int  MPL3115A2_ADDRESS =			0x60;

// Data register indexes and bit masks
public static final int  MPL3115A2_FIRST_DATA_REGISTER =	0x00;
public static final int  MPL3115A2_REGISTER_STATUS =		0x00;
public static final byte MPL3115A2_REGISTER_STATUS_TDR =	0x02;
public static final byte MPL3115A2_REGISTER_STATUS_PDR =	0x04;
public static final byte MPL3115A2_REGISTER_STATUS_PTDR =	0x08;

public static final int  MPL3115A2_REGISTER_PRESSURE_MSB =	0x01;
public static final int  MPL3115A2_REGISTER_PRESSURE_CSB =	0x02;
public static final int  MPL3115A2_REGISTER_PRESSURE_LSB =	0x03;
public static final int  MPL3115A2_REGISTER_TEMP_MSB =		0x04;
public static final int  MPL3115A2_REGISTER_TEMP_LSB =		0x05;
public static final int  MPL3115A2_REGISTER_DR_STATUS =		0x06;
public static final int  MPL3115A2_OUT_P_DELTA_MSB =		0x07;
public static final int  MPL3115A2_OUT_P_DELTA_CSB =		0x08;
public static final int  MPL3115A2_OUT_P_DELTA_LSB =		0x09;
public static final int  MPL3115A2_OUT_T_DELTA_MSB =		0x0A;
public static final int  MPL3115A2_OUT_T_DELTA_LSB =		0x0B;
public static final int  MPL3115A2_WHOAMI =			0x0C;
public static final int  MPL3115A2_LAST_DATA_REGISTER =		0x0D;

public static final int  MPL3115A2_IAM_MPL3115A2 =		0xC4;

// Sampling mode indexes and masks
public static final int  MPL3115A2_PT_DATA_CFG =		0x13;
public static final byte MPL3115A2_PT_DATA_CFG_TDEFE =		0x01;
public static final byte MPL3115A2_PT_DATA_CFG_PDEFE =		0x02;
public static final byte MPL3115A2_PT_DATA_CFG_DREM =		0x04;

// Control register indexes and bit masks
public static final int  MPL3115A2_CTRL_REG1 =			0x26;
public static final byte MPL3115A2_CTRL_REG1_SBYB =		0x00;
public static final byte MPL3115A2_CTRL_REG1_ACT =		0x01;
public static final byte MPL3115A2_CTRL_REG1_OST =		0x02;
public static final byte MPL3115A2_CTRL_REG1_RST =		0x04;
public static final byte MPL3115A2_CTRL_REG1_OS1 =		0x00;
public static final byte MPL3115A2_CTRL_REG1_OS2 =		0x08;
public static final byte MPL3115A2_CTRL_REG1_OS4 =		0x10;
public static final byte MPL3115A2_CTRL_REG1_OS8 =		0x18;
public static final byte MPL3115A2_CTRL_REG1_OS16 =		0x20;
public static final byte MPL3115A2_CTRL_REG1_OS32 =		0x28;
public static final byte MPL3115A2_CTRL_REG1_OS64 =		0x30;
public static final byte MPL3115A2_CTRL_REG1_OS128 =		0x38;
public static final byte MPL3115A2_CTRL_REG1_RAW =		0x40;
public static final int  MPL3115A2_CTRL_REG1_ALT =		0x80;
public static final int  MPL3115A2_CTRL_REG1_BAR =		0x00;
public static final byte MPL3115A2_CTRL_REG2 =			0x27;
public static final byte MPL3115A2_CTRL_REG3 =			0x28;
public static final byte MPL3115A2_CTRL_REG4 =			0x29;
public static final byte MPL3115A2_CTRL_REG5 =			0x2A;


/* Test and diagnostic routines used during developoment. */
private static boolean verbose = false;
private static boolean diag = false;

public static void main(String args[]) throws Exception {
	if (getArgs(args)) {
		if (diag) {
			diagnostic();
		}
		else {
			normal();
		}
	}
}

private static boolean getArgs(String args[]) {
	for (int i = 0; i < args.length; i++) {
		// Print help and exit
		if (args[i].equals("-h")) {
			System.out.println("MPL3115A2 [-h | -v | -t]");
			System.out.println(" -h - Show help");
			System.out.println(" -v - Run verbose");
			System.out.println(" -t - Run extended diagnostics");
			return false;
		}

		// run normal test verbose
		else if (args[i].equals("-v")) {
			verbose = true;
		}

		// run diagnostics
		else if (args[i].equals("-t")) {
			diag = true;
			verbose = true;
		}
	}
	return true;
}

private static void normal() throws Exception {
	testProbe("Getting bus... " +
		Integer.toHexString(I2CBus.BUS_1).toUpperCase());
	I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
	testProbe("Getting device...");
	MPL3115A2 dev = new MPL3115A2(bus);
	testProbe("Initializing...");
	if (dev.begin()) {
		testProbe("Sensing...");
		System.out.print("Pressure (inches Hg) = ");
		System.out.println(dev.getPressure());
		System.out.print("Altitude (feet)      = ");
		System.out.println(dev.getAltitude());
		System.out.print("Temperature (deg F)  = ");
		System.out.println(dev.getTemperature());
	}
	else {
		System.out.println("MPL3115A2 not detected");
	}
}

private static void diagnostic() throws Exception {
	testProbe("Getting bus... " +
		Integer.toHexString(I2CBus.BUS_1).toUpperCase());
	I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
	testProbe("Getting device...");
	MPL3115A2 dev = new MPL3115A2(bus);
	testProbe("Initializing...");
	if (dev.begin()) {
		for (int i = 1; true; i++) {
			if (verbose) System.out.println("Sample... " +
					String.format("%d {",i));
			dev.snap(MPL3115A2_CTRL_REG1_BAR);
			dev.readAllRegisters();
			Thread.sleep(1000);
			if (verbose) System.out.println("}");
		}
	}
	else {
		System.out.println("MPL3115A2 not detected");
	}
}


/* Sensor object definition. */
private I2CDevice device = null;
private byte[] databuffer = null;
private byte[] controlbuffer = null;

public MPL3115A2(I2CBus bus) throws Exception {
	super();
	if (bus == null)
		bus = I2CFactory.getInstance(I2CBus.BUS_1);
	device = bus.getDevice(MPL3115A2_ADDRESS);
	int bl = MPL3115A2_LAST_DATA_REGISTER - MPL3115A2_FIRST_DATA_REGISTER;
	databuffer = new byte[bl];
	for (byte b: databuffer) b = 0;
	reset();
}

private void reset() throws Exception {
	controlbuffer = new byte[1];
	controlbuffer[0] = MPL3115A2_CTRL_REG1_RST;
	try {
		write(MPL3115A2_CTRL_REG1,controlbuffer[0]);
	} catch (IOException e) {}	// Ignore, unable to complete on RST
	wait(MPL3115A2_CTRL_REG1_RST);
}

/* Wait for a control bit to be reset by the device during read(). */
private void wait(byte event) throws Exception {
	for (;(controlbuffer[0] & event) != 0;) {
		if (verbose) System.out.print("r");
		Thread.sleep(10);
		read(MPL3115A2_CTRL_REG1,controlbuffer,0,1);
	}
	if (verbose) System.out.println("");
}

/* With device in standby, driving OST high then waiting for reset */
/* fetches data to registers. */
private void snap(int mode) throws Exception {
	controlbuffer[0] = (byte) (mode | 	// ALT or BAR
				MPL3115A2_CTRL_REG1_OST |
				MPL3115A2_CTRL_REG1_SBYB);
	write(MPL3115A2_CTRL_REG1,controlbuffer[0]);	// Snap!
	wait(MPL3115A2_CTRL_REG1_OST);
}

/* Reads all data registers into the Java object.  Could be optimized. */
private void readAllRegisters() throws Exception {
	read(MPL3115A2_FIRST_DATA_REGISTER,databuffer,
		MPL3115A2_FIRST_DATA_REGISTER,
		MPL3115A2_LAST_DATA_REGISTER);
}

/* Handy for debugging durning development. */
private String byteArrayToHex(byte[] buffer) {
	StringBuilder sb = new StringBuilder(buffer.length * 3);
	for (byte b: buffer)
		sb.append(String.format("%02x ", b & 0xff));
	return (sb.toString());
}

public boolean begin() throws IOException {

// Found something like this in the Adafruit.com reference implementation
// (written in C++) but my device seems to always return 0 from whoami query.
// Also refer to figure 5 of the datasheet above.  No such test there.
/*
	try {
		int whoami = -1;
		whoami = read(MPL3115A2_WHOAMI);
		testProbe("I am... 0x" +
			Integer.toHexString(whoami).toUpperCase());
		if (whoami != MPL3115A2_IAM_MPL3115A2) {
			return false;
		}
	} catch (IOException e) { throw(e); }
*/
	return true;
}

/* Sensor object useful functions. */
public float getPressure() throws Exception {
	float p = (float) 0.0;

	// Read data
	snap(MPL3115A2_CTRL_REG1_BAR);
	readAllRegisters();

	// Convert values
	int pressure =			// unsigned 20-bit integer
		(databuffer[MPL3115A2_REGISTER_PRESSURE_MSB] << 16) & 0xff0000;
	pressure |= (databuffer[MPL3115A2_REGISTER_PRESSURE_CSB] << 8) & 0xff00;
	pressure |= (databuffer[MPL3115A2_REGISTER_PRESSURE_LSB] & 0xff);
	pressure >>= 4;
	testProbe(String.format("%06X",pressure));
	p = ((float) pressure) / ((float) 4.0);
	p /= 3386.3887;		// Convert Pascals to Inches Hg

	return(p);
}

public float getAltitude() throws Exception {
	float a = (float) 0.0;

	// Read data
	snap(MPL3115A2_CTRL_REG1_ALT);
	readAllRegisters();

	// Convert values
	int altitude =			// signed 20-bit integer
		databuffer[MPL3115A2_REGISTER_PRESSURE_MSB] << 16;
	altitude |= (databuffer[MPL3115A2_REGISTER_PRESSURE_CSB] << 8) & 0xff00;
	altitude |= (databuffer[MPL3115A2_REGISTER_PRESSURE_LSB] & 0xff);
	altitude >>= 4;
	testProbe(String.format("%06X",altitude));
	a = ((float) altitude) / ((float) 16.0);
	a *= 3.2808;			// Convert Meters to Feet

	return(a);
}

public float getTemperature() throws Exception {
	float t = (float) 0.0;

	// Read data
	snap(MPL3115A2_CTRL_REG1_BAR);	// ALT or BAR does not matter
	readAllRegisters();

	// Convert values
	int temperature = 		// signed 12-bit integer
		databuffer[MPL3115A2_REGISTER_TEMP_MSB] << 8;
	temperature |= databuffer[MPL3115A2_REGISTER_TEMP_LSB] & 0xff;
	temperature >>= 4;
	testProbe(String.format("%04X",temperature));
	t = ((float) temperature) / ((float) 16.0);
	t *= (180.0/100.0);		// Convert degrees C to degrees F
	t += 32.0;

	return(t);
}


/* Class-specific version of the I2CDevice super class. Leverages pi4j.  */
public int read()
	throws IOException {
	return(device.read());
}

public int read(byte[] buffer,int offset,int size)
	throws IOException {
	int rc = device.read(buffer,offset,size);
	if (verbose) {
		System.out.print("read(buffer," + 
			String.format("%d,",offset) +
			String.format("%d)",size) + " = 0x" +
			String.format("%02X",rc & 0xff) + ", ");
		System.out.println(byteArrayToHex(databuffer));
	}
	return rc;
}

public int read(int address)
	throws IOException {
	int d = device.read(address);
	testProbe("0x" + Integer.toHexString(d).toUpperCase() +
		"=read(0x" + Integer.toHexString(address).toUpperCase() + ")");
	return(d);
}

public int read(int address,byte[] buffer,int offset,int size)
	throws IOException {
	int rc = device.read(address,buffer,offset,size);
	if (verbose) {
		System.out.print("read(buffer," + 
			String.format("%02X,",address) + 
			String.format("%d,",offset) +
			String.format("%d)",size) + " = 0x" +
			String.format("%02X",rc & 0xff) + ", ");
		System.out.println(byteArrayToHex(databuffer));
	}
	return rc;
}

public void write(byte b)
	throws IOException {
	device.write(b);
}

public void write(byte[] buffer,int offset,int size)
	throws IOException {
	device.write(buffer,offset,size);
}

public void write(int address,byte b)
	throws IOException {
	testProbe("write(0x" + String.format("%02X",address & 0xff) +
		",0x" + String.format("%02X",b & 0xff) + ")");
	device.write(address,b);
}

public void write(int address,byte[] buffer,int offset,int size)
	throws IOException {
	device.write(address,buffer,offset,size);
}

public static void testProbe(String s) {
	if (verbose) {
		System.out.println(s);
	}
}

}
