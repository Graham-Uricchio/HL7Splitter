import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HL7Splitter {

    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter filepath of file to read");
        String inFile = input.nextLine();
        boolean subBool = false;
        int sub = 0;
        FileInputStream fis = new FileInputStream(inFile);
        System.out.println("Enter the value you want to split out");
        System.out.println("or enter 'null' to split on blank value");
        String value = input.nextLine();
        if (value.equalsIgnoreCase("null")){
            value = "";
        }
        System.out.println("Enter the segment you want to split out: ");
        String segment = input.nextLine();
        System.out.println("Enter the field you want to split out: ");
        int field = input.nextInt();
        System.out.println("Would you like yo check a subfield? (Y/N)");
            String subField = input.next();
            if (subField.equalsIgnoreCase("Y")){
                System.out.println("Which subfield would you like to check? ");
                sub = input.nextInt();
                sub = sub - 1;
                subBool = true;
            }
        System.out.println("Enter the name of the file you would like to create: ");
        String fileName = input.next();
        
        
        if (segment.equals("MSH")){
            field--;
        }
        
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            long startTime = System.currentTimeMillis();
            String line = null;
            int passes = 0;
            int passestwo = 0;
            ArrayList<String> message = new ArrayList<String>();
            File fout = new File(fileName);
            FileOutputStream fos = new FileOutputStream(fout);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            while ((line = br.readLine()) != null) {
                String whileSeg = line;
                String[] segParts = whileSeg.split("\\|");
                String[] fieldParts = segParts[sub].split("\\^");
                if(passes==0){
                    message.add(whileSeg);
                    passes++;
                }
                
                else if(segParts[0].equalsIgnoreCase("MSH")){
                    //splitting logic goes here
                    //entire message store in message<ArrayList>
                    boolean print=false;
                    for(String msgLine : message){
                        
                        //matching logic block
                        String spLine[] = msgLine.split("\\|");
                        if (spLine[0].equalsIgnoreCase(segment)){
                            if(subBool){
                              String spField[] = spLine[field].split("\\^");
                              if (spField[sub].equalsIgnoreCase(value)){
                              passes++;
                              print = true;
                              }  
                                
                            }
                            
                            else{
                                if (spLine[field].equalsIgnoreCase(value)){
                                    passes++;
                                    print = true;
                                }
                            }
                        }
                    }
                    
                    if (print){
                        passestwo++;
                        for (String lines : message){
                            osw.write(lines);
                            osw.write("\n");
                        }
                        
                    }
                    
                    //matching and printing logic done
                    //next MSH was encountered: Clear map and rebuild new one
                    message.clear();
                    message.add(whileSeg);
                    passes++;
                }
                
                else if(!segParts[0].equalsIgnoreCase("MSH")){
                    
                    message.add(whileSeg);
                    passes++;
                } 
                    
            }
            System.out.println("Number of messages analyzed: \t" + passes);
            System.out.println("Number of messages split off: \t" + passestwo);
            osw.close();
            fos.close();
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("");
            System.out.println("Time elapsed: " + totalTime/1000 + "s");
        }   catch (IOException ex) {
            Logger.getLogger(HL7Splitter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
             fis.close();
             
    }
}
