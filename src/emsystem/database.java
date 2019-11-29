package emsystem;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.IOUtils;

import org.postgresql.util.PSQLException;
import org.xml.sax.SAXException;





public class database {
	
	String url;
	String user;
	String password;
	Connection con;
	Statement stmt;
	Statement stmt1;
	

	
	public database () throws ParserConfigurationException, SAXException, IOException {
		
		Setting.Loadsetting();
		url=Setting.databaseUrl;
		password=Setting.databasePassword;
		user=Setting.databaseUserName;
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			con = DriverManager.getConnection(url, user, password);
		//this is important
			con.setAutoCommit(true);
			stmt = con.createStatement();
			stmt1=con.createStatement();
		
//		int i=1;
		
			} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
		// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
		// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	public int getid(String table) throws SQLException
	{
		int newid=0;
		String query="SELECT max(id) from " + table;
	    System.out.println("query "+ query);
		ResultSet resultSet=stmt.executeQuery(query);
		while(resultSet.next())
		{
			query= resultSet.getString("max");
		}
		if(query==null)
			newid=1;
		else
			newid= Integer.valueOf(query)+1;
		
		System.out.println("id number in db "+ newid);
		return newid;

	}
	
	public void gettaxid(String title) throws SQLException
	{
		String query="select * from species where scientific_name="+"'"+title+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		String common_name;
		String tax_id;
		while(resultSet.next())
		{
			tax_id= resultSet.getString("tax_id");
			common_name= resultSet.getString("common_name");
			System.out.println(tax_id +"       "+common_name);
		}
		
	}
	
	public String getsubmitterid(String email) throws SQLException
	{
		String query="select * from gigadb_user where email="+"'"+email+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);

		String submitter_id=null;
		while(resultSet.next())
		{
			submitter_id= resultSet.getString("submitter_id");
			
		
		}
		return submitter_id;
		
	}
	
	public String get_status(int id) throws SQLException
	{
		String query="select upload_status from dataset where id="+id+";";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);

		String upload_status=null;
		while(resultSet.next())
		{
			upload_status= resultSet.getString("upload_status");
			
		
		}
		return upload_status;
		
	}
	
	public String getfunder(String url) throws SQLException
	{
		String query="select * from funder_name where uri="+"'"+url+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
	
		String tt=null;
		while(resultSet.next())
		{
			tt= resultSet.getString("uri");
			
		}
		
		return tt;
		
	}
	public void addfile_md5(int file_id, String value) throws SQLException
	{
	  
		
		
		PreparedStatement prep1= null;
        String query1="insert into file_attributes(file_id, attribute_id, value) values(?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, file_id);
		prep1.setInt(2, 605);
		prep1.setString(3, value);
		
		prep1.executeUpdate();
		
	
	}
	
	public int getspecies_id(int tax_id) throws SQLException
	{
		String query="select id from species where tax_id="+tax_id+";";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		int id=0;
		while(resultSet.next())
		{
			id= resultSet.getInt("id");
	
		}
		
		return id;

	}
	
	
	public int getsample_id(String name) throws SQLException
	{
		String query="select id from sample where lower(name)="+"lower('"+name+"');";
		System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		int id=0;
		while(resultSet.next())
		{
			id= resultSet.getInt("id");
	
		}
		
		return id;

	}
	public int getsample_idwithdataset(String name, int dataset_id) throws SQLException
	{
		String query="select id from sample where lower(name)="+"lower('"+name+"') and id in (select sample_id from dataset_sample where dataset_id="+dataset_id+");";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		int id=0;
		while(resultSet.next())
		{
			id= resultSet.getInt("id");
	
		}
		
		return id;

	}
	
	

	
	public int file_get_sampleid(int datasetid, String code) throws SQLException
	{
		String query="select sample.id from sample, dataset_sample where sample.id=dataset_sample.sample_id and sample.name like "+"'"+code+"%'"+" and dataset_sample.dataset_id="+datasetid+";";
		System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		int id=0;
	
		while(resultSet.next())
		{
			id=resultSet.getInt("id");
		
		}
		if(id==0)
		{
			System.out.println("Can't find sample in talble" + code);
		}
		
		System.out.println(id);
		return id;

	}
	
	
	public int getattribute_id(String name) throws SQLException
	{
		String query="select id from attribute where lower(attribute_name)="+"lower('"+name+"');";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		int id=0;
		while(resultSet.next())
		{
			id= resultSet.getInt("id");
	
		}
		if(id==0)
		{
			
			String query1="select id from attribute where lower(structured_comment_name)="+"lower('"+name+"');";
			//System.out.println("query"+query);
			ResultSet resultSet1=stmt.executeQuery(query1);
			
			while(resultSet1.next())
			{
				id= resultSet1.getInt("id");
		
			}
		}
		
		return id;

	}
	

			
	
	
	public String geturlcontent(String url) throws IOException{
		
		URL url1= new URL(url);
		URLConnection conn = url1.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputline=null;
		while ((inputline = br.readLine()) != null) {
          //  System.out.println(inputline);
            return inputline;
			}
		br.close();
		return inputline;
    
		
		
		
		
	}
	
	
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

    

	
	public String getunit_id(String name) throws SQLException
	{
		String query="select id from unit where name="+"'"+name+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		String id= null;
		while(resultSet.next())
		{
			id= resultSet.getString("id");
	
		}
		
		return id;

	}
	
	public String getfile_id(String name) throws SQLException
	{
		String query="select id from file where location="+"'"+name+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		String id= null;
		while(resultSet.next())
		{
			id= resultSet.getString("id");
	
		}
		
		return id;

	}
	
	public String getfile_location(String name) throws SQLException
	{
		String query="select location from file where name="+"'"+name+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		String id= null;
		while(resultSet.next())
		{
			id= resultSet.getString("location");
	
		}
		
		return id;

	}
	
	public String getfile_id_from_filename(String name,int datasetid) throws SQLException
	{
		String query="select id from file where name="+"'"+name+"' and dataset_id="+"'"+datasetid+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		String id= null;
		while(resultSet.next())
		{
			id= resultSet.getString("id");
	
		}
		
		return id;

	}
	
	public int gettypeid(String name) throws SQLException
	{
		String query="select * from file_type where name="+"'"+name+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		int id=0;
		while(resultSet.next())
		{
			id= resultSet.getInt("id");
	
		}
		if(id==0)
		{
		    
			addtype(name);
			resultSet=stmt.executeQuery(query);
			
			
			while(resultSet.next())
			{
				id= resultSet.getInt("id");
		
			}
		}
		return id;
		
	}
	

	
	public int getformatid(String name) throws SQLException
	{
		String query="select * from file_format where name="+"'"+name+"';";
		//System.out.println("query"+query);
		ResultSet resultSet=stmt.executeQuery(query);
		
		int id=0;
		while(resultSet.next())
		{
			id= resultSet.getInt("id");
	
		}
		if(id==0)
			System.out.println("Need add new format "+ name );
		return id;
		
	}
	
	@SuppressWarnings("unchecked")
	public void upload_sample_attribute() throws SQLException
	{
		String query= "select id,s_attrs from sample order by id;";
		ResultSet rs = null;
		int sampleid=0;
		String code=null;
		//System.out.println(query);
		
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			sampleid= rs.getInt(1);
			code= rs.getString(2);
			
			//System.out.println(sampleid);
			//System.out.println(sampleid+" "+code);
			
			if(code==null || code.equals(""))
			{
				continue;
			}
			
			if(!code.equals(null))
			{
				int start=0;
				int startvalue=0;
				int end=0;
				int i=0;
				
				@SuppressWarnings("rawtypes")
				Map<String, String> attrs= new HashMap<String,String>();
				while(i < code.length())
				{
					if(String.valueOf(code.charAt(i)).equals("="))
					{
						String key= code.substring(start, i);
						while(!String.valueOf(code.charAt(i)).equals("\""))
						{
							++i;
						}
						++i;
						startvalue=i;
						while(!String.valueOf(code.charAt(i)).equals("\""))
						{
							++i;
						}
						String value= code.substring(startvalue, i);
						key.replaceAll(" ", "");
						attrs.put(key, value);
						//System.out.println(key+" "+value);
						while(i<code.length() && !String.valueOf(code.charAt(i)).equals(","))
						{
							++i;
						}
						start=i+1;
					}
					
					++i;
				}
				
				for(String keyattrs: attrs.keySet())
				{
					
					keyattrs=keyattrs.trim();
					String query1="select id from attribute where lower(attribute_name)="+"lower('"+keyattrs+"');";
					//System.out.println(query1);
					Statement stmt1 = con.createStatement();
					int attributeid=0;
					ResultSet rs2= null;
					rs2= stmt1.executeQuery(query1);
					while(rs2.next())
					{
						attributeid=rs2.getInt(1);
					}
					if(attributeid!=0)
					{
						//System.out.println(keyattrs);
					}
					else
						System.out.println(keyattrs);
						
					
				}
			
				
				
				
				/*
				String[] aa = code.split(",");
				for(int i=0; i<aa.length;++i)
				{
					String[] bb = aa[i].split("=");
					if(bb[1].contains("\"")){
					bb[1]=bb[1].replace("\"", "");
					}
					System.out.println(bb[0]+"="+bb[1]);
					*/
					/*
					String query1="update file set code ="+sampleid+" where id="+fileid+";";
					System.out.println(query1);

					PreparedStatement prep = con.prepareStatement(query1);
					prep.executeUpdate();
					*/
					
				}
				
			}
			
				
		
	}
	
	
	public void update(String item1,String item2, String item3, String item4, String tablename, String title[]) throws SQLException
	{
		
		String query="update "+tablename+" set "+title[1]+"="+item2+", "+ title[2]+"="+"'"+item3+"'"+", "+title[3]+"="+"'"+item4+"'"+" where "+ title[0]+"=" + item1+";";
		//System.out.println(query);
		//stmt.executeQuery(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
		
		
		
	}
	
	public void updatev3user(String email, Boolean a, Boolean b, Boolean c) throws SQLException
	{
		String query="update gigadb_user set is_activated="+a+", newsletter="+b+", previous_newsletter_state="+c+" where email="+"'"+email+"'";
		System.out.println(query);
		//stmt.executeQuery(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
		
	}
	
	public void update_dataset_status(int datasetid, String status) throws SQLException
	{
		String query="update dataset set upload_status='"+status+"' where id="+datasetid+";";
		System.out.println(query);
		//stmt.executeQuery(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void addtype(String name) throws SQLException
	{
		
		
		
	    
		
		PreparedStatement prep1= null;
        String query1="insert into file_type(name) values(?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setString(1, name);
		
		prep1.executeUpdate();
		
		
		
	}
	
	public void uploadfilev2v3(String[] aa) throws SQLException
	{
		
		
		
	    
		
		PreparedStatement prep1= null;
        String query1="insert into file(id,dataset_id,name,location,extension,size,description,date_stamp,format_id,type_id,code) values(?,?,?,?,?,?,?,?,?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, Integer.valueOf(aa[0]));//id
		prep1.setInt(2, Integer.valueOf(aa[1]));//dataset_id
		prep1.setString(3, aa[2]);//name
		prep1.setString(4, aa[3]);//location
		prep1.setString(5, aa[4]);//extension
		prep1.setInt(6, Integer.valueOf(aa[5]));//size
		prep1.setString(7, aa[6]);//description
		prep1.setDate(8, java.sql.Date.valueOf(aa[7]));//date_stamp
		prep1.setInt(9, Integer.valueOf(aa[8]));//format_id
		prep1.setInt(10, Integer.valueOf(aa[9]));//type_id
		if(aa.length<11){
		prep1.setString(11, "");//code
		}
		else
		prep1.setString(11, aa[10]);
		
		
		prep1.executeUpdate();
		
		
		
	}

	public int add_attribute(String name) throws SQLException
	{
		int id= this.getid("attribute");
		String query1="insert into attribute(id,attribute_name) values(?,?)";
		PreparedStatement prep1= null;
		prep1= con.prepareStatement(query1);
		prep1.setInt(1,id);
		prep1.setString(2, name);
		prep1.executeUpdate();
	
		return id;
	}
	
	public int addspeciestable(int taxid, String name) throws SQLException
	{
		int id= this.getid("species");
		String query1="insert into species(id,tax_id,scientific_name) values(?,?,?)";
		PreparedStatement prep1= null;
		prep1= con.prepareStatement(query1);
		prep1.setInt(1,id);
		prep1.setInt(2, taxid);
		prep1.setString(3, name);
		prep1.executeUpdate();
		return id;
	}
	
	public void addspecies(String array[]) throws SQLException
	{
		String query= "select tax_id from species where tax_id="+array[0]+" AND id between 1 and 107"  ;
		ResultSet rs = null;
		PreparedStatement prep1= null;
		System.out.println(query);
		rs= stmt.executeQuery(query);
		if(rs.next()==false)
		{
			String query1="insert into species(tax_id,common_name,genbank_name,scientific_name) values(?,?,?,?)";
			prep1= con.prepareStatement(query1);
			prep1.setInt(1, Integer.parseInt(array[0]));
			prep1.setString(2, array[1]);
			prep1.setString(3, array[2]);
			prep1.setString(4, array[3]);
			prep1.executeUpdate();
			
			
		}
		
		
		
		
	}
	
	public void updatesample_attribute_or_add(String value, int attribute_id, int sample_id) throws SQLException
	{
		String query= "select id from sample_attribute where sample_id="+sample_id+" and attribute_id="+attribute_id+";"  ;
		ResultSet rs = null;
		PreparedStatement prep1= null;
		System.out.println(query);
		rs= stmt.executeQuery(query);
		if(rs.next()==false)
		{
			String query1="insert into sample_attribute(sample_id,attribute_id,value) values(?,?,?)";
			System.out.println(query1);
			prep1= con.prepareStatement(query1);
			prep1.setInt(1, sample_id);
			prep1.setInt(2, attribute_id);
			prep1.setString(3, value);
			prep1.executeUpdate();	
		}
		else{
			
			query="update sample_attribute set value ="+"'"+value+"'"+ "where sample_id ="+sample_id+" and attribute_id="+attribute_id+";";
			System.out.println(query);
			PreparedStatement prep = con.prepareStatement(query);
			prep.executeUpdate();
			
			
			
		}
		
		
		
		
	}
	
	public String addsampleattribute(String id, String attribute) throws SQLException
	{
		String query= "select id from sample where code="+"'"+id+"'";
		ResultSet rs = null;
		PreparedStatement prep1= null;
		//System.out.println(query);
		rs= stmt.executeQuery(query);
		if(rs.next()==false)
		{
			return id;
			
		}
		
		
		return "ok";
		
	}
	
	public void add_dataset_author(int dataset_id, int author_id, int rank) throws SQLException
	
	{
		
		  
	    PreparedStatement prep1= null;
	    String query2= "insert into dataset_author(dataset_id,author_id,rank) values(?,?,?)";
		
		System.out.println(query2);
		prep1= con.prepareStatement(query2);
		
		prep1.setInt(1, dataset_id);
		prep1.setInt(2, author_id);		
		prep1.setInt(3, rank);
		prep1.executeUpdate();
		
		prep1.close();
		
	}
	
	public void add_dataset_funder(int dataset_id, int funder_id, String grant, String award) throws SQLException
	
	{
		
		  
	    PreparedStatement prep1= null;
	    String query2= "insert into dataset_funder(dataset_id,funder_id,grant_award,awardee) values(?,?,?,?)";
		
		System.out.println(query2);
		prep1= con.prepareStatement(query2);
		
		prep1.setInt(1, dataset_id);
		prep1.setInt(2, funder_id);		
		prep1.setString(3, grant);
		prep1.setString(4, award);
		prep1.executeUpdate();
		
		prep1.close();
		
	}
	
	public void add_funder(String url, String funder_name) throws SQLException
	
	{
		
		  
	    PreparedStatement prep1= null;
	    String query2= "insert into funder_name(uri,primary_name_display) values(?,?)";
		
		System.out.println(query2);
		prep1= con.prepareStatement(query2);
		
		prep1.setString(1, url);
		prep1.setString(2, funder_name);		

		prep1.executeUpdate();
		
		prep1.close();
		
	}
	
	public void addauthor(String first, String middle, String surname, String orcid,String doi, int rank) throws SQLException
	{
	    
		int id1= this.getid("author");
	  
	    PreparedStatement prep1= null;
	    String query1=null;
	    query1="insert into author(id, surname, middle_name, first_name,orcid) values(?,?,?,?,?)";
        System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, id1);
		prep1.setString(2, surname);
		prep1.setString(3, middle);
		prep1.setString(4, first);
		prep1.setString(5, orcid);
		prep1.executeUpdate();
		
	  
	
		
	
		
		String query2= "insert into dataset_author(dataset_id,author_id,rank) values(?,?,?)";
		
		System.out.println(query2);
		prep1= con.prepareStatement(query2);
		
		prep1.setInt(1, Integer.valueOf(doi));
		prep1.setInt(2, id1);		
		prep1.setInt(3, Integer.valueOf(rank));
		prep1.executeUpdate();
		
		prep1.close();
		
		
				
		
		
		
	}
	
	public void addsample(int species_id, String s_attrs, String code, int datasetid) throws SQLException
	{
	    int id= this.getid("sample");
	    int id1= this.getid("dataset_sample");
	    
	    species_id=getspecies_id(species_id);
		
		PreparedStatement prep1= null;
        String query1="insert into sample(id, species_id, s_attrs, code) values(?,?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, id);
		prep1.setInt(2, species_id);
		prep1.setString(3, s_attrs);
		prep1.setString(4, code);
		prep1.executeUpdate();
		
		String query2= "insert into dataset_sample(id, dataset_id, sample_id) values(?,?,?)";
		
		System.out.println(query2);
		prep1= con.prepareStatement(query2);
		prep1.setInt(1, id1);
		prep1.setInt(2, datasetid);
		prep1.setInt(3, id);
		prep1.executeUpdate();
	
	}
	
	public void addsamplev3(int datasetid, String name, int species) throws SQLException
	{
	    int id= this.getid("sample");
	   // int id1= this.getid("dataset_sample");
	    
	   
		
		PreparedStatement prep1= null;
        String query1="insert into sample(id,species_id, name) values(?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, id);
		prep1.setInt(2, species);
		prep1.setString(3, name);
		
		prep1.executeUpdate();
		
		String query2= "insert into dataset_sample(dataset_id, sample_id) values(?,?)";
		
		System.out.println(query2);
		prep1= con.prepareStatement(query2);
		prep1.setInt(1, datasetid);
		prep1.setInt(2, id);
		prep1.executeUpdate();
	
	}
	
	public void addsample_attributev3(int sampleid, int attributeid, String value) throws SQLException
	{
	
	
		PreparedStatement prep1= null;
        String query1="insert into sample_attribute(sample_id,attribute_id,value) values(?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, sampleid);
		prep1.setInt(2, attributeid);
		prep1.setString(3, value);
		
		
		
		prep1.executeUpdate();
	}
	/*
	public void addsample_attributev3(int sampleid, int attributeid, String value, String unitid) throws SQLException
	{
	
	
		PreparedStatement prep1= null;
        String query1="insert into sample_attribute(sample_id,attribute_id,value, unit_id) values(?,?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, sampleid);
		prep1.setInt(2, attributeid);
		prep1.setString(3, value);
		prep1.setString(4, unitid);
		
		
		prep1.executeUpdate();
	}*/
	
	public void addfile_sample(int sample_id, int file_id) throws SQLException
	{
	    
		
		PreparedStatement prep1= null;
        String query1="insert into file_sample(sample_id, file_id) values(?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, sample_id);
		prep1.setInt(2, file_id);
		
		
		prep1.executeUpdate();
		
		
	
	}
	
	public void addfile_attribute(int file_id,int attribute_id, String value) throws SQLException
	{
	  
		
		PreparedStatement prep1= null;
        String query1="insert into file_attributes(file_id, attribute_id, value) values(?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, file_id);
		prep1.setInt(2, attribute_id);
		prep1.setString(3, value);
		
		prep1.executeUpdate();
		
	
	}
	
	public void addsample_attribute(int sample_id, int attribute_id, String value, String unit_id) throws SQLException
	{
	  
		
		PreparedStatement prep1= null;
        String query1="insert into sample_attribute(sample_id, attribute_id, value, unit_id) values(?,?,?,?)";
        
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, sample_id);
		prep1.setInt(2, attribute_id);
		prep1.setString(3, value);
		prep1.setString(4, unit_id);
		prep1.executeUpdate();
		
	
	}
	public void addsample_attribute_without_unit(int sample_id, int attribute_id, String value) throws SQLException
	{
	  
		
		PreparedStatement prep1= null;
        String query1="insert into sample_attribute(sample_id, attribute_id, value) values(?,?,?)";
        value=value.replaceAll("\u0000", "");
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, sample_id);
		prep1.setInt(2, attribute_id);
		prep1.setString(3, value);
	
		prep1.executeUpdate();
		
	
	}
	public void updatesample_attribute_without_unit(int sample_id, int attribute_id, String value) throws SQLException
	{
	  
		
		PreparedStatement prep1= null;
        String query1="update sample_attribute set value =? where sample_id=? and attribute_id=?";
        value=value.replaceAll("\u0000", "");
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setString(1, value);
		prep1.setInt(2, sample_id);
		prep1.setInt(3, attribute_id);
		
	
		prep1.executeUpdate();
		
	
	}
	
	public void addv3user(String[] aa) throws SQLException
	{
		
		PreparedStatement prep1= null;
        String query1="insert into gigadb_user(email, password, first_name, last_name, affiliation, role, is_activated, newsletter, previous_newsletter_state, username) values(?,?,?,?,?,?,?,?,?,?)";
       
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setString(1, aa[1]);
		prep1.setString(2, aa[2]);
		prep1.setString(3, aa[3]);
		prep1.setString(4, aa[4]);
		prep1.setString(5, aa[5]);
		prep1.setString(6, aa[6]);
		prep1.setBoolean(7, Boolean.valueOf(aa[7]));
		prep1.setBoolean(8, Boolean.valueOf(aa[8]));
		prep1.setBoolean(9, Boolean.valueOf(aa[9]));
		prep1.setString(10, aa[1]);
		
		
		
		
		
		prep1.executeUpdate();
		
		
	}
	
	public void update_allsample_attribute(String[] aa) throws SQLException
	{
		String query= "select id from sample_attribute where id="+aa[0]+";";
		ResultSet rs = null;
		
		int sample_attribute_id=0;
		System.out.println(query);
		
		rs= stmt.executeQuery(query);
		if(Integer.valueOf(aa[3])==528||Integer.valueOf(aa[3])==529||Integer.valueOf(aa[3])==569)
		{
			aa[3]=String.valueOf(115);
		}
		while(rs.next())
		{
			sample_attribute_id= rs.getInt(1);
			
			if(sample_attribute_id!=0)
			{
				System.out.println("find the id and update record "+ sample_attribute_id );
				System.out.println(aa.length );
				if(aa.length < 7)
				{
			
					
					String query2="update sample_attribute set sample_id ="+aa[1]+", attribute_id="+aa[3]+", value="+"'"+aa[5]+"'"+" where id="+aa[0]+";";
					System.out.println(query2);
					PreparedStatement prep = con.prepareStatement(query2);
					prep.executeUpdate();	
					
				}
				else{
				
				String query2="update sample_attribute set sample_id ="+aa[1]+", attribute_id="+aa[3]+", value="+"'"+aa[5]+"'"+", unit_id="+"'"+aa[6]+"'"+" where id="+aa[0]+";";
				System.out.println(query2);
				PreparedStatement prep = con.prepareStatement(query2);
				prep.executeUpdate();
				}
				
				
				
				
			}
			else
			{
			   System.out.println("can't find the id and insert record "+ sample_attribute_id );
			   
			   if(aa.length < 7)
				{
			   
			    PreparedStatement prep1= null;
		        String query1="insert into sample_attribute(id, sample_id, attribute_id, value) values(?,?,?,?)";
				System.out.println(query1);
				prep1= con.prepareStatement(query1);
				
				prep1.setInt(1, Integer.valueOf(aa[0]));
				prep1.setInt(2, Integer.valueOf(aa[1]));
				prep1.setInt(3, Integer.valueOf(aa[3]));
				prep1.setString(4, aa[5]);
		
				prep1.executeUpdate();
				
				}
			   else{
				   
				   
				    PreparedStatement prep1= null;
			        String query1="insert into sample_attribute(id, sample_id, attribute_id, value, unit_id) values(?,?,?,?,?)";
					System.out.println(query1);
					prep1= con.prepareStatement(query1);
					
					prep1.setInt(1, Integer.valueOf(aa[0]));
					prep1.setInt(2, Integer.valueOf(aa[1]));
					prep1.setInt(3, Integer.valueOf(aa[3]));
					prep1.setString(4, aa[5]);
					prep1.setString(5,aa[6]);
			
					prep1.executeUpdate();
				   
			   }
			   
			   
			   
			}
			
		}


		
	}
	
	public void addfile(int dataset_id, String name, String location, String extension, long size, String description, String date_stamp,int format_id, int type_id, String code) throws SQLException
	{
	    
	    
		
		PreparedStatement prep1= null;
        String query1="insert into file(dataset_id, name, location, extension, size, description,date_stamp, format_id, type_id, code) values(?,?,?,?,?,?,?,?,?,?)";
        java.sql.Date date= java.sql.Date.valueOf(date_stamp);
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, dataset_id);
		prep1.setString(2, name);
		prep1.setString(3, location);
		prep1.setString(4, extension);
		prep1.setLong(5,size);
		prep1.setString(6,description);
		prep1.setDate(7,date);
		prep1.setInt(8, format_id);
		prep1.setInt(9, type_id);
		prep1.setString(10, code);
		
		
		
		
		prep1.executeUpdate();
		
	
	}
	
	public void addfilev3(int file_id,int dataset_id, String name, String location, String extension, long size, String description, String date_stamp,int format_id, int type_id,String code) throws SQLException
	{
		int id= getid("file");
	    
	   
		PreparedStatement prep1= null;
        String query1="insert into file(id,dataset_id, name, location, extension, size, description,date_stamp, format_id, type_id) values(?,?,?,?,?,?,?,?,?,?)";
        java.sql.Date date= java.sql.Date.valueOf(date_stamp);
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, file_id);
		prep1.setInt(2, dataset_id);
		prep1.setString(3, name);
		prep1.setString(4, location);
		prep1.setString(5, extension);
		prep1.setLong(6,size);
		prep1.setString(7,description);
		prep1.setDate(8,date);
		prep1.setInt(9, format_id);
		prep1.setInt(10, type_id);
		
		prep1.executeUpdate();
		if(code!=null){
		code= code.trim();
		}
		
	   if(code!=null && code != "" && !code.isEmpty())
	   {
		   
	   
		   String aa[]= code.split(";");
		   for(String sample_name: aa)
		   {  
			  int sample_id=0;
			  sample_name= sample_name.trim();
			  System.out.println("sample_name: "+sample_name);
			  if(sample_name ==null || sample_name == "")
				  continue;
		      sample_id=file_get_sampleid(dataset_id, sample_name);
		      if(file_id!=0 && sample_id !=0)
		      {
		    	  addfile_sample(sample_id,file_id);
		      }
		      else
		      {
		    	  System.out.println("Can't find sample id or file id");
		      }
		   }
		   
		   
	   }
		
	
	}
	
	public void addimage(String location, String tag, String url, String license, String photographer, String source, int doi) throws SQLException
	{
	    
	    
		int id=getid("image");
		
		PreparedStatement prep1= null;
        String query1="insert into image(id, location, tag, url, license, photographer, source) values(?,?,?,?,?,?,?)";
       
		System.out.println(query1);
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, id);
		prep1.setString(2, location);
		prep1.setString(3, tag);
		prep1.setString(4, url);
		prep1.setString(5,license);
		prep1.setString(6,photographer);
		prep1.setString(7,source);
		prep1.executeUpdate();
		
		String query="update dataset set image_id ="+id+" where identifier= "+"'"+ String.valueOf(doi)+"'" +";";
		System.out.println(query);
		//stmt.executeQuery(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
		
	
	}
	
	public void updatefile_description(String location, String des,int DOI) throws SQLException
	{
		String query="update file set description = "+"'"+des+"'"+" where location = "+"'"+location+"' and dataset_id="+DOI+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updatefile_sample(String location, String sample,int DOI) throws SQLException
	{
		String query="update file set code = "+"'"+sample+"'"+" where location like "+"'%"+location+"' and dataset_id="+DOI+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updatefile_code(String location, String code,int DOI) throws SQLException
	{
		String query="update file set code = "+"'"+code+"'"+" where location like "+"'%"+location+"' and dataset_id="+DOI+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updatefile_type(String location, String type) throws SQLException
	{
		String query="update file set type_id = (select id from file_type where name="+"'"+type+"'"+") where location="+"'"+location+"'"+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	public void updatefile_typeid(String location, String id) throws SQLException
	{
		String query="update file set type_id ="+id+" where location="+"'"+location+"'"+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	public void update_dataset_image_id(int dataset_id, int image_id) throws SQLException
	{
		String query="update dataset set image_id ="+image_id+" where id="+dataset_id+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	public void update_dataset_editor(int dataset_id, String editor) throws SQLException
	{
		String query="update dataset set handing_editor ="+"'"+editor+"'"+" where id="+dataset_id+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void update_dataset(String doi, String title, String description) throws SQLException
	{
		String query="update dataset set title = '"+title+"', description='"+description+"' where identifier='"+doi+"';";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updatefile_format(String location, String format) throws SQLException
	{
		String query="update file set format_id = (select id from file_format where name="+"'"+format+"'"+") where location="+"'"+location+"'"+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updateattribute_table(int id, String attribute_name,String definition, String model, String structured_comment_name, String value_syntax, String allowed_units, String occurance, String ontology_link, String note ) throws SQLException
	{
		String query="update attribute set attribute_name="+"'"+attribute_name+"'"+", definition="+"'"+definition+"'"+", model="+"'"+model+"'"+", structured_comment_name="+"'"+structured_comment_name+"'"+", value_syntax="+"'"+value_syntax+"'"+", allowed_units="+"'"+allowed_units+"'"+", occurance="+"'"+occurance+"'"+", ontology_link="+"'"+ontology_link+"'"+", note="+"'"+note+"'"+"where id ="+id+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	/*
	public void updatesampleattribute(String code, String attribute,int doi) throws SQLException
	{
		String query="update sample set s_attrs  = "+"'"+attribute+"'"+" where code ="+"'"+code+"'"+ "and id in (select sample_id from dataset_sample where dataset_id="+doi+");";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}*/
	
	public void updatesample_species(int sample_id, int species_id) throws SQLException
	{
		String query="update sample set species_id ="+species_id+" where id="+sample_id+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updatesampleattribute_withsample_id(String value,int attribute_id,int sample_id) throws SQLException
	{
		String query="update sample_attribute set value ="+"'"+value+"'"+ "where sample_id ="+sample_id+" and attribute_id="+attribute_id+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updatesampleattribute(String value, int dataset_id, int attribute_id,String sample_name) throws SQLException
	{
		String query="update sample_attribute set value ="+"'"+value+"'"+ "where sample_id = (select sample.id from sample,dataset_sample where dataset_sample.sample_id=sample.id and dataset_sample.dataset_id="+dataset_id+"and sample.name="+"'"+sample_name+"'"+") and attribute_id="+attribute_id+";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	public void updateimage(String location, String tag, String url, String license, String photographer, String source, int doi) throws SQLException
	{
		String query="update image set location = "+"'"+location+"'"+","+" tag= "+"'"+tag+"'"+","+" url= "+"'"+url+"'"+","+" license= "+"'"+license+"'"+","+" photographer= "+"'"+photographer+"'"+","+" source="+"'"+source+"'"+" where id= "+"("+ "select image_id from dataset where dataset.identifier= "+"'"+String.valueOf(doi)+"'"+");";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}
	
	
	public void updatefiletable() throws SQLException
	
	{
		
		String query= "select sample.id, file.id from file,sample where file.code=sample.code;";
		ResultSet rs = null;
		int sampleid=0;
		int fileid=0;
		System.out.println(query);
		
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			sampleid= rs.getInt(1);
			fileid= rs.getInt(2);
			
			System.out.println(sampleid);
			System.out.println(fileid);
			
			String query1="update file set code ="+sampleid+" where id="+fileid+";";
			System.out.println(query1);

			PreparedStatement prep = con.prepareStatement(query1);
			prep.executeUpdate();
			
			
		}
	}
	
	
public void updatefile_name_location(String oldname, String newname, int id) throws SQLException
	
	{
		
		String query= "select id from file where name="+"'"+oldname+"' and dataset_id="+id+";";
		ResultSet rs = null;
		
		int fileid=0;
		System.out.println(query);
		
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			fileid= rs.getInt(1);
			
			
			
			System.out.println(fileid);
			
			if(fileid!=0)
			{

			String query1="update file set name ="+"'"+newname+"'"+" where id="+fileid+";";
			query1 += "\n"+"update file set location = replace(location, "+"'"+oldname+"', "+"'"+newname+"')"+" where id="+fileid+";";
			System.out.println(query1);

			PreparedStatement prep = con.prepareStatement(query1);
			prep.executeUpdate();
			
			}
			
			
		}
	}
public void updatefile_name_location_md5(String oldname, String newname, int id, String md5) throws SQLException

{
	
	String query= "select id from file where name="+"'"+oldname+"' and dataset_id="+id+";";
	ResultSet rs = null;
	
	int fileid=0;
	System.out.println(query);
	
	rs= stmt.executeQuery(query);
	while(rs.next())
	{
		fileid= rs.getInt(1);
		
		
		
		System.out.println(fileid);
		
		if(fileid!=0)
		{

		String query1="update file set name ="+"'"+newname+"'"+" where id="+fileid+";";
		query1 += "\n"+"update file set location = replace(location, "+"'"+oldname+"', "+"'"+newname+"')"+" where id="+fileid+";";
		query1 += "\n"+"update file_attributes set value ="+"'"+md5+"'"+" where attribute_id=605 and file_id="+fileid+";";
		System.out.println(query1);

		PreparedStatement prep = con.prepareStatement(query1);
		prep.executeUpdate();
		
		}
		
		
	}
}

public void updatefile_md5(String md5, String name, int dataset_id) throws SQLException

{
	
	String query= "select id from file where name="+"'"+name+"' and dataset_id="+dataset_id+";";
	ResultSet rs = null;
	
	int fileid=0;
	System.out.println(query);
	
	rs= stmt.executeQuery(query);
	while(rs.next())
	{
		fileid= rs.getInt(1);
		
		
		
		System.out.println(fileid);
		
		if(fileid!=0)
		{

		String query1="update file_attributes set value ="+"'"+md5+"'"+" where attribute_id=605 and id="+fileid+";";
		System.out.println(query1);

		PreparedStatement prep = con.prepareStatement(query1);
		prep.executeUpdate();
		
		}
		
		
	}
}
	
	
	@SuppressWarnings("null")
	public void updatefilesize(String filelocation, BigInteger size) throws SQLException
	
	{
		
			PreparedStatement prep1= null;
			System.out.println(size);
			
			String query1="update file set size= "+size + " where location= "+"'"+filelocation+"'";
			
			System.out.println(query1);
			prep1 = con.prepareStatement(query1);
			prep1.executeUpdate();
		
		
	  // query= "delete from dataset_author where dataset_id="+doi;
	   //System.out.println(query);
	  // prep1= con.prepareStatement(query);
	 //  prep1.execute(query);
	}
	
	
	public ArrayList updatefilesizev2(int id) throws SQLException
	
	{
		
		
		String query= "select location from file where dataset_id="+id+" and size < 1;";
		ResultSet rs = null;
		String location=null;
		System.out.println(query);
		ArrayList<String> aa= new ArrayList<String>();
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			location= rs.getString(1);
			
			aa.add(location);
			
		}
		
		return aa;
		
	  // query= "delete from dataset_author where dataset_id="+doi;
	   //System.out.println(query);
	  // prep1= con.prepareStatement(query);
	 //  prep1.execute(query);
	}
  public ArrayList updatefilesizeruili(int id) throws SQLException
	
	{
		
		
		String query= "select location from file where dataset_id > "+id+" and size < 1 order by id;";
		ResultSet rs = null;
		String location=null;
		System.out.println(query);
		ArrayList<String> aa= new ArrayList<String>();
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			location= rs.getString(1);
			
			aa.add(location);
			
		}
		
		return aa;
		
	  // query= "delete from dataset_author where dataset_id="+doi;
	   //System.out.println(query);
	  // prep1= con.prepareStatement(query);
	 //  prep1.execute(query);
	}
	
	
	@SuppressWarnings("null")
	public void deleteauthor(String doi) throws SQLException
	
	{
		
		PreparedStatement prep1= null;
		String query1="delete from dataset_author where dataset_id="+doi;
			
			System.out.println(query1);
			prep1 = con.prepareStatement(query1);
			prep1.executeUpdate();
	
	  // query= "delete from dataset_author where dataset_id="+doi;
	   //System.out.println(query);
	  // prep1= con.prepareStatement(query);
	 //  prep1.execute(query);
	
		prep1.close();
	}
	
	// add more datasets part
	
	
	
	public int add_submitter(String email, String firstname, String secondname) throws SQLException{
		
		int id=0;
		String query= "select id from gigadb_user where email="+"'"+email+"';";
		System.out.println(query);
		
		ResultSet rs = null;
		rs= stmt.executeQuery(query);
		if(rs.next())
		{
		
			id=rs.getInt("id");
			
		}
		else{
			
			PreparedStatement prep1= null;
			
	        String query1="insert into gigadb_user(email,password,first_name,last_name,username) values(?,?,?,?,?)";
	       
			System.out.println(query1);
			prep1= con.prepareStatement(query1);
			prep1.setString(1, email);
			prep1.setString(2, "");
			prep1.setString(3, firstname);
			prep1.setString(4, secondname);
			prep1.setString(5,email);
			prep1.executeUpdate();
			id = this.add_submitter(email, firstname, secondname);
			prep1.close();
			
		}
		return id;
		
		
	}
	
	public String get_doi(String value) throws SQLException{
		
		int id=0;
		if(value !=null)
		{
			System.out.println("Step2");
			String query= "select id from dataset where identifier="+"'"+value+"';";
			ResultSet rs1 = null;
			rs1= stmt1.executeQuery(query);
			if(rs1.next())
			{
			
				int temp = Integer.valueOf(value);
				temp= temp+1;
				System.out.println("Step3");
				value= this.get_doi(String.valueOf(temp));
								
			}
			System.out.println("Step4");
			System.out.println("value1: "+value);
			return value;
						
		}
		
		else{
			
			String query= "select identifier from dataset where identifier like '1%' order by id desc;";
			ResultSet rs = null;
			System.out.println(query);
			String identifier= null;
			rs= stmt.executeQuery(query);
			if(rs.next())
			{
			
				identifier=rs.getString("identifier");
				
				
			}
			int temp = Integer.valueOf(identifier);
			temp= temp+1;
			System.out.println(temp);
			System.out.println("Step1");
			value = this.get_doi(String.valueOf(temp));
			
		}
		
		System.out.println("Step5");
		return value;
		
		
		
	}
	
	public int add_image2(String location, String tag, String url, String license, String photographer, String source) throws SQLException{
		
		int id=0;
		String query= "select id from image where tag="+"'"+tag+"';";
		ResultSet rs = null;
		rs= stmt.executeQuery(query);
		if(rs.next())
		{
		
			id=rs.getInt("id");
			
		}
		else{
			
			PreparedStatement prep1= null;
			
	        String query1="insert into image(location,tag,url,license,photographer,source) values(?,?,?,?,?,?)";
	       
			System.out.println(query1);
			prep1= con.prepareStatement(query1);
			prep1.setString(1, location);
			prep1.setString(2, tag);
			prep1.setString(3, url);
			prep1.setString(4, license);
			prep1.setString(5,photographer);
			prep1.setString(6,source);
			
			prep1.executeUpdate();
			id = this.add_image2(location, tag, url, license,photographer,source);
			prep1.close();
			
		}
		return id;
		
		
	}
	
	public int check_funder(String funder_name) throws SQLException {
		
		int id=0;
		String query= "select id from funder_name where LOWER(primary_name_display)="+"'"+funder_name.toLowerCase()+"';";
		ResultSet rs = null;
		rs= stmt.executeQuery(query);
		if(rs.next())
		{
		
			id=rs.getInt("id");
			
		}
		return id;
	
	}
	public int check_gigadbuser(String email) throws SQLException {
		
		int id=0;
		String query= "select id from gigadb_user where LOWER(email)="+"'"+email.toLowerCase()+"';";
		ResultSet rs = null;
		rs= stmt.executeQuery(query);
		if(rs.next())
		{
		
			id=rs.getInt("id");
			
		}
		return id;
	}
	
	
	public int get_funder(String funder_name) throws SQLException {
		
		int id=0;
		String query= "select id from funder_name where LOWER(primary_name_display)="+"'"+funder_name.toLowerCase()+"';";
		ResultSet rs = null;
		rs= stmt.executeQuery(query);
		if(rs.next())
		{
		
			id=rs.getInt("id");
			
		}
		else{
			
			PreparedStatement prep1= null;
			
	        String query1="insert into funder_name(uri,primary_name_display) values(?,?)";
	       
			System.out.println(query1);
			prep1= con.prepareStatement(query1);
			prep1.setString(1, "unknown");
			prep1.setString(2, funder_name);
			
			
			prep1.executeUpdate();
			id = this.get_funder(funder_name);
			prep1.close();
			
		}
		return id;
		
		
	}
	
	public void add_unit(String id, String name, String definition) throws SQLException{
		
		PreparedStatement prep1=null;
		String query="insert into unit values(?,?,?)";
		prep1=con.prepareStatement(query);
		prep1.setString(1, id);
		prep1.setString(2, name);
		prep1.setString(3, definition);
		
		prep1.executeUpdate();
		
		
		
	}
	
	public int get_publisher(String name) throws SQLException{
		
		int id=1;
		String query= "select id from publisher where name="+"'"+name+"';";
		ResultSet rs = null;
		rs= stmt.executeQuery(query);
		if(rs.next())
		{
		
			id=rs.getInt("id");
			
		}
		
		return id;
	}
	
public ArrayList getallpublished_dataset() throws SQLException{
		
		
		String query= "select id from dataset where upload_status='Published' order by id;";
		ResultSet rs = null;
		ArrayList<Integer> temp = new ArrayList<Integer> ();
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			temp.add(rs.getInt(1));
			
		}
		
		
		return temp;
	}

   public ArrayList<String> getdatasetids (int id, int id1) throws SQLException{
	   
	   ArrayList<String> aa = new ArrayList<String>();
	   String query="select identifier from dataset where id<"+id+" and id >"+id1+" and upload_status= 'Published' order by id;";
	   ResultSet rs= null;
	   rs= stmt.executeQuery(query);
	   while(rs.next()){
		   
		   aa.add(rs.getString(1));
			
		}
			
	   return aa;
	   
	   
   }
	
	public String getcitiation(int id) throws SQLException{
	
	String query="select publisher_id, publication_date,title,identifier from dataset where id="+id+";";
	ResultSet rs= null;
	int publisher_id=0;
	String title=null;
	String date= null;
	String identifier=null;
	rs= stmt.executeQuery(query);
	while(rs.next())
	{
		publisher_id= rs.getInt(1);
		date= String.valueOf(rs.getDate(2));
		title= rs.getString(3);
		identifier= rs.getString(4);
	}
	

	
	
	
	
	String publish_name = getpublish_name(publisher_id);
	String namelist = getallauthor_name(id);
	
	
	
	String total = namelist +"("+date.substring(0, 4)+"): "+title+" "+publish_name+".  http://dx.doi.org/10.5524/"+identifier;
	System.out.println(total);
	return null;
	
	}
public String getpublish_name(int id) throws SQLException{
		
		String query="select name from publisher where id="+id+";";
		ResultSet rs= null;
		String name= null;
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			name= rs.getString(1);
		}
		return name;
	}

public String get_editor(int id) throws SQLException{
	
	String query="select handing_editor from dataset where id="+id+";";
	ResultSet rs= null;
	String name= null;
	rs= stmt.executeQuery(query);
	while(rs.next())
	{
		name= rs.getString(1);
	}
	return name;
}

public String get_curation_log(int dataset_id, String action) throws SQLException{
	
	String query="select comments from curation_log where dataset_id="+dataset_id+ "and action='"+action+"';";
	ResultSet rs= null;
	String comments= null;
	rs= stmt.executeQuery(query);
	while(rs.next())
	{
		comments= rs.getString(1);
	}
	return comments;
}

public int getauthor_id(String name) throws SQLException{
	
	name=name.trim();
	String aa[] = name.split(" ");
	String query=null;
	ResultSet rs= null;
	int id=0;
	
	if(aa.length<3)
	{
		query="select id from author where surname="+"'"+aa[1]+"'"+"and first_name="+"'"+aa[0]+"';";
		System.out.println(query);
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id=rs.getInt(1);
		}
	}
	else
	{
		query="select id from author where surname="+"'"+aa[2]+"'"+"and first_name="+"'"+aa[0]+"' and middle_name='"+aa[1]+"';";
		System.out.println(query);
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id=rs.getInt(1);
		}
	}
	
	
	
	return id;
	
	
}

public String getallauthor_name(int id) throws SQLException{
	
	String query="select name from author, dataset_author where dataset_author.author_id= author.id and dataset_author.dataset_id="+id+" order by author.rank;;";
	ResultSet rs= null;
	String name= null;
	String idlist="";
	rs= stmt.executeQuery(query);
	ArrayList<String> aa = new ArrayList<String>();
	while(rs.next())
	{
		aa.add(rs.getString(1));
	}
	
	for(int i=0; i<aa.size();++i)
	{
		idlist= idlist + aa.get(i)+"; ";
	}
	return idlist;
}


    public int getdataset_id_from_MS(String ms) throws SQLException{
	
	String query="select id from dataset where manuscript_id="+"'"+ms+"';";
	ResultSet rs= null;
	int id=0;
	//System.out.println(query);
	rs= stmt.executeQuery(query);
	while(rs.next())
	{
		id= rs.getInt("id");
	}
	return id;
  }
	
	public int getdataset_id(int doi) throws SQLException{
		
		String query="select id from dataset where identifier="+"'"+String.valueOf(doi)+"';";
		ResultSet rs= null;
		int id=0;
		//System.out.println(query);
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id= rs.getInt("id");
		}
		return id;
	}
	
	public int getimage_id(String location) throws SQLException{
		
		String query="select id from image where location="+"'"+location+"';";
		ResultSet rs= null;
		int id=0;
		//System.out.println(query);
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id= rs.getInt("id");
		}
		return id;
	}
	
	public int getimageid_from_dataset(int dataset_id) throws SQLException{
		
		String query="select image_id from dataset where id="+dataset_id+";";
		ResultSet rs= null;
		int id=0;
		//System.out.println(query);
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id= rs.getInt("image_id");
		}
		return id;
	}
	
	public void add_dataset(int submitter, String manuscript_id, int image, String identifier, String title, String description, int dataset_size, String ftp_site, String upload_status, int publisher, String editor) throws SQLException
	{
	    
	    
		
		
		PreparedStatement prep1= null;
        String query1="insert into dataset(submitter_id,image_id,identifier,title,description,dataset_size,ftp_site,excelfile,upload_status,publisher_id,manuscript_id,handing_editor) values(?,?,?,?,?,?,?,?,?,?,?,?)";
       
		System.out.println(identifier);
		
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, submitter);
		prep1.setInt(2, image);
		prep1.setString(3, identifier);
		prep1.setString(4, title);
		prep1.setString(5, description);
		prep1.setInt(6, dataset_size);
		prep1.setString(7, ftp_site);
		prep1.setString(8, "upload_from_EM");
		prep1.setString(9, upload_status);
		prep1.setInt(10, publisher);
		prep1.setString(11, manuscript_id);
		prep1.setString(12, editor);
		prep1.executeUpdate();
		
		prep1.close();
		
	
	}
	
	
	public void deleteimage(int id) throws SQLException
	{
		  PreparedStatement prep1= null;
		  String query= "delete from image where id="+id;
		  System.out.println(query);
		  prep1= con.prepareStatement(query);
		  prep1.executeUpdate();
		  prep1.close();
		
	}
	
	public void deletecurationlog(int id) throws SQLException
	{
		  PreparedStatement prep1= null;
		  String query= "delete from curation_log where dataset_id="+id+" and created_by='EM' and action='Status updated to ImportFromEM';";
		  System.out.println(query);
		  prep1= con.prepareStatement(query);
		  prep1.executeUpdate();
		  prep1.close();
		
	}

	
	public void deletedataset(int id) throws SQLException
	{
		 int image_id = this.getimageid_from_dataset(id);
		 this.deleteimage(image_id);
		
		 PreparedStatement prep1= null;
		 String query= "delete from dataset where id="+id+"and excelfile='upload_from_EM';" ;
		 System.out.println(query);
		 prep1= con.prepareStatement(query);
		 prep1.executeUpdate();
		 
		 this.deleteauthor(String.valueOf(id));
		 this.deletedatasetfunder(String.valueOf(id));
		 this.deletedataset_curationlog(String.valueOf(id));
		 
		 prep1.close();
		 
		
	}
	
	@SuppressWarnings("null")
	public void deletedatasetfunder(String doi) throws SQLException
	
	{
		
		PreparedStatement prep1= null;
		String query1="delete from dataset_funder where dataset_id="+doi;
			
			System.out.println(query1);
			prep1 = con.prepareStatement(query1);
			prep1.executeUpdate();
	
	  // query= "delete from dataset_author where dataset_id="+doi;
	   //System.out.println(query);
	  // prep1= con.prepareStatement(query);
	 //  prep1.execute(query);
	
		prep1.close();
	}
	
	@SuppressWarnings("null")
	public void deletedataset_curationlog(String doi) throws SQLException
	
	{
		
		PreparedStatement prep1= null;
		String query1="delete from curation_log where dataset_id="+doi;
			
			System.out.println(query1);
			prep1 = con.prepareStatement(query1);
			prep1.executeUpdate();
	
	  // query= "delete from dataset_author where dataset_id="+doi;
	   //System.out.println(query);
	  // prep1= con.prepareStatement(query);
	 //  prep1.execute(query);
	
		prep1.close();
	}
	
	
	public void add_link(int dataset_id, boolean is_primary, String link ) throws SQLException
	{
	    
	    
		
		
		PreparedStatement prep1= null;
        String query1="insert into link(dataset_id,is_primary,link) values(?,?,?)";
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, dataset_id);
		prep1.setBoolean(2,is_primary);
		prep1.setString(3, link);
		prep1.executeUpdate();
		
		
		
	
	}
	
	public void add_dataset_project(int dataset_id, String project_url ) throws SQLException
	{
	    
	    
		String query="select id from project where url="+"'"+project_url+"';";
		ResultSet rs= null;
		int id=0;
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id= rs.getInt("id");
		}
		
		
		PreparedStatement prep1= null;
        String query1="insert into dataset_project(dataset_id,project_id) values(?,?)";
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, dataset_id);
		prep1.setInt(2,id);
	
		prep1.executeUpdate();
		
		
		
	
	}
	
	public void add_dataset_type(int dataset_id, String type ) throws SQLException
	{
	    
	    
		String query="select id from type where name="+"'"+type+"';";
		ResultSet rs= null;
		int id=0;
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id= rs.getInt("id");
		}
		
		
		PreparedStatement prep1= null;
        String query1="insert into dataset_type(dataset_id,type_id) values(?,?)";
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, dataset_id);
		prep1.setInt(2,id);
	
		prep1.executeUpdate();
		
	}
	
	
	
	public void add_curation_log(int dataset_id, String current_date, String action,String comment ) throws SQLException, HttpException, IOException
	{


		System.out.println("current_date: "+current_date);
		java.sql.Date date= java.sql.Date.valueOf(current_date);
		PreparedStatement prep1= null;
        String query1="insert into curation_log(dataset_id, creation_date, created_by, action, comments) values(?,?,?,?,?)";
        try{
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, dataset_id);
		prep1.setDate(2, date);
		prep1.setString(3, "EM");
		prep1.setString(4, action);
		prep1.setString(5, comment);
		System.out.println("current_date: "+current_date);
	
		prep1.executeUpdate();
		
        } catch (PSQLException e) {
        	
    		System.err.println("Can not create the curation log");
    		//throw e;
    	} finally {
    		prep1.close();
    	}
		
	}
	
	
	public void add_manuscript(int dataset_id, String manuscript ) throws SQLException, HttpException, IOException
	{
		/*
			String prefix = "http://www.ncbi.nlm.nih.gov/pubmed/";
			String postfix = "?term=" + manuscript + "&presentation=xml";
			
			String manuscript_url=prefix+postfix;
			
			String webpage = getWebPage(manuscript_url);
			// System.out.println(webpage);
			String beginMark = "&lt;ArticleId IdType=\"pubmed\"&gt;";
			String endMark = "&lt;";
			int beginIndex = webpage.indexOf(beginMark) + beginMark.length();
			if (beginIndex < beginMark.length())
				return;
			int endIndex = webpage.indexOf(endMark, beginIndex);
			String pmid = webpage.substring(beginIndex, endIndex).trim();
			//if (pmid.equals(""))
				//return;
		
	    */

		
		
		PreparedStatement prep1= null;
        String query1="insert into manuscript(identifier, dataset_id) values(?,?)";
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setString(1, manuscript);
		//prep1.setInt(2,Integer.valueOf(pmid));
		prep1.setInt(2, dataset_id);
		
	
		prep1.executeUpdate();
		
	}
	
	/* v3 */
	
	public void addv3author(String surname ,String middle_name, String first_name) throws SQLException, HttpException, IOException
	{
		

		PreparedStatement prep1= null;
        String query1="insert into author(surname,middle_name,first_name) values(?,?,?)";
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setString(1,surname);
		prep1.setString(2,middle_name);
		prep1.setString(3,first_name);

	
		prep1.executeUpdate();
		
		prep1.close();
		
	}
	
	
	public void addv3dataset_author(int dataset_id, int author_id, int order) throws SQLException, HttpException, IOException
	{
		/*
		String query="select id from dataset where identifier="+"'"+doi+"';";
		ResultSet rs= null;
		int id=0;
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id= rs.getInt("id");
		}
		*/ //input doi
		//input id
		PreparedStatement prep1= null;
        String query1="insert into dataset_author(dataset_id, author_id, rank) values(?,?,?)";
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, dataset_id);
		prep1.setInt(2, author_id);
		prep1.setInt(3, order);
		
	
		prep1.executeUpdate();
		
	}
	
	@SuppressWarnings("resource")

	
	public void addv3file_sample() throws SQLException, HttpException, IOException
	{
		
		Statement stmt1 = con.createStatement();
		String query="select id, code from file where id>87345 order by id;";
		ResultSet rs= null;
		rs= stmt.executeQuery(query);
		String code=null;
		int file_id=0;
		FileWriter fstream = new FileWriter("/Users/xiaosizhe/Documents/workspace/excel/uploadDir/file_sample_table1.txt");
		BufferedWriter fbw = new BufferedWriter(fstream);
		while(rs.next())
		{
			code= rs.getString("code");
			if(code !=null){
			code= code.replaceAll("\\r\\n|\\r|\\n","");}
			file_id= rs.getInt("id");
			
			
				int sample_id=0;
				String query1="select id from sample where name= "+"'"+code+"';";
				ResultSet rs1= null;
				
				rs1=stmt1.executeQuery(query1);
				while(rs1.next())
				{
					sample_id=rs1.getInt("id");
					
				}
				if(sample_id==0 && code !=null && !code.equals("None") && !code.equals("") && !code.equals("none") )
				{
				fbw.write(sample_id+" "+file_id+" "+code+"\n");
				System.out.println(sample_id+"   "+file_id+"    "+code);
				System.out.println(query1);
				}
				
				else if (sample_id==0){
				//fbw.write(sample_id+" "+file_id+" "+code+"\n");
				System.out.println(sample_id+"   "+file_id+"    "+code);
				}
				else
				{
					PreparedStatement prep1= null;
			        String query2="insert into file_sample(sample_id, file_id) values(?,?)";
			       
					
					
					prep1= con.prepareStatement(query2);
					prep1.setInt(1, sample_id);
					prep1.setInt(2, file_id);
					
					
				
					prep1.executeUpdate();
				}
				
			
			
			code=null;
			file_id=0;
			
			
		}
		fbw.close();
		
	}
	
	public void addv3file_sample_additional() throws SQLException, HttpException, IOException
	{
		//ArrayList<Integer> sample = new ArrayList<Integer>(606,607,608,609,610);
		String sample_string= "Rem197-E,Rem197-I,Rem316-E,Rem316-I,Rem362-E,Rem362-I,Rem367-E,Rem367-I";
		sample_string=sample_string.replace(" ","");
		sample_string=sample_string.replace("and",",");
		int file_id=13806;
		
		String[] aa= sample_string.split(",");
		for(int j=86469;j<86470;++j)
		{
			file_id=j;
		
		
		for(int i=0; i<aa.length;++i)
		{
			int sample_id=0;
			Statement stmt1 = con.createStatement();
			String query="select id from sample where code= "+"'"+aa[i]+"';";
			ResultSet rs= null;
			rs= stmt.executeQuery(query);
			while(rs.next())
			{
				sample_id=rs.getInt("id");
			}
			if(sample_id==0)
			{
				System.out.println(aa[i]+" "+file_id);
			}
			else{
				
				PreparedStatement prep1= null;
		        String query2="insert into file_sample(sample_id, file_id) values(?,?)";
		       
				
				prep1= con.prepareStatement(query2);
				prep1.setInt(1, sample_id);
				prep1.setInt(2, file_id);
				
				
			
				prep1.executeUpdate();
				System.out.println("success "+aa[i]+" "+sample_id+" "+file_id);
				
				
			}
		
		}
			
			
			
		}
		
		
		
	//	int file_id=17683;
	/*	
		for(int i=0;i<3;++i)
	{
		PreparedStatement prep1= null;
        String query1="insert into file_sample(sample_id, file_id) values(?,?)";
        System.out.println(query1);
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, sample_id);
		prep1.setInt(2, file_id);
		prep1.executeUpdate();
		file_id=file_id+1;
		
	}*/
		/*
		for(int i=0;i<4;++i)
		{
			int sample_id=1978;
			for(int j=0;j<2;++j)
			{
			
			PreparedStatement prep1= null;
	        String query1="insert into file_sample(sample_id, file_id) values(?,?)";
	        System.out.println(query1);
	       
			
			
			prep1= con.prepareStatement(query1);
			prep1.setInt(1, sample_id);
			prep1.setInt(2, file_id);
			prep1.executeUpdate();
			sample_id=sample_id+1;
			}
			file_id=file_id+1;
			
		}
		
		*/
	}
	
	
	public String checkcontain1(String table, String valuename, String value,String getvalue) throws SQLException
	{
		
		String query ="select  "+getvalue+" from "+ table +" where lower( "+valuename+")= " + "lower('" + value + "')";
		System.out.println("query "+ query);
		ResultSet resultSet=stmt.executeQuery(query);
		query=null;
		
		while(resultSet.next())
		{
			query= resultSet.getString(getvalue);
			System.out.println("checkcontain result: "+ query);
			
		}
	
		
		return query;

	}
	
	public void v3geteol_link() throws Exception
	{
		
		String query="select id, scientific_name from species where eol_link=null or eol_link='' order by id;";
		ResultSet rs= null;
		String name= null;;
		int id=0;
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			name= rs.getString("scientific_name");
			String aa = sendGet(name);
			id= rs.getInt("id");
			
			String query1="update species set eol_link="+"'"+aa+"'"+" where id="+id+";";
			System.out.println(query1);
			PreparedStatement prep = con.prepareStatement(query1);
			prep.executeUpdate();
			
		}
		
	
	}
	
	public int get_taxid(int id, String name) throws SQLException{
		
		String query="select id from species where tax_id="+id+";";
		ResultSet rs= null;
	
		int table_id=0;
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			table_id=rs.getInt(1);
		}
		if(table_id==0)
		{
			int species_id = getid("species");
			
			PreparedStatement prep1= null;
	        String query1="insert into species(id,tax_id,scientific_name) values(?,?,?)";
	       
			
			
			prep1= con.prepareStatement(query1);
			prep1.setInt(1, species_id);
			prep1.setInt(2, id);
			prep1.setString(3,name);
			
		
			prep1.executeUpdate();
			table_id= species_id;
			
		}
		
		return table_id;
	}
	
	public static String sendGet(String name) throws Exception {
		 
		name = name.replaceAll(" ", "%20");
		System.out.println(name);
		String url = "http://eol.org/search?q="+name;
		String USER_AGENT = "Mozilla/5.0";
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setInstanceFollowRedirects(false);
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		System.out.println(con.getHeaderField("Location"));
		/*
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
		*/
		return con.getHeaderField("Location");
 
	}
	
	
	
	public void add_related_doi(int dataset_id, int related_doi, String relationship, int current_doi) throws SQLException
	{
	    
	    
		String query="select id from dataset where identifier="+"'"+related_doi+"';";
		ResultSet rs= null;
		int id=0;
		rs= stmt.executeQuery(query);
		while(rs.next())
		{
			id= rs.getInt("id");
		}
		
		
		PreparedStatement prep1= null;
        String query1="insert into relation(dataset_id, related_doi, relationship) values(?,?,?)";
       
		
		
		prep1= con.prepareStatement(query1);
		prep1.setInt(1, dataset_id);
		prep1.setInt(2,related_doi);
		prep1.setString(3,relationship);
	
		prep1.executeUpdate();
		
		if(relationship == "IsSupplementTo"){
			
			PreparedStatement prep2= null;
	        String query2="insert into relation(dataset_id, related_doi, relationship) values(?,?,?)";
	       
			
			
			prep2= con.prepareStatement(query2);
			prep2.setInt(1, id);
			prep2.setInt(2,current_doi);
			prep2.setString(3,"IsSupplementedBy");
		
			prep2.executeUpdate();
			
		}
		else if(relationship == "IsSupplementBy"){
			
			PreparedStatement prep2= null;
	        String query2="insert into relation(dataset_id, related_doi, relationship) values(?,?,?)";
	       
			
			
			prep2= con.prepareStatement(query2);
			prep2.setInt(1, id);
			prep2.setInt(2,current_doi);
			prep2.setString(3,"IsSupplementedTo");
		
			prep2.executeUpdate();
			
		}
		else if(relationship == "IsNewVersionOf"){
			
			PreparedStatement prep2= null;
	        String query2="insert into relation(dataset_id, related_doi, relationship) values(?,?,?)";
	       
			
			
			prep2= con.prepareStatement(query2);
			prep2.setInt(1, id);
			prep2.setInt(2,current_doi);
			prep2.setString(3,"IsPreviousVersionOf");
		
			prep2.executeUpdate();
			
		}
		else if(relationship == "IsPreviousVersionOf"){
			
			PreparedStatement prep2= null;
	        String query2="insert into relation(dataset_id, related_doi, relationship) values(?,?,?)";
	       
			
			
			prep2= con.prepareStatement(query2);
			prep2.setInt(1, id);
			prep2.setInt(2,current_doi);
			prep2.setString(3,"IsNewVersionOf");
		
			prep2.executeUpdate();
			
		}
		else if(relationship == "IsPartOf"){
			
			PreparedStatement prep2= null;
	        String query2="insert into relation(dataset_id, related_doi, relationship) values(?,?,?)";
	       
			
			
			prep2= con.prepareStatement(query2);
			prep2.setInt(1, id);
			prep2.setInt(2,current_doi);
			prep2.setString(3,"HasPart");
		
			prep2.executeUpdate();
			
		}
		else if(relationship == "HasPartOf"){
			
			PreparedStatement prep2= null;
	        String query2="insert into relation(dataset_id, related_doi, relationship) values(?,?,?)";
	       
			
			
			prep2= con.prepareStatement(query2);
			prep2.setInt(1, id);
			prep2.setInt(2,current_doi);
			prep2.setString(3,"IsPartOf");
		
			prep2.executeUpdate();
			
		}
		
		
	
	}
	
	public void get_species(int tax_id, String common_name) throws SQLException, HttpException, IOException
	{
		String query= "select id from species where tax_id="+tax_id;
		ResultSet rs = null;
		PreparedStatement prep1= null;
		System.out.println(query);
		rs= stmt.executeQuery(query);
		if(rs.next()==false)
		{
			
			String[] pullNameArray = new String[2];
			HashSet<String> nameSet = this.getCommon_name("http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&amp;id="+tax_id,
					pullNameArray);
			
			String query1="insert into species(tax_id,common_name,genbank_name,scientific_name) values(?,?,?,?)";
			prep1= con.prepareStatement(query1);
			prep1.setInt(1, tax_id);
			prep1.setString(2, common_name);
			prep1.setString(3, pullNameArray[1]);
			prep1.setString(4, pullNameArray[0]);
			prep1.executeUpdate();
			
			
		}
		
		
		
		
	}
	
	public void getalltable() throws SQLException, HttpException, IOException
	{
		DatabaseMetaData md= con.getMetaData();
		String[] types = {"TABLE"};
		ResultSet rs= md.getTables(null, null, "%", types);
		
		while (rs.next()) {
			
			String table_name=rs.getString(3);
			  System.out.print(rs.getString(3)+":	");
			  ResultSet res = md.getColumns(null, null, table_name, null);
			  while(res.next()) {
				
				  System.out.print(res.getString("COLUMN_NAME")+"	");
				  
				  
				  
				  
				  
			  }
			  
			  System.out.println();
			}
		
		
	}
	
	public String get_result(String command, String attribute) throws SQLException
	{
		//System.out.println("get_result from query: "+ command);
		ResultSet resultSet=stmt.executeQuery(command);
		command=null;
		while(resultSet.next())
		{
			command= resultSet.getString(attribute);
		}
		//System.out.println("get_result : "+ command);
		return command;
		
	}
	
	

	
	public String getWebPage(String url) throws HttpException, IOException, NullPointerException {
		URL url1 = new URL(url);
		URLConnection con = url1.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body = IOUtils.toString(in, encoding);
		return body;
	}
	
	HashSet<String> getCommon_name(String url, String[] nameArray)
			throws HttpException, IOException {
		System.out.println(url);
		String webpage = getWebPage(url);
		HashSet<String> nameSet = new HashSet<String>();
		String beginMark = "<em>Taxonomy ID: </em>";
		String endMark = "<em>Rank: </em>";
		//System.out.println(webpage);
		int beginIndex = webpage.indexOf(beginMark) + beginMark.length();
		int endIndex = webpage.indexOf(endMark);
		System.out.println("URL "+ url);
		System.out.println("BeginIndex "+ beginIndex);
		System.out.println("endIndex "+ endIndex);
		
		String nameString = webpage.substring(beginIndex, endIndex);
		System.out.println("nameString  "+ nameString);
		
		// get scientific name
		beginMark = "<title>Taxonomy browser (";
		endMark = ")</title>";
		beginIndex = webpage.indexOf(beginMark);
		if (beginIndex != -1) {
			beginIndex += beginMark.length();
			endIndex = webpage.indexOf(endMark, beginIndex);
			nameArray[0] = webpage.substring(beginIndex, endIndex);
		}
		else{
			
			nameArray[0]=null;
		}
		// get genbank common name
		beginMark = "<em>Genbank common name: </em><strong>";
		endMark = "</strong>";
		beginIndex = 0;
		beginIndex = nameString.indexOf(beginMark, beginIndex);
		if (beginIndex != -1) {
			beginIndex += beginMark.length();
			endIndex = nameString.indexOf(endMark, beginIndex);
			nameArray[1] = nameString.substring(beginIndex, endIndex);
		}
		else{
		
			nameArray[1]=null;
		}
		beginMark = "<strong>";
		endMark = "</strong>";
		beginIndex = 0;
		while (true) {
			beginIndex = nameString.indexOf(beginMark, beginIndex);
			if (beginIndex == -1)
				break;
			beginIndex += beginMark.length();
			endIndex = nameString.indexOf(endMark, beginIndex);
			String common_name = nameString.substring(beginIndex, endIndex);
			// we use lower case
			nameSet.add(common_name.toLowerCase());
			beginIndex = endIndex + endMark.length();

		}
		if (nameSet.size() == 0) {
			
			return null;
		}
		return nameSet;
	}

	
	
	
	public void close () throws SQLException
	{
		con.close();
		
	}

	public static void main(String[] args) throws Exception {
		database db = new database();			
//		database.calPhraseProb();
//		System.out.println(database.exist("1.5524/100003"));
	}
	/*
	public void updatesamplename(String oldvalue, String newvalue, int id) throws SQLException {
		// TODO Auto-generated method stub
		
		String query= "select id from sample where code="+"'"+oldvalue+"'"+"" ;
		ResultSet rs = null;
		PreparedStatement prep1= null;
		System.out.println(query);
		rs= stmt.executeQuery(query);
		if(rs.next())
		{
			String query1="update sample set code=? where code="+"'"+oldvalue+"'";
			prep1= con.prepareStatement(query1);
			prep1.setString(1, newvalue);
			prep1.executeUpdate();
			System.out.print("update sample "+ newvalue);
			
			
			
			String query2="update file set code=? where code="+"'"+oldvalue+"'";
			prep1= con.prepareStatement(query2);
			prep1.setString(1, newvalue);
			prep1.executeUpdate();
			
			System.out.println("	update file "+ newvalue);
			
		}
		
		
		
		
		
		
	}
	*/

}
