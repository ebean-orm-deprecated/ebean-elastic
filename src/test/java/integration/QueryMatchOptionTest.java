package integration;

import io.ebean.Query;
import io.ebean.search.Match;
import org.example.domain.Customer;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class QueryMatchOptionTest extends BaseTest {

  @Test
  public void matchOr() {

    Query<Customer> query = server.find(Customer.class)
        .text()
        .match("name", "Cust NoAddress", new Match().opOr())
        .query();

    List<Customer> list = query.findList();
    assertThat(list).hasSize(1);
    assertEquals(query.getGeneratedSql(), "{\"query\":{\"match\":{\"name\":{\"query\":\"Cust NoAddress\"}}}}");
  }

  @Test
  public void matchAnd() {

    Query<Customer> query = server.find(Customer.class)
        .text()
        .match("name", "Cust NoAddress", new Match().opAnd())
        .query();

    List<Customer> list = query.findList();
    assertThat(list).hasSize(1);
    assertEquals(query.getGeneratedSql(), "{\"query\":{\"match\":{\"name\":{\"query\":\"Cust NoAddress\",\"operator\":\"and\"}}}}");
  }


  @Test
  public void matchAnd_when_doesNotExist_expect_noHits() {

    Query<Customer> query = server.find(Customer.class)
        .text()
        .match("name", "Cust DoesNotExist", new Match().opAnd())
        .query();

    List<Customer> list = query.findList();
    assertThat(list).hasSize(0);
  }

  @Test
  public void matchAllPhraseOptions() {

    Match options = new Match().opAnd().phrase()
        .analyzer("whitespace")
        .boost(2)
        .cutoffFrequency(1)
        .minShouldMatch("50%")
        .zeroTerms("all")
        .maxExpansions(3) // maxExpansions is for phrasePrefix only
        .phrase();

    Query<Customer> query = server.find(Customer.class)
        .text()
        .match("name", "Cust DoesNotExist", options)
        .query();

    List<Customer> list = query.findList();
    assertEquals(query.getGeneratedSql(), "{\"query\":{\"match\":{\"name\":{\"query\":\"Cust DoesNotExist\",\"operator\":\"and\",\"boost\":2.0,\"cutoff_frequency\":1.0,\"minimum_should_match\":\"50%\",\"zero_terms_query\":\"all\",\"analyzer\":\"whitespace\",\"type\":\"phrase\"}}}}");
    assertThat(list).hasSize(0);
  }

  @Test
  public void matchAllPhrasePrefixOptions() {

    Match options = new Match().opAnd().phrase()
        .analyzer("whitespace")
        .boost(2)
        .cutoffFrequency(1)
        .minShouldMatch("50%")
        .maxExpansions(3)
        .phrasePrefix();

    Query<Customer> query = server.find(Customer.class)
        .text()
        .match("name", "Cust NoAdd", options)
        .query();

    List<Customer> list = query.findList();
    assertEquals(query.getGeneratedSql(), "{\"query\":{\"match\":{\"name\":{\"query\":\"Cust NoAdd\",\"operator\":\"and\",\"boost\":2.0,\"cutoff_frequency\":1.0,\"minimum_should_match\":\"50%\",\"analyzer\":\"whitespace\",\"type\":\"phrase_prefix\",\"max_expansions\":3}}}}");
    assertThat(list).hasSize(0);
  }

  @Test
  public void matchPhrasePrefix() {

    Match options = new Match().opAnd().phrase()
        .phrasePrefix();

    Query<Customer> query = server.find(Customer.class)
        .text()
        .match("name", "Cust NoAdd", options)
        .query();

    List<Customer> list = query.findList();
    assertEquals(query.getGeneratedSql(), "{\"query\":{\"match\":{\"name\":{\"query\":\"Cust NoAdd\",\"operator\":\"and\",\"type\":\"phrase_prefix\"}}}}");
    assertThat(list).hasSize(1);
  }
}
