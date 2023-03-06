package plant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class Serial implements SerialPortDataListener {
	private SerialPort serialPort;
	private BufferedReader input;
	private OutputStream output;
	private String result = "";
	private Boolean process;
	
	Serial(){
		process = false;
	}
	
	public void initSerialPort(String name, int baud) throws Exception {
  	  if (serialPort != null && serialPort.isOpen()) {
  	    closePort();
  	  }
  	  serialPort = SerialPort.getCommPort(name);
  	  serialPort.setParity(SerialPort.NO_PARITY);
  	  serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
  	  serialPort.setNumDataBits(8);
  	  serialPort.addDataListener(this);
  	  serialPort.setBaudRate(baud);
  }
  
  public void closePort() throws Exception {
    if (serialPort != null) {
      serialPort.removeDataListener();
      serialPort.closePort();
    }
  }
  
  public boolean openPort() throws Exception {
      if (serialPort == null) {
    	  Main.getLogger().error("The connection to port wasn't initialized");
          throw new Exception("The connection wasn't initialized");
      }

      if (serialPort.isOpen()) {
    	  Main.getLogger().error("Can not connect, serial port is already open");
          throw new Exception("Can not connect, serial port is already open");
      }

      return serialPort.openPort();
  }
  
  public List<Object> getPortNames() {
	  return Arrays.stream(SerialPort.getCommPorts())
	      .map(SerialPort::getSystemPortName)
	      .collect(Collectors.toList());
  }
  
  public void sendByteImmediately(byte b) throws Exception {
      serialPort.writeBytes(new byte[]{b}, 1);
  }
  
  public void sendStringToComm(String command) throws Exception {
      serialPort.writeBytes(command.getBytes(), command.length());
  }
  
  public boolean isOpen() {
      return serialPort.isOpen();
  }

  @Override
  public int getListeningEvents() {
      return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
  }

  @Override
  public void serialEvent(com.fazecast.jSerialComm.SerialPortEvent event) {
	  if(process) {
		  process = false;
		  result = "";
	  }
      if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
          return;
      }
      
      byte[] newData = new byte[serialPort.bytesAvailable()];
      int numRead = serialPort.readBytes(newData, newData.length);
      result+=new String(newData, StandardCharsets.UTF_8);
      if(result.contains("|")) {
    	  result = result.replace("\n", "");
    	  result = result.replace("|", "\n");
    	  Main.getLogger().debug("Received data: "+result);
	      process = true;
      }
  }
  
  public void lampTurnON() {
	  try {
		  sendStringToComm("L+");
	  }catch(Exception e) {
		  System.out.println(e);
	  }
  }
  
  public void lampTurnOFF() {
	  try {
		  sendStringToComm("L-");
	  }catch(Exception e) {
		  System.out.println(e);
	  }
  }
  
  public String getResult() {
	  String temp = result;
	  result = "";
	  return temp;
  }
  
  public void executeCommand(String command) {
	  try {
		  sendStringToComm(command);
	  }catch(Exception e) {
		  Main.getLogger().error(e.getMessage());
	  }
  }
}
