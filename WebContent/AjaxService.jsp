
<%@page import="javax.print.attribute.standard.Finishings"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,PluSoft.Utils.*,java.lang.reflect.*"%>
<% 		
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");	
	 
    String methodName = request.getParameter("method");
    
    try{
	    Class[] argsClass = new Class[2]; 
	    argsClass[0] = HttpServletRequest.class;
	    argsClass[1] = HttpServletResponse.class;
	    
	    Class cls = this.getClass();   
	    Method method = cls.getMethod(methodName, argsClass);   
	    
	    Object[] args = new Object[2];
	    args[0] = request;
	    args[1] = response;   
	    
	    BeforeInvoke(methodName);
	    method.invoke(this, args);     
    }catch( Exception e){
        HashMap result = new HashMap();
        result.put("error", -1);
        result.put("message", e.getMessage());
        result.put("stackTrace", e.getStackTrace());
        String json = PluSoft.Utils.JSON.Encode(result);
        response.reset();
        response.getWriter().write(json);
    }        
    finally
    {
        AfterInvoke(methodName);
    }
%>
<%!
//权限管理
protected void BeforeInvoke(String methodName)
{
    //Hashtable user = GetUser();
    //if (user.role == "admin" && methodName == "remove") throw .      
}
//日志管理
protected void AfterInvoke(String methodName)
{
	
}
//////////////////////////////////////
public void SearchEmployees(HttpServletRequest request, HttpServletResponse response) throws Exception
{ 		
    //查询条件
    String key = request.getParameter("key");
    //分页
    int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
    int pageSize = Integer.parseInt(request.getParameter("pageSize"));  
    //字段排序
    String sortField = request.getParameter("sortField");
    String sortOrder = request.getParameter("sortOrder");
	
    HashMap result = new Test.TestDB().SearchEmployees(key, pageIndex, pageSize, sortField, sortOrder);
    String json = PluSoft.Utils.JSON.Encode(result);
    response.getWriter().write(json);
}
///////////////////////////////////////////////////
public void LoadNodes(HttpServletRequest request, HttpServletResponse response) throws Exception
{ 	
    //获取提交的数据
    String uid = request.getParameter("uid");
    if(StringUtil.isNullOrEmpty(uid)) uid = "-1";

    //获取下一级节点
    String sql = "select * from treedetail where pid = '" + uid + "'";
    ArrayList countries = new Test.TestDB().DBSelect(sql);
    
    ArrayList AllNodes = new ArrayList();
    //判断节点，是否有子节点。如果有，则处理isLeaf和expanded。
    for (int i = 0, l = countries.size(); i < l; i++)
    {
        HashMap node = (HashMap)countries.get(i);
        String nodeUid = node.get("uid").toString();

        String sql2 = "select * from treedetail where pid = '" + nodeUid + "'";
        ArrayList clubs = new Test.TestDB().DBSelect(sql2);
        if (clubs.size() > 0)
        {
			for (int j = 0; j < clubs.size(); j++){
				HashMap lastnode = (HashMap)clubs.get(j);
		        String lastnodeUid = lastnode.get("uid").toString();
		        
		        String sql3 = "select * from treedetail where pid = '" + lastnodeUid + "'";
		        ArrayList players = new Test.TestDB().DBSelect(sql3);
		        AllNodes.addAll(players);
			}
        }
        AllNodes.addAll(clubs);
        
    }
    
    AllNodes.addAll(countries);
    //返回处理结果
    String json = PluSoft.Utils.JSON.Encode(AllNodes);
    response.getWriter().write(json);    
}


public void LazyLoadNodes(HttpServletRequest request, HttpServletResponse response) throws Exception
{
    //获取提交的数据
    String uid = request.getParameter("uid");
    if(StringUtil.isNullOrEmpty(uid)) uid = "-1";

    //获取下一级节点
    String sql = "select * from treedetail where pid = '" + uid + "'";
    ArrayList countries = new Test.TestDB().DBSelect(sql);
    
    //判断节点，是否有子节点。如果有，则处理isLeaf和expanded。
    for (int i = 0, l = countries.size(); i < l; i++)
    {
        HashMap node = (HashMap)countries.get(i);
        String nodeUid = node.get("uid").toString();

        String sql2 = "select * from treedetail where pid = '" + nodeUid + "'";
        ArrayList clubs = new Test.TestDB().DBSelect(sql2);

        if (clubs.size() > 0)
        {
            node.put("isLeaf", false);
            node.put("expanded", false);
        }
    }
    
    //返回处理结果
    String json = PluSoft.Utils.JSON.Encode(countries);
    response.getWriter().write(json);  

}
///////////////////////////////////////////////////
public void SaveEmployees(HttpServletRequest request, HttpServletResponse response) throws Exception
{
	 String studentsStr = request.getParameter("students");
	 ArrayList students = (ArrayList)PluSoft.Utils.JSON.Decode(studentsStr);
	
	 for(int i=0,l=students.size(); i<l; i++){
	 	HashMap student = (HashMap)students.get(i);
	 		
	 	String name = student.get("name").toString();
	 	//
	 	
	 		//判断是插入还是更新对象
    	   int sno = Integer.parseInt((student.get("sno").toString()));
    	   HashMap exsistStudent = new Test.TestDB().GetEmployee(sno);
    	   if(exsistStudent == null){
    		   new Test.TestDB().InsertEmployee(student);
    	   }else{
    		   new Test.TestDB().UpdateEmployee(student);
    	   }
	 }
	
}
public void RemoveEmployees(HttpServletRequest request, HttpServletResponse response) throws Exception
{
    String snoStr = request.getParameter("sno");       
    if (StringUtil.isNullOrEmpty(snoStr)) return;
    String[] snos = snoStr.split(",");
    for (int i = 0, l = snos.length; i < l; i++)
    {
        String sno = snos[i]; 
        new Test.TestDB().DeleteEmployee(sno);
    }    
}
public void GetEmployee(HttpServletRequest request, HttpServletResponse response) throws Exception
{
    int sno = Integer.parseInt(request.getParameter("sno"));
    HashMap user = new Test.TestDB().GetEmployee(sno);
    String json = PluSoft.Utils.JSON.Encode(user);
    response.getWriter().write(json);
}

%> 