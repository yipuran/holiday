package holiday;

import java.lang.reflect.Constructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 祝日計算クラス.
 * <pre>
 * 2022年以降のみ有効
 * 春分・秋分の日は、『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式に
 * よる結果を求めてるにすぎない。毎年の官報公示の決定と異なったら官報公示に従うこと。
 * 年間の祝日リスト(国民の休日を含む）または配列の算出、
 * 年と月を指定して対象月の祝日(国民の休日を含む）の算出、
 * 指定日付の祝日判定、
 * 指定する祝日の日付の取得を目的とする。
 * 2016年以降、８月１１日  を「山の日」とする。改正祝日法、2014-5-23 参議院本会議で可決し成立した。
 * 任意の祝日を指定して情報を取得するために、
 *     public abstract class HolidayBundle を提供している。
 * この抽象クラスの具象クラスとして祝日ごとのクラスが用意されており、祝日名、祝日計算は
 * 個々の祝日HolidayBundle で実装する。
 * 振替休日計算は、HolidayBundle 抽象クラスで実装するが、特定の祝日は具象クラスで
 * オーバーライドで実装する。
 * </pre>
 */
public class Holiday{
	private int year;
	/**
	 * デフォルトコンストラクタ.
	 * 現在日の年で、Holiday(int year) コンストラクタを呼び出すのと同じ効果です。
	 */
	public Holiday(){
		this.init(LocalDate.now().getYear());
	}
	/**
	 * 対象年 指定コンストラクタ.
	 * @param year 西暦４桁
	 */
	public Holiday(int year){
		this.init(year);
	}
	private void init(int year){
		this.year = year;
	}
	/**
	 * 指定年、月の祝日、振替休日、国民の休日、Map.Entry<LocalDate, String> リストを返す
	 * @param year 西暦４桁
	 * @return  List<Map.Entry<LocalDate, String>>
	 */
	public static List<Map.Entry<LocalDate, String>> listHolidays(int year){
		Map<LocalDate, String> map = new TreeMap<>();
		HolidayType[] holidayTypes = HolidayType.values();
		for(int i=0;i < holidayTypes.length;i++){
			HolidayBundle hb = holidayTypes[i].getBundle(year);
			if (hb != null){
				map.put(hb.getDate(), hb.getDescription());
				Optional.ofNullable(hb.getChangeDate()).ifPresent(d->{
					map.put(d, "振替休日（" + hb.getDescription() + "）");
				});
			}
		}
		Arrays.stream(Holiday.getNatinalHoliday(year)).forEach(d->map.put(d, "国民の休日"));
		return map.entrySet().stream().collect(Collectors.toList());

	}
	/** HolidayType は、祝日タイプ→HolidayBundle class を紐付ける enum */
	public enum HolidayType{
		/** 元旦        ：１月１日            */ NEWYEAR_DAY             (NewYearDayBundle.class),
		/** 成人の日    ：１月の第２月曜日    */ COMING_OF_AGE_DAY       (ComingOfAgeDayBundle.class),
		/** 建国記念日  ：２月１１日          */ NATIONAL_FOUNDATION_DAY (NationalFoundationBundle.class),
		/** 天皇誕生日  ：２月２３日          */ TENNO_BIRTHDAY          (TennoBirthDayBundle.class),
		/** 春分の日    ：３月 官報公示で決定 */ SPRING_EQUINOX_DAY      (SpringEquinoxBundle.class),
		/** 昭和の日    ：４月２９日          */ SHOUWA_DAY              (ShowaDayBundle.class),
		/** 憲法記念日  ：５月３日            */ KENPOUKINEN_DAY         (KenpoukikenDayBundle.class),
		/** みどりの日  ：５月４日            */ MIDORI_DAY              (MidoriDayBundle.class),
		/** こどもの日  ：５月５日            */ KODOMO_DAY              (KodomoDayBundle.class),
		/** 海の日      ：７月の第３月曜日    */ SEA_DAY                 (SeaDayBundle.class),
		/** 山の日      ：８月１１日          */ MOUNTAIN_DAY            (MountainDayBundle.class),
		/** 敬老の日    ：９月の第３月曜      */ RESPECT_FOR_AGE_DAY     (RespectForAgeDayBundle.class),
		/** 秋分の日    ：９月 官報公示で決定 */ AUTUMN_EQUINOX_DAY      (AutumnEquinoxBundle.class),
		/** スポーツの日：１０月の第２月曜日  */ HEALTH_SPORTS_DAY       (HealthSportsDayBundle.class),
		/** 文化の日    ：１１月３日          */ CULTURE_DAY             (CultureDayBundle.class),
		/** 勤労感謝の日：１１月２３日        */ LABOR_THANKS_DAY        (LaborThanksDayBundle.class)
		;
		private Class<? extends HolidayBundle> cls;
		private HolidayType(Class<? extends HolidayBundle> cls){
			this.cls = cls;
		}
		public HolidayBundle getBundle(int year){
			try{
				Constructor<?> ct = this.cls.getDeclaredConstructor(Holiday.class,int.class);
				return (HolidayBundle)ct.newInstance(null,year);
			}catch(Exception e){
				return null;
			}
		}
	}
	// 月→HolidayBundle class 参照 enum
	private enum MonthBundle{
		JANUARY        (NewYearDayBundle.class, ComingOfAgeDayBundle.class)
		, FEBRUARY     (NationalFoundationBundle.class, TennoBirthDayBundle.class)
		, MARCH        (SpringEquinoxBundle.class)
		, APRIL        (ShowaDayBundle.class)
		, MAY          (KenpoukikenDayBundle.class, MidoriDayBundle.class, KodomoDayBundle.class)
		, JUNE         ()
		, JULY         (SeaDayBundle.class)
		, AUGUST       (MountainDayBundle.class)
		, SEPTEMBER    (RespectForAgeDayBundle.class, AutumnEquinoxBundle.class)
		, OCTOBER      (HealthSportsDayBundle.class)
		, NOVEMBER     (CultureDayBundle.class, LaborThanksDayBundle.class)
		, DECEMBER     ();
		//
		private Constructor<?>[] constructors;
		private MonthBundle(Class<?>...clss){
			if (clss.length > 0){
				constructors = new Constructor<?>[clss.length];
				for(int i=0;i < clss.length;i++){
					try{
						constructors[i] = clss[i].getDeclaredConstructor(Holiday.class,int.class);
					}catch(Exception e){}
				}
			}
		}
		Constructor<?>[] getConstructors(){
			return constructors;
		}
	}

	//========================================================================
	/** Calendar.MONTH に沿った月名の配列 */
	public static String[] MONTH_NAMES = {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
	public static String[] WEEKDAYS_JA = {"月","火","水","木","金","土","日" };
	public static String[] WEEKDAYS_SIMPLE = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun" };
	/**
	 * 祝日Bundle抽象クラス.
	 */
	public abstract class HolidayBundle{
		int year;
		private LocalDate mydate;
		public abstract int getDay();
		public abstract int getMonth();
		public abstract String getDescription();
		/** 対象年を指定するコンストラクタ
		 * @param year 西暦４桁
		 */
		public HolidayBundle(int year){
			this.year = year;
			mydate = LocalDate.of(year, getMonth(), getDay());
		}
		/** 振替休日の存在する場合、振替休日の日を返す。存在しない場合→ -1 を返す。.
		 * @return LocalDate
		 */
		public int getChangeDay(){
			if (mydate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				return mydate.plusDays(1).getDayOfMonth();
			}
			return -1;
		}
		/**
		 * 振替休日が存在するか判定
		 * @return 振替休日が存在する場合、true を返す
		 */
		public boolean hasChangeDay(){
			return mydate.getDayOfWeek().equals(DayOfWeek.SUNDAY);
		}
		/**
		 * 振替休日の存在する場合、振替休日のDateを返す。.
		 * 存在しない場合→ null を返す。
		 * @return LocalDate
		 */
		public LocalDate getChangeDate(){
			if (mydate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				return mydate.plusDays(1);
			}
			return null;
		}
		/**
		 * 祝日の曜日を DayOfWeekのint値 に従って求める.
		 * @return 1 (月曜日)から7 (日曜日)
		 */
		public int getWeekDay(){
			return mydate.getDayOfWeek().getValue();
		}
		/**
		 * 祝日の曜日.
		 * @return java.time.DayOfWeek
		 */
		public DayOfWeek getDayOfWeek(){
			return mydate.getDayOfWeek();
		}
		/** 祝日の LocaDate を取得.
		 * @return 祝日LocaDate
		 */
		public LocalDate getDate(){
			return this.mydate;
		}
		/**
		 * 年の参照.
		 * @return HolidayBundleが示す年
		 * @since 3.2
		 */
		public int getYear(){
			return this.year;
		}
		/** 曜日String算出 Japanese */
		public String dateOfWeekJA(){
			return WEEKDAYS_JA[mydate.getDayOfWeek().getValue() - 1];
		}
		/** LocalDate toString() + ":" + getDescription() */
		public String toString() {
			return this.mydate + ":"+ getDescription();
		}
	}

	/**
	 * 休日を示すLocalDate、description
	 */
	public class HolidayDate implements Comparable<HolidayDate>{
		private LocalDate date;
		private String description;
		public HolidayDate(LocalDate date, String description) {
			this.date = date;date.compareTo(date);
			this.description = description;
		}
		/**
		 * @return LocalDate
		 */
		public LocalDate getLocalDate(){
			return date;
		}
		/**
		 * @return description
		 */
		public String getDescription(){
			return description;
		}
		@Override
		public int compareTo(HolidayDate o) {
			return date.compareTo(o.getLocalDate());
		}
		@Override
		public String toString(){
			return date.toString() + " " + description;
		}
	}

	/**
	 * 指定年の祝日、振替休日、国民の休日
	 * @param year 指定年
	 * @return List<HolidayDate>
	 */
	public static List<HolidayDate> listHolidayDate(int year){
		Holiday h = new Holiday();
		Set<HolidayDate> set = new TreeSet<HolidayDate>();
		Arrays.stream(MonthBundle.values()).map(m->m.getConstructors()).filter(t->t != null)
		.forEach(t->{
			for(Constructor<?> c:t) {
				try{
					HolidayBundle b = (HolidayBundle)c.newInstance(null, year);
					set.add(h.new HolidayDate(b.getDate(), b.getDescription()));
					if (b.hasChangeDay()) {
						set.add(h.new HolidayDate(b.getChangeDate(), "振替休日"+"（"+b.getDescription()+"）"));
					}
				}catch(Exception e){
				}
			}
		});
		Arrays.stream(getNatinalHoliday(year)).map(d->h.new HolidayDate(d, "国民の休日")).forEach(set::add);
		return set.stream().collect(Collectors.toList());
	}
	/**
	 * 指定年,月の祝日、振替休日、国民の休日
	 * @param year 指定年
	 * @param month 指定月
	 * @return List<HolidayDate>
	 */
	public static List<HolidayDate> listHolidayDate(int year, int month){
		if (month < 1 || 12 < month){
			throw new IllegalArgumentException("month parameter Error");
		}
		Holiday h = new Holiday();
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		Constructor<?>[] constructors = mb.getConstructors();
		if (constructors==null) return new ArrayList<>();
		Set<HolidayDate> set = new TreeSet<HolidayDate>();
		for(int i=0;i < constructors.length;i++){
			try{
				HolidayBundle b = (HolidayBundle)constructors[i].newInstance(null,year);
				set.add(h.new HolidayDate(b.getDate(), b.getDescription()));
				if (b.hasChangeDay()) {
					set.add(h.new HolidayDate(b.getChangeDate(), "振替休日"+"（"+b.getDescription()+"）"));
				}
			}catch(Exception e){
			}
		}
		// 通常、国民の休日の発生は９月しかない
		if (month==9){
			Arrays.stream(getNatinalHoliday(year)).map(d->h.new HolidayDate(d, "国民の休日")).forEach(set::add);
		}
		return set.stream().collect(Collectors.toList());
	}

	/**
	 * 祝日、振替休日、国民の休日リスト
	 * @return  List<HolidayDate>
	 */
	public List<HolidayDate> listHolidayDate(){
		Holiday h = new Holiday();
		Set<HolidayDate> set = new TreeSet<HolidayDate>();
		Arrays.stream(MonthBundle.values()).map(m->m.getConstructors()).filter(t->t != null)
		.forEach(t->{
			for(Constructor<?> c:t) {
				try{
					HolidayBundle b = (HolidayBundle)c.newInstance(null, this.year);
					set.add(h.new HolidayDate(b.getDate(), b.getDescription()));
					if (b.hasChangeDay()) {
						set.add(h.new HolidayDate(b.getChangeDate(), "振替休日"+"（"+b.getDescription()+"）"));
					}
				}catch(Exception e){
				}
			}
		});
		Arrays.stream(getNatinalHoliday(year)).map(d->h.new HolidayDate(d, "国民の休日")).forEach(set::add);
		return set.stream().collect(Collectors.toList());
	}

	/** 祝日、振替休日を含んで、LocalDate配列で返す。*/
	public LocalDate[] arrayDate(){
		TreeSet<LocalDate> set = new TreeSet<LocalDate>();
		HolidayType[] holidayTypes = HolidayType.values();
		for(int i=0;i < holidayTypes.length;i++){
			HolidayBundle hb = holidayTypes[i].getBundle(this.year);
			if (hb != null){
				set.add(hb.getDate());
				Optional.ofNullable(hb.getChangeDate()).ifPresent(d->set.add(d));
			}
		}
		Arrays.stream(getNatinalHoliday(year)).forEach(set::add);
		return set.toArray(new LocalDate[]{});
	}

	/**
	 * 指定年、月の祝日、振替休日、国民の休日、日付(int)配列で返す
	 * @param year 西暦４桁
	 * @param month 月
	 * @return 日付(int)配列
	 * @since 4.0 2022年～
	 */
	public static int[] arrayDays(int year, int month){
		if (month < 1 || 12 < month){
			throw new IllegalArgumentException("month parameter Error");
		}
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		Constructor<?>[] constructors = mb.getConstructors();
		if (constructors==null) return new int[]{};
		Set<Integer> set = new TreeSet<Integer>();
		for(int i=0;i < constructors.length;i++){
			try{
				HolidayBundle b = (HolidayBundle)constructors[i].newInstance(null,year);
				set.add(b.getDay());
				int chgday = b.getChangeDay();
				if (chgday > 0) set.add(chgday);
			}catch(Exception e){
			}
		}
		// 通常、国民の休日の発生は９月しかない
		if (month==9){
			Arrays.stream(getNatinalHoliday(year)).forEach(d->set.add(d.getDayOfMonth()));
		}
		if (set.size()==0) return new int[]{};
		return set.stream().mapToInt(i->i).toArray();
	}
	/**
	 * 指定年、月の祝日、振替休日、国民の休日、日付(LocalDate)配列で返す
	 * @param year 西暦４桁
	 * @param month 月
	 * @return LocalDate配列
	 * @since 4.0 2022年～
	 */
	public static LocalDate[] arrayDate(int year,int month){
		if (month < 1 || 12 < month){
			throw new IllegalArgumentException("month parameter Error");
		}
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		Constructor<?>[] constructors = mb.getConstructors();
		if (constructors==null) return new LocalDate[]{};
		Set<LocalDate> set = new TreeSet<LocalDate>();
		for(int i=0;i < constructors.length;i++){
			try{
				HolidayBundle b = (HolidayBundle)constructors[i].newInstance(null,year);
				set.add(b.getDate());
				LocalDate chgdt = b.getChangeDate();
				if (chgdt != null) set.add(chgdt);
			}catch(Exception e){
			}
		}
		// 現在、国民の休日の発生は９月しかない
		if (month==9){
			Arrays.stream(getNatinalHoliday(year)).forEach(d->set.add(d));
		}
		if (set.size()==0) return new LocalDate[]{};
		LocalDate[] rtns = new LocalDate[set.size()];
		int i=0;
		for(Iterator<LocalDate> it=set.iterator();it.hasNext();i++){
			rtns[i] = it.next();
		}
		return rtns;
	}

	/**
	 * 指定年のHolidayBundleリストを取得.
	 * HolidayBundleは祝日に該当する日を表し振替休日はHolidayBundleの属性から取得すること。
	 * @param year 西暦４桁
	 * @return List<HolidayBundle>
	 */
	public static List<HolidayBundle> listHolidayBundle(int year){
		return Arrays.stream(MonthBundle.values()).map(m->m.getConstructors()).filter(t->t != null)
		 .collect(()->new ArrayList<HolidayBundle>(), (r, u)->{
			 Constructor<?>[] constructors = u;
			 for(int i=0;i < constructors.length;i++){
				try{
					r.add((HolidayBundle)constructors[i].newInstance(null, year));
				}catch(Exception e){
				}
			 }
		}, (r, t)->r.addAll(t));
	}
	/**
	 * 指定年、月のHolidayBundleリストを取得.
	 * HolidayBundleは祝日に該当する日を表し振替休日はHolidayBundleの属性から取得すること。
	 * @param year 西暦４桁
	 * @param month 月
	 * @return List<HolidayBundle>
	 */
	public static List<HolidayBundle> listHolidayBundle(int year, int month){
		List<HolidayBundle> rtn = new ArrayList<HolidayBundle>();
		try{
			MonthBundle mbundle = MonthBundle.values()[month-1];
			Constructor<?>[] ct = mbundle.getConstructors();
			for(int i=0;i < ct.length;i++){
				rtn.add((HolidayBundle)ct[i].newInstance(null, year));
			}
		}catch(Exception e){
		}
		return rtn;
	}

	/**
	 * 指定日が祝日なら、祝日名を返す。（指定日による祝日、振替休日チェックの為）.
	 * @parama 指定日
	 * @return 祝日名を返す。祝日、振替休日に該当しなければ、null を返す
	 */
	public static String queryHoliday(LocalDate dt){
		int month = dt.getMonthValue();
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		Constructor<?>[] constructors = mb.getConstructors();
		if (constructors==null){
			return null; // 祝日でない！
		}
		int targetDay = dt.getDayOfMonth();
		int targetYear = dt.getYear();
		for(int i=0;i < constructors.length;i++){
			try{
				HolidayBundle h = (HolidayBundle)constructors[i].newInstance(null,targetYear);
				if (targetDay==h.getDay()){ return h.getDescription(); }
				if (targetDay==h.getChangeDay()){ return "振替休日"+"（"+h.getDescription()+"）"; }
			}catch(Exception e){
			}
		}
		LocalDate[] nationalHolidayDates = getNatinalHoliday(targetYear);
		if (nationalHolidayDates != null){
			return Arrays.stream(nationalHolidayDates).filter(d->d.equals(dt)).findFirst().map(d->"国民の休日").orElse(null);
		}
		return null;
	}
	/**
	 * 指定日が祝日法による祝日かどうか.
	 * @param dt 指定日
	 * @return true=祝日である。
	 */
	public static boolean isHoliday(LocalDate dt){
		int month = dt.getMonthValue();
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		Constructor<?>[] constructors = mb.getConstructors();
		if (constructors==null){
			return false; // 祝日でない！
		}
		int targetDay = dt.getDayOfMonth();
		int targetYear = dt.getYear();
		for(int i=0;i < constructors.length;i++){
			try{
				HolidayBundle h = (HolidayBundle)constructors[i].newInstance(null,targetYear);
				if (targetDay==h.getDay()){ return true; }
				if (targetDay==h.getChangeDay()){ return true; }
			}catch(Exception e){
			}
		}
		LocalDate[] natinalHolidayDates = getNatinalHoliday(targetYear);
		if (natinalHolidayDates != null){
			return Arrays.stream(natinalHolidayDates).anyMatch(d->d.equals(dt));
		}
		return false;
	}
	/** 曜日String算出 Japanese */
	public static String dateOfWeekJA(LocalDate dt){
		return WEEKDAYS_JA[dt.getDayOfWeek().getValue() - 1];
	}

	/** 曜日String算出 */
	public static String dateOfWeekSimple(LocalDate dt){
		return WEEKDAYS_SIMPLE[dt.getDayOfWeek().getValue() - 1];
	}

	/**
	 * 指定年→国民の休日のみのLocalDate[]の取得.
	 * 存在しなければ、空配列を返す
	 * @return LocalDate[]
	 */
	public static LocalDate[] getNatinalHoliday(int year){
		HolidayBundle k = HolidayType.RESPECT_FOR_AGE_DAY.getBundle(year);
		HolidayBundle a = HolidayType.AUTUMN_EQUINOX_DAY.getBundle(year);
		int aday = a.getDay();
		int kday = k.getDay();
		int chgday = k.getChangeDay();
		if ((aday - kday)==2){
			return new LocalDate[]{ LocalDate.of(year, 9, kday + 1) };
		}else if (chgday > 0 && ((aday - chgday)==2)){
			return new LocalDate[]{ LocalDate.of(year, 9, chgday + 1) };
		}
		return new LocalDate[]{};
	}
	//========================================================================
	// 元旦
	class NewYearDayBundle extends HolidayBundle{
		public NewYearDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 1;
		}
		@Override
		public int getMonth(){
			return 1;
		}
		@Override
		public String getDescription(){
			return "元旦";
		}
	}
	// 成人の日
	class ComingOfAgeDayBundle extends HolidayBundle{
		public ComingOfAgeDayBundle(int year){
			super(year);
		}
		/* １月第２月曜日の日付を求める */
		@Override
		public int getDay(){
			Calendar cal = Calendar.getInstance();
			cal.set(super.year,Calendar.JANUARY,1);
			int wday = cal.get(Calendar.DAY_OF_WEEK);
			return wday > Calendar.MONDAY ? (7*2+1)-(wday - Calendar.MONDAY) : 7+1+(Calendar.MONDAY - wday);
		}
		@Override
		public int getMonth(){
			return 1;
		}
		@Override
		public String getDescription(){
			return "成人の日";
		}
	}
	// 建国記念日
	class NationalFoundationBundle extends HolidayBundle{
		public NationalFoundationBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 11;
		}
		@Override
		public int getMonth(){
			return 2;
		}
		@Override
		public String getDescription(){
			return "建国記念日";
		}
	}
	// 春分の日
	// 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式
	// さらに、1979年以前を無視、～2150年まで有効
	class SpringEquinoxBundle extends HolidayBundle{
		public SpringEquinoxBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			if (super.year <= 2099){
				return (int)(20.8431 + (0.242194 * (super.year - 1980)) - ((super.year - 1980 )/4));
			}
			return (int)(21.851 + (0.242194 * (super.year - 1980)) - ((super.year - 1980)/4));
		}
		@Override
		public int getMonth(){
			return 3;
		}
		@Override
		public String getDescription(){
			return "春分の日";
		}
	}
	// 昭和の日
	class ShowaDayBundle extends HolidayBundle{
		public ShowaDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 29;
		}
		@Override
		public int getMonth(){
			return 4;
		}
		@Override
		public String getDescription(){
			return "昭和の日";
		}
	}
	// 憲法記念日
	class KenpoukikenDayBundle extends HolidayBundle{
	   public KenpoukikenDayBundle(int year){
	      super(year);
	   }
	   @Override
	   public int getDay(){
	      return 3;
	   }
	   @Override
	   public int getMonth(){
	      return 5;
		}
		// ５月３日＝Sunday の振替は、６日
		@Override
		public int getChangeDay(){
			if (this.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				return 6;
			}
			return -1;
		}
		@Override
		public LocalDate getChangeDate(){
			if (this.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				return LocalDate.of(this.year, 5, 6);
			}
			return null;
		}
		@Override
		public String getDescription(){
			return "憲法記念日";
		}
	}
	// みどりの日
	class MidoriDayBundle extends HolidayBundle{
		public MidoriDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 4;
		}
		@Override
		public int getMonth(){
			return 5;
		}
		// ５月４日＝Sunday の振替は、６日
		@Override
		public int getChangeDay(){
			if (this.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				return 6;
			}
			return -1;
		}
		@Override
		public LocalDate getChangeDate(){
			if (this.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				return LocalDate.of(this.year, 5, 6);
			}
			return null;
		}
		@Override
		public String getDescription(){
			return "みどりの日";
		}
	}
	// こどもの日
	class KodomoDayBundle extends HolidayBundle{
		public KodomoDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 5;
		}
		@Override
		public int getMonth(){
			return 5;
		}
		@Override
		public String getDescription(){
			return "こどもの日";
		}
	}
	// 海の日
	class SeaDayBundle extends HolidayBundle{
		public SeaDayBundle(int year){
		   super(year);
		}
		/* ７月第３月曜日の日付を求める */
		@Override
		public int getDay(){
			Calendar cal = Calendar.getInstance();
			cal.set(super.year,Calendar.JULY,1);
			int wday = cal.get(Calendar.DAY_OF_WEEK);
			return wday > Calendar.MONDAY ? (7*3+1)-(wday - Calendar.MONDAY) : 14+1+(Calendar.MONDAY - wday);
		}
		@Override
		public int getMonth(){
			return 7;
		}
		@Override
		public String getDescription(){
			return "海の日";
		}
	}
	// 山の日
	class MountainDayBundle extends HolidayBundle{
		public MountainDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 11;
		}
		@Override
		public int getMonth(){
			return 8;
		}
		@Override
		public String getDescription(){
			return "山の日";
		}
	}
	// 敬老の日
	class RespectForAgeDayBundle extends HolidayBundle{
		public RespectForAgeDayBundle(int year){
			super(year);
		}
		/* ９月第３月曜日の日付を求める */
		@Override
		public int getDay(){
			Calendar cal = Calendar.getInstance();
			cal.set(super.year,Calendar.SEPTEMBER,1);
			int wday = cal.get(Calendar.DAY_OF_WEEK);
			return wday > Calendar.MONDAY ? (7*3+1)-(wday - Calendar.MONDAY) : 14+1+(Calendar.MONDAY - wday);
		}
		@Override
		public int getMonth(){
			return 9;
		}
		@Override
		public String getDescription(){
			return "敬老の日";
		}
	}
	// 秋分の日
	// 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式
	// さらに、1979年以前を無視、～2150年まで有効
	class AutumnEquinoxBundle extends HolidayBundle{
		public AutumnEquinoxBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			if (super.year <= 2099){
				return (int)(23.2488 + (0.242194 * (super.year - 1980)) - ((super.year - 1980)/4));
			}
			return (int)(24.2488 + (0.242194 * (super.year - 1980)) - ((super.year - 1980)/4));
		}
		@Override
		public int getMonth(){
			return 9;
		}
		@Override
		public String getDescription(){
			return "秋分の日";
		}
	}
	// スポーツの日（体育の日）
	class HealthSportsDayBundle extends HolidayBundle{
		public HealthSportsDayBundle(int year){
			super(year);
		}
		/* １０月第２月曜日の日付を求める */
		@Override
		public int getDay(){
			Calendar cal = Calendar.getInstance();
			cal.set(super.year,Calendar.OCTOBER,1);
			int wday = cal.get(Calendar.DAY_OF_WEEK);
			return wday > Calendar.MONDAY ? (7*2+1)-(wday - Calendar.MONDAY) : 7+1+(Calendar.MONDAY - wday);
		}
		@Override
		public int getMonth(){
			return 10;
		}
		@Override
		public String getDescription(){
			return "スポーツの日";
		}
	}
	// 文化の日
	class CultureDayBundle extends HolidayBundle{
		public CultureDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 3;
		}
		@Override
		public int getMonth(){
			return 11;
		}
		@Override
		public String getDescription(){
			return "文化の日";
		}
	}
	// 勤労感謝の日
	class LaborThanksDayBundle extends HolidayBundle{
		public LaborThanksDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 23;
		}
		@Override
		public int getMonth(){
			return 11;
		}
		@Override
		public String getDescription(){
			return "勤労感謝の日";
		}
	}
	// 天皇誕生日
	class TennoBirthDayBundle extends HolidayBundle{
		public TennoBirthDayBundle(int year){
			super(year);
		}
		@Override
		public int getDay(){
			return 23;
		}
		@Override
		public int getMonth(){
			return 2;
		}
		@Override
		public String getDescription(){
			return "天皇誕生日";
		}
	}
}
