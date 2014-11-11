/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author skot
 */
import java.lang.*;
import java.io.*;

public class jexe {
    
    public static void main(String ... args)
    {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        OutputStream out;
        InputStream in;
        String type = null;
        //String argumentArray[]= {"bash ogremail.sh"};
        //String argumentArray= "bash ./ogremail.sh";
        //String argumentArray[]= {"echo", "go", "fuck", "yourself"};
        //String argumentArray= "ls";
        //String argumentArray = "echo | mailx -s \"OGRE: opponent made their move\" " + "baronvasdeferens@gmail.com";
        //String argumentArray[] = {"echo", "| mailx -s", "\"OGRE: opponent made their move\"" , "baronvasdeferens@gmail.com"};
        String argumentArray = "bash ./ogremail baronvasdeferens@gmail.com";
        try
        {
            //System.out.println(runtime.availableProcessors());
            process = runtime.exec(argumentArray);
            process.waitFor();

            in = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line=null;

            while ( (line = br.readLine()) != null)
            {
                System.out.println(line);    
            } 

            br.close();
            isr.close();
            in.close();
        }
        catch (Exception e)
        {
            System.out.print(e.toString());
        }
        
    }
}
