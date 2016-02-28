# Intel Edison

My goal with this project was to investigate the Intel Edison. The Edison is 32-bit unix computer with
integrated wifi and bluetooth for about $50. There are also numerous add-ons available, such as batteries,
and connectors. This makes it a terrific platform for data collection and gateway projects.

## Shopping List

The following components were chosen to enable a number of mobile data collection projects and includes
wifi, bluetooth, 3-axis linear acceleration, 3-axis rotation, and 3-axis compass (magnetometer).
* Intel Edison - https://www.sparkfun.com/products/13187
* Base Block - https://www.sparkfun.com/products/13045
* Battery Block - https://www.sparkfun.com/products/13037
* 9 Degrees of Freedom Block - https://www.sparkfun.com/products/13033
* Hardware Pack - https://www.sparkfun.com/products/13024

## Setup

### Development Platform

Before starting any project, the physical components must be assembled, and the development environment
configured.

My development machine is an x86 laptop running Red Hat Enterprise Linux 7.
* Download the Arduino IDE.
	* Start at http://playground.arduino.cc//Linux/Fedora
	* Follow the "Download" link in the main menu.
	* Follow the "Linux 64 bits" link on the right side of the page.
	* Donate if you are so inclined, or follow the "Just Download" link.
	* Open with Archive Manager.
	* Extract to your desktop.
	* In a terminal, run the install.sh script.
	* Run the "arduino" command to start the IDE.
* Grant access to USB serial ports.
	* sudo usermod -a -G dialout "non-root-user"
* Install and test terminal.
	* sudo yum install screen
	* Connect USB to host computer and console connector on base block
	* screen /dev/ttyUSB0 115200
	* Hit return a few times then login as root (not password)
* Connect edison to wifi.
	* configure_edison --wifi
	* follow prompts to scan for networks or enter a hidden SSID
	* answer prompts for network type (WPA, WEP), and password, if required
	* curl http://www.redhat.com
	* it's working if HTML comes back
* Configure wifi so that it boots to available networks.
	* http://rwx.io/blog/2015/08/16/edison-wifi-config/

