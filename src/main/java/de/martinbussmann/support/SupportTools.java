package de.martinbussmann.support;

import java.util.HashMap;
import java.util.Map;

import de.martinbussmann.support.pdfbib.PDFBib;
import de.martinbussmann.support.trainingSound.TrainingSound;


public class SupportTools 
{
	
    // Command line options
    private static final String HELP = "-help";
    private static final String DUMMY = "-dummy";
    private static final String TEMPLATE = "-template";
    private static final String OUT = "-out";
    private static final String NUMBERS = "-numbers";
    private static final String LOOPS = "-loops";  
    private static final String START = "-start";  
    private static final String SCALE = "-scale";
    private static final String SOUNDFILE = "-soundfile";

    private static String arg;
	    
    public static void main( String[] args )
    {
        Map<String, String> arguments = new HashMap<String, String>();
        
        for (int i = 0; i < args.length; i++) 
        {
            arg = args[i].trim();
            if (i == 0) 
            {
            	arguments.put("FUNCTION", arg);
            } 
            else if (i == (args.length - 1)) 
            {
                //letztesElement = arg;
            } 
            else if (arg.equals(DUMMY) && ((i + 1) < args.length)) 
            {
            	String dummy = args[i + 1] .trim();
                i += 1;
            }
            else if (arg.equals(TEMPLATE) && ((i + 1) < args.length)) 
            {
            	arguments.put("TEMPLATE", args[i + 1] .trim());
                i += 1;
            }
            else if (arg.equals(OUT) && ((i + 1) < args.length)) 
            {
            	arguments.put("OUT", args[i + 1] .trim());
                i += 1;
            }
            else if (arg.equals(NUMBERS) && ((i + 1) < args.length)) 
            {
            	arguments.put("NUMBERS", args[i + 1] .trim());
            	i += 1;
            }
            else if (arg.equals(LOOPS) && ((i + 1) < args.length)) 
            {
            	arguments.put("LOOPS", args[i + 1] .trim());
            	i += 1;
            }
            else if (arg.equals(SCALE) && ((i + 1) < args.length)) 
            {
            	arguments.put("SCALE", args[i + 1] .trim());
            	i += 1;
            }
            else if (arg.equals(START) && ((i + 1) < args.length)) 
            {
            	arguments.put("START", args[i + 1] .trim());
            	i += 1;
            }
            else if (arg.equals(SOUNDFILE) && ((i + 1) < args.length)) 
            {
            	arguments.put("SOUNDFILE", args[i + 1] .trim());
            	i += 1;
            }
            else 
            {
				usage();
            }
        }
        
        runFunction(arguments);
    }

    private static void runFunction(Map<String, String> arguments) {
    	if(arguments.get("FUNCTION").equals("PDFBib")) {
    	    new PDFBib(arguments);
    	}
    	if(arguments.get("FUNCTION").equals("TrainingSound")) {
    	    new TrainingSound(arguments);
    	}
    }
    
    private static void usage()
    {
        String message = "Usage: java -jar SupportTools-x.y.z.jar <function> [options]\n"
                + "\nFunctions:\n"
                + "PDFBib\n"
                + "  -template <template.pdf>                     : template file\n"
                + "  -out      <output.pdf>                       : output file\n"
        		+ "TimingSound\n"
        		+ "  -loops <5>                                   : \n"
        		+ "  -start <20>                       		      : \n"
        		+ "  -scale <2>                       		      : \n";
        	System.err.println(message);
        	System.exit( 1 );
    }    
}
