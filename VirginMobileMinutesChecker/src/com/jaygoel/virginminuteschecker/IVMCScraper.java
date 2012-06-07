package com.jaygoel.virginminuteschecker;

import java.util.Calendar;

/**
 * Interface for VirginMobileChecker's Website Scraper
 */
public interface IVMCScraper {
    boolean isValid();
    String getPhoneNumber();
    float getMonthlyCharge();
    float getCurrentBalance();
    Calendar getChargedOn();
    Calendar getNewMonthStarts();
    int getMinutesUsed();
    int getMinutesTotal();
    int getDataUsed();
    int getDataTotal();
}