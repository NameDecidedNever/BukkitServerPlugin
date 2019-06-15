package com.ndn.bukkitplugin.ndnserverplugin.datautils;

import java.sql.*;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.ndn.bukkitplugin.ndnutils.VerificationCodeGenerator;

public class DataManager {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/minecraft-data";

	// Database credentials
	static final String USER = "plugin";
	static final String PASS = "imthemcplugin";

	// Misc Config
	static double PLAYER_DEFAULT_BALANCE = 200.0;
	static double SERVER_DEFAULT_BALANCE = 500.0;
	static double ALL_DEFAULT_CREDIT = 100.0;
	static int STARTING_TOWN_RADIUS = 50;

	// SQL Statements
	static final String SQL_ADD_PLAYER = "INSERT INTO `players` (accountid, hashword, isverified, username, verificationcode) VALUES (?, ?, ?, ?, ?)";
	static final String SQL_ADD_ACCOUNT = "INSERT INTO `accounts` (balance, credit, name) VALUES (?, ?, ?)";
	static final String SQL_SELECT_CONSTANTS = "SELECT * FROM `constants`";
	static final String SQL_SELECT_ACCOUNT_BY_NAME = "SELECT * FROM `accounts` WHERE name = ?";
	static final String SQL_SELECT_PLAYER_BY_NAME = "SELECT * FROM `players` WHERE username = ?";
	static final String SQL_GET_BALANCE = "SELECT balance from `accounts` WHERE idaccounts = ?";
	static final String SQL_GET_ACCOUNT_NAME_BY_ID = "SELECT name from `accounts` WHERE idaccounts = ?";
	static final String SQL_UPDATE_BALANCE = "UPDATE `accounts` SET balance = ? WHERE idaccounts = ?";
	static final String SQL_INSERT_TRANSACTION = "INSERT INTO `transactions` (sender, reciever, amount, message, senderLabel, recieverLabel, time) VALUES (?, ?, ?, ?, ?, ?, ?)";
	static final String SQL_INSERT_ABOUT = "INSERT INTO `about` (currentPlayersOnline, maxPlayersOnline) VALUES (0, 0)";
	static final String SQL_UPDATE_ABOUT_PLAYERS = "UPDATE `about` SET currentPlayersOnline = ?";
	static final String SQL_UPDATE_ABOUT_PEAK_PLAYERS = "UPDATE `about` SET maxPlayersOnline = ?";
	static final String SQL_SELECT_ABOUT = "SELECT * FROM `about`";
	static final String SQL_ADD_TOWN = "INSERT INTO `towns` (name, dateFounded, ownerName, ownerAccountId, centerX, centerZ, radius, mobKillTaxPerc, chestShopTaxPerc, warpTaxPerc, auctionTaxPerc, shippingTaxPerc, dailyMemberTaxAmount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
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
	
	private int getLastPlayerPeak() {
		try {
			PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_ABOUT);
			ResultSet rs = preparedStmt.executeQuery();
			rs.next();
			return rs.getInt("maxPlayersOnline");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private boolean checkIfTableHasAnyRows(String tableName) {
		try {
			PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM `" + tableName + "`");
			ResultSet rs = preparedStmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addTown(String name, Player owner, int centerX, int centerZ) {
		try {
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(SQL_ADD_TOWN);
			preparedStmt.setString(1, name);
			preparedStmt.setInt(2, (int) Instant.now().getEpochSecond());
			preparedStmt.setString(3, owner.getName());
			preparedStmt.setInt(4, getPlayerPrimaryAccount(owner.getName()));
			preparedStmt.setInt(5, centerX);
			preparedStmt.setInt(6, centerZ);
			preparedStmt.setInt(7, ConstantManager.constants.get("TOWN_DEFAULT_RADIUS").intValue());
			preparedStmt.setDouble(8, 0);
			preparedStmt.setDouble(9, 0.1);
			preparedStmt.setDouble(10, 0.1);
			preparedStmt.setDouble(11, 0.1);
			preparedStmt.setDouble(12, 0.1);
			preparedStmt.setDouble(13, 0.1);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateMaxPlayersNumber(int newNum) {
		if (!checkIfTableHasAnyRows("about")) {
			createAbout();
		}
		try {
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(SQL_UPDATE_ABOUT_PEAK_PLAYERS);
			preparedStmt.setInt(1, newNum);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateCurrentPlayersNumber(int newNum) {
		if (!checkIfTableHasAnyRows("about")) {
			createAbout();
		}
		try {
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(SQL_UPDATE_ABOUT_PLAYERS);
			preparedStmt.setInt(1, newNum);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(getLastPlayerPeak() < newNum) {
			updateMaxPlayersNumber(newNum);
		}
	}

	private void createAbout() {
		try {
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(SQL_INSERT_ABOUT);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private int createServerPrimaryAccount() {
		try {

			String accountName = "server";

			PreparedStatement preparedStmt = conn.prepareStatement(SQL_ADD_ACCOUNT);
			preparedStmt.setDouble(1, SERVER_DEFAULT_BALANCE);
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

	public int getServerPrimaryAccount() {
		try {
			PreparedStatement preparedStmt2 = conn.prepareStatement(SQL_SELECT_ACCOUNT_BY_NAME);
			preparedStmt2.setString(1, "server");
			ResultSet account = preparedStmt2.executeQuery();
			if (account.next()) {
				return account.getInt("idaccounts");
			} else {
				return createServerPrimaryAccount();
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return -1;
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
				preparedStmt.setInt(7, (int) Instant.now().getEpochSecond());
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
			preparedStmt.setDouble(1, ConstantManager.constants.get("STARTING_MONEY_PER_PLAYER"));
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
	
	public HashMap<String, Double> getConstantsFromDB(){
		HashMap<String, Double> toReturn = new HashMap<String, Double>();
		try {
		PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_CONSTANTS);
		ResultSet rs = preparedStmt.executeQuery();
		while(rs.next()) {
			toReturn.put(rs.getString("name"), rs.getDouble("value"));
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return toReturn;
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
