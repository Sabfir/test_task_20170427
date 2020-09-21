package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.Client;
import java.util.List;

public interface ClientDao {

    List<Client> getAll();

    List<Client> getAllByCounterparty(Counterparty counterparty);

    Client getById(long id);

    Client save(Client client);

    void update(Client client);

    void delete(Client client);
}
