package com.teamapt.profectus.moneytransfers.settlementrecon.utils.westernunion;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.StringJoiner;

public class WesternUnionDateSetterTest {

    @Test
    public void get_date_from_western_union_income_or_details_file(){
        String docTitle = "S0000005_20200122_20200204_01.txt";
        String [] dateParts  = new String[3];
        String startDate = "", endDate = "";

        //use mockito for get_year and implode
        String [] docTitleParts = docTitle.split("_");
        String currentYear = get_year();
        byte size = (byte) docTitleParts.length;

        for(int i=0; i<size; i++){
            if(docTitleParts[i].contains(currentYear)){
                dateParts[0] = docTitleParts[i].substring(0, currentYear.length());
                String month_and_day = docTitleParts[i].substring(currentYear.length(), docTitleParts[i].length());
                dateParts[1] = month_and_day.substring(0,2);
                dateParts[2] = month_and_day.substring(2,4);
                endDate = implode("/", dateParts);
                if(!startDate.isEmpty() && !endDate.isEmpty())
                    break;
                startDate = endDate;
                endDate = "";
            }
        }

        Assert.assertEquals("2020/01/22", startDate);
        Assert.assertEquals("2020/02/04", startDate);
    }

    private String get_year(){
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    private String implode(String separator, String ...data) {
        StringJoiner sb = new StringJoiner(separator);
        for (String token : data) {
            sb.add(token);
        }
        return sb.toString();
    }
}
