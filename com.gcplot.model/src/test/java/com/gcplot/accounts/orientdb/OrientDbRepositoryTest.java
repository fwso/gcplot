package com.gcplot.accounts.orientdb;

import com.gcplot.accounts.Account;
import com.gcplot.accounts.AccountImpl;
import com.gcplot.accounts.AccountRepository;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class OrientDbRepositoryTest {

    protected OrientDbConfig config;
    protected ODatabaseDocumentTx database;

    @Before
    public void setUp() throws Exception {
        int i = new Random().nextInt();
        database = new ODatabaseDocumentTx("memory:test" + i).create();
        config = new OrientDbConfig("memory:test" + i, "admin", "admin");
    }

    @After
    public void tearDown() throws Exception {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(config.connectionString).open(config.user, config.password);
        db.getMetadata().getSchema().dropClass("AccountImpl");
        db.getMetadata().getSchema().dropClass("Filter");
        db.drop();
    }

    @Test
    public void test() throws Exception {
        OrientDbRepository repository = new OrientDbRepository(config, new OPartitionedDatabasePoolFactory());
        repository.init();
        Assert.assertFalse(repository.account("token").isPresent());
        AccountImpl account = AccountImpl.createNew("abc", "artem@reveno.org", "token", "pass", "salt");
        account = (AccountImpl) repository.store(account);
        Assert.assertNotNull(account.getOId());
        Assert.assertTrue(repository.account("token").isPresent());
        Assert.assertFalse(repository.account("token").get().isConfirmed());
        Assert.assertTrue(repository.confirm("token", "salt"));
        Account account1 = repository.account("token").get();
        Assert.assertTrue(account1.isConfirmed());
        Assert.assertFalse(account1.isBlocked());

        Assert.assertEquals(repository.accounts().size(), 1);
        Assert.assertNotNull(repository.account(account1.id()).get());

        Assert.assertTrue(repository.account("abc", "pass", AccountRepository.LoginType.USERNAME).isPresent());

        repository.block("abc");
        account1 = repository.account("token").get();
        Assert.assertTrue(account1.isBlocked());

        repository.delete(account1);
        Assert.assertEquals(repository.accounts().size(), 0);
        repository.destroy();
    }

    @Test
    public void testFilters() throws Exception {
        OrientDbRepository repository = new OrientDbRepository(config, new OPartitionedDatabasePoolFactory());
        repository.init();
        Assert.assertEquals(0, repository.getAllFiltered("type1").size());
        repository.filter("type1", "value1");
        repository.filter("type1", "value2");
        Assert.assertEquals(2, repository.getAllFiltered("type1").size());
        Assert.assertArrayEquals(new String[] { "value1", "value2" }, repository.getAllFiltered("type1").toArray());
        repository.notFilter("type1", "value2");
        Assert.assertEquals(1, repository.getAllFiltered("type1").size());

        repository.destroy();
    }

}