package Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

import PluSoft.Utils.*;

import java.sql.Clob;


public class TestDB {
	
	public static String driver = "com.mysql.jdbc.Driver";
	public static String url = "jdbc:mysql://localhost/studentinfo?useUnicode=true&characterEncoding=GBK";
	public static String user = "root";
	public static String pwd = "root";


    public HashMap SearchEmployees(String key, int index, int size, String sortField, String sortOrder) throws Exception
    {
        //System.Threading.Thread.Sleep(300);
      	if(key == null) key = "";
      	
      	String sql = 
   "select a.* \n"
  +"from student a \n"
  +"where a.name like '%" + key + "%' \n";

          if (StringUtil.isNullOrEmpty(sortField) == false)
          {
              if ("asc".equals(sortOrder) == false) sortOrder = "desc";
              sql += " order by " + sortField + " " + sortOrder;
          }
          else
          {
              sql += " order by sno asc";
          }
          
          ArrayList dataAll = DBSelect(sql);
          
          ArrayList data = new ArrayList();
          int start = index * size, end = start + size;

          for (int i = 0, l = dataAll.size(); i < l; i++)
          {
              HashMap record = (HashMap)dataAll.get(i);
              if (record == null) continue;
              if (start <= i && i < end)
              {
                  data.add(record);
              }
          }

          HashMap result = new HashMap();
          result.put("data", data);
          result.put("total", dataAll.size());

          //minAge, maxAge, avgAge
          ArrayList ages = DBSelect("select min(age) as minAge, max(age) as maxAge, avg(age) as avgAge from student");
          HashMap ageInfo = (HashMap)ages.get(0);
          result.put("minAge", ageInfo.get("minAge"));
          result.put("maxAge", ageInfo.get("maxAge"));
          result.put("avgAge", ageInfo.get("avgAge"));
          



          return result;
    	
    }
    
    ////////////////////
    public HashMap SearchTrees() throws Exception
    {
    	String sql = "select * from  treedetail";
    	ArrayList dataAll = DBSelect(sql);
        HashMap result = new HashMap();
        result.put("data", dataAll);
        result.put("total", dataAll.size());
    	return result;
    }	
    ////////////////////
    public HashMap GetEmployee(int sno) throws Exception
    {
    	
    	String sql = "select * from student where sno = "+sno;
    	ArrayList data = DBSelect(sql);
    	return data.size() > 0 ? (HashMap)data.get(0) : null;
    }
    public void InsertEmployee(HashMap student) throws Exception
    {
        
    	Connection conn = getConn();	
    	
    	String sql = "insert into student (sno,name,age,birthday,ids,mobile,homePhone,firstletter,agelevel)"
            + " values(?, ?, ?, ?, ?, ?, ?,?,?)";
    	
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.setInt(1, ToInt(student.get("sno")));
		String name = ToString(student.get("name"));
		stmt.setString(2, name);
		int age = ToInt(student.get("age"));
		stmt.setInt(3,age);
		stmt.setTimestamp(4, ToDate(student.get("birthday")));
		stmt.setString(5, ToString(student.get("ids")));
		stmt.setString(6, ToString(student.get("mobile")));
		stmt.setString(7, ToString(student.get("homePhone")));
		//得到首字母大写
		GetFirstLetter getFirstLetter = new GetFirstLetter();
		String fl = getFirstLetter.String2Alpha(name).substring(0, 1).toUpperCase();
		stmt.setString(8, fl);
		//的到年龄等级
		int ageLevel = GetAgeLevel.agelevel(age);
		stmt.setInt(9,ageLevel);
		stmt.executeUpdate();
		
        stmt.close();
		conn.close();
    }
    public void DeleteEmployee(String sno) throws Exception
    {
      
		Connection conn = getConn();		
		Statement stmt = conn.createStatement();
		
        String sql = "delete from student where sno =" + sno;        		
        stmt.executeUpdate(sql);
                
		stmt.close();
		conn.close();
    }
    public void UpdateEmployee(HashMap student) throws Exception
    {
	      HashMap db_student = GetEmployee(Integer.parseInt(student.get("sno").toString()));
	      
	      Iterator iter = student.entrySet().iterator();
	      while (iter.hasNext()) {
	          Map.Entry entry = (Map.Entry) iter.next();
	          Object key = entry.getKey();
	          Object val = entry.getValue();
	          
	          db_student.put(key, val);
	      }         

	      DeleteEmployee(student.get("sno").toString());
	      InsertEmployee(db_student);
    }
    /////////////////////////////////////////////////////////////////
	private Connection getConn() throws Exception{		
		Class.forName(driver).newInstance();
		Connection conn = null;
		if(user == null || user.equals("")){
			conn = java.sql.DriverManager.getConnection(url);
		}else{
			conn = java.sql.DriverManager.getConnection(url, user, pwd);
		}
			
		return conn;
	}	    
	public ArrayList DBSelect(String sql) throws Exception{
    	Connection conn = getConn();		
		Statement stmt = conn.createStatement();
    	
        ResultSet rst = stmt.executeQuery(sql);		
		ArrayList list = ResultSetToList(rst);
		
		rst.close();
		stmt.close();
		conn.close();
		
        return list;
	}
    private static ArrayList ResultSetToList(ResultSet   rs) throws Exception{    	
    	ResultSetMetaData md = rs.getMetaData();
    	int columnCount = md.getColumnCount();
    	ArrayList list = new ArrayList();
    	Map rowData;
    	while(rs.next()){
	    	rowData = new HashMap(columnCount);
	    	for(int i = 1; i <= columnCount; i++)   {	 	    		
	    		Object v = rs.getObject(i);	    		
	    		
	    		if(v != null && (v.getClass() == Date.class || v.getClass() == java.sql.Date.class)){
	    			Timestamp ts= rs.getTimestamp(i);
	    			v = new java.util.Date(ts.getTime());
	    		}else if(v != null && v.getClass() == Clob.class){
	    			v = clob2String((Clob)v);
	    		}
	    		rowData.put(md.getColumnName(i),   v);
	    	}
	    	list.add(rowData);	    	
    	}
    	return list;
	} 	
    private static String clob2String(Clob clob) throws Exception {
        return (clob != null ? clob.getSubString(1, (int) clob.length()) : null);
    }  		    
    private int ToInt(Object o){
    	if(o == null) return 0;
    	double d = Double.parseDouble(o.toString());
    	int i = 0;
		i -= d;
		return -i;			
    }    
    private String ToString(Object o){
    	if(o == null) return "";
    	return o.toString();
    }    
    private Timestamp ToDate(Object o){
    	return o != null ? new java.sql.Timestamp(((Date)o).getTime()) : null;
    }
}
