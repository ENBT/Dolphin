import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class DataMigrator
{	
	public static Connection Connect(String path) 
	{
        Connection conn = null;
        try 
        {
            String url = "jdbc:sqlite:" + path;
            conn = DriverManager.getConnection(url);
            return conn;
        } 
        catch (SQLException e) 
        {
            System.out.println(e.getMessage());
            return null;
        } 
    }

	public static void Disconnect (Connection conn)
	{
		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static String GetCellTypeString(Cell cell, XSSFWorkbook workBook)
	{
		switch (cell.getCellType())
		    {
		        case NUMERIC:
		        	return "INTEGER";
		        case STRING:
		        	return "TEXT";
		        default:
		        	return "TEXT";
		    }
	}
	
	public static void CreateTable(Connection conn, String excelFilePath,
			String tableName)
	{
		try
		{
			String dropTableStatement = "DROP TABLE IF EXISTS " + tableName + ";";
			Statement statement = conn.createStatement();
			statement.executeUpdate(dropTableStatement);
						
			File input = new File(excelFilePath);
			XSSFWorkbook workBook = new XSSFWorkbook(input);
			XSSFSheet sheet = workBook.getSheetAt(0);
			int numColumns = sheet.getRow(0).getLastCellNum();
			
			String createTableStatement = "CREATE TABLE IF NOT EXISTS " + tableName + 
					"(ID INTEGER PRIMARY KEY AUTOINCREMENT";

			
			Row tableHeadersRow = sheet.getRow(0);
			Row firstDataRow = sheet.getRow(1);
			String[] headerTypes = new String[numColumns];
			String[] headerNames = new String[numColumns];
			for (int i = 0; i < numColumns; i++)
			{
				Cell headerCell = tableHeadersRow.getCell(i);
				Cell firstDataCell = firstDataRow.getCell(i);
				headerTypes[i] = GetCellTypeString(firstDataCell, workBook);
				headerNames[i] = headerCell.getStringCellValue();
				createTableStatement += ", " + headerNames[i] + " " + headerTypes[i];
			}
			createTableStatement += ");";
			
			statement.executeUpdate(createTableStatement);
			
			Row dataRow;
			String insertIntoStatement = "INSERT INTO " + tableName + " (";
			for (int i = 0; i < numColumns; i++)
			{
				insertIntoStatement += headerNames[i];
				if (i != numColumns-1)
				{
					insertIntoStatement += ", ";
				}
			}
			insertIntoStatement += ") VALUES ";
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++)
			{
				dataRow = sheet.getRow(i);
				insertIntoStatement += "(";
				for (int col = 0; col < numColumns; col++)
				{
					if (headerTypes[col] == "TEXT")
					{
						insertIntoStatement += "\"" + dataRow.getCell(col).getStringCellValue() + "\"";
					}
					else
					{
						insertIntoStatement += dataRow.getCell(col).getNumericCellValue();
					}
					if (col != numColumns - 1)
					{
						insertIntoStatement += ", ";
					}
				}
				insertIntoStatement += ")";
				if (i != sheet.getLastRowNum())
				{
					insertIntoStatement += ", ";				
				}
				else
				{
					insertIntoStatement += ";";
				}
			}
			
			statement = conn.createStatement();
			statement.executeUpdate(insertIntoStatement);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		
	}
}
