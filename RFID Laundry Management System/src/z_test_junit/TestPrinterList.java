package z_test_junit;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.junit.Test;

public class TestPrinterList {

	@Test(timeout = 1000)
	public void test01() {
		
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);

        for (PrintService printer : printServices)
            System.out.println("Printer: " + printer.getName());
        
        System.out.println();
	}
	
	@Test(timeout = 1000)
	public void test02() {
		
        PrintService[] allServices =
                PrintServiceLookup.lookupPrintServices(null, null);
        	for ( int i = 0; i < allServices.length; i++ ) {
        		PrintService printService = allServices[i];
 
                System.out.println(printService  + " supports :");
                DocFlavor[] flavors = printService.getSupportedDocFlavors();
                for (int j = 0; j < flavors.length; j++ )
                {
                    System.out.println("\t" + flavors[j]);
                }
            }
        	
        System.out.println();
	}
}
