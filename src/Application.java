import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Application {
	
	public static final int WIDTH = 400, HEIGHT = WIDTH / 12 * 9; 

	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO Auto-generated method stub
		LockerManager manager = new LockerManager("!@#$MySecr3tPassw0rd", 16, "AES");
		manager.getStringFile();
		
		new Window(WIDTH, HEIGHT, "Secure Locker", manager);
		 
		Runtime.getRuntime().addShutdownHook(new Thread() 
	    { 
	      public void run() 
	      { 
	    	  	
	    	  	
	    	  	try {
					manager.encryptAllFiles();
					System.out.println("All files will be encrypted upon closing..");
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	      } 
	    }); 
		
	}

}
