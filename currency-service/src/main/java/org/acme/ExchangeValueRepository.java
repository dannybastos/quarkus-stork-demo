package org.acme;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ExchangeValueRepository implements PanacheRepository<ExchangeValue> {
    public Optional<ExchangeValue> findFromAndTo(final String from, final String to) {
        return find("from ExchangeValue e where e.from=?1 and e.to=?2", from , to).firstResultOptional();
    }
}
