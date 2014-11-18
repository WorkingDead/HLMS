package module.hibernate.customDialect;

import java.sql.Types;

public class SQLServerDialect extends org.hibernate.dialect.SQLServerDialect {

	//Not In Use
	//Temp using org.hibernate.dialect.SQLServer2008Dialect to replace
	//Don't know why org.hibernate.dialect.SQLServerDialect make the pagination not work.
	//It would make the jumping to certain page or clicking to next page not work.
	public SQLServerDialect () {
		
		//Actually, super() would be called by default before any statements under constructor method
		//Thererfore, super() is not necessary here but just a reminder here
		super();
		registerColumnType(Types.BOOLEAN, "tinyint");
	}
}
