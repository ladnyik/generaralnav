package com.ladnyik.nav.application.views.feldolgozás;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ladnyik.nav.application.utils.Utils;
import com.ladnyik.nav.application.views.feldolgozás.entity.NyomtatvanyElement;
import com.ladnyik.nav.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.dependency.CssImport;

@Route(value = "process", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Feldolgozás")
@CssImport("./views/feldolgozás/feldolgozás-view.css")
public class FeldolgozásView extends Div {
	
	MemoryBuffer bufferMdf = new MemoryBuffer();
	Upload uploadMdf = new Upload(bufferMdf);
	H2 h2Mdf = new H2("Mező definiciós file");
	VerticalLayout mdfLayout = new VerticalLayout();
	Anchor downloadMdf = new Anchor(); 
	ByteArrayOutputStream mdfBaos;
	String nyomtatvanyMdf;
	
	MemoryBuffer bufferNyomtatvany = new MemoryBuffer();
	Upload uploadNyomtatvany = new Upload(bufferNyomtatvany);
	VerticalLayout nyomtatvanyLayout = new VerticalLayout();
	H2 h2Nyomtatvany = new H2("Nyomtatvány file");
	Anchor downloadNyomtatvany  = new Anchor(); 
	ByteArrayOutputStream mdfNyomtatvany;
	String importString;
	
    public FeldolgozásView() {
        addClassName("feldolgozás-view");
        
        uploadMdf.addSucceededListener(event -> {
        	System.out.println(event.getFileName());
        	System.out.println(bufferMdf.getInputStream());
        	try {
        		mdfLayout.remove(downloadMdf);
        		downloadMdf.setVisible(false);        		
        		mdfBaos = processMdf(bufferMdf.getInputStream());				
				downloadMdf = new Anchor(new StreamResource(nyomtatvanyMdf+"_minta.xlsx", ()-> createMdfResource()), "Download " + nyomtatvanyMdf + "_minta" );
				downloadMdf.setVisible(true);
				mdfLayout.add(downloadMdf);
		        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    
        });
        
        uploadMdf.addFileRejectedListener(event -> {

        });
        
        uploadMdf.getElement().addEventListener("file-remove", event -> {
    		mdfLayout.remove(downloadMdf);
    		downloadMdf.setVisible(false); 
        });
        
        uploadNyomtatvany.addSucceededListener(event -> {

        	try {
       			downloadNyomtatvany.setVisible(false);
        		nyomtatvanyLayout.remove(downloadNyomtatvany);        		    		
        		importString = processNyomtatvany(bufferNyomtatvany.getInputStream()); 				
				downloadNyomtatvany = new Anchor(new StreamResource("out.imp", ()-> createNyomtavanyResource()), "Download " + " out.imp" );
				downloadNyomtatvany.setVisible(true);
				nyomtatvanyLayout.add(downloadNyomtatvany);
		        
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    
        });
        
        
        uploadNyomtatvany.getElement().addEventListener("file-remove", event -> {
   			downloadNyomtatvany.setVisible(false);
    		nyomtatvanyLayout.remove(downloadNyomtatvany); 
        });
        
        add(h2Mdf, uploadMdf, mdfLayout);
        add(h2Nyomtatvany, uploadNyomtatvany,nyomtatvanyLayout);
        
    }
    
    private InputStream createMdfResource(){  
		return new ByteArrayInputStream(mdfBaos.toByteArray());    	
    }
    
    private InputStream createNyomtavanyResource(){  
    	
		try {
			return new ByteArrayInputStream(importString.getBytes("ISO8859-2"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
    }
    
    private String processNyomtatvany(InputStream is) throws IOException{
    	
		StringBuffer sbfHeader = new StringBuffer("");
		StringBuffer sbfLapok = new StringBuffer("");
		StringBuffer sbfKodok = new StringBuffer("");
		int lineNumber=0;
		String nyomtatvanyAzonosito;


		lineNumber++;
		Map<String, Integer> lapok = new TreeMap<String, Integer>();
				
		Workbook workbook = new XSSFWorkbook(is);
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator(); 
		nyomtatvanyAzonosito = workbook.getSheetAt(0).getSheetName();
		sbfHeader.append("$ny_azon=" + nyomtatvanyAzonosito + "\n");
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);
			//System.out.println(sheet.getSheetName());
			boolean lapos = false;
			Iterator<Cell> cellIterator = sheet.getRow(2).iterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (Utils.getCellAsString(cell, evaluator).contains("lapszám")) {
					lapos = true;
					break;
				}				
			}
			
			int pageNumber = 1;
			int rowNum = 4;
			Row codeRow = sheet.getRow(0);
			while (sheet.getRow(rowNum) != null) {
				cellIterator = sheet.getRow(rowNum).iterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell != null) {
						if ( lapos ) {
							if (Utils.getCellAsString(cell, evaluator).length() > 0)
								sbfKodok.append(String.format("%s[%d]=%s\n", Utils.getCellAsString(codeRow.getCell(cell.getColumnIndex()), evaluator), pageNumber, Utils.getCellAsString(cell, evaluator)));
						}
						else	
							if (Utils.getCellAsString(cell, evaluator).length() > 0)
								sbfKodok.append(String.format("%s=%s\n", Utils.getCellAsString(codeRow.getCell(cell.getColumnIndex()), evaluator), Utils.getCellAsString(cell, evaluator)));
						lineNumber++;
					}
				}
				rowNum++;
				pageNumber++;
			}
			if (lapos)
				lapok.put(sheet.getSheetName(), pageNumber-1);
		}

		if (lapok.size() > 0) {			
			int lapokszama = 0; 
			for (Map.Entry<String, Integer> entry : lapok.entrySet()) {			
				String lap = entry.getKey();				
				if (entry.getValue() > 0) {
					lapokszama++;
					System.out.println(lap);
				}
			}
			if (lapokszama > 0) {
				lineNumber++;
				sbfLapok.append(String.format("$d_lapok_száma=%d\n", lapokszama));
			}
		}		
		
		int lapcounter = 1;
		for (Map.Entry<String, Integer> entry : lapok.entrySet()) {			
			String lap = entry.getKey();
			int lapokszama = entry.getValue();
			if (lapokszama > 0) {
				lineNumber++;		
				sbfLapok.append(String.format("$d_lap%d=%s,%d\n", lapcounter, lap, lapokszama ));
				lapcounter++;
			}
		}
		System.out.println(sbfLapok);
		
		lineNumber++;		
		sbfHeader.append(String.format("$sorok_száma=%d\n", lineNumber));
		
		return (sbfHeader.append(sbfLapok).append(sbfKodok)).toString();    	
    }
    
    private ByteArrayOutputStream processMdf(InputStream is) throws IOException {
    	    	
		Map<String, ArrayList<NyomtatvanyElement>> lapoKodok = new LinkedHashMap<String, ArrayList<NyomtatvanyElement>>();
		XSSFWorkbook workbookOut = new XSSFWorkbook();

		String nyomtatvanyNev = null;
		String previousLap = "";
		String actualPage = "";
		Workbook workbook = new XSSFWorkbook(is);
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();		
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			Row currentRow = iterator.next();
			if (!first) {
				if (nyomtatvanyNev == null)
					nyomtatvanyNev = Utils.getCellAsString(currentRow.getCell(5), evaluator);
				actualPage = Utils.getCellAsString(currentRow.getCell(5), evaluator);
				ArrayList<NyomtatvanyElement> kodok;
				if (lapoKodok.containsKey(actualPage)) {
					kodok = lapoKodok.get(actualPage);
				}
				else {
					lapoKodok.put(actualPage, kodok = new ArrayList<NyomtatvanyElement>());
				}
				kodok.add(new NyomtatvanyElement(
						actualPage, 
						Utils.getCellAsString(currentRow.getCell(1), evaluator), 
						Utils.getCellAsString(currentRow.getCell(2), evaluator), 
						Utils.getCellAsString(currentRow.getCell(3), evaluator), 
						Utils.getCellAsString(currentRow.getCell(4), evaluator),
						Utils.getCellAsString(currentRow.getCell(9), evaluator)
						)
				);
			}
			first = false;
		}
		
		System.out.println(lapoKodok.keySet());
		
		for (Map.Entry<String, ArrayList<NyomtatvanyElement>> entry : lapoKodok.entrySet()) {			
			String lap = entry.getKey();
			ArrayList<NyomtatvanyElement> elements = entry.getValue();

			XSSFSheet sheet = workbookOut.createSheet(lap);
			int colNum = 1;
			Row rowKod = sheet.createRow(0);
			Row rowEazon = sheet.createRow(1);
			Row rowLeiras = sheet.createRow(2);
			Row rowTipus = sheet.createRow(3);
			for(NyomtatvanyElement nyomtatvanyElement : elements) {
				Cell cell = rowKod.createCell(colNum);
				cell.setCellValue(nyomtatvanyElement.getKod());
				cell = rowEazon.createCell(colNum);
				cell.setCellValue(nyomtatvanyElement.getEazon());
				cell = rowLeiras.createCell(colNum);
				cell.setCellValue(nyomtatvanyElement.getLeiras());
				cell = rowTipus.createCell(colNum++);
				cell.setCellValue(nyomtatvanyElement.getTipus()+","+nyomtatvanyElement.getMaxlengh());
			}
		} 
		
		for (Map.Entry<String, ArrayList<NyomtatvanyElement>> entry : lapoKodok.entrySet()) {
			String lap = entry.getKey();
			ArrayList<NyomtatvanyElement> elements = entry.getValue();
			Sheet sheet = workbookOut.getSheet(lap);
			for(int j = 0 ; j < elements.size() ; j ++) {
				sheet.autoSizeColumn(j);
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbookOut.write(baos);
		nyomtatvanyMdf = nyomtatvanyNev;
		return baos;
    }

}
