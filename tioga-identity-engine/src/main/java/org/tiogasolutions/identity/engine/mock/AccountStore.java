package org.tiogasolutions.identity.engine.mock;

import org.tiogasolutions.dev.common.exceptions.ApiException;

import java.util.*;

import static java.lang.String.*;

public class AccountStore {

    private final List<Account> accounts = new ArrayList<>();

    public AccountStore() {
        accounts.add(new Account("01-a", "mickey.mouse@disney.com", "Mickey", "Mouse", "secret"));
        accounts.add(new Account("02-b", "minnie.mouse@disney.com", "Minnie", "Mouse", "secret"));
        accounts.add(new Account("03-c", "donald.duck@disney.com", "Donald", "Duck", "secret"));
        accounts.add(new Account("04-d", "daisy.duck@disney.com", "Daisy", "Duck", "secret"));
        accounts.add(new Account("05-e", "goofy@disney.com", "Goofy", null, "secret"));
        accounts.add(new Account("06-f", "pluto@disney.com", "Pluto", null, "secret"));

        accounts.add(new Account("07-g", "snow.white@disney.com", "Snow", "White", "secret"));
        accounts.add(new Account("08-h", "bashful@disney.com", "Bashful", null, "secret"));
        accounts.add(new Account("09-i", "doc@disney.com", "Doc", null, "secret"));
        accounts.add(new Account("10-j", "dopey@disney.com", "Dopey", null, "secret"));
        accounts.add(new Account("11-k", "happy@disney.com", "Happy", null, "secret"));
        accounts.add(new Account("12-l", "happy@disney.com", "Happy", null, "secret"));
        accounts.add(new Account("13-m", "sneezy@disney.com", "Sneezy", null, "secret"));
        accounts.add(new Account("14-n", "grumpy@disney.com", "Grumpy", null, "secret"));
    }

    public Account findByEmail(String email) {
        for (Account account : accounts) {
            if (email.equals(account.getEmail())) {
                return account;
            }
        }
        throw ApiException.notFound(format("No account for %s", email));
    }

    public Account findById(String accountId) {
        for (Account account : accounts) {
            if (accountId.equals(account.getId())) {
                return account;
            }
        }
        throw ApiException.notFound(format("No account for %s", accountId));
    }

    public List<Account> getAll(int index, int pageSize) {

        List<Account> list = new ArrayList<>();
        int max = Math.min(index+pageSize, accounts.size());

        for (int i = index; i < max; i++) {
            list.add(accounts.get(i));
        }

        return list;
    }

    public int countAll() {
        return accounts.size();
    }
}
