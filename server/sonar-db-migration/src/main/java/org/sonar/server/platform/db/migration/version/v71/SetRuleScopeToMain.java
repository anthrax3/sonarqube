package org.sonar.server.platform.db.migration.version.v71;

import java.sql.SQLException;
import java.util.Date;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.System2;
import org.sonar.db.Database;
import org.sonar.server.platform.db.migration.step.DataChange;
import org.sonar.server.platform.db.migration.step.MassUpdate;

public class SetRuleScopeToMain extends DataChange {
  private final System2 system2;

  public SetRuleScopeToMain(Database db, System2 system2) {
    super(db);
    this.system2 = system2;
  }

  @Override
  protected void execute(Context context) throws SQLException {
    long now = system2.now();
    MassUpdate massUpdate = context.prepareMassUpdate();
    massUpdate.select("select ID from RULES where SCOPE is NULL");
    massUpdate.rowPluralName("rules");
    massUpdate.update("update RULES set SCOPE=?, UPDATED_AT=? where SCOPE is NULL");
    massUpdate.execute((row, update) -> {
      update.setString(1, RulesDefinition.Scope.MAIN.name());
      update.setLong(2, now);
      return true;
    });
  }
}
