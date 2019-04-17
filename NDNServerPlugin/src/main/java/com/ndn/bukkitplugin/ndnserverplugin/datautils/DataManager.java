package com.ndn.bukkitplugin.ndnserverplugin.datautils;

import java.sql.*;

import com.ndn.bukkitplugin.ndnutils.VerificationCodeGenerator;

public class DataManager {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/minecraft-data";

	// Database credentials
	static final String USER = "plugin";
	static final String PASS = "imthemcplugin";

	// SQL Statements
	static final String SQL_ADD_PLAYER = "INSERT INTO `players` (accountid, hashword, isverified, username, verificationcode) VALUES (?, ?, ?, ?, ?)";
	static final String SQL_ADD_ACCOUNT = "INSERT INTO `accounts` (balance, credit, name) VALUES (?, ?, ?)";
	static final String SQL_SELECT_ACCOUNT_BY_NAME = "SELECT * FROM `accounts` WHERE name = ?";
	static final String SQL_SELECT_PLAYER_BY_NAME = "SELECT * FROM `players` WHERE username = ?";
	static final String SQL_GET_BALANCE = "SELECT balance from `accounts` WHERE idaccounts = ?";
	static final String SQL_GET_ACCOUNT_NAME_BY_ID = "SELECT name from `accounts` WHERE idaccounts = ?";
	static final String SQL_UPDATE_BALANCE = "UPDATE `accounts` SET balance = ? WHERE idaccounts = ?";
	static final String SQL_INSERT_TRANSACTION = "INSERT INTO `transactions` (sender, reciever, amount, message, senderLabel, recieverLabel) VALUES (?, ?, ?, ?, ?, ?)";

	Connection conn = null;

	private static DataManager instance = null;

	public DataManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}
	
	public boolean doesAccountExist(int id) {
		PreparedStatement preparedStmt;
		try {
		preparedStmt = conn.prepareStatement(SQL_GET_ACCOUNT_NAME_BY_ID);
		preparedStmt.setInt(1, id);
		ResultSet rs = preparedStmt.executeQuery();
		return rs.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getPlayerPrimaryAccount(String username) {

		PreparedStatement preparedStmt;
		try {
			preparedStmt = conn.prepareStatement(SQL_SELECT_PLAYER_BY_NAME);
			preparedStmt.setString(1, username);
			ResultSet rs = preparedStmt.executeQuery();
			
			rs.next();
			
			return rs.getInt("accountid");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public double getPlayerBalance(String username) {
		return getBalance(getPlayerPrimaryAccount(username));
	}

	public double getBalance(int account) {
		PreparedStatement preparedStmt;
		try {
			preparedStmt = conn.prepareStatement(SQL_GET_BALANCE);
			preparedStmt.setInt(1, account);
			ResultSet rs = preparedStmt.executeQuery();
			
			rs.next();

			return rs.getDouble("balance");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public void setBalance(int account, double newBalance) {
		PreparedStatement preparedStmt;
		try {
			preparedStmt = conn.prepareStatement(SQL_UPDATE_BALANCE);
			preparedStmt.setDouble(1, newBalance);
			preparedStmt.setInt(2, account);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getAccountName(int id) {
		PreparedStatement preparedStmt;
		try {
		preparedStmt = conn.prepareStatement(SQL_GET_ACCOUNT_NAME_BY_ID);
		preparedStmt.setInt(1, id);
		ResultSet rs = preparedStmt.executeQuery();
		rs.next();
		return rs.getString("name");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Un-named Account";
	}

	public boolean makePayExchange(int from, int to, double ammount, String message) {
		ammount = Math.abs(ammount);
		if (getBalance(from) < ammount) {
			return false;
		} else {
			setBalance(from, getBalance(from) - ammount);
			setBalance(to, getBalance(to) + ammount);
			try {
				PreparedStatement preparedStmt;
				preparedStmt = conn.prepareStatement(SQL_INSERT_TRANSACTION);
				preparedStmt.setInt(1, from);
				preparedStmt.setInt(2, to);
				preparedStmt.setDouble(3, ammount);
				preparedStmt.setString(4, message);
				preparedStmt.setString(5, getAccountName(from));
				preparedStmt.setString(6, getAccountName(to));
				preparedStmt.execute();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public int createPersonalAccount(String username) {
		try {

			String accountName = username + "'s Account";

			PreparedStatement preparedStmt = conn.prepareStatement(SQL_ADD_ACCOUNT);
			preparedStmt.setDouble(1, 0.0);
			preparedStmt.setInt(2, 0);
			preparedStmt.setString(3, accountName);
			preparedStmt.executeUpdate();

			PreparedStatement preparedStmt2 = conn.prepareStatement(SQL_SELECT_ACCOUNT_BY_NAME);
			preparedStmt2.setString(1, accountName);
			ResultSet account = preparedStmt2.executeQuery();

			account.next();

			return account.getInt("idaccounts");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void addPlayerIfNotExists(String username) {
		try {

			Statement stmt = conn.createStatement();

			String sql = "SELECT username FROM players";
			ResultSet rs = stmt.executeQuery(sql);

			boolean playerExists = false;

			while (rs.next()) {
				if (rs.getString("username").equals(username)) {
					playerExists = true;
				}
			}

			rs.close();

			if (!playerExists) {
				int accountId = createPersonalAccount(username);
				String verificationcode = VerificationCodeGenerator.generate();
				PreparedStatement preparedStmt = conn.prepareStatement(SQL_ADD_PLAYER);
				preparedStmt.setInt(1, accountId);
				preparedStmt.setString(2, "");
				preparedStmt.setInt(3, 0);
				preparedStmt.setString(4, username);
				preparedStmt.setString(5, verificationcode);
				preparedStmt.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean checkPlayerIsVerified(String username) {

		try {

			PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLAYER_BY_NAME);
			preparedStmt.setString(1, username);
			ResultSet rs = preparedStmt.executeQuery();

			rs.next();

			if (rs.getInt("isverified") == 1) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public String getPlayerVerificationCode(String username) {

		try {

			PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLAYER_BY_NAME);
			preparedStmt.setString(1, username);
			ResultSet rs = preparedStmt.executeQuery();

			rs.next();

			return rs.getString("verificationcode");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "No Verification Code";
	}

}
