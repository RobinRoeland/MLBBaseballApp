package com.example.baseballapp.classes;

import java.time.LocalDateTime;

public class CalenderHelpFunctions {
    public static int getStartday(int year, int month){
        // Create a new LocalDateTime with the first day of the same month and year
        LocalDateTime firstDayOfMonthDateTime = LocalDateTime.of(year, month,1,0,0);

        //Maandag = 1 / Zondag = 7
        return firstDayOfMonthDateTime.getDayOfWeek().getValue();
    }

    public static int calculateDaysInMonth(int year, int month) {
        if (month < 1 || month > 12) {
            // Invalid month
            return -1;
        }

        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                // Check for leap year
                if (isLeapYear(year)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1; // Invalid month
        }
    }

    public static boolean isLeapYear(int year) {
        // Leap year is divisible by 4, except for years divisible by 100 but not divisible by 400
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }
}
