import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.util.*;

public class MyTable 
{
	public class MyColumn
	{
		public String header;
		public List<String> data;
		
		
		public MyColumn(String h, int rows)
		{
			header = h;
			data = new ArrayList<String>();
		}
	}
	
	public String name;
	public int numRows;
	public int numColumns;
	public MyColumn[] cols;

	public MyTable(String path)
	{
		try
		{
			File input = new File(path);
			XSSFWorkbook workBook = new XSSFWorkbook(input);
			XSSFSheet sheet = workBook.getSheetAt(0);
			numColumns = sheet.getRow(0).getLastCellNum();
			cols = new MyColumn[numColumns];
			numRows = sheet.getLastRowNum();
			
			for (int i = 0; i < numColumns; i++)
			{
				cols[i] = new MyColumn(sheet.getRow(0).getCell(i).getStringCellValue(), numRows);
			}
			
			for (int row = 1; row < numRows+1; row++)
			{
				for (int col = 0; col < numColumns; col++)
				{
					Cell cell = sheet.getRow(row).getCell(col);
					if (cell.getCellType() == CellType.NUMERIC)
					{
						Double value = sheet.getRow(row).getCell(col).getNumericCellValue();
						Integer valInt = value.intValue();
						cols[col].data.add(valInt.toString());
					}
					else
					{
						cols[col].data.add(sheet.getRow(row).getCell(col).getStringCellValue());
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

	}
	
}
