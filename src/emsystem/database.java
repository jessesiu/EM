package emsystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.postgresql.util.PSQLException;
import org.xml.sax.SAXException;

public class database {

	String url;
	String user;
	String password;
	Connection con;
	Statement stmt;
	Statement stmt1;

	public database() throws ParserConfigurationException, SAXException, IOException {

		Setting.Loadsetting();
		url = Setting.databaseUrl;
		password = Setting.databasePassword;
		user = Setting.databaseUserName;
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			con = DriverManager.getConnection(url, user, password);
			// this is important
			con.setAutoCommit(true);
			stmt = con.createStatement();
			stmt1 = con.createStatement();

			// int i=1;

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

	public int getid(String table) throws SQLException {
		int newid = 0;
		String query = "SELECT max(id) from " + table;
		System.out.println("query " + query);
		ResultSet resultSet = stmt.executeQuery(query);
		while (resultSet.next()) {
			query = resultSet.getString("max");
		}
		if (query == null)
			newid = 1;
		else
			newid = Integer.valueOf(query) + 1;

		System.out.println("id number in db " + newid);
		return newid;

	}

	public String get_status(int id) throws SQLException {
		String query = "select upload_status from dataset where id=" + id + ";";
		// System.out.println("query"+query);
		ResultSet resultSet = stmt.executeQuery(query);

		String upload_status = null;
		while (resultSet.next()) {
			upload_status = resultSet.getString("upload_status");

		}
		return upload_status;

	}

	public void update_dataset_status(int datasetid, String status) throws SQLException {
		String query = "update dataset set upload_status='" + status + "' where id=" + datasetid + ";";
		System.out.println(query);
		// stmt.executeQuery(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}

	public void add_dataset_author(int dataset_id, int author_id, int rank) throws SQLException

	{

		PreparedStatement prep1 = null;
		String query2 = "insert into dataset_author(dataset_id,author_id,rank) values(?,?,?)";

		System.out.println(query2);
		prep1 = con.prepareStatement(query2);

		prep1.setInt(1, dataset_id);
		prep1.setInt(2, author_id);
		prep1.setInt(3, rank);
		prep1.executeUpdate();

		prep1.close();

	}

	public void add_dataset_funder(int dataset_id, int funder_id, String grant, String award) throws SQLException

	{

		PreparedStatement prep1 = null;
		String query2 = "insert into dataset_funder(dataset_id,funder_id,grant_award,awardee) values(?,?,?,?)";

		System.out.println(query2);
		prep1 = con.prepareStatement(query2);

		prep1.setInt(1, dataset_id);
		prep1.setInt(2, funder_id);
		prep1.setString(3, grant);
		prep1.setString(4, award);
		prep1.executeUpdate();

		prep1.close();

	}

	public void update_dataset_editor(int dataset_id, String editor) throws SQLException {
		String query = "update dataset set handing_editor =" + "'" + editor + "'" + " where id=" + dataset_id + ";";
		System.out.println(query);
		PreparedStatement prep = con.prepareStatement(query);
		prep.executeUpdate();
	}

	@SuppressWarnings("null")
	public void deleteauthor(String doi) throws SQLException

	{

		PreparedStatement prep1 = null;
		String query1 = "delete from dataset_author where dataset_id=" + doi;

		System.out.println(query1);
		prep1 = con.prepareStatement(query1);
		prep1.executeUpdate();

		// query= "delete from dataset_author where dataset_id="+doi;
		// System.out.println(query);
		// prep1= con.prepareStatement(query);
		// prep1.execute(query);

		prep1.close();
	}

	// add more datasets part

	public int add_submitter(String email, String firstname, String secondname) throws SQLException {

		int id = 0;
		String query = "select id from gigadb_user where email=" + "'" + email + "';";
		System.out.println(query);

		ResultSet rs = null;
		rs = stmt.executeQuery(query);
		if (rs.next()) {

			id = rs.getInt("id");

		} else {

			PreparedStatement prep1 = null;

			String query1 = "insert into gigadb_user(email,password,first_name,last_name,username) values(?,?,?,?,?)";

			System.out.println(query1);
			prep1 = con.prepareStatement(query1);
			prep1.setString(1, email);
			prep1.setString(2, "");
			prep1.setString(3, firstname);
			prep1.setString(4, secondname);
			prep1.setString(5, email);
			prep1.executeUpdate();
			id = this.add_submitter(email, firstname, secondname);
			prep1.close();

		}
		return id;

	}

	public String get_doi(String value) throws SQLException {

		int id = 0;
		if (value != null) {
			System.out.println("Step2");
			String query = "select id from dataset where identifier=" + "'" + value + "';";
			ResultSet rs1 = null;
			rs1 = stmt1.executeQuery(query);
			if (rs1.next()) {

				int temp = Integer.valueOf(value);
				temp = temp + 1;
				System.out.println("Step3");
				value = this.get_doi(String.valueOf(temp));

			}
			System.out.println("Step4");
			System.out.println("value1: " + value);
			return value;

		}

		else {

			String query = "select identifier from dataset where identifier like '1%' order by id desc;";
			ResultSet rs = null;
			System.out.println(query);
			String identifier = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {

				identifier = rs.getString("identifier");

			}
			int temp = Integer.valueOf(identifier);
			temp = temp + 1;
			System.out.println(temp);
			System.out.println("Step1");
			value = this.get_doi(String.valueOf(temp));

		}

		System.out.println("Step5");
		return value;

	}

	public int add_image2(String location, String tag, String url, String license, String photographer, String source)
			throws SQLException {

		int id = 0;
		String query = "select id from image where tag=" + "'" + tag + "';";
		ResultSet rs = null;
		rs = stmt.executeQuery(query);
		if (rs.next()) {

			id = rs.getInt("id");

		} else {

			PreparedStatement prep1 = null;

			String query1 = "insert into image(location,tag,url,license,photographer,source) values(?,?,?,?,?,?)";

			System.out.println(query1);
			prep1 = con.prepareStatement(query1);
			prep1.setString(1, location);
			prep1.setString(2, tag);
			prep1.setString(3, url);
			prep1.setString(4, license);
			prep1.setString(5, photographer);
			prep1.setString(6, source);

			prep1.executeUpdate();
			id = this.add_image2(location, tag, url, license, photographer, source);
			prep1.close();

		}
		return id;

	}

	public int check_funder(String funder_name) throws SQLException {

		int id = 0;
		String query = "select id from funder_name where LOWER(primary_name_display)=" + "'" + funder_name.toLowerCase()
				+ "';";
		ResultSet rs = null;
		rs = stmt.executeQuery(query);
		if (rs.next()) {

			id = rs.getInt("id");

		}
		return id;

	}

	public int check_gigadbuser(String email) throws SQLException {

		int id = 0;
		String query = "select id from gigadb_user where LOWER(email)=" + "'" + email.toLowerCase() + "';";
		ResultSet rs = null;
		rs = stmt.executeQuery(query);
		if (rs.next()) {

			id = rs.getInt("id");

		}
		return id;
	}

	public int get_funder(String funder_name) throws SQLException {

		int id = 0;
		String query = "select id from funder_name where LOWER(primary_name_display)=" + "'" + funder_name.toLowerCase()
				+ "';";
		ResultSet rs = null;
		rs = stmt.executeQuery(query);
		if (rs.next()) {

			id = rs.getInt("id");

		} else {

			PreparedStatement prep1 = null;

			String query1 = "insert into funder_name(uri,primary_name_display) values(?,?)";

			System.out.println(query1);
			prep1 = con.prepareStatement(query1);
			prep1.setString(1, "unknown");
			prep1.setString(2, funder_name);

			prep1.executeUpdate();
			id = this.get_funder(funder_name);
			prep1.close();

		}
		return id;

	}

	public String get_editor(int id) throws SQLException {

		String query = "select handing_editor from dataset where id=" + id + ";";
		ResultSet rs = null;
		String name = null;
		rs = stmt.executeQuery(query);
		while (rs.next()) {
			name = rs.getString(1);
		}
		return name;
	}

	public String get_curation_log(int dataset_id, String action) throws SQLException {

		String query = "select comments from curation_log where dataset_id=" + dataset_id + "and action='" + action
				+ "';";
		ResultSet rs = null;
		String comments = null;
		rs = stmt.executeQuery(query);
		while (rs.next()) {
			comments = rs.getString(1);
		}
		return comments;
	}

	public String getallauthor_name(int id) throws SQLException {

		String query = "select name from author, dataset_author where dataset_author.author_id= author.id and dataset_author.dataset_id="
				+ id + " order by author.rank;;";
		ResultSet rs = null;
		String name = null;
		String idlist = "";
		rs = stmt.executeQuery(query);
		ArrayList<String> aa = new ArrayList<String>();
		while (rs.next()) {
			aa.add(rs.getString(1));
		}

		for (int i = 0; i < aa.size(); ++i) {
			idlist = idlist + aa.get(i) + "; ";
		}
		return idlist;
	}

	public int getdataset_id_from_MS(String ms) throws SQLException {

		String query = "select id from dataset where manuscript_id=" + "'" + ms + "';";
		ResultSet rs = null;
		int id = 0;
		// System.out.println(query);
		rs = stmt.executeQuery(query);
		while (rs.next()) {
			id = rs.getInt("id");
		}
		return id;
	}

	public int getdataset_id(int doi) throws SQLException {

		String query = "select id from dataset where identifier=" + "'" + String.valueOf(doi) + "';";
		ResultSet rs = null;
		int id = 0;
		// System.out.println(query);
		rs = stmt.executeQuery(query);
		while (rs.next()) {
			id = rs.getInt("id");
		}
		return id;
	}

	public int getimageid_from_dataset(int dataset_id) throws SQLException {

		String query = "select image_id from dataset where id=" + dataset_id + ";";
		ResultSet rs = null;
		int id = 0;
		// System.out.println(query);
		rs = stmt.executeQuery(query);
		while (rs.next()) {
			id = rs.getInt("image_id");
		}
		return id;
	}

	public void add_dataset(int submitter, String manuscript_id, int image, String identifier, String title,
			String description, int dataset_size, String ftp_site, String upload_status, int publisher, String editor)
			throws SQLException {

		PreparedStatement prep1 = null;
		String query1 = "insert into dataset(submitter_id,image_id,identifier,title,description,dataset_size,ftp_site,excelfile,upload_status,publisher_id,manuscript_id,handing_editor) values(?,?,?,?,?,?,?,?,?,?,?,?)";

		System.out.println(identifier);

		prep1 = con.prepareStatement(query1);
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

	public void deleteimage(int id) throws SQLException {
		PreparedStatement prep1 = null;
		String query = "delete from image where id=" + id;
		System.out.println(query);
		prep1 = con.prepareStatement(query);
		prep1.executeUpdate();
		prep1.close();

	}

	public void deletecurationlog(int id) throws SQLException {
		PreparedStatement prep1 = null;
		String query = "delete from curation_log where dataset_id=" + id
				+ " and created_by='EM' and action='Status updated to ImportFromEM';";
		System.out.println(query);
		prep1 = con.prepareStatement(query);
		prep1.executeUpdate();
		prep1.close();

	}

	public void deletedataset(int id) throws SQLException {
		int image_id = this.getimageid_from_dataset(id);
		this.deleteimage(image_id);

		PreparedStatement prep1 = null;
		String query = "delete from dataset where id=" + id + "and excelfile='upload_from_EM';";
		System.out.println(query);
		prep1 = con.prepareStatement(query);
		prep1.executeUpdate();

		this.deleteauthor(String.valueOf(id));
		this.deletedatasetfunder(String.valueOf(id));
		this.deletedataset_curationlog(String.valueOf(id));

		prep1.close();

	}

	@SuppressWarnings("null")
	public void deletedatasetfunder(String doi) throws SQLException

	{

		PreparedStatement prep1 = null;
		String query1 = "delete from dataset_funder where dataset_id=" + doi;

		System.out.println(query1);
		prep1 = con.prepareStatement(query1);
		prep1.executeUpdate();


		prep1.close();
	}

	@SuppressWarnings("null")
	public void deletedataset_curationlog(String doi) throws SQLException

	{

		PreparedStatement prep1 = null;
		String query1 = "delete from curation_log where dataset_id=" + doi;

		System.out.println(query1);
		prep1 = con.prepareStatement(query1);
		prep1.executeUpdate();


		prep1.close();
	}

	public void add_curation_log(int dataset_id, String current_date, String action, String comment)
			throws SQLException, HttpException, IOException {

		System.out.println("current_date: " + current_date);
		java.sql.Date date = java.sql.Date.valueOf(current_date);
		PreparedStatement prep1 = null;
		String query1 = "insert into curation_log(dataset_id, creation_date, created_by, action, comments) values(?,?,?,?,?)";
		try {
			prep1 = con.prepareStatement(query1);
			prep1.setInt(1, dataset_id);
			prep1.setDate(2, date);
			prep1.setString(3, "EM");
			prep1.setString(4, action);
			prep1.setString(5, comment);
			System.out.println("current_date: " + current_date);

			prep1.executeUpdate();

		} catch (PSQLException e) {

			System.err.println("Can not create the curation log");
			// throw e;
		} finally {
			prep1.close();
		}

	}

	public void addv3author(String surname, String middle_name, String first_name)
			throws SQLException, HttpException, IOException {

		PreparedStatement prep1 = null;
		String query1 = "insert into author(surname,middle_name,first_name) values(?,?,?)";

		prep1 = con.prepareStatement(query1);
		prep1.setString(1, surname);
		prep1.setString(2, middle_name);
		prep1.setString(3, first_name);

		prep1.executeUpdate();

		prep1.close();

	}

	public void close() throws SQLException {
		con.close();

	}

	public static void main(String[] args) throws Exception {
		database db = new database();
	}

}
