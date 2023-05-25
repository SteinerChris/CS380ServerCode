

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.security.NoSuchAlgorithmException;

public class Database {
    /*
     * public static void main(String[] args) {
     * System.out.println("Check: "+checkConnect()!=null);
     * System.out.println("can create? " + createUser("steiner","aoeu"));
     * System.out.println("log in? " + verifyCredentials("steiner","aoeu"));
     *
     *
     * }
     */
    private static Connection con = null;
    /**
     * connects to the database
     */
    public static void connect() {
    	try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String url = "jdbc:mysql://sql9619545@sql9.freemysqlhosting.net/sql9619545";
        String userName = "sql9619545";
        String pass = "TALaShDLMD";

        try {
            con = DriverManager.getConnection(url, userName, pass);
            System.out.println("connected");


        } catch (Exception e) {
            System.out.println("exception ");
            e.printStackTrace();
        }
    }

    /**
     * checks if connect, and if not, starts a new connection to the database. Has the connection be a singleton.
     * @return returns the connection to the database
     */
    public static Connection checkConnect(){
        if(con==null) connect();
        return con;
    }

    /**
     * returns true if the username corresponds to a username in the database, and the hash of the password matches
     * @param username
     * @param password
     * @return
     */
    public static boolean verifyCredentials(String username, String password){
        try {
        	//testPrint();
            ResultSet res = executeQuery("SELECT * from logins WHERE username=?;","login ", username);
            System.out.println("resultset for login:: : ");
            //printResultSet(res);
            res.next();
            //System.out.println("Password is: "+ sha256);
            if(!res.getString("password").equals(sha256(password))) return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public static void testPrint() {
    	try {
			printResultSet(con.createStatement().executeQuery("SELECT * from logins"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * makes a user with username username and password password
     * returns false if such a user already exists of if the username/password is invalid
     * puts entries into the database under the logins table with the username and password
     * @param username
     * @param password
     * @return
     */
    public static boolean createUser(String username, String password){

        //sql injection prevention
        if(username.contains(";")||password.contains(";")||username.contains("(")||password.contains("(")||username.contains(")")||password.contains(")")||username.contains("'")||password.contains("'")) return false;
        try {
            ResultSet res = executeQuery("SELECT * from logins WHERE username=?;","getuser",username);


            //if user already exists with same name, can't create user
            if(res.next()) return  false;

            executeUpdate("INSERT INTO logins (username, password) VALUES(?, ?);","insertuser", username, sha256(password));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }

    /**
     * returns the string of the hash encoded in base64
     * @param s string to hash
     * @return
     */
    public static String sha256(String s) {
        try {
            MessageDigest md =  MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x",new BigInteger(1, hashBytes));
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * executes a MYSQL query of query and prints what the query is optionally
     * @param query
     * @param printQueryBeforeExecuting
     * @return
     */
    public static ResultSet executeQuery(String query, String printQueryBeforeExecuting, String...variables) {
        try {
            if(printQueryBeforeExecuting!=null) System.out.println(printQueryBeforeExecuting+" Query is: "+ query);

            PreparedStatement statement = checkConnect().prepareStatement(query);

            for(int i = 0; i<variables.length;i++) {
                statement.setString(i+1, variables[i]);
            }
            //System.out.println("execute query called");
            ResultSet res = statement.executeQuery();
            //printResultSet(res);
            System.out.println("is res null"+res==null);
            return res;


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * executes an update of the mysql database with the query query.
     * @param query
     * @param printQueryBeforeExecuting
     */
    public static void executeUpdate(String query, String printQueryBeforeExecuting, String...variables) {
        try {
            if(printQueryBeforeExecuting!=null) System.out.println(printQueryBeforeExecuting+" Query is: "+ query);
            //con.createStatement().executeUpdate(query);
            PreparedStatement statement = checkConnect().prepareStatement(query);

            for(int i = 0; i<variables.length;i++) {
                statement.setString(i+1, variables[i]);
            }
            statement.executeUpdate();


        } catch (SQLException e) {
           e.printStackTrace();

        }
    }
  /**
   * from zeb https://stackoverflow.com/questions/24229442/print-the-data-in-resultset-along-with-column-names
   * @param resultSet
   */
  public static void printResultSet(ResultSet resultSet) {
	  try {
	  ResultSetMetaData rsmd = resultSet.getMetaData();
	  int columnsNumber;
		columnsNumber = rsmd.getColumnCount();
	  while (resultSet.next()) {
	      for (int i = 1; i <= columnsNumber; i++) {
	          if (i > 1) System.out.print(",  ");
	          String columnValue = resultSet.getString(i);
	          System.out.print(columnValue + " " + rsmd.getColumnName(i));
	      }
	      System.out.println("");
	  }
	  //resultSet.beforeFirst();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  

}
