package com.ndn.bukkitplugin.ndnserverplugin.datautils;

import java.awt.Rectangle;

import java.sql.*;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
    static double SERVER_DEFAULT_BALANCE = 4000.0;
    static double ALL_DEFAULT_CREDIT = 100.0;
    static int STARTING_TOWN_RADIUS = 50;

    // SQL Statements
    static final String SQL_ADD_PLAYER = "INSERT INTO `players` (accountid, hashword, isverified, username, verificationcode) VALUES (?, ?, ?, ?, ?)";
    static final String SQL_ADD_PLOT = "INSERT INTO `plots` (name, x, z, length, width, type, pricePerDay, townid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_ADD_ACCOUNT = "INSERT INTO `accounts` (balance, credit, name) VALUES (?, ?, ?)";
    static final String SQL_SELECT_EXPENSES = "SELECT * FROM `expenses`";
    static final String SQL_SELECT_PLOT_BY_AREA = "SELECT * FROM `plots` WHERE ? > x AND ? < x + width AND ? > z AND ? < z + length";
    static final String SQL_SELECT_PLOT_BY_PLAYER_RESIDENTIAL = "SELECT * FROM `plots` WHERE renterid = ? AND type = 1";
    static final String SQL_SELECT_TOWN_BY_OWNER = "SELECT * FROM `towns` WHERE ownerName = ?";
    static final String SQL_SELECT_TOWN_BY_AREA = "SELECT * FROM `towns` WHERE ? > (centerX - radius) AND ? < (centerX + radius) AND ? > (centerZ - radius) AND ? < (centerZ + radius)";
    static final String SQL_SELECT_TOWN_BY_ID = "SELECT * FROM `towns` WHERE idtowns = ?";
    static final String SQL_SELECT_TOWN_BY_NAME = "SELECT * FROM `towns` WHERE name = ?";
    static final String SQL_SELECT_ALL_TOWNS = "SELECT * FROM `towns`";
    static final String SQL_UPDATE_TOWN_WARP = "UPDATE `towns` SET warpLocationX = ?, warpLocationY = ?, warpLocationZ = ? WHERE idtowns = ?";
    static final String SQL_SELECT_CONSTANTS = "SELECT * FROM `constants`";
    static final String SQL_SELECT_ACCOUNT_BY_NAME = "SELECT * FROM `accounts` WHERE name = ?";
    static final String SQL_SELECT_PLAYER_BY_NAME = "SELECT * FROM `players` WHERE username = ?";
    static final String SQL_SELECT_PLAYER_BY_ID = "SELECT * FROM `players` WHERE idplayers = ?";
    static final String SQL_GET_BALANCE = "SELECT balance from `accounts` WHERE idaccounts = ?";
    static final String SQL_GET_ACCOUNT_NAME_BY_ID = "SELECT name from `accounts` WHERE idaccounts = ?";
    static final String SQL_UPDATE_BALANCE = "UPDATE `accounts` SET balance = ? WHERE idaccounts = ?";
    static final String SQL_INSERT_TRANSACTION = "INSERT INTO `transactions` (sender, reciever, amount, message, senderLabel, recieverLabel, time) VALUES (?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_INSERT_ABOUT = "INSERT INTO `about` (currentPlayersOnline, maxPlayersOnline) VALUES (0, 0)";
    static final String SQL_UPDATE_ABOUT_PLAYERS = "UPDATE `about` SET currentPlayersOnline = ?";
    static final String SQL_UPDATE_ABOUT_PEAK_PLAYERS = "UPDATE `about` SET maxPlayersOnline = ?";
    static final String SQL_SELECT_ABOUT = "SELECT * FROM `about`";
    static final String SQL_ADD_TOWN = "INSERT INTO `towns` (name, dateFounded, ownerName, ownerAccountId, centerX, centerZ, radius, mobKillTaxPerc, chestShopTaxPerc, warpTaxPerc, auctionTaxPerc, shippingTaxPerc, dailyMemberTaxAmount, warpLocationX, warpLocationY, warpLocationZ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_GET_TOWN_BY_OWNER_NAME = "SELECT * FROM `towns` WHERE ownerName = ?";

    Connection conn = null;
    long secondsAtConnectionStart = 0L;
    private static DataManager instance = null;

    private void handleSqlException(SQLException e) {
	if (e.getMessage().contains("The last packet successfully received from the server was")) {
	    try {
		conn.close();
	    } catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }
	    instance = null;
	    conn = null;
	    secondsAtConnectionStart = 0L;
	    e.printStackTrace();
	    Bukkit.getServer().broadcastMessage(ChatColor.BOLD + "Database connection reset. If you just ran a command and it didn't work, try again now!");
	} else {
	    Bukkit.getServer().broadcastMessage(ChatColor.BOLD + "Unknown database error. Please record what you were doing and tell Matthew what the hell happened.");
	}

    }

    public DataManager() {
	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    System.out.println("Connecting to a selected database...");
	    conn = DriverManager.getConnection(DB_URL, USER, PASS);
	    secondsAtConnectionStart = Instant.now().getEpochSecond();
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

    public long getConnectionLifetime() { return (Instant.now().getEpochSecond() - secondsAtConnectionStart); }

    private int getLastPlayerPeak() {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_ABOUT);
	    ResultSet rs = preparedStmt.executeQuery();
	    rs.next();
	    return rs.getInt("maxPlayersOnline");
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return 0;
    }

    private boolean checkIfTableHasAnyRows(String tableName) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM `" + tableName + "`");
	    ResultSet rs = preparedStmt.executeQuery();
	    return rs.next();
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return false;
    }

    public String getPlayerNameFromId(int id) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLAYER_BY_ID);
	    preparedStmt.setInt(1, id);
	    ResultSet rs = preparedStmt.executeQuery();
	    if (rs.next()) { return rs.getString("username"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return "";
    }

    public void executeDailyExpenses() {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_EXPENSES);
	    ResultSet rs = preparedStmt.executeQuery();
	    while (rs.next()) {
		DataManager.getInstance().makePayExchange(rs.getInt("sender"), rs.getInt("reciever"), rs.getDouble("amount"), rs.getString("message"));
	    }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
    }

    public int getPlayerIdFromName(String name) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLAYER_BY_NAME);
	    preparedStmt.setString(1, name);
	    ResultSet player = preparedStmt.executeQuery();

	    if (player.next()) { return player.getInt("idplayers"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return -1;
    }

    public int getTownIdFromOwnerName(String name) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_GET_TOWN_BY_OWNER_NAME);
	    preparedStmt.setString(1, name);
	    ResultSet town = preparedStmt.executeQuery();

	    if (town.next()) { return town.getInt("idtowns"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return -1;
    }

    public String getTownOwnerName(int townid) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_ID);
	    preparedStmt.setInt(1, townid);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) { return town.getString("ownerName"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return "";
    }

    public double getTownMobTax(int id) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_ID);
	    preparedStmt.setInt(1, id);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) { return town.getDouble("mobKillTaxPerc"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return 0.0;
    }

    public double getTownShopTax(int id) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_ID);
	    preparedStmt.setInt(1, id);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) { return town.getDouble("chestShopTaxPerc"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return 0.0;
    }

    public double getTownWarpTax(int id) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_ID);
	    preparedStmt.setInt(1, id);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) { return town.getDouble("warpTaxPerc"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return 0.0;
    }

    public int getTownIdFromName(String name) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_NAME);
	    preparedStmt.setString(1, name);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) { return town.getInt("idtowns"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return -1;
    }

    public void setTownWarpLocation(Location loc, int townid) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_UPDATE_TOWN_WARP);
	    preparedStmt.setInt(1, loc.getBlockX());
	    preparedStmt.setInt(2, loc.getBlockY());
	    preparedStmt.setInt(3, loc.getBlockZ());
	    preparedStmt.setInt(4, townid);
	    preparedStmt.executeUpdate();
	} catch (SQLException e) {
	    handleSqlException(e);
	}
    }

    public Location getTownWarpLocation(int id) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_ID);
	    preparedStmt.setInt(1, id);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) { return new Location(Bukkit.getWorld("world"), (double) town.getInt("warpLocationX"), (double) town.getInt("warpLocationY"), (double) town.getInt("warpLocationZ")); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return null;
    }

    public int getTownByArea(int x, int z) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_AREA);
	    preparedStmt.setInt(1, x);
	    preparedStmt.setInt(2, x);
	    preparedStmt.setInt(3, z);
	    preparedStmt.setInt(4, z);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) { return town.getInt("idtowns"); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return -1;
    }
    // -1 = No plot can edit
    // 0 = Can't edit
    // 1 = Can Edit Residential
    // 2 = Can Edit Market

    public Rectangle getPlotBoundary(int x, int z) {

	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLOT_BY_AREA);
	    preparedStmt.setInt(1, x);
	    preparedStmt.setInt(2, x);
	    preparedStmt.setInt(3, z);
	    preparedStmt.setInt(4, z);
	    ResultSet plot = preparedStmt.executeQuery();
	    if (plot.next()) { return new Rectangle(plot.getInt("x"), plot.getInt("z"), plot.getInt("width"), plot.getInt("length")); }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	return null;
    }

    public int getPlotType(int x, int z) {
	int townId = -1;
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_AREA);
	    preparedStmt.setInt(1, x);
	    preparedStmt.setInt(2, x);
	    preparedStmt.setInt(3, z);
	    preparedStmt.setInt(4, z);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) {
		townId = town.getInt("idtowns");
	    }
	} catch (SQLException e) {
	    handleSqlException(e);
	}
	if (townId != -1) {
	    try {
		PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLOT_BY_AREA);
		preparedStmt.setInt(1, x);
		preparedStmt.setInt(2, x);
		preparedStmt.setInt(3, z);
		preparedStmt.setInt(4, z);
		ResultSet plot = preparedStmt.executeQuery();
		if (plot.next()) { return plot.getInt("type"); }
	    } catch (SQLException e) {
		handleSqlException(e);
	    }
	}
	return -1;
    }

    public int getPlotEditableCode(int x, int z, String username) {
	int townId = -1;
	boolean isTownOwner = false;
	boolean residentsCanEdit = false;
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_AREA);
	    preparedStmt.setInt(1, x);
	    preparedStmt.setInt(2, x);
	    preparedStmt.setInt(3, z);
	    preparedStmt.setInt(4, z);
	    ResultSet town = preparedStmt.executeQuery();
	    if (town.next()) {
		residentsCanEdit = town.getInt("allowResidentsToEditTown") == 1;
		townId = town.getInt("idtowns");
		if (town.getString("ownerName").equals(username)) {
		    isTownOwner = true;
		}
	    }
	} catch (SQLException e) {
	    handleSqlException(e);

	}
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLOT_BY_AREA);
	    preparedStmt.setInt(1, x);
	    preparedStmt.setInt(2, x);
	    preparedStmt.setInt(3, z);
	    preparedStmt.setInt(4, z);
	    ResultSet plot = preparedStmt.executeQuery();
	    if (plot.next()) {
		if (plot.getInt("renterid") == (DataManager.getInstance().getPlayerIdFromName(username))) {
		    return plot.getInt("type");
		} else {
		    return 0;
		}
	    } else if (townId != -1) {
		if (isTownOwner || (residentsCanEdit && (DataManager.getInstance().getPlayerTownAffiliation(username) == townId))) {
		    return -1;
		} else {
		    return 0;
		}
	    }
	} catch (SQLException e) {
	    handleSqlException(e);

	}
	return -1;
    }

    public int getPlayerTownAffiliation(String name) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLOT_BY_PLAYER_RESIDENTIAL);
	    preparedStmt.setInt(1, DataManager.getInstance().getPlayerIdFromName(name));
	    ResultSet plot = preparedStmt.executeQuery();
	    if (plot.next()) { return plot.getInt("townid"); }
	} catch (SQLException e) {
	    handleSqlException(e);

	}
	return -1;
    }

    public boolean checkIfTownCanBeFounded(int x, int z) {
	boolean canBeFounded = true;
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_ALL_TOWNS);
	    ResultSet towns = preparedStmt.executeQuery();
	    while (towns.next()) {
		int centerX = towns.getInt("centerX");
		int centerZ = towns.getInt("centerZ");
		int radius = towns.getInt("radius");
		int minDistance = ConstantManager.constants.get("MIN_TOWN_DISTANCE").intValue();
		int foundingRadius = ConstantManager.constants.get("TOWN_DEFAULT_RADIUS").intValue();
		Rectangle townToBeCreated = new Rectangle(x - foundingRadius, z - foundingRadius, foundingRadius * 2, foundingRadius * 2);
		Rectangle townToBeCheckedBound = new Rectangle(centerX - radius - minDistance, centerZ - radius - minDistance, (radius + minDistance) * 2, (radius + minDistance) * 2);
		if (townToBeCreated.intersects(townToBeCheckedBound)) {
		    canBeFounded = false;
		}
	    }
	} catch (SQLException e) {
	    handleSqlException(e);
	    canBeFounded = false;
	}
	return canBeFounded;
    }

    public boolean checkIfPlayerHasOwnTown(String username) {
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_TOWN_BY_OWNER);
	    preparedStmt.setString(1, username);
	    ResultSet towns = preparedStmt.executeQuery();
	    return towns.next();
	} catch (SQLException e) {
	    handleSqlException(e);

	}
	return false;
    }

    public void addTown(String name, Player owner, int centerX, int centerZ, int yForDefaultWarp) {
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
	    preparedStmt.setInt(14, centerX);
	    preparedStmt.setInt(15, yForDefaultWarp);
	    preparedStmt.setInt(16, centerZ);
	    preparedStmt.execute();
	} catch (SQLException e) {
	    handleSqlException(e);
	}
    }

    public void addPlot(String plotName, int x, int z, int width, int length, int type, double pricePerDay, int townid) {
	try {
	    PreparedStatement preparedStmt;
	    preparedStmt = conn.prepareStatement(SQL_ADD_PLOT);
	    preparedStmt.setString(1, plotName);
	    preparedStmt.setInt(2, x);
	    preparedStmt.setInt(3, z);
	    preparedStmt.setInt(4, length);
	    preparedStmt.setInt(5, width);
	    preparedStmt.setInt(6, type);
	    preparedStmt.setDouble(7, pricePerDay);
	    preparedStmt.setInt(8, townid);
	    preparedStmt.execute();
	} catch (SQLException e) {
	    handleSqlException(e);
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
	    handleSqlException(e);
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
	    handleSqlException(e);
	}
	if (getLastPlayerPeak() < newNum) {
	    updateMaxPlayersNumber(newNum);
	}
    }

    private void createAbout() {
	try {
	    PreparedStatement preparedStmt;
	    preparedStmt = conn.prepareStatement(SQL_INSERT_ABOUT);
	    preparedStmt.execute();
	} catch (SQLException e) {
	    handleSqlException(e);
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
	    handleSqlException(e);

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
	    handleSqlException(e);

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
	    handleSqlException(e);
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
	    handleSqlException(e);
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
	    handleSqlException(e);
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
	    handleSqlException(e);
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
		handleSqlException(e);
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
	    handleSqlException(e);
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
	    handleSqlException(e);
	}
    }

    public boolean checkPlayerIsVerified(String username) {

	try {

	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_PLAYER_BY_NAME);
	    preparedStmt.setString(1, username);
	    ResultSet rs = preparedStmt.executeQuery();

	    rs.next();

	    if (rs.getInt("isverified") == 1) { return true; }

	} catch (SQLException e) {
	    handleSqlException(e);
	}

	return false;
    }

    public HashMap<String, Double> getConstantsFromDB() {
	HashMap<String, Double> toReturn = new HashMap<String, Double>();
	try {
	    PreparedStatement preparedStmt = conn.prepareStatement(SQL_SELECT_CONSTANTS);
	    ResultSet rs = preparedStmt.executeQuery();
	    while (rs.next()) {
		toReturn.put(rs.getString("name"), rs.getDouble("value"));
	    }
	} catch (SQLException e) {
	    handleSqlException(e);
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
	    handleSqlException(e);
	}

	return "No Verification Code";
    }

}
