import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;





public class LockerManager {
	
	HashMap<String, TYPE> config_map = new HashMap<String, TYPE>();
	private String config_file_name = "config.data";
	
	private String user_name = "Tester";
	private File userFolder = new File("Users/" + user_name);
	private File[] listOfFiles;
	
	private SecretKeySpec secretKey;
	private Cipher cipher;


	public  LockerManager(String secret, int length, String algorithm)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
		
		
		try {
			  
            FileInputStream fileIn = new FileInputStream(userFolder + "/" + config_file_name);
            ObjectInputStream objectOut = new ObjectInputStream(fileIn);
            config_map = (HashMap<String,TYPE>) objectOut.readObject();
            objectOut.close();
           // System.out.println("Everything seems to be fine");
           // for(int i = 0 ; i < config_map.size(); i++) {
           // 	System.out.println(config_map.keySet().toArray()[i] + " " + config_map.get(config_map.keySet().toArray()[i]));
            //}
 
        } catch (Exception ex) {
            for(int i = 0; i < listOfFiles.length; i++) {
            	System.out.println("Upon initiating, couldn't open config so everything now is DECRYPTED");
    			config_map.put(listOfFiles[i].getName(), TYPE.DECRYPTED);
    		}
            updateConfigFile();
            ex.printStackTrace();
        }
		
		
		
		byte[] key = new byte[length];
		key = fixSecret(secret, length);
		this.secretKey = new SecretKeySpec(key, algorithm);
		this.cipher = Cipher.getInstance(algorithm);
	}
	
	public String[] getStringFile() {
		
		listOfFiles =  getAllFilesExceptConfigFile();
		LinkedList<String>  temp_list = new LinkedList<String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile()) {
				  
				temp_list.add(listOfFiles[i].getName());
				
			  //  System.out.println("File " + listOfFiles[i].getName());
			  } else if (listOfFiles[i].isDirectory()) {
			   // System.out.println("Directory " + listOfFiles[i].getName());
			  }
		}
		// We want to convert the LinkedList<String> to array String[]
		String[] array_to_return = temp_list.toArray(new String[temp_list.size()]);
		return array_to_return;
		
	}//end of getStringFile()
	
	public void addFile() {
		JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = chooser.showOpenDialog(null);
		
		try{
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				 File afile =new File(chooser.getSelectedFile().getAbsolutePath());
		    		
		    	   if(afile.renameTo(new File( userFolder + "/"  + afile.getName()))){
		    		System.out.println("File is moved successfully!");
		    		updateConfigFile();
		    	   }else{
		    		System.out.println("File is failed to move!");
		    	   }
		    	    
				}
			}catch(Exception e){
		    		e.printStackTrace();
		    	}
		
	}
	
	public void openFile(String fileName) {
		try {
		      Desktop desktop = null;
		      if (Desktop.isDesktopSupported()) {
		        desktop = Desktop.getDesktop();
		      }

		       desktop.open(new File(userFolder + "/" + fileName));
		    } catch (IOException ioe) {
		      ioe.printStackTrace();
		    }
	}
	
	public void deleteFile(String fileName) {
		
		File file = new File(userFolder + "/" + fileName);
		file.delete();
		updateConfigFile();
	}
	
	public void encryptFile(String fileName) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		File file = new File(userFolder + "/" + fileName);
		
		
		if(config_map.get(fileName) == TYPE.ENCRYPTED) {
			System.out.println("The file is already encrypted!");
			return;
		}
		
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		this.writeToFile(file);  
		
		config_map.put(fileName, TYPE.ENCRYPTED);
		updateConfigFile();
		
	}
	
	public void encryptAllFiles() throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		
		//config_map.keySet().toArray()[i] + " " + config_map.get(config_map.keySet().toArray()[i]
		for(int i = 0 ; i < config_map.size() ; i++) {
			if(config_map.get(config_map.keySet().toArray()[i]) == TYPE.DECRYPTED){
				encryptFile(config_map.keySet().toArray()[i].toString());
			}
		}
			
	}
	
	public void decryptFile(String fileName)throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		File file = new File(userFolder + "/" + fileName);
		
		if(config_map.get(fileName) == TYPE.DECRYPTED) {
			System.out.println("The file is already decrypted!");
			return;
		}
	
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		this.writeToFile(file);
		
		config_map.put(fileName, TYPE.DECRYPTED);
		updateConfigFile();
		
	}
	
	public String getUserName() {
		String ending = "";
		if (user_name.length() > 6) ending = "..";
		int maxLength = (user_name.length() < 6)? user_name.length(): 6;
		
		return user_name.substring(0, maxLength) + ending;
	}
	
	private static byte[] readFileToByteArray(File file){
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try{
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();        
            
        }catch(IOException ioExp){
            ioExp.printStackTrace();
        }
        return bArray;
    }
	
	public HashMap<String,TYPE> getConfigMap() {
		return config_map;
	}
	
	public void writeToFile(File f) throws IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream in = new FileInputStream(f);
		byte[] input = new byte[(int) f.length()];
		in.read(input);

		FileOutputStream out = new FileOutputStream(f);
		byte[] output = this.cipher.doFinal(input);
		out.write(output);

		out.flush();
		out.close();
		in.close();
	}
	
	public void updateConfigFile() {
		listOfFiles = getAllFilesExceptConfigFile();
		
		for(int i = 0 ; i < listOfFiles.length ; i++) {
			if(config_map.containsKey(listOfFiles[i].getName())) {
				config_map.put(listOfFiles[i].getName(), config_map.get(listOfFiles[i].getName()));
				//System.out.println("AAAAAAAAAAAAAAA The file " + listOfFiles[i].getName() + " is " + config_map.get(listOfFiles[i].getName()));
			}
			else {
				config_map.put(listOfFiles[i].getName(), TYPE.DECRYPTED);
			}
		}
		
		  try {
			  
	            FileOutputStream fileOut = new FileOutputStream(userFolder + "/" + config_file_name);
	            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
	            objectOut.writeObject(config_map);
	            objectOut.close();
	            System.out.println("The Object  was succesfully written to a file");
	 
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
		  
		  for(int i = 0 ; i < config_map.size(); i++) {
          	System.out.println(config_map.keySet().toArray()[i] + " " + config_map.get(config_map.keySet().toArray()[i]));
          }
		  
		  try {
			  
	            FileInputStream fileIn = new FileInputStream(userFolder + "/" + config_file_name);
	            ObjectInputStream objectOut = new ObjectInputStream(fileIn);
	            config_map = (HashMap<String,TYPE>) objectOut.readObject();
	            objectOut.close();
	            
	            System.out.println("We just read the config FILE after update");
	            for(int i = 0 ; i < config_map.size(); i++) {
	            	System.out.println(config_map.keySet().toArray()[i] + " " + config_map.get(config_map.keySet().toArray()[i]));
	            }
	 
	        } catch (Exception ex) {
	           	System.out.println("Upon initiating, couldn't open config so everything now is DECRYPTED");
	            ex.printStackTrace();
	        }
	}
	
	public File[] getAllFilesExceptConfigFile() {
		
		File[] listOfFiles_to_return = userFolder.listFiles();
		
		for(int i = 0; i < listOfFiles_to_return.length ; i++) {
			if(listOfFiles_to_return[i].getName().equals("config.data")) {
				listOfFiles_to_return = removeElementUsingCollection(listOfFiles_to_return, i);
				return listOfFiles_to_return;
			}
		}
		return new File[0];
	}
	
	public  File[] removeElementUsingCollection( File[] arr, int index ){
        List<File> tempList = new ArrayList<File>(Arrays.asList(arr));
        tempList.remove(index);
        return tempList.toArray(new File[0]);
    }
	
	private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
		if (s.length() < length) {
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) {
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8");
	}

	
}
