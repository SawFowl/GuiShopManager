package sawfowl.guishopmanager.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import sawfowl.localeapi.api.serializetools.SerializeOptions;

public interface DBStorage extends DataStorage {

	Connection getConnection() throws SQLException ;

	Format getFormat();

	default Statement createStatement() throws SQLException {
		return getConnection().createStatement();
	}

	default ConfigurationLoader<? extends ConfigurationNode> createLoader(StringWriter sink) {
		switch (getFormat()) {
			case HOCON: 
				return SerializeOptions.createHoconConfigurationLoader(2).sink(() -> new BufferedWriter(sink)).build();
			case YAML: 
				return SerializeOptions.createYamlConfigurationLoader(2).sink(() -> new BufferedWriter(sink)).build();
			default:
				return SerializeOptions.createJsonConfigurationLoader(2).sink(() -> new BufferedWriter(sink)).build();
		}
	}

	default ConfigurationNode createNode(String data) throws ConfigurateException {
		switch (getFormat()) {
			case HOCON: 
				return SerializeOptions.createHoconConfigurationLoader(2).source(() -> new BufferedReader(new StringReader(data))).build().load();
			case YAML: 
				return SerializeOptions.createYamlConfigurationLoader(2).source(() -> new BufferedReader(new StringReader(data))).build().load();
			default:
				return SerializeOptions.createJsonConfigurationLoader(2).source(() -> new BufferedReader(new StringReader(data))).build().load();
		}
	}

	public static enum Format {

		HOCON,
		YAML,
		JSON;

		public static Format find(String name) {
			return Stream.of(Format.values()).filter(f -> f.name().equalsIgnoreCase(name)).findFirst().orElse(JSON);
		}

	}

}
