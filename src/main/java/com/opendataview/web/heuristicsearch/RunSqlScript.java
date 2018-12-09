package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunSqlScript extends MainClass {
	private static final long serialVersionUID = 1L;
   public RunSqlScript(PageParameters parameters) throws IOException {
		super(parameters);
		// TODO Auto-generated constructor stub
	}

private final static Logger log = LoggerFactory.getLogger(RunSqlScript.class);
   private static int new_id=0;
   
  public static void runSqlScript() throws ClassNotFoundException, SQLException {

    //String sqlScriptFilePath = sqlinserts_file;

    Class.forName(web_dbdriver);
    Connection conn = DriverManager.getConnection(web_dburl, web_dbusr, web_dbpwd);

    try {
    	
    	Statement st = conn.createStatement();
    if(removeExistingBData) {
    		// remove all existing content from table locations
    		log.info("Removing existing data...");
    		st.executeUpdate("TRUNCATE locations CASCADE;");
    		log.info("ok");
    }
    	
    	// we return the greatest id or 0 as the starting id
    	String sql = "SELECT coalesce(max(id), 0) FROM locations";
    	PreparedStatement ps = conn.prepareStatement(sql);
    	ResultSet rs = ps.executeQuery();
    	


    	while(rs.next()) {
    		new_id = Integer.valueOf(rs.getString(1));

    		log.info("new id "+new_id);
    	}
    	rs.close();

    	
    	BufferedReader br = new BufferedReader(new FileReader("sql"));
    	
    	String line = br.readLine();
    	
    	if (line == null) {
    	    log.info("File empty! No inserts to execute.\nYou must first create the SQL inserts (executeSQLqueries=false)");
    	}
        
    	while (line != null) {
    		    new_id++;
    		    line = line.replaceFirst("\\(", "(id,").replace("VALUES(", "VALUES("+new_id+",");

			log.info(line);

    	    st.executeUpdate(line);
    	    line = br.readLine();
	}
    	st.close();
    	br.close();
    	ps.close();
    	rs.close();
    	
      //ScriptRunner sr = new ScriptRunner(conn, false, false);
      //Reader reader = new BufferedReader(new FileReader(sqlScriptFilePath));
      //log.info("Executing file: "+sqlScriptFilePath);
     // sr.runScript(reader);

    } catch (Exception e) {
      System.err.println("Failed to Execute SQL file, with error: "
          + e.getMessage());
    }
  }
}
