import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.sqlite.core.DB;
import java.util.*;
public class DataParser
{	
	public static int FindAverageScore(MyTable testScores, MyTable retakeScores)
	{
		String[] retakeIds = new String[retakeScores.numRows];
		for (int i = 0; i < retakeScores.numRows; i++)
		{
			retakeIds[i] = retakeScores.cols[0].data.get(i);
		}
		
		int[] finalScores = new int[testScores.numRows];
		for (int i = 0; i < testScores.numRows; i++)
		{
			int origScore = (int)Double.parseDouble((testScores.cols[1].data.get(i)));
			finalScores[i] = origScore;

			String id = testScores.cols[0].data.get(i);
			if (retakeScores.cols[0].data.contains((testScores.cols[0].data.get(i))))
			{
				int retakeScoreIndex = retakeScores.cols[0].data.indexOf(id);
				if (Double.parseDouble(retakeScores.cols[1].data.get(retakeScoreIndex)) > origScore)
				{
					finalScores[i] = (int)Double.parseDouble(retakeScores.cols[1].data.get(retakeScoreIndex));
				}
				else
				{
					finalScores[i] = origScore;
				}
			}
		}
		int avScore = 0;
		for (int i = 0; i < finalScores.length; i++)
		{
			avScore += finalScores[i];
		}
		
		return avScore / finalScores.length;
	}
	
	public static int FindAverageScore(Connection conn)
	{
		String scoresQuery = "" + 
				"SELECT\r\n" + 
				"	score,\r\n" + 
				"	retakeScore\r\n" + 
				"FROM (SELECT \r\n" + 
				"		  *\r\n" + 
				"	   FROM\r\n" + 
				"		  TestScores ts \r\n" + 
				"		  LEFT OUTER JOIN (SELECT \r\n" + 
				"							   orig.studentId as retakeId,\r\n" + 
				"							   retake.score AS retakeScore\r\n" + 
				"						   FROM\r\n" + 
				"							   TestScores orig\r\n" + 
				"							   INNER JOIN RetakeScores retake ON orig.studentId == retake.studentId\r\n" + 
				"						  ) nt ON nt.retakeId == ts.studentId\r\n" + 
				"	  )";
		
		try
		{
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(scoresQuery);
			List<Integer> finalScores = new ArrayList<Integer>();
			while (resultSet.next())
			{
				int origScore = resultSet.getInt("score");
				int retakeScore = resultSet.getInt("retakeScore");
				if (retakeScore > origScore)
				{
					finalScores.add(retakeScore);
				}
				else
				{
					finalScores.add(origScore);
				}
			}
			int average = 0;
			for (int i = 0; i < finalScores.size(); i++)
			{
				average += finalScores.get(i);
			}
			average /= finalScores.size();
			return average;
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return -1;
		}
	}
	
	public static String[] FindFemaleComputeScienceMajors(MyTable studentInfo)
	{
		ArrayList<String> compSciMajorIds = new ArrayList<String>();
		
		for (int i = 0; i < studentInfo.numRows; i++)
		{
			if (studentInfo.cols[1].data.get(i).contains("computer science") &&
					studentInfo.cols[2].data.get(i).contains("F"))
			{
				compSciMajorIds.add(studentInfo.cols[0].data.get(i));	
			}
		}
		
		Collections.sort(compSciMajorIds);
		
		return compSciMajorIds.toArray(new String[0]);
	}
	
	public static String[] FindFemaleComputerScienceMajors(Connection conn)
	{
		String queryString = "" +
				"			SELECT \r\n" + 
				"				studentId\r\n" + 
				"			FROM\r\n" + 
				"				StudentInfo\r\n" + 
				"			WHERE \r\n" + 
				"				major == \"computer science\" and gender == \"F\"\r\n" + 
				"			ORDER BY studentId ASC;";
		
		try
		{
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(queryString);
			
			List<String> compSciMajorIds = new ArrayList<String>();
			while(resultSet.next())
			{
				compSciMajorIds.add(resultSet.getString("studentId"));
			}
			return compSciMajorIds.toArray(new String[0]);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
}
