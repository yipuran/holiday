package holiday;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
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
 * 2007年以降のみ有効
 * 春分・秋分の日は、『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式に
 * よる結果を求めてるにすぎない。毎年の官報公示の決定と異なったら官報公示に従うこと。
 * 年間の祝日リスト(国民の休日を含む）（配列）の算出、
 * 年と月を指定して対象月の祝日(国民の休日を含む）の算出、
 * 指定日付の祝日判定、
 * 指定する祝日の日付の取得を目的とする。
 *
 * 2016年以降、８月１１日  を「山の日」とする。改正祝日法、2014-5-23 参議院本会議で可決し成立した。
 *
 * 任意の祝日を指定して情報を取得するために、
 *     public abstract class HolidayBundle を提供している。
 * この抽象クラスの具象クラスとして祝日ごとのクラスが用意されており、祝日名、祝日計算は
 * 個々の祝日HolidayBundle で実装する。
 * 振替休日計算は、HolidayBundle 抽象クラスで実装するが、特定の祝日は具象クラスで
 * オーバーライドで実装する。
 * 【使用例】
 *     // 指定年、月の祝日、振替休日、国民の休日、Map.Entry<LocalDate, String> リスト
 *        Holiday.listHolidays(2032).stream().forEach(e->{
 *           System.out.println( e.getKey() + "  " + e.getValue() );
 *        });
 *     // 2009年の祝日配列
 *         Holiday holday = new Holiday(2009);  // コンストラクタを使用すること
 *         LocalDate[] ary = holday.listHoliDays();
 *         Arrays.stream(ary).forEach(d->{
 *            System.out.println(d);
 *         });
 *     // 2009年の９月の祝日、
 *         int[] days = Holiday.listHoliDays(2009, 9);
 *         LocalDate[] dts = Holiday.listHoliDayDates(2009, 9);
 *     // 指定日付の祝日判定
 *        String target = "2009/05/06";
 *        String res = Holiday.queryHoliday(LocalDate.parse(target, DateTimeFormatter.ofPattern("yyyy/MM/dd")));
 *        System.out.println(res);
 *     // 指定する祝日の日付の取得
 *     //   Holiday.HolidayType enum から、getBundleメソッドで、Holiday.HolidayBundle
 *     //   を取得してHoliday.HolidayBundleが提供するメソッドを利用する
 *          //    Holiday.HolidayBundle#getMonth()       → 月
 *          //    Holiday.HolidayBundle#getDay()         → 日
 *          //    Holiday.HolidayBundle#getDescription() → 祝日名
 *          //    Holiday.HolidayBundle#getDate()        → 祝日のDate
 *          //    Holiday.HolidayBundle#getChangeDay()   → 振替休日ある場合のDay
 *          //    Holiday.HolidayBundle#getChangeDate()  → 振替休日ある場合のDate
 *       // 2009年の春分の日
 *          Holiday.HolidayBundle h = Holiday.HolidayType.SPRING_EQUINOX_DAY.getBundle(2009);
 *          System.out.println(h.getMonth()+"月 "+h.getDay()+"日"
 *                              +"（"+Holiday.WEEKDAYS_JA[h.getWeekDay()-1]+"）"
 *                              +" "+h.getDescription());
 *     // 指定年→国民の休日のみのDate[]の取得
 *          // 2009年の国民の休日配列
 *          LocalDate[] ds = Holiday.getNatinalHoliday(2009);
 *          for(int i=0;i < ds.length;i++){
 *             System.out.println(ds[i]+"-->"+Holiday.queryHoliday(ds[i]));
 *          }
 *     // 指定年→HolidayBundleリストの取得
 *          List<HolidayBundle> lhs = Holiday.listHolidayBundle(2018);
 *          lhs.stream().forEach(b->{
 *             System.out.println(b.getClass().getSimpleName() + " " + b.getDate() + " " + b.getDescription() + "  " + b.getChangeDate());
 *          });
 *     // listHolidayBundleの利用
 *          Map<LocalDate, String> map = Holiday.listHolidayBundle(2018).stream().collect(()->new TreeMap<LocalDate, String>(), (r, t)->{
 *             r.put(t.getDate(), t.getDescription());
 *             Optional.ofNullable(t.getChangeDate()).ifPresent(d->r.put(d, "振替休日"));
 *          }, (r, u)->r.putAll(u));
 * </pre>
 *
 * @since 3.1 2019年は天皇誕生日無し
 * @since 3.2 static List<HolidayBundle> listHolidayBundle(int year, int month) を追加、2019年5月1日を即位日として祝日にしていない。
 * @since 3.3 2019年のみの平成の次の即位日５月１日と即位礼正殿の儀の１０月２２日の祝日を追加、自動的に休日になる
 * 国民の休日、2019年4月30日と5月2日を国民の休日として算出するようにする。
 * 祝日の間が1日になることで発生する国民の休日を年から求める関数は 2019年には対応しない。
 * 今まで 祝日の間が1日が発生しない年は、getNatinalHoliday が null を返していたが、空配列を返すように修正した。
 * @since 3.4 public static List<Map.Entry<LocalDate, String>> listHolidays(int year) を追加
 * @since 3.5 HolidayBundle 2019年、2019-4-30, 2019-5-2 の抜けを修正 ⇒ listHolidayBundle(int) listHolidayBundle(int,int) getNatinalHoliday(2019)
 * @since 3.6 public static String queryHoliday(LocalDate) 修正
 * @since 3.7 バグ修正、2019年対応で作成した private enum MonthBundle 内の setBundleclass を廃止
 * @since 3.8 2019年対応バグ修正、
 *               public static int[] listHoliDays(int year, int month) を修正
 *               public static LocalDate[] listHoliDayDates(int year,int month) を修正
 *             （注意）listHolidayBundle が返す HolidayBundle は、祝日と振替休日の束ねであって、振替休日は、HolidayBundle の getChangeDate() で求める。
 *            HolidayBundle で、public boolean hasChangeDay() を追加した。
 * @since 3.9 東京五輪対応、2020年だけ、山の日は、８月１０日
 * @since 3.10 東京五輪対応、2020年だけ、海の日は、７月２３日で、体育の日は、「スポーツの日」という名称で７月２４日、以降「体育の日」→「スポーツの日」名称変更
 * @since 3.11 東京五輪特措法対応、2021年だけ、海の日は、７月２２日、スポーツの日は、７月２３日、山の日を 2021年だけ、８月８日で、８月９日は振替休日
 * @since 3.12
 */
public class Holiday{
	private LocalDate[] holidayDates;
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
		TreeSet<LocalDate> set = new TreeSet<LocalDate>();
		HolidayType[] holidayTypes = HolidayType.values();
		for(int i=0;i < holidayTypes.length;i++){
			HolidayBundle hb = holidayTypes[i].getBundle(year);
			if (hb != null){
				if (year < 2016 && hb.getMonth()==8) continue;  // since ver 2.0
				set.add(hb.getDate());
				Optional.ofNullable(hb.getChangeDate()).ifPresent(d->set.add(d));
			}
		}
		LocalDate[] ds = getNatinalHoliday(year);
		if (ds != null){
			Arrays.stream(ds).forEach(d->{
				set.add(d);
			});
		}
		this.holidayDates = new LocalDate[set.size()];
		int n = 0;
		for(Iterator<LocalDate> it=set.iterator();it.hasNext();n++){
			this.holidayDates[n] = it.next();
		}
	}
	/**
	 * 指定年、月の祝日、振替休日、国民の休日、Map.Entry<LocalDate, String> リストを返す
	 * @param year 西暦４桁
	 * @return  List<Map.Entry<LocalDate, String>>
	 * @since 3.8 2019年対応
	 */
	public static List<Map.Entry<LocalDate, String>> listHolidays(int year){
		Map<LocalDate, String> map = new TreeMap<>();
		HolidayType[] holidayTypes = HolidayType.values();
		for(int i=0;i < holidayTypes.length;i++){
			HolidayBundle hb = holidayTypes[i].getBundle(year);
			if (hb != null){
				if (year < 2016 && hb.getMonth()==8) continue;
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
		/** 建国記念日  ：２月１１日          */ NATIONAL_FOUNDATION_DAY (NatinalFoundationBundle.class),
		/** 春分の日    ：３月 官報公示で決定 */ SPRING_EQUINOX_DAY      (SpringEquinoxBundle.class),
		/** 昭和の日    ：４月２９日          */ SHOUWA_DAY              (ShowaDayBundle.class),
		/** 憲法記念日  ：５月３日            */ KENPOUKINEN_DAY         (KenpoukikenDayBundle.class),
		/** みどりの日  ：５月４日            */ MIDORI_DAY              (MidoriDayBundle.class),
		/** こどもの日  ：５月５日            */ KODOMO_DAY              (KodomoDayBundle.class),
		/** 海の日      ：７月の第３月曜日    */ SEA_DAY                 (SeaDayBundle.class),
		/** 山の日      ：８月１１日.2016年以降 */ MOUNTAIN_DAY          (MountainDayBundle.class),
		/** 敬老の日    ：９月の第３月曜      */ RESPECT_FOR_AGE_DAY     (RespectForAgeDayBundle.class),
		/** 秋分の日    ：９月 官報公示で決定 */ AUTUMN_EQUINOX_DAY      (AutumnEquinoxBundle.class),
		/** スポーツの日：１０月の第２月曜日  */ HEALTH_SPORTS_DAY       (HealthSportsDayBundle.class),
		/** 文化の日    ：１１月３日          */ CULTURE_DAY             (CultureDayBundle.class),
		/** 勤労感謝の日：１１月２３日        */ LABOR_THANKS_DAY        (LaborThanksDayBundle.class),
		/** 天皇誕生日  ：１２月２３日        */ TENNO_BIRTHDAY          (TennoBirthDayBundle.class)
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
		, FEBRUARY     (NatinalFoundationBundle.class, TennoBirthDayBundle.class)
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
		public String toString() {
			return this.mydate + ":"+ getDescription();
		}
	}

	/** 祝日、振替休日を含んで、LocalDate配列で返す。*/
	public LocalDate[] listHoliDays(){
		return holidayDates;
	}

	/**
	 * 指定年、月の祝日、振替休日、国民の休日、日付(int)配列で返す
	 * @param year 西暦４桁
	 * @param month 月
	 * @return 日付(int)配列
	 * @since 3.1 2019年は天皇誕生日無し
	 * @since 3.8 2019年対応
	 * @since 3.11 2021年対応
	 */
	public static int[] listHoliDays(int year, int month){
		if (month < 1 || 12 < month){
			throw new IllegalArgumentException("month parameter Error");
		}
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		// 2019年は天皇誕生日無し ver 3.1
		if (month==2){
			if (year <= 2019){
				Set<Integer> set = new TreeSet<Integer>();
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ NatinalFoundationBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					set.add(h.getDay());
					int chgday = h.getChangeDay();
					if (chgday > 0) set.add(chgday);
					if (set.size()==0) return new int[]{};
					return set.stream().mapToInt(i->i).toArray();
				}catch(Exception e){
				}
			}
		}else{
			if (year==2020 && month==7) { // ver 3.10
				return new int[]{ 23, 24 };
			}
			if (year==2021 && month==7) { // ver 3.11
				return new int[]{ 22, 23 };
			}
			if ((year==2020 || year==2021) && month==10) {
				return new int[]{};
			}
			if (year < 2019 && month==12){
				Set<Integer> set = new TreeSet<Integer>();
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ TennoBirthDayBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					set.add(h.getDay());
					int chgday = h.getChangeDay();
					if (chgday > 0) set.add(chgday);
					if (set.size()==0) return new int[]{};
				}catch(Exception e){
				}
			}
			// ver3.3 平成の次の即位日 or 即位礼正殿の儀
			if (year == 2019){
				if (month==5){
					return new int[]{ 1, 2, 3, 4, 5, 6 };
				}else if(month==9){
					return new int[]{ 16, 23 };
				}else if(month==10){
					return new int[]{ 14, 22 };
				}
			}
		}
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
		// ver3.3 平成の次の即位日
		if (year==2019){
			if (month==4){
				set.add(30);
			}else if(month==5){
				set.add(2);
			}
		}
		if (set.size()==0) return new int[]{};
		return set.stream().mapToInt(i->i).toArray();
	}
	/**
	 * 指定年、月の祝日、振替休日、国民の休日、日付(Date)配列で返す
	 * @param year 西暦４桁
	 * @param month 月
	 * @return LocalDate配列
	 * @since 3.1 2019年は天皇誕生日無し
	 * @since 3.8 2019年対応
	 */
	public static LocalDate[] listHoliDayDates(int year,int month){
		if (month < 1 || 12 < month){
			throw new IllegalArgumentException("month parameter Error");
		}
		if (year < 2016 && month==8) return new LocalDate[]{}; // since ver 2.0
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		// 2019年は天皇誕生日無し ver 3.1
		if (month==2){
			if (year <= 2019){
				Set<LocalDate> set = new TreeSet<LocalDate>();
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ NatinalFoundationBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					set.add(h.getDate());
					LocalDate chgdt = h.getChangeDate();
					if (chgdt != null) set.add(chgdt);
					LocalDate[] rtns = new LocalDate[set.size()];
					int i=0;
					for(Iterator<LocalDate> it=set.iterator();it.hasNext();i++){
						rtns[i] = it.next();
					}
					return rtns;
				}catch(Exception e){
				}
			}
		}else{
			if (year==2020 && month==7) { // ver 3.10
				return new LocalDate[]{ LocalDate.of(2020, 7, 23), LocalDate.of(2020, 7, 24) };
			}
			if (year==2021 && month==7) { // ver 3.11
				return new LocalDate[]{ LocalDate.of(2020, 7, 22), LocalDate.of(2020, 7, 23) };
			}
			if (year==2020 && month==10) {
				return new LocalDate[]{};
			}
			if (year < 2019 && month==12){
				Set<LocalDate> set = new TreeSet<LocalDate>();
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ TennoBirthDayBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					set.add(h.getDate());
					LocalDate chgdt = h.getChangeDate();
					if (chgdt != null) set.add(chgdt);
					LocalDate[] rtns = new LocalDate[set.size()];
					int i=0;
					for(Iterator<LocalDate> it=set.iterator();it.hasNext();i++){
						rtns[i] = it.next();
					}
					return rtns;
				}catch(Exception e){
				}
			}
			// 平成の次の即位日 or 即位礼正殿の儀
			if (year == 2019){
				if (month==5){
					return new LocalDate[]{ LocalDate.of(2019, 5, 1), LocalDate.of(2019, 5, 2), LocalDate.of(2019, 5, 3), LocalDate.of(2019, 5, 4), LocalDate.of(2019, 5, 5), LocalDate.of(2019, 5, 6) };
				}else if(month==9){
					return new LocalDate[]{ LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 23) };
				}else if(month==10){
					return new LocalDate[]{ LocalDate.of(2019, 10, 14), LocalDate.of(2019, 10, 22) };
				}
			}
		}
		Constructor<?>[] constructors = mb.getConstructors();
		if (constructors==null) return new LocalDate[]{}; // for ver 2.0
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
		// ver3.3 平成の次の即位日
		if (year==2019){
			if (month==4){
				set.add(LocalDate.of(2019, 4, 30));
			}else if(month==5){
				set.add(LocalDate.of(2019, 5, 2));
			}
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
	 * @param year 西暦４桁
	 * @return List<HolidayBundle>
	 * @since 3.1 2019年は天皇誕生日無し
	 */
	public static List<HolidayBundle> listHolidayBundle(int year){
		return Arrays.stream(MonthBundle.values()).map(m->{
			 try{
				 if (m.equals(MonthBundle.FEBRUARY)){
					 if (year <= 2019){
						 return new Constructor<?>[]{ NatinalFoundationBundle.class.getDeclaredConstructor(Holiday.class, int.class)  };
					 }
				 }else{
					 if (year < 2019 && m.equals(MonthBundle.DECEMBER)){
						 return new Constructor<?>[]{ TennoBirthDayBundle.class.getDeclaredConstructor(Holiday.class, int.class)  };
					 }
					 if (year==2019 && m.equals(MonthBundle.APRIL)){
						 return new Constructor<?>[]{ ShowaDayBundle.class.getDeclaredConstructor(Holiday.class, int.class),
							 HeisetNextPrevBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					 }
					 if (year==2019 && m.equals(MonthBundle.MAY)){
						 return new Constructor<?>[]{
							 HeiseiNextDayBundle.class.getDeclaredConstructor(Holiday.class, int.class),
							 HeisetNextNextBundle.class.getDeclaredConstructor(Holiday.class, int.class),
							 KenpoukikenDayBundle.class.getDeclaredConstructor(Holiday.class, int.class),
							 MidoriDayBundle.class.getDeclaredConstructor(Holiday.class, int.class),
							 KodomoDayBundle.class.getDeclaredConstructor(Holiday.class, int.class)
						 };
					 }
					 if ((year==2020 || year==2021) && m.equals(MonthBundle.JULY)){
						 return new Constructor<?>[]{
							 SeaDayBundle.class.getDeclaredConstructor(Holiday.class, int.class),
							 HealthSportsDayBundle.class.getDeclaredConstructor(Holiday.class, int.class)
						 };
					 }
					 if ((year==2020 || year==2021) && m.equals(MonthBundle.OCTOBER)){
						 return new Constructor<?>[]{};
					 }
					 if (year==2019 && m.equals(MonthBundle.OCTOBER)){
						 return new Constructor<?>[]{
							 HealthSportsDayBundle.class.getDeclaredConstructor(Holiday.class, int.class),
							 SokuiReiSeidenDayBundle.class.getDeclaredConstructor(Holiday.class, int.class)
						 };
					 }
				 }
			 }catch(Exception e){
			 }
			 return m.getConstructors();
		 }).filter(t->t != null)
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
	 * @param year 西暦４桁
	 * @param month 月
	 * @return List<HolidayBundle>
	 * @since 3.2
	 */
	public static List<HolidayBundle> listHolidayBundle(int year, int month){
		List<HolidayBundle> rtn = new ArrayList<HolidayBundle>();
		if (month==6) return rtn;

		if (month==7 && (year==2020 || year==2021)){
			try{
			rtn.add(SeaDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			rtn.add(HealthSportsDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			}catch(Exception e){
			}
			return rtn;
		}
		if (month==4 && year==2019){
			// ver 3.5
			try{
			rtn.add(ShowaDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			rtn.add(HeisetNextPrevBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			}catch(Exception e){
			}
			return rtn;
		}
		if (month==5 && year==2019){
			// ver 3.5
			try{
			rtn.add(HeiseiNextDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			rtn.add(HeisetNextNextBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			rtn.add(KenpoukikenDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			rtn.add(MidoriDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			rtn.add(KodomoDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			}catch(Exception e){
			}
			return rtn;
		}
		if (month==12){
			if (year < 2019){
				try{
					rtn.add(TennoBirthDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
				}catch(Exception e){
				}
			}
		}else if(month==2){
			if (year > 2019){
				try{
					rtn.add(NatinalFoundationBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
					rtn.add(TennoBirthDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
				}catch(Exception e){
				}
			}else{
				try{
					rtn.add(NatinalFoundationBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
				}catch(Exception e){
				}
			}
		}else if(month==8){
			if (year < 2016) return rtn;
			try{
				rtn.add(MountainDayBundle.class.getDeclaredConstructor(Holiday.class, int.class).newInstance(null, year));
			}catch(Exception e){
			}
		}else{
			try{
				MonthBundle mbundle = MonthBundle.values()[month-1];
				Constructor<?>[] ct = mbundle.getConstructors();
				for(int i=0;i < ct.length;i++){
					rtn.add((HolidayBundle)ct[i].newInstance(null, year));
				}
				}catch(Exception e){
			}
		}
		return rtn;
	}

	/** 日付フォーマット yyyy/MM/dd */
	public static SimpleDateFormat YMD_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	/** Calendar.MONTH に沿った月名の配列 */
	public static String[] MONTH_NAMES = {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};

	/**
	 * 指定日が祝日なら、祝日名を返す。（指定日による祝日、振替休日チェックの為）.
	 * @parama 指定日
	 * @return 祝日名を返す。祝日、振替休日に該当しなければ、null を返す
	 * @since 3.1 2019年は天皇誕生日無し。
	 */
	public static String queryHoliday(LocalDate dt){
		int year = dt.getYear();
		int month = dt.getMonthValue();
		if (year < 2016 && dt.getMonthValue() == 8) return null;   // since ver 2.0
		if (year==2020){
			if (dt.getMonthValue()==10) return null;
			if (dt.equals(LocalDate.of(2020, 7, 23))) return "海の日";
			if (dt.equals(LocalDate.of(2020, 7, 24))) return "スポーツの日";
		}
		if (year==2021){
			if (dt.getMonthValue()==10) return null;
			if (dt.equals(LocalDate.of(2020, 7, 22))) return "海の日";
			if (dt.equals(LocalDate.of(2020, 7, 23))) return "スポーツの日";
		}
		// ver 3.3
		if (dt.getYear()==2019){
			if (dt.equals(LocalDate.of(2019, 4, 30))) return "国民の休日";
			if (dt.equals(LocalDate.of(2019, 5, 1))) return "平成の次の即位日"; // ver 3.6
			if (dt.equals(LocalDate.of(2019, 5, 2))) return "国民の休日"; // ver 3.6
			if (dt.equals(LocalDate.of(2019, 10, 22))) return "即位礼正殿の儀"; // ver 3.6
		}
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		// 2019年は天皇誕生日無し
		if (dt.getMonthValue()==2){
			if (year <= 2019){
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ NatinalFoundationBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					if (dt.getDayOfMonth()==h.getDay()){ return h.getDescription(); }
					if (dt.getDayOfMonth()==h.getChangeDay()){ return "振替休日"+"（"+h.getDescription()+"）"; }
				}catch(Exception e){
				}
				return null;
			}
		}else{
			if (year < 2019 && month==12){
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ TennoBirthDayBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					if (dt.getDayOfMonth()==h.getDay()){ return h.getDescription(); }
					if (dt.getDayOfMonth()==h.getChangeDay()){ return "振替休日"+"（"+h.getDescription()+"）"; }
				}catch(Exception e){
				}
			}
		}
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
		int year = dt.getYear();
		int month = dt.getMonthValue();
		if (year==2020){
			if (dt.getMonthValue()==10) return false;
			if (dt.equals(LocalDate.of(2020, 7, 24))) return true;
			if (dt.equals(LocalDate.of(2020, 7, 22))) return true;
			if (dt.equals(LocalDate.of(2020, 7, 23))) return true;
		}
		if (year==2021){
			if (dt.getMonthValue()==10) return false;
			if (dt.equals(LocalDate.of(2021, 7, 22))) return true;
			if (dt.equals(LocalDate.of(2021, 7, 23))) return true;
		}
		if (year < 2016 && dt.getMonthValue()==8) return false;   // since ver 2.0
		MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month - 1]);
		// 2019年は天皇誕生日無し
		if (dt.getMonthValue()==2){
			if (year <= 2019){
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ NatinalFoundationBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					if (dt.getDayOfMonth()==h.getDay()){ return true;}
					return dt.getDayOfMonth()==h.getChangeDay();
				}catch(Exception e){
				}
			}
		}else{
			if (year < 2019 && month==12){
				try{
					Constructor<?>[] constructors = new Constructor<?>[]{ TennoBirthDayBundle.class.getDeclaredConstructor(Holiday.class, int.class) };
					HolidayBundle h = (HolidayBundle)constructors[0].newInstance(null, year);
					if (dt.getDayOfMonth()==h.getDay()){ return true;}
					return dt.getDayOfMonth()==h.getChangeDay();
				}catch(Exception e){
				}
			}
		}
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
		// ver 3.3
		if (dt.getYear()==2019){
			if (dt.equals(LocalDate.of(2019, 4, 30))) return true;
			if (dt.equals(LocalDate.of(2019, 5, 2))) return true;
			if (dt.equals(LocalDate.of(2019, 10, 22))) return true;
		}

		return false;
	}

	public static String[] WEEKDAYS_JA = {"月","火","水","木","金","土","日" };
	/** 曜日String算出 Japanese */
	public static String dateOfWeekJA(LocalDate dt){
		return WEEKDAYS_JA[dt.getDayOfWeek().getValue() - 1];
	}
	public static String[] WEEKDAYS_SIMPLE = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun" };

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
		if (year==2019){
			// ver 3.5
			return new LocalDate[]{ LocalDate.of(2019, 4, 30), LocalDate.of(2019, 5, 2) };
		}
		// 2019年以外は、敬老の日と秋分の日が１日で挟まれた場合のみ。
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
	class NatinalFoundationBundle extends HolidayBundle{
		public NatinalFoundationBundle(int year){
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
	// さらに、1979年以前を無視！～2150年まで有効
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
			if (super.year==2020) return 23;
			if (super.year==2021) return 22;
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
			if (super.year==2021) return 8;  /* ver 3.11 */
			return super.year==2020 ? 10 : 11;
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
	// さらに、1979年以前を無視！～2150年まで有効
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
			//return 12;
			// ver 3.1
			return super.year < 2019 ? 12 : 2;
		}
		@Override
		public String getDescription(){
			return "天皇誕生日";
		}
	}
	/**
	 * 平成の次の即位日
	 * 注意： 2019年のみ
	 * ver 3.3
	 */
	class HeiseiNextDayBundle extends HolidayBundle{
		public HeiseiNextDayBundle(int year){
			super(2019);
		}
		@Override
		public int getDay(){
			return 1;
		}
		@Override
		public int getMonth(){
			return 5;
		}
		@Override
		public String getDescription(){
			return "平成の次の即位日";
		}
	}
	/**
	 * 即位礼正殿の儀
	 * 注意： 2019年のみ
	 * ver 3.3
	 */
	class SokuiReiSeidenDayBundle extends HolidayBundle{
		public SokuiReiSeidenDayBundle(int year){
			super(2019);
		}
		@Override
		public int getDay(){
			return 22;
		}
		@Override
		public int getMonth(){
			return 10;
		}
		@Override
		public String getDescription(){
			return "即位礼正殿の儀";
		}
	}
	/**
	 * 平成の次の即位日の前日の休日
	 * 注意： 2019年のみ
	 * ver 3.5
	 */
	class HeisetNextPrevBundle extends HolidayBundle{
		public HeisetNextPrevBundle(int year){
			super(2019);
		}
		@Override
		public int getDay(){
			return 30;
		}
		@Override
		public int getMonth(){
			return 4;
		}
		@Override
		public String getDescription(){
			return "国民の休日";
		}
	}
	/**
	 * 平成の次の即位日の翌日の休日
	 * 注意： 2019年のみ
	 * ver 3.5
	 */
	class HeisetNextNextBundle extends HolidayBundle{
		public HeisetNextNextBundle(int year){
			super(2019);
		}
		@Override
		public int getDay(){
			return 2;
		}
		@Override
		public int getMonth(){
			return 5;
		}
		@Override
		public String getDescription(){
			return "国民の休日";
		}
	}
}
