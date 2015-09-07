import com.angryelectron.thingspeak.*;

public class ThingspeakWeatherWriter {

public static final int TEMPERATURE_FIELD = 1;
public static final int PRESSURE_FIELD = 2;
public static final int STEAVE_FIELD = 3;
public static final int DS18B20_FIELD = 4;

public static void main (String args[]) throws Exception {
	int channelID = 14919;
	String apiWriteKey = "G5LUVIEZ7UZNEH13";

	MPL3115A2 dev = new MPL3115A2(null);
	DS18B20 ds18b20 = new DS18B20("28-000005abe988");

	Channel channel = new Channel(channelID,apiWriteKey);
	while (true) {
		try {
		Entry entry = new Entry();
		entry.setField(TEMPERATURE_FIELD,
			String.format("%f",dev.getTemperature()));
		entry.setField(PRESSURE_FIELD,
			String.format("%f",dev.getPressure()));
		entry.setField(DS18B20_FIELD,
			String.format("%f",ds18b20.getTemperature()));
		channel.update(entry);
		}
		catch (Exception e) {
			System.out.print(e);
		}
		Thread.sleep(30000);
	}
}

}
