import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WordDao implements AutoCloseable {
	
	public static void main(String[] args) {

        String user = "sa";
        String password = "";
        String url = "jdbc:hsqldb:file:./test_db";

        try (Connection c = DriverManager.getConnection(url,user,password)){
        	createTables(c);
        	save(c, "sajt", "cheese");
        	save(c, "kenyer", "bread");
        	save(c, "sajt", "potato");
        	lookup(c, "sajt");
        	list(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	private static void createTables(Connection conn) throws SQLException {
        try(Statement st = conn.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS words;");
            st.executeUpdate("CREATE TABLE words (id INTEGER IDENTITY PRIMARY KEY, hunWord VARCHAR(80), engWord VARCHAR(80));");
            System.out.println("Tables created");
        }
    }
	
	private static void save(Connection conn, String hunWord, String engWord) throws SQLException {
		try(Statement st = conn.createStatement(); 
				ResultSet rs = st.executeQuery("SELECT * from WORDS;")) {
		  try (PreparedStatement pst = conn.prepareStatement("INSERT INTO words (hunWord, engWord) VALUES (?, ?);")) {
			  	
			  	boolean saveable = true;
			  	
			  	while(rs.next()) {
			  		if(rs.getString("hunWord").equals(hunWord) || rs.getString("engWord").equals(engWord)) {
			  			saveable = false;
			  		}
			  	}
			  	
			  	if(saveable) {
			  		pst.setString(1, hunWord);
			  		pst.setString(2, engWord);
			  		pst.addBatch();
			  		pst.executeBatch();
			  	}
	        }
		}
	}

	private static List<Word> lookup(Connection conn, String keyWord) throws SQLException {
		List<Word> words = new ArrayList<>();
		
		try(Statement st = conn.createStatement(); 
			ResultSet rs = st.executeQuery("SELECT * from WORDS;")) {
			
			while(rs.next()) {
            	if(rs.getString("hunWord").equals(keyWord) || rs.getString("engWord").equals(keyWord)) {
            		final String hunWord = rs.getString("hunWord");
            		final String engWord = rs.getString("engWord");
            		Word word = new Word(hunWord, engWord);
            		words.add(word);
	            }
	        }
	    }
		for(Word word : words) {
			System.out.println(word);
		}
		return words;
	}

	private static List<Word> list(Connection conn) throws SQLException {
		List<Word> words = new ArrayList<>();
		
		try(Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * from WORDS;")) {
            	
			while(rs.next()) {
            	final String hunWord = rs.getString("hunWord");
        		final String engWord = rs.getString("engWord");
        		Word word = new Word(hunWord, engWord);
        		words.add(word);
            }
        }
		for(Word word : words) {
			System.out.println(word);
		}
		return words;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
