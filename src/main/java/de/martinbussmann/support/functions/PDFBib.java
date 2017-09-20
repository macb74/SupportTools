package de.martinbussmann.support.functions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

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
 
    public PDFBib(Map<String, String> arguments) {
		DEST = arguments.get("OUT");
		TEMPLATE = arguments.get("TEMPLATE");
		paintPDFBib();
    }

    public void paintPDFBib() {
        PdfDocument pdf;
		try {
			pdf = new PdfDocument(new PdfWriter(new PdfWriter(DEST)));

	        PdfDocument template = new PdfDocument(new PdfReader(TEMPLATE));
	        PdfMerger merger = new PdfMerger(pdf);
	        
	        PdfFont font = PdfFontFactory.createFont(FontProgramFactory.createFont(FontConstants.HELVETICA_BOLD));
	
	        Color fontColor = Color.RED;
	        int fontSize = 150;
	        int page = 1;
	        int maxPage = 100;
	        
	        String text = null;
	        
	        while(page <= maxPage) {
	        	text = Integer.toString(page);
		        merger.merge(template, 1, 1);
		        addBib(pdf, font, fontColor, fontSize, page, text);
		        page++;
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
 
    private static void addBib(PdfDocument pdfDoc, PdfFont font, Color fontColor, int fontSize, int page, String text) throws Exception {

        //int pages = pdfDoc.getNumberOfPages();
        PdfPage pdfPage = pdfDoc.getPage(page);
        Rectangle pagesize = pdfPage.getPageSizeWithRotation();
        pdfPage.setIgnorePageRotationForContent(true);
        float x = (pagesize.getLeft() + pagesize.getRight()) / 2;
        float y = (pagesize.getTop() + pagesize.getBottom()) / 2;
        
        PdfCanvas over = new PdfCanvas(pdfDoc.getPage(page));
        over.setFillColor(fontColor);
        
        Paragraph p = new Paragraph(text).setFont(font).setFontSize(fontSize);
        new Canvas(over, pdfDoc, pdfDoc.getDefaultPageSize())
        	.showTextAligned(p, x, y, 1, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
        over.saveState();
        //over.restoreState();

// 		  Transparent oferlay
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
