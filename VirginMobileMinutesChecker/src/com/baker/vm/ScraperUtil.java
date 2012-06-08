/**
 *
 */
package com.baker.vm;

import android.util.Log;

import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.ReferenceScraper;
import com.jaygoel.virginminuteschecker.WebsiteScraper;

/**
 * @author baker
 *
 */
public final class ScraperUtil
{
    private static final String TAG = "ScraperUtil";

    private ScraperUtil()
    {

    }

    public static VMAccount scrape(final UsernamePassword a)
    {
        VMAccount acct = null;

        if (a.pass != null && a.pass.length() != 0)
        {
            try
            {
                final String[] pages = WebsiteScraper.fetchScreen(a.user, a.pass);
                IVMCScraper scraper = new ReferenceScraper(pages[0], pages[1]);
                Log.d(TAG, pages[0]);
                Log.d(TAG, pages[1]);

                if (scraper.isValid())
                {
                    Log.d(TAG, "valid");
                    acct = new VMAccount(a, scraper);
                }
                else
                {
                    Log.d(TAG, "invalid: " + a.user);
                    acct = VMAccount.createInvalid(a);
                }
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Failed to fetch virgin mobile info: " + a.user);
            }
        }

        return acct;
    }
}
