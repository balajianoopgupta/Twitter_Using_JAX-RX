package com.twitterservices;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import org.json.simple.JSONObject;
import com.data.Tweets;
import com.data.UserFollowersList;
import com.data.UserFollowingList;
import com.data.UserSuggestion;
import com.google.gson.Gson;

@WebService
public class LoginService {
	//DatabaseConnection connection = new DatabaseConnection();
	//Connection conn = connection.config();
	ConnectionPooling connection = new ConnectionPooling();
	Connection conn = connection.getConnectionFromPool();
	
	public String checkLogin(String username, String password) throws SQLException{

		JSONObject resultSet = new JSONObject();
		Statement stmt = null;
		String sql;
		stmt = conn.createStatement();
		sql = "SELECT * FROM users WHERE password = SHA1('"+password+"') AND username='" +username+ "'";

		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				resultSet.put("username", rs.getString(1));
				resultSet.put("fname", rs.getString(3));
				resultSet.put("lname", rs.getString(4));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//System.out.println("Output being returned is: " +resultSet.toJSONString());
		return resultSet.toJSONString();
	}

	public String calculateValues(String username) throws SQLException{
		
		JSONObject resultSet = new JSONObject();
		Statement stmt = null;
		String sql;
		System.out.println("Username passed to calculateValues is: "+username);
		stmt = conn.createStatement();
		sql="SELECT concat(fname,' ',lname) usrname,usr.twitterhandle, F.FOLLOWERS, T.FOLLOWING, TW.TWEETS" +
				"	FROM " +
				"	(SELECT count(*) FOLLOWERS 	from followers	where follows='"+username+"')F," +
				"	(SELECT count(*) FOLLOWING 	from followers	where uname='"+username+"')T," +
				"	(SELECT count(*) TWEETS	from tweets	where username='"+username+"')TW," +
				"	users usr	where username='"+username+"'";

		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				resultSet.put("fullname", rs.getString(1));
				resultSet.put("twitterhandle", rs.getString(2));
				resultSet.put("followerscount", rs.getString(3));
				resultSet.put("followingcount", rs.getString(4));
				resultSet.put("tweetscount", rs.getString(5));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//System.out.println("calculateValues returns: " +resultSet.toJSONString());
		return resultSet.toJSONString();
	}

	public String renderTweets(String username) throws SQLException{

		List<Tweets> allTweets = new ArrayList<Tweets>();
		Statement stmt = null;
		String sql,result=null;
		System.out.println("Username passed to renderTweets is: "+username);
		stmt = conn.createStatement();
		sql="select distinct t.tweet, concat(u.fname,' ',u.lname)usrname, twitterhandle" +
				"	from tweets t" +
				"	inner join followers f" +
				"	on t.username='"+username+"' " +
				"   or(t.username=f.follows and f.uname='"+username+"' )" +
				"	inner join users u" +
				"	on u.username=t.username" +
				"	order by t.tweetTime desc";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String tweet = rs.getString("tweet");
				String fullname = rs.getString("usrname");
				String twitterhandle = rs.getString("twitterhandle");

				Tweets newTweet = new Tweets(tweet,fullname,twitterhandle);

				allTweets.add(newTweet);
			}
			Gson gson = new Gson();
			String json = gson.toJson(allTweets);
			System.out.println("Json string:"+json);
			result=json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println("renderTweets returns: " +result);
		return result;
	}

	public String suggestFollowers(String username) throws SQLException{

		List<UserSuggestion> suggestions = new ArrayList<UserSuggestion>();
		Statement stmt = null;
		String sql,result=null;

		System.out.println("\nUsername passed to suggestFollowers is: "+username);
		stmt = conn.createStatement();
		sql="select concat(u.fname,' ',u.lname)usrname, twitterhandle " +
				" from users u " +
				" where not exists " +
				" (select * from followers where uname='"+username+"' and follows=u.username) and username!='"+username+"'";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String fullname = rs.getString("usrname");
				String twitterhandle = rs.getString("twitterhandle");

				UserSuggestion suggestedUser = new UserSuggestion(fullname,twitterhandle);

				suggestions.add(suggestedUser);
			}
			Gson gson = new Gson();
			String json = gson.toJson(suggestions);
			System.out.println("Json string:"+json);
			result=json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println("suggestFollowers returns: " +result);
		return result;
	}

	public String updateFollowers(String username, String thandler) throws SQLException{

		Statement stmt = null;
		String sql,result=null;
		int rowcount = 0;
		System.out.println("\nUsername passed to addTweetToDB is: "+username);
		stmt = conn.createStatement();
		sql="INSERT INTO `twitter`.`followers` (`uname`, `follows`) SELECT '"+username+"', username FROM users u where twitterhandle='"+thandler+"';";
		
		try {
			rowcount=stmt.executeUpdate(sql);
			if(rowcount>0){
				result="true";
				System.out.println("Inserted succesfully");
			}
			else{
				result="false";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("addTweetToDB returns: " +result);
		return result;
	}


	public String addTweetToDB(String username, String tweet) throws SQLException{

		Statement stmt = null;
		String sql,result=null;
		int rowcount = 0;
		System.out.println("\nUsername passed to addTweetToDB is: "+username);
		stmt = conn.createStatement();
		sql="INSERT INTO `twitter`.`tweets` (`username`, `tweet`) VALUES ('"+username+"', '"+tweet+"')";

		try {
			rowcount=stmt.executeUpdate(sql);
			if(rowcount>0){
				result="true";
				System.out.println("Inserted succesfully");
			}
			else{
				result="false";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("addTweetToDB returns: " +result);
		return result;
	}

	public String renderUserTweets(String username) throws SQLException{

		List<Tweets> allTweets = new ArrayList<Tweets>();
		Statement stmt = null;
		String sql,result=null;
		System.out.println("Username passed to renderUserTweets is: "+username);
		stmt = conn.createStatement();
		sql="SELECT concat(fname,' ',lname) usrname, usr.twitterhandle, TW.TWEET " +
				"FROM" +
				"(SELECT tweet TWEET from tweets where username='"+username+"')TW," +
				" users usr where username='"+username+"' " +
				" order by TW.TWEET desc;";

		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String tweet = rs.getString(3);
				String fullname = rs.getString(1);
				String twitterhandle = rs.getString(2);

				Tweets newTweet = new Tweets(tweet,fullname,twitterhandle);

				allTweets.add(newTweet);
			}
			Gson gson = new Gson();
			String json = gson.toJson(allTweets);
			System.out.println("Json string:"+json);
			result=json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println("renderUserTweets returns: " +result);
		System.out.println();
		return result;
	}

	public String renderUserFollowing(String username) throws SQLException{

		List<UserFollowingList> allFollowing = new ArrayList<UserFollowingList>();
		Statement stmt = null;
		String sql,result=null;
		System.out.println("\nUsername passed to renderUserFollowing is: "+username);
		stmt = conn.createStatement();
		sql="SELECT concat(fname,' ',lname) usrname, usr.twitterhandle,FW.FOLLOW" +
				"	FROM " +
				"   (SELECT follows FOLLOW  from followers where uname='"+username+"')FW," +
				"    users usr where username=FW.FOLLOW;";

		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String fullname = rs.getString(1);
				String twitterhandle = rs.getString(2);

				UserFollowingList following = new UserFollowingList(fullname,twitterhandle);

				allFollowing.add(following);
			}
			Gson gson = new Gson();
			String json = gson.toJson(allFollowing);
			System.out.println("Json string:"+json);
			result=json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println("renderUserFollowing returns: " +result);
		System.out.println();
		return result;
	}

	public String renderUserFollowers(String username) throws SQLException{

		List<UserFollowersList> allFollowers = new ArrayList<UserFollowersList>();
		Statement stmt = null;
		String sql,result=null;
		System.out.println("\nUsername passed to renderUserFollowers is: "+username);
		stmt = conn.createStatement();
		sql="SELECT concat(fname,' ',lname) usrname, usr.twitterhandle,FB.FOLLOWEDBY " +
				"	FROM " +
				"  (SELECT uname FOLLOWEDBY  from followers where follows='"+username+"')FB, " +
				"   users usr where username=FB.FOLLOWEDBY; ";

		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String fullname = rs.getString(1);
				String twitterhandle = rs.getString(2);

				UserFollowersList follower = new UserFollowersList(fullname,twitterhandle);

				allFollowers.add(follower);
			}
			Gson gson = new Gson();
			String json = gson.toJson(allFollowers);
			System.out.println("Json string:"+json);
			result=json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("renderUserFollowers returns: " +result);
		System.out.println();
		return result;
	}

	public String getUserProfile(String username) throws SQLException{

		Statement stmt = null;
		String sql;
		JSONObject resultSet = new JSONObject();

		System.out.println("\nUsername passed to renderUserFollowers is: "+username);
		stmt = conn.createStatement();
		sql="SELECT concat(fname,' ',lname) usrname,usr.twitterhandle,usr.phone,usr.dob,usr.location, F.FOLLOWERS, T.FOLLOWING, TW.TWEETS" +
				"	FROM " +
				"	(SELECT count(*) FOLLOWERS 	from followers	where follows='"+username+"')F," +
				"	(SELECT count(*) FOLLOWING 	from followers	where uname='"+username+"')T," +
				"	(SELECT count(*) TWEETS	from tweets	where username='"+username+"')TW," +
				"	users usr where username='"+username+"'";

		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				resultSet.put("fullname", rs.getString(1));
				resultSet.put("twitterhandle", rs.getString(2));
				resultSet.put("phone", rs.getString(3));
				resultSet.put("dob", rs.getString(4));
				resultSet.put("location", rs.getString(5));
				resultSet.put("followerscount", rs.getString(6));
				resultSet.put("followingcount", rs.getString(7));
				resultSet.put("tweetscount", rs.getString(8));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("getUserProfile returns: " +resultSet.toJSONString());
		return resultSet.toJSONString();
	}
	
	
	public String loadSearchResults(String searchForTweet) throws SQLException{

		List<Tweets> allTweets = new ArrayList<Tweets>();
		
		Statement stmt = null;
		String sql,result=null;
		stmt = conn.createStatement();
		sql="SELECT concat(fname,' ',lname) usrname, usr.twitterhandle, t.tweet "
				+ " from tweets t, users usr "
				+ " where t.username=usr.username and t.tweet like '%"
				+ searchForTweet + "%';";

		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String tweet = rs.getString(3);
				String fullname = rs.getString(1);
				String twitterhandle = rs.getString(2);

				Tweets newTweet = new Tweets(tweet,fullname,twitterhandle);

				allTweets.add(newTweet);
			}
			Gson gson = new Gson();
			String json = gson.toJson(allTweets);
			System.out.println("Json string:"+json);
			result=json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("loadSearchResults returns: " +result);
		System.out.println();
		return result;
	}
	
	public String addUser(String username, String passwd, String fname, String lname, String thandle, Date dob, String phone, String loc) throws SQLException{

		Statement stmt = null;
		String sql,result=null;
		int rowcount = 0;
		System.out.println("\nUsername passed to addTweetToDB is: "+username);
		stmt = conn.createStatement();
		sql="INSERT INTO `twitter`.`users` (`username`,`password`,`fname`,`lname`,`twitterhandle`,`dob`,`phone`,`location`) VALUES ('"+username+"', SHA1('"+passwd+"'), '"+fname+"', '"+lname+"', '"+thandle+"', '"+dob+"', '"+phone+"', '"+loc+"')";

		try {
			rowcount=stmt.executeUpdate(sql);
			if(rowcount>0){
				result="true";
				System.out.println("Inserted succesfully");
			}
			else{
				result="false";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("addTweetToDB returns: " +result);
		return result;
	}
	
}