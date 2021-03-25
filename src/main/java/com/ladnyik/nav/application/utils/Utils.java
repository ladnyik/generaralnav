package com.ladnyik.nav.application.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class Utils {
	
	public static String getCellAsString(Cell cell) {

		if (cell.getCellType() == CellType.NUMERIC) {
			return Long.toString((long) cell.getNumericCellValue()).trim();
		} else if (cell.getCellType() == CellType.STRING) {
			return cell.getStringCellValue().trim();
		}
		return null;
	}

}
