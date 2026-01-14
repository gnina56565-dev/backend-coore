
package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LeadEqualsHashCodeTest {

    @Test
    void shouldBeReflexive_whenEqualsCalledOnSameObject() {
        Lead lead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        assertThat(lead).isEqualTo(lead);
    }

    @Test
    void shouldBeSymmetric_whenEqualsCalledOnTwoObjects() {
        Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(firstLead);
    }

    @Test
    void shouldBeTransitive_whenEqualsChainOfThreeObjects() {
        Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead thirdLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(thirdLead);
        assertThat(firstLead).isEqualTo(thirdLead);
    }

    @Test
    void shouldBeConsistent_whenEqualsCalledMultipleTimes() {
        Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
    }

    @Test
    void shouldReturnFalse_whenEqualsComparedWithNull() {
        Lead lead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        assertThat(lead).isNotEqualTo(null);
    }

    @Test
    void shouldHaveSameHashCode_whenObjectsAreEqual() {
        Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
    }

    @Test
    void shouldWorkInHashMap_whenLeadUsedAsKey() {
        Lead keyLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead lookupLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        Map<Lead, String> map = new HashMap<>();
        map.put(keyLead, "CONTACTED");

        String status = map.get(lookupLead);
        assertThat(status).isEqualTo("CONTACTED");
    }

    @Test
    void shouldNotBeEqual_whenIdsAreDifferent() {
        Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead differentLead = new Lead("2", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        assertThat(firstLead).isNotEqualTo(differentLead);

    }
}

//package ru.mentee.power.crm.domain;
//
//import org.junit.jupiter.api.Test;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import static org.assertj.core.api.Assertions.assertThat;
//
//class LeadEqualsHashCodeTest {
//
//    @Test
//    void shouldBeReflexive_whenEqualsCalledOnSameObject() {
//        UUID id = UUID.randomUUID();
//        Lead lead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        assertThat(lead).isEqualTo(lead);
//    }
//
//    @Test
//    void shouldBeSymmetric_whenEqualsCalledOnTwoObjects() {
//        UUID id = UUID.randomUUID();
//        Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//
//        assertThat(firstLead).isEqualTo(secondLead);
//        assertThat(secondLead).isEqualTo(firstLead);
//    }
//
//    @Test
//    void shouldBeTransitive_whenEqualsChainOfThreeObjects() {
//        UUID id = UUID.randomUUID();
//        Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead thirdLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//
//        assertThat(firstLead).isEqualTo(secondLead);
//        assertThat(secondLead).isEqualTo(thirdLead);
//        assertThat(firstLead).isEqualTo(thirdLead);
//    }
//
//    @Test
//    void shouldBeConsistent_whenEqualsCalledMultipleTimes() {
//        UUID id = UUID.randomUUID();
//        Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//
//        assertThat(firstLead).isEqualTo(secondLead);
//        assertThat(firstLead).isEqualTo(secondLead);
//        assertThat(firstLead).isEqualTo(secondLead);
//    }
//
//    @Test
//    void shouldReturnFalse_whenEqualsComparedWithNull() {
//        UUID id = UUID.randomUUID();
//        Lead lead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        assertThat(lead).isNotEqualTo(null);
//    }
//
//    @Test
//    void shouldHaveSameHashCode_whenObjectsAreEqual() {
//        UUID id = UUID.randomUUID();
//        Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//
//        assertThat(firstLead).isEqualTo(secondLead);
//        assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
//    }
//
//    @Test
//    void shouldWorkInHashMap_whenLeadUsedAsKey() {
//        UUID id = UUID.randomUUID();
//        Lead keyLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead lookupLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//
//        Map<Lead, String> map = new HashMap<>();
//        map.put(keyLead, "CONTACTED");
//
//        String status = map.get(lookupLead);
//        assertThat(status).isEqualTo("CONTACTED");
//    }
//
//    @Test
//    void shouldNotBeEqual_whenIdsAreDifferent() {
//        Lead firstLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead differentLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        assertThat(firstLead).isNotEqualTo(differentLead);
//    }
//}

