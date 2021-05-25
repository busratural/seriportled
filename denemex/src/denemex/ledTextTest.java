package denemex;
import java.awt.EventQueue;
import java.awt.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;


import java.util.ArrayList;
import java.util.Scanner; 

import static java.nio.charset.StandardCharsets.*;


public class ledTextTest {
	private static final Charset utf8charset = Charset.forName("UTF-8");
	private static final Charset iso88591charset = Charset.forName("ISO-8859-1");
	private Serial serial;
    private GpioController gpio;
    private GpioPinDigitalInput kapiButonu;
    private GpioPinDigitalInput takometre;
    private int gidilenMesafe=0;
    private int takometreSabiti=50;
    private int kalanMesafe=500;
    private int toplamMesafe=500;
    public String hedefIStasyon="10MY010015MESCIDISELAM";
    public String gelecekIstasyon="20MY010015CEBECI";
   // public String sonrakiIstasyon="10MY010015SULTANCIFT.";
    public static int count;
 public int counter =1;
    private static ArrayList<String> Istasyonlar = new ArrayList();
    
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
             
            ledTextTest text=new ledTextTest(); 
            }
  
    });
}
	public ledTextTest() {
		    Istasyonlar.add("20MY010015TOPKAPI");
	        Istasyonlar.add("20MY010015FETIHKAPI");
	        Istasyonlar.add("20MY010015VATAN");
	        Istasyonlar.add("20MY010015EDIRNEKAPI");
	        Istasyonlar.add("20MY010015SEHITLIK");
	        Istasyonlar.add("20MY010015DEMIRKAPI");
	        Istasyonlar.add("20MY010015TOPCULAR");
	        Istasyonlar.add("20MY010015RAMI");
	        Istasyonlar.add("20MY010015ULUYOL");
	        Istasyonlar.add("20MY010015SAGMALCILAR");
	        Istasyonlar.add("20MY010015CUKURCESME");
	        Istasyonlar.add("20MY010015A.FUATBASGIL");
	        Istasyonlar.add("20MY010015TASKOPRU");
	        Istasyonlar.add("20MY010015KARADENIZ");
	        Istasyonlar.add("20MY010015KIPTAS-VEN");
	        Istasyonlar.add("20MY010015CUMHURIYET");
	        Istasyonlar.add("20MY01001550.YIL");
	        Istasyonlar.add("20MY010015HACISUKRU");
	        Istasyonlar.add("20MY010015BAHCELIEVLER");
	        Istasyonlar.add("20MY010015SULTANCIFT.");
	        Istasyonlar.add("20MY010015CEBECI");
	        Istasyonlar.add("20MY010015MESCIDISELAM");
	        gelecekIstasyon=Istasyonlar.get(0);
		//KapiDurumuGuncelle(false);
	//KapiDurumuGuncelle(true);
		 if (!OSValidator.isWindows()) {
	            this.InitializeIO();
	        	Timer myTimer1=new Timer();
	            TimerTask gorev1 =new TimerTask() {   
	                   @Override
	                   public void run() {  
	               		ledTextTest.this.writeToSP(hedefIStasyon);
	                	   
	        }
	    };       
	    myTimer1.schedule(gorev1,0,30000);	      
	    }
		 
		 else if(OSValidator.isWindows()) {
				Timer myTimer=new Timer();
		        TimerTask gorev =new TimerTask() {   
		               @Override
		               public void run() {
		     
		              		ledTextTest.this.writeToSP(hedefIStasyon+" 1.  hedef istasyon");
		    }
		};

		myTimer.schedule(gorev,0,30000);
	     Timer myTimer1=new Timer();
         TimerTask gorev1 =new TimerTask() {   
                @Override
                public void run() {  
                	TakometreVerisiGuncelle();
     }
 };       
 myTimer1.schedule(gorev1,0,500);
		 }
	}

public void InitializeIO() {
	
	
	// this.gpio.shutdown();
	 //this.gpio.unprovisionPin(this.gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_29, PinPullResistance.PULL_UP));
	// this.gpio.unprovisionPin(this.gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_28, PinPullResistance.PULL_UP));
	// this.gpio = GpioFactory.getInstance();
	 GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
	 this.gpio = GpioFactory.getInstance();

	 this.kapiButonu = this.gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_20, PinPullResistance.PULL_UP);
     this.takometre = this.gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_21, PinPullResistance.PULL_UP);
        this.serial = SerialFactory.createInstance();
        if (this.serial != null) {
            try {
				this.serial.open("/dev/ttyAMA0", 4800);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            System.out.println("Serial port null");
        }
        
        
        this.kapiButonu.addListener(new GpioPinListener[]{new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                	ledTextTest.this.KapiDurumuDegistir(false);
                    System.out.println(" --> KAPI BUTONU: " + event.getPin() + " = " + event.getState());

                } else {
                	ledTextTest.this.KapiDurumuDegistir(true);
                    System.out.println(" --> KAPI BUTONU: " + event.getPin() + " = " + event.getState());

                }

            }
        }});
        this.takometre.addListener(new GpioPinListener[]{new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                    System.out.println(" --> TAKOMETRE: " + event.getPin() + " = " + event.getState());

                	ledTextTest.this.TakometreArttýr();
                }

            }
        }});
        this.serial.addListener(new SerialDataEventListener() { 
        	@Override
            public void dataReceived(SerialDataEvent event) {

            try {
            	 System.out.println("[HEX DATA]   " + event.getHexByteString());
            	 System.out.println("[ASCII DATA] " + event.getAsciiString());
                 System.out.println("SP: " + event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }});
        /*this.writeToSP(" ");    */ 

    }
private void KapiDurumuDegistir(final boolean durum) {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
        	ledTextTest.this.KapiDurumuGuncelle(durum);
        }
    });
}  private void TakometreArttýr() {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
        	ledTextTest.this.TakometreVerisiGuncelle();
        }
    });
}
public void TakometreVerisiGuncelle() {
        this.gidilenMesafe += takometreSabiti;
        this.kalanMesafe = this.toplamMesafe - this.gidilenMesafe;
       // ledTextTest.this.writeToSP(hedefIStasyon);
        if (this.kalanMesafe == 100) {
	           ledTextTest.this.writeToSP(gelecekIstasyon);

        	count=0;
                	Timer myTimer=new Timer();
                    TimerTask gorev =new TimerTask() {   
                           @Override
                           public void run() {  
                        	// System.out.println("butona týklandý veri göndermeye hazýr---> takometre verisi : " + gelecekIstasyon);
                        	   count++;
                        	   if(count==5) {
                       	           ledTextTest.this.writeToSP("20MY010015 ");
                        		 myTimer.cancel(); 
                        	   }
                }
            };       
            myTimer.schedule(gorev,0,1000);

                }
        else if(this.kalanMesafe==0) {
        	
        	this.gelecekIstasyon =Istasyonlar.get(counter);
	       //this.gelecekIstasyon=this.sonrakiIstasyon;    	
           this.kalanMesafe=500;
     	   this.toplamMesafe=500;
     	   this.gidilenMesafe=0;
     	   counter++;
     	  
        }
           
}
	 public void KapiDurumuGuncelle(boolean durum) {
       // ledTextTest.this.writeToSP(hedefIStasyon);
       
    if (durum) {
           ledTextTest.this.writeToSP(gelecekIstasyon);

    	count=0;
            	Timer myTimer=new Timer();
                TimerTask gorev =new TimerTask() {   
                       @Override
                       public void run() {     
                      	// System.out.println("butona týklandý veri göndermeye hazýr---> kapý verisi : " + gelecekIstasyon);
                    	   count++;
                    	   if(count==5) {
                   	           ledTextTest.this.writeToSP("20MY010015 ");
                      		 myTimer.cancel(); 
                    	   }
            }
        };       
        myTimer.schedule(gorev,0,1000);
    }
}
	 public void writeToSP(String data) {
		 int STX = 0x2;
         int ETX = 0x3;
         int ENQ = 0x5;
         int NL = 0xD;
         int LF = 0xA;
	        try {
	            if (this.serial != null && this.serial.isClosed()) {
	                try {
						this.serial.open("/dev/ttyAMA0", 4800);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }

	            if (this.serial != null && this.serial.isOpen()) {
	            data = data.toUpperCase();

	                try {
	                    this.serial.flush();

	       			 byte[] bytes= data.getBytes();
	       			 byte[] utf8 = new String(bytes, "UTF-8").getBytes("ISO-8859-9");
	        
	       			 String HexPaket = Integer.toHexString(xorDatas(utf8)).toUpperCase();
	       	            if (HexPaket.length() < 2)
	       	            {
	       	                HexPaket = '0' + HexPaket;
	       	            }
	       	            String CommandCall = Character.toString((char)STX) + data +  Character.toString((char)ENQ) + HexPaket +  Character.toString((char)ETX) +  Character.toString((char)NL) +  Character.toString((char)LF);	       	            
	       	            System.out.println(CommandCall);	       	            
	       	            
	                    this.serial.write(CommandCall);
	                } catch (UnsupportedEncodingException var5) {
	                    var5.printStackTrace();
	                } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
	            } else {
	            		            	 
	                System.out.println("Seri port kapalý! Data: " + data);
	            }
	        } catch (IllegalStateException var6) {
	            var6.printStackTrace();
	        }

	    }	 
	 private int xorDatas(byte[] data)
	    {
	        int temp = data[0];
	        for (int i = 1; i < data.length; i++)
	        {
	            temp = temp ^ data[i];
	        }
	        return temp;
	    }
}