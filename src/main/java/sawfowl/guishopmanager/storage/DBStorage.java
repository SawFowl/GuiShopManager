package sawfowl.guishopmanager.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import sawfowl.localeapi.api.ConfigTypes;

public interface DBStorage extends DataStorage {

	Connection getConnection() throws SQLException ;

	ConfigTypes getFormat();

	default Statement createStatement() throws SQLException {
		return getConnection().createStatement();
	}

}
