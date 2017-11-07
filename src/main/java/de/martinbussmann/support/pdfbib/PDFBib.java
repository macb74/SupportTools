package de.martinbussmann.support.pdfbib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.kernel.geom.Rectangle;

public class PDFBib 
{
    
    private static String DEST;
    private static String TEMPLATE;
    private static String NRFILE;

    
    public static void main( String[] args )
    {
    	Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("OUT", "output.pdf");
		TEMPLATE = arguments.put("TEMPLATE", "template.pdf");
		NRFILE = arguments.put("NUMBERS", "numbers.csv");
    	new PDFBib(arguments);
    }
    
    public PDFBib(Map<String, String> arguments) {
		DEST = arguments.get("OUT");
		TEMPLATE = arguments.get("TEMPLATE");
		NRFILE = arguments.get("NUMBERS");
		paintPDFBib();
    }

    public void paintPDFBib() {
        PdfDocument pdf;
        
		try {
	        List<String> numbers = getNumbers();
			
		pdf = new PdfDocument(new PdfWriter(new PdfWriter(DEST)));

	        PdfDocument template = new PdfDocument(new PdfReader(TEMPLATE));
	        PdfMerger merger = new PdfMerger(pdf);
	        
	        PdfFont font = PdfFontFactory.createFont(FontProgramFactory.createFont(FontConstants.HELVETICA_BOLD));

	        Color fontColor = Color.BLACK;
	        int line = 0;
	        int fontSize = 240;
	        String text = null;
	        
	        while(line < numbers.size()) {
                text = numbers.get(line).split(",")[0].trim();
		        fontColor = getColor(numbers.get(line).split(",")[1].trim().toUpperCase());
		        int templatePage = Integer.parseInt(numbers.get(line).split(",")[2].trim());
		        merger.merge(template, templatePage, templatePage);
		        addBib(pdf, font, fontColor, fontSize, line+1, text);
		        line++;
	        }
	        
	        pdf.close();
        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
    private Color getColor(String color) {
        Map<String, Color> colors = new HashMap<String, Color>();
		colors.put("BLUE",  Color.BLUE);
		colors.put("RED",   Color.RED);
		colors.put("GREEN", Color.GREEN);
		colors.put("BLACK", Color.BLACK);
		return colors.get(color);
	}

	private List<String> getNumbers() {

		List<String> list = null;
    	try {
        	Path path = Paths.get(NRFILE);
			Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
			list = lines.collect(Collectors.toList());
			lines.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private static void addBib(PdfDocument pdfDoc, PdfFont font, Color fontColor, int fontSize, int page, String text) {

        //int pages = pdfDoc.getNumberOfPages();
        PdfPage pdfPage = pdfDoc.getPage(page);
        Rectangle pagesize = pdfPage.getPageSizeWithRotation();
        pdfPage.setIgnorePageRotationForContent(true);
        float x = (pagesize.getLeft() + pagesize.getRight()) / 2;
        float y = ((pagesize.getTop() + pagesize.getBottom()) / 2) - 35;
        
        PdfCanvas over = new PdfCanvas(pdfDoc.getPage(page));
        over.setFillColor(fontColor);
        
        Paragraph p = new Paragraph(text).setFont(font).setFontSize(fontSize);
        new Canvas(over, pdfDoc, pdfDoc.getDefaultPageSize())
        	.showTextAligned(p, x, y, 1, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
        over.saveState();
        //over.restoreState();

// 	  Transparent oferlay
//        p = new Paragraph("This TRANSPARENT watermark is added ON TOP OF the existing content").setFont(font).setFontSize(15);
//        over.saveState();
//        PdfExtGState gs1 = new PdfExtGState();
//        gs1.setFillOpacity(0.5f);
//        over.setExtGState(gs1);
//        new Canvas(over, pdfDoc, pdfDoc.getDefaultPageSize()).showTextAligned(p, 10, 10, 1, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
//        over.restoreState();


    }
    
    

	public void setDest(String dest) {
		DEST = dest;
	}
	
	public void setTemplate(String template) {
		TEMPLATE = template;
	}
}
