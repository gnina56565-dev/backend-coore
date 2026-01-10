package ru.mentee.power.crm.storage;

import ru.mentee.power.crm.domain.Lead;

public class LeadStorage {
    private Lead[] leads = new Lead[100];

    public boolean add(Lead lead) {

        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null && leads[i].email().equals((lead.email()))) {
                return false;
            }
        }

        for (int i = 0; i < leads.length; i++) {
            if (leads[i] == null) {
                leads[i] = lead;
                return true;
            }
        }
        throw new IllegalStateException("Storage is full, cannot add more leads");
    }

    public Lead[] findAll() {

        int count = 0;
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                count++;
            }
        }

        Lead[] result = new Lead[count];

        int resultIndex = 0;
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                result[resultIndex++] = leads[i];
            }
        }
        return result;
    }

    public int size() {
        int count = 0;
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                count++;
            }
        }
        return count;
    }
}
