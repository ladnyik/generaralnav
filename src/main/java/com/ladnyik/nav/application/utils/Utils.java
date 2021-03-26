package com.ladnyik.nav.application.utils;

import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class Utils {
	
	public static String getCellAsString(Cell cell, FormulaEvaluator evaluator) {
		
		if (cell.getCellType() == CellType.NUMERIC) {
			return Long.toString((long) cell.getNumericCellValue()).trim();		
		}else if (cell.getCellType() == CellType.FORMULA) {
			 switch (evaluator.evaluateFormulaCell(cell)) {
		        case NUMERIC:
		            return Long.toString((long) cell.getNumericCellValue()).trim();
		        case STRING:
		            return cell.getStringCellValue().trim();
		    }
		} else if (cell.getCellType() == CellType.STRING) {
			return cell.getStringCellValue().trim();
		}
		return null;
	}

}
