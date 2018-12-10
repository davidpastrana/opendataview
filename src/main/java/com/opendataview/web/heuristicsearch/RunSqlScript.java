package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunSqlScript {
	private static final long serialVersionUID = 1L;
//   public RunSqlScript(PageParameters parameters) throws IOException {
//		super(parameters);
//		// TODO Auto-generated constructor stub
//	}

private final static Logger log = LoggerFactory.getLogger(RunSqlScript.class);
   private static int new_id=0;
   
  public static void runSqlScript(File source, Connection conn, boolean removeExistingBData) throws ClassNotFoundException, SQLException, IOException {

    //String sqlScriptFilePath = sqlinserts_file;

	    BufferedReader br =
	            new BufferedReader(new InputStreamReader(new FileInputStream(source), "utf-8"));
    log.info("line0 "+br.readLine());
    log.info("line1 "+br.readLine());
    log.info("line2 "+br.readLine());
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
    	log.info("PATH SQL FILE 5: "+source.getAbsoluteFile());
    	log.info("we are here "+new_id);
    	
        log.info("file taken: "+source.getAbsolutePath());
        log.info("line "+br.readLine());

    	String line = br.readLine();
    	
    	if (line == null) {
    	    log.info("File empty! No inserts to execute.");
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
