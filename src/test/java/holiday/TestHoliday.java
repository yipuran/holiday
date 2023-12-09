package holiday;
import java.time.LocalDate;
import java.util.Map;

import holiday.Holiday;
import holiday.Holiday.HolidayBundle;
import holiday.Holiday.HolidayDate;

public class TestHoliday {
	public static void main(String[] args) {

		Holiday holiday = new Holiday(2026);

		System.out.println("-------- arrayDate() ----------");
		for(LocalDate d : holiday.arrayDate()){
			System.out.println( d );
		}
		System.out.println("--------- listHolidayDate()  ---------");
		for(HolidayDate d : holiday.listHolidayDate()){
			System.out.println( d );
		}
		System.out.println("--------- Holiday.listHolidayDate(2026) ---------");
		for(HolidayDate d : Holiday.listHolidayDate(2022)){
			System.out.println( d );
		}
		System.out.println("--------- Holiday.listHolidays(2026) ---------");
		for(Map.Entry<LocalDate, String> e : Holiday.listHolidays(2026)) {
			System.out.println( e.getKey() + ":"+ e.getValue() );
		}
		System.out.println("--------- Holiday.listHolidayDate(2026, 9) ---------");
		for(HolidayDate d : Holiday.listHolidayDate(2026, 9)) {
			System.out.println( d );
		}
		System.out.println("--------- Holiday.listHolidayBundle(2026) ---------");
		for(HolidayBundle d : Holiday.listHolidayBundle(2026)) {
			System.out.println( d );
		}
		System.out.println("--------- Holiday.listHolidayBundle(2026, 5) ---------");
		for(HolidayBundle d : Holiday.listHolidayBundle(2026, 5)) {
			System.out.println( d );
		}
		System.out.println("--------- Holiday.isHoliday(LocalDate.of(2026, 9, 22) ---------");
		System.out.println( Holiday.isHoliday(LocalDate.of(2026, 9, 22)) );

		System.out.println("--------- Holiday.getNatinalHoliday(2026) ---------");
		for(LocalDate d:Holiday.getNatinalHoliday(2026)) {
			System.out.println( d );
		}
		System.out.println("--------- Holiday.arrayDate(2026, 9) ---------");
		for(LocalDate d : Holiday.arrayDate(2026, 9)) {
			System.out.println( d );
		}
		System.out.println("--------- Holiday.arrayDays(2026, 9) ---------");
		for(int d : Holiday.arrayDays(2026, 9)) {
			System.out.println( d );
		}


	}

}
