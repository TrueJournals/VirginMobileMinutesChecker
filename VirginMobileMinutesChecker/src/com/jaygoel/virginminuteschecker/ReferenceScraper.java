package com.jaygoel.virginminuteschecker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ReferenceScraper implements IVMCScraper
{
	// TODO: Find a good HTML parser and use that instead -- http://stackoverflow.com/questions/1732348/regex-match-open-tags-except-xhtml-self-contained-tags/1732454#1732454
	//  An HTML parser would provide the additional benefit of parse once, read many.
	private Document doc;
	private Document dataDoc;
	
	public ReferenceScraper(final String accountSource, final String dataSource)
	{
		doc = Jsoup.parse(accountSource);
		dataDoc = Jsoup.parse(dataSource);
	}

	/* usage note: don't call any other method if the page data is invalid */
    @Override
    public boolean isValid()
    {
        Elements valid = doc.select("p.tel");
        return !valid.isEmpty();
    }

    @Override
    public String getPhoneNumber()
    {
        Elements phone = doc.select("p.tel");
        return phone.first().text();
    }

    @Override
    public float getMonthlyCharge()
    {
    	Elements monthlyCharge = doc.select("#current_balance p");
    	String amount = monthlyCharge.first().text();
    	
    	try {
			Number num = NumberFormat.getCurrencyInstance().parse(amount);
			return num.floatValue();
		} catch (ParseException e) {
			return -1;
		}
    }

    @Override
    public float getCurrentBalance()
    {
    	Elements currentBalance = doc.select("#anytime_minutes_left p");
    	String amount = currentBalance.first().text();
    	
    	try {
			Number num = NumberFormat.getCurrencyInstance().parse(amount);
			return num.floatValue();
		} catch (ParseException e) {
			return -1;
		}
    }

    @Override
    public Calendar getChargedOn()
    {
    	Elements chargedOn = doc.select("#charge_date p");
    	String date = chargedOn.first().text();
    	
    	String[] parts = date.split("/");
    	
    	return new GregorianCalendar(2000+Integer.parseInt(parts[2]),
    			Integer.parseInt(parts[0])-1,		// Month is zero based... O.o
    			Integer.parseInt(parts[1]));
    }
    
    @Override
    public Calendar getNewMonthStarts()
    {
    	Elements monthStarts = doc.select("#charge_date span");
    	String text = monthStarts.first().text();
    	
    	String[] parts1 = text.split(" ");
    	String[] parts2 = parts1[parts1.length-1].split("/");
    	
    	return new GregorianCalendar(2000+Integer.parseInt(parts2[2]),
    			Integer.parseInt(parts2[0])-1,		// Month is zero based... O.o
    			Integer.parseInt(parts2[1]));
    }

    @Override
    public int getMinutesUsed()
    {
        Elements minutesUsed = doc.select("#remaining_minutes strong");
        return Integer.parseInt(minutesUsed.first().text());
    }
    
    @Override
    public int getMinutesTotal()
    {
    	Elements minutesTotal = doc.select("#remaining_minutes");
    	String text = minutesTotal.first().text();
    	
    	String[] parts = text.split("\\s+");
    	
    	return Integer.parseInt(parts[parts.length-1]);
    }
    
    @Override
    public int getDataUsed()
    {
    	//TODO: This fails... fails big time. It seems that JSoup doesn't like having two different pages just shoved together
    	// Find a way to return two different pages from WebsiteScraper, and carry that through to reference scraper, where we can
    	// have an accountDoc and a dataDoc
    	
    	// Unfortunately.. VM's horribly formatted HTML provides no proper way to do this that I can see
    	// So.. stick with the string search after extracting the correct element.
    	Elements data = dataDoc.select("div#act table.styled td");
    	String str = data.get(1).text();
    	
    	String srch = "MB Used: ";
    	int start = str.indexOf(srch);
    	int end = str.indexOf(".0 MB", start);
    	
    	if((start > 0) && (end > 0))
    	{
    		return Integer.parseInt(str.substring(start + srch.length(), end));
    	}
    	else
    	{
    		return 0;
    	}
    }
    
    @Override
    public int getDataTotal()
    {
    	// Unfortunately.. VM's horribly formatted HTML provides no proper way to do this that I can see
    	// So.. stick with the string search after extracting the correct element.
    	Elements data = dataDoc.select("div#act table.styled td");
    	String str = data.get(1).text();
    	
    	String srch = "Data speeds may be reduced at ";
    	int start = str.indexOf(srch);
    	int end = str.indexOf(".0 MB", start);
    	
    	if((start > 0) && (end > 0))
    	{
    		return Integer.parseInt(str.substring(start + srch.length(), end));
    	}
    	else
    	{
    		return 0;
    	}
    }

}
