package com.qprogramming.tasq.support.sorters;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.test.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.qprogramming.tasq.test.TestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountsSorterTest {

    private List<Account> accountsList;

    @Before
    public void setUp() {
        accountsList = TestUtils.createAccountList();
    }

    @Test
    public void sortAccountsTest() {
        Collections.sort(accountsList, new AccountSorter(AccountSorter.SORTBY.NAME, true));
        Assert.assertEquals(MARRY, accountsList.get(0).getName());
    }

    @Test
    public void sortAccountsSurnameTest() {
        Collections.sort(accountsList, new AccountSorter(AccountSorter.SORTBY.SURNAME, false));
        Assert.assertEquals(ART, accountsList.get(0).getSurname());
    }

    @Test
    public void sortAccountsEmailTest() {
        Collections.sort(accountsList, new AccountSorter(AccountSorter.SORTBY.EMAIL, true));
        Assert.assertEquals(LAMB, accountsList.get(0).getSurname());
    }
}
