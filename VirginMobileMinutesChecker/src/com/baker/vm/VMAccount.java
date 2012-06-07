package com.baker.vm;

import java.util.Calendar;
import java.util.GregorianCalendar;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import com.jaygoel.virginminuteschecker.IVMCScraper;

/**
 * @author baker
 *
 */
public final class VMAccount
{
	// If all goes as planned... I won't need these anymore
	//private static final Pattern DATE_PAT = Pattern.compile("(\\d\\d)/(\\d\\d)/(\\d\\d)");
    //private static final Pattern MINUTES_PAT = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)");

    public static VMAccount createInvalid(final UsernamePassword iAuth)
    {
        return new VMAccount(iAuth);
    }

    public static VMAccount createEmulatorAccount()
    {
        final VMAccount ret = new VMAccount(new UsernamePassword("5555215554", "password"));

        ret.monthlyCharge = (float) 40.00;
        ret.balance = (float) 0.00;
        ret.chargedOn = new GregorianCalendar(2011, 05, 15);
        ret.newMonthStarts = new GregorianCalendar(2011, 05, 16);
        ret.minutesUsed = 400;
        ret.minutesTotal = 1200;
        ret.dataUsed = 345;
        ret.dataTotal = 2560;
        ret.isValid = true;

        return ret;
    }

    public static VMAccount createTest(final UsernamePassword auth)
    {
    	final VMAccount ret = new VMAccount(auth);

    	ret.monthlyCharge = (float) 40.00;
    	ret.balance = (float) 0.00;
    	ret.chargedOn = new GregorianCalendar(2011, 4, 25);
    	ret.newMonthStarts = new GregorianCalendar(2011, 4, 26);
    	ret.minutesUsed = 650;
    	ret.minutesTotal = 1200;
    	ret.dataUsed = 345;
        ret.dataTotal = 2560;
    	ret.isValid = true;

    	return ret;
    }

    public static VMAccount createTest()
    {
    	final VMAccount ret = new VMAccount(new UsernamePassword("5555555555", "test"));

    	ret.monthlyCharge = (float) 40.00;
    	ret.balance = (float) 0.00;
    	ret.chargedOn = new GregorianCalendar(4, 31, 11);
    	ret.newMonthStarts = new GregorianCalendar(5, 1, 11);
    	ret.minutesUsed = 400;
    	ret.minutesTotal = 1200;
    	ret.dataUsed = 345;
        ret.dataTotal = 2560;
    	ret.isValid = true;

    	return ret;
    }

    public static VMAccount createFromCache(final UsernamePassword iAuth,
    										final int iMinutesUsed,
    										final int iMinutesTotal,
    										final Calendar iChargedOn)
    {
    	final VMAccount account = new VMAccount(iAuth);

    	account.chargedOn = iChargedOn;
    	account.minutesUsed = iMinutesUsed;
    	account.minutesTotal = iMinutesTotal;

    	return account;
    }

    public VMAccount(final UsernamePassword iAuth, final IVMCScraper scraper)
    {
    	auth = iAuth;
        isValid = scraper.isValid();
        if (isValid)
        {
            number = scraper.getPhoneNumber();
            monthlyCharge = scraper.getMonthlyCharge();
            balance = scraper.getCurrentBalance();
            chargedOn = scraper.getChargedOn();
            newMonthStarts = scraper.getNewMonthStarts();
            minutesUsed = scraper.getMinutesUsed();
            minutesTotal = scraper.getMinutesTotal();
            dataUsed = scraper.getDataUsed();
            dataTotal = scraper.getDataTotal();
        }
        else
        {
            number = null;
            monthlyCharge = (float) 0;
            balance = (float) 0;
            chargedOn = null;
            newMonthStarts = null;
            minutesUsed = 0;
            minutesTotal = 0;
            dataUsed = 0;
            dataTotal = 0;
        }
    }

    private VMAccount(final UsernamePassword iAuth)
    {
    	auth = iAuth;
        isValid = false;
        number = auth.user;
        monthlyCharge = (float) 0;
        balance = (float) 0;
        chargedOn = null;
        newMonthStarts = null;
        minutesUsed = 0;
        minutesTotal = 0;
        dataUsed = 0;
        dataTotal = 0;
    }

    private final UsernamePassword auth;
    private boolean isValid;
    /*
    private String number;
    private String monthlyCharge;
    private String balance;
    private String minAmountDue;		// Removed -- it's no longer a part of VM's site, and not displayed anywhere in-app
    private String dueDate;				// Removed -- it's been renamed to dueDate, it would seem
    private String chargedOn;
    private String minutesUsed;
    private String dataUsed;
    private String dataTotal;
    */
    private String number;			// Phone number stays as String -- it's a string of digits, not an interpretable number
    private float monthlyCharge;	// Monthly charge is a dollar amount
    private float balance;			// Another dollar amount
    private Calendar chargedOn;			// "You will be charged on" date. Represented as... a date!
    private Calendar newMonthStarts;	// New: the "New month starts" date. Should help with some of the date calculations
    private int minutesUsed;		// An interpretable number -- number of minutes used up so far this month
    private int minutesTotal;		// New: store the total number of minutes, since we're no longer storing used as string x/y
    private int dataUsed;			// While this is displayed as xxx.0 MB on VM's website, it's always .0, so we don't need a float
    private int dataTotal;			// Same as dataUsed

    public boolean isValid()
    {
        return isValid;
    }

    public String getNumber()
    {
        return number;
    }
    
    public float getMonthlyCharge()
    {
        return monthlyCharge;
    }
    
    public float getBalance()
    {
        return balance;
    }
    
    public Calendar getChargedOn()
    {
        return chargedOn;
    }
    
    public Calendar getNewMonthStarts()
    {
    	return newMonthStarts;
    }
    
    public int getMinutesUsed()
    {
        return minutesUsed;
    }
    
    public int getMinutesTotal()
    {
    	return minutesTotal;
    }
    
    public int getDataUsed()
    {
    	return dataUsed;
    }
    
    public int getDataTotal()
    {
    	return dataTotal;
    }

	public UsernamePassword getAuth()
	{
		return auth;
	}

}
