package models;

import java.sql.*;
import java.util.HashMap;

import models.fetcher.FlightInfoFetcher;

//import com.sap.dbtech.jdbc.*;
public class HANASQL {
	
	static Connection con = null;
	//-----------------------change for your needs------------------
	private int i;// month
	private String statistable="DELAY_STATISTICS_10_YEARS";//name of your evaluation table
	private String fields="DAY_OF_MONTH,DAY_OF_WEEK, QUARTER, UNIQUE_CARRIER,"+
	 		"ORIGIN, DEST, CRS_DEP_TIME";//no comma at start and end
	private HashMap<String,String> data;
	private String testingYear="2012";
	//-------------------------------------------------
	private String trainingtable;
	private String resulttable;
	private String testingtable;
	
	
	//private String testingview;
	private String trainingview;
	String procP = "_SYS_AFL.PAL_PREDICTWITHDT_Y";
	String procT = "_SYS_AFL.PAL_CREATEDT_Y";
	String match = "SFO";
	private String lowbondyear ="2000";
	FlightQuality fq;
	
	
	public static void main(String[] args){
		HANASQL hana= new HANASQL();
		hana.fq = FlightInfoFetcher.fetch("AA1673","2013-11-8");
		/*for(int month=6;month<=6;month++){
		if(month==5) continue;
		hana.i=month;
		//
		hana.classify(hana.i);
		hana.statisticDevika(hana.i);
	
		}*/
		//hana.train(11);
	hana.classify(11);
		try {
			HANASQL.con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("----all done :P -----------");
	}
	
	 public HANASQL(FlightQuality fq){
		 connectDB();
		 this.fq = fq;	
		 classify(11);
		// result();
		 
	 }
	 public HANASQL(){
		 connectDB();
			 
		 
	 }
	 
	 public void train(int i){
		 //PAL_CDT_TRAINING_TBL
		 
			trainingtable="FLIGHT_"+i;
			String json_model="PAL_CDT_JSONMODEL_SFO_"+i;
			
			
			drop(trainingview,"VIEW");
			
				trainingview="trainingview_"+i;
				createTrainingView(trainingview, trainingtable, match);
			
				
			createControlT();
			createTable(json_model,"PAL_CDT_JSONMODEL_T");
			callProcT(procT, trainingview, json_model);
			System.out.println("training done--");
	 }
	 
	 
	 
	 private void createControlT() {
		String[] st= new String[9];
		 st[1]= " set schema FLIGHT_DELAY";
          st[2] = "CREATE LOCAL TEMPORARY COLUMN TABLE PAL_CONTROL_TBL LIKE PAL_CONTROL_T";
		 st[3]="INSERT INTO PAL_CONTROL_TBL VALUES (\'THREAD_NUMBER\', 2, null, null)";
		  st[4]="INSERT INTO PAL_CONTROL_TBL VALUES (\'PERCENTAGE\',null,1.0,null)";
		  st[5]="INSERT INTO PAL_CONTROL_TBL VALUES (\'IS_SPLIT_MODEL\',1,null,null)";
		  st[6]="INSERT INTO PAL_CONTROL_TBL VALUES (\'PMML_EXPORT\', 2, null, null)";
		  st[7]="INSERT INTO PAL_CONTROL_TBL VALUES (\'CONTINUOUS_COL\',2,25000,null)";
		  st[8]="INSERT INTO PAL_CONTROL_TBL VALUES (\'CONTINUOUS_COL\',2,60000,null)";
		// String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
			drop("PAL_CONTROL_TBL","TABLE");
			 //System.out.println(st2);
			 Statement stmt;
			try {
				stmt = con.createStatement();
				for(int i=1;i<=8;i++)
				stmt.executeUpdate(st[i]);
				
				
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	public void classify(int i){
			if(con==null)
				connectDB();
			//testingtable="FLIGHT_"+i;
			resulttable="RESULT_SFO_"+i;
			
			String json= "PAL_CDT_JSONMODEL_SFO_"+i;
			//hana.test();
			drop(testingtable,"TABLE");
			
				testingtable="TESTINGTABLE_"+i;
				createTestingView( testingtable, match);
			
				createControlP();
			createTable(resulttable,"PAL_PCDT_RESULT_T");
			callProcP(procP, testingtable, resulttable,json);
			result();
			System.out.println("--success-------prediction done----");
	 }
	 public void connectDB(){
		 System.out.println("connecting....");
				 try {
				 Statement stmt;
				 Class.forName("com.sap.db.jdbc.Driver");
				 String url = "jdbc:sap://54.235.127.76:30015/SYSTEM";
				 Connection con =DriverManager.getConnection( url,"SYSTEM", "Cmusv2012");
				 System.out.println("connecting established");
				 System.out.println("Connection: " + con);
				 this.con= con;
				// stmt = con.createStatement();
				 
				// createDB(stmt);
				// insert(stmt);
				 //query(stmt);
				 //con.close();
				 }catch( Exception e ) {
				 e.printStackTrace();
				 }//end catch
				return ;
	 }
	 public void test(){
		 String st= "Select * from \"FLIGHT_DELAY\".\"DELAY_STATISTICS\";";
		 System.out.println(st);
		 Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(st);
			 while(resultSet.next()){
			
			 System.out.println(resultSet.getString("MONTH"));
			 System.out.println(resultSet.getString("MATCHED"));
			 System.out.println("----------");
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
	 }
	 public void drop(String view,String type){
		 String st1= "set schema FLIGHT_DELAY";
		 String st2= "DROP "+type+"  "+view;
			//System.out.println(st2);
			 Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(st1);
				
				int resultSet = stmt.executeUpdate(st2);
				
				/* while(resultSet.next()){
				
				 System.out.println(resultSet.getString("MONTH"));
				 System.out.println(resultSet.getString("MATCHED"));
				 System.out.println("----------");*/
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			 
	 }
	 public void statisticDevika(int month){
		 String st3 = "select COUNT(*) from "+
				 "(select ORIGINALT.\"DEP_DELAY_GROUP\", RESULTT.\"DEP_DELAY_GROUP\","+ 
				 "(CAST(ORIGINALT.\"DEP_DELAY_GROUP\" as INTEGER) - CAST(RESULTT.\"DEP_DELAY_GROUP\" as INTEGER))"+
				  "as Difference from (select * from \"FLIGHT_DELAY\".\""+testingtable+"\" where YEAR="+testingYear+") as ORIGINALT,"+ 
				  "(select * from  \"FLIGHT_DELAY\".\""+resulttable + "\") as RESULTT where ORIGINALT.\"ID\"=RESULTT.\"ID\" and \"ORIGIN\"='"+match+"')"+ 
				  "where Difference>=-1 and Difference<=1";
		 String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
			//String st2= "DROP VIEW  "+view;
					 		//"//+//PAL_PCDT_DATA_TBL;
			String st2=		"select COUNT(*)  from \""+testingtable+"\" where ORIGIN = \'"+match+"\' AND YEAR="+testingYear;
			Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(st1);
				System.out.println(st2);
				ResultSet totalSet = stmt.executeQuery(st2);
				totalSet.next();
				int total = (totalSet.getInt("COUNT(*)"));
				System.out.println(st3);
				ResultSet matchSet = stmt.executeQuery(st3);
				matchSet.next();
				int match = (matchSet.getInt("COUNT(*)"));
				String st4="Delete FROM  "+statistable+" WHERE MONTH=\'"+month+"\'";
				String st5="INSERT INTO "+statistable+" VALUES (  "+month+", "+match+", "+total+" , "+(float)match/(float)total+")";
				System.out.println(st5);
				stmt.executeUpdate(st4);
				stmt.executeUpdate(st5);
				/* while(resultSet.next()){
				
				 System.out.println(resultSet.getString("MONTH"));
				 System.out.println(resultSet.getString("MATCHED"));
				 System.out.println("----------");*/
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 
	 }
	 public void statistic30(int month){
		String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
			//String st2= "DROP VIEW  "+view;
					 		//"//+//PAL_PCDT_DATA_TBL;
			String st2=		"select COUNT(*)  from \""+testingtable+"\" where ORIGIN = \'"+match+"\' AND YEAR="+testingYear;
			String st3=	"select COUNT(*)  FROM \""+resulttable+"\" AS D,  \""+testingtable+
					"\" AS E WHERE D.ID = E.ID AND D.DEP_DELAY_GROUP <= E.DEP_DELAY_GROUP+1 AND D.DEP_DELAY_GROUP >= E.DEP_DELAY_GROUP-1 AND YEAR="+testingYear;
			//System.out.println(st2);
			 Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(st1);
				System.out.println(st2);
				ResultSet totalSet = stmt.executeQuery(st2);
				totalSet.next();
				int total = (totalSet.getInt("COUNT(*)"));
				System.out.println(st3);
				ResultSet matchSet = stmt.executeQuery(st3);
				matchSet.next();
				int match = (matchSet.getInt("COUNT(*)"));
				String st4="Delete FROM  "+statistable+" WHERE MONTH=\'"+month+"\'";
				String st5="INSERT INTO "+statistable+" VALUES (  "+month+", "+match+", "+total+" , "+(float)match/(float)total+")";
				System.out.println(st5);
				stmt.executeUpdate(st4);
				stmt.executeUpdate(st5);
				/* while(resultSet.next()){
				
				 System.out.println(resultSet.getString("MONTH"));
				 System.out.println(resultSet.getString("MATCHED"));
				 System.out.println("----------");*/
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 }
	 public void statisticExact(int month){
			String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
				//String st2= "DROP VIEW  "+view;
						 		//"//+//PAL_PCDT_DATA_TBL;
				String st2=		"select COUNT(*)  from \""+testingtable+"\" where ORIGIN = \'"+match+"\' AND YEAR="+testingYear;
				String st3=	"select COUNT(*)  FROM \""+resulttable+"\" AS D,  \""+testingtable+
						"\" AS E WHERE D.ID = E.ID AND D.DEP_DELAY_GROUP = E.DEP_DELAY_GROUP AND YEAR="+testingYear;
				//System.out.println(st2);
				 Statement stmt;
				try {
					stmt = con.createStatement();
					stmt.executeUpdate(st1);
					System.out.println(st2);
					ResultSet totalSet = stmt.executeQuery(st2);
					totalSet.next();
					int total = (totalSet.getInt("COUNT(*)"));
					System.out.println(st3);
					ResultSet matchSet = stmt.executeQuery(st3);
					matchSet.next();
					int match = (matchSet.getInt("COUNT(*)"));
					String st4="Delete FROM  "+statistable+" WHERE MONTH=\'"+month+"\'";
					String st5="INSERT INTO "+statistable+" VALUES (  "+month+", "+match+", "+total+" , "+(float)match/(float)total+")";
					System.out.println(st5);
					stmt.executeUpdate(st4);
					stmt.executeUpdate(st5);
					/* while(resultSet.next()){
					
					 System.out.println(resultSet.getString("MONTH"));
					 System.out.println(resultSet.getString("MATCHED"));
					 System.out.println("----------");*/
					 
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		 }
	 public void result(){
			String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
				//String st2= "DROP VIEW  "+view;
						 		//"//+//PAL_PCDT_DATA_TBL;
				String st2=		"select *  from \""+resulttable+"\"";
				 Statement stmt;
				try {
					stmt = con.createStatement();
					stmt.executeUpdate(st1);
					System.out.println(st2);
					ResultSet totalSet = stmt.executeQuery(st2);
					totalSet.next();
					int total = (totalSet.getInt("DEP_DELAY_GROUP"));
					System.out.println("----------\n"+total);
					fq.setDelay(total*15, -11);
					/* while(resultSet.next()){
					
					 System.out.println(resultSet.getString("MONTH"));
					 System.out.println(resultSet.getString("MATCHED"));
					 ;*/
					 
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		 }
 public void createTrainingView(String view,String table, String match){
		 
		 String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
		//String st2= "DROP VIEW  "+view;
				 		//"//+//PAL_PCDT_DATA_TBL;
		 String st2="DELETE FROM "+table+" WHERE \"DEP_TIME\" IS NULL or \"CRS_DEP_TIME\" IS NULL";
		String st3=		 "CREATE VIEW "+view+" AS SELECT "+fields+",  DEP_DELAY_GROUP"+
		 		" FROM "+table+" where ORIGIN = \'"+match+"\' AND YEAR<"+testingYear+" AND YEAR >= "+lowbondyear  +" AND CRS_DEP_TIME is not NULL";
		 drop(view,"VIEW");
		System.out.println(st3);
		 Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(st1);
			
			int resultSet = stmt.executeUpdate(st2);
			stmt.executeUpdate(st3);
			/* while(resultSet.next()){
			
			 System.out.println(resultSet.getString("MONTH"));
			 System.out.println(resultSet.getString("MATCHED"));
			 System.out.println("----------");*/
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
 public void creatType(){
	 
 }
	 public void createTestingView(String table, String match){
		 
		 String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
		//String st2= "DROP VIEW  "+view;
				 		//"//+//PAL_PCDT_DATA_TBL;
		 String st2="DELETE FROM "+table+" WHERE \"DEP_TIME\" IS NULL or \"CRS_DEP_TIME\" IS NULL";
		String st3=		" CREATE  COLUMN TABLE "+table+" LIKE PAL_PCDT_DATA_Y";
		  String st4="INSERT INTO "+table+" VALUES(1,"+fq.getDAY_OF_MONTH()+","+fq.getDAY_OF_WEEK()+","+fq.getQuater()+",\'"+fq.getAirline()+"\',\'"+fq.getDepartAirport().iataCode+"\',\'"+fq.getArrivalAirport().iataCode+"\',"+fq.getDepTime()+")";
		 drop(table,"TABLE");
		System.out.println(st4);
		 Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(st1);
			
			//int resultSet = stmt.executeUpdate(st2);
			stmt.executeUpdate(st3);
			stmt.executeUpdate(st4);
			
			/* while(resultSet.next()){
			
			 System.out.println(resultSet.getString("MONTH"));
			 System.out.println(resultSet.getString("MATCHED"));
			 System.out.println("----------");*/
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	 public void callProcT(String proc,String trainingview,  String json_model){
		 String st1= "set schema FLIGHT_DELAY";
		 String st= "CALL "+proc+"("+trainingview+", "+"\"PAL_CONTROL_TBL\","+json_model+",PAL_CDT_PMMLMODEL_TBL ) with overview";
		 Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(st1);
				CallableStatement callstmt = con.prepareCall(st);
				 
				 System.out.println(st);
				 callstmt.execute();
				
				/* while(resultSet.next()){
				
				 System.out.println(resultSet.getString("MONTH"));
				 System.out.println(resultSet.getString("MATCHED"));
				 System.out.println("----------");*/
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 }
	 
	 /*
	  * _SYS_AFL.PAL_PREDICTWITHDT
	  * PAL_PCDT_DATA_TBL
	  * PAL_PCDT_RESULT_TBL
	  */
	 public void callProcP(String proc,String testing,  String result,String json){
		 String st1= "set schema FLIGHT_DELAY";
		 String st= "CALL "+proc+"("+testing+", "+"\"PAL_CONTROL_TBL\","+json+","+ result+" ) with overview";
		 
		 System.out.println(st+"\npredicting..........");
			Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(st1);
				CallableStatement callstmt = con.prepareCall(st);
				 
				 System.out.println(st);
				 callstmt.execute();
				
				/* while(resultSet.next()){
				
				 System.out.println(resultSet.getString("MONTH"));
				 System.out.println(resultSet.getString("MATCHED"));
				 System.out.println("----------");*/
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 }
	 public void createControlP(){
		 String st1= " set schema FLIGHT_DELAY";
         String st2 = "CREATE LOCAL TEMPORARY COLUMN TABLE PAL_CONTROL_TBL LIKE PAL_CONTROL_T";
		 String st3="INSERT INTO PAL_CONTROL_TBL VALUES (\'THREAD_NUMBER\', 2, null, null)";
		// String st1= "set schema FLIGHT_DELAY";//+\"FLIGHT_DELAY\".\"\";"
			drop("PAL_CONTROL_TBL","TABLE");
			 System.out.println(st2);
			 Statement stmt;
			try {
				drop("PAL_CONTROL_TBL","TABLE");
				stmt = con.createStatement();
				stmt.executeUpdate(st1);
				int resultSet = stmt.executeUpdate(st2);
				stmt.executeUpdate(st3);
				/* while(resultSet.next()){
				
				 System.out.println(resultSet.getString("MONTH"));
				 System.out.println(resultSet.getString("MATCHED"));
				 System.out.println("----------");*/
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 	 }
	 
	 //DROP TABLE PAL_CDT_JSONMODEL_TBL;
	 // CREATE COLUMN TABLE PAL_CDT_JSONMODEL_TBL LIKE PAL_CDT_JSONMODEL_T;
 
	 
	 public void createTable(String name,String type){
		 
		 String st2="CREATE COLUMN TABLE "+name+" LIKE "+type;// ";
		 String st1= " set schema FLIGHT_DELAY";
         	System.out.println(st2);
			 
			 Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(st1);
				//existable(name);
				drop(name,"TABLE");
				System.out.println(st2);
				int resultSet = stmt.executeUpdate(st2);
				
				/* while(resultSet.next()){
				
				 System.out.println(resultSet.getString("MONTH"));
				 System.out.println(resultSet.getString("MATCHED"));
				 System.out.println("----------");*/
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
	 }
	 public  Statement update( String st) throws SQLException{
		 
		 Statement stmt = con.createStatement();
		 System.out.println(st);
		 stmt.executeUpdate(st);
		 return stmt;
	 }
public  Statement existable( String table) throws SQLException{
		 String st = "call SYSTEM.existstable("+table+",FLIGHT_DELAY )";
		 System.out.println(st);
		  CallableStatement stmt = con.prepareCall(st);
				 
		 
		 stmt.execute();
		 System.out.println("----success");
		 return stmt;
	 }

	 public static void drop(Statement stmt) throws SQLException{
		 stmt.executeUpdate("DROP DATABASE JunkDB");

	 }
	 
	 public ResultSet query(String st) throws SQLException {
		Statement stmt = con.createStatement();
		 ResultSet rs = stmt.executeQuery(st);
		// System.out.println("Display all results:");
		 return rs;

	 }
	 
	}//end class 