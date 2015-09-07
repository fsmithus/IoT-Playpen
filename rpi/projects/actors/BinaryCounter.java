import com.pi4j.io.gpio.*;

public class BinaryCounter {

public static void main(String args[]) throws Exception {

	final GpioController gpio = GpioFactory.getInstance();

	GpioPinDigitalOutput led[] = {
		gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01,"LED 0",PinState.LOW),
		gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04,"LED 1",PinState.LOW),
		gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05,"LED 2",PinState.LOW),
		gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06,"LED 3",PinState.LOW),
	};

	for (int counter = 0; true; counter++) {
		setLEDs(counter,led);
		Thread.sleep(20);
	}
}

private static void setLEDs(int counter,GpioPinDigitalOutput led[]) {
	int n = counter & 0x0f;
	int mask = 0x01;
	for (int i = 0; i < 4; i++) {
		if ((n & mask) != 0) {
			led[i].setState(PinState.HIGH);
		}
		else {
			led[i].setState(PinState.LOW);
		}
		mask <<= 1;
	}
}
}
