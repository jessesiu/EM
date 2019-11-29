package emsystem;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.poi.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * create object for the email
 */
public class emailsystem {
	
	private static final Boolean flag = null;
	private MimeMessage mimeMessage = null;
	//public static String directory = "/Users/xiaosizhe/Desktop/demo/"; // file directory
	public static String directory = null;
	//private String saveAttachPath = "/Users/xiaosizhe/Desktop/demo/"; 
	private static String saveAttachPath = null;
	private StringBuffer bodytext = new StringBuffer();// email body
	private String dateformat = "yy-MM-dd HH:mm"; // time format

	public emailsystem(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * get sender address
	 */
	public String getFrom() throws Exception {
		InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null)
			from = "";
		String personal = address[0].getPersonal();
		if (personal == null)
			personal = "";
		String fromaddr = personal + "<" + from + ">";
		return fromaddr;
	}

	/**
	 * get receiver and cc list
	 */
	public String getMailAddress(String type) throws Exception {
		String mailaddr = "";
		String addtype = type.toUpperCase();
		InternetAddress[] address = null;
		if (addtype.equals("TO") || addtype.equals("CC") || addtype.equals("BCC")) {
			if (addtype.equals("TO")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
			} else if (addtype.equals("CC")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
			}
			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String email = address[i].getAddress();
					if (email == null)
						email = "";
					else {
						email = MimeUtility.decodeText(email);
					}
					String personal = address[i].getPersonal();
					if (personal == null)
						personal = "";
					else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new Exception("Error emailaddr type!");
		}
		return mailaddr;
	}

	/**
	 * get email abstract
	 */
	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			if (subject == null)
				subject = "";
		} catch (Exception exce) {
		}
		return subject;
	}

	/**
	 * get email delivery date
	 */
	public String getSentDate() throws Exception {
		Date sentdate = mimeMessage.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat(dateformat);
		return format.format(sentdate);
	}

	/**
	 * email body
	 */
	public String getBodyText() {
		return bodytext.toString();
	}

	/**
	 * Save to StringBuffer
	 */
	public void getMailContent(Part part) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1)
			conname = true;
		System.out.println("CONTENTTYPE: " + contenttype);
		if (part.isMimeType("text/plain") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		} else {
		}
	}

	/**
	 *if the email need reply sign return 'true'
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replysign = false;
		String needreply[] = mimeMessage.getHeader("Disposition-Notification-To");
		if (needreply != null) {
			replysign = true;
		}
		return replysign;
	}

	/**
	 * get the email message id
	 */
	public String getMessageId() throws MessagingException {
		return mimeMessage.getMessageID();
	}

	/**
	 * check whether the email isNew or already read
	 */
	public boolean isNew() throws MessagingException {
		boolean isnew = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags's length: " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				System.out.println("seen Message.......");
				break;
			}
		}
		return isnew;
	}

	/**
	 * check whether the email contain the attachment
	 */
	public boolean isContainAttach(Part part) throws Exception {
		boolean attachflag = false;
		String contentType = part.getContentType();
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE))))
					attachflag = true;
				else if (mpart.isMimeType("multipart/*")) {
					attachflag = isContainAttach((Part) mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1)
						attachflag = true;
					if (contype.toLowerCase().indexOf("name") != -1)
						attachflag = true;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachflag = isContainAttach((Part) part.getContent());
		}
		return attachflag;
	}


	/**
	 * set the attachment path
	 */

	public void setAttachPath(String attachpath) {
		this.saveAttachPath = attachpath;
	}

	/**
	 * set the time format
	 */
	public void setDateFormat(String format) throws Exception {
		this.dateformat = format;
	}

	/**
	 *  get the attach path
	 */
	public String getAttachPath() {
		return saveAttachPath;
	}

	/**
	 * read the attachment
	 */
	@SuppressWarnings({ "resource", "deprecation" })
	public static void readfile(String fileName) throws Exception {
		
		
		XSSFWorkbook wb = null;
		XSSFSheet sheet = null;
		FileInputStream fis = new FileInputStream(fileName);		
		wb = new XSSFWorkbook(fis);
		sheet = wb.getSheetAt(0);
		System.out.println("Sheet" + 0);
		Date current_date= new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(current_date); 
		c.add(Calendar.DATE, -2); // one day before
		current_date = c.getTime();
		System.out.println(current_date);
		XSSFCreationHelper createHelper = wb.getCreationHelper();
		XSSFCellStyle cellStyle         = wb.createCellStyle();
		cellStyle.setDataFormat(
		createHelper.createDataFormat().getFormat("MMMM dd, yyyy")); 
		DateFormat format = new SimpleDateFormat("MM/d/yyyy", Locale.ENGLISH);
		database db= new database();
		ArrayList<String> reject_list = new ArrayList<String> ();
		ArrayList<String> insert_list = new ArrayList<String> ();
		ArrayList<String> compare_list = new ArrayList<String> ();
		//String filelocation ="/Users/xiaosizhe/Desktop/demo/"; //file location
		File file2 = new File(directory +new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"_report.txt");  
		BufferedWriter bw = new BufferedWriter(new FileWriter(file2));
		//String[] to = { "database@gigasciencejournal.com","scott@gigasciencejournal.com","laurie@gigasciencejournal.com","chrisa@gigasciencejournal.com","maryann@gigasciencejournal.com" }; // list of recipient email addresses

        String subject = "EM system manuscript status change";
        Map<String, Integer> rank_map = new HashMap<String, Integer>();
        rank_map.put("ImportFromEM", 1);
        rank_map.put("UserStartedIncomplete", 2);
        rank_map.put("AssigningFTPbox", 3);
        rank_map.put("UserUploadingData", 4);
        rank_map.put("DataAvailableForReview", 5);
        rank_map.put("Rejected", 6);
        rank_map.put("Submitted", 7);
        rank_map.put("DataPending", 8);
        rank_map.put("Curation", 9);
        rank_map.put("AuthorReview", 10);
        rank_map.put("Private", 11);
        rank_map.put("Published", 12);
        rank_map.put("NotRequired", 13);
    
 		for (Row row : sheet) {
 			
 			// select rows
 			if(row.getRowNum() > 104) //set to add more rows here is 100 default is 104
 			{
 				break;
 			}			
			String manuscript_id = null;
			String status = null;
			String status_flag = null;
			String title = null;
			String description = null;
			String submitter_first_name= null;
			String submitter_middle_name= null;
			String submitter_last_name= null;
			String submitter_orcid= null;
			String submitter_email= null;
			String author_list= null;
			String funding_list= null;
			String grand_list= null;
			String award_list= null;
			String curation_log= null;
			String editor= null;
			
			
			if(row.getRowNum() > 4) {
			// for(int c=1;c<col;c++){
			for (Cell cell : row) {
			
				int columnIndex = cell.getColumnIndex();
				if (columnIndex == 0) {
					if(cell.getStringCellValue().startsWith("GIGA"))
					{
						manuscript_id = cell.getStringCellValue();
						System.out.println("Manuscript_id: "+manuscript_id);
					}else
					{
						continue;
					}				
				}
				if (columnIndex == 1){
					status = cell.getStringCellValue();
					System.out.println("status: "+status);
					}
				if (columnIndex == 2){
					status_flag = cell.getStringCellValue().trim();
					System.out.println("status_flag: "+status_flag);
					}
				if (columnIndex == 3){
					title = cell.getStringCellValue().replace("'", "''").replaceAll("\\r|\\n", "<br>");
					System.out.println("title: "+title);
					}
				if (columnIndex == 4){
					description = cell.getStringCellValue().replace("'", "''").replaceAll("\\r|\\n", "<br>");
					System.out.println("description: "+description);
					}
				if (columnIndex == 5){
					submitter_first_name = cell.getStringCellValue().replace("'", "''");;
					System.out.println("submitter_first_name: "+submitter_first_name);
					}
				if (columnIndex == 6){
					submitter_middle_name = cell.getStringCellValue().replace("'", "''");;
					System.out.println("submitter_middle_name: "+submitter_middle_name);
					}
				if (columnIndex == 7){
					submitter_last_name = cell.getStringCellValue().replace("'", "''");;
					System.out.println("submitter_last_name: "+submitter_last_name);
					}
				// orcid will not used in here
				if (columnIndex == 8){
					submitter_orcid = cell.getStringCellValue();
					System.out.println("submitter_orcid: "+submitter_orcid);
					}
				if (columnIndex == 9){
					submitter_email = cell.getStringCellValue().replace("'", "''");
					if(submitter_email.contains(";"))
					{
						String[] temp = submitter_email.split(";");
						submitter_email= temp[0];
					}
					System.out.println("submitter_email: "+submitter_email);
					}
				if (columnIndex == 10){
					author_list = cell.getStringCellValue().replace("'", "''");;
					System.out.println("author_list: "+author_list);
					}
				if (columnIndex == 12){
					funding_list = cell.getStringCellValue().replace("'", "''");
					System.out.println("funding_list: "+funding_list);
					}
				if (columnIndex == 13){
										
					if(cell.getCellType()==0)
					{
						grand_list= String.valueOf(cell.getNumericCellValue()).replace("'", "''");;
						if(grand_list.endsWith(".0"))
	             		 {
							grand_list=grand_list.replace(".0", "");
	             		 }
			                	
		                	 System.out.println(grand_list);
					}else{
						
						grand_list = cell.getStringCellValue().replace("'", "''");;
						System.out.println("grand_list: "+grand_list);
						
					}
					}
				if (columnIndex == 14){
					award_list = cell.getStringCellValue().replace("'", "''");;
					System.out.println("award_list: "+award_list);
					}
				if (columnIndex == 15){
					curation_log = cell.getStringCellValue().replace("'", "''");;
					System.out.println("curation_log: "+curation_log);
					}
				
				
				if (columnIndex == 16){		
					cell.setCellStyle(cellStyle);
					Date record_date = cell.getDateCellValue();
					//System.out.println ("Is shows Status change date as show in Excel file: " + cell.getDateCellValue());	
					if(record_date.after(current_date))
					{
						System.out.println ("the row number for the date editorial status date : "+ (row.getRowNum()+1));
					}
					}
				
				if (columnIndex == 18){
					if(cell.getStringCellValue() == "" || cell.getStringCellValue() == null)
					{
						continue;
					}
					Date flag_date = format.parse(cell.getStringCellValue());
					if(flag_date.after(current_date))
					{
						System.out.println ("the row number for the date flag set : "+ (row.getRowNum()+1));
					}					
					}
				if (columnIndex == 20){
					editor = cell.getStringCellValue();
					System.out.println("editor: "+editor);
					}
				
				
			}
			
			// check and delete reject manuscript
			int return_dataset_id=db.getdataset_id_from_MS(manuscript_id);
		
			if(status.contains("Final Decision Reject"))
			{
				
				if(return_dataset_id != 0){
					String current_status = db.get_status(return_dataset_id);	
					if(rank_map.get(current_status.trim()) < 3)
					{
						db.deletedataset(return_dataset_id);				
						bw.write("-------------------------------------------"+"\n\n");
						bw.write("DELETE dataset id "+return_dataset_id+" manuscript_id "+manuscript_id+"\n\n");
						bw.write("-------------------------------------------"+"\n\n");
					}
				}
				reject_list.add(manuscript_id);
				continue;
			}
			
		
			Boolean flag1=true; 
			Boolean flag2=true; 
			Boolean flag3=true; 
			 

			 for(String j:reject_list)
			 {
				 if(j == manuscript_id)
				 {
					 flag1=false;
				 }
				  
			 }
			 for(String j:insert_list)
			 {
				 if(j == manuscript_id)
				 {
					 flag2=false;
				 }
				  
			 }
			 for(String j:compare_list)
			 {
				 if(j == manuscript_id)
				 {
					 flag3=false;
				 }
				  
			 }
			 
			 if(flag1 == false || flag2 == false || flag3 == false)
			 {
				 continue;
			 }
			 // finish
			 
			// compare updated manuscript
			 
			 if(return_dataset_id > 0)
				
			 {
					System.out.println ("check_repeat_ms: " +return_dataset_id);
					String current_status = db.get_status(return_dataset_id);					
					String log_date= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					// compare part need to add inside
					//Get Data For Review=AssigningFTPbox   Get DOI Ready for GigaDB=Submitted  ignore at this stage
					//db.comparestatus();
					bw.write("---------------------------------------------update exist dataset begin ------------------------\n");
					//db.comparestatus_flag(flag);
					if(curation_log != "" && curation_log !=null)
					{
						
						String old_log = db.get_curation_log(return_dataset_id, "notes from Eds");
						if(old_log !=null && old_log.equals(curation_log)==false)
						{
						
						//db.deletecurationlog(return_dataset_id);
						db.add_curation_log(return_dataset_id, log_date, "notes from Eds", curation_log);
						bw.write("curation_log|"+return_dataset_id+"|"+log_date+"|"+curation_log+"\n");
						}
						//bw1.write("curation_log|"+return_dataset_id+"|"+log_date+"|"+curation_log+"\n");
					}
					if(editor != "" && editor !=null)
					{
						String old_editor = db.get_editor(return_dataset_id);
						if(old_editor !=null && old_editor.equals(editor)==false)
						{
						db.update_dataset_editor(return_dataset_id, editor);
						bw.write("dataset_editor|"+return_dataset_id+"|"+editor+"\n");
						}
					}
					if(status_flag != null)
					{
						
					
					//if(status_flag.equals("Request Data For Review") && !current_status.equals("Submitted") && !current_status.equals("AssigningFTPbox"))
					if(status_flag.equals("Request Data For Review") && rank_map.get(current_status.trim()) < 3)	
					{
						System.out.println("Get in Request Data For Review");
						db.add_curation_log(return_dataset_id, log_date, "Status move from ImportFromEM upto AssigningFTPbox", "");
						db.update_dataset_status(return_dataset_id,"AssigningFTPbox");
						String print_message1="curation_log|"+return_dataset_id+"|"+log_date+"|"+"EM"+"|"+"Status change"+"|"+"Status move from ImportFromEM upto AssigningFTPbox"+"\n";
						bw.write(print_message1);
						String website_link="http://gigadb-staging-jesse.gigatools.net/adminDataset/update/id/"+return_dataset_id;
						String msgBody = "The manucript ID: "+manuscript_id+" status changed to AssigningFTPbox, Pleasae check the dataset page "+ website_link;
						sendFromGMail(subject, msgBody);
					}
					//if(status_flag.equals("Get DOI Ready for GigaDB") && !current_status.equals("Submitted"))
					if(status_flag.equals("Get DOI Ready for GigaDB") && rank_map.get(current_status.trim()) < 7)
					{
						db.add_curation_log(return_dataset_id, log_date, "Status move from DataAvailableForReview upto Submitted", "");
						db.update_dataset_status(return_dataset_id,"Submitted");
						String print_message1="curation_log|"+return_dataset_id+"|"+log_date+"|"+"EM"+"|"+"Status change"+"|"+"Status move from DataAvailableForReview upto Submitted"+"\n";
						bw.write(print_message1);
						String website_link="http://gigadb-staging-jesse.gigatools.net/adminDataset/update/id/"+return_dataset_id;
						String msgBody = "The manucript ID: "+manuscript_id+" status changed to Submitted, Pleasae check the dataset page "+ website_link;
						sendFromGMail(subject, msgBody);
						
					}
					}

					bw.write("---------------------------------------------update exist dataset end ------------------------\n");
					
					compare_list.add(manuscript_id);
							
					continue;
			 }
				
				// check insert or delete before
			 String log_location = directory +new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"_"+manuscript_id+"_report.txt";
			 File file3 = new File(log_location); // output file
			 BufferedWriter bw1 = new BufferedWriter(new FileWriter(file3));
			 int submitter_id=  db.add_submitter(submitter_email, submitter_first_name, submitter_last_name);
			 int tmp = db.check_gigadbuser(submitter_email);
			 if(tmp != 0)
			 {
				 bw.write("gigadb_user already exists, id "+ tmp +"\n");
				 bw1.write("gigadb_user already exists, id "+ tmp +"\n");
			 }else{
			 String print_ms_user="gigadb_user|"+submitter_id+"|"+submitter_email+"|"+submitter_first_name+"|"+submitter_last_name+"\n";
			 bw.write(print_ms_user);
			 bw1.write(print_ms_user);
			 }
			 System.out.println("submitter_id: "+submitter_id);
			 int image_id=db.add_image2("no_image.png", manuscript_id, "http://gigadb.org/images/data/cropped/no_image.png", "CC0", "GigaDB", "GigaDB");
			 bw.write("image|"+"no_image.png"+"|"+manuscript_id+"|"+"http://gigadb.org/images/data/cropped/no_image.png"+"|"+"CC0"+"|"+"GigaDB"+"|"+"GigaDB"+"\n");
			 //bw1.write("image|"+"no_image.png"+"|"+manuscript_id+"|"+"http://gigadb.org/images/data/cropped/no_image.png"+"|"+"CC0"+"|"+"GigaDB"+"|"+"GigaDB"+"\n");
			 System.out.println("image_id: "+image_id);
			 String identifier= db.get_doi(null);
			 System.out.println("doi: "+identifier);
			 String ftp_site= "ftp://parrot.genomics.cn/gigadb/pub/10.5524/100001_101000/"+identifier+"/";
			 if(status_flag != null && status_flag.equals("Request Data For Review"))
			 {
				 db.add_dataset(submitter_id,manuscript_id, image_id, identifier, title, description, 1024, ftp_site,"AssigningFTPbox", 3, editor);	 
			 }else{
				 db.add_dataset(submitter_id,manuscript_id, image_id, identifier, title, description, 1024, ftp_site,"ImportFromEM", 3, editor);
			 }
			 insert_list.add(manuscript_id);
			 bw.write("dataset|"+submitter_id+"|"+manuscript_id+"|"+image_id+"|"+identifier+"|"+title+"|"+description+"|"+ftp_site+"|"+"Incomplete"+"\n");
			 //bw1.write("dataset|"+submitter_id+"|"+manuscript_id+"|"+image_id+"|"+identifier+"|"+title+"|"+description+"|"+ftp_site+"|"+"Incomplete"+"\n");
			 int dataset_id = db.getdataset_id(Integer.valueOf(identifier));
			 int author_id = db.getid("author");
			 String[] aa = author_list.split(";");
			 for(int i=0; i<aa.length; i++)
			 {
				 String[] bb = aa[i].split(","); 
				 String[] names= bb[0].trim().split(" ");
				 String first_name = null;
				 String middle_name = null;
				 String surname = null;
				 if(names.length > 2)
				 {
					 first_name = names[0];
					 middle_name = names[1];
					 surname = names[2];
					 
				 }
				 else{
					 
					 first_name = names[0];
					 middle_name = "";
					 surname = names[1];
					 
				 }
				 
				 System.out.println(first_name + " " + middle_name + " " + surname);
				 
				 db.addv3author(surname, middle_name, first_name);
				 String print_ms_author="auther|"+author_id+"|"+surname+"|"+middle_name+"|"+first_name+"\n";
				 bw.write(print_ms_author);
				 bw1.write(print_ms_author);				
				 db.add_dataset_author(dataset_id, author_id, i+1);
				 int rank = i+1;
				 String print_ms_dataset_author="dataset_auther|"+dataset_id+"|"+author_id+"|"+rank+"\n";
				 bw.write(print_ms_dataset_author);
				 //bw1.write("dataset_auther|"+dataset_id+"|"+author_id+"|"+rank+"\n");
				 author_id++;
				 
			 }
			 
			
			
			
			//funder part
			if(funding_list != null && funding_list != " " && funding_list != "")
			{	
				String funding[] = funding_list.split(";");
				String grand[] = grand_list.split(";");
				String award[] = award_list.split(";");
			
				for(int i=0; i< funding.length;i++)
					{
						/*
						if(db.check_funder(funding[i].trim()) == true)
						{
							bw.write("funder_name|"+funding[i].trim()+"\n");
						}
						*/
						int temp = db.check_funder(funding[i].trim());
						if(temp != 0 )
						{
						String print_ms_funder="funding body already exists, id:"+ temp+" name: "+funding[i].trim()+"\n";
						bw.write(print_ms_funder);
						bw1.write(print_ms_funder);
						}else{
						bw.write("funder|"+funding[i].trim()+"\n");
						bw1.write("funder|"+funding[i].trim()+"\n");	
						}
						int funding_id = db.get_funder(funding[i].trim());
			  
						db.add_dataset_funder(dataset_id, funding_id, grand[i].trim(), award[i].trim());
						String print_ms_dataset_funder="dataset_funder|"+funding[i].trim().replace("''", "'")+"|"+grand[i].trim()+"|"+award[i].trim()+"\n";
						
						bw.write(print_ms_dataset_funder);
						bw1.write(print_ms_dataset_funder);
			  
		
					}
			}
			
			
			// curation_log
			String log_date= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			db.add_curation_log(dataset_id, log_date, "Status updated to ImportFromEM", "");
			String print_message="curation_log|"+dataset_id+"|"+log_date+"|"+"EM"+"|"+"Status updated to ImportFromEM"+"|"+""+"\n";
			bw.write(print_message);
			bw1.write(print_message);			
			if(curation_log != "" && curation_log !=null)
			{
				
				db.add_curation_log(dataset_id, log_date, "notes from Eds", curation_log);
				String print_message1="curation_log|"+dataset_id+"|"+log_date+"|"+"EM"+"|"+"notes from Eds"+"|"+curation_log+"\n";
				bw.write(print_message1);
				bw1.write(print_message1);
			}

			
			db.add_curation_log(dataset_id, log_date,"log file location", log_location);
			String print_message2="curation_log|"+dataset_id+"|"+log_date+"|"+"EM"+"|"+"log file location"+"|"+log_location+"\n";
			bw.write(print_message2);
			bw1.write(print_message2);
			
			if(status_flag!=null && status_flag.equals("Request Data For Review"))
			{
				db.add_curation_log(dataset_id, log_date, "Status move from ImportFromEM upto AssigningFTPbox", "");
				print_message="curation_log|"+dataset_id+"|"+log_date+"|"+"EM"+"|"+"Status move from ImportFromEM upto AssigningFTPbox"+"|"+""+"\n";
				bw.write(print_message);
				bw1.write(print_message);
				String website_link=Setting.link+"adminDataset/update/id/"+dataset_id;
				String msgBody = "The manucript ID: "+manuscript_id+" status changed to AssigningFTPbox, Pleasae check the dataset page "+ website_link;
				sendFromGMail(subject, msgBody);
			}
			
			bw.write("-----------------------------------------------------------------"+"\n\n");
			
			
			
			
			bw1.close();
			}
			

			
			
		}
 		
 		bw.close();
 		db.close();

	}
	
	public static void sendFromGMail(String subject, String body) {
 
		    final String username = "xiaosizhejesse@gmail.com";
	        final String password = "jttpabnzyvpbnqmd";

	        Properties prop = new Properties();
			prop.put("mail.smtp.host", "smtp.gmail.com");
	        prop.put("mail.smtp.port", "587");
	        prop.put("mail.smtp.auth", "true");
	        prop.put("mail.smtp.starttls.enable", "true"); //TLS
	        
	        Session session = Session.getInstance(prop,
	                new javax.mail.Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication(username, password);
	                    }
	                });

	        try {

	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress("xiaosizhejesse@gmail.com"));
	            message.setRecipients(
	                    Message.RecipientType.TO,
	                    InternetAddress.parse(Setting.mailto)
	            );
	            message.setSubject(subject);
	            message.setText(body);

	            Transport.send(message);

	            System.out.println("Done");

	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
	    }


	
	

	/**
	 * main function
	 */
	public static void main(String args[]) throws Exception {
		
		Setting.Loadsetting();
		directory = Setting.savedir;
		saveAttachPath = Setting.savedir;
		/*
		readfile("/Users/xiaosizhe/Desktop/demo/2019-07-10report.xlsx");
		*/
		String email = "xiaosizhejesse@gmail.com";
		String password = "jttpabnzyvpbnqmd";
		// set properties
		Properties properties = new Properties();
		// You can use imap or imaps , *s -Secured
		properties.put("mail.store.protocol", "imaps");
		// Host Address of Your Mail
		properties.put("mail.imaps.host", "imap.gmail.com");
		// Port number of your Mail Host
		properties.put("mail.imaps.port", "993");

		Session session = Session.getDefaultInstance(properties, null);
		// SET the store for IMAPS
		Store store = session.getStore("imaps");

		System.out.println("Connection initiated......");
		// Trying to connect IMAP server
		store.connect(email, password);
		System.out.println("Connection is ready :)");
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);//READ_ONLY
		Message message[] = folder.getMessages();
		System.out.println("Messages's length: " + message.length);
		emailsystem pmm = null;
		Flags deleted = new Flags(Flags.Flag.DELETED);
		for (int i = 0; i < message.length; i++) {
			System.out.println("======================");
			pmm = new emailsystem((MimeMessage) message[i]);
			if (pmm.getFrom().equals("GigaScience Editorial Office<em@editorialmanager.com>")
					&& pmm.isContainAttach((Part) message[i]) == true) {
				Date current_date = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(current_date);
				c.add(Calendar.DATE, -1);
				current_date = c.getTime();
				System.out.println("current_date: " + current_date);
				DateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.ENGLISH);
				Date file_date = format.parse(pmm.getSentDate());
				System.out.println("file_date: " + file_date);
				System.out.println("file_date: " + pmm.getSentDate());
				if (file_date.after(current_date)) {
					System.out.println("Message " + i + " subject: " + pmm.getSubject());
					System.out.println("Message " + i + " sentdate: " + pmm.getSentDate());
					System.out.println("Message " + i + " replysign: " + pmm.getReplySign());
					System.out.println("Message " + i + " hasRead: " + pmm.isNew()); //check whether it's new email
					System.out.println("Message " + i + "  containAttachment: " + pmm.isContainAttach((Part) message[i]));
					System.out.println("Message " + i + " from: " + pmm.getFrom());
					System.out.println("Message " + i + " to: " + pmm.getMailAddress("to"));
					System.out.println("Message " + i + " cc: " + pmm.getMailAddress("cc"));
					System.out.println("Message " + i + " bcc: " + pmm.getMailAddress("bcc"));
					pmm.setDateFormat("yy年MM月dd日 HH:mm");
					System.out.println("Message " + i + " sentdate: " + pmm.getSentDate());
					System.out.println("Message " + i + " Message-ID: " + pmm.getMessageId());
					// get content
					pmm.getMailContent((Part) message[i]);
					System.out.println("Message " + i + " bodycontent: \r\n" + pmm.getBodyText());
					
					//message[i].setFlag(Flags.Flag.SEEN, true);//设置为已读，不能使用。。。。 message[i].saveChanges();
					 
					pmm.setAttachPath("directory"+"attachments"); 
					//pmm.saveAttachMent((Part) message[i]);
					 
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					List<File> attachments = new ArrayList<File>();

					Multipart multipart = (Multipart) message[i].getContent();
					String filename = null;

					for (int j = 0; j < multipart.getCount(); j++) {
						BodyPart bodyPart = multipart.getBodyPart(j);
						if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
							continue; // dealing with attachments only
						}
						InputStream is = bodyPart.getInputStream();
						File f = new File(directory + sdf.format(new Date()) + bodyPart.getFileName()); // change file path here
						filename=directory + sdf.format(new Date()) + bodyPart.getFileName();																		
						FileOutputStream fos = new FileOutputStream(f);
						byte[] buf = new byte[4096];
						int bytesRead;
						while ((bytesRead = is.read(buf)) != -1) {
							fos.write(buf, 0, bytesRead);
						}
						fos.close();
						attachments.add(f);
					}
					
					readfile(filename);
                
					
				}else{
					message[i].setFlags(deleted, true);
				}

			}else{
				message[i].setFlags(deleted, true);
			}
		}
		folder.close(true);
		
	}
}
