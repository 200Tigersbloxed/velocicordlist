package gay.tigers.velocicordlist.Databasing;

import java.util.Optional;

public interface IDatabase {
    boolean connect();
    Optional<String[]> GetWhitelistedUsers();
    void SetWhitelistedUsers(String[] users);
}
